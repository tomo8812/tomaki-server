package net.satisfy.candlelight.client.gui;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableList.Builder;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.IntFunction;
import net.minecraft.ChatFormatting;
import net.minecraft.client.GameNarrator;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.ClickEvent.Action;
import net.minecraft.network.chat.Component.Serializer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.util.Mth;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.satisfy.candlelight.Candlelight;
import net.satisfy.candlelight.core.registry.ObjectRegistry;
import org.jetbrains.annotations.Nullable;

@OnlyIn(Dist.CLIENT)
public class SignedPaperGui extends Screen {
   public static final SignedPaperGui.Contents EMPTY_PROVIDER = new SignedPaperGui.Contents() {
      @Override
      public int getPageCount() {
         return 0;
      }

      @Override
      public FormattedText getPageUnchecked(int index) {
         return FormattedText.EMPTY;
      }
   };
   public static final ResourceLocation BOOK_TEXTURE = Candlelight.identifier("textures/gui/note_paper_gui.png");
   private final SignedPaperGui.Contents contents;
   private int pageIndex;
   private List<FormattedCharSequence> cachedPage = Collections.emptyList();
   private int cachedPageIndex = -1;
   // $VF: synthetic field
   static final boolean $assertionsDisabled = !SignedPaperGui.class.desiredAssertionStatus();

   public SignedPaperGui(SignedPaperGui.Contents contents) {
      super(GameNarrator.NO_TITLE);
      this.contents = contents;
   }

   static List<String> readPages(CompoundTag nbt) {
      Builder<String> builder = ImmutableList.builder();
      filterPages(nbt, builder::add);
      return builder.build();
   }

   public static void filterPages(CompoundTag nbt, Consumer<String> pageConsumer) {
      ListTag nbtList = nbt.getList("text", 8).copy();
      IntFunction intFunction;
      if (Minecraft.getInstance().isTextFilteringEnabled() && nbt.contains("filtered_pages", 10)) {
         CompoundTag nbtCompound = nbt.getCompound("filtered_pages");
         intFunction = page -> {
            String string = String.valueOf(page);
            return nbtCompound.contains(string) ? nbtCompound.getString(string) : nbtList.getString(page);
         };
      } else {
         intFunction = nbtList::getString;
      }

      for (int i = 0; i < nbtList.size(); i++) {
         pageConsumer.accept((String)intFunction.apply(i));
      }
   }

   public boolean setPage(int index) {
      int i = Mth.clamp(index, 0, this.contents.getPageCount() - 1);
      if (i != this.pageIndex) {
         this.pageIndex = i;
         this.cachedPageIndex = -1;
         return true;
      } else {
         return false;
      }
   }

   public void renderBackground(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
      this.renderMenuBackground(graphics);
   }

   protected boolean jumpToPage(int page) {
      return this.setPage(page);
   }

   protected void init() {
      this.addCloseButton();
   }

   protected void addCloseButton() {
      this.addRenderableWidget(Button.builder(CommonComponents.GUI_DONE, button -> {
         assert this.minecraft != null;
         this.minecraft.setScreen(null);
      }).bounds(this.width / 2 - 100, 196, 200, 20).build());
   }

