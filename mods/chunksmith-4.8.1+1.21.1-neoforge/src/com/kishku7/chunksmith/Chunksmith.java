package com.kishku7.chunksmith;

import com.kishku7.chunksmith.api.ChunksmithAPI;
import com.kishku7.chunksmith.api.ChunksmithAPIImpl;
import com.kishku7.chunksmith.command.CancelCommand;
import com.kishku7.chunksmith.command.CenterCommand;
import com.kishku7.chunksmith.command.ChunksmithCommand;
import com.kishku7.chunksmith.command.ConfirmCommand;
import com.kishku7.chunksmith.command.ContinueCommand;
import com.kishku7.chunksmith.command.CornersCommand;
import com.kishku7.chunksmith.command.DebugCommand;
import com.kishku7.chunksmith.command.HelpCommand;
import com.kishku7.chunksmith.command.HorizonCommand;
import com.kishku7.chunksmith.command.PatternCommand;
import com.kishku7.chunksmith.command.PauseCommand;
import com.kishku7.chunksmith.command.ProgressCommand;
import com.kishku7.chunksmith.command.QuietCommand;
import com.kishku7.chunksmith.command.RadiusCommand;
import com.kishku7.chunksmith.command.ReloadCommand;
import com.kishku7.chunksmith.command.SelectionCommand;
import com.kishku7.chunksmith.command.SetCommand;
import com.kishku7.chunksmith.command.ShapeCommand;
import com.kishku7.chunksmith.command.SilentCommand;
import com.kishku7.chunksmith.command.SpawnCommand;
import com.kishku7.chunksmith.command.StartCommand;
import com.kishku7.chunksmith.command.StatusCommand;
import com.kishku7.chunksmith.command.TrimCommand;
import com.kishku7.chunksmith.command.WorldBorderCommand;
import com.kishku7.chunksmith.command.WorldCommand;
import com.kishku7.chunksmith.event.EventBus;
import com.kishku7.chunksmith.platform.Config;
import com.kishku7.chunksmith.platform.Sender;
import com.kishku7.chunksmith.platform.Server;
import com.kishku7.chunksmith.util.AutoPause;
import com.kishku7.chunksmith.util.ChunkResidency;
import com.kishku7.chunksmith.util.ChunkSettleSupport;
import com.kishku7.chunksmith.util.Input;
import com.kishku7.chunksmith.util.PendingAction;
import com.kishku7.chunksmith.util.RegionCache;
import com.kishku7.chunksmith.util.TaskLoader;
import com.kishku7.chunksmith.util.TaskScheduler;
import com.kishku7.chunksmith.util.Translator;
import com.kishku7.chunksmith.util.Version;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

public final class Chunksmith {
   private final Server server;
   private final Config config;
   private final TaskLoader taskLoader;
   private final EventBus eventBus;
   private final Selection.Builder selection;
   private final TaskScheduler scheduler = new TaskScheduler();
   private final Map<String, GenerationTask> generationTasks = new ConcurrentHashMap<>();
   private final Map<String, TrimCommand.Task> trimTasks = new ConcurrentHashMap<>();
   private final Map<String, PendingAction> pendingActions = new HashMap<>();
   private final RegionCache regionCache = new RegionCache();
   private final double limit;
   private final Version version;
   private final Map<String, ChunksmithCommand> commands;
   private final ChunksmithAPI api;

   public Chunksmith(Server server, Config config) {
      this.server = server;
      this.config = config;
      this.taskLoader = new TaskLoader(this);
      this.eventBus = new EventBus();
      this.selection = Selection.builder(this, server.getWorlds().get(0));
      this.limit = this.loadLimit().orElse(Double.MAX_VALUE);
      this.version = this.loadVersion();
      this.commands = this.loadCommands();
      this.api = new ChunksmithAPIImpl(this);
      ChunksmithProvider.register(this);
   }

