package de.maxhenkel.gravestone.corelib.death;

import de.maxhenkel.gravestone.corelib.item.ItemUtils;
import de.maxhenkel.gravestone.corelib.player.PlayerUtils;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public class Death {
   private UUID id;
   private UUID playerUUID;
   private String playerName;
   private NonNullList<ItemStack> mainInventory = NonNullList.withSize(36, ItemStack.EMPTY);
   private NonNullList<ItemStack> armorInventory = NonNullList.withSize(4, ItemStack.EMPTY);
   private NonNullList<ItemStack> offHandInventory = NonNullList.withSize(1, ItemStack.EMPTY);
   private NonNullList<ItemStack> additionalItems = NonNullList.create();
   private NonNullList<ItemStack> equipment = NonNullList.withSize(EquipmentSlot.values().length, ItemStack.EMPTY);
   private long timestamp;
   private int experience;
   private double posX;
   private double posY;
   private double posZ;
   private String dimension;
   private byte model;

   private Death() {
   }

   public UUID getId() {
      return this.id;
   }

   public UUID getPlayerUUID() {
      return this.playerUUID;
   }

   public String getPlayerName() {
      return this.playerName;
   }

   public NonNullList<ItemStack> getMainInventory() {
      return this.mainInventory;
   }

   public NonNullList<ItemStack> getArmorInventory() {
      return this.armorInventory;
   }

   public NonNullList<ItemStack> getOffHandInventory() {
      return this.offHandInventory;
   }

   public NonNullList<ItemStack> getAdditionalItems() {
      return this.additionalItems;
   }

   public NonNullList<ItemStack> getEquipment() {
      return this.equipment;
   }

   public long getTimestamp() {
      return this.timestamp;
   }

   public int getExperience() {
      return this.experience;
   }

   public double getPosX() {
      return this.posX;
   }

   public double getPosY() {
      return this.posY;
   }

   public double getPosZ() {
      return this.posZ;
   }

   public BlockPos getBlockPos() {
      return BlockPos.containing(this.posX, this.posY, this.posZ);
   }

   public String getDimension() {
      return this.dimension;
   }

   public byte getModel() {
      return this.model;
   }

   @Override
   public String toString() {
      return "Death{name=" + this.playerName + "timestamp=" + this.timestamp + "}";
   }

   public static Death fromPlayer(Player player) {
      Death death = new Death();
      death.id = UUID.randomUUID();
      death.playerUUID = player.getUUID();
      death.playerName = player.getName().getString();

      for (int i = 0; i < death.mainInventory.size(); i++) {
         death.mainInventory.set(i, (ItemStack)player.getInventory().items.get(i));
      }

      for (int i = 0; i < death.armorInventory.size(); i++) {
         death.armorInventory.set(i, (ItemStack)player.getInventory().armor.get(i));
      }

      for (int i = 0; i < death.offHandInventory.size(); i++) {
         death.offHandInventory.set(i, (ItemStack)player.getInventory().offhand.get(i));
      }

      death.equipment = NonNullList.withSize(EquipmentSlot.values().length, ItemStack.EMPTY);

      for (int i = 0; i < death.equipment.size(); i++) {
         death.equipment.set(i, player.getItemBySlot(EquipmentSlot.values()[i]).copy());
      }

      death.timestamp = System.currentTimeMillis();
      death.experience = player.experienceLevel;
      death.posX = player.position().x;
      death.posY = player.position().y;
      death.posZ = player.position().z;
      death.dimension = player.level().dimension().location().toString();
      death.model = PlayerUtils.getModel(player);
      return death;
   }

   public void processDrops(Collection<ItemEntity> items) {
      List<ItemStack> drops = items.stream()
         .filter(Objects::nonNull)
         .<ItemStack>map(ItemEntity::getItem)
         .filter(itemStack -> !itemStack.isEmpty())
         .collect(Collectors.toList());
      this.processInventory(drops, this.mainInventory);
      this.processInventory(drops, this.armorInventory);
      this.processInventory(drops, this.offHandInventory);
      this.additionalItems.addAll(drops);
   }

   private void processInventory(List<ItemStack> drops, NonNullList<ItemStack> inventory) {
      for (int i = 0; i < inventory.size(); i++) {
         ItemStack itemStack = (ItemStack)inventory.get(i);
         if (!itemStack.isEmpty()) {
            if (drops.contains(itemStack)) {
               drops.remove(itemStack);
            } else {
               inventory.set(i, ItemStack.EMPTY);
            }
         }
      }
   }

   public NonNullList<ItemStack> getAllItems() {
      NonNullList<ItemStack> items = NonNullList.create();
      items.addAll(this.filterList(this.mainInventory));
      items.addAll(this.filterList(this.armorInventory));
      items.addAll(this.filterList(this.offHandInventory));
      items.addAll(this.filterList(this.additionalItems));
      return items;
   }

   private List<ItemStack> filterList(List<ItemStack> stacks) {
      return stacks.stream().filter(itemStack -> !itemStack.isEmpty()).collect(Collectors.toList());
   }

   public static Death fromNBT(Provider provider, CompoundTag compound) {
      Death death = new Death();
      if (compound.contains("IdMost") && compound.contains("IdLeast")) {
         death.id = new UUID(compound.getLong("IdMost"), compound.getLong("IdLeast"));
      } else if (compound.contains("Id")) {
         death.id = compound.getUUID("Id");
      } else {
         death.id = UUID.randomUUID();
      }

      if (compound.contains("PlayerUuidMost") && compound.contains("PlayerUuidLeast")) {
         death.playerUUID = new UUID(compound.getLong("PlayerUuidMost"), compound.getLong("PlayerUuidLeast"));
      } else if (compound.contains("PlayerUuid")) {
         death.playerUUID = compound.getUUID("PlayerUuid");
      } else {
         death.playerUUID = UUID.randomUUID();
      }

      death.playerName = compound.getString("PlayerName");
      ItemUtils.readInventory(provider, compound, "MainInventory", death.mainInventory);
      ItemUtils.readInventory(provider, compound, "ArmorInventory", death.armorInventory);
      ItemUtils.readInventory(provider, compound, "OffHandInventory", death.offHandInventory);
      death.additionalItems = ItemUtils.readItemList(provider, compound, "Items");
      ItemUtils.readItemList(provider, compound, "Equipment", death.equipment);
      death.timestamp = compound.getLong("Timestamp");
      death.experience = compound.getInt("Experience");
      death.posX = compound.getDouble("PosX");
      death.posY = compound.getDouble("PosY");
      death.posZ = compound.getDouble("PosZ");
      death.dimension = compound.getString("Dimension");
      death.model = compound.getByte("Model");
      return death;
   }

   public CompoundTag toNBT(Provider provider) {
      return this.toNBT(provider, true);
   }

   public CompoundTag toNBT(Provider provider, boolean withItems) {
      CompoundTag compound = new CompoundTag();
      compound.putUUID("Id", this.id);
      compound.putUUID("PlayerUuid", this.playerUUID);
      compound.putString("PlayerName", this.playerName);
      if (withItems) {
         ItemUtils.saveInventory(provider, compound, "MainInventory", this.mainInventory);
         ItemUtils.saveInventory(provider, compound, "ArmorInventory", this.armorInventory);
         ItemUtils.saveInventory(provider, compound, "OffHandInventory", this.offHandInventory);
         ItemUtils.saveItemList(provider, compound, "Items", this.additionalItems);
      }

      ItemUtils.saveItemList(provider, compound, "Equipment", this.equipment);
      compound.putLong("Timestamp", this.timestamp);
      compound.putInt("Experience", this.experience);
      compound.putDouble("PosX", this.posX);
      compound.putDouble("PosY", this.posY);
      compound.putDouble("PosZ", this.posZ);
      compound.putString("Dimension", this.dimension);
      compound.putByte("Model", this.model);
      return compound;
   }

   public static class Builder {
      private Death death = new Death();

      public Builder(UUID playerUUID, UUID id) {
         this.death.dimension = "";
         this.death.playerName = "";
         this.death.playerUUID = playerUUID;
         this.death.id = id;
      }

      public Death build() {
         return this.death;
      }

      public Death.Builder id(UUID uuid) {
         this.death.id = uuid;
         return this;
      }

      public Death.Builder playerUUID(UUID uuid) {
         this.death.playerUUID = uuid;
         return this;
      }

      public Death.Builder playerName(String name) {
         this.death.playerName = name;
         return this;
      }

      public Death.Builder mainInventory(NonNullList<ItemStack> list) {
         this.death.mainInventory = list;
         return this;
      }

      public Death.Builder armorInventory(NonNullList<ItemStack> list) {
         this.death.armorInventory = list;
         return this;
      }

      public Death.Builder offHandInventory(NonNullList<ItemStack> list) {
         this.death.offHandInventory = list;
         return this;
      }

      public Death.Builder additionalItems(NonNullList<ItemStack> list) {
         this.death.additionalItems = list;
         return this;
      }

      public Death.Builder equipment(NonNullList<ItemStack> list) {
         this.death.equipment = list;
         return this;
      }

      public Death.Builder timestamp(long timestamp) {
         this.death.timestamp = timestamp;
         return this;
      }

      public Death.Builder experience(int experience) {
         this.death.experience = experience;
         return this;
      }

      public Death.Builder posX(double posX) {
         this.death.posX = posX;
         return this;
      }

      public Death.Builder posY(double posY) {
         this.death.posY = posY;
         return this;
      }

      public Death.Builder posZ(double posZ) {
         this.death.posZ = posZ;
         return this;
      }

      public Death.Builder dimension(String dimension) {
         this.death.dimension = dimension;
         return this;
      }

      public Death.Builder model(byte model) {
         this.death.model = model;
         return this;
      }
   }
}
