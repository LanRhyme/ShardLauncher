package com.lanrhyme.shardlauncher.game.versioninfo.models;

import com.google.gson.annotations.SerializedName;

import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;

public class GameManifest {
    private Arguments arguments;
    private AssetIndex assetIndex;
    private String assets;
    private int complianceLevel;
    private Downloads downloads;
    private String id;
    private JavaVersion javaVersion;
    private List<Library> libraries;
    private String mainClass;
    private String minecraftArguments;
    private int minimumLauncherVersion;
    private String releaseTime;
    private String time;
    private String type;
    private Logging logging;
    private String inheritsFrom; //作为非合并版本的标记

    public Arguments getArguments() {
        return arguments;
    }

    public void setArguments(Arguments arguments) {
        this.arguments = arguments;
    }

    public AssetIndex getAssetIndex() {
        return assetIndex;
    }

    public void setAssetIndex(AssetIndex assetIndex) {
        this.assetIndex = assetIndex;
    }

    public String getAssets() {
        return assets;
    }

    public void setAssets(String assets) {
        this.assets = assets;
    }

    public int getComplianceLevel() {
        return complianceLevel;
    }

    public void setComplianceLevel(int complianceLevel) {
        this.complianceLevel = complianceLevel;
    }

    public Downloads getDownloads() {
        return downloads;
    }

