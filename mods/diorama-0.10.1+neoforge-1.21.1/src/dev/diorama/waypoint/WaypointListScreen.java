package dev.diorama.waypoint;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.diorama.DioramaClient;
import dev.diorama.MapSession;
import dev.diorama.render.Pins;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Stream;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import org.jspecify.annotations.Nullable;

public final class WaypointListScreen extends Screen {
   private static final int LIST_WIDTH = 340;
   private static final int ROW = 24;
   private static final int PITCH = 26;
   private static final int FOLDER_Y = 22;
   private static final int SEARCH_Y = 46;
   private static final int LIST_TOP = 72;
   private static final int ARROW = 20;
   private static final int FOLDER_NAME_WIDTH = 160;
   private static final int TEXT_LEFT = 24;
   private static final int ACTION_GAP = 2;
   private static final long DOUBLE_CLICK_MS = 250L;
   private static final int RED = -43691;
   private static final int DETAIL = -7303024;
   private static final int HIDDEN = -8355712;
   private static final int HINT = -6250336;
   private static final int SEARCH_HINT = -9408400;
   private final @Nullable Screen parent;
   private final List<WaypointListScreen.Folder> folders = new ArrayList<>();
   private @Nullable String folderKey;
   private @Nullable String shownDimension;
   private boolean empty;
   private final List<Button> folderActions = new ArrayList<>();
   private int folderNameWidth = 160;
   private @Nullable Button nextFolder;
   private @Nullable FolderMenu folderMenu;
   private boolean menuMoves;
   private boolean folderArmed;
   private final List<Waypoint> all = new ArrayList<>();
   private final List<Waypoint> others = new ArrayList<>();
   private final List<Waypoint> rows = new ArrayList<>();
   private int otherStart = -1;
   private int searchWidth;
   private @Nullable Button sortOrder;
   private @Nullable Button sortDirection;
   private boolean sortReversed;
   private final List<Button> actions = new ArrayList<>();
   private @Nullable Button edit;
   private @Nullable Button teleport;
   private @Nullable Button delete;
   private final MapPiece piece = new MapPiece(88, 56, 12);
   private final Waypoint spotHere = new Waypoint();
   private final Waypoint sample = new Waypoint("Home", 0, 0, 0, "", Waypoint.PALETTE[2]);
   private @Nullable EditBox search;
   private @Nullable Waypoint selected;
   private final Set<Waypoint> marked = new LinkedHashSet<>();
   private @Nullable Waypoint anchor;
   private boolean controlClick;
   private boolean shiftClick;
   private @Nullable Button move;
   private boolean armed;
   private @Nullable Button visibility;
   private @Nullable Button confirm;
   private int scroll;
   private int listBottom;
   private int hintY;
   private int visibleRows;
   private boolean draggingScrollbar;
   private long lastClick;
   private @Nullable Waypoint lastClicked;
   private boolean canTeleport;
   private int waystoneChanges;
   private boolean hadWaystones;
   private boolean newWaypointPressed;
   private boolean xaeroRead;
   private XaeroImport.@Nullable Found xaero;
   private @Nullable Component imported;
   private int importedY;
   private int emptyTop;
   private static final boolean MAC = System.getProperty("os.name", "").toLowerCase(Locale.ROOT).contains("mac");

   public WaypointListScreen(@Nullable Screen parent) {
      super(Component.translatable("diorama.waypoints.title"));
      this.parent = parent;
   }

   void showFolder(String key) {
      this.folderKey = key;
      this.selected = null;
      this.marked.clear();
      this.scroll = 0;
   }

   public void showDimension(String dimension) {
      this.shownDimension = dimension;
      this.showFolder(WaypointStore.dimensionKey(dimension));
   }

   private void collectFolders(@Nullable WaypointStore store, @Nullable String here) {
      this.folders.clear();
      LinkedHashSet<String> dimensions = new LinkedHashSet<>(List.of("minecraft:overworld", "minecraft:the_nether", "minecraft:the_end"));
      if (here != null) {
         dimensions.add(here);
      }

      if (this.shownDimension != null) {
         dimensions.add(this.shownDimension);
      }

      if (store != null) {
         for (Waypoint waypoint : store.all()) {
            if (store.folderName(waypoint.folder) == null) {
               dimensions.add(waypoint.dimension);
            }
         }
      }

      for (String dimension : dimensions) {
         this.folders.add(new WaypointListScreen.Folder(WaypointStore.dimensionKey(dimension), Waypoints.dimensionName(dimension), dimension, null));
      }

      if (store != null) {
         for (String name : store.customFolders()) {
            this.folders.add(new WaypointListScreen.Folder(WaypointStore.customKey(name), Component.literal(name), null, name));
         }

         if (!store.waystones().isEmpty()) {
            this.folders.add(new WaypointListScreen.Folder("waystones", Component.translatable("diorama.folder.waystones"), null, null));
         }
      }

      if (this.folderKey == null || this.folders.stream().noneMatch(f -> f.key().equals(this.folderKey))) {
         this.folderKey = here != null ? WaypointStore.dimensionKey(here) : this.folders.get(0).key();
         this.selected = null;
         this.marked.clear();
      }
   }

   private WaypointListScreen.Folder folder() {
      for (WaypointListScreen.Folder folder : this.folders) {
         if (folder.key().equals(this.folderKey)) {
            return folder;
         }
      }

      return this.folders.get(0);
   }

   private void openFolderMenu() {
      WaypointStore store = WaypointStore.get();
      Map<String, Integer> counts = new HashMap<>();
      if (store != null) {
         for (List<Waypoint> list : List.of(store.all(), store.waystones())) {
            for (Waypoint waypoint : list) {
               counts.merge(store.folderKey(waypoint), 1, Integer::sum);
            }
         }
      }

      List<FolderMenu.Entry> entries = new ArrayList<>();

      for (WaypointListScreen.Folder folder : this.folders) {
         entries.add(
            new FolderMenu.Entry(
               folder.key(), folder.name().getString(), counts.getOrDefault(folder.key(), 0), store != null && store.folderHidden(folder.key())
            )
         );
      }

      int left = folderNameLeft(this.width / 2 - 170);
      this.menuMoves = false;
      this.folderMenu = new FolderMenu(this.font, entries, this.folder().key(), left, 22, 42, Math.max(160, this.folderNameWidth), this.height);
   }

