package org.embeddedt.modernfix.common.mixin.perf.cache_profile_texture_url;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import org.embeddedt.modernfix.annotation.ClientOnlyMixin;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(targets = "net/minecraft/client/resources/SkinManager$TextureCache")
@ClientOnlyMixin
public class SkinManagerMixin {
   @Unique
   private final Cache<MinecraftProfileTexture, String> mfix$hashCache = CacheBuilder.newBuilder()
      .expireAfterAccess(60L, TimeUnit.SECONDS)
      .concurrencyLevel(1)
      .build();

   @Redirect(
      method = {"getOrLoad", "registerTexture"},
      at = @At(value = "INVOKE", target = "Lcom/mojang/authlib/minecraft/MinecraftProfileTexture;getHash()Ljava/lang/String;", remap = false)
   )
   private String useCachedHash(MinecraftProfileTexture texture) {
      String hash = (String)this.mfix$hashCache.getIfPresent(texture);
      if (hash != null) {
         return hash;
      }

      try {
         return (String)this.mfix$hashCache.get(texture, texture::getHash);
      } catch (ExecutionException e) {
         throw new RuntimeException(e);
      }
   }
}
