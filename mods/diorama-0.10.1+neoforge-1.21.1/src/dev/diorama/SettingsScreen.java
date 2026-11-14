package dev.diorama;

import com.mojang.blaze3d.systems.RenderSystem;
import dev.diorama.render.Pins;
import dev.diorama.render.TextSize;
import dev.diorama.render.gpu.GpuBakeCheck;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;
import java.util.function.IntConsumer;
import java.util.function.IntFunction;
import java.util.function.IntSupplier;
import java.util.function.Supplier;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractSliderButton;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Checkbox;
import net.minecraft.client.gui.components.StringWidget;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.components.tabs.GridLayoutTab;
import net.minecraft.client.gui.components.tabs.Tab;
import net.minecraft.client.gui.components.tabs.TabManager;
import net.minecraft.client.gui.components.tabs.TabNavigationBar;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.jspecify.annotations.Nullable;

public final class SettingsScreen extends Screen {
   private static final int SIDE = 340;
   private static final int PREVIEW_ROOM = 140;
   private static final int CONTENT = 280;
   private static final int COLUMN_GAP = 8;
   private static final int HEADING = 11;
   private static final int TAB_BAR_HEIGHT = 24;
   private static final ResourceLocation LIST_BACKGROUND = ResourceLocation.withDefaultNamespace("textures/gui/inworld_menu_list_background.png");
   private final @Nullable Screen parent;
   private final TabManager tabManager = new TabManager(x$0 -> {
      AbstractWidget var10000 = (AbstractWidget)this.addRenderableWidget(x$0);
   }, x$0 -> this.removeWidget(x$0));
   private final MinimapHud preview = new MinimapHud();
   private @Nullable TabNavigationBar tabBar;
   private List<SettingsScreen.SettingsTab> tabs = List.of();
   private int side = 340;
   private @Nullable Button gpuBaking;
   private boolean gpuFailedShown;

   public SettingsScreen(@Nullable Screen parent) {
      super(Component.translatable("diorama.settings.title"));
      this.parent = parent;
   }

