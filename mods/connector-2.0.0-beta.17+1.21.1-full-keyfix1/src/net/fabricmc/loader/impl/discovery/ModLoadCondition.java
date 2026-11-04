package net.fabricmc.loader.impl.discovery;

public enum ModLoadCondition {
   ALWAYS,
   IF_POSSIBLE,
   IF_RECOMMENDED,
   IF_NEEDED;

   // $VF: synthetic method
   private static ModLoadCondition[] $values() {
      return new ModLoadCondition[]{ALWAYS, IF_POSSIBLE, IF_RECOMMENDED, IF_NEEDED};
   }
}
