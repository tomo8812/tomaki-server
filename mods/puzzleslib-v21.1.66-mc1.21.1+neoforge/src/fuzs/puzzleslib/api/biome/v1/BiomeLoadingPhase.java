package fuzs.puzzleslib.api.biome.v1;

public enum BiomeLoadingPhase {
   ADDITIONS,
   REMOVALS,
   MODIFICATIONS,
   POST_PROCESSING;

   // $VF: synthetic method
   private static BiomeLoadingPhase[] $values() {
      return new BiomeLoadingPhase[]{ADDITIONS, REMOVALS, MODIFICATIONS, POST_PROCESSING};
   }
}
