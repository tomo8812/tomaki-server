package dev.terminalmc.clientsort.client.config.legacy;

import dev.terminalmc.clientsort.client.config.Vec2i;
import org.jetbrains.annotations.Nullable;

public record ButtonLayout(
   String className, @Nullable Vec2i offset, @Nullable Boolean sortEnabled, @Nullable Boolean stackFillEnabled, @Nullable Boolean transferEnabled
) {
}
