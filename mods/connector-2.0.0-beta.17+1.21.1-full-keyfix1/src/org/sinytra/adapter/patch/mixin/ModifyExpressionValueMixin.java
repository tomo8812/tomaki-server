package org.sinytra.adapter.patch.mixin;

import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import org.objectweb.asm.Type;
import org.sinytra.adapter.env.ctx.MixinContext;
import org.sinytra.adapter.env.param.MethodParameters;
import org.sinytra.adapter.patch.Recipe;
import org.sinytra.adapter.patch.TxResult;
import org.sinytra.adapter.patch.config.Configuration;
import org.sinytra.adapter.patch.config.ConfigurationTemplates;
import org.sinytra.adapter.patch.config.MutableConfiguration;
import org.sinytra.adapter.patch.config.PropertyContainerTemplate;
import org.sinytra.adapter.patch.config.key.MixinKeys;
import org.sinytra.adapter.patch.processor.Processors;
import org.sinytra.adapter.patch.resolver.Resolvers;
import org.sinytra.adapter.patch.resolver.injection.ArbitraryInjectionPointSubResolver;
import org.sinytra.adapter.patch.resolver.injection.InjectionPointResolver;
import org.sinytra.adapter.patch.resolver.special.ResolverSyntheticInstanceof;
import org.sinytra.adapter.util.MethodQualifier;

public class ModifyExpressionValueMixin implements MixinType {
   private static final PropertyContainerTemplate TEMPLATE = ConfigurationTemplates.MIXIN_AT
      .extend()
      .pluralKeys(MixinKeys.TARGET_METHOD, MixinKeys.TARGET_AT)
      .build();

   @Override
   public PropertyContainerTemplate getConfigurationTemplate() {
      return TEMPLATE;
   }

   @Override
   public Set<MixinFlag> getFlags() {
      return EnumSet.of(MixinFlag.RETURN_TYPE_SENSITIVE);
   }

   @Override
   public TxResult preProcess(MixinContext context, MutableConfiguration clean, Resolvers resolvers, Processors processors) {
      resolvers.getOrThrow(InjectionPointResolver.class).addSubResolver(new ArbitraryInjectionPointSubResolver());
      resolvers.addBefore(InjectionPointResolver.class, new ResolverSyntheticInstanceof(true));
      clean.setParameters(
         MethodParameters.create(context.methodNode(), List.of(MethodParameters.ParamGroup.SINGLE_ANY, MethodParameters.ParamGroup.CAPTURED_PARAMS))
      );
      return TxResult.SUCCESS;
   }

   @Override
   public TxResult postProcess(MixinContext context, Configuration clean, MutableConfiguration dirty, Recipe recipe) {
      if (dirty.getTargetMethod() != null && dirty.getAtData() != null) {
         if (!dirty.getAtData().getValue().equals("INVOKE")) {
            dirty.inheritParameters();
            dirty.inheritReturnType();
            return TxResult.SUCCESS;
         } else {
            MethodQualifier targetDesc = dirty.getAtData().getTarget().flatMap(MethodQualifier::parse).orElse(null);
            if (targetDesc == null) {
               dirty.inheritParameters();
               dirty.inheritReturnType();
               return TxResult.SUCCESS;
            } else {
               List<Type> dirtyCaptured = context.methods().resolveCapturedMethodParams(recipe.clean(), recipe.dirty());
               Type modifyingType = Type.getReturnType(targetDesc.desc());
               MethodParameters params = MethodParameters.builder()
                  .putType(MethodParameters.ParamGroup.SINGLE_ANY, modifyingType)
                  .putTypes(MethodParameters.ParamGroup.CAPTURED_PARAMS, dirtyCaptured)
                  .build();
               dirty.setParameters(params);
               dirty.setReturnType(modifyingType);
               return TxResult.SUCCESS;
            }
         }
      } else {
         return TxResult.FAIL;
      }
   }
}
