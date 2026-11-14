package maki.chunksmithcontroller;

import java.util.ArrayDeque;
import maki.chunksmithfixes.GenerationWorkEvent;

final class ControlledWorkQueue {
   private final ArrayDeque<GenerationWorkEvent> waiting = new ArrayDeque<>();
   private volatile int running;
   private long epoch;

   synchronized void submit(GenerationWorkEvent event) {
      this.waiting.add(event);
   }

   int running() {
      return this.running;
   }

   synchronized void tick(int capacity, boolean resourcesAvailable) {
      if (resourcesAvailable) {
         int slots = Math.max(0, capacity - this.running);

         for (int i = 0; i < slots && !this.waiting.isEmpty(); i++) {
            GenerationWorkEvent work = this.waiting.remove();
            this.running++;
            long runEpoch = this.epoch;

            try {
               work.start().whenComplete((unused, error) -> this.finished(runEpoch));
            } catch (RuntimeException error) {
               this.finished(runEpoch);
            }
         }
      }
   }

   private synchronized void finished(long runEpoch) {
      if (runEpoch == this.epoch) {
         this.running--;
      }
   }

   synchronized void clear() {
      this.epoch++;
      this.waiting.clear();
      this.running = 0;
   }

   synchronized int waiting() {
      return this.waiting.size();
   }
}
