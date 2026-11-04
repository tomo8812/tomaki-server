package net.caffeinemc.mods.lithium.common.entity;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public interface TypeFilterableListInternalAccess<T> {
   <S extends T> List<S> lithium$getOrCreateAllOfTypeRaw(Class<S> var1);

   <S extends T> List<S> lithium$replaceCollectionAndGet(Class<S> var1, Function<ArrayList<S>, List<S>> var2);

   <S extends T> List<S> lithium$replaceCollectionAndGet(Class<S> var1, ArrayList<S> var2);
}
