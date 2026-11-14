package dev.diorama.bake;

import com.mojang.blaze3d.platform.NativeImage;
import dev.diorama.Diorama;
import dev.diorama.data.Copycats;
import java.lang.reflect.Field;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.IntSupplier;
import net.minecraft.client.Minecraft;
import net.minecraft.client.color.block.BlockColors;
import net.minecraft.client.renderer.BiomeColors;
import net.minecraft.client.renderer.block.BlockModelShaper;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.texture.MissingTextureAtlasSprite;
import net.minecraft.client.renderer.texture.SpriteContents;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ColorResolver;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions;
import net.neoforged.neoforge.client.model.data.ModelData;
import org.jspecify.annotations.Nullable;

public final class BlockLooks extends Looks {
   private static final Object MAKING = new Object();
   private static final Object IMAGES = new Object();
   private static volatile BlockLooks current;
   private static int builds;
   private static Object builtFrom;
   private final BlockModelShaper models;
   private final BlockColors colors;
   private final @Nullable TextureAtlas atlas;
   private final Map<TextureAtlasSprite, float[]> spriteCache = new IdentityHashMap<>();
   private final RandomSource random = RandomSource.create(42L);
   private final TintProbe probe = new TintProbe();
   private final Map<Fluid, BlockLooks.FluidLook> fluidLooks = new IdentityHashMap<>();
   private boolean warnedModel;
   private boolean withModels;
   private volatile boolean provisional;
   private int made;
   private long makingNanos;
   private static final Field ORIGINAL_IMAGE;
   private static boolean warnedFreed;

   private BlockLooks(int registrySize, int size, BlockModelShaper models, BlockColors colors, @Nullable TextureAtlas atlas) {
      super(registrySize, size);
      this.models = models;
      this.colors = colors;
      this.atlas = atlas;
   }

   public static BlockLooks get() {
      return current;
   }

   public static int bakeId(BlockState state) {
      return bakeId(state, current);
   }

   public static int builds() {
      return builds;
   }

   public static void invalidate() {
      current = null;
   }

   public static boolean isStale() {
      BlockLooks looks = current;
      if (looks == null) {
         return false;
      } else {
         return looks.provisional ? true : builtFrom != Minecraft.getInstance().getModelManager().getMissingModel();
      }
   }

   public static BlockLooks build() {
      long start = System.nanoTime();
      BlockLooks last = current;
      if (last != null) {
         Diorama.LOGGER.info("The last block color table worked out {}", last.madeSoFar());
      }

      BlockLooks looks = detached();
      current = looks;
      builds++;
      builtFrom = Minecraft.getInstance().getModelManager().getMissingModel();
      Diorama.LOGGER
         .info(
            "Block color table for {} states set up in {} ms; each state's colors are worked out when the map first meets it",
            looks.registrySize,
            (System.nanoTime() - start) / 1000000L
         );
      return looks;
   }

   public static BlockLooks detached() {
      Minecraft mc = Minecraft.getInstance();
      BlockModelShaper models = mc.getModelManager().getBlockModelShaper();
      int size = Block.BLOCK_STATE_REGISTRY.size();
      TextureAtlas atlas = null;

      try {
         TextureAtlasSprite stone = particle(models.getBlockModel(Blocks.STONE.defaultBlockState()));
         if (stone != null && mc.getTextureManager().getTexture(stone.atlasLocation()) instanceof TextureAtlas blocks) {
            atlas = blocks;
         }
      } catch (RuntimeException | Error var9) {
      }

      BlockLooks looks = new BlockLooks(size, Copycats.INSTALLED ? size + 16384 : size, models, mc.getBlockColors(), atlas);
      warnedFreed = false;
      synchronized (MAKING) {
         looks.probe.reset();
         BiomeTints.setWater(waterResolver(looks.probe));
      }

      looks.ensure(VoxelReader.AIR_ID);
      return looks;
   }

   public String madeSoFar() {
      synchronized (MAKING) {
         return (this.made == 1 ? "1 look" : this.made + " looks") + " in " + this.makingNanos / 1000000L + " ms";
      }
   }

