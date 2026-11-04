package net.caffeinemc.mods.lithium.mixin.minimal_nonvanilla.spawning;

import com.google.common.collect.AbstractIterator;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.objects.ObjectCollection;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import java.util.Iterator;
import net.caffeinemc.mods.lithium.common.world.ChunkAwareEntityIterable;
import net.minecraft.world.level.entity.EntityAccess;
import net.minecraft.world.level.entity.EntitySection;
import net.minecraft.world.level.entity.EntitySectionStorage;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(EntitySectionStorage.class)
public abstract class EntitySectionStorageMixin<T extends EntityAccess> implements ChunkAwareEntityIterable<T> {
   @Shadow
   @Final
   private Long2ObjectMap<EntitySection<T>> sections;

   @Override
   public Iterable<T> lithium$IterateEntitiesInTrackedSections() {
      ObjectCollection<EntitySection<T>> sections = this.sections.values();
      return () -> {
         final ObjectIterator<EntitySection<T>> sectionsIterator = sections.iterator();
         return new AbstractIterator<T>() {
            Iterator<T> entityIterator;

            @Nullable
            protected T computeNext() {
               if (this.entityIterator != null && this.entityIterator.hasNext()) {
                  return this.entityIterator.next();
               }

               while (sectionsIterator.hasNext()) {
                  EntitySection<T> section = (EntitySection<T>)sectionsIterator.next();
                  if (section.getStatus().isAccessible() && !section.isEmpty()) {
                     this.entityIterator = ((EntitySectionAccessor)section).getCollection().iterator();
                     if (this.entityIterator.hasNext()) {
                        return this.entityIterator.next();
                     }
                  }
               }

               return (T)this.endOfData();
            }

            // $VF: synthetic method
            // $VF: bridge method
            @Nullable
            protected Object computeNext() {
               return this.computeNext();
            }
         };
      };
   }
}
