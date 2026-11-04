package dev.terminalmc.clientsort.client.util;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;

public class TaskManager {
   private final List<TaskManager.Task> tasks = new ArrayList<>();
   private final LinkedBlockingQueue<TaskManager.Task> pendingTasks = new LinkedBlockingQueue<>();

   public void schedule(int ticks, Runnable task) {
      this.pendingTasks.add(new TaskManager.Task(ticks, task));
   }

   public void tick() {
      this.tasks.removeIf(TaskManager.Task::tick);
      this.pendingTasks.drainTo(this.tasks);
   }

   private static class Task {
      int ticks;
      final Runnable task;

      public Task(int ticks, Runnable task) {
         this.ticks = ticks;
         this.task = task;
      }

      public boolean tick() {
         if (this.ticks-- <= 0) {
            this.task.run();
            return true;
         } else {
            return false;
         }
      }
   }
}
