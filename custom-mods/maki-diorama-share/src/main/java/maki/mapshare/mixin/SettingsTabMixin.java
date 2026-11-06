package maki.mapshare.mixin;

import dev.diorama.SettingsScreen;
import java.util.List;
import maki.mapshare.HistoryScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(targets = "dev.diorama.SettingsScreen$SettingsTab", remap = false)
public abstract class SettingsTabMixin {
    @Shadow
    @Final
    private List<AbstractWidget> wide;

    @Inject(
        method = "<init>(Ldev/diorama/SettingsScreen;Ljava/lang/String;)V",
        at = @At("RETURN"),
        remap = false
    )
    private void maki$historySetting(SettingsScreen screen, String section, CallbackInfo callback) {
        if (!section.equals("diorama.settings.waypoints")) {
            return;
        }

        wide.add(
            Button.builder(Component.literal("死亡履歴の表示件数…"), button -> {
                Minecraft minecraft = Minecraft.getInstance();
                minecraft.setScreen(new HistoryScreen(minecraft.screen));
            }).size(280, 20).build()
        );
    }
}
