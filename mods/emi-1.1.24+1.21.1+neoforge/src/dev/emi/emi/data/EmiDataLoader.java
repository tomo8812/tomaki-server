package dev.emi.emi.data;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import dev.emi.emi.EmiPort;
import dev.emi.emi.runtime.EmiLog;
import java.io.InputStreamReader;
import java.util.function.Consumer;
import java.util.function.Supplier;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimplePreparableReloadListener;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.profiling.ProfilerFiller;

public class EmiDataLoader<T> extends SimplePreparableReloadListener<T> implements EmiResourceReloadListener {
   private static final Gson GSON = new Gson();
   private final ResourceLocation id;
   private final String path;
   private final Supplier<T> baseSupplier;
   private final EmiDataLoader.DataConsumer<T> prepare;
   private final Consumer<T> apply;

   public EmiDataLoader(ResourceLocation id, String path, Supplier<T> baseSupplier, EmiDataLoader.DataConsumer<T> prepare, Consumer<T> apply) {
      this.id = id;
      this.path = path;
      this.baseSupplier = baseSupplier;
      this.prepare = prepare;
      this.apply = apply;
   }

   public T prepare(ResourceManager manager, ProfilerFiller profiler) {
      T t = this.baseSupplier.get();

      for (ResourceLocation id : EmiPort.findResources(manager, this.path, i -> i.endsWith(".json"))) {
         if (id.getNamespace().equals("emi")) {
            for (Resource resource : manager.getResourceStack(id)) {
               try {
                  InputStreamReader reader = new InputStreamReader(EmiPort.getInputStream(resource));
                  JsonObject json = (JsonObject)GsonHelper.fromJson(GSON, reader, JsonObject.class);
                  this.prepare.accept(t, json, id);
               } catch (Exception e) {
                  EmiLog.error("Error loading data for " + this.id + " in " + id, e);
               }
            }
         }
      }

      return t;
   }

   public void apply(T t, ResourceManager manager, ProfilerFiller profiler) {
      this.apply.accept(t);
   }

   @Override
   public ResourceLocation getEmiId() {
      return this.id;
   }

   public interface DataConsumer<T> {
      void accept(T var1, JsonObject var2, ResourceLocation var3);
   }
}
