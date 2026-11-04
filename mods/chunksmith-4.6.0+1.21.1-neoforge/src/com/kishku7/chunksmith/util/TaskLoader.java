package com.kishku7.chunksmith.util;

import com.kishku7.chunksmith.Chunksmith;
import com.kishku7.chunksmith.GenerationTask;
import com.kishku7.chunksmith.Selection;
import com.kishku7.chunksmith.platform.World;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Stream;

public final class TaskLoader {
   private final Chunksmith chunky;
   private final Path savePath;
   private final Map<String, Properties> tasks = new ConcurrentHashMap<>();

   public TaskLoader(Chunksmith chunky) {
      this.chunky = chunky;
      this.savePath = chunky.getConfig().getDirectory().resolve("tasks");
      this.reload();
   }

   public void reload() {
      try {
         Files.createDirectories(this.savePath);
      } catch (IOException e) {
         e.printStackTrace();
      }

      try (Stream<Path> taskWalker = Files.walk(this.savePath)) {
         taskWalker.forEach(task -> {
            if (task.getFileName().toString().endsWith(".properties")) {
               try (InputStream input = Files.newInputStream(task)) {
                  Properties properties = new Properties();
                  properties.load(input);
                  String world = properties.getProperty("world");
                  if (world != null) {
                     this.tasks.put(world, properties);
                  }
               } catch (IOException e) {
                  ex.printStackTrace();
               }
            }
         });
      } catch (IOException e) {
         e.printStackTrace();
      }
   }

   public Optional<GenerationTask> loadTask(World world) {
      Properties task = this.tasks.get(world.getName());
      if (task == null) {
         return Optional.empty();
      }

      boolean cancelled = Input.tryBoolean(task.getProperty(TaskLoader.TaskProperty.CANCELLED.key())).orElse(false);
      double centerX = Input.tryDouble(task.getProperty(TaskLoader.TaskProperty.CENTER_X.key())).orElse(0.0);
      double centerZ = Input.tryDouble(task.getProperty(TaskLoader.TaskProperty.CENTER_Z.key())).orElse(0.0);
      double radiusX = Input.tryDouble(task.getProperty(TaskLoader.TaskProperty.RADIUS_X.key())).orElse(500.0);
      double radiusZ = Input.tryDouble(task.getProperty(TaskLoader.TaskProperty.RADIUS_Z.key())).orElse(radiusX);
      String pattern = task.getProperty(TaskLoader.TaskProperty.PATTERN.key(), "region");
      String file = task.getProperty(TaskLoader.TaskProperty.CSV.key());
      String shape = task.getProperty(TaskLoader.TaskProperty.SHAPE.key(), "square");
      double horizon = Input.tryDouble(task.getProperty(TaskLoader.TaskProperty.LOD_HORIZON.key())).orElse(0.0);
      Selection.Builder selection = Selection.builder(this.chunky, world)
         .centerX(centerX)
         .centerZ(centerZ)
         .radiusX(radiusX)
         .radiusZ(radiusZ)
         .pattern(Parameter.of(pattern, file))
         .shape(shape)
         .horizon(horizon);
      long chunks = Input.tryLong(task.getProperty(TaskLoader.TaskProperty.CHUNKS.key())).orElse(0L);
      long time = Input.tryLong(task.getProperty(TaskLoader.TaskProperty.TIME.key())).orElse(0L);
      return Optional.of(new GenerationTask(this.chunky, selection.build(), chunks, time, cancelled));
   }

   public List<GenerationTask> loadTasks() {
      List<GenerationTask> generationTasks = new ArrayList<>();

      for (World world : this.chunky.getServer().getWorlds()) {
         this.loadTask(world).ifPresent(generationTasks::add);
      }

      return generationTasks;
   }