   private void clickFolderMenu(double x, double y, int button) {
      FolderMenu menu = this.folderMenu;
      this.folderMenu = null;
      this.pickFolder(menu != null && button == 0 ? menu.under(x, y) : null);
   }

   private void keyFolderMenu(int key) {
      FolderMenu menu = this.folderMenu;
      if (menu != null && key != 256) {
         FolderMenu.Entry picked = menu.keyPressed(key);
         if (picked != null) {
            this.folderMenu = null;
            this.pickFolder(picked);
         }
      } else {
         this.folderMenu = null;
      }
   }

   private void pickFolder(FolderMenu.@Nullable Entry picked) {
      if (this.menuMoves) {
         if (picked != null) {
            this.moveTo(picked.key());
         }
      } else {
         if (picked != null && !picked.key().equals(this.folderKey)) {
            this.showFolder(picked.key());
            this.rebuildWidgets();
         }
      }
   }

   private List<Waypoint> movable() {
      return this.marked.stream().filter(Waypoint::editable).toList();
   }

   private void openMoveMenu() {
      Button button = this.move;
      WaypointStore store = WaypointStore.get();
      List<Waypoint> moving = this.movable();
      if (button != null && store != null && !moving.isEmpty()) {
         Map<String, Integer> counts = new HashMap<>();

         for (Waypoint waypoint : store.all()) {
            counts.merge(store.folderKey(waypoint), 1, Integer::sum);
         }

         String dimension = moving.get(0).dimension;
         boolean oneDimension = moving.stream().allMatch(w -> w.dimension.equals(dimension));
         String in = store.folderKey(moving.get(0));
         boolean oneFolder = moving.stream().allMatch(w -> store.folderKey(w).equals(in));
         List<FolderMenu.Entry> entries = new ArrayList<>();
         String current = null;
         if (oneDimension) {
            String key = WaypointStore.dimensionKey(dimension);
            entries.add(new FolderMenu.Entry("", Waypoints.dimensionName(dimension).getString(), counts.getOrDefault(key, 0), store.folderHidden(key)));
            current = oneFolder && in.equals(key) ? "" : null;
         } else {
            entries.add(new FolderMenu.Entry("", Component.translatable("diorama.folder.own_dimensions").getString(), -1, false));
         }

         for (String name : store.customFolders()) {
            String key = WaypointStore.customKey(name);
            entries.add(new FolderMenu.Entry(name, name, counts.getOrDefault(key, 0), store.folderHidden(key)));
            if (oneFolder && in.equals(key)) {
               current = name;
            }
         }

         int width = 150;
         int x = Math.max(2, Math.min(button.getX(), this.width - width - 2));
         this.menuMoves = true;
         this.folderMenu = new FolderMenu(this.font, entries, current, x, button.getY(), button.getY() + button.getHeight(), width, this.height);
      }
   }

   private void moveTo(String folder) {
      WaypointStore store = WaypointStore.get();
      if (store != null) {
         for (Waypoint waypoint : this.movable()) {
            waypoint.folder = folder;
         }

         store.changed();
         this.selected = null;
         this.marked.clear();
         this.rebuildWidgets();
      }
   }

   private void stepFolder(int step) {
      int index = this.folders.indexOf(this.folder());
      this.showFolder(this.folders.get(Math.floorMod(index + step, this.folders.size())).key());
      this.rebuildWidgets();
   }

   private static int folderNameLeft(int left) {
      return left + 20 + 2;
   }

