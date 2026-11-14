package dev.diorama;

import dev.diorama.waypoint.ScreenArt;
import dev.diorama.waypoint.WaypointStore;
import dev.diorama.waypoint.Waypoints;
import java.nio.file.Path;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import org.jspecify.annotations.Nullable;

public final class MapsScreen extends Screen {
   private static final int LIST_WIDTH = 340;
   private static final int NEW_WIDTH = 100;
   private static final int ROW = 24;
   private static final int PITCH = 26;
   private static final int BAR_Y = 22;
   private static final int LIST_TOP = 48;
   private static final int ARROW = 20;
   private static final int NAME_WIDTH = 160;
   private static final int TEXT_LEFT = 6;
   private static final int ACTION_GAP = 2;
   private static final long DOUBLE_CLICK_MS = 250L;
   private static final int DETAIL = -7303024;
   private static final int HINT = -6250336;
   private final Map<Path, CompletableFuture<Long>> sizes = new ConcurrentHashMap<>();
   private final @Nullable Screen parent;
   private final List<String> dimensions = new ArrayList<>();
   private @Nullable String dimension;
   private final List<Maps.Info> rows = new ArrayList<>();
   private @Nullable String selected;
   private boolean armed;
   private final List<Button> actions = new ArrayList<>();
   private @Nullable Button use;
   private @Nullable Button delete;
   private @Nullable Button confirm;
   private int scroll;
   private int listBottom;
   private int visibleRows;
   private long lastClick;
   private @Nullable String lastClicked;

   public MapsScreen(@Nullable Screen parent) {
      super(Component.translatable("diorama.maps.title"));
      this.parent = parent;
   }

   void show(String dimension, @Nullable String selected) {
      this.dimension = dimension;
      this.selected = selected;
      this.armed = false;
   }

   private void collectDimensions(@Nullable String here, @Nullable Maps maps) {
      LinkedHashSet<String> found = new LinkedHashSet<>(List.of("minecraft:overworld", "minecraft:the_nether", "minecraft:the_end"));
      if (here != null) {
         found.add(here);
      }

      if (this.dimension != null) {
         found.add(this.dimension);
      }

      if (maps != null) {
         found.addAll(maps.dimensions());
      }

      this.dimensions.clear();
      this.dimensions.addAll(found);
      if (this.dimension == null || !this.dimensions.contains(this.dimension)) {
         this.dimension = here != null ? here : this.dimensions.get(0);
         this.selected = null;
      }
   }

   private void step(int step) {
      this.lastClicked = null;
      int index = this.dimensions.indexOf(this.dimension);
      this.show(this.dimensions.get(Math.floorMod(index + step, this.dimensions.size())), null);
      this.scroll = 0;
      this.rebuildWidgets();
   }

   protected void init() {
      String here = this.minecraft.level == null ? null : this.minecraft.level.dimension().location().toString();
      Maps maps = Maps.get();
      this.collectDimensions(here, maps);
      this.rows.clear();
      this.sizes.clear();
      if (maps != null) {
         this.rows.addAll(maps.list(this.dimension));

         for (Maps.Info info : this.rows) {
            this.sizes.put(info.folder(), CompletableFuture.supplyAsync(() -> Maps.size(info.folder())));
         }
      }

      if (this.selected != null && this.rows.stream().noneMatch(info -> info.id().equals(this.selected))) {
         this.selected = null;
      }

      this.armed = false;
      int cx = this.width / 2;
      int left = cx - 170;
      int footer = this.height - 28;
      this.addRenderableWidget(Button.builder(CommonComponents.GUI_DONE, b -> this.onClose()).bounds(cx - 100, footer, 200, 20).build());
      this.listBottom = footer - 10;
      int nameRight = left + 20 + 2 + 160;
      this.addRenderableWidget(Button.builder(Component.literal("<"), b -> this.step(-1)).bounds(left, 22, 20, 20).build());
      this.addRenderableWidget(Button.builder(Component.literal(">"), b -> this.step(1)).bounds(nameRight + 2, 22, 20, 20).build());
      Button newMap = (Button)this.addRenderableWidget(
         Button.builder(Component.translatable("diorama.maps.new"), b -> this.minecraft.setScreen(new MapNameScreen(this, this.dimension, null)))
            .bounds(left + 340 - 100, 22, 100, 20)
            .build()
      );
      newMap.active = maps != null;
      this.actions.clear();
      this.use = this.action(Component.translatable("diorama.maps.use"), () -> this.use(this.selected));
      this.actions.add(this.use);
      this.actions.add(this.action(Component.translatable("diorama.maps.rename"), () -> {
         if (this.selected != null) {
            this.minecraft.setScreen(new MapNameScreen(this, this.dimension, this.selected));
         }
      }));
      this.delete = this.action(Component.translatable("diorama.waypoint.delete").withStyle(ChatFormatting.RED), () -> {
         this.armed = true;
         this.placeRowButtons();
      });
      this.actions.add(this.delete);
      this.confirm = (Button)this.addRenderableWidget(
         Button.builder(Component.translatable("diorama.menu.delete_confirm").withStyle(ChatFormatting.RED), b -> {
            Maps current = Maps.get();
            if (this.armed && this.selected != null && current != null) {
               current.delete(this.dimension, this.selected);
               this.selected = null;
               this.rebuildWidgets();
            }
         }).size(20, 20).build()
      );
      this.visibleRows = Math.max(1, (this.listBottom - 48 - 2) / 26);
      this.scrollTo(this.scroll);
   }

