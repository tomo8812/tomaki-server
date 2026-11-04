package org.sinytra.connector.locator.filter;

import cpw.mods.niofs.union.UnionPathFilter;
import java.nio.file.Path;
import java.util.Set;

public record PackageTracker(Set<String> packages) implements UnionPathFilter {
   public boolean test(String path, Path basePath) {
      if (!this.packages.isEmpty() && !path.startsWith("META-INF/")) {
         int idx = path.lastIndexOf(47);
         return idx < 0 || idx == path.length() - 1 || !this.packages.contains(path.substring(0, idx).replace('/', '.'));
      } else {
         return true;
      }
   }
}
