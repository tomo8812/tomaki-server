package org.embeddedt.modernfix.common.mixin.perf.dynamic_resources;

import com.google.common.base.Stopwatch;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import java.lang.reflect.Method;
import java.time.Duration;
import java.util.Comparator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.TimeUnit;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelManager;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.neoforged.bus.api.Event;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.ModList;
import net.neoforged.fml.ModLoader;
import net.neoforged.fml.util.ObfuscationReflectionHelper;
import net.neoforged.neoforge.client.ClientHooks;
import net.neoforged.neoforge.client.event.ModelEvent.BakingCompleted;
import net.neoforged.neoforge.client.event.ModelEvent.ModifyBakingResult;
import org.embeddedt.modernfix.ModernFix;
import org.embeddedt.modernfix.neoforge.dynresources.ModelBakeEventHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ClientHooks.class)
public class ForgeHooksClientMixin {
   @Redirect(
      method = "onModifyBakingResult",
      at = @At(value = "INVOKE", target = "Lnet/neoforged/fml/ModLoader;postEvent(Lnet/neoforged/bus/api/Event;)V"),
      remap = false
   )
   private static void postNamespacedKeySetEvent(Event event) {
      if (!ModLoader.hasErrors()) {
         ModifyBakingResult bakeEvent = (ModifyBakingResult)event;
         Stopwatch globalTimer = Stopwatch.createStarted();
         Stopwatch selfTimer = Stopwatch.createStarted();
         ModelBakeEventHelper helper = new ModelBakeEventHelper(bakeEvent.getModels());
         selfTimer.stop();
         Method acceptEv = ObfuscationReflectionHelper.findMethod(ModContainer.class, "acceptEvent", new Class[]{Event.class});
         Map<String, Stopwatch> times = new Object2ObjectOpenHashMap();
         times.put("modernfix", selfTimer);
         ModList.get().forEachModContainer((id, mc) -> {
            Map<ModelResourceLocation, BakedModel> newRegistry = helper.wrapRegistry(id);
            ModifyBakingResult postedEvent = new ModifyBakingResult(newRegistry, bakeEvent.getTextureGetter(), bakeEvent.getModelBakery());
            Stopwatch timer = times.computeIfAbsent(id, $ -> Stopwatch.createUnstarted());
            timer.start();

            try {
               acceptEv.invoke(mc, postedEvent);
            } catch (ReflectiveOperationException e) {
               e.printStackTrace();
            }

            timer.stop();
         });
         globalTimer.stop();
         if (globalTimer.elapsed(TimeUnit.SECONDS) >= 1L) {
            ModernFix.LOGGER.warn("Posting dynamic ModelEvent.ModifyBakingResult to mods took {}, breakdown below:", globalTimer);
            times.entrySet()
               .stream()
               .sorted(Comparator.<Entry<String, Stopwatch>, Duration>comparing(e -> e.getValue().elapsed()).reversed())
               .filter(e -> e.getValue().elapsed(TimeUnit.MILLISECONDS) > 50L)
               .forEach(entry -> ModernFix.LOGGER.warn("    {}: {}", entry.getKey(), entry.getValue().toString()));
         }
      }
   }

   @Redirect(
      method = "onModelBake",
      at = @At(value = "INVOKE", target = "Lnet/neoforged/fml/ModLoader;postEvent(Lnet/neoforged/bus/api/Event;)V"),
      remap = false
   )
   private static void postNamespacedKeySetBakingCompletedEvent(Event event) {
      if (!ModLoader.hasErrors()) {
         BakingCompleted bakeEvent = (BakingCompleted)event;
         ModelManager modelManager = bakeEvent.getModelManager();
         ModelBakeEventHelper helper = new ModelBakeEventHelper(bakeEvent.getModels());
         Method acceptEv = ObfuscationReflectionHelper.findMethod(ModContainer.class, "acceptEvent", new Class[]{Event.class});
         ModList.get().forEachModContainer((id, mc) -> {
            Map<ModelResourceLocation, BakedModel> newRegistry = helper.wrapRegistry(id);
            BakingCompleted postedEvent = new BakingCompleted(modelManager, newRegistry, bakeEvent.getModelBakery());

            try {
               acceptEv.invoke(mc, postedEvent);
            } catch (ReflectiveOperationException e) {
               e.printStackTrace();
            }
         });
      }
   }
}
