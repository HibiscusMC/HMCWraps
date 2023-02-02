package de.skyslycer.hmcwraps.serialization.debug;

public class DebugInformation implements Debuggable {

    private final String version;
    private final String latestVersion;
    private final String protocolLibVersion;
    private final String serverVersion;

    public DebugInformation(String version, String latestVersion, String protocolLibVersion, String serverVersion) {
        this.version = version;
        this.latestVersion = latestVersion;
        this.protocolLibVersion = protocolLibVersion;
        this.serverVersion = serverVersion;
    }

}
