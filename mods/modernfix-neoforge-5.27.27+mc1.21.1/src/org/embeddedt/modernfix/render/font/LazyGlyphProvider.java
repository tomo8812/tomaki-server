package org.embeddedt.modernfix.render.font;

import com.mojang.blaze3d.font.GlyphInfo;
import com.mojang.blaze3d.font.GlyphProvider;
import com.mojang.datafixers.util.Either;
import com.mojang.serialization.MapCodec;
import it.unimi.dsi.fastutil.ints.IntSet;
import java.io.IOException;
import java.lang.ref.SoftReference;
import java.util.function.Function;
import net.minecraft.client.gui.font.providers.GlyphProviderDefinition;
import net.minecraft.client.gui.font.providers.GlyphProviderType;
import net.minecraft.client.gui.font.providers.GlyphProviderDefinition.Loader;
import net.minecraft.client.gui.font.providers.GlyphProviderDefinition.Reference;
import net.minecraft.server.packs.resources.ResourceManager;
import org.jetbrains.annotations.Nullable;

public class LazyGlyphProvider implements GlyphProvider {
   private final Loader loader;
   private final ResourceManager manager;
   private SoftReference<GlyphProvider> innerProvider = new SoftReference<>(null);

   LazyGlyphProvider(Loader loader, ResourceManager manager) {
      this.loader = loader;
      this.manager = manager;
   }

   public void close() {
      GlyphProvider prov = this.innerProvider.get();
      if (prov != null) {
         prov.close();
      }
   }

   @Nullable
   private synchronized GlyphProvider getGlyphProvider() {
      GlyphProvider prov = this.innerProvider.get();
      if (prov == null) {
         try {
            prov = this.loader.load(this.manager);
         } catch (IOException e) {
            return null;
         }

         this.innerProvider = new SoftReference<>(prov);
      }

      return prov;
   }

   @Nullable
   public GlyphInfo getGlyph(int character) {
      GlyphProvider prov = this.getGlyphProvider();
      return prov != null ? prov.getGlyph(character) : null;
   }

   public IntSet getSupportedGlyphs() {
      GlyphProvider prov = this.getGlyphProvider();
      return prov != null ? prov.getSupportedGlyphs() : IntSet.of();
   }

   public static MapCodec<? extends GlyphProviderDefinition> wrap(MapCodec<? extends GlyphProviderDefinition> codec) {
      return codec.xmap(LazyGlyphProvider.Definition::new, LazyGlyphProvider.Definition::delegate);
   }

   private static class Definition implements GlyphProviderDefinition {
      private final GlyphProviderDefinition delegate;

      public Definition(GlyphProviderDefinition delegate) {
         this.delegate = delegate;
      }

      public GlyphProviderType type() {
         return this.delegate.type();
      }

      public Either<Loader, Reference> unpack() {
         return this.delegate.unpack().mapBoth(loader -> resourceManager -> new LazyGlyphProvider(loader, resourceManager), Function.identity());
      }

      public <T extends GlyphProviderDefinition> T delegate() {
         return (T)this.delegate;
      }
   }
}