   public void saveTask(GenerationTask task) {
      Selection selection = task.getSelection();
      String world = selection.world().getName();
      Properties properties = this.tasks.getOrDefault(world, new Properties());
      properties.setProperty(TaskLoader.TaskProperty.WORLD.key(), world);
      properties.setProperty(TaskLoader.TaskProperty.CANCELLED.key(), String.valueOf(task.isCancelled()));
      properties.setProperty(TaskLoader.TaskProperty.CENTER_X.key(), String.valueOf(selection.centerX()));
      properties.setProperty(TaskLoader.TaskProperty.CENTER_Z.key(), String.valueOf(selection.centerZ()));
      properties.setProperty(TaskLoader.TaskProperty.RADIUS_X.key(), String.valueOf(selection.radiusX()));
      String shape = task.getShape().name();
      if ("rectangle".equals(shape) || "ellipse".equals(shape)) {
         properties.setProperty(TaskLoader.TaskProperty.RADIUS_Z.key(), String.valueOf(selection.radiusZ()));
      }

      Parameter pattern = Parameter.of(task.getChunkIterator().name());
      properties.setProperty(TaskLoader.TaskProperty.PATTERN.key(), pattern.getType());
      if ("csv".equals(pattern.getType())) {
         pattern.getValue().ifPresent(file -> properties.setProperty(TaskLoader.TaskProperty.CSV.key(), file));
      }

      properties.setProperty(TaskLoader.TaskProperty.SHAPE.key(), shape);
      if (selection.horizon() > 0.0) {
         properties.setProperty(TaskLoader.TaskProperty.LOD_HORIZON.key(), String.valueOf(selection.horizon()));
      } else {
         properties.remove(TaskLoader.TaskProperty.LOD_HORIZON.key());
      }

      properties.setProperty(TaskLoader.TaskProperty.CHUNKS.key(), String.valueOf(task.getCount()));
      properties.setProperty(TaskLoader.TaskProperty.TIME.key(), String.valueOf(task.getTotalTime()));
      this.tasks.put(world, properties);
      this.writeTask(world, properties);
   }

   public void saveTasks() {
      for (GenerationTask task : this.chunky.getGenerationTasks().values()) {
         this.saveTask(task);
      }
   }

   public void cancelTask(World world) {
      this.loadTask(world).ifPresent(generationTask -> {
         generationTask.stop(true);
         this.saveTask(generationTask);
      });
   }

   public void cancelTasks() {
      this.loadTasks().forEach(generationTask -> this.cancelTask(generationTask.getSelection().world()));
   }

   private void writeTask(String world, Properties properties) {
      StringBuilder propertiesBuilder = new StringBuilder();

      for (TaskLoader.TaskProperty taskProperty : TaskLoader.TaskProperty.values()) {
         String propertyValue = properties.getProperty(taskProperty.key());
         if (propertyValue != null) {
            propertiesBuilder.append(taskProperty.key()).append('=').append(propertyValue).append('\n');
         }
      }

      String taskFileName = world.replace(':', '/') + ".properties";
      Path taskPath = this.savePath.resolve(taskFileName);

      try {
         Files.createDirectories(taskPath.getParent());
         Files.write(taskPath, propertiesBuilder.toString().getBytes());
      } catch (IOException e) {
         e.printStackTrace();
      }
   }

   private enum TaskProperty {
      WORLD("world"),
      CANCELLED("cancelled"),
      CENTER_X("center-x"),
      CENTER_Z("center-z"),
      RADIUS_X("radius"),
      RADIUS_Z("radius-z"),
      SHAPE("shape"),
      LOD_HORIZON("lod-horizon"),
      PATTERN("pattern"),
      CSV("csv"),
      CHUNKS("chunks"),
      TIME("time");

      private final String key;

      TaskProperty(String key) {
         this.key = key;
      }

      public String key() {
         return this.key;
      }

      // $VF: synthetic method
      private static TaskLoader.TaskProperty[] $values() {
         return new TaskLoader.TaskProperty[]{WORLD, CANCELLED, CENTER_X, CENTER_Z, RADIUS_X, RADIUS_Z, SHAPE, LOD_HORIZON, PATTERN, CSV, CHUNKS, TIME};
      }
   }
}