   protected void init() {
      Settings s = Settings.get();
      Tab current = this.tabManager.getCurrentTab();
      int selected = current == null ? 0 : Math.max(0, this.tabs.indexOf(current));
      this.side = Math.max(Math.min(340, this.width - 140), Math.min(this.width, 290));
      SettingsScreen.SettingsTab minimap = new SettingsScreen.SettingsTab("diorama.settings.minimap");
      minimap.toggle("minimap_shown", () -> s.minimap, v -> s.minimap = v);
      minimap.toggle("compass", () -> s.compass, v -> s.compass = v);
      minimap.toggle("coordinates", () -> s.coordinates, v -> s.coordinates = v);
      minimap.toggle("biome", () -> s.biome, v -> s.biome = v);
      minimap.toggle("auto_caves", () -> s.autoCaves, v -> s.autoCaves = v, ServerRules.caveMapsAllowed() ? null : false);
      minimap.toggle("prefer_surface", () -> s.preferSurface, v -> s.preferSurface = v);
      minimap.toggle("legible_caves", () -> s.legibleCaves, v -> s.legibleCaves = v);
      minimap.toggle("enlarge_toggle", () -> s.minimapEnlargeToggle, v -> s.minimapEnlargeToggle = v);
      minimap.open("position", MinimapPositionScreen.placeName(), () -> this.minecraft.setScreen(new MinimapPositionScreen(this)));
      minimap.choice("game_time", Settings.GameTime.values(), () -> s.gameTime, v -> s.gameTime = v);
      minimap.lastTwoSideBySide();
      int sizes = 21;
      minimap.slider("size", (s.minimapSize - 64) / 8, sizes, i -> s.minimapSize = 64 + i * 8, i -> Component.literal(Integer.toString(64 + i * 8)));
      int enlargedSizes = 12;
      minimap.slider(
         "enlarged",
         (s.minimapEnlarged - 125) / 25,
         enlargedSizes,
         i -> s.minimapEnlarged = 125 + i * 25,
         i -> Component.literal(BigDecimal.valueOf(125 + i * 25, 2).stripTrailingZeros().toPlainString() + "×")
      );
      minimap.lastTwoSideBySide();
      minimap.slider("zoom", s.zoomStep(), Settings.ZOOMS.length, s::zoomStep, i -> Component.literal(Settings.ZOOMS[i] + "×"));
      int opacities = 17;
      minimap.slider(
         "opacity", (Settings.snapOpacity(s.minimapOpacity) - 20) / 5, opacities, i -> s.minimapOpacity = 20 + i * 5, i -> Component.literal(20 + i * 5 + "%")
      );
      int[] textSizes = TextSize.sizes();
      minimap.slider(
         "text_size",
         nearestIndex(textSizes, s.minimapTextSize),
         textSizes.length,
         i -> s.minimapTextSize = textSizes[i],
         i -> Component.literal(textSizes[i] + "%")
      );
      minimap.lastThreeSideBySide();
      minimap.choice("shape", Settings.Shape.values(), () -> s.minimapShape, v -> s.minimapShape = v);
      minimap.choice("frame", Settings.Frame.values(), () -> s.minimapFrame, v -> s.minimapFrame = v);
      minimap.lastTwoSideBySide();
      SettingsScreen.SettingsTab radar = new SettingsScreen.SettingsTab("diorama.settings.radar");
      Boolean radarSet = ServerRules.radarAllowed() ? null : false;
      radar.toggle("radar_minimap", () -> s.radarOnMinimap, v -> s.radarOnMinimap = v, radarSet);
      radar.toggle("radar_map", () -> s.radarOnMap, v -> s.radarOnMap = v, radarSet);
      radar.toggle("players", () -> s.radarPlayers, v -> s.radarPlayers = v);
      radar.toggle("hostile", () -> s.radarHostile, v -> s.radarHostile = v);
      radar.toggle("neutral", () -> s.radarNeutral, v -> s.radarNeutral = v);
      radar.toggle("passive", () -> s.radarPassive, v -> s.radarPassive = v);
      radar.toggle("items", () -> s.radarItems, v -> s.radarItems = v);
      radar.toggle("type_outline", () -> s.radarTypeOutline, v -> s.radarTypeOutline = v);
      radar.toggle("height_arrows", () -> s.radarHeightArrows, v -> s.radarHeightArrows = v);
      radar.choice("radar_style", Settings.RadarStyle.values(), () -> s.radarStyle, v -> s.radarStyle = v);
      int scales = 7;
      radar.slider("icon_size", (s.radarIconScale - 50) / 25, scales, i -> s.radarIconScale = 50 + i * 25, i -> Component.literal(50 + i * 25 + "%"));
      radar.lastTwoSideBySide();
      radar.choice("player_names", Settings.NameTags.values(), () -> s.playerNames, v -> s.playerNames = v);
      int[] ranges = Settings.RANGES;
      radar.slider("range", indexOf(ranges, s.radarRange), ranges.length, i -> s.radarRange = ranges[i], i -> Component.literal(ranges[i] + " m"));
      radar.open("radar_categories", RadarCategoriesScreen.summary(), () -> this.minecraft.setScreen(new RadarCategoriesScreen(this)));
      radar.lastTwoSideBySide();
      SettingsScreen.SettingsTab overlays = new SettingsScreen.SettingsTab("diorama.settings.overlays");
      overlays.toggle("chunk_grid", () -> s.chunkGrid, v -> s.chunkGrid = v);
      overlays.toggle("slime_chunks", () -> s.slimeChunks, v -> s.slimeChunks = v);
      overlays.toggle("map_maps_button", () -> s.mapMapsButton, v -> s.mapMapsButton = v);
      overlays.toggle("map_dimension_button", () -> s.mapDimensionButton, v -> s.mapDimensionButton = v);
      overlays.toggle("map_waypoints_button", () -> s.mapWaypointsButton, v -> s.mapWaypointsButton = v);
      overlays.toggle("map_settings_button", () -> s.mapSettingsButton, v -> s.mapSettingsButton = v);
      Settings.SpawnSpots[] spots = Settings.SpawnSpots.values();
      overlays.cycle(
         "spawn_spots",
         () -> s.spawnSpots.ordinal(),
         spots.length,
         i -> s.spawnSpots = spots[i],
         i -> Component.translatable("diorama.settings.spawn_spots." + spots[i].name().toLowerCase(Locale.ROOT))
      );
      if (Claims.supported()) {
         overlays.choice("claims", Settings.ClaimsShown.values(), () -> s.claims, v -> s.claims = v);
      }

      overlays.choice("drawings", Settings.DrawingsShown.values(), () -> s.drawings, v -> s.drawings = v);
      overlays.choice("web_map", Settings.WebMap.values(), () -> s.webMap, v -> s.webMap = v);
      overlays.lastTwoSideBySide();
      overlays.choice("world_border", Settings.WorldBorderShown.values(), () -> s.worldBorder, v -> s.worldBorder = v);
      SettingsScreen.SettingsTab waypoints = new SettingsScreen.SettingsTab("diorama.settings.waypoints");
      waypoints.toggle("waypoints_map", () -> s.waypointsOnMap, v -> s.waypointsOnMap = v);
      waypoints.toggle("waypoints_world", () -> s.waypointsInWorld, v -> s.waypointsInWorld = v);
      waypoints.toggle("deaths", () -> s.deathMarkers, v -> s.deathMarkers = v);
      waypoints.toggle("deaths_reached", () -> s.removeReachedDeaths, v -> s.removeReachedDeaths = v);
      waypoints.toggle("beds", () -> s.bedMarkers, v -> s.bedMarkers = v);
      waypoints.toggle("portals", () -> s.portalMarkers, v -> s.portalMarkers = v);
      waypoints.toggle("map_add_waypoints", () -> s.mapAddWaypoints, v -> s.mapAddWaypoints = v);
      waypoints.choice("waypoint_labels", Settings.WaypointLabels.values(), () -> s.waypointLabels, v -> s.waypointLabels = v);
      int[] distances = Settings.WAYPOINT_DISTANCES;
      waypoints.slider(
         "waypoint_distance",
         indexOf(distances, s.waypointDistance),
         distances.length,
         i -> s.waypointDistance = distances[i],
         i -> distances[i] == 0 ? Component.translatable("diorama.settings.waypoint_distance.any") : Component.literal(distances[i] + " m")
      );
      waypoints.choice("waypoint_beams", Settings.Beams.values(), () -> s.waypointBeams, v -> s.waypointBeams = v);
      waypoints.lastTwoSideBySide();
      int[] pinSizes = Pins.sizes();
      waypoints.heading("waypoint_size");
      waypoints.slider(
         "waypoint_size_minimap",
         nearestIndex(pinSizes, s.waypointMinimapSize),
         pinSizes.length,
         i -> s.waypointMinimapSize = pinSizes[i],
         i -> Component.literal(pinSizes[i] + "%")
      );
      waypoints.slider(
         "waypoint_size_map",
         nearestIndex(pinSizes, s.waypointMapSize),
         pinSizes.length,
         i -> s.waypointMapSize = pinSizes[i],
         i -> Component.literal(pinSizes[i] + "%")
      );
      waypoints.slider(
         "waypoint_size_world",
         nearestIndex(pinSizes, s.waypointWorldSize),
         pinSizes.length,
         i -> s.waypointWorldSize = pinSizes[i],
         i -> Component.literal(pinSizes[i] + "%")
      );
      waypoints.lastThreeSideBySide();
      waypoints.open("teleport_command", Component.literal("/" + s.teleportCommand), () -> this.minecraft.setScreen(new TeleportCommandScreen(this)));
      SettingsScreen.SettingsTab look = new SettingsScreen.SettingsTab("diorama.settings.look");
      look.toggle("outlines", () -> s.outlines, v -> s.outlines = v);
      look.choice("day_night", Settings.DayNight.values(), () -> s.dayNight, v -> s.dayNight = v);
      look.toggle("rounded_fog", () -> s.roundedFog, v -> s.roundedFog = v);
      look.toggle("fog_of_war", () -> s.alwaysFogOfWar, v -> s.alwaysFogOfWar = v, ServerRules.fogOfWarForced() ? true : null);
      look.toggle("saved_chunks", () -> s.mapSavedChunks, v -> s.mapSavedChunks = v);
      look.open("map_off", DimensionsScreen.summary(), () -> this.minecraft.setScreen(new DimensionsScreen(this)));
      Settings.Quality[] qualities = Settings.Quality.values();
      look.cycle(
         "quality",
         () -> s.quality.ordinal(),
         qualities.length,
         i -> s.quality = qualities[i],
         i -> Component.translatable("diorama.settings.quality." + qualities[i].name().toLowerCase(Locale.ROOT))
      );
      this.gpuBaking = look.cycle("gpu_baking", () -> s.gpuBaking ? 1 : 0, 2, i -> {
         s.gpuBaking = i == 1;
         if (s.gpuBaking) {
            GpuBakeCheck.retry();
            this.gpuFailedShown = GpuBakeCheck.failed();
         }
      }, SettingsScreen::gpuBakingValue);
      this.gpuFailedShown = GpuBakeCheck.failed();
      look.cycle(
         "arrow_color",
         () -> s.arrowColor,
         Settings.ARROW_COLORS.length,
         i -> s.arrowColor = i,
         i -> Component.translatable("diorama.settings.arrow_color." + Settings.ARROW_COLOR_NAMES[i]).withColor(Settings.ARROW_COLORS[i])
      );
      TabNavigationBar bar = TabNavigationBar.builder(this.tabManager, this.side).addTabs(new Tab[]{minimap, radar, overlays, waypoints, look}).build();
      bar.setWidth(this.side);
      bar.arrangeElements();
      this.tabs = List.of(minimap, radar, overlays, waypoints, look);
      this.tabBar = (TabNavigationBar)this.addWidget(bar);
      this.addRenderableWidget(
         Button.builder(Component.translatable("gui.done"), b -> this.onClose()).bounds(this.side / 2 - 100, this.height - 26, 200, 20).build()
      );
      this.tabManager.setTabArea(new ScreenRectangle(0, 24, this.side, this.listBottom() - 24));
      bar.selectTab(selected, false);
   }

