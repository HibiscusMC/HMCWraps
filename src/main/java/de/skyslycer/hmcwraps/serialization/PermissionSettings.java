package de.skyslycer.hmcwraps.serialization;

import org.spongepowered.configurate.objectmapping.ConfigSerializable;

@ConfigSerializable
public class PermissionSettings implements IPermissionSettings {

    private boolean checkPermissionPhysical;
    private boolean checkPermissionVirtual;
    private boolean permissionPhysical;
    private boolean permissionVirtual;

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

}