   protected void init() {
      this.all.clear();
      this.others.clear();
      WaypointStore store = WaypointStore.get();
      Entity viewer = MapSession.viewer();
      String here = this.minecraft.level == null ? null : this.minecraft.level.dimension().location().toString();
      this.collectFolders(store, here);
      if (store != null && viewer != null && here != null) {
         String key = this.folder().key();

         for (List<Waypoint> list : List.of(store.all(), store.waystones())) {
            for (Waypoint waypoint : list) {
               (store.folderKey(waypoint).equals(key) ? this.all : this.others).add(waypoint);
            }
         }

         this.resort(store);
      }

      this.empty = store == null || store.all().isEmpty() && store.customFolders().isEmpty() && store.waystones().isEmpty();
      this.waystoneChanges = Waystones.changes();
      this.hadWaystones = store != null && !store.waystones().isEmpty();
      this.folderArmed = false;
      this.folderActions.clear();
      this.folderMenu = null;
      if (this.selected != null && !this.all.contains(this.selected) && !this.others.contains(this.selected)) {
         UUID id = this.selected.id;
         this.selected = Stream.concat(this.all.stream(), this.others.stream()).filter(w -> w.id.equals(id)).findFirst().orElse(null);
      }

      if (!this.marked.isEmpty()) {
         Set<UUID> ids = new HashSet<>();
         this.marked.forEach(w -> ids.add(w.id));
         this.marked.clear();
         Stream.concat(this.all.stream(), this.others.stream()).filter(w -> ids.contains(w.id)).forEach(this.marked::add);
      }

      if (!this.xaeroRead) {
         this.xaeroRead = true;
         this.xaero = XaeroImport.find();
      }

      this.canTeleport = Waypoints.canTeleport();
      int cx = this.width / 2;
      int left = cx - 170;
      int footer = this.height - 26;
      this.addRenderableWidget(Button.builder(CommonComponents.GUI_DONE, b -> this.onClose()).bounds(cx - 100, footer, 200, 20).build());
      int above = footer;
      Button importButton = this.importButton(store);
      if (importButton != null) {
         above -= 24;
         this.addRenderableWidget(importButton);
         importButton.setRectangle(204, 20, cx - 102, above);
      }

      if (this.imported != null) {
         above -= 12;
         this.importedY = above;
      }

      this.visibleRows = Math.max(1, (above - 12 - 72 - 2) / 26);
      this.listBottom = Math.min(above - 12, Math.max(above - 18, 74 + this.visibleRows * 26 - 2));
      this.hintY = this.listBottom + 2 + (above - this.listBottom - 2 - 7) / 2;
      if (this.empty) {
         int newY = 186;
         this.emptyTop = Math.min(0, above - 6 - (newY + 20 + 4));
         this.addRenderableWidget(
            Button.builder(Component.translatable("diorama.waypoint.new"), b -> this.newWaypoint()).bounds(cx - 75, newY + this.emptyTop, 150, 20).build()
         );
         this.search = null;
         this.sortOrder = null;
         this.sortDirection = null;
         this.actions.clear();
         this.marked.clear();
         this.move = null;
         this.visibility = null;
         this.confirm = null;
      } else {
         WaypointListScreen.Folder folder = this.folder();
         int nameRight = folderNameLeft(left) + 160;
         this.addRenderableWidget(Button.builder(Component.literal("<"), b -> this.stepFolder(-1)).bounds(left, 22, 20, 20).build());
         this.nextFolder = (Button)this.addRenderableWidget(
            Button.builder(Component.literal(">"), b -> this.stepFolder(1)).bounds(nameRight + 2, 22, 20, 20).build()
         );
         boolean hidden = store != null && store.folderHidden(folder.key());
         this.folderActions.add(this.action(Component.translatable(hidden ? "diorama.waypoints.show" : "diorama.waypoints.hide"), () -> {
            WaypointStore current = WaypointStore.get();
            if (current != null) {
               current.setFolderHidden(this.folder().key(), !current.folderHidden(this.folder().key()));
               this.rebuildWidgets();
            }
         }));
         String from = folder.dimension();
         if (store != null && here != null && from != null && !from.equals(here)) {
            Button showHere = this.action(CommonComponents.optionStatus(Component.translatable("diorama.folder.show_here"), store.showsIn(here, from)), () -> {
               WaypointStore current = WaypointStore.get();
               if (current != null) {
                  current.setShownIn(here, from, !current.showsIn(here, from));
                  this.rebuildWidgets();
               }
            });
            showHere.setTooltip(
               Tooltip.create(
                  Component.translatable("diorama.folder.show_here.tooltip", new Object[]{Waypoints.dimensionName(from), Waypoints.dimensionName(here)})
               )
            );
            this.folderActions.add(showHere);
         }

         String custom = folder.custom();
         if (custom != null) {
            this.folderActions
               .add(this.action(Component.translatable("diorama.folder.rename"), () -> this.minecraft.setScreen(new WaypointFolderScreen(this, custom))));
            this.folderActions.add(this.action(Component.translatable("diorama.waypoint.delete").withStyle(ChatFormatting.RED), () -> {
               this.folderArmed = true;
               this.placeFolderButtons();
            }));
            this.folderActions
               .add((Button)this.addRenderableWidget(Button.builder(Component.translatable("diorama.menu.delete_confirm").withStyle(ChatFormatting.RED), b -> {
                  WaypointStore current = WaypointStore.get();
                  if (this.folderArmed && current != null) {
                     current.deleteFolder(custom);
                     this.rebuildWidgets();
                  }
               }).size(20, 20).build()));
         }

         this.placeFolderButtons();
         Component newWaypoint = Component.translatable("diorama.waypoint.new");
         Component newFolder = Component.translatable("diorama.folder.new");
         int newWidth = width(this.font.width(newWaypoint));
         int newFolderWidth = width(this.font.width(newFolder));
         int sortWidth = 0;

         for (WaypointSort sort : WaypointSort.values()) {
            sortWidth = Math.max(sortWidth, width(this.font.width(Component.translatable(sort.key()))));
         }

         int x = left + 340 - newWidth;
         this.addRenderableWidget(Button.builder(newWaypoint, b -> this.newWaypoint()).bounds(x, 46, newWidth, 20).build());
         x -= 4 + newFolderWidth;
         this.addRenderableWidget(
            Button.builder(newFolder, b -> this.minecraft.setScreen(new WaypointFolderScreen(this, null))).bounds(x, 46, newFolderWidth, 20).build()
         );
         x -= 24;
         this.sortDirection = (Button)this.addRenderableWidget(
            Button.builder(Component.empty(), b -> this.changeSort(false))
               .createNarration(n -> Component.translatable(this.sortDirectionKey()))
               .bounds(x, 46, 20, 20)
               .build()
         );
         x -= 2 + sortWidth;
         this.sortOrder = (Button)this.addRenderableWidget(Button.builder(Component.empty(), b -> this.changeSort(true)).bounds(x, 46, sortWidth, 20).build());
         this.labelSort(store);
         this.searchWidth = x - 4 - left;
         String query = this.search != null ? this.search.getValue() : "";
         EditBox box = new EditBox(this.font, left + 20, 52, this.searchWidth - 24, 12, Component.translatable("diorama.waypoints.search"));
         box.setBordered(false);
         box.setMaxLength(32);
         box.setValue(query);
         box.setResponder(text -> this.filter());
         this.search = (EditBox)this.addRenderableWidget(box);
         this.actions.clear();
         this.edit = this.action(Component.translatable("diorama.waypoints.edit"), () -> this.edit(this.selected));
         this.actions.add(this.edit);
         this.move = this.action(Component.translatable("diorama.waypoints.move"), this::openMoveMenu);
         this.actions.add(this.move);
         this.visibility = this.action(Component.translatable("diorama.waypoints.hide"), () -> this.toggleVisible(this.selected));
         this.actions.add(this.visibility);
         this.teleport = null;
         if (this.canTeleport) {
            this.teleport = this.action(Component.translatable("diorama.waypoint.teleport"), () -> this.teleport(this.selected));
            this.actions.add(this.teleport);
         }

         this.delete = this.action(Component.translatable("diorama.waypoint.delete").withStyle(ChatFormatting.RED), () -> {
            this.armed = true;
            this.placeRowButtons();
         });
         this.actions.add(this.delete);
         this.confirm = (Button)this.addRenderableWidget(
            Button.builder(Component.translatable("diorama.menu.delete_confirm").withStyle(ChatFormatting.RED), b -> {
               if (this.armed) {
                  this.delete(this.selected);
               }
            }).size(20, 20).build()
         );
         this.filter();
         this.setInitialFocus(box);
      }
   }

