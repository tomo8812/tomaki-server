package org.embeddedt.modernfix.util;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.function.Function;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.core.registries.BuiltInRegistries;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ItemMesherMap<K> implements Map<K, ModelResourceLocation> {
   private final Function<K, ModelResourceLocation> getLocation;

   public ItemMesherMap(Function<K, ModelResourceLocation> getLocation) {
      this.getLocation = getLocation;
   }

   @Override
   public int size() {
      return BuiltInRegistries.ITEM.keySet().size();
   }

   @Override
   public boolean isEmpty() {
      return false;
   }

   @Override
   public boolean containsKey(Object key) {
      return true;
   }

   @Override
   public boolean containsValue(Object value) {
      return false;
   }

   public ModelResourceLocation get(Object key) {
      return this.getLocation.apply((K)key);
   }

   @Nullable
   public ModelResourceLocation put(K key, ModelResourceLocation value) {
      throw new UnsupportedOperationException();
   }

   public ModelResourceLocation remove(Object key) {
      throw new UnsupportedOperationException();
   }

   @Override
   public void putAll(@NotNull Map<? extends K, ? extends ModelResourceLocation> m) {
      throw new UnsupportedOperationException();
   }

   @Override
   public void clear() {
      throw new UnsupportedOperationException();
   }

   @NotNull
   @Override
   public Set<K> keySet() {
      throw new UnsupportedOperationException();
   }

   @NotNull
   @Override
   public Collection<ModelResourceLocation> values() {
      throw new UnsupportedOperationException();
   }

   @NotNull
   @Override
   public Set<Entry<K, ModelResourceLocation>> entrySet() {
      throw new UnsupportedOperationException();
   }

   // $VF: synthetic method
   // $VF: bridge method
   @Override
   public Object remove(Object param1) {
      return this.remove(var1);
   }

   // $VF: synthetic method
   // $VF: bridge method
   @Nullable
   @Override
   public Object put(Object param1, Object nullx) {
      return this.put((K)var1, (ModelResourceLocation)nullx);
   }

   // $VF: synthetic method
   // $VF: bridge method
   @Override
   public Object get(Object param1) {
      return this.get(var1);
   }
}
