package org.valkyrienskies.mod.mixin.server;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import kotlin.Unit;
import net.minecraft.class_1923;
import net.minecraft.class_1937;
import net.minecraft.class_2248;
import net.minecraft.class_2424;
import net.minecraft.class_243;
import net.minecraft.class_2680;
import net.minecraft.class_2818;
import net.minecraft.class_3218;
import net.minecraft.class_3324;
import net.minecraft.class_4048;
import net.minecraft.class_4284;
import net.minecraft.class_5321;
import net.minecraft.class_18.class_8645;
import net.minecraft.class_2350.class_2351;
import net.minecraft.class_5459.class_5460;
import net.minecraft.server.MinecraftServer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.valkyrienskies.core.api.ships.LoadedServerShip;
import org.valkyrienskies.core.api.ships.properties.IShipActiveChunksSet;
import org.valkyrienskies.core.api.world.ShipWorld;
import org.valkyrienskies.core.internal.VsiGameServer;
import org.valkyrienskies.core.internal.world.VsiPipeline;
import org.valkyrienskies.core.internal.world.VsiPlayer;
import org.valkyrienskies.core.internal.world.VsiServerShipWorld;
import org.valkyrienskies.mod.common.IShipObjectWorldServerProvider;
import org.valkyrienskies.mod.common.ShipSavedData;
import org.valkyrienskies.mod.common.VSGameUtilsKt;
import org.valkyrienskies.mod.common.ValkyrienSkiesMod;
import org.valkyrienskies.mod.common.config.MassDatapackResolver;
import org.valkyrienskies.mod.common.hooks.VSGameEvents;
import org.valkyrienskies.mod.common.util.EntityDragger;
import org.valkyrienskies.mod.common.util.VSLevelChunk;
import org.valkyrienskies.mod.common.util.VSServerLevel;
import org.valkyrienskies.mod.common.world.ChunkManagement;
import org.valkyrienskies.mod.common.world.ShipActivationManager;
import org.valkyrienskies.mod.common.world.ShipPlantMower;
import org.valkyrienskies.mod.common.world.VSTicketType;
import org.valkyrienskies.mod.compat.LoadedMods;
import org.valkyrienskies.mod.compat.Weather2Compat;
import org.valkyrienskies.mod.util.KrunchSupport;
import org.valkyrienskies.mod.util.McMathUtilKt;

@Mixin(MinecraftServer.class)
public abstract class MixinMinecraftServer implements IShipObjectWorldServerProvider, VsiGameServer {
   @Shadow
   private class_3324 field_4550;
   @Unique
   private VsiServerShipWorld shipWorld;
   @Unique
   private VsiPipeline vsPipeline;
   @Unique
   private Set<String> loadedLevels = new HashSet<>();
   @Unique
   private final Map<String, class_3218> dimensionToLevelMap = new HashMap<>();

   @Shadow
   public abstract class_3218 method_30002();

   @Shadow
   public abstract Iterable<class_3218> method_3738();

   @Inject(at = @At(value = "INVOKE", target = "Lnet/minecraft/server/MinecraftServer;initServer()Z"), method = "runServer")
   private void beforeInitServer(CallbackInfo info) {
      ValkyrienSkiesMod.setCurrentServer(MinecraftServer.class.cast(this));
   }

   @Inject(at = @At("TAIL"), method = "stopServer")
   private void afterStopServer(CallbackInfo ci) {
      ValkyrienSkiesMod.setCurrentServer(null);
   }

   @Nullable
   @Override
   public VsiServerShipWorld getShipObjectWorld() {
      return this.shipWorld;
   }

   @Nullable
   @Override
   public VsiPipeline getVsPipeline() {
      return this.vsPipeline;
   }

   @Inject(
      method = "createLevels",
      at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerLevel;getDataStorage()Lnet/minecraft/world/level/storage/DimensionDataStorage;")
   )
   private void postCreateLevels(CallbackInfo ci) {
      if (!MassDatapackResolver.INSTANCE.getRegisteredBlocks()) {
         List<class_2680> blockStateList = new ArrayList<>(class_2248.field_10651.method_10204());
         class_2248.field_10651.forEach(blockStateList::add);
         MassDatapackResolver.INSTANCE.registerAllBlockStates(blockStateList);
         ValkyrienSkiesMod.getVsCore().registerBlockStates(MassDatapackResolver.INSTANCE.getBlockStateData());
      }

      class_8645<ShipSavedData> factory = new class_8645(
         ShipSavedData.Companion::createEmpty, (tag, provider) -> ShipSavedData.load(tag), class_4284.field_19212
      );
      ShipSavedData shipSavedData = (ShipSavedData)this.method_30002().method_17983().method_17924(factory, "vs_ship_data");
      Throwable ex = shipSavedData.getLoadingException();
      if (ex != null) {
         System.err.println("VALKYRIEN SKIES ERROR WHILE LOADING SHIP DATA");
         ex.printStackTrace();
         throw new RuntimeException(ex);
      }

      this.vsPipeline = shipSavedData.getPipeline();
      KrunchSupport.INSTANCE.setKrunchSupported(!this.vsPipeline.isUsingDummyPhysics());
      this.shipWorld = this.vsPipeline.getShipWorld();
      this.shipWorld.setGameServer(this);
      VSGameEvents.INSTANCE.getRegistriesCompleted().emit(Unit.INSTANCE);
      this.getShipObjectWorld()
         .addDimension(
            VSGameUtilsKt.getDimensionId(this.method_30002()),
            VSGameUtilsKt.getYRange(this.method_30002()),
            McMathUtilKt.getDEFAULT_WORLD_GRAVITY(),
            62.0,
            962.0
         );
   }

