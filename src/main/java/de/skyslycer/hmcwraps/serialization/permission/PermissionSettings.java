package de.skyslycer.hmcwraps.serialization.permission;

import de.skyslycer.hmcwraps.serialization.IPermissionSettings;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;

@ConfigSerializable
public class PermissionSettings implements IPermissionSettings {

    private boolean checkPermissionPhysical;
    private boolean checkPermissionVirtual;
    private boolean permissionPhysical;
    private boolean permissionVirtual;
    private boolean inventoryPermission;

    public PermissionSettings(boolean checkPermissionPhysical, boolean checkPermissionVirtual, boolean permissionPhysical, boolean permissionVirtual,
                              boolean inventoryPermission) {
        this.checkPermissionPhysical = checkPermissionPhysical;
        this.checkPermissionVirtual = checkPermissionVirtual;
        this.permissionPhysical = permissionPhysical;
        this.permissionVirtual = permissionVirtual;
        this.inventoryPermission = inventoryPermission;
    }

    public PermissionSettings() {
    }

    @Override
    public boolean isCheckPermissionPhysical() {
        return checkPermissionPhysical;
    }

    @Override
    public boolean isCheckPermissionVirtual() {
        return checkPermissionVirtual;
    }

    @Override
    public boolean isPermissionPhysical() {
        return permissionPhysical;
    }

    @Override
    public boolean isPermissionVirtual() {
        return permissionVirtual;
    }

    @Override
    public boolean isInventoryPermission() {
        return inventoryPermission;
    }

}
