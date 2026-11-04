package dev.emi.emi.config;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.platform.InputConstants.Type;
import dev.emi.emi.com.unascribed.qdcss.QDCSS;
import dev.emi.emi.input.EmiBind;
import dev.emi.emi.platform.EmiAgnos;
import dev.emi.emi.runtime.EmiLog;
import it.unimi.dsi.fastutil.ints.IntList;
import java.io.File;
import java.io.FileWriter;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import joptsimple.internal.Strings;
import net.minecraft.Util;
import net.minecraft.client.StringSplitter;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.chat.Style;

public class EmiConfig {
   private static final Map<Class<?>, EmiConfig.Setter> SETTERS = Maps.newHashMap();
   private static final Map<Class<?>, EmiConfig.Writer<?>> WRITERS = Maps.newHashMap();
   private static final Map<Class<?>, EmiConfig.MultiWriter<?>> MULTI_WRITERS = Maps.newHashMap();
   private static final Map<String, List<String>> unparsed = Maps.newHashMap();
   public static final Map<String, Predicate<?>> FILTERS = Maps.newHashMap();
   public static final String DEFAULT_CONFIG;
   public static boolean useGlobalConfig = false;
   public static String startupConfig;
   @EmiConfig.Comment("Whether EMI is enabled and visible.")
   @EmiConfig.ConfigValue("general.enabled")
   public static boolean enabled = true;
   @EmiConfig.Comment("Whether cheating in items is enabled.")
   @EmiConfig.ConfigValue("general.cheat-mode")
   public static CheatMode cheatMode = CheatMode.CREATIVE;
   @EmiConfig.Comment("How much EMI should use tooltips and popups to show controls and information.")
   @EmiConfig.ConfigValue("general.help-level")
   public static HelpLevel helpLevel = HelpLevel.NORMAL;
   @EmiConfig.Comment("Where EMI should pull stacks from to populate the index.")
   @EmiConfig.ConfigValue("general.index-source")
   public static IndexSource indexSource = IndexSource.CREATIVE;
   @EmiConfig.ConfigGroup("general.search")
   @EmiConfig.Comment("Which sidebar should be searched using the search bar.")
   @EmiConfig.ConfigValue("general.search-sidebar")
   public static SidebarSide searchSidebar = SidebarSide.RIGHT;
   @EmiConfig.Comment("Whether normal search queries should include the tooltip.")
   @EmiConfig.ConfigValue("general.search-tooltip-by-default")
   public static boolean searchTooltipByDefault = true;
   @EmiConfig.Comment("Whether normal search queries should include the mod name.")
   @EmiConfig.ConfigValue("general.search-mod-name-by-default")
   public static boolean searchModNameByDefault = false;
   @EmiConfig.ConfigGroupEnd
   @EmiConfig.Comment("Whether normal search queries should include the stack's tags.")
   @EmiConfig.ConfigValue("general.search-tags-by-default")
   public static boolean searchTagsByDefault = false;
   @EmiConfig.Comment("Which action should be performed when clicking the recipe book.")
   @EmiConfig.ConfigValue("ui.recipe-book-action")
   public static RecipeBookAction recipeBookAction = RecipeBookAction.TOGGLE_CRAFTABLES;
   @EmiConfig.Comment("Where to display status effects in the inventory.")
   @EmiConfig.ConfigValue("ui.effect-location")
   public static EffectLocation effectLocation = EffectLocation.TOP;
   @EmiConfig.Comment("Whether to display a gray overlay when hovering over a stack.")
   @EmiConfig.ConfigValue("ui.show-hover-overlay")
   public static boolean showHoverOverlay = true;
   @EmiConfig.ConfigGroup("ui.mod-id")
   @EmiConfig.Comment("Whether to add mod name to tooltips")
   @EmiConfig.ConfigValue("ui.append-mod-id")
   public static boolean appendModId = true;
   @EmiConfig.ConfigGroupEnd
   @EmiConfig.Comment("Whether to add mod name to item tooltips, in case another mod provides behavior")
   @EmiConfig.ConfigValue("ui.append-item-mod-id")
   public static boolean appendItemModId = true;
   @EmiConfig.Comment("Prevents recipes being quick crafted from shifting around under the cursor.")
   @EmiConfig.ConfigValue("ui.miscraft-prevention")
   public static boolean miscraftPrevention = true;
   @EmiConfig.Comment("The unit to display fluids as.")
   @EmiConfig.ConfigValue("ui.fluid-unit")
   public static FluidUnit fluidUnit = EmiAgnos.isForge() ? FluidUnit.MILLIBUCKETS : FluidUnit.LITERS;
   @EmiConfig.Comment("Whether to use the batched render system. Batching is faster, but may have incompatibilities with shaders or other mods.")
   @EmiConfig.ConfigValue("ui.use-batched-renderer")
   public static boolean useBatchedRenderer = true;
   @EmiConfig.Comment("Whether to have the search bar in the center of the screen, instead of to the side.")
   @EmiConfig.ConfigValue("ui.center-search-bar")
   public static boolean centerSearchBar = true;
   @EmiConfig.ConfigFilter("ui.search-sidebar-focus")
   private static Predicate<SidebarType> searchSidebarFocusFilter = type -> type != SidebarType.CHESS;
   @EmiConfig.Comment("Which sidebar type to switch to when searching.")
   @EmiConfig.ConfigValue("ui.search-sidebar-focus")
   public static SidebarType searchSidebarFocus = SidebarType.INDEX;
   @EmiConfig.ConfigFilter("ui.empty-search-sidebar-focus")
   private static Predicate<SidebarType> emptySearchSidebarFocusFilter = type -> type != SidebarType.CHESS;
   @EmiConfig.Comment("Which sidebar type to focus when the search is empty.")
   @EmiConfig.ConfigValue("ui.empty-search-sidebar-focus")
   public static SidebarType emptySearchSidebarFocus = SidebarType.NONE;
   @EmiConfig.Comment("Whether the EMI config button should be visible.")
   @EmiConfig.ConfigValue("ui.emi-config-button-visibility")
   public static ButtonVisibility emiConfigButtonVisibility = ButtonVisibility.SHOWN;
   @EmiConfig.Comment("Whether the recipe tree button should be visible.")
   @EmiConfig.ConfigValue("ui.recipe-tree-button-visibility")
   public static ButtonVisibility recipeTreeButtonVisibility = ButtonVisibility.AUTO;
   @EmiConfig.ConfigGroup("ui.recipe-screen")
   @EmiConfig.Comment("The maximum height the recipe screen will grow to be if space is available in pixels.")
   @EmiConfig.ConfigValue("ui.maximum-recipe-screen-height")
   public static int maximumRecipeScreenHeight = 256;
   @EmiConfig.Comment(
      "The minimum width of the recipe screen in pixels. Controls how many tabs there can be, and where the page switching buttons go. The default is 176, the width of most screens."
   )
   @EmiConfig.ConfigValue("ui.minimum-recipe-screen-width")
   public static int minimumRecipeScreenWidth = 176;
   @EmiConfig.Comment("The amount of vertical margin to give in the recipe screen.")
   @EmiConfig.ConfigValue("ui.vertical-margin")
   public static int verticalMargin = 20;
   @EmiConfig.ConfigFilter("ui.workstation-location")
   private static Predicate<SidebarSide> workstationLocationFilter = side -> side != SidebarSide.TOP;
   @EmiConfig.Comment("Where to show workstations in the recipe screen")
   @EmiConfig.ConfigValue("ui.workstation-location")
   public static SidebarSide workstationLocation = SidebarSide.BOTTOM;
   @EmiConfig.ConfigGroupEnd
   @EmiConfig.Comment("Display cost per batch when hovering a recipe output")
   @EmiConfig.ConfigValue("ui.show-cost-per-batch")
   public static boolean showCostPerBatch = true;
   @EmiConfig.ConfigGroup("ui.recipe-buttons")
   @EmiConfig.Comment("Whether recipes should have a button to set as default.")
   @EmiConfig.ConfigValue("ui.recipe-default-button")
   public static boolean recipeDefaultButton = true;
   @EmiConfig.Comment("Whether recipes should have a button to show the recipe tree.")
   @EmiConfig.ConfigValue("ui.recipe-tree-button")
   public static boolean recipeTreeButton = true;
   @EmiConfig.Comment("Whether recipes should have a button to fill the ingredients in a handler.")
   @EmiConfig.ConfigValue("ui.recipe-fill-button")
   public static boolean recipeFillButton = true;
   @EmiConfig.Comment("Whether recipes should have a button to take a screenshot of the recipe.")
   @EmiConfig.ConfigValue("ui.recipe-screenshot-button")
   public static boolean recipeScreenshotButton = false;
   @EmiConfig.ConfigGroupEnd
   @EmiConfig.Comment("The GUI scale at which recipe screenshots are saved. Use 0 to use the current GUI scale.")
   @EmiConfig.ConfigValue("ui.recipe-screenshot-scale")
   public static int recipeScreenshotScale = 0;
   @EmiConfig.ConfigGroup("ui.left-sidebar")
   @EmiConfig.Comment("The pages in the left sidebar")
   @EmiConfig.ConfigValue("ui.left-sidebar-pages")
   public static SidebarPages leftSidebarPages = new SidebarPages(List.of(new SidebarPages.SidebarPage(SidebarType.FAVORITES)), SidebarSettings.LEFT);
   @EmiConfig.Comment("The subpanels in the left sidebar")
   @EmiConfig.ConfigValue("ui.left-sidebar-subpanels")
   public static SidebarSubpanels leftSidebarSubpanels = new SidebarSubpanels(List.of(), SidebarSettings.LEFT);
   @EmiConfig.Comment("How many columns and rows of ingredients to limit the left sidebar to")
   @EmiConfig.ConfigValue("ui.left-sidebar-size")
   public static IntGroup leftSidebarSize = new IntGroup("emi.sidebar.size.", List.of("columns", "rows"), IntList.of(12, 100));
   @EmiConfig.Comment("How much space to maintain between the left sidebar and obstructions, in pixels")
   @EmiConfig.ConfigValue("ui.left-sidebar-margins")
   public static Margins leftSidebarMargins = new Margins(2, 2, 2, 2);
   @EmiConfig.Comment("Where to position the left sidebar")
   @EmiConfig.ConfigValue("ui.left-sidebar-align")
   public static ScreenAlign leftSidebarAlign = new ScreenAlign(ScreenAlign.Horizontal.LEFT, ScreenAlign.Vertical.TOP);
   @EmiConfig.Comment("Whether to render the header buttons and page count for the left sidebar")
   @EmiConfig.ConfigValue("ui.left-sidebar-header")
   public static HeaderType leftSidebarHeader = HeaderType.VISIBLE;
   @EmiConfig.ConfigGroupEnd
   @EmiConfig.Comment("Which theme to use for the left sidebar")
   @EmiConfig.ConfigValue("ui.left-sidebar-theme")
   public static SidebarTheme leftSidebarTheme = SidebarTheme.TRANSPARENT;
   @EmiConfig.ConfigGroup("ui.right-sidebar")
   @EmiConfig.Comment("The pages in the right sidebar")
   @EmiConfig.ConfigValue("ui.right-sidebar-pages")
   public static SidebarPages rightSidebarPages = new SidebarPages(
      List.of(new SidebarPages.SidebarPage(SidebarType.INDEX), new SidebarPages.SidebarPage(SidebarType.CRAFTABLES)), SidebarSettings.RIGHT
   );
   @EmiConfig.Comment("The subpanels in the right sidebar")
   @EmiConfig.ConfigValue("ui.right-sidebar-subpanels")
   public static SidebarSubpanels rightSidebarSubpanels = new SidebarSubpanels(List.of(), SidebarSettings.RIGHT);
   @EmiConfig.Comment("How many columns and rows of ingredients to limit the right sidebar to")
   @EmiConfig.ConfigValue("ui.right-sidebar-size")
   public static IntGroup rightSidebarSize = new IntGroup("emi.sidebar.size.", List.of("columns", "rows"), IntList.of(12, 100));
   @EmiConfig.Comment("How much space to maintain between the right sidebar and obstructions, in pixels")
   @EmiConfig.ConfigValue("ui.right-sidebar-margins")
   public static Margins rightSidebarMargins = new Margins(2, 2, 2, 2);
   @EmiConfig.Comment("Where to position the right sidebar")
   @EmiConfig.ConfigValue("ui.right-sidebar-align")
   public static ScreenAlign rightSidebarAlign = new ScreenAlign(ScreenAlign.Horizontal.RIGHT, ScreenAlign.Vertical.TOP);
   @EmiConfig.Comment("Whether to render the header buttons and page count for the right sidebar")
   @EmiConfig.ConfigValue("ui.right-sidebar-header")
   public static HeaderType rightSidebarHeader = HeaderType.VISIBLE;
   @EmiConfig.ConfigGroupEnd
   @EmiConfig.Comment("Which theme to use for the right sidebar")
   @EmiConfig.ConfigValue("ui.right-sidebar-theme")
   public static SidebarTheme rightSidebarTheme = SidebarTheme.TRANSPARENT;
   @EmiConfig.ConfigGroup("ui.top-sidebar")
   @EmiConfig.Comment("The pages in the top sidebar")
   @EmiConfig.ConfigValue("ui.top-sidebar-pages")
   public static SidebarPages topSidebarPages = new SidebarPages(List.of(), SidebarSettings.TOP);
   @EmiConfig.Comment("The subpanels in the top sidebar")
   @EmiConfig.ConfigValue("ui.top-sidebar-subpanels")
   public static SidebarSubpanels topSidebarSubpanels = new SidebarSubpanels(List.of(), SidebarSettings.TOP);
   @EmiConfig.Comment("How many columns and rows of ingredients to limit the top sidebar to")
   @EmiConfig.ConfigValue("ui.top-sidebar-size")
   public static IntGroup topSidebarSize = new IntGroup("emi.sidebar.size.", List.of("columns", "rows"), IntList.of(9, 9));
   @EmiConfig.Comment("How much space to maintain between the top sidebar and obstructions, in pixels")
   @EmiConfig.ConfigValue("ui.top-sidebar-margins")
   public static Margins topSidebarMargins = new Margins(2, 2, 2, 2);
   @EmiConfig.Comment("Where to position the top sidebar")
   @EmiConfig.ConfigValue("ui.top-sidebar-align")
   public static ScreenAlign topSidebarAlign = new ScreenAlign(ScreenAlign.Horizontal.CENTER, ScreenAlign.Vertical.CENTER);
   @EmiConfig.Comment("Whether to render the header buttons and page count for the top sidebar")
   @EmiConfig.ConfigValue("ui.top-sidebar-header")
   public static HeaderType topSidebarHeader = HeaderType.VISIBLE;
   @EmiConfig.ConfigGroupEnd
   @EmiConfig.Comment("Which theme to use for the top sidebar")
   @EmiConfig.ConfigValue("ui.top-sidebar-theme")
   public static SidebarTheme topSidebarTheme = SidebarTheme.TRANSPARENT;
   @EmiConfig.ConfigGroup("ui.bottom-sidebar")
   @EmiConfig.Comment("The pages in the bottom sidebar")
   @EmiConfig.ConfigValue("ui.bottom-sidebar-pages")
   public static SidebarPages bottomSidebarPages = new SidebarPages(List.of(), SidebarSettings.BOTTOM);
   @EmiConfig.Comment("The subpanels in the bottom sidebar")
   @EmiConfig.ConfigValue("ui.bottom-sidebar-subpanels")
   public static SidebarSubpanels bottomSidebarSubpanels = new SidebarSubpanels(List.of(), SidebarSettings.BOTTOM);
   @EmiConfig.Comment("How many columns and rows of ingredients to limit the bottom sidebar to")
   @EmiConfig.ConfigValue("ui.bottom-sidebar-size")
   public static IntGroup bottomSidebarSize = new IntGroup("emi.sidebar.size.", List.of("columns", "rows"), IntList.of(9, 9));
   @EmiConfig.Comment("How much space to maintain between the bottom sidebar and obstructions, in pixels")
   @EmiConfig.ConfigValue("ui.bottom-sidebar-margins")
   public static Margins bottomSidebarMargins = new Margins(2, 2, 2, 2);
   @EmiConfig.Comment("Where to position the bottom sidebar")
   @EmiConfig.ConfigValue("ui.bottom-sidebar-align")
   public static ScreenAlign bottomSidebarAlign = new ScreenAlign(ScreenAlign.Horizontal.CENTER, ScreenAlign.Vertical.CENTER);
   @EmiConfig.Comment("Whether to render the header buttons and page count for the bottom sidebar")
   @EmiConfig.ConfigValue("ui.bottom-sidebar-header")
   public static HeaderType bottomSidebarHeader = HeaderType.VISIBLE;
   @EmiConfig.ConfigGroupEnd
   @EmiConfig.Comment("Which theme to use for the bottom sidebar")
   @EmiConfig.ConfigValue("ui.bottom-sidebar-theme")
   public static SidebarTheme bottomSidebarTheme = SidebarTheme.TRANSPARENT;
   @EmiConfig.Comment("Toggle the visibility of EMI.")
   @EmiConfig.ConfigValue("binds.toggle-visibility")
   public static EmiBind toggleVisibility = new EmiBind("key.emi.toggle_visibility", 1, 79);
   @EmiConfig.Comment("Focuses the search bar.")
   @EmiConfig.ConfigValue("binds.focus-search")
   public static EmiBind focusSearch = new EmiBind("key.emi.focus_search", 1, 70);
   @EmiConfig.Comment("Clears the search bar.")
   @EmiConfig.ConfigValue("binds.clear-search")
   public static EmiBind clearSearch = new EmiBind("key.emi.clear_search", InputConstants.UNKNOWN.getValue());
   @EmiConfig.Comment("Display the recipes for creating a stack.")
   @EmiConfig.ConfigValue("binds.view-recipes")
   public static EmiBind viewRecipes = new EmiBind(
      "key.emi.view_recipes", new EmiBind.ModifiedKey(Type.KEYSYM.getOrCreate(82), 0), new EmiBind.ModifiedKey(Type.MOUSE.getOrCreate(0), 0)
   );
   @EmiConfig.Comment("Display the recipes that can be created using a stack.")
   @EmiConfig.ConfigValue("binds.view-uses")
   public static EmiBind viewUses = new EmiBind(
      "key.emi.view_uses", new EmiBind.ModifiedKey(Type.KEYSYM.getOrCreate(85), 0), new EmiBind.ModifiedKey(Type.MOUSE.getOrCreate(1), 0)
   );
   @EmiConfig.Comment("Favorite the item to display on the side of the screen opposite of recipies for quick access.")
   @EmiConfig.ConfigValue("binds.favorite")
   public static EmiBind favorite = new EmiBind("key.emi.favorite", 65);
   @EmiConfig.Comment("Set the default recipe for a given stack in the output of a recipe to that recipe.")
   @EmiConfig.ConfigValue("binds.default-stack")
   public static EmiBind defaultStack = new EmiBind("key.emi.default_stack", new EmiBind.ModifiedKey(Type.MOUSE.getOrCreate(0), 1));
   @EmiConfig.Comment("Display the recipe tree for a given stack.")
   @EmiConfig.ConfigValue("binds.view-stack-tree")
   public static EmiBind viewStackTree = new EmiBind("key.emi.view_stack_tree", InputConstants.UNKNOWN.getValue());
   @EmiConfig.Comment("Display the recipe tree.")
   @EmiConfig.ConfigValue("binds.view-tree")
   public static EmiBind viewTree = new EmiBind("key.emi.view_tree", InputConstants.UNKNOWN.getValue());
   @EmiConfig.Comment("Return to the previous page in EMI.")
   @EmiConfig.ConfigValue("binds.back")
   public static EmiBind back = new EmiBind("key.emi.back", 259);
   @EmiConfig.Comment("Return to the next page in EMI after going back.")
   @EmiConfig.ConfigValue("binds.forward")
   public static EmiBind forward = new EmiBind("key.emi.forward", InputConstants.UNKNOWN.getValue());
   @EmiConfig.ConfigGroup("binds.crafts")
   @EmiConfig.Comment("When on a stack with an associated recipe:\nMove ingredients for a single result.")
   @EmiConfig.ConfigValue("binds.craft-one")
   public static EmiBind craftOne = new EmiBind("key.emi.craft_one", new EmiBind.ModifiedKey(Type.MOUSE.getOrCreate(0), 0));
   @EmiConfig.Comment("When on a stack with an associated recipe:\nMove ingredients for as many results as possible.")
   @EmiConfig.ConfigValue("binds.craft-all")
   public static EmiBind craftAll = new EmiBind("key.emi.craft_all", new EmiBind.ModifiedKey(Type.MOUSE.getOrCreate(0), 4));
   @EmiConfig.Comment("When on a stack with an associated recipe:\nMove ingredients for a single result and put in inventory if possible.")
   @EmiConfig.ConfigValue("binds.craft-one-to-inventory")
   public static EmiBind craftOneToInventory = new EmiBind("key.emi.craft_one_to_inventory", InputConstants.UNKNOWN.getValue());
   @EmiConfig.Comment("When on a stack with an associated recipe:\nMove ingredients for as many results as possible and put in inventory if possible.")
   @EmiConfig.ConfigValue("binds.craft-all-to-inventory")
   public static EmiBind craftAllToInventory = new EmiBind("key.emi.craft_all_to_inventory", InputConstants.UNKNOWN.getValue());
   @EmiConfig.Comment("When on a stack with an associated recipe:\nMove ingredients for a single result and put in cursor if possible.")
   @EmiConfig.ConfigValue("binds.craft-one-to-cursor")
   public static EmiBind craftOneToCursor = new EmiBind("key.emi.craft_one_to_cursor", new EmiBind.ModifiedKey(Type.MOUSE.getOrCreate(0), 1));
   @EmiConfig.ConfigGroupEnd
   @EmiConfig.Comment("Display the recipe that will be used to craft on a stack with no recipe context.")
   @EmiConfig.ConfigValue("binds.show-craft")
   public static EmiBind showCraft = new EmiBind("key.emi.show_craft", 340);
   @EmiConfig.ConfigGroup("binds.cheats")
   @EmiConfig.Comment("Cheat in one of an item into the inventory.")
   @EmiConfig.ConfigValue("binds.cheat-one-to-inventory")
   public static EmiBind cheatOneToInventory = new EmiBind("key.emi.cheat_one_to_inventory", new EmiBind.ModifiedKey(Type.MOUSE.getOrCreate(1), 1));
   @EmiConfig.Comment("Cheat in a stack of an item into the inventory.")
   @EmiConfig.ConfigValue("binds.cheat-stack-to-inventory")
   public static EmiBind cheatStackToInventory = new EmiBind("key.emi.cheat_stack_to_inventory", new EmiBind.ModifiedKey(Type.MOUSE.getOrCreate(0), 1));
   @EmiConfig.Comment("Cheat in one of an item into the cursor.")
   @EmiConfig.ConfigValue("binds.cheat-one-to-cursor")
   public static EmiBind cheatOneToCursor = new EmiBind("key.emi.cheat_one_to_cursor", new EmiBind.ModifiedKey(Type.MOUSE.getOrCreate(2), 1));
   @EmiConfig.Comment("Cheat in a stack of an item into the cursor.")
   @EmiConfig.ConfigValue("binds.cheat-stack-to-cursor")
   public static EmiBind cheatStackToCursor = new EmiBind("key.emi.cheat_stack_to_cursor", InputConstants.UNKNOWN.getValue());
   @EmiConfig.ConfigGroupEnd
   @EmiConfig.Comment("Delete the stack in the cursor when hovering the index")
   @EmiConfig.ConfigValue("binds.delete-cursor-stack")
   public static EmiBind deleteCursorStack = new EmiBind("key.emi.delete_cursor_stack", new EmiBind.ModifiedKey(Type.MOUSE.getOrCreate(0), 0));
   @EmiConfig.Comment("Copies the hovered recipe's ID to the clipboard")
   @EmiConfig.ConfigValue("binds.copy-recipe-id")
   public static EmiBind copyId = new EmiBind("key.emi.copy_recipe_id", InputConstants.UNKNOWN.getValue());
   @EmiConfig.Comment("In edit mode, hide the hovered stack")
   @EmiConfig.ConfigValue("binds.hide-stack")
   public static EmiBind hideStack = new EmiBind("key.emi.hide_stack", new EmiBind.ModifiedKey(Type.MOUSE.getOrCreate(0), 1));
   @EmiConfig.Comment("In edit mode, hide stacks with the hovered stack's id")
   @EmiConfig.ConfigValue("binds.hide-stack-by-id")
   public static EmiBind hideStackById = new EmiBind("key.emi.hide_stack_by_id", new EmiBind.ModifiedKey(Type.MOUSE.getOrCreate(0), 5));
   @EmiConfig.Comment("Whether development functions should be enabled. Not recommended for general play.")
   @EmiConfig.ConfigValue("dev.dev-mode")
   public static boolean devMode = EmiAgnos.isDevelopmentEnvironment();
   @EmiConfig.Comment("Whether editing the index is enabled")
   @EmiConfig.ConfigValue("dev.edit-mode")
   public static boolean editMode = false;
   @EmiConfig.Comment("Whether to log untranslated tags as warnings.")
   @EmiConfig.ConfigValue("dev.log-untranslated-tags")
   public static boolean logUntranslatedTags = EmiAgnos.isDevelopmentEnvironment();
   @EmiConfig.Comment("Whether to log ingredients that don't have a representative tag as warnings.")
   @EmiConfig.ConfigValue("dev.log-non-tag-ingredients")
   public static boolean logNonTagIngredients = false;
   @EmiConfig.Comment("Whether hovering the output of a recipe should show the recipe's EMI ID.")
   @EmiConfig.ConfigValue("dev.show-recipe-ids")
   public static boolean showRecipeIds = false;
   @EmiConfig.Comment(
      "Whether to display additional widgets added to recipes from other mods.\nThese are typically developer facing and compatibility related, and not useful for players."
   )
   @EmiConfig.ConfigValue("dev.show-recipe-decorators")
   public static boolean showRecipeDecorators = EmiAgnos.isDevelopmentEnvironment();
   @EmiConfig.Comment("Whether stacks in the index should display a highlight if they have a recipe default.")
   @EmiConfig.ConfigValue("dev.highlight-defaulted")
   public static boolean highlightDefaulted = false;
   @EmiConfig.Comment("Whether to display exclusion areas")
   @EmiConfig.ConfigValue("dev.highlight-exclusion-areas")
   public static boolean highlightExclusionAreas = false;