   public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
      return super.keyPressed(keyCode, scanCode, modifiers);
   }

   public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float delta) {
      this.renderBackground(guiGraphics, mouseX, mouseY, delta);
      int i = (this.width - 192) / 2;
      guiGraphics.blit(BOOK_TEXTURE, i, 2, 0, 0, 192, 192);
      if (this.cachedPageIndex != this.pageIndex) {
         FormattedText stringVisitable = this.contents.getPage(this.pageIndex);
         this.cachedPage = this.font.split(stringVisitable, 114);
      }

      this.cachedPageIndex = this.pageIndex;
      int l = Math.min(14, this.cachedPage.size());

      for (int m = 0; m < l; m++) {
         FormattedCharSequence orderedText = this.cachedPage.get(m);
         int var10003 = i + 36;
         Objects.requireNonNull(this.font);
         guiGraphics.drawString(this.font, orderedText, var10003, 32 + m * 9, 0, false);
      }

      Style style = this.getTextStyleAt(mouseX, mouseY);
      if (style != null) {
         guiGraphics.renderComponentHoverEffect(this.font, style, mouseX, mouseY);
      }

      super.render(guiGraphics, mouseX, mouseY, delta);
   }

   public boolean mouseClicked(double mouseX, double mouseY, int button) {
      if (button == 0) {
         Style style = this.getTextStyleAt(mouseX, mouseY);
         if (style != null && this.handleComponentClicked(style)) {
            return true;
         }
      }

      return super.mouseClicked(mouseX, mouseY, button);
   }

   public boolean handleComponentClicked(Style style) {
      assert style != null;
      ClickEvent clickEvent = style.getClickEvent();
      if (clickEvent == null) {
         return false;
      }

      if (clickEvent.getAction() == Action.CHANGE_PAGE) {
         String string = clickEvent.getValue();

         try {
            int i = Integer.parseInt(string) - 1;
            return this.jumpToPage(i);
         } catch (Exception var5) {
            return false;
         }
      } else {
         boolean bl = super.handleComponentClicked(style);
         if (bl && clickEvent.getAction() == Action.RUN_COMMAND) {
            this.closeScreen();
         }

         return bl;
      }
   }

   protected void closeScreen() {
      assert this.minecraft != null;
      this.minecraft.setScreen(null);
   }

   @Nullable
   public Style getTextStyleAt(double x, double y) {
      if (this.cachedPage.isEmpty()) {
         return null;
      }

      int i = Mth.floor(x - (this.width - 192) / 2.0 - 36.0);
      int j = Mth.floor(y - 32.0);
      if (i >= 0 && j >= 0 && i <= 114) {
         int k = Math.min(14, this.cachedPage.size());
         if (j >= 9 * k + k) {
            return null;
         }

         int l = j / 9;
         if (l >= this.cachedPage.size()) {
            return null;
         }

         FormattedCharSequence line = this.cachedPage.get(l);
         assert this.minecraft != null;
         return this.minecraft.font.getSplitter().componentStyleAtWidth(line, i);
      } else {
         return null;
      }
   }

   @OnlyIn(Dist.CLIENT)
   public interface Contents {
      static SignedPaperGui.Contents create(ItemStack stack) {
         if (stack.is((Item)ObjectRegistry.NOTE_PAPER_WRITEABLE.get())) {
            return new SignedPaperGui.WrittenPaperContents(stack);
         } else {
            return stack.is((Item)ObjectRegistry.NOTE_PAPER_WRITEABLE.get()) ? new SignedPaperGui.WritablePaperContents(stack) : SignedPaperGui.EMPTY_PROVIDER;
         }
      }

      int getPageCount();

      FormattedText getPageUnchecked(int var1);

      default FormattedText getPage(int index) {
         return index >= 0 && index < this.getPageCount() ? this.getPageUnchecked(index) : FormattedText.EMPTY;
      }
   }

   @OnlyIn(Dist.CLIENT)
   public static class WritablePaperContents implements SignedPaperGui.Contents {
      private final List<String> pages;

      public WritablePaperContents(ItemStack stack) {
         this.pages = getPages(stack);
      }

      private static List<String> getPages(ItemStack stack) {
         CustomData data = (CustomData)stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY);
         CompoundTag nbtCompound = data.copyTag();
         return SignedPaperGui.readPages(nbtCompound);
      }

      @Override
      public int getPageCount() {
         return this.pages.size();
      }

      @Override
      public FormattedText getPageUnchecked(int index) {
         return FormattedText.of(this.pages.get(index));
      }
   }

   @OnlyIn(Dist.CLIENT)
   public static class WrittenPaperContents implements SignedPaperGui.Contents {
      private final List<String> pages;
      // $VF: synthetic field
      static final boolean $assertionsDisabled = !SignedPaperGui.class.desiredAssertionStatus();

      public WrittenPaperContents(ItemStack stack) {
         this.pages = getPages(stack);
      }

      private static List<String> getPages(ItemStack stack) {
         CustomData data = (CustomData)stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY);
         CompoundTag nbtCompound = data.copyTag();
         if (makeSureTagIsValid(nbtCompound)) {
            return SignedPaperGui.readPages(nbtCompound);
         }

         assert Minecraft.getInstance().level != null;
         return ImmutableList.of(
            Serializer.toJson(Component.translatable("book.invalid.tag").withStyle(ChatFormatting.DARK_RED), Minecraft.getInstance().level.registryAccess())
         );
      }

      private static boolean makeSureTagIsValid(CompoundTag nbtCompound) {
         if (nbtCompound == null) {
            return false;
         }

         if (!nbtCompound.contains("text", 9)) {
            return false;
         }

         ListTag listTag = nbtCompound.getList("text", 8);

         for (int i = 0; i < listTag.size(); i++) {
            String string = listTag.getString(i);
            if (string.length() > 32767) {
               return false;
            }
         }

         return true;
      }

      @Override
      public int getPageCount() {
         return this.pages.size();
      }

      @Override
      public FormattedText getPageUnchecked(int index) {
         String string = this.pages.get(index);

         try {
            assert Minecraft.getInstance().level != null;
            FormattedText stringVisitable = Serializer.fromJson(string, Minecraft.getInstance().level.registryAccess());
            if (stringVisitable != null) {
               return stringVisitable;
            }
         } catch (Exception var4) {
         }

         return FormattedText.of(string);
      }
   }
}
