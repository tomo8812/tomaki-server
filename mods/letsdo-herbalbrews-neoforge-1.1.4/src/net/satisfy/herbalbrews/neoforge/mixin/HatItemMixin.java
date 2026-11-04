package net.satisfy.herbalbrews.neoforge.mixin;

import java.util.function.Consumer;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.Model;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ArmorItem.Type;
import net.minecraft.world.item.ArmorMaterial.Layer;
import net.minecraft.world.item.Item.Properties;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;
import net.satisfy.herbalbrews.core.items.HatItem;
import net.satisfy.herbalbrews.core.registry.ArmorRegistry;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(HatItem.class)
public abstract class HatItemMixin extends ArmorItem {
   @Shadow
   @Final
   private ResourceLocation hatTexture;

   public HatItemMixin(Holder<ArmorMaterial> arg, Type arg2, Properties arg3) {
      super(arg, arg2, arg3);
   }

   public void initializeClient(Consumer<IClientItemExtensions> consumer) {
      consumer.accept(new IClientItemExtensions() {
         @NotNull
         public Model getGenericArmorModel(LivingEntity livingEntity, ItemStack itemStack, EquipmentSlot equipmentSlot, HumanoidModel<?> original) {
            return ArmorRegistry.getHatModel(itemStack.getItem(), original.getHead());
         }
      });
   }

   @Nullable
   public ResourceLocation getArmorTexture(ItemStack stack, Entity entity, EquipmentSlot slot, Layer layer, boolean innerModel) {
      return this.hatTexture;
   }
}
