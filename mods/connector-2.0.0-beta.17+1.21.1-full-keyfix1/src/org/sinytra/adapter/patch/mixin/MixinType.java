package org.sinytra.adapter.patch.mixin;

import java.util.EnumSet;
import java.util.Set;
import org.sinytra.adapter.env.ctx.MixinContext;
import org.sinytra.adapter.patch.Recipe;
import org.sinytra.adapter.patch.TxResult;
import org.sinytra.adapter.patch.config.Configuration;
import org.sinytra.adapter.patch.config.MutableConfiguration;
import org.sinytra.adapter.patch.config.PropertyContainerTemplate;
import org.sinytra.adapter.patch.processor.Processors;
import org.sinytra.adapter.patch.resolver.Resolvers;

public interface MixinType {
   PropertyContainerTemplate getConfigurationTemplate();

   default Set<MixinFlag> getFlags() {
      return EnumSet.noneOf(MixinFlag.class);
   }

   default boolean canInject(MixinContext context, Configuration config) {
      return true;
   }

   TxResult preProcess(MixinContext var1, MutableConfiguration var2, Resolvers var3, Processors var4);

   TxResult postProcess(MixinContext var1, Configuration var2, MutableConfiguration var3, Recipe var4);
}