   public static void loadConfig() {
      try {
         File global = new File(getGlobalFolder(), "global.css");
         if (global.exists() && global.isFile()) {
            QDCSS css = QDCSS.load(global);
            if (css.containsKey("global.use-global") && css.get("global.use-global").get().equals("true")) {
               useGlobalConfig = true;
            }
         }

         File config = getConfigFile();
         if (config.exists() && config.isFile()) {
            QDCSS css = QDCSS.load(config);
            loadConfig(css);
         } else {
            File defaultConfig = new File(EmiAgnos.getConfigDirectory().getParent().toFile(), "defaultconfigs/emi.css");
            if (defaultConfig.exists() && defaultConfig.isFile()) {
               QDCSS css = QDCSS.load(defaultConfig);
               loadConfig(css);
            }
         }

         if (startupConfig == null) {
            startupConfig = getSavedConfig();
         }

         writeConfig();
      } catch (Exception e) {
         EmiLog.error("Error reading config", e);
      }
   }

   public static void setGlobalState(boolean useGlobal) {
      try {
         File folder = getGlobalFolder();
         folder.mkdirs();
         File global = new File(folder, "global.css");
         FileWriter writer = new FileWriter(global);
         writer.write("#global {\n\tuse-global: " + useGlobal + ";\n}\n");
         writer.close();
         useGlobalConfig = useGlobal;
         File emi = getConfigFile();
         if (!emi.exists()) {
            emi.createNewFile();
            writeConfig();
         } else {
            loadConfig();
         }
      } catch (Exception e) {
         EmiLog.error("Error writing global config", e);
      }
   }

