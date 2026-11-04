package org.embeddedt.modernfix.common.mixin.perf.optimize_surface_rules;

import com.google.common.collect.ImmutableList;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import it.unimi.dsi.fastutil.objects.ObjectArraySet;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.function.Consumer;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.SurfaceRules.Context;
import net.minecraft.world.level.levelgen.SurfaceRules.RuleSource;
import net.minecraft.world.level.levelgen.SurfaceRules.SequenceRule;
import net.minecraft.world.level.levelgen.SurfaceRules.SurfaceRule;
import org.embeddedt.modernfix.annotation.FeatureLevel;
import org.embeddedt.modernfix.annotation.RequiresFeatureLevel;
import org.embeddedt.modernfix.annotation.RequiresMod;
import org.embeddedt.modernfix.world.gen.ExtendedSurfaceContext;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import terrablender.worldgen.surface.NamespacedSurfaceRuleSource;

@Mixin(NamespacedSurfaceRuleSource.class)
@RequiresMod("terrablender")
@RequiresFeatureLevel(FeatureLevel.BETA)
public class NamespacedSurfaceRuleSourceMixin {
   @Shadow
   @Final
   private Map<String, RuleSource> sources;
   @Shadow
   @Final
   private RuleSource base;

   @Inject(
      method = "apply(Lnet/minecraft/world/level/levelgen/SurfaceRules$Context;)Lnet/minecraft/world/level/levelgen/SurfaceRules$SurfaceRule;",
      at = @At("HEAD"),
      cancellable = true,
      remap = false
   )
   private void modernfix$fastApply(
      Context context, CallbackInfoReturnable<SurfaceRule> cir, @Share("possibleNamespaces") LocalRef<Set<String>> possibleNamespacesRef
   ) {
      Set<ResourceKey<Biome>> possibleBiomes = ((ExtendedSurfaceContext)context).mfix$getPossibleBiomes();
      if (possibleBiomes != null) {
         Set<String> namespaces = mfix$findNamespaces(possibleBiomes);
         possibleNamespacesRef.set(namespaces);
         if (namespaces.size() == 1) {
            String singleNamespace = namespaces.iterator().next();
            RuleSource namespacedSource = this.sources.get(singleNamespace);
            if (namespacedSource == null) {
               cir.setReturnValue((SurfaceRule)this.base.apply(context));
            } else {
               cir.setReturnValue(new SequenceRule(ImmutableList.of((SurfaceRule)namespacedSource.apply(context), (SurfaceRule)this.base.apply(context))));
            }
         }
      }
   }

   @ModifyArg(
      method = "apply(Lnet/minecraft/world/level/levelgen/SurfaceRules$Context;)Lnet/minecraft/world/level/levelgen/SurfaceRules$SurfaceRule;",
      at = @At(value = "INVOKE", target = "Ljava/util/Set;forEach(Ljava/util/function/Consumer;)V"),
      remap = false
   )
   private Consumer<Entry<String, RuleSource>> mfix$filterConsumer(
      Consumer<Entry<String, RuleSource>> originalConsumer, @Share("possibleNamespaces") LocalRef<Set<String>> possibleNamespacesRef
   ) {
      Set<String> possibleNamespaces = (Set<String>)possibleNamespacesRef.get();
      return possibleNamespaces == null ? originalConsumer : entry -> {
         if (possibleNamespaces.contains(entry.getKey())) {
            originalConsumer.accept(entry);
         }
      };
   }

   private static Set<String> mfix$findNamespaces(Set<ResourceKey<Biome>> possibleBiomes) {
      if (possibleBiomes.size() == 1) {
         return Set.of(possibleBiomes.iterator().next().location().getNamespace());
      }

      ObjectArraySet<String> namespaces = new ObjectArraySet(4);

      for (ResourceKey<Biome> key : possibleBiomes) {
         namespaces.add(key.location().getNamespace());
      }

      return Set.copyOf(namespaces);
   }
}
