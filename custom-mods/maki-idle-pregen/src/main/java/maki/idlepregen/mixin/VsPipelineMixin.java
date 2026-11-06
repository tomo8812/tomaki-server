package maki.idlepregen.mixin;

import java.util.concurrent.ConcurrentLinkedQueue;
import maki.idlepregen.IdlePregenHooks;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Pseudo
@Mixin(targets = "org.valkyrienskies.core.impl.shadow.FQ", remap = false)
public abstract class VsPipelineMixin {
    @Redirect(
            method = "postTickGame",
            at = @At(
                    value = "INVOKE",
                    target = "Ljava/util/concurrent/ConcurrentLinkedQueue;add(Ljava/lang/Object;)Z"
            ),
            remap = false
    )
    private boolean maki$skipPhysicsFrameEnqueue(
            ConcurrentLinkedQueue<Object> queue,
            Object gameFrame
    ) {
        if (IdlePregenHooks.suppressVsGameFrame()) {
            // Keep VS postTickGame() itself running: it advances the internal
            // game-stage checker and clears per-tick change state. Only the final
            // handoff to the paused physics thread is suppressed.
            return true;
        }
        return queue.add(gameFrame);
    }
}
