package net.craftingstore.core.models.api.request;

import java.io.Serializable;

public class PackageInformationRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    String inGameName;
    String uuid;
    String ip;
    int packageId;

    public PackageInformationRequest(String inGameName, String uuid, String ip, int packageId) {
        this.inGameName = inGameName;
        this.uuid = uuid;
        this.ip = ip;
        this.packageId = packageId;
    }
}
