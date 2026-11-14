package dev.diorama;

import com.mojang.blaze3d.platform.InputConstants.Type;
import com.mojang.blaze3d.vertex.PoseStack;
import dev.diorama.bake.BlockLooks;
import dev.diorama.bake.MapPick;
import dev.diorama.bake.TileKey;
import dev.diorama.bake.TileSpots;
import dev.diorama.drawing.DrawMode;
import dev.diorama.drawing.DrawingStore;
import dev.diorama.render.MapLevel;
import dev.diorama.render.MapViewport;
import dev.diorama.render.Markers;
import dev.diorama.render.Pins;
import dev.diorama.render.Radar;
import dev.diorama.render.SavedChunkImport;
import dev.diorama.waypoint.Waypoint;
import dev.diorama.waypoint.WaypointEditScreen;
import dev.diorama.waypoint.WaypointListScreen;
import dev.diorama.waypoint.WaypointOverlay;
import dev.diorama.waypoint.WaypointShare;
import dev.diorama.waypoint.WaypointStore;
import dev.diorama.waypoint.Waypoints;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.tooltip.MenuTooltipPositioner;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.Nullable;

public final class MapScreen extends Screen {
   private static final double NETHER_SCALE = 8.0;
   private static final double MIN_SCREEN_ZOOM = 0.001953125;
   private static final long FOLLOW_HOLD_MS = 3000L;
   private static final double MAX_ZOOM = 16.0;
   private static final int DIMENSION_BUTTON_X = 4;
   private static final int BAR = 16;
   private static final int BOTTOM_BAR = 14;
   private static final Component HELP = Component.translatable("diorama.map.help");
   private static final String STATUS_GAP = "   ·   ";
   private static final int ZOOM_BUTTON_STEPS = 2;
   private static final ResourceLocation TAB = ResourceLocation.withDefaultNamespace("widget/tab");
   private static final ResourceLocation TAB_HIGHLIGHTED = ResourceLocation.withDefaultNamespace("widget/tab_highlighted");
   private static final ResourceLocation TAB_SELECTED = ResourceLocation.withDefaultNamespace("widget/tab_selected");
   private static final ResourceLocation TAB_SELECTED_HIGHLIGHTED = ResourceLocation.withDefaultNamespace("widget/tab_selected_highlighted");
   private static final Component[] TAB_NAMES = new Component[]{Component.translatable("diorama.map.surface"), Component.translatable("diorama.map.caves")};
   private final MapViewport viewport = new MapViewport();
   private double centerU;
   private double centerV;
   private double zoom = 2.0;
   private int layer;
   private boolean layerPicked;
   private long followHoldStart = -1L;
   private boolean following = true;
   private List<Radar.Blip> blips = List.of();
   private @Nullable List<FormattedCharSequence> tooltipLines;
   private List<WaypointOverlay.Pin> pins = List.of();
   private MapViewport.@Nullable View lastView;
   private double pressX;
   private double pressY;
   private boolean dragged;
   private @Nullable MapMenu menu;
   private boolean pressOnMenu;
   private int pressButton = -1;
   private boolean pressOnControl;
   private final DepthGauge gauge = new DepthGauge();
   private Button export;
   private Button zoomIn;
   private Button zoomOut;
   private Button recenter;
   private Button sliceUp;
   private Button sliceDown;
   private Button dimensionButton;
   private boolean severalDimensions;
   private Button mapsButton;
   private Button waypointsButton;
   private Button settingsButton;
   private Button drawButton;
   private final DrawMode draw = new DrawMode();
   private final WorldBorderOverlay worldBorder = new WorldBorderOverlay();
   private final int[] tabX = new int[2];
   private final int[] tabWidth = new int[2];
   private int tabsEnd;
   private int spotU = Integer.MIN_VALUE;
   private int spotV;
   private int spotLayer;
   private int spotAge;
   private @Nullable BlockPos spot;
   private @Nullable Object spotRequest;
   private @Nullable String otherDimension;
   private MapScreen.@Nullable OtherMap otherMap;
   private Button facingButton;
   private static final int REFRESH_TICKS = 1200;
   private int refreshIn = 1200;
   private static final long SPOT_WAIT_MS = 50L;
   private static final int USUAL_GROUND_Y = 64;
   private static final String[] OVERWORLD_ICON = new String[]{
      "....###....", "..#######..", ".#########.", ".#########.", "..#######..", "....###....", ".....#.....", ".....#.....", "....###...."
   };
   private static final String[] NETHER_ICON = new String[]{
      ".....#.....", "....##.....", "....###.#..", "...#####...", "..#######..", ".####.####.", ".###...###.", "..##...##..", "...#####..."
   };
   private static final String[] END_ICON = new String[]{
      "...#####...", ".##.....##.", "#...###...#", "#..#####..#", "#...###...#", ".##.....##.", "...#####..."
   };
   private static final String[] PICTURE = new String[]{
      "###########", "#.........#", "#......##.#", "#......##.#", "#...#.....#", "#..#.#....#", "#.#...#.#.#", "##.....#.##", "###########"
   };
   private static boolean pausesForTests;

   public MapScreen() {
      super(Component.translatable("diorama.screen.title"));
      this.layer = this.layerOnOpening();
      this.refreshShared();
   }

   private void refreshShared() {
      SavedChunkImport fill = MapSession.get() == null ? null : MapSession.get().savedChunks();
      if (fill != null) {
         fill.refresh();
      }
   }

   private int layerOnOpening() {
      MapSession session = MapSession.get();
      Entity viewer = MapSession.viewer();
      return session != null && viewer != null && Settings.get().autoCaves && ServerRules.caveMapsAllowed() ? session.autoLayer(viewer) : Integer.MIN_VALUE;
   }

   protected void init() {
      this.menu = null;
      this.gauge.cancel();
      this.pressButton = -1;
      this.pressOnMenu = false;
      this.pressOnControl = false;
      Settings settings = Settings.get();
      this.dimensionButton = (Button)this.addRenderableWidget(
         Button.builder(Component.empty(), b -> this.nextDimension()).size(20, 20).createNarration(n -> this.dimensionName().copy()).build()
      );
      this.severalDimensions = this.dimensions().size() > 1;
      this.mapsButton = (Button)this.addRenderableWidget(Button.builder(Component.translatable("diorama.map.maps"), b -> {
         MapsScreen maps = new MapsScreen(this);
         if (this.otherDimension != null) {
            maps.show(this.otherDimension, null);
         }

         this.minecraft.setScreen(maps);
      }).size(64, 16).build());
      this.mapsButton.visible = settings.mapMapsButton;
      this.mapsButton.active = Maps.get() != null;
      this.waypointsButton = (Button)this.addRenderableWidget(
         Button.builder(Component.translatable("diorama.map.waypoints"), b -> this.openWaypoints()).size(64, 16).build()
      );
      this.waypointsButton.visible = settings.mapWaypointsButton;
      this.settingsButton = (Button)this.addRenderableWidget(
         Button.builder(Component.translatable("diorama.settings.button"), b -> this.minecraft.setScreen(new SettingsScreen(this))).size(64, 16).build()
      );
      this.settingsButton.visible = settings.mapSettingsButton;
      int right = this.width;

      for (Button button : List.of(this.settingsButton, this.waypointsButton, this.mapsButton)) {
         if (button.visible) {
            right -= button.getWidth();
            button.setPosition(right, 0);
            right -= 2;
         }
      }

      this.export = (Button)this.addRenderableWidget(Button.builder(Component.empty(), b -> {
         this.menu = null;
         this.minecraft.setScreen(new ExportScreen(this, this.centerU, this.centerV, this.zoom, this.layer, this.width, this.height));
      }).size(20, 20).createNarration(n -> Component.translatable("diorama.export.title")).build());
      this.zoomIn = (Button)this.addRenderableWidget(
         Button.builder(Component.literal("+"), b -> this.zoomButton(2))
            .size(20, 20)
            .createNarration(n -> Component.translatable("diorama.map.zoom_in"))
            .build()
      );
      this.zoomOut = (Button)this.addRenderableWidget(
         Button.builder(Component.literal("-"), b -> this.zoomButton(-2))
            .size(20, 20)
            .createNarration(n -> Component.translatable("diorama.map.zoom_out"))
            .build()
      );
      this.recenter = (Button)this.addRenderableWidget(Button.builder(Component.empty(), b -> {
         this.menu = null;
         this.following = true;
      }).size(20, 20).createNarration(n -> Component.translatable("diorama.map.recenter")).build());
      this.facingButton = (Button)this.addRenderableWidget(
         Button.builder(facingLetter(), b -> this.turn(DrawMode.shiftDown() ? -1 : 1))
            .size(20, 20)
            .createNarration(n -> Component.translatable("diorama.map.facing." + Projection.facing().id()))
            .build()
      );
      this.sliceUp = (Button)this.addRenderableWidget(Button.builder(Component.empty(), b -> {
         this.menu = null;
         this.shiftSlice(4, MapSession.viewer());
      }).size(18, 14).createNarration(n -> Component.translatable("diorama.map.slice_up")).build());
      this.sliceDown = (Button)this.addRenderableWidget(Button.builder(Component.empty(), b -> {
         this.menu = null;
         this.shiftSlice(-4, MapSession.viewer());
      }).size(18, 14).createNarration(n -> Component.translatable("diorama.map.slice_down")).build());
      this.drawButton = (Button)this.addRenderableWidget(Button.builder(Component.empty(), b -> {
         this.menu = null;
         this.draw.toggle();
      }).size(20, 20).createNarration(n -> Component.translatable("diorama.draw.button")).build());
      this.draw
         .init(
            this.font,
            x$0 -> {
               AbstractWidget var10000 = (AbstractWidget)this.addRenderableWidget(x$0);
            },
            this::setFocused,
            (x, y) -> this.spotAt(x, y, false).thenApply(MapLevel.Spot::pos),
            (u, v) -> this.spotAtMap(u, v, false).thenApply(MapLevel.Spot::pos),
            this::dimension
         );
      this.placeControls();
   }

