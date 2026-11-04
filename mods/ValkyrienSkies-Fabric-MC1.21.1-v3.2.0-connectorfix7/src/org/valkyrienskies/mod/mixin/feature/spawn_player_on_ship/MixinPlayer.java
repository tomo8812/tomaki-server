package org.valkyrienskies.mod.mixin.feature.spawn_player_on_ship;

import com.mojang.authlib.GameProfile;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import it.unimi.dsi.fastutil.longs.LongSet;
import net.minecraft.class_1299;
import net.minecraft.class_1309;
import net.minecraft.class_1657;
import net.minecraft.class_1937;
import net.minecraft.class_2338;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.valkyrienskies.mod.common.VSGameUtilsKt;
import org.valkyrienskies.mod.common.ValkyrienSkiesMod;
import org.valkyrienskies.mod.common.networking.PacketChangeKnownShips;
import org.valkyrienskies.mod.mixinducks.feature.tickets.PlayerKnownShipsDuck;

@Mixin(class_1657.class)
public abstract class MixinPlayer extends class_1309 implements PlayerKnownShipsDuck {
   @Unique
   private LongSet vs_knownShips = new LongOpenHashSet();

   protected MixinPlayer(class_1299<? extends class_1309> entityType, class_1937 level) {
      super(entityType, level);
   }

   @Inject(method = "<init>", at = @At("TAIL"))
   private void populateLoadedShips(class_1937 level, class_2338 blockPos, float f, GameProfile gameProfile, CallbackInfo ci) {
      if (level != null && level.method_8608()) {
         VSGameUtilsKt.getShipObjectWorld(level).getLoadedShips().forEach(ship -> this.vs_knownShips.add(ship.getId()));
      }
   }

   @Override
   public void vs_addKnownShip(long shipId) {
      this.vs_knownShips.add(shipId);
      if (this.method_37908().field_9236) {
         PacketChangeKnownShips packet = new PacketChangeKnownShips(true, shipId);
         ValkyrienSkiesMod.getVsCore().getSimplePacketNetworking().sendToServer(packet);
      }
   }

   @Override
   public void vs_removeKnownShip(long shipId) {
      this.vs_knownShips.remove(shipId);
      if (this.method_37908().field_9236) {
         PacketChangeKnownShips packet = new PacketChangeKnownShips(false, shipId);
         ValkyrienSkiesMod.getVsCore().getSimplePacketNetworking().sendToServer(packet);
      }
   }

   @Override
   public boolean vs_isKnownShip(long shipId) {
      return this.vs_knownShips.contains(shipId);
   }

   @Override
   public LongSet vs_getKnownShips() {
      return this.vs_knownShips;
   }

   @Override
   public void vs_setKnownShips(LongSet ships) {
      this.vs_knownShips = ships;
   }
}