   public static void loadConfig(QDCSS css) {
      try {
         Set<String> consumed = Sets.newHashSet();

         for (Field field : EmiConfig.class.getFields()) {
            EmiConfig.ConfigValue annot = field.getAnnotation(EmiConfig.ConfigValue.class);
            if (annot != null && css.containsKey(annot.value())) {
               consumed.add(annot.value());
               assignField(css, annot.value(), field);
            }
         }

         for (String key : css.keySet()) {
            if (!consumed.contains(key)) {
               unparsed.put(key, css.getAll(key));
            }
         }
      } catch (Exception e) {
         EmiLog.error("Error reading config", e);
      }
   }

   public static void writeConfig() {
      try {
         FileWriter writer = new FileWriter(getConfigFile());
         writer.write(getSavedConfig());
         writer.close();
      } catch (Exception e) {
         EmiLog.error("Error writing config", e);
      }
   }

   public static String getSavedConfig() {
      Map<String, List<String>> unparsed = Maps.newLinkedHashMap();
      StringSplitter wrapper = new StringSplitter((point, style) -> 1.0F);

      for (Field field : EmiConfig.class.getFields()) {
         EmiConfig.ConfigValue annot = field.getAnnotation(EmiConfig.ConfigValue.class);
         if (annot != null) {
            String[] parts = annot.value().split("\\.");
            String group = parts[0];
            String key = parts[1];
            EmiConfig.Comment comment = field.getAnnotation(EmiConfig.Comment.class);
            String commentText = "";
            if (comment != null) {
               commentText = commentText + "\t/**\n";

               for (FormattedText line : wrapper.splitLines(comment.value(), 80, Style.EMPTY)) {
                  commentText = commentText + "\t * ";
                  commentText = commentText + line.getString();
                  commentText = commentText + "\n";
               }

               commentText = commentText + "\t */\n";
            }

            String text = commentText;

            try {
               text = text + writeField(key, field);
            } catch (Exception e) {
               EmiLog.error("Error serializing config", e);
            }

            unparsed.computeIfAbsent(group, g -> Lists.newArrayList()).add(text);
         }
      }

      for (Entry<String, List<String>> entry : EmiConfig.unparsed.entrySet()) {
         String[] parts = entry.getKey().split("\\.");
         String group = parts[0];
         String key = parts[1];

         for (String value : entry.getValue()) {
            unparsed.computeIfAbsent(group, g -> Lists.newArrayList()).add("\t/** unparsed */\n\t" + key + ": " + value + ";\n");
         }
      }

      String ret = "";
      ret = ret + "/** EMI Config */\n\n";
      boolean firstCategory = true;

      for (Entry<String, List<String>> category : unparsed.entrySet()) {
         if (!firstCategory) {
            ret = ret + "\n";
         }

         firstCategory = false;
         ret = ret + "#" + category.getKey() + " {\n";
         ret = ret + Strings.join(category.getValue(), "\n");
         ret = ret + "}\n";
      }

      return ret;
   }