    public void setDownloads(Downloads downloads) {
        this.downloads = downloads;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public JavaVersion getJavaVersion() {
        return javaVersion;
    }

    public void setJavaVersion(JavaVersion javaVersion) {
        this.javaVersion = javaVersion;
    }

    public List<Library> getLibraries() {
        return libraries;
    }

    public void setLibraries(List<Library> libraries) {
        this.libraries = libraries;
    }

    public String getMainClass() {
        return mainClass;
    }

    public void setMainClass(String mainClass) {
        this.mainClass = mainClass;
    }

    public String getMinecraftArguments() {
        return minecraftArguments;
    }

    public void setMinecraftArguments(String minecraftArguments) {
        this.minecraftArguments = minecraftArguments;
    }

    public int getMinimumLauncherVersion() {
        return minimumLauncherVersion;
    }

    public void setMinimumLauncherVersion(int minimumLauncherVersion) {
        this.minimumLauncherVersion = minimumLauncherVersion;
    }

    public String getReleaseTime() {
        return releaseTime;
    }

    public void setReleaseTime(String releaseTime) {
        this.releaseTime = releaseTime;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Logging getLogging() {
        return logging;
    }

    public void setLogging(Logging logging) {
        this.logging = logging;
    }

    public String getInheritsFrom() {
        return inheritsFrom;
    }

    public void setInheritsFrom(String inheritsFrom) {
        this.inheritsFrom = inheritsFrom;
    }

    public static class Arguments {
        private List<Object> game;
        private List<Object> jvm;

        public List<Object> getGame() {
            return game;
        }

        public void setGame(List<Object> game) {
            this.game = game;
        }

        public List<Object> getJvm() {
            return jvm;
        }

        public void setJvm(List<Object> jvm) {
            this.jvm = jvm;
        }
    }

    public static class AssetIndex {
        private String id;
        private String sha1;
        private long size;
        private long totalSize;
        private String url;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getSha1() {
            return sha1;
        }

        public void setSha1(String sha1) {
            this.sha1 = sha1;
        }

        public long getSize() {
            return size;
        }

        public void setSize(long size) {
            this.size = size;
        }

        public long getTotalSize() {
            return totalSize;
        }

        public void setTotalSize(long totalSize) {
            this.totalSize = totalSize;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }
    }

    public static class Downloads {
        private Client client;
        @SerializedName("client_mappings")
        private ClientMappings clientMappings;
        private Server server;
        @SerializedName("server_mappings")
        private ServerMappings serverMappings;

        public Client getClient() {
            return client;
        }

        public void setClient(Client client) {
            this.client = client;
        }

        public ClientMappings getClientMappings() {
            return clientMappings;
        }

        public void setClientMappings(ClientMappings clientMappings) {
            this.clientMappings = clientMappings;
        }

        public Server getServer() {
            return server;
        }

        public void setServer(Server server) {
            this.server = server;
        }

        public ServerMappings getServerMappings() {
            return serverMappings;
        }

        public void setServerMappings(ServerMappings serverMappings) {
            this.serverMappings = serverMappings;
        }
    }

    public static class Client {
        private String sha1;
        private long size;
        private String url;

        public String getSha1() {
            return sha1;
        }

        public void setSha1(String sha1) {
            this.sha1 = sha1;
        }

        public long getSize() {
            return size;
        }

        public void setSize(long size) {
            this.size = size;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }
    }

    public static class ClientMappings {
        private String sha1;
        private long size;
        private String url;

        public String getSha1() {
            return sha1;
        }

        public void setSha1(String sha1) {
            this.sha1 = sha1;
        }

        public long getSize() {
            return size;
        }

        public void setSize(long size) {
            this.size = size;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }
    }

    public static class Server {
        private String sha1;
        private long size;
        private String url;

        public String getSha1() {
            return sha1;
        }

        public void setSha1(String sha1) {
            this.sha1 = sha1;
        }

        public long getSize() {
            return size;
        }

        public void setSize(long size) {
            this.size = size;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }
    }

    public static class ServerMappings {
        private String sha1;
        private long size;
        private String url;

        public String getSha1() {
            return sha1;
        }

        public void setSha1(String sha1) {
            this.sha1 = sha1;
        }

        public long getSize() {
            return size;
        }

        public void setSize(long size) {
            this.size = size;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }
    }

    public static class JavaVersion {
        private String component;
        private int majorVersion;
        private int version; // parameter used by LabyMod 4

        public String getComponent() {
            return component;
        }
        public void setComponent(String component) {
            this.component = component;
        }
        public int getMajorVersion() {
            return majorVersion;
        }
        public void setMajorVersion(int majorVersion) {
            this.majorVersion = majorVersion;
        }
        public int getVersion() {
            return version;
        }
        public void setVersion(int version) {
            this.version = version;
        }
    }

    public static class Library {
        private DownloadsX downloads;
        private String name;
        @Nullable private Map<OperatingSystem, String> natives;
        private List<Rule> rules;
        private String url;
        @Nullable private String sha1;
        private long size;

        public DownloadsX getDownloads() {
            return downloads;
        }

        public void setDownloads(DownloadsX downloads) {
            this.downloads = downloads;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public @Nullable Map<OperatingSystem, String> getNatives() {
            return natives;
        }

        public void setNatives(@Nullable Map<OperatingSystem, String> natives) {
            this.natives = natives;
        }

        public List<Rule> getRules() {
            return rules;
        }

        public void setRules(List<Rule> rules) {
            this.rules = rules;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public @Nullable String getSha1() {
            return sha1;
        }

        public void setSha1(@Nullable String sha1) {
            this.sha1 = sha1;
        }

        public long getSize() {
            return size;
        }

        public void setSize(long size) {
            this.size = size;
        }

        public boolean isNative() {
            return this.natives != null && Rule.checkRules(rules);
        }
    }

    public static class DownloadsX {
        private Artifact artifact;

        public Artifact getArtifact() {
            return artifact;
        }
        public void setArtifact(Artifact artifact) {
            this.artifact = artifact;
        }
    }

    public static class Artifact {
        private String path;
        private String sha1;
        private long size;
        private String url;

        public String getPath() {
            return path;
        }

        public void setPath(String path) {
            this.path = path;
        }

        public String getSha1() {
            return sha1;
        }

        public void setSha1(String sha1) {
            this.sha1 = sha1;
        }

        public long getSize() {
            return size;
        }

        public void setSize(long size) {
            this.size = size;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }
    }

    public static class Rule {
        private Action action;
        private Os os;
        private Features features;
        private List<Object> value;

        public Action getAction() {
            return action;
        }

        public void setAction(Action action) {
            this.action = action;
        }

        public Os getOs() {
            return os;
        }

        public void setOs(Os os) {
            this.os = os;
        }

        public Features getFeatures() {
            return features;
        }

        public void setFeatures(Features features) {
            this.features = features;
        }

        public List<Object> getValue() {
            return value;
        }

        public void setValue(List<Object> value) {
            this.value = value;
        }

        /**
         * [Modified from PojavLauncher](https://github.com/PojavLauncherTeam/PojavLauncher/blob/a6f3fc0/app_pojavlauncher/src/main/java/net/kdt/pojavlaunch/Tools.java#L815-L823)
         */
        public static boolean checkRules(@Nullable List<Rule> rules) {
            if (rules == null || rules.isEmpty()) return true; // always allow

            for (Rule rule : rules) {
                if (rule.action == Action.ALLOW && rule.os != null && rule.os.name.equals("osx")) {
                    return false; //disallow
                }
            }
            return true; // allow if none match
        }
    }

    public enum Action {
        @SerializedName("allow")
        ALLOW,
        @SerializedName("disallow")
        DISALLOW
    }

    public static class Os {
        private String name;
        private String arch;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getArch() {
            return arch;
        }

        public void setArch(String arch) {
            this.arch = arch;
        }
    }

    public static class Features {
        @SerializedName("is_demo_user")
        private Boolean isDemoUser;
        @SerializedName("has_custom_resolution")
        private Boolean hasCustomResolution;
        @SerializedName("has_quick_plays_support")
        private Boolean hasQuickPlaysSupport;
        @SerializedName("is_quick_play_singleplayer")
        private Boolean isQuickPlaySingleplayer;
        @SerializedName("is_quick_play_multiplayer")
        private Boolean isQuickPlayMultiplayer;
        @SerializedName("is_quick_play_realms")
        private Boolean isQuickPlayRealms;

        public Boolean getDemoUser() {
            return isDemoUser;
        }

        public void setDemoUser(Boolean demoUser) {
            isDemoUser = demoUser;
        }

        public Boolean getHasCustomResolution() {
            return hasCustomResolution;
        }

        public void setHasCustomResolution(Boolean hasCustomResolution) {
            this.hasCustomResolution = hasCustomResolution;
        }

        public Boolean getHasQuickPlaysSupport() {
            return hasQuickPlaysSupport;
        }

        public void setHasQuickPlaysSupport(Boolean hasQuickPlaysSupport) {
            this.hasQuickPlaysSupport = hasQuickPlaysSupport;
        }

        public Boolean getQuickPlaySingleplayer() {
            return isQuickPlaySingleplayer;
        }

        public void setQuickPlaySingleplayer(Boolean quickPlaySingleplayer) {
            isQuickPlaySingleplayer = quickPlaySingleplayer;
        }

        public Boolean getQuickPlayMultiplayer() {
            return isQuickPlayMultiplayer;
        }

        public void setQuickPlayMultiplayer(Boolean quickPlayMultiplayer) {
            isQuickPlayMultiplayer = quickPlayMultiplayer;
        }

        public Boolean getQuickPlayRealms() {
            return isQuickPlayRealms;
        }

        public void setQuickPlayRealms(Boolean quickPlayRealms) {
            isQuickPlayRealms = quickPlayRealms;
        }
    }

    public static class Logging {
        private LoggingClient client;

        public LoggingClient getClient() {
            return client;
        }

        public void setClient(LoggingClient client) {
            this.client = client;
        }
    }

    public static class LoggingClient {
        private String argument;
        private LoggingFile file;
        private String type;

        public String getArgument() {
            return argument;
        }

        public void setArgument(String argument) {
            this.argument = argument;
        }

        public LoggingFile getFile() {
            return file;
        }

        public void setFile(LoggingFile file) {
            this.file = file;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }
    }

    public static class LoggingFile {
        private String id;
        private String sha1;
        private long size;
        private String url;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getSha1() {
            return sha1;
        }

        public void setSha1(String sha1) {
            this.sha1 = sha1;
        }

        public long getSize() {
            return size;
        }

        public void setSize(long size) {
            this.size = size;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }
    }
}