   public void disable() {
      this.taskLoader.saveTasks();
      this.getGenerationTasks().values().forEach(generationTask -> generationTask.stop(false));
      this.getScheduler().cancelTasks();
      AutoPause.clear();
      ChunkResidency.clear();
      ChunkSettleSupport.forget();
      ChunksmithProvider.unregister();
   }

   private Optional<Double> loadLimit() {
      Path limitFile = this.config.getDirectory().resolve(".chunky.properties");

      try (InputStream input = Files.newInputStream(limitFile)) {
         Properties properties = new Properties();
         properties.load(input);
         return Input.tryDouble(properties.getProperty("radius-limit"));
      } catch (IOException e) {
         return Optional.empty();
      }
   }

   private Version loadVersion() {
      try (InputStream input = this.getClass().getClassLoader().getResourceAsStream("version.properties")) {
         Properties properties = new Properties();
         properties.load(input);
         return new Version(properties.getProperty("version"));
      } catch (IOException e) {
         return Version.INVALID;
      }
   }

   private Map<String, ChunksmithCommand> loadCommands() {
      Map<String, ChunksmithCommand> commandMap = new HashMap<>();
      commandMap.put("cancel", new CancelCommand(this));
      commandMap.put("center", new CenterCommand(this));
      commandMap.put("confirm", new ConfirmCommand(this));
      commandMap.put("continue", new ContinueCommand(this));
      commandMap.put("corners", new CornersCommand(this));
      DebugCommand debugCommand = new DebugCommand(this);
      commandMap.put("debug", debugCommand);
      commandMap.put("diag", debugCommand);
      commandMap.put("help", new HelpCommand(this));
      commandMap.put("horizon", new HorizonCommand(this));
      commandMap.put("pattern", new PatternCommand(this));
      commandMap.put("pause", new PauseCommand(this));
      commandMap.put("progress", new ProgressCommand(this));
      commandMap.put("quiet", new QuietCommand(this));
      commandMap.put("radius", new RadiusCommand(this));
      commandMap.put("reload", new ReloadCommand(this));
      commandMap.put("selection", new SelectionCommand(this));
      commandMap.put("shape", new ShapeCommand(this));
      commandMap.put("set", new SetCommand(this));
      commandMap.put("silent", new SilentCommand(this));
      commandMap.put("spawn", new SpawnCommand(this));
      commandMap.put("status", new StatusCommand(this));
      commandMap.put("start", new StartCommand(this));
      commandMap.put("trim", new TrimCommand(this));
      commandMap.put("worldborder", new WorldBorderCommand(this));
      commandMap.put("world", new WorldCommand(this));
      return commandMap;
   }

   public TaskScheduler getScheduler() {
      return this.scheduler;
   }

   public Server getServer() {
      return this.server;
   }

   public Config getConfig() {
      return this.config;
   }

   public TaskLoader getTaskLoader() {
      return this.taskLoader;
   }

   public EventBus getEventBus() {
      return this.eventBus;
   }

   public Map<String, GenerationTask> getGenerationTasks() {
      return this.generationTasks;
   }

   public Map<String, TrimCommand.Task> getTrimTasks() {
      return this.trimTasks;
   }

   public Map<String, ChunksmithCommand> getCommands() {
      return this.commands;
   }

   public Selection.Builder getSelection() {
      return this.selection;
   }

   public Optional<Runnable> getPendingAction(Sender sender) {
      this.pendingActions.values().removeIf(PendingAction::hasExpired);
      PendingAction pendingAction = this.pendingActions.remove(sender.getName());
      return Optional.ofNullable(pendingAction).map(PendingAction::getAction);
   }

   public void setPendingAction(Sender sender, Runnable action) {
      this.pendingActions.put(sender.getName(), new PendingAction(action));
   }

   public void setLanguage(String language) {
      Translator.setLanguage(language);
   }

   public RegionCache getRegionCache() {
      return this.regionCache;
   }

   public double getLimit() {
      return this.limit;
   }

   public Version getVersion() {
      return this.version;
   }

   public ChunksmithAPI getApi() {
      return this.api;
   }
}