   private Button action(Component label, Runnable onPress) {
      return (Button)this.addRenderableWidget(Button.builder(label, b -> onPress.run()).size(Math.max(32, this.font.width(label) + 10), 20).build());
   }

   private void use(@Nullable String id) {
      Maps maps = Maps.get();
      if (id != null && maps != null && this.dimension != null) {
         maps.use(this.dimension, id);
         this.rebuildWidgets();
      }
   }

   private Maps.@Nullable Info selectedInfo() {
      for (Maps.Info info : this.rows) {
         if (info.id().equals(this.selected)) {
            return info;
         }
      }

      return null;
   }

   private List<Button> rowActions() {
      Maps.Info info = this.selectedInfo();
      return info != null && info.active() ? this.actions.stream().filter(b -> b != this.use && b != this.delete).toList() : this.actions;
   }

   private int actionsWidth() {
      int span = -2;

      for (Button button : this.rowActions()) {
         span += button.getWidth() + 2;
      }

      return span;
   }

   private void placeRowButtons() {
      int index = this.selected == null ? -1 : this.rows.indexOf(this.selectedInfo()) - this.scroll;
      boolean shown = index >= 0 && index < this.visibleRows;
      List<Button> buttons = this.rowActions();
      int span = this.actionsWidth();
      int right = this.width / 2 + 170 - 2;
      int y = shown ? this.rowY(index) + 2 : 0;
      int x = right - span;

      for (Button button : this.actions) {
         boolean has = buttons.contains(button);
         show(button, shown && !this.armed && has);
         button.setPosition(x, y);
         if (has) {
            x += button.getWidth() + 2;
         }
      }

      Button confirm = this.confirm;
      if (confirm != null) {
         show(confirm, shown && this.armed);
         confirm.setRectangle(span, 20, right - span, y);
      }
   }

   private static void show(Button button, boolean shown) {
      button.visible = shown;
      button.active = shown;
   }

   private int rowY(int index) {
      return 50 + index * 26;
   }

   private int maxScroll() {
      return Math.max(0, this.rows.size() - this.visibleRows);
   }

   private void scrollTo(int scroll) {
      this.scroll = Math.max(0, Math.min(scroll, this.maxScroll()));
      this.placeRowButtons();
   }

   private int rowAt(double x, double y) {
      int left = this.width / 2 - 170;
      if (!(x < left) && !(x >= left + 340) && !(y < 48.0) && !(y >= this.listBottom)) {
         int index = (int)((y - 48.0 - 2.0) / 26.0);
         return index >= 0 && index < this.visibleRows && this.scroll + index < this.rows.size() && !(y >= this.rowY(index) + 24) ? this.scroll + index : -1;
      } else {
         return -1;
      }
   }

