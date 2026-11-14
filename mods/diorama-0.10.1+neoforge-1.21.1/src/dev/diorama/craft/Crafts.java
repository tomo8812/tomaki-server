package dev.diorama.craft;

import dev.diorama.Diorama;
import dev.diorama.bake.BlockLooks;
import dev.diorama.render.MapLevel;
import dev.diorama.render.MapViewport;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.entity.Entity;
import org.jspecify.annotations.Nullable;

public final class Crafts {
   private static final int READ_EVERY = 600;
   private static final int READ_CHANGED_AFTER = 10;
   private static final int PARK_AFTER = 100;
   private static final double STILL = 0.25;
   private static final double NEAR = 48.0;
   private static final int GONE_AFTER = 100;
   private static final int SAVE_EVERY = 1200;
   private static final List<Crafts.Source> SOURCES = new CopyOnWriteArrayList<>();
   private final ClientLevel level;
   private final Consumer<int[]> worldChanged;
   private final @Nullable Path file;
   private final Map<UUID, Craft> crafts = new HashMap<>();
   private final CraftPainter painter;
   private @Nullable CompletableFuture<List<CraftFile.Saved>> loading;
   private boolean dirty;
   private long savedAt;

   public static void register(Crafts.Source source) {
      SOURCES.add(source);
   }

   public Crafts(ClientLevel level, MapLevel ground, @Nullable Path folder, Consumer<int[]> worldChanged) {
      this.level = level;
      this.worldChanged = worldChanged;
      this.painter = new CraftPainter(ground);
      this.file = folder == null ? null : folder.resolve("crafts.bin");
      if (this.file != null) {
         Path from = this.file;
         this.loading = CompletableFuture.<List<CraftFile.Saved>>supplyAsync(() -> CraftFile.read(from), CraftPainter.BAKER).exceptionally(e -> {
            Diorama.LOGGER.warn("Couldn't read the parked crafts from {}", from, e);
            return List.of();
         });
      }
   }

   public void tick(@Nullable Entity viewer, long tick) {
      BlockLooks looks = BlockLooks.get();
      if (looks != null) {
         this.restore(tick);
         Set<UUID> live = new HashSet<>();
         List<Body> bodies = new ArrayList<>();

         for (Crafts.Source source : SOURCES) {
            bodies.addAll(source.bodies(this.level));
         }

         for (Body body : bodies) {
            live.add(body.id());
            Craft craft = this.crafts.computeIfAbsent(body.id(), Craft::new);
            boolean back = craft.parked;
            if (back) {
               craft.parked = false;
               this.dirty = true;
            }

            craft.body = body;
            craft.nearSince = -1L;
            boolean due = (tick + (craft.id.hashCode() & 2147483647)) % 600L == 0L;
            boolean changed = body.version() != craft.version && tick - craft.readAt >= 10L;
            if (craft.blocks == null || due || back || changed) {
               craft.readAt = tick;
               Body.Blocks read = body.read(looks);
               if (read != null) {
                  craft.version = body.version();
               }

               if (read != null
                  && (craft.blocks == null || !read.blocks().looksLike(craft.blocks.blocks(), looks) || !Arrays.equals(read.min(), craft.blocks.min()))) {
                  craft.blocks = read;
                  this.dirty = true;
               }
            }

            if (craft.blocks != null && body.pose(craft.blocks.min(), 1.0F, craft.pose)) {
               if (!craft.posed || distance(craft.pose, craft.anchor) > 0.25) {
                  System.arraycopy(craft.pose, 0, craft.anchor, 0, 3);
                  craft.movedAt = tick;
                  if (craft.settled) {
                     craft.settled = false;
                     this.dirty = true;
                  }
               }

               craft.posed = true;
               if (!craft.settled && tick - craft.movedAt >= 100L) {
                  craft.settled = true;
                  this.dirty = true;
               }

               if (!craft.announced) {
                  craft.announced = true;
                  this.worldChanged.accept(craft.box(craft.pose));
               }
            }
         }

         Iterator<Craft> it = this.crafts.values().iterator();

         while (it.hasNext()) {
            Craft craft = it.next();
            if (!live.contains(craft.id)) {
               if (craft.body != null) {
                  craft.body = null;
                  if (!craft.posed || craft.blocks == null || tick - craft.movedAt < 100L) {
                     if (craft.posed) {
                        this.worldChanged.accept(craft.box(craft.pose));
                     }

                     craft.close();
                     it.remove();
                     continue;
                  }

                  craft.parked = true;
                  craft.nearSince = -1L;
                  this.dirty = true;
               }

               if (craft.parked && viewer != null) {
                  double[] c = craft.center(craft.pose);
                  double dx = c[0] - viewer.getX();
                  double dz = c[2] - viewer.getZ();
                  if (dx * dx + dz * dz < 2304.0) {
                     if (craft.nearSince < 0L) {
                        craft.nearSince = tick;
                     } else if (tick - craft.nearSince > 100L) {
                        this.worldChanged.accept(craft.box(craft.pose));
                        craft.close();
                        it.remove();
                        this.dirty = true;
                     }
                  } else {
                     craft.nearSince = -1L;
                  }
               }
            }
         }

         if (this.dirty && tick - this.savedAt >= 1200L) {
            this.savedAt = tick;
            this.save(tick, false);
         }
      }
   }