   private static Component gpuBakingValue(int on) {
      GpuBakeCheck.Off off = GpuBakeCheck.off();
      String var10000;
      if (on == 0) {
         var10000 = "options.off";
      } else if (off == null) {
         var10000 = "options.on";
      } else {
         switch (off) {
            case NO_MATCH:
               var10000 = "diorama.settings.gpu_baking.no_match";
               break;
            case TOO_SLOW:
               var10000 = "diorama.settings.gpu_baking.too_slow";
               break;
            case CRASHED:
               var10000 = "diorama.settings.gpu_baking.crashed";
               break;
            default:
               throw new MatchException(null, null);
         }
      }

      return Component.translatable(var10000);
   }

   public void tick() {
      super.tick();
      boolean failed = GpuBakeCheck.failed();
      if (this.gpuBaking != null && failed != this.gpuFailedShown) {
         this.gpuFailedShown = failed;
         this.gpuBaking.setMessage(label(Component.translatable("diorama.settings.gpu_baking"), gpuBakingValue(Settings.get().gpuBaking ? 1 : 0)));
      }
   }

   private int listBottom() {
      return this.height - 33;
   }

   public void selectTab(int index) {
      if (this.tabBar != null) {
         this.tabBar.selectTab(index, false);
      }
   }

   static Checkbox checkbox(Component label, Font font, boolean selected, int maxWidth, Consumer<Boolean> set) {
      return Checkbox.builder(label, font).selected(selected).maxWidth(maxWidth).onValueChange((box, value) -> {
         set.accept(value);
         Settings.get().save();
      }).build();
   }

