package net.caffeinemc.mods.lithium.mixin.ai.task.replace_streams;

import java.util.Iterator;
import java.util.List;
import net.caffeinemc.mods.lithium.common.ai.WeightedListIterable;
import net.minecraft.world.entity.ai.behavior.ShufflingList;
import net.minecraft.world.entity.ai.behavior.ShufflingList.WeightedEntry;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(ShufflingList.class)
public class ShufflingListMixin<U> implements WeightedListIterable<U> {
   @Shadow
   @Final
   protected List<WeightedEntry<? extends U>> entries;

   @Override
   public Iterator<U> iterator() {
      return new WeightedListIterable.ListIterator<>(this.entries.iterator());
   }
}
