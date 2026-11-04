package net.caffeinemc.mods.lithium.mixin.world.explosions.block_raycast;

import it.unimi.dsi.fastutil.longs.LongIterator;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Optional;
import net.caffeinemc.mods.lithium.common.util.Pos;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.BlockPos.MutableBlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.ExplosionDamageCalculator;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.Explosion.BlockInteraction;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.LevelChunkSection;
import net.minecraft.world.level.dimension.BuiltinDimensionTypes;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Explosion.class)
public abstract class ExplosionMixin {
   @Shadow
   @Final
   private float radius;
   @Shadow
   @Final
   private double x;
   @Shadow
   @Final
   private double y;
   @Shadow
   @Final
   private double z;
   @Shadow
   @Final
   private Level level;
   @Shadow
   @Final
   private ExplosionDamageCalculator damageCalculator;
   @Shadow
   @Final
   private boolean fire;
   private final MutableBlockPos cachedPos = new MutableBlockPos();
   private int prevChunkX = Integer.MIN_VALUE;
   private int prevChunkZ = Integer.MIN_VALUE;
   private ChunkAccess prevChunk;
   private boolean explodeAirBlocks;
   private int bottomY;
   private int topY;

   @Inject(
      method = "<init>(Lnet/minecraft/world/level/Level;Lnet/minecraft/world/entity/Entity;Lnet/minecraft/world/damagesource/DamageSource;Lnet/minecraft/world/level/ExplosionDamageCalculator;DDDFZLnet/minecraft/world/level/Explosion$BlockInteraction;Lnet/minecraft/core/particles/ParticleOptions;Lnet/minecraft/core/particles/ParticleOptions;Lnet/minecraft/core/Holder;)V",
      at = @At("TAIL")
   )
   private void init(
      Level world,
      Entity entity,
      DamageSource damageSource,
      ExplosionDamageCalculator behavior,
      double x,
      double y,
      double z,
      float power,
      boolean createFire,
      BlockInteraction destructionType,
      ParticleOptions particle,
      ParticleOptions emitterParticle,
      Holder<?> soundEvent,
      CallbackInfo ci
   ) {
      this.bottomY = this.level.getMinBuildHeight();
      this.topY = this.level.getMaxBuildHeight();
      boolean explodeAir = this.fire;
      if (!explodeAir && this.level.dimension() == Level.END && this.level.dimensionTypeRegistration().is(BuiltinDimensionTypes.END)) {
         float overestimatedExplosionRange = 8 + (int)(6.0F * this.radius);
         int endPortalX = 0;
         int endPortalZ = 0;
         if (overestimatedExplosionRange > Math.abs(this.x - endPortalX) && overestimatedExplosionRange > Math.abs(this.z - endPortalZ)) {
            explodeAir = true;
         }
      }

      this.explodeAirBlocks = explodeAir;
   }

   @Redirect(method = "explode()V", at = @At(value = "INVOKE", target = "Lcom/google/common/collect/Sets;newHashSet()Ljava/util/HashSet;", remap = false))
   public HashSet<BlockPos> skipNewHashSet() {
      return null;
   }

   @ModifyConstant(method = "explode()V", constant = @Constant(intValue = 16, ordinal = 1))
   public int skipLoop(int prevValue) {
      return 0;
   }

   @Redirect(
      method = "explode()V",
      at = @At(value = "INVOKE", target = "Lit/unimi/dsi/fastutil/objects/ObjectArrayList;addAll(Ljava/util/Collection;)Z", remap = false)
   )
   public boolean collectBlocks(ObjectArrayList<BlockPos> affectedBlocks, Collection<BlockPos> collection) {
      LongOpenHashSet touched = new LongOpenHashSet(0);
      RandomSource random = this.level.random;

      for (int rayX = 0; rayX < 16; rayX++) {
         boolean xPlane = rayX == 0 || rayX == 15;
         double vecX = rayX / 15.0F * 2.0F - 1.0F;

         for (int rayY = 0; rayY < 16; rayY++) {
            boolean yPlane = rayY == 0 || rayY == 15;
            double vecY = rayY / 15.0F * 2.0F - 1.0F;

            for (int rayZ = 0; rayZ < 16; rayZ++) {
               boolean zPlane = rayZ == 0 || rayZ == 15;
               if (xPlane || yPlane || zPlane) {
                  double vecZ = rayZ / 15.0F * 2.0F - 1.0F;
                  this.performRayCast(random, vecX, vecY, vecZ, touched);
               }
            }
         }
      }

      LongIterator it = touched.iterator();
      boolean added = false;

      while (it.hasNext()) {
         added |= affectedBlocks.add(BlockPos.of(it.nextLong()));
      }

      return added;
   }

