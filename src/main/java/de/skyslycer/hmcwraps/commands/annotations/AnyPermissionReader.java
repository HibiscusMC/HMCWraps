package de.skyslycer.hmcwraps.commands.annotations;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import revxrsal.commands.bukkit.BukkitCommandActor;
import revxrsal.commands.command.trait.CommandAnnotationHolder;
import revxrsal.commands.process.PermissionReader;

public class AnyPermissionReader implements PermissionReader {

    @Override public @Nullable revxrsal.commands.command.CommandPermission getPermission(@NotNull CommandAnnotationHolder command) {
        var annotation = command.getAnnotation(AnyPermission.class);
        if (annotation == null) return null;
        var permissions = annotation.value();
        return actor -> {
            var sender = ((BukkitCommandActor) actor).getSender();
            for (var permission : permissions) {
                if (sender.hasPermission(permission)) return true;
            }
            return false;
        };
    }

}
