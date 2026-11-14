package dev.diorama.bake;

import dev.diorama.Diorama;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.VarHandle;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.EmptyBlockGetter;
import net.minecraft.world.level.NaturalSpawner;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.SnowLayerBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jspecify.annotations.Nullable;

public abstract class Looks {
   public static final byte AIR = 0;
   public static final byte SOLID = 1;
   public static final byte WATER = 2;
   public static final byte LAVA = 3;
   public static final byte GLASS = 4;
   public static final byte PLANT = 5;
   public static final byte TINT_NONE = 0;
   public static final byte TINT_GRASS = 1;
   public static final byte TINT_FOLIAGE = 2;
   public static final byte TINT_DRY_FOLIAGE = 3;
   public static final byte TINT_WATER = 4;
   protected static final byte NOT_YET = -1;
   protected static final VarHandle KINDS = MethodHandles.arrayElementVarHandle(byte[].class);
   public final byte[] kind;
   public final byte[] tint;
   public final byte[] emission;
   public final int[] top;
   public final int[] topTinted;
   public final float[] topTintWeight;
   public final int[] side;
   public final int[] sideTinted;
   public final float[] sideTintWeight;
   public final int[] constantTint;
   public final boolean[] spawnOn;
   public final boolean[] spawnIn;
   public final int[] shape;
   public volatile float[][] shapes = new float[][]{new float[0]};
   private volatile @Nullable ShapeFaces @Nullable [] faces;
   public final int[] wetShape;
   protected static final int COPYCAT_LOOKS = 16384;
   protected final int registrySize;
   private final Map<Long, Integer> copycatIds = new ConcurrentHashMap<>();
   private final AtomicInteger copycatCount = new AtomicInteger();
   private volatile boolean warnedCopycats;
   private final Map<VoxelShape, Integer> shapeByVoxels = new IdentityHashMap<>();
   private final Map<String, Integer> shapeByBoxes = new HashMap<>();
   private final List<float[]> shapeList = new ArrayList<>(List.of(new float[0]));
   private boolean warnedShape;
   private static final int MAX_BOXES = 16;

   protected Looks(int registrySize, int size) {
      this.registrySize = registrySize;
      this.kind = new byte[size];
      this.tint = new byte[size];
      this.emission = new byte[size];
      this.top = new int[size];
      this.topTinted = new int[size];
      this.topTintWeight = new float[size];
      this.side = new int[size];
      this.sideTinted = new int[size];
      this.sideTintWeight = new float[size];
      this.constantTint = new int[size];
      this.spawnOn = new boolean[size];
      this.spawnIn = new boolean[size];
      this.shape = new int[size];
      this.wetShape = new int[size];
      Arrays.fill(this.wetShape, -1);
      Arrays.fill(this.kind, 0, registrySize, (byte)-1);
   }

   protected abstract void make(int var1);

   protected abstract byte colours(int var1, BlockState var2, byte var3);

   protected byte flatColours(int id, BlockState state, byte kind) {
      int mapColor = mapColor(state);
      if (mapColor == 0) {
         return 0;
      }

      FaceColor face = new FaceColor();
      face.add(new float[]{(mapColor >> 16 & 0xFF) / 255.0F, (mapColor >> 8 & 0xFF) / 255.0F, (mapColor & 0xFF) / 255.0F, 1.0F}, -1);
      this.setColours(id, face, face);
      return kind;
   }

   protected void setColours(int id, FaceColor topFace, FaceColor sideFace) {
      this.top[id] = topFace.untinted();
      this.topTinted[id] = topFace.tinted();
      this.topTintWeight[id] = topFace.tintWeight();
      this.side[id] = sideFace.untinted();
      this.sideTinted[id] = sideFace.tinted();
      this.sideTintWeight[id] = sideFace.tintWeight();
   }

   protected static int mapColor(BlockState state) {
      try {
         return state.getMapColor(EmptyBlockGetter.INSTANCE, BlockPos.ZERO).col;
      } catch (RuntimeException | Error e) {
         return 0;
      }
   }