   static Component serverSetLine(boolean note) {
      boolean file = Minecraft.getInstance().hasSingleplayerServer();
      return Component.translatable("diorama.settings." + (note ? "server_note" : "server_set") + (file ? "_file" : "")).withStyle(ChatFormatting.GRAY);
   }

   private static Tooltip tooltip(String key) {
      return Tooltip.create(Component.translatable("diorama.settings." + key + ".tooltip"));
   }

   static Component label(Component name, Component value) {
      return Component.empty().append(name).append(": ").append(value);
   }

   private static int nearestIndex(int[] values, int value) {
      int nearest = 0;

      for (int i = 1; i < values.length; i++) {
         if (Math.abs(values[i] - value) <= Math.abs(values[nearest] - value)) {
            nearest = i;
         }
      }

      return nearest;
   }

   private static int indexOf(int[] values, int value) {
      for (int i = 0; i < values.length; i++) {
         if (values[i] == value) {
            return i;
         }
      }

      return 1;
   }

   public void renderBackground(GuiGraphics graphics, int mouseX, int mouseY, float a) {
      super.renderBackground(graphics, mouseX, mouseY, a);
      graphics.enableScissor(this.side, 0, this.width, this.height);
      this.preview.preview(graphics, this.minecraft.getTimer().getGameTimeDeltaPartialTick(true));
      graphics.disableScissor();
      RenderSystem.enableBlend();
      graphics.blit(LIST_BACKGROUND, 0, 24, 0.0F, 0.0F, this.side, this.listBottom() - 24, 32, 32);
      graphics.blit(Screen.INWORLD_FOOTER_SEPARATOR, 0, this.listBottom(), 0.0F, 0.0F, this.side, 2, 32, 2);
      RenderSystem.disableBlend();
   }

