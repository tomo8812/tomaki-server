package net.caffeinemc.mods.lithium.mixin.util.entity_collection_replacement;

import java.util.AbstractCollection;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import net.caffeinemc.mods.lithium.common.entity.TypeFilterableListInternalAccess;
import net.minecraft.util.ClassInstanceMultiMap;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(ClassInstanceMultiMap.class)
public abstract class ClassInstanceMultiMapMixin<T> extends AbstractCollection<T> implements TypeFilterableListInternalAccess<T> {
   @Shadow
   @Final
   private Map<Class<?>, List<T>> byClass;

   @Shadow
   public abstract <S> Collection<S> find(Class<S> var1);

   @Override
   public <S extends T> List<S> lithium$getOrCreateAllOfTypeRaw(Class<S> type) {
      List<S> s = this.byClass.get(type);
      if (s == null) {
         this.find(type);
         s = this.byClass.get(type);
      }

      return s;
   }

   @Override
   public <S extends T> List<S> lithium$replaceCollectionAndGet(Class<S> type, Function<ArrayList<S>, List<S>> listCtor) {
      List<T> oldList = this.byClass.get(type);
      List<S> newList = listCtor.apply((ArrayList<S>)oldList);
      this.byClass.put(type, newList);
      return newList;
   }

   @Override
   public <S extends T> List<S> lithium$replaceCollectionAndGet(Class<S> type, ArrayList<S> list) {
      this.byClass.put(type, list);
      return list;
   }
}
