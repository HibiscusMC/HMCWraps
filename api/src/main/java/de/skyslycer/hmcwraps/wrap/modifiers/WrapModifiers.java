package de.skyslycer.hmcwraps.wrap.modifiers;

import de.skyslycer.hmcwraps.HMCWraps;
import de.skyslycer.hmcwraps.wrap.modifiers.minecraft.*;
import de.skyslycer.hmcwraps.wrap.modifiers.plugin.*;

/**
 * This class contains instances of all available modifiers.
 */
public class WrapModifiers {

    private final ModelDataModifier modelData;
    private final ColorModifier color;
    private final ArmorImitationModifier armorImitation;
    private final EquippableModifier equippable;
    private final FlagsModifier flags;
    private final GlintOverrideModifier glintOverride;
    private final ItemModelModifier itemModel;
    private final LoreModifier lore;
    private final NameModifier name;
    private final NBTModifier nbt;
    private final TrimModifier trim;
    private final ItemsAdderModifier itemsAdder;
    private final MythicModifier mythic;
    private final NexoModifier nexo;
    private final OraxenModifier oraxen;
    private final ExecutableItemsModifier executableItems;
    private final TooltipStyleModifier tooltipStyle;

    public WrapModifiers(HMCWraps plugin) {
        this.modelData = new ModelDataModifier(plugin);
        this.color = new ColorModifier(plugin);
        this.armorImitation = new ArmorImitationModifier(plugin);
        this.equippable = new EquippableModifier(plugin);
        this.flags = new FlagsModifier(plugin);
        this.glintOverride = new GlintOverrideModifier(plugin);
        this.itemModel = new ItemModelModifier(plugin);
        this.lore = new LoreModifier(plugin);
        this.name = new NameModifier(plugin);
        this.nbt = new NBTModifier();
        this.trim = new TrimModifier(plugin);
        this.itemsAdder = new ItemsAdderModifier(plugin);
        this.mythic = new MythicModifier(plugin);
        this.nexo = new NexoModifier(plugin);
        this.oraxen = new OraxenModifier(plugin);
        this.executableItems = new ExecutableItemsModifier(plugin);
        this.tooltipStyle = new TooltipStyleModifier(plugin);
    }

    public ModelDataModifier modelData() {
        return modelData;
    }

    public ColorModifier color() {
        return color;
    }

    public ArmorImitationModifier armorImitation() {
        return armorImitation;
    }

    public EquippableModifier equippable() {
        return equippable;
    }

    public FlagsModifier flags() {
        return flags;
    }

    public GlintOverrideModifier glintOverride() {
        return glintOverride;
    }

    public ItemModelModifier itemModel() {
        return itemModel;
    }

    public LoreModifier lore() {
        return lore;
    }

    public NameModifier name() {
        return name;
    }

    public NBTModifier nbt() {
        return nbt;
    }

    public TrimModifier trim() {
        return trim;
    }

    public ItemsAdderModifier itemsAdder() {
        return itemsAdder;
    }

    public MythicModifier mythic() {
        return mythic;
    }

    public NexoModifier nexo() {
        return nexo;
    }

    public OraxenModifier oraxen() {
        return oraxen;
    }

    public ExecutableItemsModifier executableItems() {
        return executableItems;
    }

    public TooltipStyleModifier tooltipStyle() {
        return tooltipStyle;
    }

}

