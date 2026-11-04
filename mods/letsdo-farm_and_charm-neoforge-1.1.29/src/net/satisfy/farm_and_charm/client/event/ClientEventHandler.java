package net.satisfy.farm_and_charm.client.event;

import dev.architectury.event.events.client.ClientTickEvent;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.ints.IntIterator;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.Entity;
import net.satisfy.farm_and_charm.client.util.CartWheelUtil;
import net.satisfy.farm_and_charm.core.entity.AbstractCartEntity;
import net.satisfy.farm_and_charm.core.registry.SoundEventRegistry;

public final class ClientEventHandler {
   private static final Int2ObjectOpenHashMap<List<CartWheelUtil.CartWheel>> WHEELS = new Int2ObjectOpenHashMap();

   private ClientEventHandler() {
   }

   public static void init() {
      ClientTickEvent.CLIENT_POST.register(ClientEventHandler::onClientTick);
   }

   private static void onClientTick(Minecraft minecraft) {
      if (minecraft.level != null && !minecraft.isPaused()) {
         ClientLevel level = minecraft.level;
         IntOpenHashSet aliveCartIds = new IntOpenHashSet();

         for (Entity entity : level.entitiesForRendering()) {
            if (entity instanceof AbstractCartEntity cart) {
               aliveCartIds.add(cart.getId());
               Entity puller = cart.getPulling();
               if (puller == minecraft.player) {
                  cart.pulledPostTick();
               }

               List<CartWheelUtil.CartWheel> wheels = (List<CartWheelUtil.CartWheel>)WHEELS.get(cart.getId());
               if (wheels == null) {
                  wheels = CartWheelUtil.createDefaultWheels(cart);
                  WHEELS.put(cart.getId(), wheels);
               }

               CartWheelUtil.tickCart(cart, wheels, (SoundEvent)SoundEventRegistry.CART_MOVING.get());
            }
         }

         cleanupDeadCarts(aliveCartIds);
      }
   }

   private static void cleanupDeadCarts(IntOpenHashSet aliveCartIds) {
      IntIterator iterator = WHEELS.keySet().iterator();

      while (iterator.hasNext()) {
         int cartId = iterator.nextInt();
         if (!aliveCartIds.contains(cartId)) {
            iterator.remove();
            CartWheelUtil.stopCartById(cartId);
         }
      }
   }
}
