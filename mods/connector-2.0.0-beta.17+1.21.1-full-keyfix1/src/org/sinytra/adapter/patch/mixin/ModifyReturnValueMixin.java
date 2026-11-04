package org.sinytra.adapter.patch.mixin;

import java.util.List;
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
import org.sinytra.adapter.patch.resolver.injection.InjectionPointResolver;
import org.sinytra.adapter.patch.resolver.special.InjectorOrdinalResolver;
import org.sinytra.adapter.util.MethodQualifier;

public class ModifyReturnValueMixin implements MixinType {
   private static final PropertyContainerTemplate TEMPLATE = ConfigurationTemplates.MIXIN_AT
      .extend()
      .pluralKeys(MixinKeys.TARGET_METHOD, MixinKeys.TARGET_AT)
      .build();

   @Override
   public PropertyContainerTemplate getConfigurationTemplate() {
      return TEMPLATE;
   }

   @Override
   public boolean canInject(MixinContext context, Configuration config) {
      return !context.classNode().name.toLowerCase().contains("mace");
   }

   @Override
   public TxResult preProcess(MixinContext context, MutableConfiguration clean, Resolvers resolvers, Processors processors) {
      resolvers.addBefore(InjectionPointResolver.class, new InjectorOrdinalResolver());
      clean.setParameters(
         MethodParameters.create(
            context.methodNode(),
            List.of(MethodParameters.ParamGroup.SINGLE_ANY, MethodParameters.ParamGroup.CAPTURED_PARAMS, MethodParameters.ParamGroup.LOCALS)
         )
      );
      return TxResult.SUCCESS;
   }

   @Override
   public TxResult postProcess(MixinContext context, Configuration clean, MutableConfiguration dirty, Recipe recipe) {
      if (dirty.getTargetMethod() == null) {
         return TxResult.FAIL;
      }

      MethodQualifier target = dirty.getTargetMethod();
      List<Type> dirtyCaptured = context.methods().resolveCapturedMethodParams(recipe.clean(), recipe.dirty());
      Type modifyingType = Type.getReturnType(target.desc());
      MethodParameters params = MethodParameters.builder()
         .putType(MethodParameters.ParamGroup.SINGLE_ANY, modifyingType)
         .putTypes(MethodParameters.ParamGroup.CAPTURED_PARAMS, dirtyCaptured)
         .put(MethodParameters.ParamGroup.LOCALS, clean.getParameters().get(MethodParameters.ParamGroup.LOCALS))
         .build();
      dirty.setParameters(params);
      dirty.setReturnType(modifyingType);
      return TxResult.SUCCESS;
   }
}
