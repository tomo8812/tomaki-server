package com.archiangel.quadhotbar;

import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.neoforge.common.ModConfigSpec;
import net.neoforged.neoforge.common.ModConfigSpec.BooleanValue;
import net.neoforged.neoforge.common.ModConfigSpec.Builder;
import net.neoforged.neoforge.common.ModConfigSpec.EnumValue;
import net.neoforged.neoforge.common.ModConfigSpec.IntValue;

public final class QuadHotbarConfig {
   public static final int DEFAULT_OVERVIEW_SCROLL_STEP = 20;
   public static final int MAX_OVERVIEW_SCROLL_STEP = 180;
   private static final Builder BUILDER = new Builder();
   private static final QuadHotbarConfig.Profile PREFERENCES = new QuadHotbarConfig.Profile();
   private static boolean multiplayerContext;
   public static final ModConfigSpec SPEC = BUILDER.build();
   public static int hotbarRows = 2;
   public static boolean enabled = true;
   public static boolean scrollBetweenHotbars = true;
   public static boolean showAllKeybinds = false;
   public static QuadHotbarLayout.InventoryOrder inventoryRowOrder = QuadHotbarLayout.InventoryOrder.TOP_TO_BOTTOM;
   public static QuadHotbarLayout.MainCorner mainHotbarCorner = QuadHotbarLayout.MainCorner.BOTTOM_LEFT;
   public static boolean experimentsEnabled = false;
   public static int inventoryPages = 1;
   public static boolean showPageButtons = true;
   public static boolean alwaysShowPageLabels = false;
   public static boolean overviewEnabled = true;
   public static boolean showOverviewButton = true;
   public static boolean autoOpenOverview = true;
   public static boolean overviewLastOpen = true;
   public static boolean standaloneOverview = false;
   public static int overviewWidth = 9;
   public static boolean overviewFirstPageRight = false;
   public static boolean overviewHorizontal = true;
   public static int overviewHeight = 9;
   public static int overviewScrollStep = 20;
   public static boolean overviewDraggable = true;
   public static int overviewX = -1;
   public static int overviewY = -1;

   public static void setMultiplayerContext(boolean multiplayer) {
      multiplayerContext = multiplayer;
   }

   public static boolean multiplayerContext() {
      return multiplayerContext;
   }

   public static void setOverviewEnabled(boolean value) {
      PREFERENCES.OVERVIEW_ENABLED.set(value);
      overviewEnabled = value;
   }

   public static void setShowOverviewButton(boolean value) {
      PREFERENCES.SHOW_OVERVIEW_BUTTON.set(value);
      showOverviewButton = value;
   }

   public static void setAutoOpenOverview(boolean value) {
      if (value && !autoOpenOverview) {
         setOverviewLastOpen(true);
      }

      PREFERENCES.AUTO_OPEN_OVERVIEW.set(value);
      autoOpenOverview = value;
   }

   public static void setOverviewLastOpen(boolean value) {
      PREFERENCES.OVERVIEW_LAST_OPEN.set(value);
      overviewLastOpen = value;
   }

   public static void setStandaloneOverview(boolean value) {
      PREFERENCES.STANDALONE_OVERVIEW.set(value);
      standaloneOverview = value;
   }

   public static void setOverviewWidth(int value) {
      overviewWidth = QuadHotbarOverviewLayout.normalizeWidth(value);
      PREFERENCES.OVERVIEW_WIDTH.set(overviewWidth);
   }

   public static void setOverviewFirstPageRight(boolean value) {
      overviewFirstPageRight = value;
      PREFERENCES.OVERVIEW_FIRST_PAGE_RIGHT.set(value);
   }

   public static void setOverviewHorizontal(boolean value) {
      overviewHorizontal = value;
      PREFERENCES.OVERVIEW_HORIZONTAL.set(value);
   }

   public static void setOverviewHeight(int value) {
      overviewHeight = Math.max(1, Math.min(50, value));
      PREFERENCES.OVERVIEW_HEIGHT.set(overviewHeight);
   }