   public void renderBackground(GuiGraphics graphics, int mouseX, int mouseY, float a) {
      super.renderBackground(graphics, mouseX, mouseY, a);
      int cx = this.width / 2;
      int left = cx - 170;
      int right = left + 340;
      graphics.drawCenteredString(this.font, this.title, cx, 8, -1);
      int nameLeft = left + 20 + 2;
      ScreenArt.sprite(graphics, ScreenArt.TEXT_FIELD, nameLeft, 22, 160, 20);
      String label = this.fit(Waypoints.dimensionName(this.dimension).getString() + " (" + this.rows.size() + ")", 152);
      graphics.drawCenteredString(this.font, Component.literal(label), nameLeft + 80, 28, -1);
      ScreenArt.list(graphics, 0, 48, this.width, this.listBottom);
      if (this.rows.isEmpty()) {
         graphics.drawCenteredString(this.font, Component.translatable("diorama.maps.none"), cx, 68, -6250336);
      }

      WaypointStore store = WaypointStore.get();

      for (int i = 0; i < this.visibleRows && this.scroll + i < this.rows.size(); i++) {
         Maps.Info info = this.rows.get(this.scroll + i);
         int y = this.rowY(i);
         boolean selected = info.id().equals(this.selected);
         if (selected) {
            ScreenArt.selection(graphics, left, y, right, y + 24);
         }

         int room = right - (selected ? this.actionsWidth() + 4 : 6) - (left + 6);
         graphics.drawString(this.font, this.fit(info.name(), room), left + 6, y + 4, -1, true);
         graphics.drawString(this.font, this.fit(this.detail(info, store), room), left + 6, y + 14, -7303024, true);
      }

      if (this.maxScroll() > 0) {
         int track = this.listBottom - 48;
         int handle = Math.max(32, Math.min(track - 8, track * this.visibleRows / Math.max(1, this.rows.size())));
         int top = (track - handle) * this.scroll / this.maxScroll();
         ScreenArt.scrollbar(graphics, right + 4, 48, this.listBottom, handle, top);
      }
   }

   private String detail(Maps.Info info, @Nullable WaypointStore store) {
      List<String> parts = new ArrayList<>();
      if (info.active()) {
         parts.add(Component.translatable("diorama.maps.in_use").getString());
      }

      CompletableFuture<Long> size = this.sizes.get(info.folder());
      Long bytes = size != null && size.isDone() && !size.isCompletedExceptionally() ? size.join() : null;
      parts.add(
         bytes == null
            ? CommonComponents.ELLIPSIS.getString()
            : (bytes == 0L ? Component.translatable("diorama.maps.empty").getString() : Maps.readableSize(bytes))
      );
      if (store != null) {
         int count = store.count(this.dimension, info.id());
         parts.add(Component.translatable(count == 1 ? "diorama.maps.waypoint" : "diorama.maps.waypoints", new Object[]{count}).getString());
      }

      if (info.created() > 0L) {
         parts.add(Component.translatable("diorama.maps.made", new Object[]{date(info.created())}).getString());
      }

      return String.join("  ·  ", parts);
   }

   private static String date(long millis) {
      LocalDate day = Instant.ofEpochMilli(millis).atZone(ZoneId.systemDefault()).toLocalDate();
      return day.format(DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM));
   }

   private String fit(String text, int width) {
      if (this.font.width(text) <= width) {
         return text;
      }

      String ellipsis = CommonComponents.ELLIPSIS.getString();
      return this.font.plainSubstrByWidth(text, Math.max(0, width - this.font.width(ellipsis))) + ellipsis;
   }

   public boolean mouseScrolled(double x, double y, double scrollX, double scrollY) {
      this.scrollTo(this.scroll - (int)Math.signum(scrollY));
      return true;
   }

   public boolean mouseClicked(double x, double y, int button) {
      this.disarmUnless(x, y);
      return super.mouseClicked(x, y, button) || this.clicked(x, y, button);
   }

   private void disarmUnless(double x, double y) {
      Button button = this.confirm;
      if (this.armed && (button == null || !button.isMouseOver(x, y))) {
         this.armed = false;
         if (this.getFocused() == button || this.actions.contains(this.getFocused())) {
            this.setFocused(null);
         }

         this.placeRowButtons();
      }
   }

   private boolean clicked(double x, double y, int button) {
      if (button != 0) {
         return false;
      }

      int nameLeft = this.width / 2 - 170 + 20 + 2;
      if (x >= nameLeft && x < nameLeft + 160 && y >= 22.0 && y < 42.0) {
         this.step(1);
         return true;
      }

      int row = this.rowAt(x, y);
      if (row < 0) {
         return false;
      }

      String id = this.rows.get(row).id();
      long now = System.currentTimeMillis();
      if (id.equals(this.lastClicked) && now - this.lastClick < 250L) {
         this.use(id);
      } else {
         this.selected = id;
         this.armed = false;
         this.placeRowButtons();
      }

      this.lastClick = now;
      this.lastClicked = id;
      return true;
   }

   public void onClose() {
      this.minecraft.setScreen(this.parent);
   }

   public boolean isPauseScreen() {
      return false;
   }
}
