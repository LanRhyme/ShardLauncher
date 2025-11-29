package com.lanrhyme.shardlauncher.data

import android.content.ContentUris
import android.content.Context
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.provider.MediaStore
import android.provider.OpenableColumns
import android.util.Log
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.lanrhyme.shardlauncher.model.MusicItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.io.FileReader
import java.io.FileWriter
import java.io.IOException

class MusicRepository(private val context: Context) {

    private val musicDir: File by lazy {
        val shardLauncherDir = File(context.getExternalFilesDir(null), ".shardlauncher/musics")
        if (!shardLauncherDir.exists()) {
            shardLauncherDir.mkdirs()
        }
        shardLauncherDir
    }

    private val musicFile: File by lazy {
        File(musicDir, "musics.json")
    }
    private val gson = Gson()

    suspend fun getMusicFiles(directoryPath: String? = null): List<MusicItem> = withContext(Dispatchers.IO) {
        val loadedMusic = loadMusicList()
        if (loadedMusic.isNotEmpty()) {
            return@withContext loadedMusic
        }
        // Fallback to media store is complex and not what user wants for persistence.
        // User wants to add files explicitly and have them copied.
        // This function should now probably just load from the JSON.
        // The old functionality can be part of a separate "import from MediaStore" feature.
        return@withContext emptyList()
    }

    suspend fun getMusicDirectories(): List<String> = withContext(Dispatchers.IO) {
        val directories = mutableSetOf<String>()

        val collection =
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
                MediaStore.Audio.Media.getContentUri(
                    MediaStore.VOLUME_EXTERNAL
                )
            } else {
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
            }

        val projection = arrayOf(
            MediaStore.Audio.Media.DATA
        )

        try {
            context.contentResolver.query(
                collection,
                projection,
                null,
                null,
                null
            )?.use { cursor ->
                val dataColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA)
                while (cursor.moveToNext()) {
                    val filePath = cursor.getString(dataColumn)
                    try {
                        File(filePath).parentFile?.absolutePath?.let {
                            directories.add(it)
                        }
                    } catch (e: Exception) {
                        Log.e("MusicRepository", "Error accessing file path: $filePath", e)
                    }
                }
            }
        } catch (e: Exception) {
            Log.e("MusicRepository", "Error querying directories from MediaStore", e)
        }
        directories.toList()
    }


    private fun getFileName(uri: Uri): String {
        var name: String? = null
        if (uri.scheme == "content") {
            val cursor: Cursor? = context.contentResolver.query(uri, null, null, null, null)
            cursor?.use {
                if (it.moveToFirst()) {
                    val nameIndex = it.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                    if (nameIndex != -1) {
                        name = it.getString(nameIndex)
                    }
                }
            }
        }
        if (name == null) {
            name = uri.path
            val cut = name?.lastIndexOf('/')
            if (cut != -1) {
                if (cut != null) {
                    name = name?.substring(cut + 1)
                }
            }
        }
        return name ?: "unknown_music_${System.currentTimeMillis()}"
    }

    private fun copyAudioToInternalStorage(uri: Uri): File? {
        return try {
            val inputStream = context.contentResolver.openInputStream(uri) ?: return null
            val fileName = getFileName(uri)
            val destinationFile = File(musicDir, fileName)

            inputStream.use { input ->
                FileOutputStream(destinationFile).use { output ->
                    input.copyTo(output)
                }
            }
            destinationFile
        } catch (e: IOException) {
            Log.e("MusicRepository", "Failed to copy audio file", e)
            null
        }
    }


    suspend fun getMusicItemFromUri(uri: Uri): MusicItem? = withContext(Dispatchers.IO) {
        val retriever = MediaMetadataRetriever()
        try {
            val copiedFile = copyAudioToInternalStorage(uri) ?: return@withContext null
            val copiedFileUri = Uri.fromFile(copiedFile)

            retriever.setDataSource(context, copiedFileUri)

            val title = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE) ?: copiedFile.nameWithoutExtension
            val artist = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST) ?: "Unknown Artist"

            var albumArtUri = ""
            val albumArtData = retriever.embeddedPicture
            if (albumArtData != null) {
                val bitmap = BitmapFactory.decodeByteArray(albumArtData, 0, albumArtData.size)
                // Note: Storing album art in cache is temporary. For true persistence, it should also be saved to a permanent location.
                val cacheDir = context.cacheDir
                val tempFile = File(cacheDir, "album_art_uri_${copiedFile.name.hashCode()}.png")
                FileOutputStream(tempFile).use { out ->
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
                }
                albumArtUri = tempFile.toURI().toString()
            }

            return@withContext MusicItem(title, artist, albumArtUri, copiedFileUri.toString())

        } catch (e: Exception) {
            Log.e("MusicRepository", "Error processing URI: $uri", e)
            return@withContext null
        } finally {
            retriever.release()
        }
    }

    suspend fun saveMusicList(musicList: List<MusicItem>) = withContext(Dispatchers.IO) {
        try {
            if (!musicFile.exists()) {
                musicFile.createNewFile()
            }
            FileWriter(musicFile).use { writer ->
                gson.toJson(musicList, writer)
            }
        } catch (e: Exception) {
            Log.e("MusicRepository", "Error saving music list", e)
        }
    }

    suspend fun deleteMusicFile(mediaUri: String) = withContext(Dispatchers.IO) {
        try {
            val file = File(Uri.parse(mediaUri).path ?: return@withContext)
            if (file.exists()) {
                file.delete()
            }
        } catch (e: Exception) {
            Log.e("MusicRepository", "Error deleting music file: $mediaUri", e)
        }
    }

    suspend fun loadMusicList(): List<MusicItem> = withContext(Dispatchers.IO) {
        if (!musicFile.exists()) {
            return@withContext emptyList()
        }
        return@withContext try {
            FileReader(musicFile).use { reader ->
                val musicListType = object : TypeToken<List<MusicItem>>() {}.type
                val musicList: List<MusicItem>? = gson.fromJson(reader, musicListType)
                // Validate that files still exist
                musicList?.filter {
                    val path = Uri.parse(it.mediaUri).path
                    if (path != null) {
                        File(path).exists()
                    } else {
                        false
                    }
                } ?: emptyList()
            }
        } catch (e: Exception) {
            Log.e("MusicRepository", "Error loading music list", e)
            emptyList()
        }
    }
}
