package net.satisfy.candlelight.core.item;

import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.item.Item.TooltipContext;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.satisfy.candlelight.core.registry.ObjectRegistry;
import org.jetbrains.annotations.NotNull;

public class ClosedLetterItem extends Item {
   public ClosedLetterItem(Properties settings) {
      super(settings);
   }

   public void appendHoverText(ItemStack stack, TooltipContext tooltipContext, List<Component> tooltip, TooltipFlag tooltipFlag) {
      CompoundTag nbt = ((CustomData)stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY)).copyTag();
      String title = nbt.getString("letter_title");
      String sender = nbt.getString("letter_sender");
      if (!title.isBlank() && !sender.isBlank()) {
         tooltip.add(Component.translatable("tooltip.candlelight.letter.line", new Object[]{title, sender}).withStyle(ChatFormatting.GRAY));
      }

      tooltip.add(Component.translatable("item.candlelight.letter.tooltip").withStyle(new ChatFormatting[]{ChatFormatting.ITALIC, ChatFormatting.GRAY}));
   }

   @NotNull
   public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
      ItemStack in = player.getItemInHand(hand);
      if (!level.isClientSide()) {
         CompoundTag sealed = ((CustomData)in.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY)).copyTag();
         CompoundTag payload = sealed.contains("sealed_payload") ? sealed.getCompound("sealed_payload") : new CompoundTag();
         ItemStack out = new ItemStack((ItemLike)ObjectRegistry.NOTE_PAPER_WRITTEN.get());
         out.set(DataComponents.CUSTOM_DATA, CustomData.of(payload.copy()));
         if (this == ObjectRegistry.LOVE_LETTER_CLOSED.get()) {
            ((ServerLevel)level).sendParticles(ParticleTypes.HEART, player.getX(), player.getY() + 1.0, player.getZ(), 20, 0.3, 0.2, 0.3, 0.05);
         }

         player.setItemInHand(hand, out);
         player.awardStat(Stats.ITEM_USED.get(this));
      }

      return InteractionResultHolder.sidedSuccess(in, level.isClientSide());
   }
}