   @Override
   protected void make(int id) {
      synchronized (MAKING) {
         if (this.kind[id] == -1) {
            if (Block.BLOCK_STATE_REGISTRY.size() == this.registrySize) {
               long start = System.nanoTime();
               BlockState state = (BlockState)Block.BLOCK_STATE_REGISTRY.byId(id);
               boolean reloading = Minecraft.getInstance().getOverlay() != null;
               this.withModels = !reloading;
               byte kind = state == null ? 0 : this.work(id, state);
               if (reloading) {
                  this.provisional = true;
               }

               KINDS.setRelease((byte[])this.kind, (int)id, (byte)kind);
               this.made++;
               this.makingNanos = this.makingNanos + (System.nanoTime() - start);
            }
         }
      }
   }

   @Override
   protected byte colours(int id, BlockState state, byte kind) {
      if (!this.withModels) {
         return this.flatColours(id, state, kind);
      }

      kind = this.modelColours(id, state, kind);
      if (kind == 0) {
         return 0;
      }

      if (kind == 2) {
         FluidState fluid = state.getFluidState().getType().defaultFluidState();
         BlockLooks.FluidLook look = this.fluidLooks.computeIfAbsent(fluid.getType(), type -> this.fluidLook(fluid));
         if (look.color() >= 0) {
            this.top[id] = look.color();
            this.topTinted[id] = look.color();
            this.topTintWeight[id] = 1.0F;
            this.side[id] = look.color();
            this.sideTinted[id] = look.color();
            this.sideTintWeight[id] = 1.0F;
            this.tint[id] = look.tint().kind();
            this.constantTint[id] = look.tint().constant();
         }
      }

      return kind;
   }

   private byte modelColours(int id, BlockState state, byte kind) {
      BakedModel model = this.models.getBlockModel(state);
      FaceColor topFace = new FaceColor();
      FaceColor sideFace = new FaceColor();

      try {
         for (BlockLooks.LegacyPart part : List.of(new BlockLooks.LegacyPart(model, state, this.random))) {
            this.accumulate(topFace, part.getQuads(Direction.UP));
            this.accumulate(sideFace, part.getQuads(Direction.SOUTH));
            if (sideFace.isEmpty()) {
               this.accumulate(sideFace, part.getQuads(Direction.NORTH));
            }

            List<BakedQuad> loose = part.getQuads(null);
            if (!loose.isEmpty()) {
               this.accumulateLoose(topFace, sideFace, loose);
            }
         }
      } catch (RuntimeException | Error e) {
         topFace = new FaceColor();
         sideFace = new FaceColor();
         if (!this.warnedModel) {
            this.warnedModel = true;
            Diorama.LOGGER
               .warn("The model of {} failed when asked for its faces ({}); such blocks take their particle texture or map colour", state, e.toString());
         }
      }

      if (topFace.isEmpty() && sideFace.isEmpty()) {
         TextureAtlasSprite particle = particle(model);
         if (particle != null && !isMissing(particle)) {
            float[] avg = this.spriteAverage(particle);
            topFace.add(avg, -1);
            sideFace.add(avg, -1);
         }
      }

      if (topFace.isEmpty() && sideFace.isEmpty()) {
         return this.flatColours(id, state, kind);
      }

      if (topFace.isEmpty()) {
         topFace = sideFace;
      }

      if (sideFace.isEmpty()) {
         sideFace = topFace;
      }

      this.setColours(id, topFace, sideFace);
      if (kind == 1 && !state.is(BlockTags.LEAVES) && topFace.coverage() < 0.55F) {
         kind = 4;
      }

      int tintIndex = topFace.tintIndex() >= 0 ? topFace.tintIndex() : sideFace.tintIndex();
      if (tintIndex >= 0) {
         BlockLooks.Tint tint = blockTint(this.colors, state, tintIndex, this.probe);
         this.tint[id] = tint.kind();
         this.constantTint[id] = tint.constant();
      }

      return kind;
   }

   private static @Nullable TextureAtlasSprite particle(BakedModel model) {
      try {
         return model.getParticleIcon(ModelData.EMPTY);
      } catch (RuntimeException | Error e) {
         return null;
      }
   }