   private List<String> dimensions() {
      Maps maps = Maps.get();
      ClientPacketListener connection = this.minecraft.getConnection();
      if (maps != null && connection != null && this.minecraft.level != null) {
         List<String> saved = maps.saved(
               this.minecraft.level.dimension().location().toString(), connection.levels().stream().map(level -> level.location().toString()).toList()
            )
            .stream()
            .filter(dimension -> !ServerRules.mapOff(dimension))
            .toList();
         if (this.otherDimension != null && !saved.contains(this.otherDimension)) {
            List<String> withShown = new ArrayList<>(saved);
            withShown.add(this.otherDimension);
            return withShown;
         } else {
            return saved;
         }
      } else {
         return List.of();
      }
   }

   private void nextDimension() {
      List<String> dimensions = this.dimensions();
      if (dimensions.size() >= 2 && this.minecraft.level != null) {
         int step = DrawMode.shiftDown() ? -1 : 1;
         String next = dimensions.get(Math.floorMod(dimensions.indexOf(this.dimension()) + step, dimensions.size()));
         this.draw.leaveDimension();
         this.closeOtherMap();
         this.otherDimension = next.equals(this.minecraft.level.dimension().location().toString()) ? null : next;
         this.viewChanged();
      }
   }

   private void viewChanged() {
      this.menu = null;
      this.gauge.cancel();
      this.following = true;
      this.layerPicked = false;
      this.followHoldStart = -1L;
      this.layer = this.otherDimension == null ? this.layerOnOpening() : Integer.MIN_VALUE;
      this.spotU = Integer.MIN_VALUE;
      this.spot = null;
      this.spotRequest = null;
   }

   private MapLevel shownMap(MapSession session) {
      String other = this.otherDimension;
      Maps maps = Maps.get();
      if (other == null) {
         return session.map();
      }

      if (maps != null && this.minecraft.level != null && !other.equals(this.minecraft.level.dimension().location().toString()) && !ServerRules.mapOff(other)) {
         Path folder = maps.tileFolder(other, maps.active(other));
         if (this.otherMap == null
            || !this.otherMap.folder().equals(folder)
            || this.otherMap.style() != MapStyle.get()
            || this.otherMap.facing() != Projection.facing()) {
            this.closeOtherMap();
            this.otherMap = new MapScreen.OtherMap(folder, MapStyle.get(), Projection.facing(), MapLevel.saved(folder));
         }

         this.otherMap.map().setFogOfWar(ServerRules.fogOfWar(other.equals("minecraft:the_nether"), this.otherMap.map().roofless()));
         return this.otherMap.map();
      } else {
         this.draw.leaveDimension();
         this.closeOtherMap();
         this.otherDimension = null;
         this.viewChanged();
         return session.map();
      }
   }

   private void closeOtherMap() {
      if (this.otherMap != null) {
         this.otherMap.map().close();
         this.otherMap = null;
      }
   }

   private Component dimensionName() {
      return Waypoints.dimensionName(this.dimension());
   }

   private double scaleToShown() {
      String here = this.minecraft.level == null ? "" : this.minecraft.level.dimension().location().toString();
      return dimensionScale(here) / dimensionScale(this.dimension());
   }

   public static double dimensionScale(String dimension) {
      return dimension.equals("minecraft:the_nether") ? 8.0 : 1.0;
   }

   public void tick() {
      super.tick();
      if (--this.refreshIn <= 0) {
         this.refreshIn = 1200;
         this.refreshShared();
      }

      if (this.otherMap != null) {
         this.otherMap.map().update();
      }
   }

   private static Component facingLetter() {
      return Component.translatable("diorama.map.facing_letter." + Projection.facing().id());
   }

   public void turn(int steps) {
      Maps maps = Maps.get();
      MapSession session = MapSession.get();
      if (maps != null && session != null) {
         this.menu = null;
         Facing was = Projection.facing();
         Facing now = was.next(steps);
         if (maps.facing(now)) {
            double y = this.layer == Integer.MIN_VALUE ? 64.0 : this.layer;
            double z = Projection.zAt(this.centerV, y);
            double worldX = was.worldX(this.centerU, z);
            double worldZ = was.worldZ(this.centerU, z);
            Projection.setFacing(now);
            this.closeOtherMap();
            MapSession.facingChanged();
            this.spotU = Integer.MIN_VALUE;
            this.spot = null;
            this.spotRequest = null;
            this.centerU = Projection.u(worldX, worldZ);
            this.centerV = Projection.v(worldX, y, worldZ);
            this.facingButton.setMessage(facingLetter());
         }
      }
   }

   private void placeControls() {
      boolean caves = this.layer != Integer.MIN_VALUE;
      int x = this.width - (caves ? 56 : 26);
      int y = this.height - 90;
      this.drawButton.setPosition(x, y - 26);
      MapSession session = MapSession.get();
      this.export.visible = !this.draw.active() && this.otherDimension == null && session != null && (caves || session.map().savedMap() != null);
      this.export.setPosition(x, y - 48);
      this.zoomIn.setPosition(x, y);
      this.zoomOut.setPosition(x, y + 22);
      this.recenter.setPosition(x, y + 48);
      this.facingButton.setPosition(x - 22, y + 48);
      this.facingButton.visible = !this.draw.active();
      this.dimensionButton.visible = this.severalDimensions && Settings.get().mapDimensionButton && !this.draw.active();
      this.dimensionButton.setPosition(4, y + 48);
      this.zoomIn.active = this.zoom < 16.0;
      this.zoomOut.active = this.zoom > this.minZoom();
      this.sliceUp.visible = caves;
      this.sliceDown.visible = caves;
      ClientLevel level = this.minecraft.level;
      if (caves && level != null) {
         this.sliceUp.active = this.layer < level.getMaxBuildHeight() - 1 + 1;
         this.sliceDown.active = this.layer > level.getMinBuildHeight() + 4;
      }
   }

