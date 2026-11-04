package org.valkyrienskies.mod.mixinducks.world.entity;

import net.minecraft.class_243;
import org.valkyrienskies.mod.common.util.MinecraftPlayer;

public interface PlayerDuck {
   MinecraftPlayer vs_getPlayer();

   class_243 vs_getQueuedPositionUpdate();

   void vs_setQueuedPositionUpdate(class_243 var1);

   boolean vs_handledMovePacket();

   void vs_setHandledMovePacket(boolean var1);
}
