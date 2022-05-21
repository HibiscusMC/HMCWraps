package de.skyslycer.hmcwraps.serialization;

import org.spongepowered.configurate.objectmapping.ConfigSerializable;

@ConfigSerializable
public class PermissionSettings {

    private boolean checkPermissionPhysical;
    private boolean checkPermissionVirtual;
    private boolean permissionPhysical;
    private boolean permissionVirtual;

    public boolean isCheckPermissionPhysical() {
        return checkPermissionPhysical;
    }

    public boolean isCheckPermissionVirtual() {
        return checkPermissionVirtual;
    }

    public boolean isPermissionPhysical() {
        return permissionPhysical;
    }

    public boolean isPermissionVirtual() {
        return permissionVirtual;
    }

}
