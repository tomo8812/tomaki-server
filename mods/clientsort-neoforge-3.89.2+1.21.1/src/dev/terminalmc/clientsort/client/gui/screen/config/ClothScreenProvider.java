package dev.terminalmc.clientsort.client.gui.screen.config;

import dev.terminalmc.clientsort.client.ClientSort;
import dev.terminalmc.clientsort.client.config.ClassPolicy;
import dev.terminalmc.clientsort.client.config.Config;
import dev.terminalmc.clientsort.client.config.Operation;
import dev.terminalmc.clientsort.client.config.Vec2i;
import dev.terminalmc.clientsort.client.order.CreativeSearchOrder;
import dev.terminalmc.clientsort.client.order.SortOrder;
import dev.terminalmc.clientsort.client.util.KeybindManager;
import dev.terminalmc.clientsort.mixin.client.accessor.KeyMappingAccessor;
import dev.terminalmc.clientsort.util.Localization;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import me.shedaniel.clothconfig2.gui.entries.EnumListEntry;
import me.shedaniel.clothconfig2.impl.builders.SubCategoryBuilder;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public class ClothScreenProvider {
   private static EnumListEntry<?> firstSelector = null;
   private static EnumListEntry<?> secondSelector = null;
   private static EnumListEntry<?> thirdSelector = null;
   private static EnumListEntry<?> fourthSelector = null;

   private ClothScreenProvider() {
      throw new UnsupportedOperationException("This class cannot be instantiated.");
   }

   static Screen getConfigScreen(Screen parent) {
      Config.Options options = Config.options();
      ConfigBuilder builder = ConfigBuilder.create().setParentScreen(parent).setTitle(Localization.localized("name")).setSavingRunnable(Config::getAndSave);
      ConfigEntryBuilder eb = builder.entryBuilder();
      ConfigCategory general = builder.getOrCreateCategory(Localization.localized("option", "general"));
      general.addEntry(
         eb.startIntField(Localization.localized("option", "interactionInterval"), options.interactionInterval)
            .setTooltip(new Component[]{Localization.localized("option", "interactionInterval.tooltip")})
            .setErrorSupplier(val -> {
               if (val < 1) {
                  return Optional.of(Localization.localized("error", "low"));
               } else {
                  return val > 1000 ? Optional.of(Localization.localized("error", "high")) : Optional.empty();
               }
            })
            .setDefaultValue(10)
            .setSaveConsumer(val -> options.interactionInterval = val)
            .build()
      );
      general.addEntry(
         eb.startBooleanToggle(Localization.localized("option", "useServerAcceleration"), options.useServerAcceleration)
            .setTooltip(new Component[]{Localization.localized("option", "useServerAcceleration.tooltip")})
            .setDefaultValue(true)
            .setSaveConsumer(val -> options.useServerAcceleration = val)
            .build()
      );
      general.addEntry(
         eb.startBooleanToggle(Localization.localized("option", "useClientFallback"), options.useClientFallback)
            .setTooltip(new Component[]{Localization.localized("option", "useClientFallback.tooltip")})
            .setDefaultValue(false)
            .setSaveConsumer(val -> options.useClientFallback = val)
            .build()
      );
      general.addEntry(
         eb.startBooleanToggle(Localization.localized("option", "optimizeCreativeSorting"), options.optimizeCreativeSorting)
            .setTooltip(new Component[]{Localization.localized("option", "optimizeCreativeSorting.tooltip")})
            .setDefaultValue(true)
            .setSaveConsumer(val -> {
               options.optimizeCreativeSorting = val;
               if (val) {
                  CreativeSearchOrder.tryRefreshStackPositionMap();
               } else {
                  ClientSort.searchOrderUpdated = false;
               }
            })
            .build()
      );
      general.addEntry(
         eb.startIntField(Localization.localized("option", "autoOpDelayPlayer"), options.autoOpDelayPlayer)
            .setTooltip(new Component[]{Localization.localized("option", "autoOpDelayPlayer.tooltip")})
            .setErrorSupplier(val -> {
               if (val < 0) {
                  return Optional.of(Localization.localized("error", "low"));
               } else {
                  return val > 40 ? Optional.of(Localization.localized("error", "high")) : Optional.empty();
               }
            })
            .setDefaultValue(2)
            .setSaveConsumer(val -> options.autoOpDelayPlayer = val)
            .build()
      );
      general.addEntry(
         eb.startIntField(Localization.localized("option", "autoOpDelayContainer"), options.autoOpDelayContainer)
            .setTooltip(new Component[]{Localization.localized("option", "autoOpDelayContainer.tooltip")})
            .setErrorSupplier(val -> {
               if (val < 0) {
                  return Optional.of(Localization.localized("error", "low"));
               } else {
                  return val > 40 ? Optional.of(Localization.localized("error", "high")) : Optional.empty();
               }
            })
            .setDefaultValue(2)
            .setSaveConsumer(val -> options.autoOpDelayContainer = val)
            .build()
      );
      general.addEntry(
         eb.startEnumSelector(Localization.localized("option", "hotbarScope"), Config.Options.HotbarScope.class, options.hotbarScope)
            .setEnumNameProvider(val -> Localization.localized("hotbarScope", val.name()))
            .setTooltipSupplier(val -> Optional.of(new Component[]{Localization.localized("hotbarScope", val + ".tooltip")}))
            .setDefaultValue(Config.Options.hotbarScopeDefault)
            .setSaveConsumer(val -> options.hotbarScope = val)
            .build()
      );
      general.addEntry(
         eb.startEnumSelector(Localization.localized("option", "extraSlotScope"), Config.Options.ExtraSlotScope.class, options.extraSlotScope)
            .setEnumNameProvider(val -> Localization.localized("extraSlotScope", val.name()))
            .setTooltipSupplier(val -> Optional.of(new Component[]{Localization.localized("extraSlotScope", val + ".tooltip")}))
            .setDefaultValue(Config.Options.extraSlotScopeDefault)
            .setSaveConsumer(val -> options.extraSlotScope = val)
            .build()
      );
      general.addEntry(
         eb.startBooleanToggle(Localization.localized("option", "bundlesUseLeftClick"), options.bundlesUseLeftClick)
            .setTooltip(new Component[]{Localization.localized("option", "bundlesUseLeftClick.tooltip")})
            .setDefaultValue(false)
            .setSaveConsumer(val -> options.bundlesUseLeftClick = val)
            .build()
      );
      general.addEntry(
         eb.startBooleanToggle(Localization.localized("option", "transferReverseOrder"), options.transferReverseOrder)
            .setTooltip(new Component[]{Localization.localized("option", "transferReverseOrder.tooltip")})
            .setDefaultValue(true)
            .setSaveConsumer(val -> options.transferReverseOrder = val)
            .build()
      );
      general.addEntry(
         eb.startBooleanToggle(Localization.localized("option", "showDebugInfo"), dev.terminalmc.clientsort.ClientSort.debugEnabled)
            .setTooltip(new Component[]{Localization.localized("option", "showDebugInfo.tooltip")})
            .setDefaultValue(false)
            .setSaveConsumer(val -> dev.terminalmc.clientsort.ClientSort.debugEnabled = val)
            .build()
      );
      ConfigCategory sort = builder.getOrCreateCategory(Localization.localized("option", "sorting"));
      sort.addEntry(
         eb.startSelector(Localization.localized("option", "sortOrder"), SortOrder.SORT_ORDERS.keySet().toArray(), options.sortOrderStr)
            .setNameProvider(val -> Localization.localized("sortOrder", (String)val))
            .setDefaultValue(Config.Options.sortOrderStrDefault)
            .setSaveConsumer(val -> options.sortOrderStr = (String)val)
            .build()
      );
      sort.addEntry(
         eb.startSelector(Localization.localized("option", "shiftSortOrder"), SortOrder.SORT_ORDERS.keySet().toArray(), options.shiftSortOrderStr)
            .setNameProvider(val -> Localization.localized("sortOrder", (String)val))
            .setDefaultValue(Config.Options.shiftSortOrderStrDefault)
            .setSaveConsumer(val -> options.shiftSortOrderStr = (String)val)
            .build()
      );
      sort.addEntry(
         eb.startSelector(Localization.localized("option", "ctrlSortOrder"), SortOrder.SORT_ORDERS.keySet().toArray(), options.ctrlSortOrderStr)
            .setNameProvider(val -> Localization.localized("sortOrder", (String)val))
            .setDefaultValue(Config.Options.ctrlSortOrderStrDefault)
            .setSaveConsumer(val -> options.ctrlSortOrderStr = (String)val)
            .build()
      );
      sort.addEntry(
         eb.startSelector(Localization.localized("option", "altSortOrder"), SortOrder.SORT_ORDERS.keySet().toArray(), options.altSortOrderStr)
            .setNameProvider(val -> Localization.localized("sortOrder", (String)val))
            .setDefaultValue(Config.Options.altSortOrderStrDefault)
            .setSaveConsumer(val -> options.altSortOrderStr = (String)val)
            .build()
      );
      sort.addEntry(eb.startTextDescription(Localization.localized("option", "orderOverrides")).build());
      sort.addEntry(
         eb.startBooleanToggle(Localization.localized("option", "useStartOverrides"), options.useStartOverrides)
            .setTooltip(new Component[]{Localization.localized("option", "useStartOverrides.tooltip")})
            .setDefaultValue(true)
            .setSaveConsumer(val -> options.useStartOverrides = val)
            .build()
      );
      sort.addEntry(
         eb.startStrList(Localization.localized("option", "startOverrideItems"), options.startOverrideItems)
            .setTooltip(
               new Component[]{
                  Localization.localized("option", "startOverrideItems.tooltip")
                     .append("\n")
                     .append(
                        Localization.localized(
                           "option", "overrideItems.tooltip.2", Localization.localized("option", "overrideItems.tooltip.2.item").withStyle(ChatFormatting.GOLD)
                        )
                     )
               }
            )
            .setDefaultValue(Config.Options.startOverrideItemsDefault.get())
            .setSaveConsumer(val -> {
               options.startOverrideItems.clear();
               options.startOverrideItems.addAll(val.stream().map(String::strip).filter(s -> !s.isBlank()).toList());
            })
            .setInsertInFront(true)
            .setExpanded(options.useStartOverrides)
            .build()
      );
      sort.addEntry(
         eb.startBooleanToggle(Localization.localized("option", "useEndOverrides"), options.useEndOverrides)
            .setTooltip(new Component[]{Localization.localized("option", "useEndOverrides.tooltip")})
            .setDefaultValue(true)
            .setSaveConsumer(val -> options.useEndOverrides = val)
            .build()
      );
      sort.addEntry(
         eb.startStrList(Localization.localized("option", "endOverrideItems"), options.endOverrideItems)
            .setTooltip(
               new Component[]{
                  Localization.localized("option", "endOverrideItems.tooltip")
                     .append("\n")
                     .append(
                        Localization.localized(
                           "option", "overrideItems.tooltip.2", Localization.localized("option", "overrideItems.tooltip.2.item").withStyle(ChatFormatting.GOLD)
                        )
                     )
               }
            )
            .setDefaultValue(Config.Options.endOverrideItemsDefault.get())
            .setSaveConsumer(val -> {
               options.endOverrideItems.clear();
               options.endOverrideItems.addAll(val.stream().map(String::strip).filter(s -> !s.isBlank()).toList());
            })
            .setInsertInFront(true)
            .setExpanded(options.useEndOverrides)
            .build()
      );
      ConfigCategory matching = builder.getOrCreateCategory(Localization.localized("option", "matching"));
      matching.addEntry(
         eb.startBooleanToggle(Localization.localized("option", "alwaysMatchByType"), options.alwaysMatchByType)
            .setTooltip(new Component[]{Localization.localized("option", "alwaysMatchByType.tooltip")})
            .setDefaultValue(false)
            .setSaveConsumer(val -> options.alwaysMatchByType = val)
            .build()
      );
      matching.addEntry(
         eb.startStrList(Localization.localized("option", "typeMatchTags"), options.typeMatchTags)
            .setTooltip(
               new Component[]{
                  Localization.localized("option", "typeMatchTags.tooltip.1")
                     .append("\n")
                     .append(Localization.localized("option", "typeMatchTags.tooltip.2"))
                     .append("\n")
                     .append(
                        Localization.localized(
                           "option", "typeMatchTags.tooltip.3", Component.literal("https://minecraft.wiki/w/Item_tag").withStyle(ChatFormatting.GOLD)
                        )
                     )
               }
            )
            .setDefaultValue(Config.Options.typeMatchTagsDefault.get())
            .setSaveConsumer(val -> {
               options.typeMatchTags.clear();
               options.typeMatchTags.addAll(val.stream().map(String::strip).filter(s -> !s.isBlank()).toList());
            })
            .setInsertInFront(true)
            .setExpanded(!options.alwaysMatchByType)
            .build()
      );
      ConfigCategory sounds = builder.getOrCreateCategory(Localization.localized("option", "sounds"));
      sounds.addEntry(
         eb.startBooleanToggle(Localization.localized("option", "playSoundSort"), options.playSoundSort)
            .setTooltip(new Component[]{Localization.localized("option", "playSoundSort.tooltip")})
            .setDefaultValue(false)
            .setSaveConsumer(val -> options.playSoundSort = val)
            .build()
      );
      sounds.addEntry(
         eb.startBooleanToggle(Localization.localized("option", "playSoundOther"), options.playSoundOther)
            .setTooltip(new Component[]{Localization.localized("option", "playSoundOther.tooltip")})
            .setDefaultValue(false)
            .setSaveConsumer(val -> options.playSoundOther = val)
            .build()
      );
      sounds.addEntry(
         eb.startStrField(Localization.localized("option", "interactionSound"), options.interactionSound)
            .setDefaultValue("minecraft:block.note_block.xylophone")
            .setSaveConsumer(val -> options.interactionSound = val)
            .setErrorSupplier(
               val -> ResourceLocation.tryParse(val) == null ? Optional.of(Localization.localized("error", "resourceLocation.parse")) : Optional.empty()
            )
            .build()
      );
      sounds.addEntry(
         eb.startIntField(Localization.localized("option", "soundInterval"), options.soundInterval)
            .setTooltip(new Component[]{Localization.localized("option", "soundInterval.tooltip")})
            .setErrorSupplier(val -> {
               if (val < 1) {
                  return Optional.of(Localization.localized("error", "low"));
               } else {
                  return val > 100 ? Optional.of(Localization.localized("error", "high")) : Optional.empty();
               }
            })
            .setDefaultValue(1)
            .setSaveConsumer(val -> options.soundInterval = val)
            .build()
      );
      sounds.addEntry(
         eb.startFloatField(Localization.localized("option", "soundPitchMin"), options.soundPitchMin)
            .setTooltip(new Component[]{Localization.localized("option", "soundPitchMin.tooltip")})
            .setErrorSupplier(val -> {
               if (val < 0.5F) {
                  return Optional.of(Localization.localized("error", "low"));
               } else {
                  return val > options.soundPitchMax ? Optional.of(Localization.localized("error", "high")) : Optional.empty();
               }
            })
            .setDefaultValue(0.5F)
            .setSaveConsumer(val -> options.soundPitchMin = val)
            .build()
      );
      sounds.addEntry(
         eb.startFloatField(Localization.localized("option", "soundPitchMax"), options.soundPitchMax)
            .setTooltip(new Component[]{Localization.localized("option", "soundPitchMax.tooltip")})
            .setErrorSupplier(val -> {
               if (val < options.soundPitchMin) {
                  return Optional.of(Localization.localized("error", "low"));
               } else {
                  return val > 2.0F ? Optional.of(Localization.localized("error", "high")) : Optional.empty();
               }
            })
            .setDefaultValue(2.0F)
            .setSaveConsumer(val -> options.soundPitchMax = val)
            .build()
      );
      sounds.addEntry(eb.startFloatField(Localization.localized("option", "soundVolume"), options.soundVolume).setErrorSupplier(val -> {
         if (val < 0.0F) {
            return Optional.of(Localization.localized("error", "low"));
         } else {
            return val > 1.0F ? Optional.of(Localization.localized("error", "high")) : Optional.empty();
         }
      }).setDefaultValue(0.2F).setSaveConsumer(val -> options.soundVolume = val).build());
      sounds.addEntry(
         eb.startBooleanToggle(Localization.localized("option", "allowSoundOverlap"), options.allowSoundOverlap)
            .setTooltip(new Component[]{Localization.localized("option", "allowSoundOverlap.tooltip")})
            .setDefaultValue(true)
            .setSaveConsumer(val -> options.allowSoundOverlap = val)
            .build()
      );
      ConfigCategory keybinds = builder.getOrCreateCategory(Localization.localized("option", "keybinds"));
      keybinds.addEntry(
         eb.startKeyCodeField(Localization.localized("key", "edit"), ((KeyMappingAccessor)KeybindManager.EDIT_KEY).clientsort$getKey())
            .setDefaultValue(KeybindManager.EDIT_KEY.getDefaultKey())
            .setKeySaveConsumer(key -> KeybindManager.bindKey(KeybindManager.EDIT_KEY, key))
            .build()
      );
      keybinds.addEntry(
         eb.startKeyCodeField(Localization.localized("key", "cancelAuto"), ((KeyMappingAccessor)KeybindManager.CANCEL_AUTO_KEY).clientsort$getKey())
            .setDefaultValue(KeybindManager.CANCEL_AUTO_KEY.getDefaultKey())
            .setKeySaveConsumer(key -> KeybindManager.bindKey(KeybindManager.CANCEL_AUTO_KEY, key))
            .build()
      );
      keybinds.addEntry(
         eb.startKeyCodeField(Localization.localized("key", "op.sort"), ((KeyMappingAccessor)KeybindManager.SORT_KEY).clientsort$getKey())
            .setDefaultValue(KeybindManager.SORT_KEY.getDefaultKey())
            .setKeySaveConsumer(key -> KeybindManager.bindKey(KeybindManager.SORT_KEY, key))
            .build()
      );
      keybinds.addEntry(
         eb.startKeyCodeField(Localization.localized("key", "op.stackFill"), ((KeyMappingAccessor)KeybindManager.STACK_FILL_KEY).clientsort$getKey())
            .setDefaultValue(KeybindManager.STACK_FILL_KEY.getDefaultKey())
            .setKeySaveConsumer(key -> KeybindManager.bindKey(KeybindManager.STACK_FILL_KEY, key))
            .build()
      );
      keybinds.addEntry(
         eb.startKeyCodeField(Localization.localized("key", "op.matchTransfer"), ((KeyMappingAccessor)KeybindManager.MATCH_TRANSFER_KEY).clientsort$getKey())
            .setDefaultValue(KeybindManager.MATCH_TRANSFER_KEY.getDefaultKey())
            .setKeySaveConsumer(key -> KeybindManager.bindKey(KeybindManager.MATCH_TRANSFER_KEY, key))
            .build()
      );
      keybinds.addEntry(
         eb.startKeyCodeField(Localization.localized("key", "op.transfer"), ((KeyMappingAccessor)KeybindManager.TRANSFER_KEY).clientsort$getKey())
            .setDefaultValue(KeybindManager.TRANSFER_KEY.getDefaultKey())
            .setKeySaveConsumer(key -> KeybindManager.bindKey(KeybindManager.TRANSFER_KEY, key))
            .build()
      );
      ConfigCategory buttons = builder.getOrCreateCategory(Localization.localized("option", "buttons"));
      buttons.addEntry(
         eb.startBooleanToggle(Localization.localized("option", "showButtons"), options.showButtons)
            .setTooltip(new Component[]{Localization.localized("option", "showButtons.tooltip")})
            .setDefaultValue(true)
            .setSaveConsumer(val -> options.showButtons = val)
            .build()
      );
      buttons.addEntry(
         eb.startBooleanToggle(Localization.localized("option", "showButtonTooltips"), options.showButtonTooltips)
            .setTooltip(new Component[]{Localization.localized("option", "showButtonTooltips.tooltip")})
            .setDefaultValue(false)
            .setSaveConsumer(val -> options.showButtonTooltips = val)
            .build()
      );
      buttons.addEntry(
         eb.startBooleanToggle(Localization.localized("option", "anchorButtonsLeft"), options.anchorButtonsLeft)
            .setTooltip(new Component[]{Localization.localized("option", "anchorButtonsLeft.tooltip")})
            .setDefaultValue(false)
            .setSaveConsumer(val -> options.anchorButtonsLeft = val)
            .build()
      );
      buttons.addEntry(
         eb.startBooleanToggle(Localization.localized("option", "justifyButtonsTopLeft"), options.justifyButtonsTopLeft)
            .setTooltip(new Component[]{Localization.localized("option", "justifyButtonsTopLeft.tooltip")})
            .setDefaultValue(true)
            .setSaveConsumer(val -> options.justifyButtonsTopLeft = val)
            .build()
      );
      buttons.addEntry(
         eb.startBooleanToggle(Localization.localized("option", "buttonsHorizontal"), options.buttonsHorizontal)
            .setTooltip(new Component[]{Localization.localized("option", "buttonsHorizontal.tooltip")})
            .setDefaultValue(false)
            .setSaveConsumer(val -> options.buttonsHorizontal = val)
            .build()
      );
      firstSelector = eb.startEnumSelector(Localization.localized("option", "firstButtonOp"), Operation.class, options.firstButtonOp)
         .setErrorSupplier(
            val -> !val.equals(getSecondSelector()) && !val.equals(getThirdSelector()) && !val.equals(getFourthSelector())
               ? Optional.empty()
               : Optional.of(Localization.localized("error", "triggerButton.duplicate"))
         )
         .setEnumNameProvider(val -> Localization.localized("triggerButton", val.name()))
         .setDefaultValue(Config.Options.firstButtonOpDefault)
         .setSaveConsumer(val -> options.firstButtonOp = val)
         .build();
      buttons.addEntry(firstSelector);
      secondSelector = eb.startEnumSelector(Localization.localized("option", "secondButtonOp"), Operation.class, options.secondButtonOp)
         .setEnumNameProvider(val -> Localization.localized("triggerButton", val.name()))
         .setErrorSupplier(
            val -> !val.equals(getFirstSelector()) && !val.equals(getThirdSelector()) && !val.equals(getFourthSelector())
               ? Optional.empty()
               : Optional.of(Localization.localized("error", "triggerButton.duplicate"))
         )
         .setDefaultValue(Config.Options.secondButtonOpDefault)
         .setSaveConsumer(val -> options.secondButtonOp = val)
         .build();
      buttons.addEntry(secondSelector);
      thirdSelector = eb.startEnumSelector(Localization.localized("option", "thirdButtonOp"), Operation.class, options.thirdButtonOp)
         .setEnumNameProvider(val -> Localization.localized("triggerButton", val.name()))
         .setErrorSupplier(
            val -> !val.equals(getFirstSelector()) && !val.equals(getSecondSelector()) && !val.equals(getFourthSelector())
               ? Optional.empty()
               : Optional.of(Localization.localized("error", "triggerButton.duplicate"))
         )
         .setDefaultValue(Config.Options.thirdButtonOpDefault)
         .setSaveConsumer(val -> options.thirdButtonOp = val)
         .build();
      buttons.addEntry(thirdSelector);
      fourthSelector = eb.startEnumSelector(Localization.localized("option", "fourthButtonOp"), Operation.class, options.fourthButtonOp)
         .setEnumNameProvider(val -> Localization.localized("triggerButton", val.name()))
         .setErrorSupplier(
            val -> !val.equals(getFirstSelector()) && !val.equals(getSecondSelector()) && !val.equals(getThirdSelector())
               ? Optional.empty()
               : Optional.of(Localization.localized("error", "triggerButton.duplicate"))
         )
         .setDefaultValue(Config.Options.fourthButtonOpDefault)
         .setSaveConsumer(val -> options.fourthButtonOp = val)
         .build();
      buttons.addEntry(fourthSelector);
      SubCategoryBuilder layoutDefaults = eb.startSubCategory(Localization.localized("option", "layoutDefaults"))
         .setTooltip(new Component[]{Localization.localized("option", "layoutDefaults.tooltip")})
         .setExpanded(true);
      layoutDefaults.add(
         eb.startIntField(Localization.localized("option", "layoutOffset.x"), options.layoutOffset.x())
            .setDefaultValue(Config.Options.layoutOffsetDefault.x())
            .setSaveConsumer(val -> options.layoutOffset = new Vec2i(val, options.layoutOffset.y()))
            .build()
      );
      layoutDefaults.add(
         eb.startIntField(Localization.localized("option", "layoutOffset.y"), options.layoutOffset.y())
            .setDefaultValue(Config.Options.layoutOffsetDefault.y())
            .setSaveConsumer(val -> options.layoutOffset = new Vec2i(options.layoutOffset.x(), val))
            .build()
      );
      buttons.addEntry(layoutDefaults.build());
      ConfigCategory policies = builder.getOrCreateCategory(Localization.localized("option", "policies"));
      SubCategoryBuilder policiesInstructions = eb.startSubCategory(Localization.localized("option", "policies.instructions"));
      policiesInstructions.add(eb.startTextDescription(Localization.localized("option", "policies.description.1")).build());
      policiesInstructions.add(eb.startTextDescription(Localization.localized("option", "policies.description.2")).build());
      policiesInstructions.add(eb.startTextDescription(Localization.localized("option", "policies.description.3")).build());
      policiesInstructions.add(eb.startTextDescription(Localization.localized("option", "policies.description.4")).build());
      policiesInstructions.add(eb.startTextDescription(Localization.localized("option", "policies.description.5")).build());
      policiesInstructions.add(eb.startTextDescription(Localization.localized("option", "policies.description.6")).build());
      policiesInstructions.add(eb.startTextDescription(Localization.localized("option", "policies.description.7")).build());
      policiesInstructions.add(eb.startTextDescription(Localization.localized("option", "policies.description.8")).build());
      policiesInstructions.add(eb.startTextDescription(Localization.localized("option", "policies.description.9")).build());
      policiesInstructions.add(eb.startTextDescription(Localization.localized("option", "policies.description.10")).build());
      policiesInstructions.add(eb.startTextDescription(Localization.localized("option", "policies.description.11")).build());
      policiesInstructions.add(eb.startTextDescription(Localization.localized("option", "policies.description.12")).build());
      policies.addEntry(policiesInstructions.build());
      policies.addEntry(
         eb.startStrList(Localization.localized("option", "classPolicies"), getPolicyStrings(options.classPolicies.values()))
            .setTooltip(new Component[]{Localization.localized("option", "classPolicies.tooltip")})
            .setExpanded(true)
            .setErrorSupplier(list -> {
               int i = 0;

               for (String string : list) {
                  try {
                     ClassPolicy.fromDataString(string, options.classPolicies.keySet());
                  } catch (ParseException ex) {
                     return Optional.of(Localization.localized("error", "classPolicy.parse", i + 1, ex.getMessage()));
                  }

                  i++;
               }

               return Optional.empty();
            })
            .setDefaultValue(getPolicyStrings(Config.Options.classPoliciesDefaultList.get()))
            .setSaveConsumer(list -> {
               Set<ClassPolicy> classPolicies = new HashSet<>();

               for (String string : list) {
                  try {
                     ClassPolicy policy = ClassPolicy.fromDataString(string, options.classPolicies.keySet());
                     classPolicies.add(policy);
                  } catch (ParseException ex) {
                     ClientSort.LOG.error("Encountered a class policy parsing error on string '{}' not caught by error checker: {}", string, ex.getMessage());
                  }
               }

               options.classPolicies.clear();
               classPolicies.forEach(policy -> options.classPolicies.put(policy.getKey(), policy));
            })
            .build()
      );
      policies.addEntry(
         eb.startStrList(Localization.localized("option", "screenClassBlacklist"), options.screenClassBlacklist)
            .setTooltip(new Component[]{Localization.localized("option", "screenClassBlacklist.tooltip")})
            .setDefaultValue(Config.Options.screenClassBlacklistDefault.get())
            .setSaveConsumer(val -> {
               options.screenClassBlacklist.clear();
               options.screenClassBlacklist.addAll(val.stream().map(String::strip).filter(s -> !s.isBlank()).toList());
            })
            .setInsertInFront(true)
            .setExpanded(true)
            .build()
      );
      return builder.build();
   }

   private static List<String> getPolicyStrings(Collection<ClassPolicy> policies) {
      List<String> strings = new ArrayList<>();

      for (ClassPolicy policy : policies) {
         strings.add(policy.toDataString());
      }

      return strings;
   }

   private static Operation getFirstSelector() {
      return firstSelector == null ? null : (Operation)firstSelector.getValue();
   }

   private static Operation getSecondSelector() {
      return secondSelector == null ? null : (Operation)secondSelector.getValue();
   }

   private static Operation getThirdSelector() {
      return thirdSelector == null ? null : (Operation)thirdSelector.getValue();
   }

   private static Operation getFourthSelector() {
      return fourthSelector == null ? null : (Operation)fourthSelector.getValue();
   }
}
