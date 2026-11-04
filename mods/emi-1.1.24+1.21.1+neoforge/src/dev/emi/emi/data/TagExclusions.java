package dev.emi.emi.data;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import java.util.Map;
import java.util.Set;
import net.minecraft.resources.ResourceLocation;

public class TagExclusions {
   public final Set<ResourceLocation> globalExclusions = Sets.newHashSet();
   public final Map<ResourceLocation, Set<ResourceLocation>> exclusions = Maps.newHashMap();

   public void add(ResourceLocation id) {
      this.globalExclusions.add(id);
   }

   public void add(ResourceLocation type, ResourceLocation id) {
      this.exclusions.computeIfAbsent(type, t -> Sets.newHashSet()).add(id);
   }

   public void clear() {
      this.globalExclusions.clear();
      this.exclusions.clear();
   }

   public boolean contains(ResourceLocation type, ResourceLocation id) {
      return this.globalExclusions.contains(id) || this.exclusions.containsKey(type) && this.exclusions.get(type).contains(id);
   }
}
