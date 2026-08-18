package de.skyslycer.hmcwraps.commands.parameter;

import de.skyslycer.hmcwraps.HMCWraps;
import de.skyslycer.hmcwraps.commands.exception.InvalidWrapException;
import de.skyslycer.hmcwraps.serialization.wrap.Wrap;
import org.jetbrains.annotations.NotNull;
import revxrsal.commands.autocomplete.SuggestionProvider;
import revxrsal.commands.bukkit.actor.BukkitCommandActor;
import revxrsal.commands.node.ExecutionContext;
import revxrsal.commands.parameter.ParameterType;
import revxrsal.commands.stream.MutableStringStream;

import java.util.List;

public class WrapParameterType implements ParameterType<BukkitCommandActor, Wrap> {

    private final HMCWraps plugin;

    public WrapParameterType(HMCWraps plugin) {
        this.plugin = plugin;
    }

    @Override
    public Wrap parse(@NotNull MutableStringStream mutableStringStream, @NotNull ExecutionContext<BukkitCommandActor> executionContext) {
        var uuid = mutableStringStream.readString();
        var wrap = plugin.getWrapsLoader().getWraps().get(uuid);
        if (wrap == null) throw new InvalidWrapException(uuid);
        return wrap;
    }

    @Override public @NotNull SuggestionProvider<BukkitCommandActor> defaultSuggestions() {
        return (context) -> List.copyOf(plugin.getWrapsLoader().getWraps().keySet());
    }

}
