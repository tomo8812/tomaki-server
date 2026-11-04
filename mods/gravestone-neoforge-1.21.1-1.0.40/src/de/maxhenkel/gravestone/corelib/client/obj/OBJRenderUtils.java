package de.maxhenkel.gravestone.corelib.client.obj;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat.Mode;
import java.util.function.Function;
import net.minecraft.Util;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.RenderStateShard.LightmapStateShard;
import net.minecraft.client.renderer.RenderStateShard.OverlayStateShard;
import net.minecraft.client.renderer.RenderStateShard.ShaderStateShard;
import net.minecraft.client.renderer.RenderStateShard.TextureStateShard;
import net.minecraft.client.renderer.RenderStateShard.TransparencyStateShard;
import net.minecraft.client.renderer.RenderType.CompositeState;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
class OBJRenderUtils {
   public static final ShaderStateShard RENDERTYPE_ENTITY_CUTOUT_SHADER = new ShaderStateShard(GameRenderer::getRendertypeEntityCutoutShader);
   public static final TransparencyStateShard NO_TRANSPARENCY = new TransparencyStateShard("no_transparency", RenderSystem::disableBlend, () -> {});
   public static final LightmapStateShard LIGHTMAP = new LightmapStateShard(true);
   public static final OverlayStateShard OVERLAY = new OverlayStateShard(true);
   public static final Function<ResourceLocation, RenderType> ENTITY_CUTOUT_TRIANGLES = Util.memoize(
      rl -> {
         CompositeState state = CompositeState.builder()
            .setShaderState(RENDERTYPE_ENTITY_CUTOUT_SHADER)
            .setTextureState(new TextureStateShard(rl, false, false))
            .setTransparencyState(NO_TRANSPARENCY)
            .setLightmapState(LIGHTMAP)
            .setOverlayState(OVERLAY)
            .createCompositeState(true);
         return RenderType.create("entity_cutout_triangles", DefaultVertexFormat.NEW_ENTITY, Mode.TRIANGLES, 256, true, false, state);
      }
   );
}
