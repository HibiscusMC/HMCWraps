package de.skyslycer.hmcwraps.configuration;

import org.bukkit.Bukkit;
import org.spongepowered.configurate.ConfigurateException;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.transformation.ConfigurationTransformation;
import org.spongepowered.configurate.transformation.TransformAction;

import static org.spongepowered.configurate.NodePath.path;

public class ConfigTransformations {

    private static final int LATEST_VERSION = 0;

    private ConfigTransformations() { }

    public static ConfigurationTransformation.Versioned create() {
        return ConfigurationTransformation.versionedBuilder()
                .addVersion(LATEST_VERSION, initialTransform())
                .build();
    }

    public static ConfigurationTransformation initialTransform() {
        return ConfigurationTransformation.builder()
                .addAction(path("items", ConfigurationTransformation.WILDCARD_OBJECT, "wraps", ConfigurationTransformation.WILDCARD_OBJECT, "nbt"), TransformAction.rename("wrap-nbt"))
                .addAction(path("permission-settings"), TransformAction.rename("permissions"))
                .build();
    }

    public static <N extends ConfigurationNode> N updateNode(final N node) throws ConfigurateException {
        if (!node.virtual()) {
            final ConfigurationTransformation.Versioned trans = create();
            final int startVersion = trans.version(node);
            trans.apply(node);
            final int endVersion = trans.version(node);
            if (startVersion != endVersion) {
                Bukkit.getLogger().info("Updated config schema from " + startVersion + " to " + endVersion);
            }
        }
        return node;
    }

}
