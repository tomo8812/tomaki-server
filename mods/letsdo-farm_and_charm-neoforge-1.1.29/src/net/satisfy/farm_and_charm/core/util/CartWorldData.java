package net.satisfy.farm_and_charm.core.util;

import dev.architectury.event.events.common.TickEvent;
import dev.architectury.event.events.common.TickEvent.ServerLevelTick;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import java.util.ArrayList;
import java.util.Objects;
import java.util.UUID;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.world.entity.animal.horse.AbstractHorse;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.saveddata.SavedData.Factory;
import net.minecraft.world.phys.Vec3;
import net.satisfy.farm_and_charm.FarmAndCharm;
import net.satisfy.farm_and_charm.core.entity.AbstractCartEntity;
import org.jetbrains.annotations.NotNull;

public class CartWorldData extends SavedData {
   private static boolean initialized;
   private static CartWorldData clientInstance;
   private static final ResourceLocation CART_SLOW_ID = FarmAndCharm.identifier("cart_slow");
   private final Int2ObjectMap<AbstractCartEntity> pulling = new Int2ObjectOpenHashMap();
   private final ArrayList<UUID> pendingCartUuids = new ArrayList<>();
   private final Int2ObjectMap<Vec3> horsePreTickPositions = new Int2ObjectOpenHashMap();
   private final IntOpenHashSet slowedPlayers = new IntOpenHashSet();
   private final IntOpenHashSet slowedHorses = new IntOpenHashSet();

   public static void ensureInitialized() {
      if (!initialized) {
         initialized = true;
         TickEvent.SERVER_LEVEL_PRE.register((ServerLevelTick)level -> get(level).preTick(level));
         TickEvent.SERVER_LEVEL_POST.register((ServerLevelTick)level -> get(level).tick(level));
      }
   }

   public boolean canAttach(Entity puller, AbstractCartEntity cart) {
      if (puller != null && cart != null) {
         AbstractCartEntity existing = (AbstractCartEntity)this.pulling.get(puller.getId());
         return existing == null || existing == cart;
      } else {
         return false;
      }
   }

   public AbstractCartEntity getCartPulledBy(Entity puller) {
      return puller == null ? null : (AbstractCartEntity)this.pulling.get(puller.getId());
   }

   public void addPulling(AbstractCartEntity cart) {
      Entity puller = cart.getPulling();
      if (puller != null) {
         AbstractCartEntity existing = (AbstractCartEntity)this.pulling.get(puller.getId());
         if (existing == null || existing == cart) {
            this.pulling.put(puller.getId(), cart);
            this.setDirty();
         }
      }
   }

   public void preTick(ServerLevel level) {
      this.horsePreTickPositions.clear();
      ArrayList<AbstractCartEntity> cartsSnapshot = new ArrayList<>(Math.max(0, this.pulling.size()));
      ObjectIterator var3 = this.pulling.values().iterator();

      while (var3.hasNext()) {
         AbstractCartEntity cart = (AbstractCartEntity)var3.next();
         if (cart != null) {
            cartsSnapshot.add(cart);
            if (cart.getPulling() instanceof AbstractHorse horse) {
               this.horsePreTickPositions.put(horse.getId(), horse.position());
            }
         }
      }

      for (AbstractCartEntity cart : cartsSnapshot) {
         cart.pulledPostTick();
         if (cart.shouldStopPulledTick()) {
            this.removePullingByCart(cart);
         }
      }
   }

   public void tick(ServerLevel level) {
      if (!this.pendingCartUuids.isEmpty()) {
         for (int index = this.pendingCartUuids.size() - 1; index >= 0; index--) {
            UUID cartUuid = this.pendingCartUuids.get(index);
            if (level.getEntity(cartUuid) instanceof AbstractCartEntity cart) {
               if (cart.getPulling() != null && this.canAttach(cart.getPulling(), cart)) {
                  this.addPulling(cart);
               }

               this.pendingCartUuids.remove(index);
            }
         }
      }

      IntOpenHashSet currentPlayers = new IntOpenHashSet();
      IntOpenHashSet currentHorses = new IntOpenHashSet();

      for (int pullId : this.pulling.keySet().toIntArray()) {
         AbstractCartEntity cart = (AbstractCartEntity)this.pulling.get(pullId);
         if (cart != null && !cart.shouldStopPulledTick()) {
            Entity puller = cart.getPulling();
            if (puller instanceof Player player) {
               this.applyCartSlow(player);
               currentPlayers.add(player.getId());
            } else if (puller instanceof AbstractHorse horse) {
               this.applyCartSlow(horse);
               currentHorses.add(horse.getId());
               Entity controller = cart.getCartDriver();
               boolean controlled = controller instanceof Player controllingPlayer && controllingPlayer.getVehicle() == cart;
               if (controlled) {
                  this.suppressHorseIdleAnimations(horse);
                  this.driveHorseFromCart(cart, horse, (Player)controller);
               } else {
                  Vec3 prePos = (Vec3)this.horsePreTickPositions.get(horse.getId());
                  if (prePos != null) {
                     Vec3 current = horse.position();
                     double dx = current.x - prePos.x;
                     double dz = current.z - prePos.z;
                     if (dx * dx + dz * dz > 1.0E-10) {
                        horse.setPos(prePos.x, current.y, prePos.z);
                     }
                  }

                  horse.setDeltaMovement(0.0, horse.getDeltaMovement().y, 0.0);
                  cart.setCartSpeed(0.0F);
               }
            }

            if (!(cart.getPulling() instanceof AbstractCartEntity)) {
               cart.pulledPostTick();
            }
         } else {
            this.pulling.remove(pullId);
            this.setDirty();
         }
      }

      this.cleanupSlowness(level, currentPlayers, currentHorses);
   }

