package de.maxhenkel.gravestone.entity;

import com.mojang.authlib.GameProfile;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.RemotePlayer;
import net.minecraft.core.NonNullList;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.player.PlayerModelPart;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.common.NeoForgeMod;

public class DummyPlayer extends RemotePlayer {
   private final byte model;

   public DummyPlayer(ClientLevel world, GameProfile gameProfile, NonNullList<ItemStack> equipment, byte model) {
      super(world, gameProfile);
      this.model = model;

      for (EquipmentSlot type : EquipmentSlot.values()) {
         this.setItemSlot(type, (ItemStack)equipment.get(type.ordinal()));
      }

      AttributeInstance attribute = this.getAttributes().getInstance(NeoForgeMod.NAMETAG_DISTANCE);
      if (attribute != null) {
         attribute.setBaseValue(0.0);
      }
   }

   public boolean isSpectator() {
      return false;
   }

   public boolean isModelPartShown(PlayerModelPart part) {
      return (this.model & part.getMask()) == part.getMask();
   }
}