   public static void setOverviewScrollStep(int value) {
      overviewScrollStep = Math.max(1, Math.min(180, value));
      PREFERENCES.OVERVIEW_SCROLL_STEP.set(overviewScrollStep);
   }

   public static void setOverviewDraggable(boolean value) {
      overviewDraggable = value;
      PREFERENCES.OVERVIEW_DRAGGABLE.set(value);
   }

   public static void setOverviewPosition(int x, int y) {
      overviewX = Math.max(-1, Math.min(10000, x));
      overviewY = Math.max(-1, Math.min(10000, y));
      PREFERENCES.OVERVIEW_X.set(overviewX);
      PREFERENCES.OVERVIEW_Y.set(overviewY);
   }

   private QuadHotbarConfig() {
   }

   public static void setHotbarRows(int rows) {
      PREFERENCES.HOTBAR_ROWS.set(Math.max(2, Math.min(experimentsEnabled ? 8 : 4, rows)));
      hotbarRows = (Integer)PREFERENCES.HOTBAR_ROWS.get();
      if (experimentsEnabled) {
         setInventoryPages(inventoryPages);
      }
   }

   public static void setEnabled(boolean value) {
      PREFERENCES.ENABLED.set(value);
      enabled = (Boolean)PREFERENCES.ENABLED.get();
   }

   public static void setScrollBetweenHotbars(boolean value) {
      PREFERENCES.SCROLL_BETWEEN_HOTBARS.set(value);
      scrollBetweenHotbars = (Boolean)PREFERENCES.SCROLL_BETWEEN_HOTBARS.get();
   }

   public static void setShowAllKeybinds(boolean value) {
      PREFERENCES.SHOW_ALL_KEYBINDS.set(value);
      showAllKeybinds = value;
   }

   public static void setInventoryRowOrder(QuadHotbarLayout.InventoryOrder value) {
      PREFERENCES.INVENTORY_ROW_ORDER.set(value);
      inventoryRowOrder = value;
   }

   public static void setMainHotbarCorner(QuadHotbarLayout.MainCorner value) {
      PREFERENCES.MAIN_HOTBAR_CORNER.set(value);
      mainHotbarCorner = value;
   }

   public static void save() {
      SPEC.save();
   }

   public static void setExperimentsEnabled(boolean value) {
      PREFERENCES.EXPERIMENTS_ENABLED.set(value);
      experimentsEnabled = (Boolean)PREFERENCES.EXPERIMENTS_ENABLED.get();
   }

   public static void setInventoryPages(int pages) {
      PREFERENCES.INVENTORY_PAGES.set(experimentsEnabled ? QuadHotbarPages.alignInventoryPages(pages, hotbarRows) : Math.max(1, Math.min(100, pages)));
      inventoryPages = (Integer)PREFERENCES.INVENTORY_PAGES.get();
   }

   public static void setShowPageButtons(boolean show) {
      PREFERENCES.SHOW_PAGE_BUTTONS.set(show);
      showPageButtons = (Boolean)PREFERENCES.SHOW_PAGE_BUTTONS.get();
   }

   public static void setAlwaysShowPageLabels(boolean show) {
      PREFERENCES.ALWAYS_SHOW_PAGE_LABELS.set(show);
      alwaysShowPageLabels = (Boolean)PREFERENCES.ALWAYS_SHOW_PAGE_LABELS.get();
   }

   static void onLoad(ModConfigEvent event) {
      if (event.getConfig().getSpec() == SPEC) {
         loadPreferences();
      }
   }

