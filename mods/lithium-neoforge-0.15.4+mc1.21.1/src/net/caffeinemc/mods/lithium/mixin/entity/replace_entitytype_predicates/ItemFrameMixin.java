package net.caffeinemc.mods.lithium.mixin.entity.replace_entitytype_predicates;

import java.util.List;
import java.util.function.Predicate;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.decoration.HangingEntity;
import net.minecraft.world.entity.decoration.ItemFrame;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ItemFrame.class)
public abstract class ItemFrameMixin extends HangingEntity {
   protected ItemFrameMixin(EntityType<? extends HangingEntity> entityType_1, Level world_1) {
      super(entityType_1, world_1);
   }

   @Redirect(
      method = "survives()Z",
      at = @At(
         value = "INVOKE",
         target = "Lnet/minecraft/world/level/Level;getEntities(Lnet/minecraft/world/entity/Entity;Lnet/minecraft/world/phys/AABB;Ljava/util/function/Predicate;)Ljava/util/List;"
      )
   )
   private List<Entity> getAbstractDecorationEntities(Level world, Entity excluded, AABB box, Predicate<? super Entity> predicate) {
      return predicate == HANGING_ENTITY
         ? world.getEntitiesOfClass(HangingEntity.class, box, entity -> entity != excluded)
         : world.getEntities(excluded, box, predicate);
   }
}
