package org.sinytra.adapter.patch;

import com.google.common.base.Suppliers;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Supplier;
import org.sinytra.adapter.analysis.locals.LocalVariableLookup;
import org.sinytra.adapter.env.ann.AtData;
import org.sinytra.adapter.env.ctx.MixinContext;
import org.sinytra.adapter.env.ctx.TargetPair;
import org.sinytra.adapter.patch.config.Configuration;
import org.sinytra.adapter.patch.processor.Processors;
import org.sinytra.adapter.patch.resolver.Resolvers;

public final class Recipe {
   private final Configuration clean;
   private final Configuration dirty;
   private final Resolvers resolvers;
   private final Processors processors;
   private final MixinContext context;
   private final Supplier<TargetPair> cleanTarget;
   private final Supplier<TargetPair> newCleanTarget;
   private final Supplier<TargetPair> dirtyTarget;
   private final Supplier<LocalVariableLookup> cleanLocalsTableCache;
   private final Supplier<LocalVariableLookup> dirtyLocalsTableCache;

   public Recipe(Configuration clean, Configuration dirty, Resolvers resolvers, Processors processors, MixinContext context) {
      this.clean = clean;
      this.dirty = dirty;
      this.resolvers = resolvers;
      this.processors = processors;
      this.context = context;
      this.cleanTarget = Suppliers.memoize(
         () -> clean.getTargetMethod() == null ? null : context.methods().findOwnMethodPair(context.cleanLookup(), clean.getTargetMethod())
      );
      this.newCleanTarget = Suppliers.memoize(
         () -> clean.getTargetMethod() == null ? null : context.methods().findOwnMethodPair(context.dirtyLookup(), clean.getTargetMethod())
      );
      this.dirtyTarget = Suppliers.memoize(
         () -> dirty.getTargetMethod() == null ? null : context.methods().findOwnMethodPair(context.dirtyLookup(), dirty.getTargetMethod())
      );
      this.cleanLocalsTableCache = Suppliers.memoize(
         () -> Optional.ofNullable(this.getCleanTarget()).map(pair -> new LocalVariableLookup(pair.methodNode())).orElse(null)
      );
      this.dirtyLocalsTableCache = Suppliers.memoize(
         () -> Optional.ofNullable(this.getDirtyTarget()).map(pair -> new LocalVariableLookup(pair.methodNode())).orElse(null)
      );
   }

   public LocalVariableLookup cleanLocalsTable() {
      return this.cleanLocalsTableCache.get();
   }

   public LocalVariableLookup dirtyLocalsTable() {
      return this.dirtyLocalsTableCache.get();
   }

   public Recipe withDirtyConfig(Configuration dirty) {
      return new Recipe(this.clean, dirty, this.resolvers, this.processors, this.context);
   }

   public TargetPair getCleanTarget() {
      return this.cleanTarget.get();
   }

   public TargetPair getNewCleanTarget() {
      return this.newCleanTarget.get();
   }

   public TargetPair getDirtyTarget() {
      return this.dirtyTarget.get();
   }

   public boolean hasInjectionPointValue(String value) {
      AtData at = this.clean.getAtData();
      return at != null && value.equals(at.getValue());
   }

   public Configuration clean() {
      return this.clean;
   }

   public Configuration dirty() {
      return this.dirty;
   }

   public Resolvers resolvers() {
      return this.resolvers;
   }

   public Processors processors() {
      return this.processors;
   }

   public MixinContext context() {
      return this.context;
   }

   @Override
   public boolean equals(Object obj) {
      if (obj == this) {
         return true;
      } else if (obj != null && obj.getClass() == this.getClass()) {
         Recipe that = (Recipe)obj;
         return Objects.equals(this.clean, that.clean)
            && Objects.equals(this.dirty, that.dirty)
            && Objects.equals(this.resolvers, that.resolvers)
            && Objects.equals(this.processors, that.processors)
            && Objects.equals(this.context, that.context);
      } else {
         return false;
      }
   }

   @Override
   public int hashCode() {
      return Objects.hash(this.clean, this.dirty, this.resolvers, this.processors, this.context);
   }

   @Override
   public String toString() {
      return "Recipe[clean="
         + this.clean
         + ", dirty="
         + this.dirty
         + ", resolvers="
         + this.resolvers
         + ", processors="
         + this.processors
         + ", context="
         + this.context
         + "]";
   }
}
