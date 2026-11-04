package de.maxhenkel.gravestone.events;

import de.maxhenkel.gravestone.GraveUtils;
import de.maxhenkel.gravestone.Main;
import de.maxhenkel.gravestone.blocks.GraveStoneBlock;
import de.maxhenkel.gravestone.corelib.death.Death;
import de.maxhenkel.gravestone.corelib.death.PlayerDeathEvent;
import de.maxhenkel.gravestone.items.ObituaryItem;
import de.maxhenkel.gravestone.tileentity.GraveStoneTileEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent.Clone;

public class DeathEvents {
   public DeathEvents() {
      de.maxhenkel.gravestone.corelib.death.DeathEvents.register();
   }

   @SubscribeEvent
   public void playerDeath(PlayerDeathEvent event) {
      event.storeDeath();
      Death death = event.getDeath();
      Player player = event.getPlayer();
      Level world = player.level();
      if (!keepInventory(player)) {
         Main.LOGGER.info("The death ID of player {} is {}", death.getPlayerName(), death.getId());
         BlockPos graveStoneLocation = GraveUtils.getGraveStoneLocation(world, death.getBlockPos());
         if ((Boolean)Main.SERVER_CONFIG.giveObituaries.get()) {
            player.getInventory().add(((ObituaryItem)Main.OBITUARY.get()).toStack(death));
         }

         if (graveStoneLocation == null) {
            Main.LOGGER.info("Grave of '{}' can't be placed (No space)", death.getPlayerName());
            Main.LOGGER.info("The death ID of '{}' is {}", death.getPlayerName(), death.getId().toString());
         } else {
            world.setBlockAndUpdate(
               graveStoneLocation,
               (BlockState)((GraveStoneBlock)Main.GRAVESTONE.get()).defaultBlockState().setValue(GraveStoneBlock.FACING, player.getDirection().getOpposite())
            );
            if (GraveUtils.isReplaceable(world, graveStoneLocation.below())) {
               world.setBlockAndUpdate(graveStoneLocation.below(), Blocks.DIRT.defaultBlockState());
            }

            if (world.getBlockEntity(graveStoneLocation) instanceof GraveStoneTileEntity gravestone) {
               gravestone.setDeath(death);
               event.removeDrops();
            } else {
               Main.LOGGER.info("Grave of '{}' can't be filled with loot (No tileentity found)", death.getPlayerName());
               Main.LOGGER.info("The death ID of '{}' is {}", death.getPlayerName(), death.getId().toString());
            }
         }
      }
   }

   @SubscribeEvent(priority = EventPriority.LOWEST)
   public void onPlayerCloneLast(Clone event) {
      if ((Boolean)Main.SERVER_CONFIG.giveObituaries.get()) {
         if (event.isWasDeath()) {
            if (!keepInventory(event.getEntity())) {
               for (ItemStack stack : event.getOriginal().getInventory().items) {
                  if (stack.getItem() instanceof ObituaryItem) {
                     event.getEntity().getInventory().add(stack);
                  }
               }
            }
         }
      }
   }

   public static boolean keepInventory(Player player) {
      try {
         return player.getCommandSenderWorld().getLevelData().getGameRules().getBoolean(GameRules.RULE_KEEPINVENTORY);
      } catch (Exception e) {
         return false;
      }
   }
}
