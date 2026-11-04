package fuzs.goldenagecombat;

import fuzs.goldenagecombat.config.ClientConfig;
import fuzs.goldenagecombat.config.CommonConfig;
import fuzs.goldenagecombat.config.ServerConfig;
import fuzs.goldenagecombat.data.DynamicEnchantmentRegistryProvider;
import fuzs.goldenagecombat.handler.AttackAttributeHandler;
import fuzs.goldenagecombat.handler.ClassicCombatHandler;
import fuzs.puzzleslib.api.config.v3.ConfigHolder;
import fuzs.puzzleslib.api.core.v1.ModConstructor;
import fuzs.puzzleslib.api.core.v1.context.PackRepositorySourcesContext;
import fuzs.puzzleslib.api.core.v1.utility.ResourceLocationHelper;
import fuzs.puzzleslib.api.data.v2.core.DataProviderContext.Factory;
import fuzs.puzzleslib.api.event.v1.ComputeItemAttributeModifiersCallback;
import fuzs.puzzleslib.api.event.v1.FinalizeItemComponentsCallback;
import fuzs.puzzleslib.api.event.v1.entity.ProjectileImpactCallback;
import fuzs.puzzleslib.api.event.v1.entity.living.LivingKnockBackCallback;
import fuzs.puzzleslib.api.event.v1.entity.living.UseItemEvents;
import fuzs.puzzleslib.api.event.v1.level.PlayLevelSoundEvents;
import fuzs.puzzleslib.api.resources.v1.DynamicPackResources;
import fuzs.puzzleslib.api.resources.v1.PackResourcesHelper;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.repository.RepositorySource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GoldenAgeCombat implements ModConstructor {
   public static final String MOD_ID = "goldenagecombat";
   public static final String MOD_NAME = "Golden Age Combat";
   public static final Logger LOGGER = LoggerFactory.getLogger("Golden Age Combat");
   public static final ConfigHolder CONFIG = ConfigHolder.builder("goldenagecombat")
      .client(ClientConfig.class)
      .common(CommonConfig.class)
      .server(ServerConfig.class);

   public void onConstructMod() {
      registerEventHandlers();
   }

   private static void registerEventHandlers() {
      PlayLevelSoundEvents.POSITION.register(ClassicCombatHandler::onPlaySoundAtPosition);
      UseItemEvents.FINISH.register(ClassicCombatHandler::onUseItemFinish);
      LivingKnockBackCallback.EVENT.register(ClassicCombatHandler::onLivingKnockBack);
      ProjectileImpactCallback.EVENT.register(ClassicCombatHandler::onProjectileImpact);
      ComputeItemAttributeModifiersCallback.EVENT.register(AttackAttributeHandler::onComputeItemAttributeModifiers);
      FinalizeItemComponentsCallback.EVENT.register(AttackAttributeHandler::onFinalizeItemComponents);
   }

   public void onAddDataPackFinders(PackRepositorySourcesContext context) {
      if (((CommonConfig)CONFIG.get(CommonConfig.class)).boostSharpness) {
         context.addRepositorySource(
            new RepositorySource[]{
               PackResourcesHelper.buildServerPack(
                  id("boosted_sharpness"), DynamicPackResources.create(new Factory[]{DynamicEnchantmentRegistryProvider::new}), true
               )
            }
         );
      }
   }

   public static ResourceLocation id(String path) {
      return ResourceLocationHelper.fromNamespaceAndPath("goldenagecombat", path);
   }
}
