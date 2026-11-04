package snownee.jade.addon.vanilla;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Display.ItemDisplay;
import org.jetbrains.annotations.Nullable;
import snownee.jade.api.Accessor;
import snownee.jade.api.EntityAccessor;
import snownee.jade.api.IEntityComponentProvider;
import snownee.jade.api.ITooltip;
import snownee.jade.api.JadeIds;
import snownee.jade.api.config.IPluginConfig;
import snownee.jade.api.ui.IElement;
import snownee.jade.api.ui.IElementHelper;

public enum ItemDisplayProvider implements IEntityComponentProvider {
   INSTANCE;

   @Nullable
   public IElement getIcon(EntityAccessor accessor, IPluginConfig config, IElement currentIcon) {
      ItemDisplay itemDisplay = (ItemDisplay)accessor.getEntity();
      return itemDisplay.getSlot(0).get().isEmpty() ? null : IElementHelper.get().item(itemDisplay.getSlot(0).get());
   }

   @Override
   public ResourceLocation getUid() {
      return JadeIds.MC_ITEM_DISPLAY;
   }

   public void appendTooltip(ITooltip tooltip, EntityAccessor accessor, IPluginConfig config) {
   }

   @Override
   public boolean isRequired() {
      return true;
   }

   // $VF: synthetic method
   // $VF: bridge method
   @Override
   public void appendTooltip(ITooltip param1, Accessor nullx, IPluginConfig nullxx) {
      this.appendTooltip(var1, (EntityAccessor)nullx, nullxx);
   }

   // $VF: synthetic method
   // $VF: bridge method
   @Nullable
   @Override
   public IElement getIcon(Accessor param1, IPluginConfig nullx, IElement nullxx) {
      return this.getIcon((EntityAccessor)var1, nullx, nullxx);
   }

   // $VF: synthetic method
   private static ItemDisplayProvider[] $values() {
      return new ItemDisplayProvider[]{INSTANCE};
   }
}