   public void renderBackground(GuiGraphics graphics, int mouseX, int mouseY, float a) {
   }

   public void render(GuiGraphics graphics, int mouseX, int mouseY, float a) {
      this.tooltipLines = null;
      if (!ServerRules.minimapAllowed()) {
         this.onClose();
      } else {
         this.mapsButton.active = Maps.get() != null;
         MapSession session = MapSession.get();
         Entity viewer = MapSession.viewer();
         if (session != null && viewer != null) {
            MapLevel shown = this.shownMap(session);
            boolean elsewhere = this.otherDimension != null;
            Vec3 pos = viewer.getPosition(a);
            double scale = this.scaleToShown();
            double playerU = Projection.u(pos.x * scale, pos.z * scale);
            double playerV = Projection.v(pos.x * scale, pos.y, pos.z * scale);
            if (this.following) {
               this.centerU = playerU;
               this.centerV = playerV;
            }

            this.followLayer(session, viewer);
            this.zoom = Math.max(this.minZoom(), this.zoom);
            MapViewport.View view = new MapViewport.View(0, 0, this.width, this.height, this.centerU, this.centerV, this.zoom, this.layer);
            this.lastView = view;
            if (this.draw.holding() && this.pressButton != 0) {
               this.draw.follow(view, this.font, this.dimension(), mouseX, mouseY);
            }

            this.viewport
               .draw(
                  graphics,
                  shown,
                  view,
                  elsewhere ? 0 : overlayBits(session),
                  !elsewhere && Settings.get().claims != Settings.ClaimsShown.OFF,
                  elsewhere ? null : () -> session.lights().draw(graphics, view, a, 1.0F, ServerRules.radarAllowed() && Settings.get().radarOnMap)
               );
            if (!elsewhere) {
               session.crafts().draw(graphics, view, a, 1.0F);
            }

            if (!elsewhere && Settings.get().worldBorder != Settings.WorldBorderShown.OFF) {
               this.worldBorder.draw(graphics, this.font, view, this.minecraft.level, viewer);
            }

            DrawingStore drawings = DrawingStore.get();
            if (drawings != null && (this.draw.active() || Settings.get().drawings != Settings.DrawingsShown.OFF)) {
               this.draw.drawMap(graphics, this.font, view, drawings.in(this.dimension()), mouseX, mouseY, !this.overControls(mouseX, mouseY));
            }

            boolean caves = this.layer != Integer.MIN_VALUE;
            this.placeControls();
            Radar.Blip hovered = null;
            this.blips = List.of();
            if (!elsewhere && ServerRules.radarAllowed() && Settings.get().radarOnMap) {
               this.blips = Radar.collect(view, a, true);
               Radar.draw(graphics, view, this.blips, true, false);
               hovered = Radar.under(this.blips, mouseX, mouseY);
            }

            WaypointStore store = WaypointStore.get();
            this.pins = store != null && Settings.get().waypointsOnMap ? this.layoutPins(view, store.shownIn(this.dimension())) : List.of();
            WaypointOverlay.Pin hoveredPin = WaypointOverlay.under(this.pins, mouseX, mouseY);
            WaypointOverlay.draw(graphics, view, this.pins, true, hoveredPin);
            Radar.draw(graphics, view, this.blips, true, true);
            Markers.player(graphics, view.screenX(playerU), view.screenY(playerV), Markers.onMap(viewer.getViewYRot(a)), 6.0F);
            if (caves) {
               this.gauge.layout(this.minecraft.level, viewer.blockPosition(), this.layer, this.width, this.height);
               this.sliceUp.setPosition(this.gauge.upButtonX(), this.gauge.upButtonY());
               this.sliceDown.setPosition(this.gauge.upButtonX(), this.gauge.downButtonY());
               int avoidX = this.zoomIn.getX() - 3;
               int avoidTop = this.drawButton.getY() - 2;
               int avoidBottom = this.recenter.getY() + this.recenter.getHeight() + 2;
               graphics.drawManaged(() -> this.gauge.draw(graphics, this.font, pos.y, this.layer, avoidX, avoidTop, avoidBottom));
            } else {
               this.gauge.cancel();
            }

            if (this.menu == null) {
               if (!this.draw.active() || this.overControls(mouseX, mouseY)) {
                  if (this.overControls(mouseX, mouseY)) {
                     MapScreen.Tip tip = this.controlTooltip(mouseX, mouseY);
                     if (tip != null) {
                        this.setTooltipForNextRenderPass(tip.lines(), new MenuTooltipPositioner(tip.control()), false);
                     }
                  } else if (hovered != null && hovered.kind() == Radar.Kind.PLAYER) {
                     this.tooltipLines = withClaim(Radar.describe(hovered, viewer), hovered.entity().getBlockX(), hovered.entity().getBlockZ());
                  } else if (hoveredPin != null) {
                     Waypoint waypoint = hoveredPin.waypoint();
                     this.tooltipLines = elsewhere
                        ? List.of(Component.literal(waypoint.shownName()).getVisualOrderText())
                        : withClaim(WaypointOverlay.describe(waypoint, viewer), waypoint.x, waypoint.z);
                  } else if (hovered != null) {
                     this.tooltipLines = withClaim(Radar.describe(hovered, viewer), hovered.entity().getBlockX(), hovered.entity().getBlockZ());
                  } else if (this.pressButton == -1) {
                     this.tooltipLines = this.spotTooltip(mouseX, mouseY);
                  }
               }

               if (this.tooltipLines != null) {
                  this.setTooltipForNextRenderPass(this.tooltipLines);
               }
            }

            graphics.fill(0, 0, this.width, 16, -1879048192);
            this.drawTabs(graphics, mouseX, mouseY);
            int statusX = this.tabsEnd + 8;
            graphics.drawString(this.font, this.statusText(shown, viewer, caves, this.buttonsStart() - 2 - statusX), statusX, 4, -1, false);
            graphics.fill(0, this.height - 14, this.width, this.height, -1879048192);
            String help = (this.draw.active() ? this.draw.help() : HELP).getString();
            graphics.drawString(this.font, help, 6, this.height - 11, -3616550, false);
            this.drawMapName(graphics, 6 + this.font.width(help) + 12);
            super.render(graphics, mouseX, mouseY, a);
            Markers.player(graphics, this.recenter.getX() + 10, this.recenter.getY() + 10.5F, 180.0F, 5.0F);
            DrawMode.icon(graphics, DrawMode.PENCIL_ICON, this.drawButton.getX(), this.drawButton.getY(), -1);
            if (this.draw.active()) {
               DrawMode.outline(graphics, this.drawButton.getX(), this.drawButton.getY(), 20, 20, -1);
            }

            this.draw.drawControls(graphics);
            if (this.export.visible) {
               DrawMode.icon(graphics, PICTURE, this.export.getX(), this.export.getY(), -1);
            }

            if (this.dimensionButton.visible) {
               this.drawDimensionIcon(graphics);
            }

            if (caves) {
               this.caret(graphics, this.sliceUp, false);
               this.caret(graphics, this.sliceDown, true);
            }

            if (this.menu != null) {
               graphics.flush();
               PoseStack pose = graphics.pose();
               pose.pushPose();
               pose.translate(0.0F, 0.0F, 400.0F);
               this.menu.draw(graphics, mouseX, mouseY);
               pose.popPose();
            }
         }
      }
   }