   private void changeSort(boolean nextOrder) {
      WaypointStore store = WaypointStore.get();
      if (store != null) {
         if (nextOrder) {
            store.setSort(store.sort().next(), store.sortReversed());
         } else {
            store.setSort(store.sort(), !store.sortReversed());
         }

         this.labelSort(store);
         this.resort(store);
         this.scroll = 0;
         this.filter();
      }
   }

   private void resort(WaypointStore store) {
      Entity viewer = MapSession.viewer();
      if (viewer != null && this.minecraft.level != null) {
         String here = this.minecraft.level.dimension().location().toString();
         Comparator<Waypoint> order = store.sort().comparator(store.sortReversed(), w -> this.distance(store, here, viewer, w));
         this.all.sort(order);
         this.others.sort(order);
      }
   }

   private double distance(WaypointStore store, String here, Entity viewer, Waypoint waypoint) {
      if (waypoint.dimension.equals(here)) {
         return waypoint.distanceTo(viewer.getX(), viewer.getY(), viewer.getZ());
      }

      if (!store.showsIn(here, waypoint.dimension)) {
         return Double.NaN;
      }

      double divider = store.coordinateScale(here) / store.coordinateScale(waypoint.dimension);
      return waypoint.scaled(divider, this.spotHere).distanceTo(viewer.getX(), viewer.getY(), viewer.getZ());
   }

   private void labelSort(@Nullable WaypointStore store) {
      Button order = this.sortOrder;
      Button direction = this.sortDirection;
      if (order != null && direction != null) {
         WaypointSort sort = store == null ? WaypointSort.DISTANCE : store.sort();
         boolean reversed = store != null && store.sortReversed();
         Component name = Component.translatable(sort.key());
         order.setMessage(name);
         this.sortReversed = reversed;
         Tooltip tooltip = Tooltip.create(
            Component.translatable("diorama.waypoints.sort", new Object[]{name})
               .append("\n")
               .append(Component.translatable(sort.directionKey(reversed)).withStyle(ChatFormatting.GRAY))
         );
         order.setTooltip(tooltip);
         direction.setTooltip(tooltip);
      }
   }

   private String sortDirectionKey() {
      WaypointStore store = WaypointStore.get();
      return (store == null ? WaypointSort.DISTANCE : store.sort()).directionKey(this.sortReversed);
   }

   private void drawSortCaret(GuiGraphics graphics) {
      Button button = this.sortDirection;
      if (button != null && button.visible) {
         int cx = button.getX() + button.getWidth() / 2;
         int y = button.getY() + (button.getHeight() - 3) / 2;

         for (int row = 0; row < 3; row++) {
            int half = this.sortReversed ? 2 - row : row;
            graphics.fill(cx - half, y + row, cx + half + 1, y + row + 1, -1);
         }
      }
   }

   private void filter() {
      String query = this.query();
      this.rows.clear();
      this.otherStart = -1;
      WaypointStore store = WaypointStore.get();
      boolean whole = !query.isEmpty() && this.folder().name().getString().toLowerCase(Locale.ROOT).contains(query);

      for (Waypoint waypoint : this.all) {
         if (query.isEmpty() || whole || waypoint.shownName().toLowerCase(Locale.ROOT).contains(query)) {
            this.rows.add(waypoint);
         }
      }

      if (!query.isEmpty() && store != null) {
         int start = this.rows.size();
         Map<String, Boolean> named = new HashMap<>();

         for (Waypoint waypoint : this.others) {
            boolean inNamed = named.computeIfAbsent(store.folderKey(waypoint), key -> this.folderName(key).toLowerCase(Locale.ROOT).contains(query));
            if (inNamed || waypoint.shownName().toLowerCase(Locale.ROOT).contains(query)) {
               this.rows.add(waypoint);
            }
         }

         if (this.rows.size() > start) {
            this.otherStart = start;
         }
      }

      if (this.marked.retainAll(new HashSet<>(this.rows))) {
         this.armed = false;
      }

      if (this.selected != null && !this.rows.contains(this.selected)) {
         this.selected = null;

         for (Waypoint waypoint : this.marked) {
            this.selected = waypoint;
         }

         this.armed = false;
      }

      if (this.marked.size() < 2) {
         this.marked.clear();
      }

      this.scrollTo(this.scroll);
   }

   private String query() {
      return this.search != null ? this.search.getValue().trim().toLowerCase(Locale.ROOT) : "";
   }

   private String folderName(String key) {
      for (WaypointListScreen.Folder folder : this.folders) {
         if (folder.key().equals(key)) {
            return folder.name().getString();
         }
      }

      return "";
   }

   private int lines() {
      return this.rows.size() + (this.otherStart >= 0 ? 1 : 0);
   }

   private int lineOf(int row) {
      return this.otherStart >= 0 && row >= this.otherStart ? row + 1 : row;
   }

   private int rowOn(int line) {
      if (this.otherStart >= 0 && line >= this.otherStart) {
         return line == this.otherStart ? -1 : line - 1;
      } else {
         return line;
      }
   }

   private int maxScroll() {
      return Math.max(0, this.lines() - this.visibleRows);
   }

   private void scrollTo(int scroll) {
      this.scroll = Math.max(0, Math.min(scroll, this.maxScroll()));
      this.placeRowButtons();
   }

   private void select(@Nullable Waypoint waypoint) {
      this.selected = waypoint;
      this.marked.clear();
      this.armed = false;
      this.placeRowButtons();
   }