   private static List<BakedQuad> legacyQuads(BakedModel model, BlockState state, @Nullable Direction direction, RandomSource random) {
      return model.getQuads(state, direction, random, ModelData.EMPTY, null);
   }

   private BlockLooks.FluidLook fluidLook(FluidState fluid) {
      TextureAtlasSprite sprite;
      try {
         sprite = (TextureAtlasSprite)Minecraft.getInstance()
            .getTextureAtlas(TextureAtlas.LOCATION_BLOCKS)
            .apply(IClientFluidTypeExtensions.of(fluid).getStillTexture());
      } catch (RuntimeException | Error e) {
         sprite = null;
      }

      if (sprite != null && isMissing(sprite)) {
         sprite = null;
      }

      float[] avg = sprite == null ? null : this.spriteAverage(sprite);
      int color = avg != null && !(avg[3] <= 0.0F) ? FaceColor.pack(avg[0], avg[1], avg[2]) : -1;
      return new BlockLooks.FluidLook(color, fluidTint(fluid, this.probe));
   }

   private static BlockLooks.Tint blockTint(BlockColors colors, BlockState state, int index, TintProbe probe) {
      return BlockLooks.Tint.ask(probe, () -> colors.getColor(state, probe, BlockPos.ZERO, index), () -> colors.getColor(state, null, null, index));
   }

   private static ColorResolver waterResolver(TintProbe probe) {
      probe.reset();
      fluidTint(Fluids.WATER.defaultFluidState(), probe);
      ColorResolver asked = probe.asked();
      return asked != null ? asked : BiomeColors.WATER_COLOR_RESOLVER;
   }

   private static BlockLooks.Tint fluidTint(FluidState fluid, TintProbe probe) {
      try {
         IClientFluidTypeExtensions extensions = IClientFluidTypeExtensions.of(fluid);
         return BlockLooks.Tint.ask(probe, () -> extensions.getTintColor(fluid, probe, BlockPos.ZERO), extensions::getTintColor);
      } catch (RuntimeException | Error e) {
         return BlockLooks.Tint.NONE;
      }
   }

   private void accumulate(FaceColor face, List<BakedQuad> quads) {
      for (BakedQuad quad : quads) {
         TextureAtlasSprite sprite = quad.getSprite();
         int tintIndex = quad.getTintIndex();
         if (!isMissing(sprite)) {
            face.add(this.spriteAverage(sprite), tintIndex);
         }
      }
   }

   private void accumulateLoose(FaceColor top, FaceColor side, List<BakedQuad> quads) {
      for (BakedQuad quad : quads) {
         TextureAtlasSprite sprite = quad.getSprite();
         int tintIndex = quad.getTintIndex();
         if (!isMissing(sprite)) {
            float[] avg = this.spriteAverage(sprite);
            if (quad.getDirection() != Direction.UP && quad.getDirection() != Direction.DOWN) {
               top.add(avg, tintIndex);
               side.add(avg, tintIndex);
            } else {
               top.add(avg, tintIndex);
            }
         }
      }
   }

   private static boolean isMissing(TextureAtlasSprite sprite) {
      return sprite.contents().name().equals(MissingTextureAtlasSprite.getLocation());
   }

   private float[] spriteAverage(TextureAtlasSprite sprite) {
      return this.spriteCache
         .computeIfAbsent(
            sprite,
            s -> {
               float[] avg = average(s);
               if (avg != null) {
                  return avg;
               }

               TextureAtlasSprite current = this.currentSprite(s);
               avg = current == null ? null : average(current);
               if (!warnedFreed) {
                  warnedFreed = true;
                  Diorama.LOGGER
                     .warn(
                        "A block model holds texture {} from before the last resource reload; {}",
                        s.contents().name(),
                        avg != null ? "using the reloaded texture" : "it adds no colour"
                     );
               }

               return avg != null ? avg : new float[]{0.0F, 0.0F, 0.0F, 0.0F};
            }
         );
   }

   public static void closing(BlockLooks.ClosingTexture texture) {
      synchronized (IMAGES) {
         texture.diorama$markClosed();
      }
   }

