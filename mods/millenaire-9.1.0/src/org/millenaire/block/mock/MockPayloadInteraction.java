package org.millenaire.block.mock;

import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent.RightClickBlock;

public final class MockPayloadInteraction {
   private MockPayloadInteraction() {
   }

   public static void onRightClickBlock(RightClickBlock event) {
      Player player = event.getEntity();
      Level level = event.getLevel();
      if (level.getBlockState(event.getPos()).getBlock() instanceof MockPayloadBlock block) {
         if (player.getAbilities().instabuild) {
            if (block.authorsWhileSneaking() || !player.isSecondaryUseActive() || event.getItemStack().isEmpty()) {
               event.setCanceled(true);
               event.setCancellationResult(InteractionResult.sidedSuccess(level.isClientSide));
               if (!level.isClientSide) {
                  if (level.getBlockEntity(event.getPos()) instanceof MockPayloadBlockEntity be) {
                     ItemStack held = event.getItemStack();
                     if (held.isEmpty()) {
                        String current = be.getPayload();
                        player.displayClientMessage(
                           current.isEmpty()
                              ? Component.translatable("block.millenaire.mock_generic.chat.not_set")
                              : Component.translatable("block.millenaire.mock_generic.chat.current", new Object[]{current}),
                           false
                        );
                     } else {
                        String payload = block.payloadFor(held, new BlockPlaceContext(player, event.getHand(), held, event.getHitVec()));
                        if (payload == null) {
                           player.displayClientMessage(Component.translatable(block.acceptedItemsKey()), false);
                        } else {
                           be.setPayload(payload);
                           player.displayClientMessage(Component.translatable("block.millenaire.mock_generic.chat.set", new Object[]{payload}), false);
                        }
                     }
                  }
               }
            }
         }
      }
   }
}