   private void driveHorseFromCart(AbstractCartEntity cart, AbstractHorse horse, Player player) {
      float input = player.zza;
      float speed = cart.getCartSpeed();
      float accel = 0.02F;
      float decel = 0.03F;
      float maxSpeed = 0.23F;
      if (input > 0.001F) {
         speed = Mth.clamp(speed + accel * input, 0.0F, maxSpeed);
      } else if (input < -0.001F) {
         speed = Mth.clamp(speed + decel * input, 0.0F, maxSpeed);
      } else {
         speed = Mth.clamp(speed - 0.01F, 0.0F, maxSpeed);
      }

      cart.setCartSpeed(speed);
      float yaw = player.getYRot();
      horse.setYRot(yaw);
      horse.yRotO = yaw;
      horse.setYHeadRot(yaw);
      horse.yHeadRotO = yaw;
      horse.getNavigation().stop();
      if (speed <= 1.0E-4F) {
         horse.setDeltaMovement(0.0, horse.getDeltaMovement().y, 0.0);
      } else {
         float yawRad = yaw * (float) (Math.PI / 180.0);
         double forwardX = -Mth.sin(yawRad);
         double forwardZ = Mth.cos(yawRad);
         Vec3 delta = new Vec3(forwardX * speed, 0.0, forwardZ * speed);
         horse.setDeltaMovement(delta.x, horse.getDeltaMovement().y, delta.z);
         horse.move(MoverType.SELF, new Vec3(delta.x, 0.0, delta.z));
         horse.hasImpulse = true;
      }
   }

   private void suppressHorseIdleAnimations(AbstractHorse horse) {
      horse.setEating(false);
      horse.setStanding(false);
   }

   private void applyCartSlow(Entity entity) {
      if (entity instanceof LivingEntity livingEntity) {
         AttributeInstance attr = livingEntity.getAttribute(Attributes.MOVEMENT_SPEED);
         if (attr != null) {
            AttributeModifier existing = attr.getModifier(CART_SLOW_ID);
            if (existing == null) {
               attr.addTransientModifier(new AttributeModifier(CART_SLOW_ID, -0.1, Operation.ADD_MULTIPLIED_TOTAL));
            }
         }
      }
   }

   private void removeCartSlow(Entity entity) {
      if (entity instanceof LivingEntity livingEntity) {
         AttributeInstance attr = livingEntity.getAttribute(Attributes.MOVEMENT_SPEED);
         if (attr != null) {
            AttributeModifier existing = attr.getModifier(CART_SLOW_ID);
            if (existing != null) {
               attr.removeModifier(CART_SLOW_ID);
            }
         }
      }
   }

   private void cleanupSlowness(ServerLevel level, IntOpenHashSet currentPlayers, IntOpenHashSet currentHorses) {
      int[] playerIds = this.slowedPlayers.toIntArray();

      for (int playerId : playerIds) {
         if (!currentPlayers.contains(playerId)) {
            Entity entity = level.getEntity(playerId);
            if (entity != null) {
               this.removeCartSlow(entity);
            }

            this.slowedPlayers.remove(playerId);
         }
      }

      int[] horseIds = this.slowedHorses.toIntArray();

      for (int horseId : horseIds) {
         if (!currentHorses.contains(horseId)) {
            Entity entity = level.getEntity(horseId);
            if (entity != null) {
               this.removeCartSlow(entity);
            }

            this.slowedHorses.remove(horseId);
         }
      }

      this.slowedPlayers.addAll(currentPlayers);
      this.slowedHorses.addAll(currentHorses);
   }

   public void tickClient(Level level) {
      for (int pullId : this.pulling.keySet().toIntArray()) {
         AbstractCartEntity cart = (AbstractCartEntity)this.pulling.get(pullId);
         if (cart != null && cart.isAlive()) {
            Entity puller = cart.getPulling();
            if (puller != null && puller.isAlive()) {
               cart.pulledPostTick();
            } else {
               this.pulling.remove(pullId);
            }
         } else {
            this.pulling.remove(pullId);
         }
      }
   }

   @NotNull
   public CompoundTag save(CompoundTag tag, Provider provider) {
      ListTag cartList = new ListTag();
      ObjectIterator var4 = this.pulling.values().iterator();

      while (var4.hasNext()) {
         AbstractCartEntity cart = (AbstractCartEntity)var4.next();
         cartList.add(NbtUtils.createUUID(cart.getUUID()));
      }

      tag.put("cartList", cartList);
      return tag;
   }

   public static CartWorldData createFromNbt(CompoundTag tag) {
      CartWorldData data = new CartWorldData();

      for (Tag value : tag.getList("cartList", 11)) {
         data.pendingCartUuids.add(NbtUtils.loadUUID(value));
      }

      return data;
   }

   public static CartWorldData get(Level level) {
      ensureInitialized();
      if (level.isClientSide()) {
         if (clientInstance == null) {
            clientInstance = new CartWorldData();
         }

         return clientInstance;
      } else {
         return getServer(Objects.requireNonNull(level.getServer()), level.dimension());
      }
   }

   private static CartWorldData getServer(MinecraftServer server, ResourceKey<Level> levelType) {
      ServerLevel level = Objects.requireNonNull(server.getLevel(levelType));
      Factory<CartWorldData> factory = new Factory(CartWorldData::new, (tag, provider) -> createFromNbt(tag), null);
      return (CartWorldData)level.getDataStorage().computeIfAbsent(factory, "farm_and_charm_cart_world");
   }

   public void removePullingByCart(AbstractCartEntity cart) {
      for (int pullId : this.pulling.keySet().toIntArray()) {
         if (this.pulling.get(pullId) == cart) {
            this.pulling.remove(pullId);
            this.setDirty();
         }
      }
   }
}