   @Unique
   private void performRayCast(RandomSource random, double vecX, double vecY, double vecZ, LongOpenHashSet touched) {
      double dist = Math.sqrt(vecX * vecX + vecY * vecY + vecZ * vecZ);
      double normX = vecX / dist * 0.3;
      double normY = vecY / dist * 0.3;
      double normZ = vecZ / dist * 0.3;
      float strength = this.radius * (0.7F + random.nextFloat() * 0.6F);
      double stepX = this.x;
      double stepY = this.y;
      double stepZ = this.z;
      int prevX = Integer.MIN_VALUE;
      int prevY = Integer.MIN_VALUE;
      int prevZ = Integer.MIN_VALUE;
      float prevResistance = 0.0F;
      int boundMinY = this.bottomY;
      int boundMaxY = this.topY;

      while (strength > 0.0F) {
         int blockX = Mth.floor(stepX);
         int blockY = Mth.floor(stepY);
         int blockZ = Mth.floor(stepZ);
         float resistance;
         if (prevX == blockX && prevY == blockY && prevZ == blockZ) {
            resistance = prevResistance;
         } else {
            if (blockY < boundMinY || blockY >= boundMaxY || blockX < -30000000 || blockZ < -30000000 || blockX >= 30000000 || blockZ >= 30000000) {
               return;
            }

            resistance = this.traverseBlock(strength, blockX, blockY, blockZ, touched);
            prevX = blockX;
            prevY = blockY;
            prevZ = blockZ;
            prevResistance = resistance;
         }

         strength -= resistance;
         strength -= 0.22500001F;
         stepX += normX;
         stepY += normY;
         stepZ += normZ;
      }
   }

   @Unique
   private float traverseBlock(float strength, int blockX, int blockY, int blockZ, LongOpenHashSet touched) {
      BlockPos pos = this.cachedPos.set(blockX, blockY, blockZ);
      int chunkX = Pos.ChunkCoord.fromBlockCoord(blockX);
      int chunkZ = Pos.ChunkCoord.fromBlockCoord(blockZ);
      if (this.prevChunkX != chunkX || this.prevChunkZ != chunkZ) {
         this.prevChunk = this.level.getChunk(chunkX, chunkZ);
         this.prevChunkX = chunkX;
         this.prevChunkZ = chunkZ;
      }

      BlockState blockState;
      float totalResistance;
      Optional<Float> blastResistance;
      label35: {
         ChunkAccess chunk = this.prevChunk;
         blockState = Blocks.AIR.defaultBlockState();
         totalResistance = 0.0F;
         if (chunk != null) {
            LevelChunkSection section = chunk.getSections()[Pos.SectionYIndex.fromBlockCoord(chunk, blockY)];
            if (section != null && !section.hasOnlyAir()) {
               blockState = section.getBlockState(blockX & 15, blockY & 15, blockZ & 15);
               if (blockState.getBlock() != Blocks.AIR) {
                  FluidState fluidState = blockState.getFluidState();
                  blastResistance = this.damageCalculator.getBlockExplosionResistance((Explosion)this, this.level, pos, blockState, fluidState);
                  break label35;
               }
            }
         }

         blastResistance = this.damageCalculator
            .getBlockExplosionResistance((Explosion)this, this.level, pos, Blocks.AIR.defaultBlockState(), Fluids.EMPTY.defaultFluidState());
      }

      if (blastResistance.isPresent()) {
         totalResistance = (blastResistance.get() + 0.3F) * 0.3F;
      }

      float reducedStrength = strength - totalResistance;
      if (reducedStrength > 0.0F
         && (this.explodeAirBlocks || !blockState.isAir())
         && this.damageCalculator.shouldBlockExplode((Explosion)this, this.level, pos, blockState, reducedStrength)) {
         touched.add(pos.asLong());
      }

      return totalResistance;
   }
}