   private String statusText(MapLevel shown, Entity viewer, boolean caves, int room) {
      boolean elsewhere = this.otherDimension != null;
      String roof = !caves && shown.roofless() ? Component.translatable("diorama.map.under_roof").getString() : null;
      String position = elsewhere
         ? Component.translatable("diorama.map.you_are_in", new Object[]{Waypoints.dimensionName(viewer.level().dimension().location().toString())})
            .getString()
         : String.format("%d, %d, %d", viewer.getBlockX(), viewer.getBlockY(), viewer.getBlockZ());
      String chunks = elsewhere ? null : Component.translatable("diorama.map.chunks", new Object[]{shown.chunkCount()}).getString();
      SavedChunkImport job = elsewhere ? null : MapSession.get().savedChunks();
      boolean redrawing = job != null && shown.memory() != null && job.chunksLeft() > 0;
      String baking = redrawing
         ? Component.translatable("diorama.map.redrawing", new Object[]{job.percentDone()}).getString()
         : (!elsewhere && shown.bakingCount() > 0 ? Component.translatable("diorama.map.baking").getString() : null);
      MapExport export = MapExport.running();
      String saving = export != null ? Component.translatable("diorama.export.saving", new Object[]{Math.round(export.progress() * 100.0)}).getString() : null;
      List<String> parts = new ArrayList<>();

      for (String part : new String[]{roof, position, chunks, baking, saving}) {
         if (part != null) {
            parts.add(part);
         }
      }

      for (String dropped : new String[]{chunks, baking, roof, saving}) {
         if (this.font.width(String.join("   ·   ", parts)) <= room) {
            break;
         }

         parts.remove(dropped);
      }

      return this.font.plainSubstrByWidth(String.join("   ·   ", parts), Math.max(0, room));
   }

   private void drawMapName(GuiGraphics graphics, int left) {
      Maps maps = Maps.get();
      if (maps != null && this.minecraft.level != null) {
         String dimension = this.dimension();
         if (maps.several(dimension)) {
            String name = Component.translatable("diorama.map.current_map", new Object[]{maps.name(dimension, maps.active(dimension))}).getString();
            int room = this.width - 6 - left;
            if (this.font.width(name) > room) {
               String ellipsis = CommonComponents.ELLIPSIS.getString();
               if (room < this.font.width(ellipsis) * 4) {
                  return;
               }

               name = this.font.plainSubstrByWidth(name, room - this.font.width(ellipsis)) + ellipsis;
            }

            graphics.drawString(this.font, name, this.width - 6 - this.font.width(name), this.height - 11, -1, false);
         }
      }
   }

   private int buttonsStart() {
      int start = this.width;

      for (Button button : List.of(this.mapsButton, this.waypointsButton, this.settingsButton)) {
         if (button.visible) {
            start = Math.min(start, button.getX());
         }
      }

      return start;
   }

   public boolean mouseClicked(double x, double y, int button) {
      this.draw.anyPress(x, y);
      return this.boundPressed(
            button,
            DioramaClient.WAYPOINT_LIST.matchesMouse(button),
            DioramaClient.OPEN_MAP.matchesMouse(button),
            DioramaClient.TOGGLE_DIMENSION.matchesMouse(button)
         )
         || this.menuPressed(x, y, button)
         || this.clicked(super.mouseClicked(x, y, button), x, y, button);
   }

   public boolean mouseReleased(double x, double y, int button) {
      return this.released(x, y, button) || super.mouseReleased(x, y, button);
   }

   public boolean mouseDragged(double x, double y, int button, double dx, double dy) {
      return this.dragged(x, y, button, dx, dy) || super.mouseDragged(x, y, button, dx, dy);
   }

   public boolean keyPressed(int key, int scancode, int modifiers) {
      return this.keyDown(
            key,
            DioramaClient.WAYPOINT_LIST.matches(key, scancode),
            DioramaClient.OPEN_MAP.matches(key, scancode),
            DioramaClient.TOGGLE_DIMENSION.matches(key, scancode)
         )
         || super.keyPressed(key, scancode, modifiers);
   }

   private boolean boundPressed(int button, boolean waypointListKey, boolean mapKey, boolean dimensionKey) {
      return button != 0 && button != 1 && (button != 2 || !this.draw.active()) ? this.bindingPressed(waypointListKey, mapKey, dimensionKey) : false;
   }

   private boolean menuPressed(double x, double y, int button) {
      if (this.menu == null) {
         return false;
      }

      this.pressOnMenu = this.menu.contains(x, y);
      this.pressButton = button;
      this.pressX = x;
      this.pressY = y;
      this.dragged = false;
      return true;
   }

