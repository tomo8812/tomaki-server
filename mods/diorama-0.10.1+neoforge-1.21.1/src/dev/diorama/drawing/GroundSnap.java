package dev.diorama.drawing;

import dev.diorama.Facing;
import dev.diorama.Projection;
import dev.diorama.render.MapViewport;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import net.minecraft.core.BlockPos;
import org.jspecify.annotations.Nullable;

public final class GroundSnap {
   private static final int REACH = 512;
   private static final int MAX_GUESSES = 16;
   private static final long CLICK_WAIT_NS = TimeUnit.MILLISECONDS.toNanos(200L);
   private final GroundSnap.Picker picker;
   private int layer = Integer.MIN_VALUE;
   private @Nullable Facing facing;
   private GroundSnap.@Nullable Ground anchor;
   private GroundSnap.@Nullable Ground under;
   private GroundSnap.@Nullable Landing landing;

   GroundSnap(GroundSnap.Picker picker) {
      this.picker = picker;
   }

   void clicked(MapViewport.View view, double[] point) {
      this.forgetOtherLayers(view);
      this.anchor = this.ask(point[0], point[1]);
      this.under = null;
      this.landing = null;
   }

   private void forgetOtherLayers(MapViewport.View view) {
      Facing facing = Projection.facing();
      if (view.layer() != this.layer || facing != this.facing) {
         this.layer = view.layer();
         this.facing = facing;
         this.anchor = null;
         this.under = null;
         this.landing = null;
      }
   }

   double @Nullable [] snap(MapViewport.View view, double[] last, double[] cursor, boolean click) {
      this.forgetOtherLayers(view);
      long deadline = click ? System.nanoTime() + CLICK_WAIT_NS : 0L;
      GroundSnap.Ground anchor = this.anchor;
      if (anchor == null || anchor.u != last[0] || anchor.v != last[1] || click && anchor.answered() && anchor.block() == null) {
         anchor = this.anchor = this.ask(last[0], last[1]);
      }

      anchor.await(deadline);
      double[] from = anchor.point();
      if (from == null) {
         return null;
      }

      if (this.under == null || this.under.answered() && (this.under.u != cursor[0] || this.under.v != cursor[1]) || click) {
         GroundSnap.Ground fresh = this.ask(cursor[0], cursor[1]);
         if (this.under != null) {
            BlockPos before = this.under.block();
            fresh.previous = before != null ? before.getY() : this.under.previous;
         }

         this.under = fresh;
      }

      this.under.await(deadline);
      BlockPos about = this.under.block();
      int cursorY = about != null ? about.getY() : (this.under.previous != Integer.MIN_VALUE ? this.under.previous : (int)from[1]);
      double cursorZ = Projection.zAt(cursor[1], cursorY);
      double dx = cursor[0] - from[0];
      double dz = cursorZ - from[2];
      double angle = Math.round(Math.atan2(dz, dx) / (Math.PI / 4)) * (Math.PI / 4);
      double ax = Math.abs(Math.cos(angle)) < 1.0E-9 ? 0.0 : Math.signum(Math.cos(angle));
      double az = Math.abs(Math.sin(angle)) < 1.0E-9 ? 0.0 : Math.signum(Math.sin(angle));
      double along = (dx * ax + dz * az) / (ax * ax + az * az);
      double x = from[0] + ax * along;
      double z = from[2] + az * along;
      int blockX = (int)Math.floor(x);
      int blockZ = (int)Math.floor(z);
      GroundSnap.Landing landing = this.landing;
      if (landing == null || landing.blockX != blockX || landing.blockZ != blockZ || click && landing.done && !landing.found) {
         int guess = landing != null && landing.found ? landing.guess : cursorY;
         landing = this.landing = new GroundSnap.Landing(blockX, blockZ, guess);
      }

      landing.advance(x, z, deadline);
      double[] point = new double[]{x, Projection.v(landing.guess, z)};
      if (click) {
         this.anchor = landing.found
            ? new GroundSnap.Ground(point[0], point[1], CompletableFuture.completedFuture(null), new BlockPos(blockX, landing.guess, blockZ))
            : this.ask(point[0], point[1]);
         this.landing = null;
      }

      return point;
   }