   protected final byte work(int id, BlockState state) {
      this.emission[id] = (byte)state.getLightEmission();
      this.constantTint[id] = -1;

      try {
         this.spawnOn[id] = state.isValidSpawn(EmptyBlockGetter.INSTANCE, BlockPos.ZERO, EntityType.ZOMBIE);
         this.spawnIn[id] = NaturalSpawner.isValidEmptySpawnBlock(EmptyBlockGetter.INSTANCE, BlockPos.ZERO, state, state.getFluidState(), EntityType.ZOMBIE)
            && state.getCollisionShape(EmptyBlockGetter.INSTANCE, BlockPos.ZERO).isEmpty();
      } catch (RuntimeException | Error e) {
         this.spawnOn[id] = false;
         this.spawnIn[id] = false;
      }

      byte kind = 1;

      try {
         kind = classify(state);
         if (kind == 1) {
            this.shape[id] = this.shapeIndex(state);
         }
      } catch (RuntimeException | Error e) {
         if (!this.warnedShape) {
            this.warnedShape = true;
            Diorama.LOGGER.warn("The shape of {} failed when asked for ({}); such blocks draw as full blocks", state, e.toString());
         }
      }

      if (kind == 0) {
         return 0;
      }

      kind = this.colours(id, state, kind);
      if (kind == 0) {
         this.shape[id] = 0;
         return 0;
      }

      if (this.shape[id] != 0 && !state.getFluidState().isEmpty()) {
         int fluid = Block.BLOCK_STATE_REGISTRY.getId(state.getFluidState().createLegacyBlock());
         if (fluid >= 0 && fluid < this.registrySize && fluid != id && this.kind[this.ensure(fluid)] == 2) {
            this.wetShape[id] = fluid;
         }
      }

      return kind;
   }

   ShapeFaces faces(int shape) {
      ShapeFaces[] faces = this.faces;
      if (faces == null || shape >= faces.length) {
         faces = Arrays.copyOf(faces == null ? new ShapeFaces[0] : faces, this.shapes.length);
         this.faces = faces;
      }

      ShapeFaces made = faces[shape];
      if (made == null) {
         made = ShapeFaces.of(this.shapes[shape]);
         faces[shape] = made;
      }

      return made;
   }

   public static int id(BlockState state) {
      int id = Block.BLOCK_STATE_REGISTRY.getId(state);
      if (id < 0) {
         id = Block.BLOCK_STATE_REGISTRY.getId(state.getBlock().defaultBlockState());
      }

      if (id < 0) {
         id = Block.BLOCK_STATE_REGISTRY.getId(Blocks.AIR.defaultBlockState());
      }

      return Math.max(id, 0);
   }

   protected static int bakeId(BlockState state, @Nullable Looks looks) {
      int id = Block.BLOCK_STATE_REGISTRY.getId(state);
      if (id >= 0) {
         return id;
      } else if (looks != null && Block.BLOCK_STATE_REGISTRY.size() != looks.registrySize) {
         throw Looks.StatesRenumbered.INSTANCE;
      } else {
         return id(state);
      }
   }

   public int ensure(int id) {
      if (id >= 0 && id < this.registrySize && (byte)KINDS.getAcquire((byte[])this.kind, (int)id) == -1) {
         this.make(id);
      }

      return id;
   }

   public int lookId(BlockState state) {
      return this.ensure(id(state));
   }

   public int bakeLookId(BlockState state) {
      int id = this.ensure(bakeId(state, this));
      if (id < this.registrySize && (byte)KINDS.getAcquire((byte[])this.kind, (int)id) == -1) {
         throw Looks.StatesRenumbered.INSTANCE;
      } else {
         return id;
      }
   }

   public boolean sameLook(int a, int b) {
      this.ensure(a);
      this.ensure(b);
      return this.kind[a] == this.kind[b]
         && this.top[a] == this.top[b]
         && this.topTinted[a] == this.topTinted[b]
         && this.side[a] == this.side[b]
         && this.sideTinted[a] == this.sideTinted[b]
         && this.topTintWeight[a] == this.topTintWeight[b]
         && this.sideTintWeight[a] == this.sideTintWeight[b]
         && this.tint[a] == this.tint[b]
         && this.constantTint[a] == this.constantTint[b]
         && this.emission[a] == this.emission[b]
         && this.spawnOn[a] == this.spawnOn[b]
         && this.spawnIn[a] == this.spawnIn[b]
         && this.shape[a] == this.shape[b]
         && this.wetShape[a] == this.wetShape[b];
   }

   public int copycat(int copycat, int material) {
      if (copycat >= 0 && copycat < this.registrySize && material >= 0 && material < this.registrySize && this.kind.length != this.registrySize) {
         this.ensure(copycat);
         this.ensure(material);
         byte frame = this.kind[copycat];
         if ((frame == 1 || frame == 4 || frame == 5) && this.kind[material] != 0 && this.kind[material] != -1) {
            Long key = (long)copycat << 32 | material;
            Integer id = this.copycatIds.get(key);
            if (id != null) {
               return id;
            } else if (this.copycatCount.get() >= this.kind.length - this.registrySize) {
               this.warnFull();
               return copycat;
            } else {
               return this.copycatIds.computeIfAbsent(key, unused -> this.addCopycat(copycat, material));
            }
         } else {
            return copycat;
         }
      } else {
         return this.ensure(copycat);
      }
   }