   private void placeRowButtons() {
      int row = this.selected == null ? -1 : this.rows.indexOf(this.selected);
      int index = row < 0 ? -1 : this.lineOf(row) - this.scroll;
      boolean shown = index >= 0 && index < this.visibleRows;
      Button visibility = this.visibility;
      boolean several = this.marked.size() > 1;
      if (shown && visibility != null) {
         boolean hide = several ? this.marked.stream().anyMatch(w -> w.visible) : this.selected.visible;
         Component label = Component.translatable(hide ? "diorama.waypoints.hide" : "diorama.waypoints.show");
         visibility.setMessage(label);
         visibility.setWidth(width(this.font.width(label)));
      }

      List<Button> buttons = this.rowActions();
      int span = -2;

      for (Button button : buttons) {
         span += button.getWidth() + 2;
      }

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

      Button teleport = this.teleport;
      if (shown && teleport != null && buttons.contains(teleport)) {
         teleport.active = !this.armed && Waypoints.canTeleport(this.selected.dimension);
      }

      Button confirm = this.confirm;
      if (confirm != null) {
         show(confirm, shown && this.armed);
         Component ask = several
            ? Component.translatable("diorama.waypoints.delete_many", new Object[]{this.movable().size()}).withStyle(ChatFormatting.RED)
            : Component.translatable("diorama.menu.delete_confirm").withStyle(ChatFormatting.RED);
         confirm.setMessage(ask);
         int wide = several ? Math.max(span, this.font.width(ask) + 10) : span;
         confirm.setRectangle(wide, 20, right - wide, y);
      }
   }

   private void placeFolderButtons() {
      boolean custom = this.folder().custom() != null;
      List<Button> buttons = custom ? this.folderActions.subList(0, this.folderActions.size() - 1) : this.folderActions;
      int span = -2;

      for (Button button : buttons) {
         span += button.getWidth() + 2;
      }

      int right = this.width / 2 + 170;
      int x = right - span;
      int nameLeft = folderNameLeft(right - 340);
      this.folderNameWidth = Math.max(60, Math.min(160, x - 6 - 20 - 2 - nameLeft));
      if (this.nextFolder != null) {
         this.nextFolder.setPosition(nameLeft + this.folderNameWidth + 2, 22);
      }

      for (Button button : buttons) {
         show(button, !this.folderArmed);
         button.setPosition(x, 22);
         x += button.getWidth() + 2;
      }

      if (custom) {
         Button confirm = this.folderActions.get(this.folderActions.size() - 1);
         show(confirm, this.folderArmed);
         confirm.setRectangle(span, 20, right - span, 22);
      }
   }

   private static void show(Button button, boolean shown) {
      button.visible = shown;
      button.active = shown;
   }

   private Button action(Component label, Runnable onPress) {
      return (Button)this.addRenderableWidget(Button.builder(label, b -> onPress.run()).size(width(this.font.width(label)), 20).build());
   }

   private static int width(int label) {
      return Math.max(32, label + 10);
   }

   private List<Button> rowActions() {
      List<Button> buttons = new ArrayList<>(this.actions);
      if (this.marked.size() > 1) {
         buttons.remove(this.edit);
         buttons.remove(this.teleport);
         if (this.movable().isEmpty()) {
            buttons.remove(this.move);
            buttons.remove(this.delete);
         }

         return buttons;
      } else {
         buttons.remove(this.move);
         if (this.selected != null && !this.selected.editable()) {
            buttons.remove(this.edit);
            buttons.remove(this.delete);
         }

         return buttons;
      }
   }

   private int actionsWidth() {
      int span = 0;

      for (Button button : this.rowActions()) {
         span += button.getWidth() + 2;
      }

      Button confirm = this.confirm;
      if (this.armed && confirm != null) {
         span = Math.max(span, confirm.getWidth() + 2);
      }

      return span;
   }

   private static float badgeX(Waypoint waypoint) {
      int width = Pins.badgeWidth(waypoint, 15);
      return width > 15 ? 4.0F + width / 2.0F : 11.5F;
   }

   private int rowY(int index) {
      return 74 + index * 26;
   }

   private int rowAt(double x, double y) {
      int left = this.width / 2 - 170;
      if (!(x < left) && !(x >= left + 340) && !(y < 72.0) && !(y >= this.listBottom)) {
         int index = (int)((y - 72.0 - 2.0) / 26.0);
         return index >= 0 && index < this.visibleRows && this.scroll + index < this.lines() && !(y >= this.rowY(index) + 24)
            ? this.rowOn(this.scroll + index)
            : -1;
      } else {
         return -1;
      }
   }

   public void renderBackground(GuiGraphics graphics, int mouseX, int mouseY, float a) {
      super.renderBackground(graphics, mouseX, mouseY, a);
      if (this.empty) {
         this.drawEmpty(graphics);
      } else {
         this.drawList(graphics, mouseX, mouseY);
      }
   }

