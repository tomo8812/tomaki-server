package net.satisfy.brewery.core.event;

import dev.architectury.event.EventResult;
import dev.architectury.event.events.common.PlayerEvent.AttackEntity;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import java.util.List;
import java.util.Random;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.FireworkRocketEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.FireworkExplosion;
import net.minecraft.world.item.component.Fireworks;
import net.minecraft.world.item.component.FireworkExplosion.Shape;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.satisfy.brewery.core.registry.MobEffectRegistry;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;

public class PartyStarterEvent implements AttackEntity {
   private final Random random = new Random();

   public EventResult attack(Player player, Level level, Entity target, InteractionHand hand, @Nullable EntityHitResult result) {
      if (player.hasEffect(BuiltInRegistries.MOB_EFFECT.wrapAsHolder((MobEffect)MobEffectRegistry.PARTYSTARTER.get())) && target instanceof LivingEntity entity
         )
       {
         for (int i = 0; i < 3; i++) {
            ItemStack fireworkStack = new ItemStack(Items.FIREWORK_ROCKET);
            Fireworks fireworks = this.createFireworks(this.random);
            fireworkStack.set(DataComponents.FIREWORKS, fireworks);
            FireworkRocketEntity fireworkRocket = new FireworkRocketEntity(level, fireworkStack, entity);
            if (!level.isClientSide) {
               level.addFreshEntity(fireworkRocket);
               level.broadcastEntityEvent(fireworkRocket, (byte)17);
               fireworkRocket.discard();
            }
         }

         return EventResult.pass();
      } else {
         return EventResult.pass();
      }
   }

   private Fireworks createFireworks(Random random) {
      Vector3f rgb = new Vector3f(random.nextFloat(), random.nextFloat(), random.nextFloat());
      int r = (int)(rgb.x * 255.0F);
      int g = (int)(rgb.y * 255.0F);
      int b = (int)(rgb.z * 255.0F);
      int color = r << 16 | g << 8 | b;
      IntList colors = new IntArrayList();
      colors.add(color);
      FireworkExplosion explosion = new FireworkExplosion(Shape.BURST, colors, IntList.of(), false, false);
      return new Fireworks(0, List.of(explosion));
   }
}