   @Inject(method = "tickServer", at = @At("HEAD"))
   private void preTick(CallbackInfo ci) {
      Set<VsiPlayer> vsPlayers = this.field_4550.method_14571().stream().map(VSGameUtilsKt::getPlayerWrapper).collect(Collectors.toSet());
      vsPlayers.addAll(ShipActivationManager.activeShipObservers(this.shipWorld, MinecraftServer.class.cast(this)));
      this.shipWorld.setPlayers(vsPlayers);
      Map<String, class_3218> newLoadedLevels = new HashMap<>();

      for (class_3218 level : this.method_3738()) {
         String dimensionId = VSGameUtilsKt.getDimensionId(level);
         newLoadedLevels.put(dimensionId, level);
         this.dimensionToLevelMap.put(dimensionId, level);
      }

      for (String oldLoadedLevelId : this.loadedLevels) {
         if (!newLoadedLevels.containsKey(oldLoadedLevelId)) {
            this.shipWorld.removeDimension(oldLoadedLevelId);
            this.dimensionToLevelMap.remove(oldLoadedLevelId);
         }
      }

      this.loadedLevels = newLoadedLevels.keySet();
      this.vsPipeline.preTickGame();
   }

   @Inject(method = "tickChildren", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/network/ServerConnectionListener;tick()V", shift = Shift.AFTER))
   private void preConnectionTick(CallbackInfo ci) {
      ChunkManagement.tickChunkLoading(this.shipWorld, MinecraftServer.class.cast(this));
      ShipActivationManager.tick(this.shipWorld, MinecraftServer.class.cast(this));
      ShipPlantMower.tick(this.shipWorld, MinecraftServer.class.cast(this));
   }

   @Shadow
   public abstract class_3218 method_3847(class_5321<class_1937> var1);

   @Inject(method = "tickServer", at = @At("TAIL"))
   private void postTick(CallbackInfo ci) {
      this.vsPipeline.postTickGame();
      boolean anyShips = this.shipWorld != null && this.shipWorld.getAllShips().size() > 0;

      for (class_3218 level : this.method_3738()) {
         if (anyShips) {
            EntityDragger.INSTANCE.dragEntitiesWithShips(level.method_27909(), false);
         }

         if (LoadedMods.getWeather2()) {
            Weather2Compat.INSTANCE.tick(level);
         }
      }
   }

   @Unique
   private class_243 getRelativePortalPosition(class_2351 axis, class_5460 foundRectangle, class_4048 entityDimensions, class_243 position) {
      return class_2424.method_30494(foundRectangle, axis, position, entityDimensions);
   }

   @Inject(method = "stopServer", at = @At("HEAD"))
   private void preStopServer(CallbackInfo ci) {
      if (this.vsPipeline != null) {
         this.vsPipeline.setDeleteResources(true);
         this.vsPipeline.setArePhysicsRunning(true);
      }

      if (this.shipWorld != null) {
         ShipActivationManager.clearAll(MinecraftServer.class.cast(this));

         for (LoadedServerShip ship : this.shipWorld.getLoadedShips()) {
            class_3218 level = this.dimensionToLevelMap.get(ship.getChunkClaimDimension());
            if (level != null) {
               ship.getActiveChunksSet().forEach((x, z) -> {
                  class_1923 cp = new class_1923(x, z);
                  level.method_14178().method_17300(VSTicketType.SHIP_CHUNK, cp, 0, cp);
                  level.method_14178().method_12124(cp, false);
               });
            }
         }
      }
   }

   @Inject(method = "stopServer", at = @At("RETURN"))
   private void postStopServer(CallbackInfo ci) {
      this.dimensionToLevelMap.clear();
      if (this.shipWorld != null) {
         this.shipWorld.setGameServer(null);
         this.shipWorld = null;
      }
   }

   @NotNull
   private class_3218 getLevelFromDimensionId(@NotNull String dimensionId) {
      return this.dimensionToLevelMap.get(dimensionId);
   }

   @Override
   public void moveTerrainAcrossDimensions(@NotNull IShipActiveChunksSet shipChunks, @NotNull String srcDimension, @NotNull String destDimension) {
      class_3218 srcLevel = this.getLevelFromDimensionId(srcDimension);
      class_3218 destLevel = this.getLevelFromDimensionId(destDimension);
      shipChunks.forEach((x, z) -> {
         class_2818 srcChunk = srcLevel.method_8497(x, z);
         ((VSServerLevel)destLevel).removeChunk(x, z);
         class_2818 destChunk = destLevel.method_8497(x, z);
         ((VSLevelChunk)destChunk).copyChunkFromOtherDimension((VSLevelChunk)srcChunk);
      });
      shipChunks.forEach((x, z) -> {
         class_2818 srcChunk = srcLevel.method_8497(x, z);
         ((VSLevelChunk)srcChunk).clearChunk();
         class_1923 chunkPos = srcChunk.method_12004();
         srcLevel.method_14178().method_12124(chunkPos, false);
         ((VSServerLevel)srcLevel).removeChunk(x, z);
      });
   }

   // $VF: synthetic method
   // $VF: bridge method
   @Nullable
   @Override
   public ShipWorld getShipObjectWorld() {
      return this.getShipObjectWorld();
   }
}