   private void drawList(GuiGraphics graphics, int mouseX, int mouseY) {
      int cx = this.width / 2;
      int left = cx - 170;
      int right = left + 340;
      graphics.drawCenteredString(this.font, this.title, cx, 8, -1);
      int nameLeft = folderNameLeft(left);
      int nameRight = nameLeft + this.folderNameWidth;
      ScreenArt.sprite(graphics, ScreenArt.TEXT_FIELD, nameLeft, 22, nameRight - nameLeft, 20);
      WaypointStore store = WaypointStore.get();
      WaypointListScreen.Folder folder = this.folder();
      boolean folderHidden = store != null && store.folderHidden(folder.key());
      String label = this.fit(folder.name().getString() + " (" + this.all.size() + ")", nameRight - nameLeft - 28);
      graphics.drawCenteredString(this.font, Component.literal(label), (nameLeft + nameRight) / 2, 28, folderHidden ? -8355712 : -1);

      for (int row = 0; row < 3; row++) {
         graphics.fill(nameRight - 12 + row, 31 + row, nameRight - 7 - row, 32 + row, -7303024);
      }

      EditBox box = this.search;
      if (box != null) {
         ScreenArt.sprite(graphics, box.isFocused() ? ScreenArt.TEXT_FIELD_FOCUSED : ScreenArt.TEXT_FIELD, left, 46, this.searchWidth, 20);
         ScreenArt.sprite(graphics, ScreenArt.SEARCH, left + 4, 50, 12, 12);
         if (box.getValue().isEmpty()) {
            graphics.drawString(
               this.font, this.fit(Component.translatable("diorama.waypoints.search").getString(), this.searchWidth - 24), left + 20, 52, -9408400, false
            );
         }
      }

      ScreenArt.list(graphics, 0, 72, this.width, this.listBottom);
      Entity viewer = MapSession.viewer();
      if (this.rows.isEmpty()) {
         graphics.drawCenteredString(
            this.font,
            Component.translatable(this.all.isEmpty() && this.query().isEmpty() ? "diorama.folder.empty" : "diorama.waypoints.no_match"),
            cx,
            92,
            -6250336
         );
      }

      String here = this.minecraft.level == null ? "" : this.minecraft.level.dimension().location().toString();
      int textLeft = 24;

      for (int i = 0; i < this.visibleRows && this.scroll + i < this.lines(); i++) {
         int row = this.rowOn(this.scroll + i);
         if (row >= 0) {
            textLeft = Math.max(textLeft, (int)Math.ceil(badgeX(this.rows.get(row)) + Pins.badgeWidth(this.rows.get(row), 15) / 2.0F) + 2);
         }
      }

      for (int i = 0; i < this.visibleRows && this.scroll + i < this.lines(); i++) {
         int row = this.rowOn(this.scroll + i);
         int y = this.rowY(i);
         if (row < 0) {
            graphics.drawCenteredString(this.font, Component.translatable("diorama.waypoints.other_folders"), cx, y + 8, -6250336);
            graphics.fill(left, y + 24 - 3, right, y + 24 - 2, -10461088);
         } else {
            Waypoint waypoint = this.rows.get(row);
            boolean other = this.otherStart >= 0 && row >= this.otherStart;
            boolean inHidden = other ? store != null && store.folderHidden(store.folderKey(waypoint)) : folderHidden;
            boolean selected = waypoint == this.selected;
            if (selected || this.marked.contains(waypoint)) {
               ScreenArt.selection(graphics, left, y, right, y + 24);
            }

            Pins.badge(graphics, waypoint, left + badgeX(waypoint), y + 12.5F, 15);
            int room = right - (selected ? this.actionsWidth() + 4 : 6) - (left + textLeft);
            String full = waypoint.shownName();
            String name = this.fit(full, room);
            graphics.drawString(this.font, name, left + textLeft, y + 4, waypoint.visible && !inHidden ? -1 : -8355712, true);
            String detail = waypoint.x + ", " + waypoint.y + ", " + waypoint.z;
            if (waypoint.dimension.equals(here)) {
               if (viewer != null) {
                  detail = Math.round(waypoint.distanceTo(viewer.getX(), viewer.getY(), viewer.getZ())) + " m · " + detail;
               }
            } else {
               detail = Waypoints.dimensionName(waypoint.dimension).getString() + " · " + detail;
               double distance = viewer != null && store != null ? this.distance(store, here, viewer, waypoint) : Double.NaN;
               if (!Double.isNaN(distance)) {
                  detail = Math.round(distance) + " m · " + detail;
               }
            }

            if (other && store != null) {
               String key = store.folderKey(waypoint);
               if (waypoint.dimension.equals(here) || !key.equals(WaypointStore.dimensionKey(waypoint.dimension))) {
                  detail = this.folderName(key) + " · " + detail;
               }
            }

            if (!waypoint.visible) {
               detail = detail + "  ·  " + Component.translatable("diorama.waypoint.hidden").getString();
            }

            String shownDetail = this.fit(detail, room);
            graphics.drawString(this.font, shownDetail, left + textLeft, y + 14, -7303024, true);
            boolean overText = mouseX >= left + textLeft && mouseX < left + textLeft + room && mouseY >= y && mouseY < y + 24;
            if (overText && this.folderMenu == null && (!name.equals(full) || !shownDetail.equals(detail))) {
               this.setTooltipForNextRenderPass(List.of(Component.literal(full).getVisualOrderText(), Component.literal(detail).getVisualOrderText()));
            }
         }
      }

      if (this.maxScroll() > 0) {
         int[] bar = this.scrollbar();
         ScreenArt.scrollbar(graphics, bar[0], 72, this.listBottom, bar[1], bar[2]);
      }

      if (this.imported != null) {
         graphics.drawCenteredString(this.font, this.imported, cx, this.importedY, -6233952);
      }

      String hint = Component.translatable("diorama.waypoints.select_hint", new Object[]{MAC ? "Cmd" : "Ctrl"}).getString();
      graphics.drawCenteredString(this.font, Component.literal(this.fit(hint, this.width - 8)), cx, this.hintY, -6250336);
   }

   private int[] scrollbar() {
      int track = this.listBottom - 72;
      int handle = Math.max(32, Math.min(track - 8, track * this.visibleRows / Math.max(1, this.lines())));
      int top = this.maxScroll() == 0 ? 0 : (track - handle) * this.scroll / this.maxScroll();
      return new int[]{this.width / 2 + 170 + 4, handle, top};
   }

   private void drawEmpty(GuiGraphics graphics) {
      int cx = this.width / 2;
      int top = this.emptyTop;
      graphics.drawCenteredString(this.font, this.title, cx, 16 + Math.max(top, -8), -1);
      int pieceX = cx - this.piece.outerWidth() / 2;
      int pieceY = 52 + top;
      this.piece.draw(graphics, pieceX, pieceY);
      graphics.pose().pushPose();
      graphics.pose().translate(pieceX + this.piece.tipX(), pieceY + this.piece.tipY(), 0.0F);
      graphics.pose().scale(2.0F, 2.0F, 1.0F);
      Pins.standing(graphics, this.sample, 0.0F, 0.0F, 15);
      graphics.pose().popPose();
      graphics.drawCenteredString(this.font, Component.translatable("diorama.waypoints.empty_title"), cx, 142 + top, -1);
      Component how = DioramaClient.NEW_WAYPOINT.isUnbound()
         ? Component.translatable("diorama.waypoints.empty_unbound")
         : Component.translatable("diorama.waypoints.empty", new Object[]{DioramaClient.NEW_WAYPOINT.getTranslatedKeyMessage()});
      graphics.drawCenteredString(this.font, how, cx, 158 + top, -6250336);
      graphics.drawCenteredString(
         this.font,
         Component.translatable("diorama.waypoints.empty_map", new Object[]{Component.translatable("diorama.menu.add_waypoint")}),
         cx,
         168 + top,
         -6250336
      );
      if (this.imported != null) {
         graphics.drawCenteredString(this.font, this.imported, cx, this.importedY, -6233952);
      }
   }

