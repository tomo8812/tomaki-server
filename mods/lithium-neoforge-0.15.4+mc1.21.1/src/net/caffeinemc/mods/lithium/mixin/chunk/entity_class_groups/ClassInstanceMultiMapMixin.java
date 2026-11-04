package net.caffeinemc.mods.lithium.mixin.chunk.entity_class_groups;

import it.unimi.dsi.fastutil.objects.ObjectIterator;
import it.unimi.dsi.fastutil.objects.Reference2ReferenceArrayMap;
import it.unimi.dsi.fastutil.objects.ReferenceLinkedOpenHashSet;
import java.util.Collection;
import java.util.List;
import java.util.Map.Entry;
import net.caffeinemc.mods.lithium.common.entity.EntityClassGroup;
import net.caffeinemc.mods.lithium.common.world.chunk.ClassGroupFilterableList;
import net.minecraft.util.ClassInstanceMultiMap;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(ClassInstanceMultiMap.class)
public abstract class ClassInstanceMultiMapMixin<T> implements ClassGroupFilterableList<T> {
   @Shadow
   @Final
   private List<T> allInstances;
   private final Reference2ReferenceArrayMap<EntityClassGroup, ReferenceLinkedOpenHashSet<T>> entitiesByGroup = new Reference2ReferenceArrayMap();

   @ModifyVariable(method = "add(Ljava/lang/Object;)Z", at = @At("HEAD"), argsOnly = true)
   public T add(T entity) {
      ObjectIterator var2 = this.entitiesByGroup.entrySet().iterator();

      while (var2.hasNext()) {
         Entry<EntityClassGroup, ReferenceLinkedOpenHashSet<T>> entityGroupAndSet = (Entry<EntityClassGroup, ReferenceLinkedOpenHashSet<T>>)var2.next();
         EntityClassGroup entityClassGroup = entityGroupAndSet.getKey();
         if (entityClassGroup.contains((Entity)entity)) {
            entityGroupAndSet.getValue().add(entity);
         }
      }

      return entity;
   }

   @ModifyVariable(method = "remove(Ljava/lang/Object;)Z", at = @At("HEAD"), argsOnly = true)
   public Object remove(Object o) {
      ObjectIterator var2 = this.entitiesByGroup.values().iterator();

      while (var2.hasNext()) {
         ReferenceLinkedOpenHashSet<T> entitySet = (ReferenceLinkedOpenHashSet<T>)var2.next();
         entitySet.remove(o);
      }

      return o;
   }

   @Override
   public Collection<T> lithium$getAllOfGroupType(EntityClassGroup type) {
      Collection<T> collection = (Collection<T>)this.entitiesByGroup.get(type);
      if (collection == null) {
         collection = this.createAllOfGroupType(type);
      }

      return collection;
   }

   @Unique
   private Collection<T> createAllOfGroupType(EntityClassGroup type) {
      ReferenceLinkedOpenHashSet<T> allOfType = new ReferenceLinkedOpenHashSet();

      for (T entity : this.allInstances) {
         if (type.contains((Entity)entity)) {
            allOfType.add(entity);
         }
      }

      this.entitiesByGroup.put(type, allOfType);
      return allOfType;
   }
}
