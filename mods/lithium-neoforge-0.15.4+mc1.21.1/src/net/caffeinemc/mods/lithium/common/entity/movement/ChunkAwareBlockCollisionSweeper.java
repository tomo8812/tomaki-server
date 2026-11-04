package net.caffeinemc.mods.lithium.common.entity.movement;

import com.google.common.collect.AbstractIterator;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import net.caffeinemc.mods.lithium.common.block.BlockCountingSection;
import net.caffeinemc.mods.lithium.common.block.BlockStateFlags;
import net.caffeinemc.mods.lithium.common.shapes.VoxelShapeCaster;
import net.caffeinemc.mods.lithium.common.util.Pos;
import net.minecraft.core.BlockPos.MutableBlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.LevelChunkSection;
import net.minecraft.world.level.chunk.status.ChunkStatus;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

public class ChunkAwareBlockCollisionSweeper extends AbstractIterator<VoxelShape> {
   private final MutableBlockPos pos = new MutableBlockPos();
   private final AABB box;
   private final VoxelShape shape;
   private final Level world;
   private final CollisionContext context;
   private final int minX;
   private final int minY;
   private final int minZ;
   private final int maxX;
   private final int maxY;
   private final int maxZ;
   private int chunkX;
   private int chunkYIndex;
   private int chunkZ;
   private int cStartX;
   private int cStartZ;
   private int cEndX;
   private int cEndZ;
   private int cX;
   private int cY;
   private int cZ;
   private int maxHitX;
   private int maxHitY;
   private int maxHitZ;
   private VoxelShape maxShape;
   private final boolean hideLastCollision;
   private int cTotalSize;
   private int cIterated;
   private boolean sectionOversizedBlocks;
   private ChunkAccess cachedChunk;
   private LevelChunkSection cachedChunkSection;

   public ChunkAwareBlockCollisionSweeper(Level world, @Nullable Entity entity, AABB box) {
      this(world, entity, box, false);
   }

   public ChunkAwareBlockCollisionSweeper(Level world, @Nullable Entity entity, AABB box, boolean hideLastCollision) {
      this.box = box;
      this.shape = Shapes.create(box);
      this.context = entity == null ? CollisionContext.empty() : CollisionContext.of(entity);
      this.world = world;
      this.minX = Mth.floor(box.minX - 1.0E-7);
      this.maxX = Mth.floor(box.maxX + 1.0E-7);
      this.minY = Mth.clamp(Mth.floor(box.minY - 1.0E-7), Pos.BlockCoord.getMinY(this.world), Pos.BlockCoord.getMaxYInclusive(this.world));
      this.maxY = Mth.clamp(Mth.floor(box.maxY + 1.0E-7), Pos.BlockCoord.getMinY(this.world), Pos.BlockCoord.getMaxYInclusive(this.world));
      this.minZ = Mth.floor(box.minZ - 1.0E-7);
      this.maxZ = Mth.floor(box.maxZ + 1.0E-7);
      this.chunkX = Pos.ChunkCoord.fromBlockCoord(expandMin(this.minX));
      this.chunkZ = Pos.ChunkCoord.fromBlockCoord(expandMin(this.minZ));
      this.cIterated = 0;
      this.cTotalSize = 0;
      this.maxHitX = Integer.MIN_VALUE;
      this.maxHitY = Integer.MIN_VALUE;
      this.maxHitZ = Integer.MIN_VALUE;
      this.maxShape = null;
      this.hideLastCollision = hideLastCollision;
      this.chunkX--;
   }

   public VoxelShape getLastCollision() {
      return this.maxShape;
   }

   public Iterator<VoxelShape> getLastCollisionIterator() {
      return new Iterator<VoxelShape>() {
         @Override
         public boolean hasNext() {
            return ChunkAwareBlockCollisionSweeper.this.hideLastCollision && ChunkAwareBlockCollisionSweeper.this.maxShape != null;
         }

         public VoxelShape next() {
            if (this.hasNext()) {
               VoxelShape previousMaxShape = ChunkAwareBlockCollisionSweeper.this.maxShape;
               ChunkAwareBlockCollisionSweeper.this.maxShape = null;
               return previousMaxShape;
            } else {
               throw new NoSuchElementException();
            }
         }

         // $VF: synthetic method
         // $VF: bridge method
         @Override
         public Object next() {
            return this.next();
         }
      };
   }

