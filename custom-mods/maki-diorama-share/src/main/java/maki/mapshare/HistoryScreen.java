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

    @Override
    protected void init() {
        int centerX = width / 2;
        int centerY = height / 2;

        count = new EditBox(font, centerX - 100, centerY - 20, 200, 20, Component.literal("各プレイヤーの死亡履歴"));
        count.setMaxLength(4);
        count.setValue(Integer.toString(SharingMod.HISTORY.get()));
        count.setFilter(value -> value.matches("[0-9]*"));
        addRenderableWidget(count);

        save = addRenderableWidget(
            Button.builder(Component.literal("保存"), button -> {
                SharingMod.HISTORY.set(Integer.parseInt(count.getValue()));
                SharingMod.SPEC.save();
                minecraft.setScreen(parent);
            }).bounds(centerX - 100, centerY + 50, 96, 20).build()
        );

        addRenderableWidget(
            Button.builder(Component.literal("キャンセル"), button -> minecraft.setScreen(parent))
                .bounds(centerX + 4, centerY + 50, 96, 20)
                .build()
        );

        count.setResponder(value -> validateCount());
        validateCount();
    }

    private void validateCount() {
        try {
            int value = Integer.parseInt(count.getValue());
            save.active = value >= 1 && value <= ShareState.MAX_HISTORY;
        } catch (NumberFormatException exception) {
            save.active = false;
        }
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        super.render(graphics, mouseX, mouseY, partialTick);
        graphics.drawCenteredString(font, title, width / 2, height / 2 - 70, 0xFFFFFF);
        graphics.drawCenteredString(font, Component.literal("全員の死亡地点を、自分の設定件数ずつ表示"), width / 2, height / 2 - 48, 0xCCCCCC);
        graphics.drawCenteredString(font, Component.literal("1～1000件／人・初期値1件"), width / 2, height / 2 + 8, 0xCCCCCC);
        graphics.drawCenteredString(font, Component.literal("表示ON/OFFはDioramaの死亡地点設定に連動"), width / 2, height / 2 + 26, 0xCCCCCC);
    }

    @Override
    public void onClose() {
        minecraft.setScreen(parent);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
