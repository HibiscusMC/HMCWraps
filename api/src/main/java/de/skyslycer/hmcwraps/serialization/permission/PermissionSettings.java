package de.skyslycer.hmcwraps.serialization.permission;

import org.spongepowered.configurate.objectmapping.ConfigSerializable;

@ConfigSerializable
public class PermissionSettings {

    private boolean checkPermissionPhysical;
    private boolean checkPermissionVirtual;
    private boolean permissionPhysical;
    private boolean permissionVirtual;
    private boolean inventoryPermission;
    private boolean previewPermission;

    public PermissionSettings(boolean checkPermissionPhysical, boolean checkPermissionVirtual, boolean permissionPhysical, boolean permissionVirtual,
                              boolean inventoryPermission, boolean previewPermission) {
        this.checkPermissionPhysical = checkPermissionPhysical;
        this.checkPermissionVirtual = checkPermissionVirtual;
        this.permissionPhysical = permissionPhysical;
        this.permissionVirtual = permissionVirtual;
        this.inventoryPermission = inventoryPermission;
        this.previewPermission = previewPermission;
    }

    public PermissionSettings() {
    }

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

    public boolean isInventoryPermission() {
        return inventoryPermission;
    }

    public boolean isPreviewPermission() {
        return previewPermission;
    }

}
