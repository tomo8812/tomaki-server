package org.sinytra.adapter.patch.mixin;

public enum MixinFlag {
   RETURN_TYPE_SENSITIVE,
   AT_TARGET_SENSITIVE,
   TARGETS_VARIABLE,
   ACCEPTS_INSTANCE;

   // $VF: synthetic method
   private static MixinFlag[] $values() {
      return new MixinFlag[]{RETURN_TYPE_SENSITIVE, AT_TARGET_SENSITIVE, TARGETS_VARIABLE, ACCEPTS_INSTANCE};
   }
}