   public void render(GuiGraphics graphics, int mouseX, int mouseY, float a) {
      super.render(graphics, mouseX, mouseY, a);
      if (this.tabBar != null) {
         graphics.enableScissor(0, 0, this.side, 24);
         this.tabBar.render(graphics, mouseX, mouseY, a);
         graphics.disableScissor();
      }
   }

   public boolean keyPressed(int key, int scanCode, int modifiers) {
      return this.tabBar != null && this.tabBar.keyPressed(key) || super.keyPressed(key, scanCode, modifiers);
   }

   public void removed() {
      this.preview.close();
   }

   public void onClose() {
      this.minecraft.setScreen(this.parent);
   }

   public boolean isPauseScreen() {
      return false;
   }

   private final class SettingsTab extends GridLayoutTab {
      private final List<AbstractWidget> checkboxes = new ArrayList<>();
      private final List<AbstractWidget> wide = new ArrayList<>();
      private final Set<AbstractWidget> pairedWithNext = new HashSet<>();
      private final Set<AbstractWidget> tripled = new HashSet<>();
      private final Set<AbstractWidget> headings = new HashSet<>();
      private boolean serverNoted;

      SettingsTab(String key) {
         super(Component.translatable(key));
      }

      void toggle(String key, BooleanSupplier get, Consumer<Boolean> set) {
         this.toggle(key, get, set, null);
      }

      void toggle(String key, BooleanSupplier get, Consumer<Boolean> set, @Nullable Boolean serverSet) {
         int column = 136;
         boolean shown = serverSet != null ? serverSet : get.getAsBoolean();
         Checkbox checkbox = SettingsScreen.checkbox(Component.translatable("diorama.settings." + key), SettingsScreen.this.font, shown, column, set);
         checkbox.setTooltip(
            serverSet == null
               ? SettingsScreen.tooltip(key)
               : Tooltip.create(
                  Component.empty()
                     .append(SettingsScreen.serverSetLine(false))
                     .append("\n\n")
                     .append(Component.translatable("diorama.settings." + key + ".tooltip"))
               )
         );
         if (serverSet != null) {
            checkbox.active = false;
            checkbox.setAlpha(0.5F);
            if (!this.serverNoted) {
               this.serverNoted = true;
               StringWidget note = new StringWidget(SettingsScreen.serverSetLine(true), SettingsScreen.this.font);
               this.headings.add(note);
               this.wide.add(0, note);
            }
         }

         this.checkboxes.add(checkbox);
      }

      Button cycle(String key, IntSupplier get, int count, IntConsumer set, IntFunction<Component> name) {
         Component label = Component.translatable("diorama.settings." + key);
         Button button = Button.builder(SettingsScreen.label(label, name.apply(get.getAsInt())), b -> {
            int next = (get.getAsInt() + 1) % count;
            set.accept(next);
            Settings.get().save();
            b.setMessage(SettingsScreen.label(label, name.apply(next)));
         }).size(280, 20).tooltip(SettingsScreen.tooltip(key)).build();
         this.wide.add(button);
         return button;
      }

      <E extends Enum<E>> Button choice(String key, E[] values, Supplier<E> get, Consumer<E> set) {
         return this.cycle(
            key,
            () -> get.get().ordinal(),
            values.length,
            i -> set.accept(values[i]),
            i -> Component.translatable("diorama.settings." + key + "." + values[i].name().toLowerCase(Locale.ROOT))
         );
      }