   private @Nullable TextureAtlasSprite currentSprite(TextureAtlasSprite old) {
      try {
         TextureAtlas atlas = this.atlas;
         if (atlas == null || !old.atlasLocation().equals(atlas.location())) {
            if (!Minecraft.getInstance().isSameThread()) {
               return null;
            }

            atlas = Minecraft.getInstance().getTextureManager().getTexture(old.atlasLocation()) instanceof TextureAtlas found ? found : null;
            if (atlas == null) {
               return null;
            }
         }

         TextureAtlasSprite sprite = atlas.getSprite(old.contents().name());
         return sprite != old && !sprite.contents().name().equals(MissingTextureAtlasSprite.getLocation()) ? sprite : null;
      } catch (RuntimeException | Error var5) {
         return null;
      }
   }

   private static float @Nullable [] average(TextureAtlasSprite s) {
      synchronized (IMAGES) {
         SpriteContents contents = s.contents();
         return contents instanceof BlockLooks.ClosingTexture closing && closing.diorama$closed() ? null : average(contents);
      }
   }

   private static float @Nullable [] average(SpriteContents contents) {
      NativeImage image = null;

      try {
         image = ORIGINAL_IMAGE == null ? null : (NativeImage)ORIGINAL_IMAGE.get(contents);
      } catch (IllegalAccessException var20) {
      }

      if (image == null) {
         return new float[]{1.0F, 0.0F, 1.0F, 1.0F};
      }

      int w = Math.min(contents.width(), image.getWidth());
      int h = Math.min(contents.height(), image.getHeight());
      double r = 0.0;
      double g = 0.0;
      double b = 0.0;
      double weight = 0.0;
      int visible = 0;

      try {
         for (int y = 0; y < h; y++) {
            for (int x = 0; x < w; x++) {
               int abgr = image.getPixelRGBA(x, y);
               int argb = abgr & -16711936 | abgr >> 16 & 0xFF | (abgr & 0xFF) << 16;
               int a = argb >>> 24;
               if (a >= 26) {
                  double wa = a / 255.0;
                  r += (argb >> 16 & 0xFF) * wa;
                  g += (argb >> 8 & 0xFF) * wa;
                  b += (argb & 0xFF) * wa;
                  weight += wa;
                  visible++;
               }
            }
         }
      } catch (IllegalStateException freed) {
         return null;
      }

      return weight == 0.0
         ? new float[]{0.0F, 0.0F, 0.0F, 0.0F}
         : new float[]{(float)(r / weight / 255.0), (float)(g / weight / 255.0), (float)(b / weight / 255.0), (float)visible / (w * h)};
   }

   static {
      Field field;
      try {
         field = SpriteContents.class.getDeclaredField("originalImage");
         field.setAccessible(true);
      } catch (ReflectiveOperationException e) {
         field = null;
      }

      ORIGINAL_IMAGE = field;
   }

   public interface ClosingTexture {
      void diorama$markClosed();

      boolean diorama$closed();
   }

   private record FluidLook(int color, BlockLooks.Tint tint) {
   }

   private record LegacyPart(BakedModel model, BlockState state, RandomSource random) {
      List<BakedQuad> getQuads(@Nullable Direction direction) {
         this.random.setSeed(42L);
         return BlockLooks.legacyQuads(this.model, this.state, direction, this.random);
      }
   }

   private record Tint(byte kind, int constant) {
      static final BlockLooks.Tint NONE = new BlockLooks.Tint((byte)0, -1);

      static BlockLooks.Tint ask(TintProbe probe, IntSupplier inWorld, IntSupplier outOfWorld) {
         probe.reset();

         int color;
         try {
            color = inWorld.getAsInt();
         } catch (RuntimeException | Error e) {
            color = -1;
         }

         byte kind = probe.kind();
         if (kind != 0) {
            return new BlockLooks.Tint(kind, -1);
         }

         if (probe.askedForBiome() || color == -1) {
            try {
               color = outOfWorld.getAsInt();
            } catch (RuntimeException | Error e) {
               return NONE;
            }
         }

         return (color & 16777215) == 16777215 ? NONE : new BlockLooks.Tint((byte)0, color & 16777215);
      }
   }
}
