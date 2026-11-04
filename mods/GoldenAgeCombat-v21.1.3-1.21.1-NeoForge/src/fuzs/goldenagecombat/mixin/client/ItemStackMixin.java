package fuzs.goldenagecombat.mixin.client;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import fuzs.goldenagecombat.GoldenAgeCombat;
import fuzs.goldenagecombat.config.ClientConfig;
import fuzs.goldenagecombat.config.ServerConfig;
import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponentHolder;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextColor;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(ItemStack.class)
abstract class ItemStackMixin implements DataComponentHolder {
   @WrapMethod(method = "addAttributeTooltips")
   private void addAttributeTooltips(
      Consumer<Component> tooltipAdder,
      @Nullable Player player,
      Operation<Void> operation,
      @Share("equipmentSlotGroups") LocalRef<Set<EquipmentSlotGroup>> equipmentSlotGroupsRef
   ) {
      if (((ClientConfig)GoldenAgeCombat.CONFIG.get(ClientConfig.class)).attributesStyle == ClientConfig.AttributesStyle.LEGACY) {
         equipmentSlotGroupsRef.set(EnumSet.noneOf(EquipmentSlotGroup.class));
         List<Component> tooltipLines = new ArrayList<>();
         operation.call(new Object[]{tooltipLines::add, player});
         if (this.goldenagecombat$allMatchSameEquipmentSlot((Collection<EquipmentSlotGroup>)equipmentSlotGroupsRef.get())) {
            tooltipLines.removeIf(component -> {
               if (component == CommonComponents.EMPTY) {
                  return true;
               }

               TextColor color = component.getStyle().getColor();
               return color != null && color.serialize().equals(ChatFormatting.GRAY.getName());
            });
            if (!tooltipLines.isEmpty()) {
               tooltipLines.addFirst(CommonComponents.EMPTY);
            }
         }

         tooltipLines.forEach(tooltipAdder);
      } else if (((ClientConfig)GoldenAgeCombat.CONFIG.get(ClientConfig.class)).attributesStyle == ClientConfig.AttributesStyle.MODERN) {
         operation.call(new Object[]{tooltipAdder, player});
      }
   }

   @ModifyArg(
      method = "addAttributeTooltips",
      at = @At(
         value = "INVOKE",
         target = "Lnet/minecraft/world/item/ItemStack;forEachModifier(Lnet/minecraft/world/entity/EquipmentSlotGroup;Ljava/util/function/BiConsumer;)V"
      )
   )
   private BiConsumer<Holder<Attribute>, AttributeModifier> addAttributeTooltips(
      EquipmentSlotGroup equipmentSlotGroup,
      BiConsumer<Holder<Attribute>, AttributeModifier> action,
      @Share("equipmentSlotGroups") LocalRef<Set<EquipmentSlotGroup>> equipmentSlotGroupsRef
   ) {
      return (holder, attributeModifier) -> {
         if (!((ServerConfig)GoldenAgeCombat.CONFIG.get(ServerConfig.class)).removeAttackCooldown || !holder.is(Attributes.ATTACK_SPEED)) {
            action.accept(holder, attributeModifier);
            Set<EquipmentSlotGroup> equipmentSlotGroups = (Set<EquipmentSlotGroup>)equipmentSlotGroupsRef.get();
            if (equipmentSlotGroups != null) {
               equipmentSlotGroups.add(equipmentSlotGroup);
            }
         }
      };
   }

   @Unique
   private boolean goldenagecombat$allMatchSameEquipmentSlot(Collection<EquipmentSlotGroup> equipmentSlotGroups) {
      if (!equipmentSlotGroups.isEmpty()) {
         label25:
         for (EquipmentSlot equipmentSlot : EquipmentSlot.values()) {
            for (EquipmentSlotGroup equipmentSlotGroup : equipmentSlotGroups) {
               if (!equipmentSlotGroup.test(equipmentSlot)) {
                  continue label25;
               }
            }

            return true;
         }
      }

      return false;
   }

   @ModifyVariable(method = "addModifierTooltip", at = @At("LOAD"), ordinal = 0)
   private boolean addModifierTooltip(boolean isBaseAttributeModifierId) {
      return isBaseAttributeModifierId && ((ClientConfig)GoldenAgeCombat.CONFIG.get(ClientConfig.class)).attributesStyle != ClientConfig.AttributesStyle.LEGACY;
   }
}
