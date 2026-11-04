package fuzs.goldenagecombat.handler;

import com.google.common.collect.ImmutableMap;
import fuzs.goldenagecombat.GoldenAgeCombat;
import fuzs.goldenagecombat.config.CommonConfig;
import fuzs.puzzleslib.api.config.v3.serialization.ConfigDataSet;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.HoeItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.PickaxeItem;
import net.minecraft.world.item.ShovelItem;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.TieredItem;
import net.minecraft.world.item.component.Tool;
import net.minecraft.world.item.component.ItemAttributeModifiers.Entry;

public class AttackAttributeHandler {
   private static final Map<Class<? extends TieredItem>, Double> ATTACK_DAMAGE_BONUS_OVERRIDES = ImmutableMap.of(
      SwordItem.class, 4.0, AxeItem.class, 3.0, PickaxeItem.class, 2.0, ShovelItem.class, 1.0, HoeItem.class, 0.0
   );

   public static void onFinalizeItemComponents(Item item, Consumer<Function<DataComponentMap, DataComponentPatch>> consumer) {
      if (GoldenAgeCombat.CONFIG.getHolder(CommonConfig.class).isAvailable()) {
         if (((CommonConfig)GoldenAgeCombat.CONFIG.get(CommonConfig.class)).noItemDurabilityPenalty) {
            if (item instanceof SwordItem) {
               consumer.accept(dataComponents -> {
                  if (dataComponents.has(DataComponents.TOOL)) {
                     Tool tool = (Tool)dataComponents.get(DataComponents.TOOL);
                     if (tool.damagePerBlock() == 2) {
                        Tool newTool = new Tool(tool.rules(), tool.defaultMiningSpeed(), 1);
                        return DataComponentPatch.builder().set(DataComponents.TOOL, newTool).build();
                     }
                  }

                  return DataComponentPatch.EMPTY;
               });
            }
         }
      }
   }

   public static void onComputeItemAttributeModifiers(Item item, List<Entry> itemAttributeModifiers) {
      if (GoldenAgeCombat.CONFIG.getHolder(CommonConfig.class).isAvailable()) {
         if (!setAttributeValue(
               item,
               itemAttributeModifiers,
               Attributes.ATTACK_DAMAGE,
               Item.BASE_ATTACK_DAMAGE_ID,
               ((CommonConfig)GoldenAgeCombat.CONFIG.get(CommonConfig.class)).attackDamageOverrides
            )
            && ((CommonConfig)GoldenAgeCombat.CONFIG.get(CommonConfig.class)).oldAttackDamage) {
            for (java.util.Map.Entry<Class<? extends TieredItem>, Double> entry : ATTACK_DAMAGE_BONUS_OVERRIDES.entrySet()) {
               if (entry.getKey().isInstance(item)) {
                  setAttributeValue(
                     itemAttributeModifiers,
                     Attributes.ATTACK_DAMAGE,
                     Item.BASE_ATTACK_DAMAGE_ID,
                     ((TieredItem)item).getTier().getAttackDamageBonus() + entry.getValue()
                  );
                  break;
               }
            }
         }
      }
   }

   private static boolean setAttributeValue(
      Item item, List<Entry> itemAttributeModifiers, Holder<Attribute> attribute, ResourceLocation id, ConfigDataSet<Item> attackDamageOverrides
   ) {
      if (attackDamageOverrides.contains(item)) {
         double newValue = (Double)attackDamageOverrides.getOptional(item, 0).orElseThrow();
         setAttributeValue(itemAttributeModifiers, attribute, id, newValue);
         return true;
      } else {
         return false;
      }
   }

   private static void setAttributeValue(List<Entry> itemAttributeModifiers, Holder<Attribute> attribute, ResourceLocation id, double newValue) {
      AttributeModifier attributeModifier = new AttributeModifier(id, newValue, Operation.ADD_VALUE);
      Entry newEntry = new Entry(attribute, attributeModifier, EquipmentSlotGroup.MAINHAND);
      ListIterator<Entry> iterator = itemAttributeModifiers.listIterator();

      while (iterator.hasNext()) {
         Entry entry = iterator.next();
         if (entry.slot() == EquipmentSlotGroup.MAINHAND && entry.matches(attribute, id)) {
            iterator.set(newEntry);
            return;
         }
      }

      itemAttributeModifiers.add(newEntry);
   }
}
