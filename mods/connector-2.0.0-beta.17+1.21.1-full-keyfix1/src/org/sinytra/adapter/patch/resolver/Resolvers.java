package org.sinytra.adapter.patch.resolver;

import org.sinytra.adapter.env.util.OrderedRegistry;
import org.sinytra.adapter.patch.resolver.injection.InjectionPointResolver;
import org.sinytra.adapter.patch.resolver.special.SliceBoundaryResolver;
import org.sinytra.adapter.patch.resolver.target.TargetMethodResolver;

public class Resolvers extends OrderedRegistry<Resolver> {
   public Resolvers() {
      this(true);
   }

   public Resolvers(boolean registerDefault) {
      if (registerDefault) {
         this.registerDefaultResolvers();
      }
   }

   private void registerDefaultResolvers() {
      this.add(new TargetMethodResolver());
      this.add(new SliceBoundaryResolver());
      this.add(new InjectionPointResolver());
   }
}