   private GroundSnap.Ground ask(double u, double v) {
      return new GroundSnap.Ground(u, v, this.picker.at(u, v), null);
   }

   private static final class Ground {
      final double u;
      final double v;
      private final CompletableFuture<@Nullable BlockPos> pick;
      private @Nullable BlockPos block;
      private boolean answered;
      int previous = Integer.MIN_VALUE;

      Ground(double u, double v, CompletableFuture<@Nullable BlockPos> pick, @Nullable BlockPos known) {
         this.u = u;
         this.v = v;
         this.pick = pick;
         if (known != null) {
            this.block = known;
            this.answered = true;
         }
      }

      boolean answered() {
         if (!this.answered && this.pick.isDone()) {
            BlockPos world = !this.pick.isCompletedExceptionally() && !this.pick.isCancelled() ? this.pick.join() : null;
            if (world != null) {
               Facing facing = Projection.facing();
               this.block = new BlockPos(facing.blockX(world.getX(), world.getZ()), world.getY(), facing.blockZ(world.getX(), world.getZ()));
            }

            this.answered = true;
         }

         return this.answered;
      }

      @Nullable BlockPos block() {
         return this.answered() ? this.block : null;
      }

      double @Nullable [] point() {
         BlockPos block = this.block();
         if (block == null) {
            return null;
         }

         double z = Math.max(block.getZ(), Math.min(block.getZ() + 0.999, Projection.zAt(this.v, block.getY())));
         return new double[]{this.u, block.getY(), z};
      }

      void await(long deadline) {
         long left = deadline - System.nanoTime();
         if (!this.answered && deadline != 0L && left > 0L) {
            try {
               this.pick.get(left, TimeUnit.NANOSECONDS);
            } catch (InterruptedException e) {
               Thread.currentThread().interrupt();
            } catch (ExecutionException | TimeoutException var7) {
            }
         }
      }
   }

   private final class Landing {
      final int blockX;
      final int blockZ;
      int guess;
      private int low;
      private int high;
      private int guesses;
      private int inBlock = Integer.MIN_VALUE;
      boolean found;
      private boolean done;
      private GroundSnap.@Nullable Ground asked;

      Landing(int blockX, int blockZ, int guess) {
         this.blockX = blockX;
         this.blockZ = blockZ;
         this.guess = guess;
         this.low = guess - 512;
         this.high = guess + 512;
      }

      void advance(double x, double z, long deadline) {
         while (!this.done) {
            if (this.asked == null) {
               this.asked = GroundSnap.this.ask(x, Projection.v(this.guess, z));
            }

            this.asked.await(deadline);
            if (!this.asked.answered()) {
               return;
            }

            BlockPos seen = this.asked.block();
            this.asked = null;
            if (seen == null) {
               this.done = true;
            } else if (seen.getZ() == this.blockZ && seen.getY() == this.guess) {
               this.found = true;
               this.done = true;
            } else {
               if (seen.getZ() == this.blockZ) {
                  this.inBlock = Math.max(this.inBlock, seen.getY());
               }

               if (seen.getZ() >= this.blockZ && (seen.getZ() != this.blockZ || seen.getY() >= this.guess)) {
                  this.low = this.guess;
               } else {
                  this.high = this.guess;
               }

               int next = seen.getY() > this.low && seen.getY() < this.high ? seen.getY() : Math.floorDiv(this.low + this.high, 2);
               if (next > this.low && next < this.high && ++this.guesses < 16) {
                  this.guess = next;
               } else {
                  this.done = true;
                  if (this.inBlock != Integer.MIN_VALUE) {
                     this.guess = this.inBlock;
                     this.found = true;
                  }
               }
            }
         }
      }
   }

   public interface Picker {
      CompletableFuture<@Nullable BlockPos> at(double var1, double var3);
   }
}
