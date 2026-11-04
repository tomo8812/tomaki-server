package fuzs.puzzleslib.api.entity.v1;

import java.util.function.Consumer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ItemStack;

public final class ProjectileHelper {
   private ProjectileHelper() {
   }

   public static <T extends Projectile> T spawnProjectileFromRotation(
      ProjectileHelper.ProjectileFactory<T> creator,
      ServerLevel serverLevel,
      ItemStack itemStack,
      LivingEntity source,
      float yOffset,
      float pow,
      float uncertainty
   ) {
      return spawnProjectile(
         creator.create(serverLevel, source, itemStack),
         serverLevel,
         itemStack,
         shotProjectile -> shotProjectile.shootFromRotation(source, source.getXRot(), source.getYRot(), yOffset, pow, uncertainty)
      );
   }

   public static <T extends Projectile> T spawnProjectileUsingShoot(
      ProjectileHelper.ProjectileFactory<T> creator,
      ServerLevel serverLevel,
      ItemStack itemStack,
      LivingEntity source,
      double targetX,
      double targetY,
      double targetZ,
      float pow,
      float uncertainty
   ) {
      return spawnProjectile(
         creator.create(serverLevel, source, itemStack),
         serverLevel,
         itemStack,
         shotProjectile -> shotProjectile.shoot(targetX, targetY, targetZ, pow, uncertainty)
      );
   }

   public static <T extends Projectile> T spawnProjectileUsingShoot(
      T projectile, ServerLevel serverLevel, ItemStack itemStack, double targetX, double targetY, double targetZ, float pow, float uncertainty
   ) {
      return spawnProjectile(projectile, serverLevel, itemStack, shotProjectile -> shotProjectile.shoot(targetX, targetY, targetZ, pow, uncertainty));
   }

   public static <T extends Projectile> T spawnProjectile(T projectile, ServerLevel serverLevel, ItemStack itemStack) {
      return spawnProjectile(projectile, serverLevel, itemStack, shotProjectile -> {});
   }

   public static <T extends Projectile> T spawnProjectile(T projectile, ServerLevel serverLevel, ItemStack itemStack, Consumer<T> shootFunction) {
      shootFunction.accept(projectile);
      serverLevel.addFreshEntity(projectile);
      return projectile;
   }

   @FunctionalInterface
   public interface ProjectileFactory<T extends Projectile> {
      T create(ServerLevel var1, LivingEntity var2, ItemStack var3);
   }
}