   private void warnFull() {
      if (!this.warnedCopycats) {
         this.warnedCopycats = true;
         Diorama.LOGGER.warn("More than {} kinds of filled copycat; the rest draw as they do empty", 16384);
      }
   }

   private int addCopycat(int copycat, int material) {
      int id = this.registrySize + this.copycatCount.getAndIncrement();
      if (id >= this.kind.length) {
         this.warnFull();
         return copycat;
      } else {
         this.kind[id] = (byte)(this.kind[copycat] == 5 ? 5 : (this.kind[material] == 4 ? 4 : 1));
         this.shape[id] = this.shape[copycat];
         this.spawnOn[id] = this.spawnOn[copycat];
         this.spawnIn[id] = this.spawnIn[copycat];
         this.wetShape[id] = this.wetShape[copycat];
         this.top[id] = this.top[material];
         this.topTinted[id] = this.topTinted[material];
         this.topTintWeight[id] = this.topTintWeight[material];
         this.side[id] = this.side[material];
         this.sideTinted[id] = this.sideTinted[material];
         this.sideTintWeight[id] = this.sideTintWeight[material];
         this.tint[id] = this.tint[material];
         this.constantTint[id] = this.constantTint[material];
         this.emission[id] = this.emission[material];
         return id;
      }
   }

   private int shapeIndex(BlockState state) {
      Map<VoxelShape, Integer> byVoxels = this.shapeByVoxels;
      List<float[]> shapes = this.shapeList;

      VoxelShape outline;
      try {
         outline = state.getShape(EmptyBlockGetter.INSTANCE, BlockPos.ZERO);
      } catch (RuntimeException | Error e) {
         return 0;
      }

      Integer known = byVoxels.get(outline);
      if (known != null) {
         return known;
      }

      int index = 0;
      if (!outline.isEmpty() && outline != Shapes.block()) {
         List<AABB> boxes = outline.toAabbs();
         if (boxes.size() > 16) {
            boxes = List.of(outline.bounds());
         }

         float[] flat = new float[boxes.size() * 6];
         int n = 0;
         boolean full = false;

         for (AABB box : boxes) {
            float[] b = new float[]{clamp01(box.minX), clamp01(box.minY), clamp01(box.minZ), clamp01(box.maxX), clamp01(box.maxY), clamp01(box.maxZ)};
            if (!(b[0] >= b[3]) && !(b[1] >= b[4]) && !(b[2] >= b[5])) {
               System.arraycopy(b, 0, flat, n, 6);
               n += 6;
               full |= b[0] == 0.0F && b[1] == 0.0F && b[2] == 0.0F && b[3] == 1.0F && b[4] == 1.0F && b[5] == 1.0F;
            }
         }

         float[] kept = Arrays.copyOf(flat, n);
         if (!full && n > 0) {
            index = this.shapeByBoxes.computeIfAbsent(Arrays.toString(kept), key -> {
               shapes.add(kept);
               float[][] published = this.shapes;
               if (published.length < shapes.size()) {
                  published = Arrays.copyOf(published, Math.max(16, published.length * 2));
               }

               published[shapes.size() - 1] = kept;
               this.shapes = published;
               return shapes.size() - 1;
            });
         }
      }

      byVoxels.put(outline, index);
      return index;
   }

   private static float clamp01(double v) {
      return (float)Math.max(0.0, Math.min(1.0, v));
   }

   private static byte classify(BlockState state) {
      if (!state.isAir() && (state.getRenderShape() != RenderShape.INVISIBLE || !state.getFluidState().isEmpty())) {
         FluidState fluid = state.getFluidState();
         boolean noCollision = state.getCollisionShape(EmptyBlockGetter.INSTANCE, BlockPos.ZERO).isEmpty()
            && !(state.getBlock() instanceof LeavesBlock)
            && !state.is(BlockTags.LEAVES);
         if (!fluid.isEmpty() && noCollision) {
            return (byte)(fluid.is(FluidTags.LAVA) ? 3 : 2);
         } else if (noCollision) {
            return (byte)(state.getBlock() instanceof SnowLayerBlock ? 1 : 5);
         } else {
            return 1;
         }
      } else {
         return 0;
      }
   }

   public static final class StatesRenumbered extends RuntimeException {
      static final Looks.StatesRenumbered INSTANCE = new Looks.StatesRenumbered();

      private StatesRenumbered() {
         super("Block states are being numbered again", null, false, false);
      }
   }
}
