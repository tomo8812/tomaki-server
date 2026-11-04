package org.sinytra.adapter.patch.mixin;

import java.util.HashMap;
import java.util.Map;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Redirect;

public class MixinTypes {
   private static final Map<String, MixinType> MIXIN_TYPES = new HashMap<>();
   public static final MixinType INJECT = new InjectMixin();
   public static final MixinType MODIFY_VAR = new ModifyVariableMixin();
   public static final MixinType MODIFY_ARG = new ModifyArgMixin();
   public static final MixinType MODIFY_RET = new ModifyReturnValueMixin();
   public static final MixinType REDIRECT = new RedirectMixin();
   public static final MixinType WRAP_OP = new WrapOperationMixin();
   public static final MixinType MODIFY_EXPR_VAL = new ModifyExpressionValueMixin();

   @Nullable
   public static MixinType getMixinType(String annotation) {
      return MIXIN_TYPES.get(annotation);
   }

   private static void registerMixinType(Class<?> annotation, MixinType type) {
      String internalName = annotation.getName().replace('.', '/');
      registerMixinType(internalName, type);
   }

   private static void registerMixinType(String annInternalName, MixinType type) {
      MIXIN_TYPES.put(annInternalName, type);
   }

   static {
      registerMixinType(Inject.class, INJECT);
      registerMixinType(ModifyVariable.class, MODIFY_VAR);
      registerMixinType(ModifyArg.class, MODIFY_ARG);
      registerMixinType("com/llamalad7/mixinextras/injector/ModifyReturnValue", MODIFY_RET);
      registerMixinType(Redirect.class, REDIRECT);
      registerMixinType("com/llamalad7/mixinextras/injector/wrapoperation/WrapOperation", WRAP_OP);
      registerMixinType("com/llamalad7/mixinextras/injector/ModifyExpressionValue", MODIFY_EXPR_VAL);
   }
}
