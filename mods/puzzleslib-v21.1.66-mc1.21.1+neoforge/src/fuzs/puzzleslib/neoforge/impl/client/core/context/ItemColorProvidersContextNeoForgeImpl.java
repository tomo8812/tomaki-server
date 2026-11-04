package fuzs.puzzleslib.neoforge.impl.client.core.context;

import com.google.common.base.Preconditions;
import fuzs.puzzleslib.api.client.core.v1.context.ColorProvidersContext;
import fuzs.puzzleslib.neoforge.mixin.client.accessor.ItemColorsNeoForgeAccessor;
import java.util.Objects;
import java.util.function.BiConsumer;
import net.minecraft.client.color.item.ItemColor;
import net.minecraft.client.color.item.ItemColors;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.ItemLike;
import org.jetbrains.annotations.Nullable;

public record ItemColorProvidersContextNeoForgeImpl(BiConsumer<ItemColor, ItemLike> consumer, ItemColors itemColors)
   implements ColorProvidersContext<Item, ItemColor> {
   public void registerColorProvider(ItemColor provider, Item... items) {
      Objects.requireNonNull(provider, "provider is null");
      Objects.requireNonNull(items, "items is null");
      Preconditions.checkState(items.length > 0, "items is empty");

      for (ItemLike item : items) {
         Objects.requireNonNull(item, "item is null");
         this.consumer.accept(provider, item);
      }
   }

   @Nullable
   public ItemColor getProvider(Item item) {
      return ((ItemColorsNeoForgeAccessor)this.itemColors).puzzleslib$getItemColors().get(item);
   }

   // $VF: synthetic method
   // $VF: bridge method
   @Nullable
   @Override
   public Object getProvider(Object param1) {
      return this.getProvider((Item)var1);
   }

   // $VF: synthetic method
   // $VF: bridge method
   @Override
   public void registerColorProvider(Object param1, Object[] nullx) {
      this.registerColorProvider((ItemColor)var1, (Item[])nullx);
   }
}
