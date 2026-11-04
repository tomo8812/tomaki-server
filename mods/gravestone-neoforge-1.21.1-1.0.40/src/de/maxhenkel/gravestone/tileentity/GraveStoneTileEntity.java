package de.maxhenkel.gravestone.tileentity;

import de.maxhenkel.gravestone.GraveUtils;
import de.maxhenkel.gravestone.Main;
import de.maxhenkel.gravestone.blocks.GraveStoneBlock;
import de.maxhenkel.gravestone.corelib.death.Death;
import java.util.Optional;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Component.Serializer;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.Clearable;
import net.minecraft.world.Nameable;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public class GraveStoneTileEntity extends BlockEntity implements Nameable, Clearable {
   protected Death death = new Death.Builder(GraveUtils.EMPTY_UUID, GraveUtils.EMPTY_UUID).build();
   protected Component customName;

   public GraveStoneTileEntity(BlockPos pos, BlockState state) {
      super((BlockEntityType)Main.GRAVESTONE_TILEENTITY.get(), pos, state);
   }

   protected void saveAdditional(CompoundTag compound, Provider provider) {
      super.saveAdditional(compound, provider);
      compound.put("Death", this.death.toNBT(provider));
      if (this.customName != null) {
         compound.putString("CustomName", Serializer.toJson(this.customName, provider));
      }
   }

   protected void loadAdditional(CompoundTag compound, Provider provider) {
      super.loadAdditional(compound, provider);
      if (compound.contains("Death")) {
         this.death = Death.fromNBT(provider, compound.getCompound("Death"));
      } else {
         UUID playerUUID = GraveUtils.EMPTY_UUID;

         try {
            playerUUID = UUID.fromString(compound.getString("PlayerUUID"));
         } catch (Exception var9) {
         }

         Death.Builder builder = new Death.Builder(playerUUID, UUID.randomUUID());
         NonNullList<ItemStack> items = NonNullList.create();
         ListTag list = compound.getList("ItemStacks", 10);

         for (int i = 0; i < list.size(); i++) {
            Optional<ItemStack> parse = ItemStack.parse(provider, list.getCompound(i));
            parse.ifPresent(items::add);
         }

         builder.additionalItems(items);
         builder.playerName(compound.getString("PlayerName"));
         builder.timestamp(compound.getLong("DeathTime"));
         this.death = builder.build();
      }

      if (compound.contains("CustomName")) {
         this.customName = Serializer.fromJson(compound.getString("CustomName"), provider);
      }
   }

   public Packet<ClientGamePacketListener> getUpdatePacket() {
      return ClientboundBlockEntityDataPacket.create(this);
   }

   public CompoundTag getUpdateTag(Provider provider) {
      CompoundTag compound = new CompoundTag();
      this.saveAdditional(compound, provider);
      return compound;
   }

   public Death getDeath() {
      return this.death;
   }

   public void setDeath(Death death) {
      this.death = death;
      this.setChanged();
   }

   public void setCustomName(Component name) {
      this.customName = name;
      this.setChanged();
   }

   public Component getName() {
      return this.customName != null ? this.customName : this.getDefaultName();
   }

   public Component getDisplayName() {
      return this.getName();
   }

   @Nullable
   public Component getCustomName() {
      return this.customName;
   }

   protected Component getDefaultName() {
      String name = this.death.getPlayerName();
      return name != null && !name.isEmpty()
         ? Component.translatable("message.gravestone.grave_of", new Object[]{name})
         : Component.translatable(((GraveStoneBlock)Main.GRAVESTONE.get()).getDescriptionId());
   }

   @Nullable
   public Component getGraveName() {
      if (!this.death.getPlayerName().isEmpty()) {
         return Component.literal(this.death.getPlayerName());
      } else {
         return this.customName != null ? this.customName : null;
      }
   }

   public void clearContent() {
      this.death.getMainInventory().clear();
      this.death.getArmorInventory().clear();
      this.death.getOffHandInventory().clear();
      this.death.getAdditionalItems().clear();
      this.death.getEquipment().clear();
   }
}
