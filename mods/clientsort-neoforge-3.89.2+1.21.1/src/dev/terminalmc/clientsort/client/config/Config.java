package dev.terminalmc.clientsort.client.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dev.terminalmc.clientsort.client.ClientSort;
import dev.terminalmc.clientsort.client.config.legacy.ButtonLayout;
import dev.terminalmc.clientsort.client.order.SortOrder;
import dev.terminalmc.clientsort.platform.services.PlatformServices;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.function.Supplier;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.inventory.HopperMenu;
import net.minecraft.world.inventory.HorseInventoryMenu;
import net.minecraft.world.inventory.PlayerEnderChestContainer;
import net.minecraft.world.inventory.ShulkerBoxMenu;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.entity.RandomizableContainerBlockEntity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class Config {
   private static final Path CONFIG_DIR = PlatformServices.getInstance().getConfigDir();
   private static final String FILE_NAME = "clientsort.json";
   private static final String BACKUP_FILE_NAME = "clientsort.unreadable.json";
   private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
   public final Config.Options options = new Config.Options();
   private static Config instance = null;

   private Config() {
   }

   public static Config.Options options() {
      return get().options;
   }

   private static int unbox(@Nullable Integer val) {
      return val != null ? val : 0;
   }

   private static float unbox(@Nullable Float val) {
      return val != null ? val : 0.0F;
   }

   private void validate() {
      this.options.interactionInterval = Config.Options.interactionIntervalValidator.validate(this.options.interactionInterval);
      this.options.autoOpDelayPlayer = Config.Options.autoOpDelayValidator.validate(this.options.autoOpDelayPlayer);
      this.options.autoOpDelayContainer = Config.Options.autoOpDelayValidator.validate(this.options.autoOpDelayContainer);
      this.options.hotbarScope = Config.Options.hotbarScopeValidator.validate(this.options.hotbarScope);
      this.options.extraSlotScope = Config.Options.extraSlotScopeValidator.validate(this.options.extraSlotScope);
      this.options.typeMatchTags = Config.Options.typeMatchTagsValidator.validate(this.options.typeMatchTags);
      this.options.sortOrderStr = Config.Options.sortOrderStrValidator.validate(this.options.sortOrderStr);
      this.options.shiftSortOrderStr = Config.Options.shiftSortOrderStrValidator.validate(this.options.shiftSortOrderStr);
      this.options.ctrlSortOrderStr = Config.Options.ctrlSortOrderStrValidator.validate(this.options.ctrlSortOrderStr);
      this.options.altSortOrderStr = Config.Options.altSortOrderStrValidator.validate(this.options.altSortOrderStr);
      this.options.startOverrideItems = Config.Options.startOverrideItemsValidator.validate(this.options.startOverrideItems);
      this.options.endOverrideItems = Config.Options.endOverrideItemsValidator.validate(this.options.endOverrideItems);
      this.options.interactionSound = Config.Options.interactionSoundValidator.validate(this.options.interactionSound);
      this.options.soundInterval = Config.Options.soundIntervalValidator.validate(this.options.soundInterval);
      this.options.soundPitchMin = Config.Options.soundPitchMinValidator.validate(this.options.soundPitchMin, this.options);
      this.options.soundPitchMax = Config.Options.soundPitchMaxValidator.validate(this.options.soundPitchMax, this.options);
      this.options.soundVolume = Config.Options.soundVolumeValidator.validate(this.options.soundVolume);
      this.options.firstButtonOp = Config.Options.firstButtonOpValidator.validate(this.options.firstButtonOp, this.options);
      this.options.secondButtonOp = Config.Options.secondButtonOpValidator.validate(this.options.secondButtonOp, this.options);
      this.options.thirdButtonOp = Config.Options.thirdButtonOpValidator.validate(this.options.thirdButtonOp, this.options);
      this.options.fourthButtonOp = Config.Options.fourthButtonOpValidator.validate(this.options.fourthButtonOp, this.options);
      this.options.layoutOffset = Config.Options.layoutOffsetValidator.validate(this.options.layoutOffset);
      this.options.classPolicies = Config.Options.classPoliciesValidator.validate(this.options.classPolicies);
      this.options.screenClassBlacklist = Config.Options.screenClassBlacklistValidator.validate(this.options.screenClassBlacklist);
   }

   private void upgradeLegacy() {
      if (this.options.buttonLayouts != null && !this.options.buttonLayouts.isEmpty()) {
         this.options
            .buttonLayouts
            .values()
            .forEach(
               bl -> this.options
                  .classPolicies
                  .put(
                     ClassPolicy.getKey(bl.className(), null),
                     new ClassPolicy(
                        bl.className(),
                        null,
                        bl.offset(),
                        false,
                        Boolean.TRUE.equals(bl.sortEnabled()) ? Policy.KEYBIND_BUTTON : Policy.KEYBIND,
                        Boolean.TRUE.equals(bl.stackFillEnabled()) ? Policy.KEYBIND_BUTTON : Policy.KEYBIND,
                        Policy.KEYBIND,
                        Boolean.TRUE.equals(bl.transferEnabled()) ? Policy.KEYBIND_BUTTON : Policy.KEYBIND,
                        null,
                        false,
                        new TreeSet<>()
                     )
                  )
            );
         this.options.classPolicies = Config.Options.classPoliciesValidator.validate(this.options.classPolicies);
      }

      this.options.buttonLayouts = null;
   }

   public static Config get() {
      if (instance == null) {
         instance = load();
      }

      return instance;
   }

   public static Config getAndSave() {
      get();
      save();
      return instance;
   }

   public static Config resetAndSave() {
      instance = new Config();
      save();
      return instance;
   }

   @NotNull
   public static Config load() {
      Path file = CONFIG_DIR.resolve("clientsort.json");
      Config config = null;
      if (Files.exists(file)) {
         config = load(file, GSON);
         if (config == null) {
            backup();
            ClientSort.LOG.warn("Resetting config");
         } else {
            config.upgradeLegacy();
         }
      }

      return config != null ? config : new Config();
   }

   @Nullable
   private static Config load(Path file, Gson gson) {
      try (InputStreamReader reader = new InputStreamReader(new FileInputStream(file.toFile()), StandardCharsets.UTF_8)) {
         return (Config)gson.fromJson(reader, Config.class);
      } catch (Exception e) {
         ClientSort.LOG.error("Unable to load config", e);
         return null;
      }
   }

   private static void backup() {
      try {
         ClientSort.LOG.warn("Copying {} to {}", "clientsort.json", "clientsort.unreadable.json");
         if (!Files.isDirectory(CONFIG_DIR)) {
            Files.createDirectories(CONFIG_DIR);
         }

         Path file = CONFIG_DIR.resolve("clientsort.json");
         Path backupFile = file.resolveSibling("clientsort.unreadable.json");
         Files.move(file, backupFile, StandardCopyOption.ATOMIC_MOVE, StandardCopyOption.REPLACE_EXISTING);
      } catch (IOException e) {
         ClientSort.LOG.error("Unable to copy config file", e);
      }
   }

   public static void save() {
      if (instance != null) {
         instance.validate();

         try {
            if (!Files.isDirectory(CONFIG_DIR)) {
               Files.createDirectories(CONFIG_DIR);
            }

            Path file = CONFIG_DIR.resolve("clientsort.json");
            Path tempFile = file.resolveSibling(file.getFileName() + ".tmp");

            try (OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(tempFile.toFile()), StandardCharsets.UTF_8)) {
               writer.write(GSON.toJson(instance));
            } catch (IOException e) {
               throw new IOException(e);
            }

            Files.move(tempFile, file, StandardCopyOption.ATOMIC_MOVE, StandardCopyOption.REPLACE_EXISTING);
            ClientSort.afterConfigSaved(instance);
         } catch (IOException e) {
            ClientSort.LOG.error("Unable to save config", e);
         }
      }
   }

   @FunctionalInterface
   public interface AwareValidator<T> {
      @NotNull
      T validate(@Nullable T var1, @NotNull Config.Options var2);
   }

   public static class Options {
      public static final int INTERACTION_INTERVAL_MIN = 1;
      public static final int INTERACTION_INTERVAL_MAX = 1000;
      public static final int interactionIntervalDefault = 10;
      public int interactionInterval = 10;
      public static Config.Validator<Integer> interactionIntervalValidator = val -> Math.clamp(Config.unbox(val), 1, 1000);
      public static final boolean useServerAccelerationDefault = true;
      public boolean useServerAcceleration = true;
      public static final boolean useClientFallbackDefault = false;
      public boolean useClientFallback = false;
      public static final boolean optimizeCreativeSortingDefault = true;
      public boolean optimizeCreativeSorting = true;
      public static final int AUTO_OP_DELAY_MIN = 0;
      public static final int AUTO_OP_DELAY_MAX = 40;
      public static Config.Validator<Integer> autoOpDelayValidator = val -> Math.clamp(Config.unbox(val), 0, 40);
      public static final int autoOpDelayPlayerDefault = 2;
      public int autoOpDelayPlayer = 2;
      public static final int autoOpDelayContainerDefault = 2;
      public int autoOpDelayContainer = 2;
      public static final Config.Options.HotbarScope hotbarScopeDefault = Config.Options.HotbarScope.HOTBAR;
      public Config.Options.HotbarScope hotbarScope = hotbarScopeDefault;
      public static Config.Validator<Config.Options.HotbarScope> hotbarScopeValidator = val -> val != null
            && Arrays.stream(Config.Options.HotbarScope.values()).toList().contains(val)
         ? val
         : hotbarScopeDefault;
      public static final Config.Options.ExtraSlotScope extraSlotScopeDefault = Config.Options.ExtraSlotScope.NONE;
      public Config.Options.ExtraSlotScope extraSlotScope = extraSlotScopeDefault;
      public static Config.Validator<Config.Options.ExtraSlotScope> extraSlotScopeValidator = val -> val != null
            && Arrays.stream(Config.Options.ExtraSlotScope.values()).toList().contains(val)
         ? val
         : extraSlotScopeDefault;
      public static final boolean bundlesUseLeftClickDefault = false;
      public boolean bundlesUseLeftClick = false;
      public static final boolean transferReverseOrderDefault = true;
      public boolean transferReverseOrder = true;
      public static final boolean alwaysMatchByTypeDefault = false;
      public boolean alwaysMatchByType = false;
      public static final Supplier<List<String>> typeMatchTagsDefault = () -> new ArrayList<>(
         List.of("enchantable/weapon", "enchantable/mining", "enchantable/armor")
      );
      public List<String> typeMatchTags = typeMatchTagsDefault.get();
      public static Config.Validator<List<String>> typeMatchTagsValidator = val -> val != null ? val : typeMatchTagsDefault.get();
      public final transient HashSet<Item> typeMatchItemCache = new HashSet<>();
      public static final String sortOrderStrDefault = SortOrder.CREATIVE.name;
      public String sortOrderStr = sortOrderStrDefault;
      public static Config.Validator<String> sortOrderStrValidator = val -> val != null && SortOrder.SORT_ORDERS.containsKey(val) ? val : sortOrderStrDefault;
      public transient SortOrder sortOrder;
      public static final String shiftSortOrderStrDefault = SortOrder.QUANTITY.name;
      public String shiftSortOrderStr = shiftSortOrderStrDefault;
      public static Config.Validator<String> shiftSortOrderStrValidator = val -> val != null && SortOrder.SORT_ORDERS.containsKey(val)
         ? val
         : shiftSortOrderStrDefault;
      public transient SortOrder shiftSortOrder;
      public static final String ctrlSortOrderStrDefault = SortOrder.ALPHABET.name;
      public String ctrlSortOrderStr = ctrlSortOrderStrDefault;
      public static Config.Validator<String> ctrlSortOrderStrValidator = val -> val != null && SortOrder.SORT_ORDERS.containsKey(val)
         ? val
         : ctrlSortOrderStrDefault;
      public transient SortOrder ctrlSortOrder;
      public static final String altSortOrderStrDefault = SortOrder.RAW_ID.name;
      public String altSortOrderStr = altSortOrderStrDefault;
      public static Config.Validator<String> altSortOrderStrValidator = val -> val != null && SortOrder.SORT_ORDERS.containsKey(val)
         ? val
         : altSortOrderStrDefault;
      public transient SortOrder altSortOrder;
      public static final boolean useStartOverridesDefault = true;
      public boolean useStartOverrides = true;
      public static final Supplier<List<String>> startOverrideItemsDefault = ArrayList::new;
      public List<String> startOverrideItems = startOverrideItemsDefault.get();
      public static Config.Validator<List<String>> startOverrideItemsValidator = val -> val != null ? val : startOverrideItemsDefault.get();
      public final transient Map<Item, Integer> startOverrideMap = new HashMap<>();
      public static final boolean useEndOverridesDefault = true;
      public boolean useEndOverrides = true;
      public static final Supplier<List<String>> endOverrideItemsDefault = ArrayList::new;
      public List<String> endOverrideItems = endOverrideItemsDefault.get();
      public static Config.Validator<List<String>> endOverrideItemsValidator = val -> val != null ? val : endOverrideItemsDefault.get();
      public final transient Map<Item, Integer> endOverrideMap = new HashMap<>();
      public static final boolean playSoundSortDefault = false;
      public boolean playSoundSort = false;
      public static final boolean playSoundOtherDefault = false;
      public boolean playSoundOther = false;
      public static final String interactionSoundDefault = "minecraft:block.note_block.xylophone";
      public String interactionSound = "minecraft:block.note_block.xylophone";
      public static Config.Validator<String> interactionSoundValidator = val -> val != null && ResourceLocation.tryParse(val) != null
         ? val
         : "minecraft:block.note_block.xylophone";
      @Nullable
      public transient ResourceLocation sortSoundLoc = null;
      public static final int SOUND_INTERVAL_MIN = 1;
      public static final int SOUND_INTERVAL_MAX = 100;
      public static final int soundIntervalDefault = 1;
      public int soundInterval = 1;
      public static Config.Validator<Integer> soundIntervalValidator = val -> Math.clamp(Config.unbox(val), 1, 100);
      public static final float SOUND_PITCH_MIN = 0.5F;
      public static final float SOUND_PITCH_MAX = 2.0F;
      public static final float soundPitchMinDefault = 0.5F;
      public float soundPitchMin = 0.5F;
      public static Config.AwareValidator<Float> soundPitchMinValidator = (val, options) -> Math.clamp(
         Config.unbox(val), 0.5F, Math.clamp(options.soundPitchMax, 0.5F, 2.0F)
      );
      public static final float soundPitchMaxDefault = 2.0F;
      public float soundPitchMax = 2.0F;
      public static Config.AwareValidator<Float> soundPitchMaxValidator = (val, options) -> Math.clamp(
         Config.unbox(val), Math.clamp(options.soundPitchMin, 0.5F, 2.0F), 2.0F
      );
      public static final float SOUND_VOLUME_MIN = 0.0F;
      public static final float SOUND_VOLUME_MAX = 1.0F;
      public static final float soundVolumeDefault = 0.2F;
      public float soundVolume = 0.2F;
      public static Config.Validator<Float> soundVolumeValidator = val -> Math.clamp(Config.unbox(val), 0.0F, 1.0F);
      public static final boolean allowSoundOverlapDefault = true;
      public boolean allowSoundOverlap = true;
      public static final boolean isolateKeybindsDefault = true;
      public boolean isolateKeybinds = true;
      public static final boolean showButtonsDefault = true;
      public boolean showButtons = true;
      public static final boolean showButtonTooltipsDefault = false;
      public boolean showButtonTooltips = false;
      public static final boolean anchorButtonsLeftDefault = false;
      public boolean anchorButtonsLeft = false;
      public static final boolean justifyButtonsTopLeftDefault = true;
      public boolean justifyButtonsTopLeft = true;
      public static final boolean buttonsHorizontalDefault = false;
      public boolean buttonsHorizontal = false;
      public static final Operation firstButtonOpDefault = Operation.SORT;
      public Operation firstButtonOp = firstButtonOpDefault;
      public static Config.AwareValidator<Operation> firstButtonOpValidator = (val, options) -> validateUniqueOp(
         val, options.secondButtonOp, options.thirdButtonOp, options.fourthButtonOp
      );
      public static final Operation secondButtonOpDefault = Operation.STACK_FILL;
      public Operation secondButtonOp = secondButtonOpDefault;
      public static Config.AwareValidator<Operation> secondButtonOpValidator = (val, options) -> validateUniqueOp(
         val, options.firstButtonOp, options.thirdButtonOp, options.fourthButtonOp
      );
      public static final Operation thirdButtonOpDefault = Operation.MATCH_TRANSFER;
      public Operation thirdButtonOp = thirdButtonOpDefault;
      public static Config.AwareValidator<Operation> thirdButtonOpValidator = (val, options) -> validateUniqueOp(
         val, options.firstButtonOp, options.secondButtonOp, options.fourthButtonOp
      );
      public static final Operation fourthButtonOpDefault = Operation.TRANSFER;
      public Operation fourthButtonOp = fourthButtonOpDefault;
      public static Config.AwareValidator<Operation> fourthButtonOpValidator = (val, options) -> validateUniqueOp(
         val, options.firstButtonOp, options.secondButtonOp, options.thirdButtonOp
      );
      public static final Vec2i layoutOffsetDefault = new Vec2i(-4, 0);
      public Vec2i layoutOffset = layoutOffsetDefault;
      public static Config.Validator<Vec2i> layoutOffsetValidator = val -> val != null ? val : layoutOffsetDefault;
      public static final Supplier<List<ClassPolicy>> classPoliciesDefaultList = () -> new ArrayList<>(
         List.of(
            new ClassPolicy(
               Inventory.class.getName(),
               null,
               null,
               false,
               Policy.KEYBIND_BUTTON,
               Policy.KEYBIND,
               Policy.KEYBIND_BUTTON,
               Policy.KEYBIND_BUTTON,
               null,
               false,
               new TreeSet<>()
            ),
            new ClassPolicy(
               ChestMenu.class.getName(),
               null,
               null,
               false,
               Policy.KEYBIND_BUTTON,
               Policy.KEYBIND_BUTTON,
               Policy.KEYBIND_BUTTON,
               Policy.KEYBIND_BUTTON,
               null,
               false,
               new TreeSet<>()
            ),
            new ClassPolicy(
               HopperMenu.class.getName(),
               null,
               null,
               false,
               Policy.KEYBIND,
               Policy.KEYBIND,
               Policy.KEYBIND_BUTTON,
               Policy.KEYBIND_BUTTON,
               null,
               false,
               new TreeSet<>()
            ),
            new ClassPolicy(
               HorseInventoryMenu.class.getName(),
               null,
               null,
               false,
               Policy.KEYBIND_BUTTON,
               Policy.KEYBIND,
               Policy.KEYBIND,
               Policy.KEYBIND_BUTTON,
               null,
               false,
               new TreeSet<>()
            ),
            new ClassPolicy(
               PlayerEnderChestContainer.class.getName(),
               null,
               null,
               false,
               Policy.KEYBIND_BUTTON,
               Policy.KEYBIND_BUTTON,
               Policy.KEYBIND_BUTTON,
               Policy.KEYBIND_BUTTON,
               null,
               false,
               new TreeSet<>()
            ),
            new ClassPolicy(
               ShulkerBoxMenu.class.getName(),
               null,
               null,
               false,
               Policy.KEYBIND_BUTTON,
               Policy.KEYBIND_BUTTON,
               Policy.KEYBIND_BUTTON,
               Policy.KEYBIND_BUTTON,
               null,
               false,
               new TreeSet<>()
            ),
            new ClassPolicy(
               RandomizableContainerBlockEntity.class.getName(),
               null,
               null,
               false,
               Policy.KEYBIND_BUTTON,
               Policy.KEYBIND_BUTTON,
               Policy.KEYBIND_BUTTON,
               Policy.KEYBIND_BUTTON,
               null,
               false,
               new TreeSet<>()
            ),
            new ClassPolicy(
               "com.simibubi.create.content.equipment.toolbox.ToolboxMenu",
               null,
               null,
               false,
               Policy.NONE,
               Policy.NONE,
               Policy.NONE,
               Policy.NONE,
               null,
               false,
               new TreeSet<>()
            ),
            new ClassPolicy(
               "com.tiviacz.travelersbackpack.inventory.menu.BackpackSettingsMenu",
               null,
               null,
               false,
               Policy.NONE,
               Policy.NONE,
               Policy.NONE,
               Policy.NONE,
               null,
               false,
               new TreeSet<>()
            ),
            new ClassPolicy(
               "com.tom.storagemod.menu.CraftingTerminalMenu",
               null,
               null,
               false,
               Policy.NONE,
               Policy.NONE,
               Policy.NONE,
               Policy.NONE,
               null,
               false,
               new TreeSet<>()
            ),
            new ClassPolicy(
               "com.tom.storagemod.menu.StorageTerminalMenu",
               null,
               null,
               false,
               Policy.NONE,
               Policy.NONE,
               Policy.NONE,
               Policy.NONE,
               null,
               false,
               new TreeSet<>()
            )
         )
      );
      public static final Supplier<Map<String, ClassPolicy>> classPoliciesDefault = () -> {
         Map<String, ClassPolicy> map = new LinkedHashMap<>();
         classPoliciesDefaultList.get().forEach(policy -> map.put(policy.getKey(), policy));
         return map;
      };
      public Map<String, ClassPolicy> classPolicies = classPoliciesDefault.get();
      public static Config.Validator<Map<String, ClassPolicy>> classPoliciesValidator = val -> {
         Map<String, ClassPolicy> validPolicies = new LinkedHashMap<>();
         if (val == null) {
            return validPolicies;
         }

         val.values()
            .forEach(
               cp -> {
                  if (cp != null && cp.className() != null && !cp.className().isBlank()) {
                     String className = cp.className();
                     ClassPolicy newCp = new ClassPolicy(
                        className,
                        cp.invTitle(),
                        cp.buttonOffset(),
                        cp.offsetFromSlot(),
                        Config.Options.policyValidator.validate(cp.sortPolicy()),
                        Config.Options.policyValidator.validate(cp.stackFillPolicy()),
                        Config.Options.policyValidator.validate(cp.matchTransferPolicy()),
                        Config.Options.policyValidator.validate(cp.transferPolicy()),
                        cp.autoOp(),
                        cp.autoOpOther(),
                        cp.ignoredSlots() == null ? new TreeSet<>() : cp.ignoredSlots()
                     );
                     validPolicies.put(newCp.getKey(), newCp);
                  }
               }
            );
         Map<String, ClassPolicy> sortedPolicies = new LinkedHashMap<>();
         validPolicies.keySet().stream().sorted().forEach(k -> sortedPolicies.put(k, validPolicies.get(k)));
         return sortedPolicies;
      };
      public static Config.Validator<Policy> policyValidator = val -> val != null && Arrays.stream(Policy.values()).toList().contains(val) ? val : Policy.NONE;
      public static final Supplier<List<String>> screenClassBlacklistDefault = () -> new ArrayList<>(
         List.of("com.simibubi.create.content.logistics.stockTicker.StockKeeperCategoryScreen")
      );
      public List<String> screenClassBlacklist = screenClassBlacklistDefault.get();
      public static final Config.Validator<List<String>> screenClassBlacklistValidator = val -> {
         if (val == null) {
            return new ArrayList<>();
         }

         val.removeIf(String::isBlank);
         val.sort(String::compareTo);
         return val;
      };
      @Nullable
      public Map<String, ButtonLayout> buttonLayouts;

      private static Operation validateUniqueOp(@Nullable Operation val, @Nullable Operation... others) {
         if (others.length >= Operation.values().length) {
            throw new IllegalArgumentException();
         }

         Set<Operation> ops = new HashSet<>(Arrays.stream(Operation.values()).toList());
         if (val != null && ops.contains(val) && !Arrays.stream(others).toList().contains(val)) {
            return val;
         }

         Arrays.stream(others).forEach(ops::remove);
         return ops.stream().findAny().get();
      }

      public enum ExtraSlotScope {
         EXTRA,
         HOTBAR,
         INVENTORY,
         NONE;

         // $VF: synthetic method
         private static Config.Options.ExtraSlotScope[] $values() {
            return new Config.Options.ExtraSlotScope[]{EXTRA, HOTBAR, INVENTORY, NONE};
         }
      }

      public enum HotbarScope {
         HOTBAR,
         INVENTORY,
         NONE;

         // $VF: synthetic method
         private static Config.Options.HotbarScope[] $values() {
            return new Config.Options.HotbarScope[]{HOTBAR, INVENTORY, NONE};
         }
      }
   }

   @FunctionalInterface
   public interface Validator<T> {
      @NotNull
      T validate(@Nullable T var1);
   }
}
