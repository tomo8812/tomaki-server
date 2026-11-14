package maki.mapshare;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public final class HistoryScreen extends Screen {
   private final Screen parent;
   private EditBox count;
   private Button save;

   public HistoryScreen(Screen parent) {
      super(Component.literal("共有する死亡地点の表示件数"));
      this.parent = parent;
   }

   protected void init() {
      int centerX = this.width / 2;
      int centerY = this.height / 2;
      this.count = new EditBox(this.font, centerX - 100, centerY - 20, 200, 20, Component.literal("各プレイヤーの死亡履歴"));
      this.count.setMaxLength(4);
      this.count.setValue(Integer.toString((Integer)SharingMod.HISTORY.get()));
      this.count.setFilter(value -> value.matches("[0-9]*"));
      this.addRenderableWidget(this.count);
      this.save = (Button)this.addRenderableWidget(Button.builder(Component.literal("保存"), button -> {
         SharingMod.HISTORY.set(Integer.parseInt(this.count.getValue()));
         SharingMod.SPEC.save();
         this.minecraft.setScreen(this.parent);
      }).bounds(centerX - 100, centerY + 50, 96, 20).build());
      this.addRenderableWidget(
         Button.builder(Component.literal("キャンセル"), button -> this.minecraft.setScreen(this.parent)).bounds(centerX + 4, centerY + 50, 96, 20).build()
      );
      this.count.setResponder(value -> this.validateCount());
      this.validateCount();
   }

   private void validateCount() {
      try {
         int value = Integer.parseInt(this.count.getValue());
         this.save.active = value >= 1 && value <= 1000;
      } catch (NumberFormatException exception) {
         this.save.active = false;
      }
   }

   public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
      super.render(graphics, mouseX, mouseY, partialTick);
      graphics.drawCenteredString(this.font, this.title, this.width / 2, this.height / 2 - 70, 16777215);
      graphics.drawCenteredString(this.font, Component.literal("全員の死亡地点を、自分の設定件数ずつ表示"), this.width / 2, this.height / 2 - 48, 13421772);
      graphics.drawCenteredString(this.font, Component.literal("1～1000件／人・初期値1件"), this.width / 2, this.height / 2 + 8, 13421772);
      graphics.drawCenteredString(this.font, Component.literal("表示ON/OFFはDioramaの死亡地点設定に連動"), this.width / 2, this.height / 2 + 26, 13421772);
   }

   public void onClose() {
      this.minecraft.setScreen(this.parent);
   }

   public boolean isPauseScreen() {
      return false;
   }
}
