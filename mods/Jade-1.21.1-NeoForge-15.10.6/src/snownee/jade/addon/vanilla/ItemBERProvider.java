package snownee.jade.addon.vanilla;

import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;
import snownee.jade.api.Accessor;
import snownee.jade.api.BlockAccessor;
import snownee.jade.api.IBlockComponentProvider;
import snownee.jade.api.ITooltip;
import snownee.jade.api.JadeIds;
import snownee.jade.api.config.IPluginConfig;
import snownee.jade.api.ui.IElement;
import snownee.jade.api.ui.IElementHelper;

public enum ItemBERProvider implements IBlockComponentProvider {
   INSTANCE;

   @Nullable
   public IElement getIcon(BlockAccessor accessor, IPluginConfig config, IElement currentIcon) {
      if (accessor.getBlockEntity() != null) {
         ItemStack stack = accessor.getPickedResult();
         Minecraft.getInstance().addCustomNbtData(stack, accessor.getBlockEntity(), accessor.getLevel().registryAccess());
         return IElementHelper.get().item(stack);
      } else {
         return null;
      }
   }

   public void appendTooltip(ITooltip tooltip, BlockAccessor accessor, IPluginConfig config) {
   }

   @Override
   public ResourceLocation getUid() {
      return JadeIds.MC_ITEM_BER;
   }

   @Override
   public boolean isRequired() {
      return true;
   }

   // $VF: synthetic method
   // $VF: bridge method
   @Override
   public void appendTooltip(ITooltip param1, Accessor nullx, IPluginConfig nullxx) {
      this.appendTooltip(var1, (BlockAccessor)nullx, nullxx);
   }

   // $VF: synthetic method
   // $VF: bridge method
   @Nullable
   @Override
   public IElement getIcon(Accessor param1, IPluginConfig nullx, IElement nullxx) {
      return this.getIcon((BlockAccessor)var1, nullx, nullxx);
   }

   // $VF: synthetic method
   private static ItemBERProvider[] $values() {
      return new ItemBERProvider[]{INSTANCE};
   }
}
