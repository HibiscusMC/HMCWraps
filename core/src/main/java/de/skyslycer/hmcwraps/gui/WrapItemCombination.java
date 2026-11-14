package de.skyslycer.hmcwraps.gui;

import de.skyslycer.hmcwraps.serialization.wrap.Wrap;

/** A wrap with its sort keys, worked out once so sorting does not build items or check permissions again. */
public record WrapItemCombination(Wrap wrap, boolean permission, int modelId) { }
