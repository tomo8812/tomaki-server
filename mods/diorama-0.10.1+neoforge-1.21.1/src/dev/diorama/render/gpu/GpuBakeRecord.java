package dev.diorama.render.gpu;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dev.diorama.Diorama;
import java.io.Reader;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.LinkedHashMap;
import java.util.Map;
import net.minecraft.client.Minecraft;
import org.jspecify.annotations.Nullable;

final class GpuBakeRecord {
   private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
   Map<String, GpuBakeRecord.Entry> off = new LinkedHashMap<>();
   @Nullable String starting;
   long startingProcess;
   @Nullable String startingMod;

   GpuBakeCheck.@Nullable Off result(String device, String mod) {
      GpuBakeRecord.Entry entry = this.off.get(device);
      if (entry != null && mod.equals(entry.mod)) {
         return entry.result == GpuBakeCheck.Off.NO_MATCH && entry.times == 1 ? null : entry.result;
      } else {
         return null;
      }
   }

   void turnedOff(String device, GpuBakeCheck.Off why, String mod) {
      GpuBakeRecord.Entry before = this.off.get(device);
      int times = before != null && before.result == why && mod.equals(before.mod) ? before.times + 1 : 1;
      this.off.put(device, new GpuBakeRecord.Entry(why, mod, times));
   }

   static GpuBakeRecord load() {
      Path file = file();
      if (Files.isRegularFile(file)) {
         try (Reader reader = Files.newBufferedReader(file, StandardCharsets.UTF_8)) {
            GpuBakeRecord record = (GpuBakeRecord)GSON.fromJson(reader, GpuBakeRecord.class);
            if (record != null) {
               record.off = record.off == null ? new LinkedHashMap<>() : record.off;
               record.off.values().removeIf(entry -> entry == null || entry.result == null);
               return record;
            }
         } catch (Exception e) {
            Diorama.LOGGER.error("Couldn't read {}; every graphics card gets checked again", file, e);
         }
      }

      return new GpuBakeRecord();
   }

   void save() {
      Path file = file();

      try {
         Files.createDirectories(file.getParent());
         Path temp = file.resolveSibling("gpu-bake." + ProcessHandle.current().pid() + ".tmp");

         try (Writer writer = Files.newBufferedWriter(temp, StandardCharsets.UTF_8)) {
            GSON.toJson(this, writer);
         }

         Files.move(temp, file, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
      } catch (Exception e) {
         Diorama.LOGGER.error("Couldn't save {}", file, e);
      }
   }

   private static Path file() {
      return Minecraft.getInstance().gameDirectory.toPath().resolve("diorama").resolve("gpu-bake.json");
   }

   static final class Entry {
      GpuBakeCheck.Off result;
      String mod;
      int times;

      Entry(GpuBakeCheck.Off result, String mod, int times) {
         this.result = result;
         this.mod = mod;
         this.times = times;
      }
   }
}
