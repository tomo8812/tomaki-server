package org.embeddedt.modernfix.dynamiclanguages;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.ForwardingMap;
import com.google.common.collect.Maps;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.locale.Language;
import net.minecraft.server.packs.resources.Resource;
import org.embeddedt.modernfix.ModernFix;

public class DynamicLanguageMap extends ForwardingMap<String, String> {
   private volatile Map<String, Object> storage;
   private final LoadingCache<Resource, Map<String, String>> languageFileContents = CacheBuilder.newBuilder()
      .softValues()
      .build(new CacheLoader<Resource, Map<String, String>>() {
         public Map<String, String> load(Resource resource) {
            return DynamicLanguageMap.loadLanguageFile(resource);
         }

         // $VF: synthetic method
         // $VF: bridge method
         public Object load(Object param1) throws Exception {
            return this.load((Resource)var1);
         }
      });
   private volatile Map<String, String> delegate;

   private DynamicLanguageMap(Map<String, Object> storage) {
      this.storage = storage;
      this.delegate = Maps.asMap(storage.keySet(), this::lookup);
   }

   private static Map<String, Object> createStorage(Map<String, String> rawLanguageContents, List<Resource> languageResources) {
      Map<String, Object> storage = new HashMap<>(rawLanguageContents);

      for (Resource resource : languageResources) {
         try (InputStream stream = resource.open()) {
            Language.loadFromJson(stream, (key, value) -> {
               if (value != null && value.equals(storage.get(key))) {
                  storage.put(key, resource);
               }
            });
         } catch (Exception var10) {
         }
      }

      return Map.copyOf(storage);
   }

   public static Map<String, String> forVanillaData(Map<String, String> rawLanguageContents, List<Resource> languageResources) {
      return new DynamicLanguageMap(createStorage(rawLanguageContents, languageResources));
   }

   private static Map<String, String> loadLanguageFile(Resource resource) {
      Map<String, String> data = new Object2ObjectOpenHashMap();

      try (InputStream stream = resource.open()) {
         Language.loadFromJson(stream, data::put);
      } catch (Exception e) {
         ModernFix.LOGGER.error("Error loading language data from {}", resource.sourcePackId(), e);
      }

      return data;
   }

   private String lookup(String key) {
      Map<String, Object> storage = this.storage;
      if (storage == null) {
         return this.delegate.get(key);
      }

      Object value = storage.get(key);
      return value instanceof Resource r ? (String)((Map)this.languageFileContents.getUnchecked(r)).get(key) : (String)value;
   }

   protected Map<String, String> delegate() {
      return this.delegate;
   }

   public String getOrDefault(Object key, String defaultValue) {
      String value = this.delegate().get(key);
      return value != null ? value : defaultValue;
   }

   public void materialize() {
      Map<String, Object> storage = this.storage;
      if (storage != null) {
         Map<String, String> contents = Maps.newHashMapWithExpectedSize(storage.size());
         Map<Resource, List<String>> keysByResource = new IdentityHashMap<>();
         storage.forEach((key, value) -> {
            if (value instanceof Resource r) {
               keysByResource.computeIfAbsent(r, k -> new ArrayList<>()).add(key);
            } else {
               contents.put(key, (String)value);
            }
         });
         keysByResource.forEach((resource, keys) -> {
            Map<String, String> file = (Map<String, String>)this.languageFileContents.getIfPresent(resource);
            if (file == null) {
               file = loadLanguageFile(resource);
            } else {
               this.languageFileContents.invalidate(resource);
            }

            for (String key : keys) {
               String value = file.get(key);
               if (value != null) {
                  contents.put(key, value);
               }
            }
         });
         this.delegate = Map.copyOf(contents);
         this.storage = null;
         this.languageFileContents.invalidateAll();
      }
   }

   // $VF: synthetic method
   // $VF: bridge method
   public Object getOrDefault(Object param1, Object nullx) {
      return this.getOrDefault(var1, (String)nullx);
   }

   // $VF: synthetic method
   // $VF: bridge method
   protected Object delegate() {
      return this.delegate();
   }
}