   private @Nullable Button importButton(@Nullable WaypointStore store) {
      XaeroImport.Found found = this.xaero;
      if (found != null && store != null) {
         XaeroImport.World chosen = found.chosen();
         if (chosen != null) {
            int count = XaeroImport.missing(chosen, store).size();
            return count == 0 ? null : Button.builder(Component.translatable("diorama.xaero.import", new Object[]{count}), b -> {
               this.importFrom(chosen);
               this.rebuildWidgets();
            }).build();
         } else {
            return found.worlds().stream().allMatch(world -> XaeroImport.missing(world, store).isEmpty())
               ? null
               : Button.builder(Component.translatable("diorama.xaero.import_pick"), b -> this.minecraft.setScreen(new XaeroWorldScreen(this, found.worlds())))
                  .build();
         }
      } else {
         return null;
      }
   }

   @Nullable Component imported() {
      return this.imported;
   }

   void importFrom(XaeroImport.World world) {
      WaypointStore store = WaypointStore.get();
      if (store != null && this.minecraft.level != null) {
         List<Waypoint> added = XaeroImport.importInto(world, store);
         String here = this.minecraft.level.dimension().location().toString();
         long elsewhere = added.stream().filter(w -> !w.dimension.equals(here)).count();
         Component count = added.size() == 1
            ? Component.translatable("diorama.xaero.count_one")
            : Component.translatable("diorama.xaero.count", new Object[]{added.size()});
         this.imported = elsewhere == 0L
            ? Component.translatable("diorama.xaero.imported", new Object[]{count})
            : Component.translatable("diorama.xaero.imported_elsewhere", new Object[]{count, elsewhere});
      }
   }

   private String fit(String text, int width) {
      if (this.font.width(text) <= width) {
         return text;
      }

      String ellipsis = CommonComponents.ELLIPSIS.getString();
      return this.font.plainSubstrByWidth(text, Math.max(0, width - this.font.width(ellipsis))) + ellipsis;
   }

   public boolean mouseScrolled(double x, double y, double scrollX, double scrollY) {
      if (this.folderMenu != null) {
         this.folderMenu.scroll(scrollY);
         return true;
      } else {
         this.scrollTo(this.scroll - (int)Math.signum(scrollY));
         return true;
      }
   }

   private static boolean usedByList(int button) {
      return button == 0 || button == 1;
   }

   public boolean keyPressed(int key, int scancode, int modifiers) {
      if (this.folderMenu != null) {
         this.keyFolderMenu(key);
         return true;
      } else if (DioramaClient.NEW_WAYPOINT.matches(key, scancode) && !(this.getFocused() instanceof EditBox)) {
         this.newWaypointPressed = true;
         return true;
      } else {
         return super.keyPressed(key, scancode, modifiers);
      }
   }

   public boolean mouseClicked(double x, double y, int button) {
      if (this.folderMenu != null) {
         this.clickFolderMenu(x, y, button);
         return true;
      } else {
         this.shiftClick = Screen.hasShiftDown();
         this.controlClick = Screen.hasControlDown();
         this.disarmUnless(x, y);
         if (!usedByList(button) && DioramaClient.NEW_WAYPOINT.matchesMouse(button)) {
            this.newWaypointPressed = true;
            return true;
         } else if (super.mouseClicked(x, y, button)) {
            return true;
         } else if (DioramaClient.NEW_WAYPOINT.matchesMouse(button)) {
            this.newWaypointPressed = true;
            return true;
         } else {
            return this.clicked(x, y, button);
         }
      }
   }

   public boolean mouseDragged(double x, double y, int button, double dx, double dy) {
      return this.dragged(y) || super.mouseDragged(x, y, button, dx, dy);
   }

   public boolean mouseReleased(double x, double y, int button) {
      this.draggingScrollbar = false;
      return super.mouseReleased(x, y, button);
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
      if (button == 0 && !this.empty) {
         int left = this.width / 2 - 170;
         if (x >= folderNameLeft(left) && x < folderNameLeft(left) + this.folderNameWidth && y >= 22.0 && y < 42.0) {
            this.setFocused(null);
            this.openFolderMenu();
            return true;
         }

         EditBox box = this.search;
         if (box != null && x >= left && x < left + this.searchWidth && y >= 46.0 && y < 66.0) {
            this.setFocused(box);
            return true;
         }

         int[] bar = this.scrollbar();
         if (this.maxScroll() > 0 && x >= bar[0] && x < bar[0] + 6 && y >= 72.0 && y < this.listBottom) {
            this.draggingScrollbar = true;
            this.dragged(y);
            return true;
         }

         int row = this.rowAt(x, y);
         if (row < 0) {
            return false;
         }

         Waypoint waypoint = this.rows.get(row);
         long now = System.currentTimeMillis();
         boolean twice = waypoint == this.lastClicked && now - this.lastClick < 250L;
         this.lastClicked = waypoint;
         this.lastClick = now;
         this.setFocused(null);
         boolean shift = this.shiftClick;
         if (!shift && !this.controlClick) {
            this.anchor = waypoint;
            if (twice) {
               this.edit(waypoint);
            } else if (waypoint != this.selected || !this.marked.isEmpty()) {
               this.select(waypoint);
            }

            return true;
         } else {
            this.lastClicked = null;
            this.selectMore(waypoint, row, shift);
            return true;
         }
      } else {
         return false;
      }
   }

