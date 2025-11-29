package com.lanrhyme.shardlauncher.data

import android.content.ContentUris
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.provider.MediaStore
import android.util.Log
import com.lanrhyme.shardlauncher.model.MusicItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream

class MusicRepository(private val context: Context) {

    suspend fun getMusicFiles(directoryPath: String? = null): List<MusicItem> = withContext(Dispatchers.IO) {
        val musicList = mutableListOf<MusicItem>()

        val collection =
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
                MediaStore.Audio.Media.getContentUri(
                    MediaStore.VOLUME_EXTERNAL
                )
            } else {
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
            }

        val projection = arrayOf(
            MediaStore.Audio.Media._ID,
            MediaStore.Audio.Media.TITLE,
            MediaStore.Audio.Media.ARTIST,
            MediaStore.Audio.Media.DATA // Path to the file
        )

        val selection = if (directoryPath != null && directoryPath != "内置目录") {
            "${MediaStore.Audio.Media.DATA} LIKE ?"
        } else {
            null
        }
        val selectionArgs = if (directoryPath != null && directoryPath != "内置目录") {
            arrayOf("$directoryPath/%")
        } else {
            null
        }

        val sortOrder = "${MediaStore.Audio.Media.TITLE} ASC"

        try {
            context.contentResolver.query(
                collection,
                projection,
                selection,
                selectionArgs,
                sortOrder
            )?.use { cursor ->
                val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media._ID)
                val titleColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE)
                val artistColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST)

                val retriever = MediaMetadataRetriever()

                while (cursor.moveToNext()) {
                    val id = cursor.getLong(idColumn)
                    val contentUri: Uri = ContentUris.withAppendedId(
                        MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                        id
                    )

                    try {
                        retriever.setDataSource(context, contentUri)

                        val title = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE)
                            ?: cursor.getString(titleColumn) // Fallback to MediaStore
                            ?: "Unknown Title"
                        val artist = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST)
                            ?: cursor.getString(artistColumn) // Fallback
                            ?: "Unknown Artist"

                        var albumArtUri = ""
                        val albumArtData = retriever.embeddedPicture
                        if (albumArtData != null) {
                            val bitmap = BitmapFactory.decodeByteArray(albumArtData, 0, albumArtData.size)
                            val cacheDir = context.cacheDir
                            val tempFile = File(cacheDir, "album_art_${id}.png")
                            FileOutputStream(tempFile).use { out ->
                                bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
                            }
                            albumArtUri = tempFile.toURI().toString()
                        }

                        musicList.add(MusicItem(title, artist, albumArtUri, contentUri.toString()))

                    } catch (e: Exception) {
                        Log.e("MusicRepository", "Error processing file: $contentUri", e)
                    }
                }
                retriever.release()
            }
        } catch (e: Exception) {
            Log.e("MusicRepository", "Error querying MediaStore", e)
        }
        musicList
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

    suspend fun getMusicItemFromUri(uri: Uri): MusicItem? = withContext(Dispatchers.IO) {
        val retriever = MediaMetadataRetriever()
        try {
            retriever.setDataSource(context, uri)

            val title = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE) ?: "Unknown Title"
            val artist = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST) ?: "Unknown Artist"

            var albumArtUri = ""
            val albumArtData = retriever.embeddedPicture
            if (albumArtData != null) {
                val bitmap = BitmapFactory.decodeByteArray(albumArtData, 0, albumArtData.size)
                val cacheDir = context.cacheDir
                val tempFile = File(cacheDir, "album_art_uri_${uri.toString().hashCode()}.png")
                FileOutputStream(tempFile).use { out ->
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
                }
                albumArtUri = tempFile.toURI().toString()
            }

            return@withContext MusicItem(title, artist, albumArtUri, uri.toString())

        } catch (e: Exception) {
            Log.e("MusicRepository", "Error processing URI: $uri", e)
            return@withContext null
        } finally {
            retriever.release()
        }
    }
}