   private static double distance(double[] pose, double[] anchor) {
      double dx = pose[0] - anchor[0];
      double dy = pose[1] - anchor[1];
      double dz = pose[2] - anchor[2];
      return Math.sqrt(dx * dx + dy * dy + dz * dz);
   }

   private void restore(long tick) {
      if (this.loading != null && this.loading.isDone()) {
         List<CraftFile.Saved> saved = this.loading.getNow(List.of());
         this.loading = null;

         for (CraftFile.Saved s : saved) {
            Body.Blocks blocks = s.decode(this.level);
            if (blocks != null && !this.crafts.containsKey(s.id())) {
               Craft craft = new Craft(s.id());
               craft.blocks = blocks;
               System.arraycopy(s.pose(), 0, craft.pose, 0, 12);
               craft.posed = true;
               craft.parked = true;
               craft.movedAt = tick - 100L;
               this.crafts.put(s.id(), craft);
            }
         }

         if (!saved.isEmpty() && Diorama.DEBUG) {
            Diorama.LOGGER.info("Restored {} parked crafts", saved.size());
         }
      }
   }

   private void save(long tick, boolean now) {
      if (this.file != null && this.loading == null) {
         this.dirty = false;
         List<CraftFile.Saved> keep = new ArrayList<>();

         for (Craft craft : this.crafts.values()) {
            if (craft.blocks != null && craft.posed && (craft.parked || tick - craft.movedAt >= 100L)) {
               keep.add(CraftFile.Saved.of(craft.id, craft.pose, craft.blocks));
            }
         }

         Path file = this.file;
         CompletableFuture<Void> written = CompletableFuture.runAsync(() -> CraftFile.write(file, keep), CraftPainter.BAKER);
         if (now) {
            written.join();
         }
      }
   }

   public void close(long tick) {
      if (this.loading != null) {
         this.loading.join();
         this.restore(tick);
      }

      this.save(tick, true);

      for (Craft craft : this.crafts.values()) {
         craft.close();
      }

      this.crafts.clear();
   }

   public boolean has(UUID id) {
      return this.crafts.containsKey(id);
   }

   public Body.@Nullable Blocks shown(UUID id) {
      Craft craft = this.crafts.get(id);
      return craft != null
            && craft.blocks != null
            && craft.texture != null
            && craft.pending == null
            && craft.imageFrom != null
            && craft.imageFrom.blocks() == craft.blocks.blocks()
         ? craft.blocks
         : null;
   }

   public void draw(GuiGraphics graphics, MapViewport.View view, float partialTick, float opacity) {
      this.painter.draw(graphics, view, this.crafts.values(), partialTick, opacity);
   }

   public interface Source {
      List<Body> bodies(ClientLevel var1);
   }
}
