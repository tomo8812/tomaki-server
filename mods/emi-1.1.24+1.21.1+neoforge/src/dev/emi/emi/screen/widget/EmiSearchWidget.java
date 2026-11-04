package dev.emi.emi.screen.widget;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.math.Axis;
import dev.emi.emi.EmiPort;
import dev.emi.emi.config.EmiConfig;
import dev.emi.emi.runtime.EmiDrawContext;
import dev.emi.emi.screen.EmiScreenManager;
import dev.emi.emi.search.EmiSearch;
import dev.emi.emi.search.QueryType;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.util.Mth;
import net.minecraft.util.Tuple;
import org.joml.Matrix4fStack;

public class EmiSearchWidget extends EditBox {
   private static final Pattern ESCAPE = Pattern.compile("\\\\.");
   private List<String> searchHistory = Lists.newArrayList();
   private int searchHistoryIndex = 0;
   private List<Tuple<Integer, Style>> styles;
   private long lastClick = 0L;
   private String last = "";
   private long lastRender = System.currentTimeMillis();
   private long accumulatedSpin = 0L;
   public boolean highlight = false;
   public boolean isFocused;

   public EmiSearchWidget(Font textRenderer, int x, int y, int width, int height) {
      super(textRenderer, x, y, width, height, EmiPort.literal(""));
      this.setCanLoseFocus(true);
      this.setTextColor(-1);
      this.setTextColorUneditable(-1);
      this.setMaxLength(256);
      this.setFormatter((string, stringStart) -> {
         MutableComponent text = null;
         int s = 0;
         int last = 0;

         while (s < this.styles.size()) {
            Tuple<Integer, Style> style = this.styles.get(s);
            int end = (Integer)style.getA();
            if (end > stringStart) {
               if (end - stringStart >= string.length()) {
                  text = EmiPort.literal(string.substring(0, string.length()), (Style)style.getB());
                  s = this.styles.size();
               } else {
                  text = EmiPort.literal(string.substring(0, end - stringStart), (Style)style.getB());
                  last = end - stringStart;
                  s++;
               }
               break;
            }

            s++;
         }

         while (s < this.styles.size()) {
            Tuple<Integer, Style> style = this.styles.get(s);
            int end = (Integer)style.getA();
            if (end - stringStart >= string.length()) {
               EmiPort.append(text, EmiPort.literal(string.substring(last, string.length()), (Style)style.getB()));
               break;
            }

            EmiPort.append(text, EmiPort.literal(string.substring(last, end - stringStart), (Style)style.getB()));
            last = end - stringStart;
            s++;
         }

         return EmiPort.ordered(text);
      });
      this.setResponder(string -> {
         if (string.isEmpty()) {
            this.setSuggestion(I18n.get("emi.search", new Object[0]));
         } else {
            this.setSuggestion("");
         }

         EmiScreenManager.updateSearchSidebar();
         Matcher matcher = EmiSearch.TOKENS.matcher(string);
         List<Tuple<Integer, Style>> styles = Lists.newArrayList();
         int last = 0;

         while (matcher.find()) {
            int start = matcher.start();
            int end = matcher.end();
            if (last < start) {
               styles.add(new Tuple(start, Style.EMPTY.applyFormat(ChatFormatting.WHITE)));
            }

            String group = matcher.group();
            if (group.startsWith("-")) {
               styles.add(new Tuple(start + 1, Style.EMPTY.applyFormat(ChatFormatting.RED)));
               start++;
               group = group.substring(1);
            }

            QueryType type = QueryType.fromString(group);
            int subStart = type.prefix.length();
            if (group.length() > 1 + subStart && group.substring(subStart).startsWith("/") && group.endsWith("/")) {
               int rOff = start + subStart + 1;
               styles.add(new Tuple(rOff, type.slashColor));
               Matcher rMatcher = ESCAPE.matcher(string.substring(rOff, end - 1));
               int rLast = 0;

               while (rMatcher.find()) {
                  int rStart = rMatcher.start();
                  int rEnd = rMatcher.end();
                  if (rLast < rStart) {
                     styles.add(new Tuple(rStart + rOff, type.regexColor));
                  }

                  styles.add(new Tuple(rEnd + rOff, type.escapeColor));
                  rLast = rEnd;
               }

               if (rLast < end - 1) {
                  styles.add(new Tuple(end - 1, type.regexColor));
               }

               styles.add(new Tuple(end, type.slashColor));
            } else {
               styles.add(new Tuple(end, type.color));
            }

            last = end;
         }

         if (last < string.length()) {
            styles.add(new Tuple(string.length(), Style.EMPTY.applyFormat(ChatFormatting.WHITE)));
         }

         this.styles = styles;
         EmiSearch.search(string);
      });
   }

   public void update() {
      this.setValue(this.getValue());
   }

   public void swap() {
      String last = this.getValue();
      this.setValue(this.last);
      this.last = last;
   }