   private static void loadPreferences() {
      hotbarRows = (Integer)PREFERENCES.HOTBAR_ROWS.get();
      enabled = (Boolean)PREFERENCES.ENABLED.get();
      scrollBetweenHotbars = (Boolean)PREFERENCES.SCROLL_BETWEEN_HOTBARS.get();
      showAllKeybinds = (Boolean)PREFERENCES.SHOW_ALL_KEYBINDS.get();
      inventoryRowOrder = (QuadHotbarLayout.InventoryOrder)PREFERENCES.INVENTORY_ROW_ORDER.get();
      mainHotbarCorner = (QuadHotbarLayout.MainCorner)PREFERENCES.MAIN_HOTBAR_CORNER.get();
      experimentsEnabled = (Boolean)PREFERENCES.EXPERIMENTS_ENABLED.get();
      inventoryPages = (Integer)PREFERENCES.INVENTORY_PAGES.get();
      showPageButtons = (Boolean)PREFERENCES.SHOW_PAGE_BUTTONS.get();
      alwaysShowPageLabels = (Boolean)PREFERENCES.ALWAYS_SHOW_PAGE_LABELS.get();
      overviewEnabled = (Boolean)PREFERENCES.OVERVIEW_ENABLED.get();
      showOverviewButton = (Boolean)PREFERENCES.SHOW_OVERVIEW_BUTTON.get();
      autoOpenOverview = (Boolean)PREFERENCES.AUTO_OPEN_OVERVIEW.get();
      overviewLastOpen = (Boolean)PREFERENCES.OVERVIEW_LAST_OPEN.get();
      standaloneOverview = (Boolean)PREFERENCES.STANDALONE_OVERVIEW.get();
      overviewWidth = QuadHotbarOverviewLayout.normalizeWidth((Integer)PREFERENCES.OVERVIEW_WIDTH.get());
      overviewFirstPageRight = (Boolean)PREFERENCES.OVERVIEW_FIRST_PAGE_RIGHT.get();
      overviewHorizontal = (Boolean)PREFERENCES.OVERVIEW_HORIZONTAL.get();
      overviewHeight = (Integer)PREFERENCES.OVERVIEW_HEIGHT.get();
      overviewScrollStep = (Integer)PREFERENCES.OVERVIEW_SCROLL_STEP.get();
      overviewDraggable = (Boolean)PREFERENCES.OVERVIEW_DRAGGABLE.get();
      overviewX = (Integer)PREFERENCES.OVERVIEW_X.get();
      overviewY = (Integer)PREFERENCES.OVERVIEW_Y.get();
   }

   static {
      BUILDER.comment("Legacy multiplayer preferences; retained but no longer used.").push("multiplayer");
      new QuadHotbarConfig.Profile();
      BUILDER.pop();
      BUILDER.comment("Legacy migration marker; retained but no longer used.").define("separateProfilesInitialized", false);
   }

