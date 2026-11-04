package de.maxhenkel.gravestone.items;

import de.maxhenkel.gravestone.DeathInfo;
import de.maxhenkel.gravestone.Main;
import de.maxhenkel.gravestone.corelib.death.Death;
import de.maxhenkel.gravestone.corelib.death.DeathManager;
import de.maxhenkel.gravestone.net.MessageOpenObituary;
import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentUtils;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.ClickEvent.Action;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.network.PacketDistributor;

public class ObituaryItem extends Item {
   public ObituaryItem() {
      super(new Properties().stacksTo(1));
   }

   public InteractionResultHolder<ItemStack> use(Level world, Player p, InteractionHand hand) {
      if (p instanceof ServerPlayer player) {
         ItemStack itemInHand = player.getItemInHand(hand);
         convert(itemInHand);
         Death death = this.fromStack(player, itemInHand);
         if (death == null) {
            player.displayClientMessage(Component.translatable("message.gravestone.death_not_found"), true);
         } else if (player.isShiftKeyDown()) {
            if (player.hasPermissions(player.server.getOperatorUserPermissionLevel())) {
               Component replace = ComponentUtils.wrapInSquareBrackets(Component.translatable("message.gravestone.restore.replace"))
                  .withStyle(
                     style -> style.applyFormat(ChatFormatting.GREEN)
                        .withClickEvent(new ClickEvent(Action.SUGGEST_COMMAND, "/restore @s " + death.getId().toString() + " replace"))
                        .withHoverEvent(
                           new HoverEvent(
                              net.minecraft.network.chat.HoverEvent.Action.SHOW_TEXT, Component.translatable("message.gravestone.restore.replace.description")
                           )
                        )
                  );
               Component add = ComponentUtils.wrapInSquareBrackets(Component.translatable("message.gravestone.restore.add"))
                  .withStyle(
                     style -> style.applyFormat(ChatFormatting.GREEN)
                        .withClickEvent(new ClickEvent(Action.SUGGEST_COMMAND, "/restore @s " + death.getId().toString() + " add"))
                        .withHoverEvent(
                           new HoverEvent(
                              net.minecraft.network.chat.HoverEvent.Action.SHOW_TEXT, Component.translatable("message.gravestone.restore.add.description")
                           )
                        )
                  );
               player.sendSystemMessage(Component.translatable("message.gravestone.restore").append(" ").append(replace).append(" ").append(add));
            }
         } else {
            PacketDistributor.sendToPlayer(player, new MessageOpenObituary(death), new CustomPacketPayload[0]);
         }

         return InteractionResultHolder.success(itemInHand);
      } else {
         return InteractionResultHolder.success(p.getItemInHand(hand));
      }
   }

   @Nullable
   public Death fromStack(ServerPlayer player, ItemStack stack) {
      DeathInfo deathInfo = (DeathInfo)stack.get(Main.DEATH_DATA_COMPONENT);
      return deathInfo == null ? null : DeathManager.getDeath(player.serverLevel(), deathInfo.getPlayerId(), deathInfo.getDeathId());
   }

   public ItemStack toStack(Death death) {
      ItemStack stack = new ItemStack(this);
      stack.set(Main.DEATH_DATA_COMPONENT, new DeathInfo(death.getPlayerUUID(), death.getId()));
      return stack;
   }

   public static void convert(ItemStack stack) {
      if (stack.getItem() instanceof ObituaryItem) {
         if (!stack.has(Main.DEATH_DATA_COMPONENT)) {
            CustomData customData = (CustomData)stack.get(DataComponents.CUSTOM_DATA);
            if (customData != null) {
               CompoundTag compoundTag = customData.copyTag();
               if (compoundTag.contains("Death", 10)) {
                  CompoundTag death = compoundTag.getCompound("Death");
                  DeathInfo info = new DeathInfo(death.getUUID("PlayerUUID"), death.getUUID("DeathID"));
                  compoundTag.remove("Death");
                  if (compoundTag.isEmpty()) {
                     stack.remove(DataComponents.CUSTOM_DATA);
                  } else {
                     stack.set(DataComponents.CUSTOM_DATA, CustomData.of(compoundTag));
                  }

                  stack.set(Main.DEATH_DATA_COMPONENT, info);
               }
            }
         }
      }
   }
}
