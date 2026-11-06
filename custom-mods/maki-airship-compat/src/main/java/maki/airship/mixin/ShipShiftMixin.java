package maki.airship.mixin;

import maki.airship.ShipShiftDecision;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.protocol.game.ServerboundPlayerCommandPacket;
import net.minecraft.network.protocol.game.ServerboundPlayerCommandPacket.Action;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.valkyrienskies.mod.common.entity.ShipMountingEntity;

@Mixin(value = LocalPlayer.class, remap = false)
public abstract class ShipShiftMixin {
    @Shadow @Final public ClientPacketListener connection;
    @Shadow private boolean wasShiftKeyDown;
    @Unique private boolean maki$wasShipMount;

    @Inject(method = "tick()V", at = @At("TAIL"), remap = false)
    private void maki$syncShipShift(CallbackInfo ci) {
        LocalPlayer player = (LocalPlayer) (Object) this;
        boolean shipMount = player.getVehicle() instanceof ShipMountingEntity;
        boolean shiftDown = player.isShiftKeyDown();
        if (ShipShiftDecision.send(shipMount, maki$wasShipMount, shiftDown, wasShiftKeyDown)) {
            connection.send(new ServerboundPlayerCommandPacket(
                player, shiftDown ? Action.PRESS_SHIFT_KEY : Action.RELEASE_SHIFT_KEY));
            wasShiftKeyDown = shiftDown;
        }
        maki$wasShipMount = shipMount;
    }
}