      void open(String key, Component value, Runnable press) {
         Component label = Component.translatable("diorama.settings." + key);
         this.wide.add(Button.builder(SettingsScreen.label(label, value), b -> press.run()).size(280, 20).tooltip(SettingsScreen.tooltip(key)).build());
      }

      void slider(String key, int index, int count, IntConsumer set, IntFunction<Component> name) {
         SettingsScreen.StepSlider slider = new SettingsScreen.StepSlider(Component.translatable("diorama.settings." + key), index, count, set, name);
         slider.setTooltip(SettingsScreen.tooltip(key));
         this.wide.add(slider);
      }

      void lastTwoSideBySide() {
         this.pairedWithNext.add(this.wide.get(this.wide.size() - 2));
      }

      void lastThreeSideBySide() {
         this.tripled.add(this.wide.get(this.wide.size() - 3));
      }

      void heading(String key) {
         StringWidget heading = new StringWidget(Component.translatable("diorama.settings." + key), SettingsScreen.this.font);
         this.headings.add(heading);
         this.wide.add(heading);
      }

      public void visitChildren(Consumer<AbstractWidget> consumer) {
         this.checkboxes.forEach(consumer);
         this.wide.forEach(consumer);
      }

      public void doLayout(ScreenRectangle area) {
         int content = Math.min(280, area.width() - 10);
         int column = (content - 8) / 2;
         int left = area.left() + (area.width() - content) / 2;
         int rows = this.wide.size() - this.pairedWithNext.size() - this.tripled.size() * 2 - this.headings.size();
         int checkRows = (this.checkboxes.size() + 1) / 2;
         boolean tight = 22 + checkRows * 22 + rows * 24 + this.headings.size() * 11 > area.height();
         int rowStep = tight ? 22 : 24;
         int y = area.top() + (tight ? 6 : 12);

         for (int i = 0; i < this.checkboxes.size(); i++) {
            this.checkboxes.get(i).setPosition(left + i % 2 * (column + 8), y + i / 2 * (tight ? 20 : 22));
         }

         if (!this.checkboxes.isEmpty()) {
            y += checkRows * (tight ? 20 : 22) + (tight ? 3 : 10);
         }

         for (int i = 0; i < this.wide.size(); i++) {
            AbstractWidget widget = this.wide.get(i);
            if (this.headings.contains(widget)) {
               widget.setPosition(left, y);
               widget.setWidth(content);
               y += 11;
            } else {
               if (this.tripled.contains(widget)) {
                  int third = (content - 8) / 3;

                  for (int j = 0; j < 3; j++) {
                     AbstractWidget part = this.wide.get(i + j);
                     part.setPosition(left + j * (content - third) / 2, y);
                     part.setWidth(third);
                  }

                  i += 2;
               } else if (this.pairedWithNext.contains(widget) && i + 1 < this.wide.size()) {
                  int half = (content - 4) / 2;
                  widget.setPosition(left, y);
                  widget.setWidth(half);
                  AbstractWidget next = this.wide.get(++i);
                  next.setPosition(left + content - half, y);
                  next.setWidth(half);
               } else {
                  widget.setPosition(left, y);
                  widget.setWidth(content);
               }

               y += rowStep;
            }
         }
      }
   }

   static final class StepSlider extends AbstractSliderButton {
      private final Component name;
      private final int count;
      private final IntConsumer set;
      private final IntFunction<Component> valueName;
      private int index;

      StepSlider(Component name, int index, int count, IntConsumer set, IntFunction<Component> valueName) {
         super(0, 0, 280, 20, Component.empty(), index <= 0 ? 0.0 : (index >= count - 1 ? 1.0 : (index + 0.5) / count));
         this.name = name;
         this.count = count;
         this.set = set;
         this.valueName = valueName;
         this.index = Math.max(0, Math.min(count - 1, index));
         this.updateMessage();
      }

      protected void updateMessage() {
         this.setMessage(SettingsScreen.label(this.name, this.valueName.apply(this.index)));
      }

      protected void applyValue() {
         int picked = Math.min(this.count - 1, (int)Math.floor(this.value * this.count));
         if (picked != this.index) {
            this.index = picked;
            this.set.accept(picked);
            Settings.get().save();
         }
      }
   }
}