   private static File getConfigFile() {
      String s = System.getProperty("emi.config");
      if (s != null) {
         File f = new File(s);
         if (f.exists() && f.isFile()) {
            return f;
         }

         EmiLog.error("System property 'emi.config' set to '" + s + "' but does not point to real file, using default config.");
      }

      return useGlobalConfig ? new File(getGlobalFolder(), "emi.css") : new File(EmiAgnos.getConfigDirectory().toFile(), "emi.css");
   }

   private static File getGlobalFolder() {
      String s = switch (Util.getPlatform()) {
         case WINDOWS -> System.getenv("APPDATA") + "/.minecraft";
         case OSX -> System.getProperty("user.home") + "/Library/Application Support/minecraft";
         default -> System.getProperty("user.home") + "/.minecraft";
      };
      return new File(s + "/global/emi");
   }

   private static void assignField(QDCSS css, String annot, Field field) throws IllegalAccessException {
      Class<?> type = field.getType();
      EmiConfig.Setter setter = SETTERS.get(type);
      if (setter != null) {
         setter.setValue(css, annot, field);
      } else {
         if (!ConfigEnum.class.isAssignableFrom(type)) {
            throw new RuntimeException("[emi] Unknown parsing type: " + type);
         }

         SETTERS.get(ConfigEnum.class).setValue(css, annot, field);
      }
   }

