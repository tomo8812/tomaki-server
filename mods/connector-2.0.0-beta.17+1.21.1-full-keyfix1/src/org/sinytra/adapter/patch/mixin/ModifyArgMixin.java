package org.sinytra.adapter.patch.mixin;

import java.util.List;
import org.jetbrains.annotations.Nullable;
import org.objectweb.asm.Type;
import org.sinytra.adapter.env.ann.AtData;
import org.sinytra.adapter.env.ctx.MixinContext;
import org.sinytra.adapter.env.param.MethodParameters;
import org.sinytra.adapter.env.param.Parameter;
import org.sinytra.adapter.env.param.Parameters;
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
import org.sinytra.adapter.types.TypeAdapter;
import org.sinytra.adapter.util.MethodQualifier;

public class ModifyArgMixin implements MixinType {
   private static final PropertyContainerTemplate TEMPLATE = ConfigurationTemplates.MIXIN_AT
      .extend()
      .keys(MixinKeys.INDEX)
      .pluralKeys(MixinKeys.TARGET_METHOD)
      .build();

   @Override
   public PropertyContainerTemplate getConfigurationTemplate() {
      return TEMPLATE;
   }

   @Override
   public TxResult preProcess(MixinContext context, MutableConfiguration clean, Resolvers resolvers, Processors processors) {
      resolvers.getOrThrow(InjectionPointResolver.class).addSubResolver(new ArbitraryInjectionPointSubResolver());
      clean.setParameters(MethodParameters.create(context.methodNode(), List.of(MethodParameters.ParamGroup.SINGLE_ANY)));
      return TxResult.SUCCESS;
   }

   @Override
   public TxResult postProcess(MixinContext context, Configuration clean, MutableConfiguration dirty, Recipe recipe) {
      dirty.inheritProperyIfAbsent(MixinKeys.ORDINAL);
      dirty.inheritProperyIfAbsent(MixinKeys.INDEX);
      if (clean.getAtData().equals(dirty.getAtData())) {
         dirty.inheritParameters();
         dirty.inheritReturnType();
         return TxResult.SUCCESS;
      }

      Type type = findArgType(context, recipe.clean().getAtData(), dirty.getAtData(), dirty);
      if (type != null) {
         MethodParameters parameters = MethodParameters.create(context.methodNode(), List.of(MethodParameters.ParamGroup.SINGLE_ANY));
         parameters.set(MethodParameters.ParamGroup.SINGLE_ANY, Parameter.simple(type));
         dirty.setParameters(parameters);
         dirty.setReturnType(type);
      }

      return TxResult.SUCCESS;
   }

   @Nullable
   private static Type findArgType(MixinContext context, AtData cleanAtData, AtData atData, Configuration dirty) {
      MethodQualifier cleanQualifier = cleanAtData.getTarget().flatMap(MethodQualifier::parse).orElse(null);
      MethodQualifier dirtyQualifier = atData.getTarget().flatMap(MethodQualifier::parse).orElse(null);
      if (cleanQualifier != null && dirtyQualifier != null) {
         List<Type> cleanArgs = Parameters.getParameterTypes(cleanQualifier.desc());
         List<Type> dirtyArgs = Parameters.getParameterTypes(dirtyQualifier.desc());
         if (dirty.hasProperty(MixinKeys.INDEX)) {
            int dirtyIndex = dirty.getProperty(MixinKeys.INDEX).orElseThrow();
            return dirtyIndex < dirtyArgs.size() ? dirtyArgs.get(dirtyIndex) : null;
         }

         if (cleanArgs.size() == 1 && dirtyArgs.size() == 1) {
            if (cleanArgs.getFirst().equals(dirtyArgs.getFirst())) {
               return dirtyArgs.getFirst();
            }

            Type dirtyType = dirtyArgs.getFirst();
            TypeAdapter adapter = context.getTypeAdapter(cleanArgs.getFirst(), dirtyType);
            if (adapter != null) {
               return dirtyType;
            }
         }
      }

      return null;
   }
}