   private static final class Profile {
      private final IntValue HOTBAR_ROWS = QuadHotbarConfig.BUILDER
         .comment("How many hotbar rows should be rendered. More than four rows require experimental inventory pages.")
         .defineInRange("hotbarRows", 2, 2, 8);
      private final BooleanValue ENABLED = QuadHotbarConfig.BUILDER
         .comment("Enable QuadHotbar's hotbar rendering and controls. When false, use the vanilla hotbar.")
         .define("enabled", true);
      private final BooleanValue SCROLL_BETWEEN_HOTBARS = QuadHotbarConfig.BUILDER
         .comment("Allow the mouse wheel to switch between hotbars. When false, it stays within the selected hotbar.")
         .define("scrollBetweenHotbars", true);
      private final BooleanValue SHOW_ALL_KEYBINDS = QuadHotbarConfig.BUILDER
         .comment("Show all QuadHotbar keybinds in Controls, including unavailable slots. Hidden bindings are always saved.")
         .define("showAllKeybinds", false);
      private final EnumValue<QuadHotbarLayout.InventoryOrder> INVENTORY_ROW_ORDER = QuadHotbarConfig.BUILDER
         .comment("Order of the three additional inventory rows after the main hotbar. Does not move items.")
         .defineEnum("inventoryRowOrder", QuadHotbarLayout.InventoryOrder.TOP_TO_BOTTOM);
      private final EnumValue<QuadHotbarLayout.MainCorner> MAIN_HOTBAR_CORNER = QuadHotbarConfig.BUILDER
         .comment("Corner of the hotbar grid occupied by the main hotbar. Additional bars follow from that corner.")
         .defineEnum("mainHotbarCorner", QuadHotbarLayout.MainCorner.BOTTOM_LEFT);
      private final BooleanValue EXPERIMENTS_ENABLED = QuadHotbarConfig.BUILDER
         .comment("Enable experimental inventory and hotbar paging.")
         .define("experimentsEnabled", false);
      private final IntValue INVENTORY_PAGES = QuadHotbarConfig.BUILDER
         .comment("Number of inventory pages, capped at 100 and aligned to complete hotbar pages. Reducing it never deletes items.")
         .defineInRange("inventoryPages", 1, 1, 100);
      private final BooleanValue SHOW_PAGE_BUTTONS = QuadHotbarConfig.BUILDER
         .comment("Show inventory page arrow buttons. Page keybinds still work when buttons are hidden.")
         .define("showPageButtons", true);
      private final BooleanValue ALWAYS_SHOW_PAGE_LABELS = QuadHotbarConfig.BUILDER
         .comment("Keep inventory and hotbar page numbers visible instead of hiding them after three seconds.")
         .define("alwaysShowPageLabels", false);
      private final BooleanValue OVERVIEW_ENABLED = QuadHotbarConfig.BUILDER.define("overviewEnabled", true);
      private final BooleanValue SHOW_OVERVIEW_BUTTON = QuadHotbarConfig.BUILDER.define("showOverviewButton", true);
      private final BooleanValue AUTO_OPEN_OVERVIEW = QuadHotbarConfig.BUILDER
         .comment("Automatically open the page overview when opening the player inventory. Server restrictions still apply.")
         .define("autoOpenOverview", true);
      private final BooleanValue OVERVIEW_LAST_OPEN = QuadHotbarConfig.BUILDER
         .comment(
            "Remember whether the player left the inventory page overview open. Closing only the panel pauses auto-open until it is opened manually again; closing the whole inventory does not."
         )
         .define("overviewLastOpen", true);
      private final BooleanValue STANDALONE_OVERVIEW = QuadHotbarConfig.BUILDER.define("standaloneOverview", false);
      private final IntValue OVERVIEW_WIDTH = QuadHotbarConfig.BUILDER
         .comment("Overview width in slots: 9, 18 or 27. GUI scale and server limits temporarily reduce the displayed width.")
         .defineInRange("overviewWidth", 9, 9, 27);
      private final BooleanValue OVERVIEW_FIRST_PAGE_RIGHT = QuadHotbarConfig.BUILDER
         .comment("Start overview page numbering at the rightmost column.")
         .define("overviewFirstPageRight", false);
      private final BooleanValue OVERVIEW_HORIZONTAL = QuadHotbarConfig.BUILDER
         .comment("Fill overview pages by rows instead of by columns.")
         .define("overviewHorizontal", true);
      private final IntValue OVERVIEW_HEIGHT = QuadHotbarConfig.BUILDER
         .comment("Overview viewport height in slots; automatically fits the screen.")
         .defineInRange("overviewHeight", 9, 1, 50);
      private final IntValue OVERVIEW_SCROLL_STEP = QuadHotbarConfig.BUILDER
         .comment("Overview scroll distance per mouse wheel step in GUI pixels. A slot is 18 pixels tall.")
         .defineInRange("overviewScrollStep", 20, 1, 180);
      private final BooleanValue OVERVIEW_DRAGGABLE = QuadHotbarConfig.BUILDER
         .comment("Allow dragging the overview by its title bar.")
         .define("overviewDraggable", true);
      private final IntValue OVERVIEW_X = QuadHotbarConfig.BUILDER
         .comment("Saved overview X position in GUI pixels. -1 uses automatic positioning.")
         .defineInRange("overviewX", -1, -1, 10000);
      private final IntValue OVERVIEW_Y = QuadHotbarConfig.BUILDER
         .comment("Saved overview Y position in GUI pixels. -1 uses automatic positioning.")
         .defineInRange("overviewY", -1, -1, 10000);
   }
}
