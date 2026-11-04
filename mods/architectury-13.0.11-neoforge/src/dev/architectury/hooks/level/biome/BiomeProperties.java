package dev.architectury.hooks.level.biome;

public interface BiomeProperties {
   ClimateProperties getClimateProperties();

   EffectsProperties getEffectsProperties();

   GenerationProperties getGenerationProperties();

   SpawnProperties getSpawnProperties();

   interface Mutable extends BiomeProperties {
      ClimateProperties.Mutable getClimateProperties();

      EffectsProperties.Mutable getEffectsProperties();

      GenerationProperties.Mutable getGenerationProperties();

      SpawnProperties.Mutable getSpawnProperties();

      // $VF: synthetic method
      // $VF: bridge method
      @Override
      default SpawnProperties getSpawnProperties() {
         return this.getSpawnProperties();
      }

      // $VF: synthetic method
      // $VF: bridge method
      @Override
      default GenerationProperties getGenerationProperties() {
         return this.getGenerationProperties();
      }

      // $VF: synthetic method
      // $VF: bridge method
      @Override
      default EffectsProperties getEffectsProperties() {
         return this.getEffectsProperties();
      }

      // $VF: synthetic method
      // $VF: bridge method
      @Override
      default ClimateProperties getClimateProperties() {
         return this.getClimateProperties();
      }
   }
}