   private boolean nextSection() {
      while (true) {
         if (this.cachedChunk != null
            && this.chunkYIndex < Pos.SectionYIndex.getMaxYSectionIndexInclusive(this.world)
            && this.chunkYIndex < Pos.SectionYIndex.fromBlockCoord(this.world, expandMax(this.maxY))) {
            this.chunkYIndex++;
            this.cachedChunkSection = this.cachedChunk.getSections()[this.chunkYIndex];
         } else {
            if (this.chunkX < Pos.ChunkCoord.fromBlockCoord(expandMax(this.maxX))) {
               this.chunkX++;
            } else {
               if (this.chunkZ >= Pos.ChunkCoord.fromBlockCoord(expandMax(this.maxZ))) {
                  return false;
               }

               this.chunkX = Pos.ChunkCoord.fromBlockCoord(expandMin(this.minX));
               this.chunkZ++;
            }

            this.cachedChunk = this.world.getChunk(this.chunkX, this.chunkZ, ChunkStatus.FULL, false);
            if (this.cachedChunk != null) {
               this.chunkYIndex = Mth.clamp(
                  Pos.SectionYIndex.fromBlockCoord(this.world, expandMin(this.minY)),
                  Pos.SectionYIndex.getMinYSectionIndex(this.world),
                  Pos.SectionYIndex.getMaxYSectionIndexInclusive(this.world)
               );
               this.cachedChunkSection = this.cachedChunk.getSections()[this.chunkYIndex];
            }
         }

         if (this.cachedChunk != null && this.cachedChunkSection != null && !this.cachedChunkSection.hasOnlyAir()) {
            this.sectionOversizedBlocks = hasChunkSectionOversizedBlocks(this.cachedChunk, this.chunkYIndex);
            int sizeExtension = this.sectionOversizedBlocks ? 1 : 0;
            this.cEndX = Math.min(this.maxX + sizeExtension, Pos.BlockCoord.getMaxInSectionCoord(this.chunkX));
            int cEndY = Math.min(this.maxY + sizeExtension, Pos.BlockCoord.getMaxYInSectionIndex(this.world, this.chunkYIndex));
            this.cEndZ = Math.min(this.maxZ + sizeExtension, Pos.BlockCoord.getMaxInSectionCoord(this.chunkZ));
            this.cStartX = Math.max(this.minX - sizeExtension, Pos.BlockCoord.getMinInSectionCoord(this.chunkX));
            int cStartY = Math.max(this.minY - sizeExtension, Pos.BlockCoord.getMinYInSectionIndex(this.world, this.chunkYIndex));
            this.cStartZ = Math.max(this.minZ - sizeExtension, Pos.BlockCoord.getMinInSectionCoord(this.chunkZ));
            this.cX = this.cStartX;
            this.cY = cStartY;
            this.cZ = this.cStartZ;
            this.cTotalSize = (this.cEndX - this.cStartX + 1) * (cEndY - cStartY + 1) * (this.cEndZ - this.cStartZ + 1);
            if (this.cTotalSize != 0) {
               this.cIterated = 0;
               return true;
            }
         }
      }
   }

   public VoxelShape computeNext() {
      while (this.cIterated < this.cTotalSize || this.nextSection()) {
         this.cIterated++;
         int x = this.cX;
         int y = this.cY;
         int z = this.cZ;
         if (this.cX < this.cEndX) {
            this.cX++;
         } else if (this.cZ < this.cEndZ) {
            this.cX = this.cStartX;
            this.cZ++;
         } else {
            this.cX = this.cStartX;
            this.cZ = this.cStartZ;
            this.cY++;
         }

         int edgesHit = this.sectionOversizedBlocks
            ? (x >= this.minX && x <= this.maxX ? 0 : 1) + (y >= this.minY && y <= this.maxY ? 0 : 1) + (z >= this.minZ && z <= this.maxZ ? 0 : 1)
            : 0;
         if (edgesHit != 3) {
            BlockState state = this.cachedChunkSection.getBlockState(x & 15, y & 15, z & 15);
            if (canInteractWithBlock(state, edgesHit)) {
               this.pos.set(x, y, z);
               VoxelShape collisionShape = state.getCollisionShape(this.world, this.pos, this.context);
               if (collisionShape != Shapes.empty() && collisionShape != null) {
                  VoxelShape collidedShape = getCollidedShape(this.box, this.shape, collisionShape, x, y, z);
                  if (collidedShape != null) {
                     if (z >= this.maxHitZ && (z > this.maxHitZ || y >= this.maxHitY && (y > this.maxHitY || x > this.maxHitX))) {
                        this.maxHitX = x;
                        this.maxHitY = y;
                        this.maxHitZ = z;
                        VoxelShape previousMaxShape = this.maxShape;
                        this.maxShape = collidedShape;
                        if (previousMaxShape == null) {
                           continue;
                        }

                        return previousMaxShape;
                     }

                     return collidedShape;
                  }
               }
            }
         }
      }

      if (!this.hideLastCollision && this.maxShape != null) {
         VoxelShape previousMaxShape = this.maxShape;
         this.maxShape = null;
         return previousMaxShape;
      } else {
         return (VoxelShape)this.endOfData();
      }
   }

   private static boolean canInteractWithBlock(BlockState state, int edgesHit) {
      return (edgesHit != 1 || state.hasLargeCollisionShape()) && (edgesHit != 2 || state.getBlock() == Blocks.MOVING_PISTON);
   }

   private static VoxelShape getCollidedShape(AABB entityBox, VoxelShape entityShape, VoxelShape shape, int x, int y, int z) {
      if (shape == Shapes.block()) {
         return entityBox.intersects(x, y, z, x + 1.0, y + 1.0, z + 1.0) ? shape.move(x, y, z) : null;
      }

      if (shape instanceof VoxelShapeCaster) {
         return ((VoxelShapeCaster)shape).intersects(entityBox, x, y, z) ? shape.move(x, y, z) : null;
      }

      shape = shape.move(x, y, z);
      return Shapes.joinIsNotEmpty(shape, entityShape, BooleanOp.AND) ? shape : null;
   }

   private static int expandMin(int coord) {
      return coord - 1;
   }

   private static int expandMax(int coord) {
      return coord + 1;
   }

   private static boolean hasChunkSectionOversizedBlocks(ChunkAccess chunk, int chunkY) {
      if (!BlockStateFlags.ENABLED) {
         return true;
      }

      LevelChunkSection section = chunk.getSections()[chunkY];
      return section != null && ((BlockCountingSection)section).lithium$mayContainAny(BlockStateFlags.OVERSIZED_SHAPE);
   }

   public List<VoxelShape> collectAll() {
      ArrayList<VoxelShape> collisions = new ArrayList<>();

      while (this.hasNext()) {
         collisions.add((VoxelShape)this.next());
      }

      return collisions;
   }

   // $VF: synthetic method
   // $VF: bridge method
   public Object computeNext() {
      return this.computeNext();
   }
}