   private static String writeField(String key, Field field) throws IllegalAccessException {
      String text = "";
      Class<?> type = field.getType();
      if (MULTI_WRITERS.containsKey(type)) {
         for (String line : ((EmiConfig.MultiWriter<Object>)MULTI_WRITERS.get(type)).writeValue(field.get(null))) {
            text = text + "\t" + key + ": " + line + ";\n";
         }
      } else if (WRITERS.containsKey(type)) {
         text = text + "\t" + key + ": " + ((EmiConfig.Writer<Object>)WRITERS.get(type)).writeValue(field.get(null)) + ";\n";
      } else if (ConfigEnum.class.isAssignableFrom(type)) {
         text = text + "\t" + key + ": " + ((EmiConfig.Writer<Object>)WRITERS.get(ConfigEnum.class)).writeValue(field.get(null)) + ";\n";
      }

      return text;
   }

   private static void defineType(Class<?> clazz, EmiConfig.Setter setter, EmiConfig.Writer<?> writer) {
      SETTERS.put(clazz, setter);
      WRITERS.put(clazz, writer);
   }

   private static void defineType(Class<?> clazz, EmiConfig.Setter setter) {
      defineType(clazz, setter, field -> field.toString());
   }

   private static void defineMultiType(Class<?> clazz, EmiConfig.Setter setter, EmiConfig.MultiWriter<?> writer) {
      SETTERS.put(clazz, setter);
      MULTI_WRITERS.put(clazz, writer);
   }