   public void setFocused(boolean focused) {
      if (focused != this.isFocused) {
         if (!focused) {
            this.searchHistoryIndex = 0;
            String currentSearch = this.getValue();
            if (!currentSearch.isBlank() && !currentSearch.isEmpty()) {
               this.searchHistory.removeIf(String::isBlank);
               this.searchHistory.remove(currentSearch);
               this.searchHistory.add(0, currentSearch);
               if (this.searchHistory.size() > 36) {
                  this.searchHistory.remove(this.searchHistory.size() - 1);
               }
            }
         }

         this.isFocused = focused;
         super.setFocused(focused);
         EmiScreenManager.updateSearchSidebar();
      }
   }

   public boolean isFocused() {
      return this.isFocused;
   }

   public boolean mouseClicked(double mouseX, double mouseY, int button) {
      if (this.isMouseOver(mouseX, mouseY) && EmiConfig.enabled) {
         boolean b = super.mouseClicked(mouseX, mouseY, button == 1 ? 0 : button);
         if (this.isMouseOver(mouseX, mouseY)) {
            EmiPort.focus(this, true);
         }

         if (this.isFocused()) {
            if (button == 0) {
               if (System.currentTimeMillis() - this.lastClick < 500L) {
                  this.highlight = !this.highlight;
                  this.lastClick = 0L;
               } else {
                  this.lastClick = System.currentTimeMillis();
               }
            } else if (button == 1) {
               this.setValue("");
               EmiPort.focus(this, true);
            }
         }

         return b;
      } else {
         EmiPort.focus(this, false);
         return false;
      }
   }

   public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
      if (this.isFocused()) {
         if (EmiConfig.clearSearch.matchesKey(keyCode, scanCode)) {
            this.setValue("");
            return true;
         }

         if (EmiConfig.focusSearch.matchesKey(keyCode, scanCode) || keyCode == 257 || keyCode == 256) {
            EmiPort.focus(this, false);
            return true;
         }

         if (keyCode == 265 || keyCode == 264) {
            int offset = keyCode == 265 ? 1 : -1;
            if (this.searchHistoryIndex + offset >= 0 && this.searchHistoryIndex + offset < this.searchHistory.size()) {
               if (this.searchHistoryIndex >= 0 && this.searchHistoryIndex < this.searchHistory.size()) {
                  this.searchHistory.set(this.searchHistoryIndex, this.getValue());
               }

               this.searchHistoryIndex += offset;
               this.setValue(this.searchHistory.get(this.searchHistoryIndex));
            }
         }
      }

      return super.keyPressed(keyCode, scanCode, modifiers);
   }

   public void renderWidget(GuiGraphics raw, int mouseX, int mouseY, float delta) {
      EmiDrawContext context = EmiDrawContext.wrap(raw);
      this.setEditable(EmiConfig.enabled);
      String lower = this.getValue().toLowerCase();
      boolean dinnerbone = lower.contains("dinnerbone");
      this.accumulatedSpin = this.accumulatedSpin + (dinnerbone ? 1 : -1) * Math.abs(System.currentTimeMillis() - this.lastRender);
      if (this.accumulatedSpin < 0L) {
         this.accumulatedSpin = 0L;
      } else if (this.accumulatedSpin > 500L) {
         this.accumulatedSpin = 500L;
      }

      this.lastRender = System.currentTimeMillis();
      long deg = this.accumulatedSpin * -180L / 500L;
      Matrix4fStack view = RenderSystem.getModelViewStack();
      view.pushMatrix();
      if (deg != 0L) {
         view.translate(this.x + this.width / 2, this.y + this.height / 2, 0.0F);
         view.rotate(Axis.ZN.rotationDegrees((float)deg));
         view.translate(-(this.x + this.width / 2), -(this.y + this.height / 2), 0.0F);
         EmiPort.applyModelViewMatrix();
      }

      if (lower.contains("jeb_")) {
         int amount = 1023;
         float h = (float)(this.lastRender & amount) % amount / amount;
         int rgb = Mth.hsvToRgb(h, 1.0F, 1.0F);
         context.setColor((rgb >> 16 & 0xFF) / 255.0F, (rgb >> 8 & 0xFF) / 255.0F, (rgb >> 0 & 0xFF) / 255.0F);
      }

      if (EmiConfig.enabled) {
         super.renderWidget(context.raw(), mouseX, mouseY, delta);
         if (this.highlight) {
            int border = -1118720;
            context.fill(this.x - 1, this.y - 1, this.width + 2, 1, border);
            context.fill(this.x - 1, this.y + this.height, this.width + 2, 1, border);
            context.fill(this.x - 1, this.y - 1, 1, this.height + 2, border);
            context.fill(this.x + this.width, this.y - 1, 1, this.height + 2, border);
         }
      }

      context.resetColor();
      view.popMatrix();
      EmiPort.applyModelViewMatrix();
   }
}
