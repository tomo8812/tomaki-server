package fuzs.puzzleslib.api.client.particle.v1;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.TextureSheetParticle;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.util.RandomSource;
import org.jetbrains.annotations.Nullable;

@FunctionalInterface
public interface ParticleProvider<T extends ParticleOptions> extends net.minecraft.client.particle.ParticleProvider<T> {
   @Nullable
   Particle createParticle(T var1, ClientLevel var2, double var3, double var5, double var7, double var9, double var11, double var13, RandomSource var15);

   @Nullable
   default Particle createParticle(T options, ClientLevel level, double x, double y, double z, double xAux, double yAux, double zAux) {
      return this.createParticle(options, level, x, y, z, xAux, yAux, zAux, RandomSource.create());
   }

   @FunctionalInterface
   interface Sprite<T extends ParticleOptions> extends net.minecraft.client.particle.ParticleProvider.Sprite<T> {
      @Nullable
      TextureSheetParticle createParticle(
         T var1, ClientLevel var2, double var3, double var5, double var7, double var9, double var11, double var13, RandomSource var15
      );

      @Nullable
      default TextureSheetParticle createParticle(T options, ClientLevel level, double x, double y, double z, double xAux, double yAux, double zAux) {
         return this.createParticle(options, level, x, y, z, xAux, yAux, zAux, RandomSource.create());
      }
   }
}
