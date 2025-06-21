package de.skyslycer.hmcwraps.serialization.debug;

public class DebugInformation implements Debuggable {

    private final String version;
    private final String latestVersion;
    private final String protocolLibVersion;
    private final String serverVersion;
    private final String serverSoftware;
    private final String iaVersion;
    private final String oraxenVersion;
    private final String mythicMobsVersion;
    private final String crucibleVersion;

    public DebugInformation(String version, String latestVersion, String protocolLibVersion, String serverVersion,
                            String serverSoftware, String iaVersion, String oraxenVersion,
                            String mythicMobsVersion, String crucibleVersion) {
        this.version = version;
        this.latestVersion = latestVersion;
        this.protocolLibVersion = protocolLibVersion;
        this.serverVersion = serverVersion;
        this.serverSoftware = serverSoftware;
        this.iaVersion = iaVersion;
        this.oraxenVersion = oraxenVersion;
        this.mythicMobsVersion = mythicMobsVersion;
        this.crucibleVersion = crucibleVersion;
    }
}