   private boolean clicked(boolean widgetTook, double x, double y, int button) {
      this.pressButton = button;
      this.pressOnControl = false;
      if (!widgetTook && button == 0) {
         int tab = this.tabAt(x, y);
         if (tab >= 0) {
            if (tab == 1 != (this.layer != Integer.MIN_VALUE)) {
               this.toggleCaves();
            } else if (this.otherDimension == null) {
               this.layerPicked = true;
            }

            this.minecraft.getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F));
            widgetTook = true;
         } else if (this.layer != Integer.MIN_VALUE && this.minecraft.level != null && this.gauge.contains(x, y)) {
            this.menu = null;
            this.layerPicked = true;
            this.gauge.grab(this.minecraft.level, y);
            widgetTook = true;
         }
      }

      if (!widgetTook && this.onButton(x, y)) {
         widgetTook = true;
      }

      if (widgetTook) {
         this.pressOnControl = true;
         this.dragged = true;
         return true;
      }

      this.pressX = x;
      this.pressY = y;
      this.dragged = false;
      if (this.draw.active() && this.lastView != null) {
         this.draw.press(this.lastView, this.font, this.dimension(), x, y, button);
      }

      return true;
   }

   private boolean released(double x, double y, int button) {
      if (button == 0 && this.gauge.dragging()) {
         int slice = this.gauge.release();
         if (this.layer != Integer.MIN_VALUE) {
            this.layer = slice;
            this.layerPicked = true;
         }
      }

      if (button == 0 && button != this.pressButton && this.draw.holding() && this.lastView != null) {
         this.draw.release(this.lastView, this.font, this.dimension(), x, y, button, true);
         return true;
      }

      if (button != this.pressButton) {
         return true;
      }

      this.pressButton = -1;
      boolean moved = this.dragged || Math.hypot(x - this.pressX, y - this.pressY) > 3.0;
      if (this.pressOnMenu) {
         this.pressOnMenu = false;
         if (!moved && button == 0 && this.menu != null && this.menu.click(x, y)) {
            this.menu = null;
         }

         return true;
      } else {
         if (this.draw.active()
            && !this.pressOnControl
            && this.lastView != null
            && this.draw.release(this.lastView, this.font, this.dimension(), x, y, button, moved)) {
            return true;
         }

         if (moved) {
            return true;
         }

         if (this.menu != null) {
            this.menu = null;
            if (button == 1) {
               this.openMenu(x, y);
            }

            return true;
         } else if (button != 0) {
            if (button == 1) {
               this.openMenu(x, y);
               return true;
            } else {
               return false;
            }
         } else {
            WaypointOverlay.Pin pin = WaypointOverlay.under(this.pins, x, y);
            if (pin != null && pin.waypoint().editable()) {
               this.minecraft.setScreen(new WaypointEditScreen(this, pin.waypoint().source(), false));
            } else if (pin != null) {
               this.openMenu(x, y);
            }

            return true;
         }
      }
   }

   private void openMenu(double screenX, double screenY) {
      WaypointOverlay.Pin pin = WaypointOverlay.under(this.pins, screenX, screenY);
      if (pin != null) {
         this.menu = new MapMenu(this.waypointItems(pin.waypoint()), this.font, screenX, screenY, this.width, this.height);
      } else {
         CompletableFuture<MapLevel.Spot> spot = this.spotAt(screenX, screenY, true);
         BlockPos rough = this.roughSpot(screenX, screenY);

         try {
            MapLevel.Spot found = spot.get(50L, TimeUnit.MILLISECONDS);
            this.menu = new MapMenu(this.spotItems(found, rough), this.font, screenX, screenY, this.width, this.height);
         } catch (TimeoutException e) {
            MapMenu waiting = new MapMenu(
               List.of(MapMenu.Item.info(Component.translatable("diorama.menu.finding"))), this.font, screenX, screenY, this.width, this.height
            );
            this.menu = waiting;
            spot.thenAcceptAsync(found -> {
               if (this.menu == waiting) {
                  this.menu = new MapMenu(this.spotItems(found, rough), this.font, screenX, screenY, this.width, this.height);
               }
            }, this.minecraft);
         } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
         } catch (ExecutionException e) {
            this.menu = new MapMenu(this.spotItems(MapLevel.Spot.NOTHING, rough), this.font, screenX, screenY, this.width, this.height);
         }
      }
   }

   private CompletableFuture<MapLevel.Spot> spotAt(double screenX, double screenY, boolean readWorld) {
      MapViewport.View view = this.lastView;
      return view == null
         ? CompletableFuture.completedFuture(MapLevel.Spot.NOTHING)
         : this.spotAtMap(
            view.centerU() + (screenX - view.x() - view.width() / 2.0) / view.zoom(),
            view.centerV() + (screenY - view.y() - view.height() / 2.0) / view.zoom(),
            readWorld
         );
   }

   private CompletableFuture<MapLevel.Spot> spotAtMap(double u, double mapV, boolean readWorld) {
      MapSession session = MapSession.get();
      BlockLooks looks = BlockLooks.get();
      if (session != null && looks != null) {
         double v = TileSpots.rowMiddle(mapV);
         if (this.otherDimension != null) {
            return this.shownMap(session).savedSpot(this.layer, u, v, looks, false);
         }

         TileKey tile = new TileKey(this.layer, (int)Math.floor(u / 128.0), (int)Math.floor(v / 128.0));
         MapPick pick = new MapPick(session.map(), looks, tile, session.map().fogOfWar());

         return switch (pick.pick(u, v)) {
            case 0 -> CompletableFuture.completedFuture(
               new MapLevel.Spot(Projection.facing().worldPos(new BlockPos((int)Math.floor(u), pick.y, pick.z)), false)
            );
            case 1 -> CompletableFuture.completedFuture(MapLevel.Spot.NOTHING);
            default -> session.map().savedSpot(this.layer, u, v, looks, readWorld);
         };
      } else {
         return CompletableFuture.completedFuture(MapLevel.Spot.NOTHING);
      }
   }

   private @Nullable BlockPos roughSpot(double screenX, double screenY) {
      MapViewport.View view = this.lastView;
      if (view == null) {
         return null;
      }

      int y = this.layer == Integer.MIN_VALUE ? 64 : this.layer;
      double u = view.centerU() + (screenX - view.x() - view.width() / 2.0) / view.zoom();
      double v = TileSpots.rowMiddle(view.centerV() + (screenY - view.y() - view.height() / 2.0) / view.zoom());
      return Projection.facing().worldPos(new BlockPos((int)Math.floor(u), y, (int)Math.floor(Projection.zAt(v, y))));
   }

   private List<MapMenu.Item> spotItems(MapLevel.Spot found, @Nullable BlockPos rough) {
      BlockPos spot = found.pos();
      if (spot == null) {
         List<MapMenu.Item> unknown = new ArrayList<>();
         unknown.add(MapMenu.Item.info(Component.translatable(found.unsaved() ? "diorama.menu.not_saved" : "diorama.menu.not_mapped")));
         if (rough != null) {
            if (this.otherDimension == null) {
               unknown.add(MapMenu.Item.info(this.roughly(rough.getX(), rough.getZ())));
            }

            unknown.add(this.copyItem(rough.getX() + " " + rough.getZ()));
         }

         return unknown;
      } else {
         int x = spot.getX();
         int y = spot.getY();
         int z = spot.getZ();
         String dimension = this.dimension();
         List<MapMenu.Item> items = new ArrayList<>();
         items.add(MapMenu.Item.info(this.where(x, y, z)));
         items.add(MapMenu.Item.action(Component.translatable("diorama.menu.teleport_here"), Waypoints.canTeleport(dimension), () -> {
            Waypoints.teleport(dimension, x, y, z);
            this.minecraft.setScreen(null);
         }));
         if (Settings.get().mapAddWaypoints) {
            items.add(MapMenu.Item.action(Component.translatable("diorama.menu.add_waypoint"), true, () -> {
               Waypoint waypoint = Waypoints.at(spot, dimension);
               if (waypoint != null) {
                  this.minecraft.setScreen(new WaypointEditScreen(this, waypoint, true));
               }
            }));
            items.add(MapMenu.Item.action(Component.translatable("diorama.menu.add_temporary"), true, () -> Waypoints.addTemporary(spot, dimension)));
         }

         items.add(this.copyItem(x + " " + y + " " + z));
         items.add(MapMenu.Item.confirmed(Component.translatable("diorama.menu.share_location"), Component.translatable("diorama.menu.share_confirm"), () -> {
            Waypoint location = new Waypoint(Component.translatable("diorama.menu.shared_location").getString(), x, y, z, dimension, Waypoint.PALETTE[0]);
            if (WaypointShare.send(location)) {
               this.minecraft.setScreen(null);
            }
         }));
         return items;
      }
   }

   private List<MapMenu.Item> waypointItems(Waypoint shown) {
      Waypoint waypoint = shown.source();
      List<MapMenu.Item> items = new ArrayList<>();
      items.add(MapMenu.Item.title(Component.literal(shown.shownName())));
      items.add(MapMenu.Item.info(this.where(shown.x, shown.y, shown.z)));
      if (waypoint.editable()) {
         items.add(
            MapMenu.Item.action(
               Component.translatable("diorama.waypoints.edit"), true, () -> this.minecraft.setScreen(new WaypointEditScreen(this, waypoint, false))
            )
         );
      }

      items.add(MapMenu.Item.action(Component.translatable("diorama.waypoint.teleport"), Waypoints.canTeleport(waypoint.dimension), () -> {
         Waypoints.teleport(waypoint.dimension, waypoint.x, waypoint.y, waypoint.z);
         this.minecraft.setScreen(null);
      }));
      items.add(this.copyItem(shown.x + " " + shown.y + " " + shown.z));
      items.add(MapMenu.Item.confirmed(Component.translatable("diorama.waypoint.share"), Component.translatable("diorama.menu.share_confirm"), () -> {
         if (WaypointShare.send(waypoint)) {
            this.minecraft.setScreen(null);
         }
      }));
      if (waypoint.kind == Waypoint.Kind.TEMPORARY) {
         items.add(MapMenu.Item.action(Component.translatable("diorama.menu.keep"), true, () -> {
            waypoint.kind = Waypoint.Kind.MANUAL;
            this.changed();
         }));
      }

      items.add(MapMenu.Item.action(Component.translatable("diorama.waypoints.hide"), true, () -> {
         WaypointStore store = WaypointStore.get();
         if (store != null) {
            store.setVisible(waypoint, false);
         }
      }));
      if (waypoint.editable()) {
         items.add(MapMenu.Item.confirmed(Component.translatable("diorama.waypoint.delete"), Component.translatable("diorama.menu.delete_confirm"), () -> {
            WaypointStore store = WaypointStore.get();
            if (store != null) {
               store.remove(waypoint);
            }
         }));
      }

      return items;
   }

   private MapMenu.Item copyItem(String coordinates) {
      return MapMenu.Item.stays(
         Component.translatable("diorama.menu.copy_coordinates"),
         Component.translatable("diorama.menu.copied"),
         () -> this.minecraft.keyboardHandler.setClipboard(coordinates)
      );
   }

   private void changed() {
      WaypointStore store = WaypointStore.get();
      if (store != null) {
         store.changed();
      }
   }

   private Component where(int x, int y, int z) {
      if (this.otherDimension != null) {
         return Component.translatable("diorama.map.spot", new Object[]{x, y, z});
      }

      Entity viewer = MapSession.viewer();
      long distance = viewer == null ? 0L : Math.round(Math.sqrt(viewer.distanceToSqr(x + 0.5, y, z + 0.5)));
      return Component.translatable("diorama.menu.where", new Object[]{x, y, z, distance});
   }

   private Component roughly(int x, int z) {
      Entity viewer = MapSession.viewer();
      long distance = viewer == null ? 0L : Math.round(Math.hypot(viewer.getX() - (x + 0.5), viewer.getZ() - (z + 0.5)));
      return Component.translatable("diorama.menu.where_rough", new Object[]{x, z, distance});
   }

   private boolean dragged(double x, double y, int button, double dx, double dy) {
      if (this.pressButton == -1) {
         return false;
      }

      if (Math.hypot(x - this.pressX, y - this.pressY) > 3.0) {
         this.dragged = true;
      }

      if (this.pressOnMenu) {
         return true;
      }

      if (this.gauge.dragging()) {
         if (this.minecraft.level != null) {
            this.gauge.drag(this.minecraft.level, y);
         }

         return true;
      } else {
         if (this.pressOnControl) {
            return false;
         }

         if (this.draw.active() && !this.draw.pans(button)) {
            if (this.lastView != null) {
               this.draw.drag(this.lastView, this.font, this.dimension(), x, y);
            }

            return true;
         } else if ((button == 0 || this.draw.active()) && this.dragged) {
            this.menu = null;
            this.centerU = this.centerU - dx / this.zoom;
            this.centerV = this.centerV - dy / this.zoom;
            this.following = false;
            return true;
         } else {
            return false;
         }
      }
   }

   public boolean mouseScrolled(double x, double y, double scrollX, double scrollY) {
      this.menu = null;
      double before = this.zoom;
      double after = Math.max(this.minZoom(), Math.min(16.0, before * Math.pow(1.25, scrollY)));
      if (after != before) {
         double mapU = this.centerU + (x - this.width / 2.0) / before;
         double mapV = this.centerV + (y - this.height / 2.0) / before;
         this.centerU = mapU - (x - this.width / 2.0) / after;
         this.centerV = mapV - (y - this.height / 2.0) / after;
         this.zoom = after;
         if (this.following) {
            this.following = false;
         }
      }

      return true;
   }

   private void zoomButton(int steps) {
      this.menu = null;
      this.zoom = Math.max(this.minZoom(), Math.min(16.0, this.zoom * Math.pow(1.25, steps)));
   }

   private boolean keyDown(int key, boolean waypointListKey, boolean mapKey, boolean dimensionKey) {
      if (this.draw.typing()) {
         return this.draw.key(key, this.dimension());
      }

      if (this.bindingPressed(waypointListKey, mapKey, dimensionKey)) {
         return true;
      }

      if (key == 256 && this.menu != null) {
         this.menu = null;
         return true;
      }

      if (this.draw.key(key, this.dimension())) {
         return true;
      }

      Entity viewer = MapSession.viewer();
      switch (key) {
         case 32:
            this.menu = null;
            this.following = true;
            return true;
         case 67:
            this.toggleCaves();
            return true;
         case 266:
            this.menu = null;
            this.shiftSlice(4, viewer);
            return true;
         case 267:
            this.menu = null;
            this.shiftSlice(-4, viewer);
            return true;
         default:
            return false;
      }
   }

   private boolean bindingPressed(boolean waypointListKey, boolean mapKey, boolean dimensionKey) {
      if (dimensionKey) {
         this.nextDimension();
         return true;
      } else if (waypointListKey) {
         this.openWaypoints();
         return true;
      } else if (mapKey) {
         this.onClose();
         return true;
      } else {
         return false;
      }
   }

   private void openWaypoints() {
      WaypointListScreen list = new WaypointListScreen(this);
      if (this.otherDimension != null) {
         list.showDimension(this.otherDimension);
      }

      this.minecraft.setScreen(list);
   }

   private void toggleCaves() {
      this.menu = null;
      if (this.otherDimension == null && ServerRules.caveMapsAllowed()) {
         this.gauge.cancel();
         Entity viewer = MapSession.viewer();
         this.layer = this.layer == Integer.MIN_VALUE && viewer != null ? playerSlice(viewer) : Integer.MIN_VALUE;
         this.layerPicked = true;
      }
   }

   private static int playerSlice(Entity viewer) {
      MapSession session = MapSession.get();
      return session != null ? session.caveLayer(viewer) : MapSession.caveLayerFor(viewer.getY());
   }

   private void shiftSlice(int delta, @Nullable Entity viewer) {
      if (this.otherDimension == null && ServerRules.caveMapsAllowed()) {
         if (this.layer == Integer.MIN_VALUE) {
            if (viewer == null) {
               return;
            }

            this.layer = playerSlice(viewer);
         }

         this.layer = Math.max(this.minecraft.level.getMinBuildHeight() + 4, Math.min(this.minecraft.level.getMaxBuildHeight() - 1 + 1, this.layer + delta));
         this.layerPicked = true;
      }
   }

   private List<WaypointOverlay.Pin> layoutPins(MapViewport.View view, List<Waypoint> waypoints) {
      int top = 17;
      int bottom = this.height - 14 - 1;
      int right = this.width - (this.layer != Integer.MIN_VALUE ? 60 : 0);
      MapViewport.View clear = new MapViewport.View(
         0,
         top,
         right,
         bottom - top,
         view.centerU() + (right / 2.0 - view.x() - view.width() / 2.0) / view.zoom(),
         view.centerV() + ((top + bottom) / 2.0 - view.y() - view.height() / 2.0) / view.zoom(),
         view.zoom(),
         view.layer()
      );
      List<WaypointOverlay.Pin> pins = WaypointOverlay.layout(clear, waypoints, true);
      int bx0 = (this.facingButton.visible ? this.facingButton.getX() : this.zoomIn.getX()) - 2;
      int by0 = (this.export.visible ? this.export.getY() : this.drawButton.getY()) - 2;
      int bx1 = this.zoomIn.getX() + this.zoomIn.getWidth() + 2;
      int by1 = this.recenter.getY() + this.recenter.getHeight() + 2;
      int dx1 = this.dimensionButton.getX() + this.dimensionButton.getWidth() + 2;
      int dy0 = this.dimensionButton.getY() - 2;
      List<WaypointOverlay.Pin> out = new ArrayList<>(pins.size());

      for (WaypointOverlay.Pin pin : pins) {
         float r = Pins.reach(pin.waypoint(), (int)pin.size()) * pin.scale();
         float rx = r + Pins.stretch(pin.waypoint(), (int)pin.size()) * pin.scale() / 2.0F;
         if (pin.clamped() && pin.x() + rx > bx0 && pin.x() - rx < bx1 && pin.y() + r > by0 && pin.y() - r < by1) {
            float up = pin.y() + r - by0;
            float left = pin.x() + rx - bx0;
            pin = up <= left
               ? new WaypointOverlay.Pin(pin.waypoint(), pin.x(), by0 - r, pin.size(), pin.scale(), true)
               : new WaypointOverlay.Pin(pin.waypoint(), bx0 - rx, pin.y(), pin.size(), pin.scale(), true);
         }

         if (this.dimensionButton.visible && pin.clamped() && pin.x() - rx < dx1 && pin.y() + r > dy0) {
            float up = pin.y() + r - dy0;
            float across = dx1 - (pin.x() - rx);
            pin = up <= across
               ? new WaypointOverlay.Pin(pin.waypoint(), pin.x(), dy0 - r, pin.size(), pin.scale(), true)
               : new WaypointOverlay.Pin(pin.waypoint(), dx1 + rx, pin.y(), pin.size(), pin.scale(), true);
         }

         out.add(pin);
      }

      return out;
   }

   private void drawTabs(GuiGraphics graphics, int mouseX, int mouseY) {
      int x = 2;

      for (int i = 0; i < this.tabs(); i++) {
         boolean selected = i == 1 == (this.layer != Integer.MIN_VALUE);
         String name = TAB_NAMES[i].getString();
         int nameWidth = this.font.width(name);
         int w = nameWidth + 13;
         int h = selected ? 18 : 16;
         boolean hovered = this.menu == null && mouseX >= x && mouseX < x + w && mouseY >= 0 && mouseY < h;
         sprite(graphics, selected ? (hovered ? TAB_SELECTED_HIGHLIGHTED : TAB_SELECTED) : (hovered ? TAB_HIGHLIGHTED : TAB), x, 0, w, h);
         if (selected) {
            graphics.fill(x + 2, 2, x + w - 2, h, 1073741824);
            int underline = nameWidth - 1;
            graphics.fill(x + (w - underline) / 2, h - 3, x + (w - underline) / 2 + underline, h - 2, -1);
         }

         graphics.drawString(this.font, name, x + w / 2 - nameWidth / 2, selected ? 4 : 6, selected ? -1 : -2039584, true);
         this.tabX[i] = x;
         this.tabWidth[i] = w;
         x += w;
      }

      this.tabsEnd = x;
   }

   private int tabs() {
      return this.otherDimension == null && ServerRules.caveMapsAllowed() ? 2 : 1;
   }

   private int tabAt(double x, double y) {
      for (int i = 0; i < this.tabs(); i++) {
         if (x >= this.tabX[i] && x < this.tabX[i] + this.tabWidth[i] && y >= 0.0 && y < 16.0) {
            return i;
         }
      }

      return -1;
   }

   static void tab(GuiGraphics graphics, boolean selected, boolean hovered, int x, int y, int w, int h) {
      sprite(graphics, selected ? (hovered ? TAB_SELECTED_HIGHLIGHTED : TAB_SELECTED) : (hovered ? TAB_HIGHLIGHTED : TAB), x, y, w, h);
   }

   private static void sprite(GuiGraphics graphics, ResourceLocation sprite, int x, int y, int w, int h) {
      graphics.blitSprite(sprite, x, y, w, h);
   }

   private void drawDimensionIcon(GuiGraphics graphics) {
      int x = this.dimensionButton.getX();
      int y = this.dimensionButton.getY();

      String[] icon = switch (this.dimension()) {
         case "minecraft:overworld" -> OVERWORLD_ICON;
         case "minecraft:the_nether" -> NETHER_ICON;
         case "minecraft:the_end" -> END_ICON;
         default -> null;
      };
      if (icon != null) {
         DrawMode.icon(graphics, icon, x, y, -1);
      } else {
         String name = this.dimensionName().getString();
         String letter = name.isEmpty() ? "?" : name.substring(0, name.offsetByCodePoints(0, 1));
         graphics.drawString(this.font, letter, x + 10 - this.font.width(letter) / 2, y + 6, -1, true);
      }
   }

   private void caret(GuiGraphics graphics, Button button, boolean down) {
      int colour = button.active ? -1 : -6250336;
      int cx = button.getX() + button.getWidth() / 2;
      int y = button.getY() + (button.getHeight() - 3) / 2;

      for (int row = 0; row < 3; row++) {
         int half = down ? 2 - row : row;
         graphics.fill(cx - half, y + row, cx + half + 1, y + row + 1, colour);
      }
   }

   private boolean overControls(double x, double y) {
      return !(y < 16.0) && !(y >= this.height - 14)
         ? this.onButton(x, y) || this.layer != Integer.MIN_VALUE && this.gauge.contains(x, y) || this.draw.onControls(x, y)
         : true;
   }

   private boolean onButton(double x, double y) {
      for (AbstractWidget widget : new AbstractWidget[]{
         this.facingButton,
         this.dimensionButton,
         this.mapsButton,
         this.waypointsButton,
         this.settingsButton,
         this.export,
         this.zoomIn,
         this.zoomOut,
         this.recenter,
         this.sliceUp,
         this.sliceDown,
         this.drawButton
      }) {
         if (on(widget, x, y)) {
            return true;
         }
      }

      return false;
   }

   private static boolean on(AbstractWidget widget, double x, double y) {
      return widget.visible && x >= widget.getX() && x < widget.getX() + widget.getWidth() && y >= widget.getY() && y < widget.getY() + widget.getHeight();
   }

   private MapScreen.@Nullable Tip controlTooltip(double x, double y) {
      int tab = this.tabAt(x, y);
      if (tab >= 0) {
         return new MapScreen.Tip(lines(TAB_NAMES[tab], key(keyName(67))), new ScreenRectangle(this.tabX[tab], 0, this.tabWidth[tab], 16));
      }

      if (on(this.dimensionButton, x, y)) {
         return tip(
            this.dimensionButton,
            this.dimensionName(),
            DioramaClient.TOGGLE_DIMENSION.isUnbound()
               ? Component.translatable("diorama.map.dimension_button")
               : Component.translatable("diorama.map.dimension_button_key", new Object[]{DioramaClient.TOGGLE_DIMENSION.getTranslatedKeyMessage()})
         );
      }

      if (on(this.waypointsButton, x, y) && !DioramaClient.WAYPOINT_LIST.isUnbound()) {
         return tip(this.waypointsButton, this.waypointsButton.getMessage(), key(DioramaClient.WAYPOINT_LIST.getTranslatedKeyMessage()));
      }

      if (on(this.export, x, y)) {
         return new MapScreen.Tip(
            List.of(Component.translatable("diorama.export.title").getVisualOrderText()),
            new ScreenRectangle(this.export.getX(), this.export.getY(), this.export.getWidth(), this.export.getHeight())
         );
      }

      if (on(this.zoomIn, x, y)) {
         return tip(this.zoomIn, Component.translatable("diorama.map.zoom_in"), Component.translatable("diorama.map.scroll_up"));
      }

      if (on(this.zoomOut, x, y)) {
         return tip(this.zoomOut, Component.translatable("diorama.map.zoom_out"), Component.translatable("diorama.map.scroll_down"));
      }

      if (!on(this.facingButton, x, y)) {
         if (on(this.recenter, x, y)) {
            return tip(this.recenter, Component.translatable("diorama.map.recenter"), key(keyName(32)));
         } else if (on(this.drawButton, x, y)) {
            return tip(
               this.drawButton,
               Component.translatable("diorama.draw.button"),
               Component.translatable(this.draw.active() ? "diorama.draw.button.stop" : "diorama.draw.button.start")
            );
         } else if (on(this.sliceUp, x, y)) {
            return tip(this.sliceUp, Component.translatable("diorama.map.slice_up"), key(keyName(266)));
         } else {
            return on(this.sliceDown, x, y) ? tip(this.sliceDown, Component.translatable("diorama.map.slice_down"), key(keyName(267))) : null;
         }
      } else {
         MapSession session = MapSession.get();
         boolean walk = session != null && session.map().memory() != null && session.savedChunks() == null;
         return tip(
            this.facingButton,
            Component.translatable("diorama.map.facing." + Projection.facing().id()),
            Component.translatable(walk ? "diorama.map.facing_walk" : "diorama.map.facing_turn")
         );
      }
   }

   private static MapScreen.Tip tip(AbstractWidget control, Component name, Component second) {
      return new MapScreen.Tip(lines(name, second), new ScreenRectangle(control.getX(), control.getY(), control.getWidth(), control.getHeight()));
   }

   private List<FormattedCharSequence> spotTooltip(int mouseX, int mouseY) {
      MapViewport.View view = this.lastView;
      Entity viewer = MapSession.viewer();
      if (view != null && viewer != null) {
         double u = view.centerU() + (mouseX - view.x() - view.width() / 2.0) / view.zoom();
         double v = view.centerV() + (mouseY - view.y() - view.height() / 2.0) / view.zoom();
         int blockU = (int)Math.floor(u);
         int blockV = (int)Math.floor(v);
         boolean moved = blockU != this.spotU || blockV != this.spotV || this.layer != this.spotLayer;
         if (moved || ++this.spotAge > 10) {
            this.spotU = blockU;
            this.spotV = blockV;
            this.spotLayer = this.layer;
            this.spotAge = 0;
            CompletableFuture<BlockPos> found = this.spotAt(mouseX, mouseY, false).thenApply(MapLevel.Spot::pos);
            Object request = new Object();
            this.spotRequest = request;
            if (found.isDone()) {
               this.spot = found.getNow(null);
            } else {
               if (moved) {
                  this.spot = null;
               }

               found.thenAcceptAsync(spotx -> {
                  if (this.spotRequest == request) {
                     this.spot = spotx;
                  }
               }, this.minecraft);
            }
         }

         BlockPos spot = this.spot;
         if (spot == null) {
            return null;
         }

         if (this.otherDimension != null) {
            return List.of(Component.translatable("diorama.map.spot", new Object[]{spot.getX(), spot.getY(), spot.getZ()}).getVisualOrderText());
         }

         long distance = Math.round(Math.sqrt(viewer.distanceToSqr(spot.getX() + 0.5, spot.getY(), spot.getZ() + 0.5)));
         Component biome = this.biomeAt(spot);
         Component detail = biome != null
            ? Component.translatable("diorama.map.spot_detail", new Object[]{biome, distance})
            : Component.translatable("diorama.map.distance", new Object[]{distance});
         return withClaim(
            lines(Component.translatable("diorama.map.spot", new Object[]{spot.getX(), spot.getY(), spot.getZ()}), detail), spot.getX(), spot.getZ()
         );
      } else {
         return null;
      }
   }

   private static List<FormattedCharSequence> withClaim(Component described, int x, int z) {
      return withClaim(List.of(described.getVisualOrderText()), x, z);
   }

   private static List<FormattedCharSequence> withClaim(List<FormattedCharSequence> lines, int x, int z) {
      Claims.Claim claim = Settings.get().claims == Settings.ClaimsShown.OFF ? null : Claims.get().at(x >> 4, z >> 4);
      if (claim == null) {
         return lines;
      }

      List<FormattedCharSequence> both = new ArrayList<>(lines);
      both.add(claimLine(claim).getVisualOrderText());
      return both;
   }

   public List<String> tooltipText() {
      List<String> text = new ArrayList<>();

      for (FormattedCharSequence line : this.tooltipLines == null ? List.of() : this.tooltipLines) {
         StringBuilder builder = new StringBuilder();
         line.accept((index, style, codePoint) -> {
            builder.appendCodePoint(codePoint);
            return true;
         });
         text.add(builder.toString());
      }

      return text;
   }

   private static Component claimLine(Claims.Claim claim) {
      if (claim.titled()) {
         return Component.literal("■ ").withStyle(s -> s.withColor(claim.rgb())).append(Component.literal(claim.name()).withStyle(ChatFormatting.GRAY));
      }

      Component owner = claim.name().isEmpty() ? Component.translatable("diorama.map.claim.unnamed") : Component.literal(claim.name());
      Component line = Component.literal("■ ")
         .withStyle(s -> s.withColor(claim.rgb()))
         .append(Component.translatable("diorama.map.claim", new Object[]{owner}).withStyle(ChatFormatting.GRAY));
      return (Component)(claim.forceLoaded()
         ? line.copy().append(Component.translatable("diorama.map.claim.force_loaded").withStyle(ChatFormatting.GRAY))
         : line);
   }

   public @Nullable Component biomeAt(BlockPos pos) {
      ClientLevel level = this.minecraft.level;
      if (level == null) {
         return null;
      }

      int cellX = pos.getX() - 2 >> 2;
      int cellZ = pos.getZ() - 2 >> 2;

      for (int chunkX = cellX >> 2; chunkX <= cellX + 1 >> 2; chunkX++) {
         for (int chunkZ = cellZ >> 2; chunkZ <= cellZ + 1 >> 2; chunkZ++) {
            if (!level.getChunkSource().hasChunk(chunkX, chunkZ)) {
               return null;
            }
         }
      }

      return level.getBiome(pos)
         .unwrapKey()
         .map(k -> Component.translatableWithFallback(k.location().toLanguageKey("biome"), k.location().getPath()))
         .orElse(null);
   }

   private static List<FormattedCharSequence> lines(Component first, Component second) {
      return List.of(first.getVisualOrderText(), second.copy().withStyle(ChatFormatting.GRAY).getVisualOrderText());
   }

   private static Component key(Component name) {
      return Component.translatable("diorama.map.key", new Object[]{name});
   }

   private static Component keyName(int key) {
      return Type.KEYSYM.getOrCreate(key).getDisplayName();
   }

   public static void pauseForTests(boolean pause) {
      pausesForTests = pause;
   }

   public boolean isPauseScreen() {
      return pausesForTests;
   }

   public void removed() {
      this.viewport.close();
      this.draw.close();
      this.closeOtherMap();
   }

   public String dimension() {
      if (this.otherDimension != null) {
         return this.otherDimension;
      } else {
         return this.minecraft.level == null ? "" : this.minecraft.level.dimension().location().toString();
      }
   }

   public DrawMode drawMode() {
      return this.draw;
   }

   public Button drawButton() {
      return this.drawButton;
   }

   public Button dimensionButton() {
      return this.dimensionButton;
   }

   public @Nullable MapLevel otherMap() {
      return this.otherMap == null ? null : this.otherMap.map();
   }

   public MapViewport.@Nullable View view() {
      return this.lastView;
   }

   public List<WaypointOverlay.Pin> pins() {
      return this.pins;
   }

   public Button exportButton() {
      return this.export;
   }

   public @Nullable MapMenu menu() {
      return this.menu;
   }

   public float @Nullable [] sliceBand() {
      return this.layer == Integer.MIN_VALUE ? null : this.gauge.band();
   }

   public List<Radar.Blip> blips() {
      return this.blips;
   }

   private static int overlayBits(MapSession session) {
      Settings s = Settings.get();
      int bits = 0;
      if (s.chunkGrid) {
         bits |= 3;
      }

      if (s.slimeChunks && session.slimeSeed() != null) {
         bits |= 4;
      }

      if (s.spawnSpots != Settings.SpawnSpots.OFF) {
         bits |= 16;
      }

      if (s.spawnSpots == Settings.SpawnSpots.AT_NIGHT_TOO) {
         bits |= 8;
      }

      return bits;
   }

   private void followLayer(MapSession session, Entity viewer) {
      if (!ServerRules.caveMapsAllowed()) {
         this.layer = Integer.MIN_VALUE;
      }

      if (!this.layerPicked && this.otherDimension == null && Settings.get().autoCaves && ServerRules.caveMapsAllowed()) {
         int auto = session.autoLayer(viewer);
         if (auto == this.layer) {
            this.followHoldStart = -1L;
         } else {
            long now = System.currentTimeMillis();
            if (this.followHoldStart < 0L) {
               this.followHoldStart = now;
            }

            MapLevel level = session.map();
            MapViewport.View wanted = new MapViewport.View(
               0, 0, this.width, this.height, this.centerU, this.centerV, Math.max(this.minZoom(auto), this.zoom), auto
            );
            MapViewport.View current = new MapViewport.View(0, 0, this.width, this.height, this.centerU, this.centerV, this.zoom, this.layer);
            if (MapViewport.ready(level, wanted) || now - this.followHoldStart > 3000L || !MapViewport.showsAnything(level, current)) {
               this.layer = auto;
               this.followHoldStart = -1L;
               this.menu = null;
            }
         }
      } else {
         this.followHoldStart = -1L;
      }
   }

   private double minZoom() {
      return this.minZoom(this.layer);
   }

   private double minZoom(int layer) {
      MapSession session = MapSession.get();
      boolean overviews = layer == Integer.MIN_VALUE && session != null && (this.otherDimension != null || session.map().hasOverviews());
      return overviews ? 0.001953125 / Minecraft.getInstance().getWindow().getGuiScale() : 0.5;
   }

   public double zoom() {
      return this.zoom;
   }

   public void lookAt(double u, double v, double zoom, int layer) {
      this.centerU = u;
      this.centerV = v;
      this.zoom = zoom;
      this.layer = layer;
      this.layerPicked = true;
      this.following = false;
   }

   private record OtherMap(Path folder, MapStyle style, Facing facing, MapLevel map) {
   }

   private record Tip(List<FormattedCharSequence> lines, ScreenRectangle control) {
   }
}
