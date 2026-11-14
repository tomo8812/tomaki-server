package dev.diorama;

import dev.diorama.bake.BlockLooks;
import dev.diorama.data.ImageExport;
import dev.diorama.data.TileStore;
import dev.diorama.drawing.Drawing;
import dev.diorama.drawing.ExportDrawings;
import dev.diorama.render.CaveSliceTiles;
import dev.diorama.render.ExportFog;
import dev.diorama.render.MapLevel;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ClickEvent.Action;
import org.jspecify.annotations.Nullable;

public final class MapExport {
   private static final DateTimeFormatter NAME = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH.mm.ss");
   private static volatile @Nullable MapExport running;
   private final Path file;
   private volatile double progress;

   private MapExport(Path file) {
      this.file = file;
   }

   public static @Nullable MapExport running() {
      return running;
   }

   public double progress() {
      return this.progress;
   }

   public static boolean start(TileStore store, ImageExport.Area area, boolean smoke, List<Drawing> drawings) {
      return start(store, area, smoke, 0.0F, drawings);
   }

   public static boolean start(TileStore store, ImageExport.Area area, boolean smoke, float night, List<Drawing> drawings) {
      return start(() -> {
         store.catchUp();
         return ImageExport.Tiles.of(store);
      }, area, smoke ? ExportFog.Look.SMOKE : ExportFog.Look.FOG, night, drawings);
   }

   public static boolean startCave(MapLevel level, int layer, ImageExport.Area area, List<Drawing> drawings) {
      BlockLooks looks = BlockLooks.get();
      if (looks == null) {
         return false;
      }

      if (running != null) {
         return false;
      }

      CaveSliceTiles tiles = new CaveSliceTiles(level, layer, looks);
      return start(() -> tiles, area, ExportFog.Look.CAVE, 0.0F, drawings);
   }

   private static synchronized boolean start(MapExport.Source source, ImageExport.Area area, ExportFog.Look look, float night, List<Drawing> drawings) {
      if (running != null) {
         return false;
      }

      Minecraft mc = Minecraft.getInstance();
      Path folder = mc.gameDirectory.toPath().resolve("screenshots");
      ImageExport.deleteUnfinished(folder, "diorama_");
      Path file = folder.resolve("diorama_" + LocalDateTime.now().format(NAME) + ".png");

      for (int i = 1; file.toFile().exists(); i++) {
         file = folder.resolve("diorama_" + LocalDateTime.now().format(NAME) + "_" + i + ".png");
      }

      MapExport export = new MapExport(file);
      running = export;
      List<Drawing> copied = drawings.stream().map(Drawing::fixed).toList();
      Set<String> fontOptions = new HashSet<>();
      if ((Boolean)mc.options.forceUnicodeFont().get()) {
         fontOptions.add("uniform");
      }

      if ((Boolean)mc.options.japaneseGlyphVariants().get()) {
         fontOptions.add("jp");
      }

      Thread thread = new Thread(() -> export.run(source, area, look, night, copied, fontOptions), "diorama-export");
      thread.setDaemon(true);
      thread.setPriority(2);
      thread.start();
      return true;
   }

   private void run(MapExport.Source source, ImageExport.Area area, ExportFog.Look look, float night, List<Drawing> drawings, Set<String> fontOptions) {
      Component message;
      try {
         ImageExport.Tiles tiles = source.open();
         double drawing = tiles.prepareShare();
         tiles.prepare(area, p -> this.progress = p * drawing);
         ImageExport.Finish finish = ExportFog.build(tiles, area, look, night);
         if (!drawings.isEmpty()) {
            finish = finish.then(ExportDrawings.build(drawings, area, Minecraft.getInstance().getResourceManager(), fontOptions));
         }

         ImageExport.write(tiles, area, this.file, finish, night, p -> this.progress = drawing + (1.0 - drawing) * p);
         Path absolute = this.file.toAbsolutePath();
         Component link = Component.literal(absolute.getFileName().toString())
            .withStyle(ChatFormatting.UNDERLINE)
            .withStyle(style -> style.withClickEvent(new ClickEvent(Action.OPEN_FILE, absolute.toString())));
         message = Component.translatable("diorama.export.done", new Object[]{link});
      } catch (Exception e) {
         Diorama.LOGGER.error("Couldn't export the map to {}", this.file, e);
         message = Component.translatable("diorama.export.failed", new Object[]{String.valueOf(e.getMessage())});
      } finally {
         running = null;
      }

      Component shown = message;
      Minecraft mc = Minecraft.getInstance();
      mc.execute(() -> mc.gui.getChat().addMessage(shown));
   }

   private interface Source {
      ImageExport.Tiles open() throws InterruptedException;
   }
}