   static {
      defineType(boolean.class, (css, annotx, fieldx) -> fieldx.setBoolean(null, css.getBoolean(annotx).get()));
      defineType(int.class, (css, annotx, fieldx) -> fieldx.setInt(null, css.getInt(annotx).get()));
      defineType(double.class, (css, annotx, fieldx) -> fieldx.setDouble(null, css.getDouble(annotx).get()));
      defineType(String.class, (css, annotx, fieldx) -> {
         String s = css.get(annotx).get();
         s = s.substring(1, s.length() - 1);
         fieldx.set(null, s);
      }, fieldx -> "\"" + fieldx + "\"");
      defineMultiType(EmiBind.class, (css, annotx, fieldx) -> {
         List<String> strings = Lists.newArrayList(css.getAll(annotx));

         for (int i = 0; i < strings.size(); i++) {
            String s = strings.get(i);
            strings.set(i, s.substring(1, s.length() - 1));
         }

         ((EmiBind)fieldx.get(null)).setKey(strings);
      }, fieldx -> {
         List<String> list = Lists.newArrayList();

         for (EmiBind.ModifiedKey key : fieldx.boundKeys) {
            if (!key.isUnbound() || fieldx.boundKeys.size() == 1) {
               list.add("\"" + key.toName() + "\"");
            }
         }

         return list;
      });
      defineType(ScreenAlign.class, (css, annotx, fieldx) -> {
         String[] parts = css.get(annotx).get().split(",");
         if (parts.length == 2) {
            ((ScreenAlign)fieldx.get(null)).horizontal = ScreenAlign.Horizontal.fromName(parts[0].strip());
            ((ScreenAlign)fieldx.get(null)).vertical = ScreenAlign.Vertical.fromName(parts[1].strip());
         } else {
            ((ScreenAlign)fieldx.get(null)).horizontal = ScreenAlign.Horizontal.CENTER;
            ((ScreenAlign)fieldx.get(null)).vertical = ScreenAlign.Vertical.CENTER;
         }
      }, fieldx -> fieldx.horizontal.getName() + ", " + fieldx.vertical.getName());
      defineType(SidebarPages.class, (css, annotx, fieldx) -> {
         String[] parts = css.get(annotx).get().split(",");
         SidebarPages pages = (SidebarPages)fieldx.get(null);
         pages.pages.clear();

         for (String s : parts) {
            pages.pages.add(new SidebarPages.SidebarPage(SidebarType.fromName(s.trim().toLowerCase())));
         }

         pages.unique();
      }, fieldx -> fieldx.pages.isEmpty() ? "none" : fieldx.pages.stream().map(p -> p.type.getName()).collect(Collectors.joining(", ")));
      defineType(SidebarSubpanels.class, (css, annotx, fieldx) -> {
         String[] parts = css.get(annotx).get().split(",");
         SidebarSubpanels subpanels = (SidebarSubpanels)fieldx.get(null);
         subpanels.subpanels.clear();

         for (String s : parts) {
            String[] subparts = s.trim().split("\\s+");
            SidebarType type = SidebarType.fromName(subparts[0].toLowerCase());
            int rows = subparts.length > 1 ? Integer.parseInt(subparts[1]) : 1;
            if (rows >= 1) {
               subpanels.subpanels.add(new SidebarSubpanels.Subpanel(type, rows));
            }
         }

         subpanels.unique();
      }, fieldx -> fieldx.subpanels.isEmpty() ? "none" : fieldx.subpanels.stream().map(p -> p.type.getName() + " " + p.rows).collect(Collectors.joining(", ")));
      defineType(IntGroup.class, (css, annotx, fieldx) -> ((IntGroup)fieldx.get(null)).deserialize(css.get(annotx).get()), group -> group.serialize());
      defineType(Margins.class, (css, annotx, fieldx) -> ((Margins)fieldx.get(null)).deserialize(css.get(annotx).get()), group -> group.serialize());
      defineType(ConfigEnum.class, (css, annotx, fieldx) -> {
         String name = css.get(annotx).get();

         for (ConfigEnum e : (ConfigEnum[])fieldx.getType().getEnumConstants()) {
            if (e.getName().equals(name)) {
               fieldx.set(null, e);
               break;
            }
         }
      }, c -> c.getName());

      try {
         for (Field field : EmiConfig.class.getDeclaredFields()) {
            EmiConfig.ConfigFilter annot = field.getAnnotation(EmiConfig.ConfigFilter.class);
            if (annot != null) {
               Predicate<?> predicate = (Predicate<?>)field.get(null);
               FILTERS.put(annot.value(), predicate);
            }
         }
      } catch (Exception e) {
         EmiLog.error("Error reflecting config", e);
      }

      DEFAULT_CONFIG = getSavedConfig();
   }

   @Target(ElementType.FIELD)
   @Retention(RetentionPolicy.RUNTIME)
   public @interface Comment {
      String value();
   }

   @Target(ElementType.FIELD)
   @Retention(RetentionPolicy.RUNTIME)
   public @interface ConfigFilter {
      String value();
   }

   @Target(ElementType.FIELD)
   @Retention(RetentionPolicy.RUNTIME)
   public @interface ConfigGroup {
      String value();
   }

   @Target(ElementType.FIELD)
   @Retention(RetentionPolicy.RUNTIME)
   public @interface ConfigGroupEnd {
   }

   @Target(ElementType.FIELD)
   @Retention(RetentionPolicy.RUNTIME)
   public @interface ConfigValue {
      String value();
   }

   private interface MultiWriter<T> {
      List<String> writeValue(T var1);
   }

   private interface Setter {
      void setValue(QDCSS var1, String var2, Field var3) throws IllegalAccessException;
   }

   private interface Writer<T> {
      String writeValue(T var1);
   }
}