   private void selectMore(Waypoint waypoint, int row, boolean shift) {
      this.armed = false;
      int from = shift && this.anchor != null ? this.rows.indexOf(this.anchor) : -1;
      if (from >= 0) {
         this.marked.clear();

         for (int i = Math.min(from, row); i <= Math.max(from, row); i++) {
            this.marked.add(this.rows.get(i));
         }

         this.selected = waypoint;
      } else if (waypoint != this.selected && !this.marked.contains(waypoint)) {
         this.anchor = waypoint;
         if (this.selected != null) {
            this.marked.add(this.selected);
         }

         this.marked.add(waypoint);
         this.selected = waypoint;
      } else {
         this.anchor = waypoint;
         this.marked.remove(waypoint);
         if (waypoint == this.selected) {
            this.selected = null;

            for (Waypoint left : this.marked) {
               this.selected = left;
            }
         }
      }

      if (this.marked.size() < 2) {
         this.marked.clear();
      }

      this.placeRowButtons();
   }

   private boolean dragged(double y) {
      if (!this.draggingScrollbar) {
         return false;
      }

      int[] bar = this.scrollbar();
      int track = this.listBottom - 72 - bar[1];
      double t = track <= 0 ? 0.0 : (y - 72.0 - bar[1] / 2.0) / track;
      this.scrollTo((int)Math.round(t * this.maxScroll()));
      return true;
   }

   public void tick() {
      int changes = Waystones.changes();
      if (changes != this.waystoneChanges) {
         this.waystoneChanges = changes;
         this.folderMenu = null;
         WaypointStore store = WaypointStore.get();
         boolean hasWaystones = store != null && !store.waystones().isEmpty();
         if (hasWaystones != this.hadWaystones || "waystones".equals(this.folderKey) || !this.query().isEmpty()) {
            this.rebuildWidgets();
         } else if (store != null) {
            this.others.removeIf(w -> w.kind == Waypoint.Kind.WAYSTONE);
            this.others.addAll(store.waystones());
            this.resort(store);
         }
      }

      if (this.newWaypointPressed) {
         this.newWaypointPressed = false;
         this.newWaypoint();
      }
   }

   private void newWaypoint() {
      Waypoint fresh = Waypoints.atPlayer();
      if (fresh != null) {
         String custom = this.folders.isEmpty() ? null : this.folder().custom();
         if (custom != null) {
            fresh.folder = custom;
         }

         this.minecraft.setScreen(new WaypointEditScreen(this, fresh, true));
      }
   }

   private void edit(@Nullable Waypoint waypoint) {
      if (waypoint != null && waypoint.editable()) {
         this.minecraft.setScreen(new WaypointEditScreen(this, waypoint, false));
      }
   }

   private void toggleVisible(@Nullable Waypoint waypoint) {
      if (waypoint != null) {
         WaypointStore store = WaypointStore.get();
         if (store != null) {
            if (this.marked.size() > 1) {
               boolean hide = this.marked.stream().anyMatch(w -> w.visible);

               for (Waypoint each : List.copyOf(this.marked)) {
                  if (each.visible == hide) {
                     store.setVisible(each, !hide);
                  }
               }
            } else {
               store.setVisible(waypoint, !waypoint.visible);
            }

            this.resort(store);
            this.filter();
            int index = this.rows.indexOf(waypoint);
            index = index < 0 ? -1 : this.lineOf(index);
            if (index >= 0 && index < this.scroll) {
               this.scrollTo(index);
            } else if (index >= this.scroll + this.visibleRows) {
               this.scrollTo(index - this.visibleRows + 1);
            }
         }
      }
   }

   private void delete(@Nullable Waypoint waypoint) {
      WaypointStore store = WaypointStore.get();
      if (store != null && this.marked.size() > 1) {
         this.movable().forEach(store::remove);
      } else if (store != null && waypoint != null) {
         store.remove(waypoint);
      }

      this.selected = null;
      this.marked.clear();
      this.armed = false;
      this.rebuildWidgets();
   }

   private void teleport(@Nullable Waypoint waypoint) {
      if (waypoint != null) {
         Waypoints.teleport(waypoint.dimension, waypoint.x, waypoint.y, waypoint.z);
      }

      this.minecraft.setScreen(null);
   }

   public void render(GuiGraphics graphics, int mouseX, int mouseY, float a) {
      boolean overMenu = this.folderMenu != null && this.folderMenu.contains(mouseX, mouseY);
      super.render(graphics, overMenu ? -1 : mouseX, overMenu ? -1 : mouseY, a);
      this.drawSortCaret(graphics);
      this.drawFolderMenu(graphics, mouseX, mouseY);
   }

   private void drawFolderMenu(GuiGraphics graphics, int mouseX, int mouseY) {
      FolderMenu menu = this.folderMenu;
      if (menu != null) {
         graphics.flush();
         PoseStack pose = graphics.pose();
         pose.pushPose();
         pose.translate(0.0F, 0.0F, 400.0F);
         menu.draw(graphics, mouseX, mouseY);
         pose.popPose();
      }
   }

   public @Nullable FolderMenu folderMenu() {
      return this.folderMenu;
   }

   public int[] folderNameCentre() {
      return new int[]{folderNameLeft(this.width / 2 - 170) + this.folderNameWidth / 2, 32};
   }

   public @Nullable Button sortDirection() {
      return this.sortDirection;
   }

   public @Nullable Waypoint selected() {
      return this.selected;
   }

   public List<Waypoint> selection() {
      return !this.marked.isEmpty() ? List.copyOf(this.marked) : (this.selected == null ? List.of() : List.of(this.selected));
   }

   public String folderName() {
      return this.folders.isEmpty() ? "" : this.folder().name().getString();
   }

   public int shownInFolder() {
      return this.otherStart < 0 ? this.rows.size() : this.otherStart;
   }

   public List<Waypoint> shown() {
      return List.copyOf(this.rows);
   }

   public int @Nullable [] rowCentre(Waypoint waypoint) {
      int row = this.rows.indexOf(waypoint);
      int index = row < 0 ? -1 : this.lineOf(row) - this.scroll;
      return index >= 0 && index < this.visibleRows ? new int[]{this.width / 2 - 170 + 24 + 20, this.rowY(index) + 12} : null;
   }

   public void removed() {
      this.piece.close();
      super.removed();
   }

   public void onClose() {
      this.minecraft.setScreen(this.parent);
   }

   public boolean isPauseScreen() {
      return false;
   }

   private record Folder(String key, Component name, @Nullable String dimension, @Nullable String custom) {
   }
}
