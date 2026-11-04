package org.valkyrienskies.core.impl.program;

import javax.inject.Provider;
import org.valkyrienskies.core.api.physics.blockstates.BoxesBlockShape;
import org.valkyrienskies.core.api.physics.blockstates.LiquidState;
import org.valkyrienskies.core.api.physics.blockstates.SolidState;
import org.valkyrienskies.core.impl.game.ships.serialization.vspipeline.VSPipelineSerializer;
import org.valkyrienskies.core.impl.networking.simple.SimplePacketNetworking;
import org.valkyrienskies.core.impl.shadow.CH;
import org.valkyrienskies.core.impl.shadow.DN;
import org.valkyrienskies.core.impl.shadow.EX;
import org.valkyrienskies.core.impl.shadow.FP;
import org.valkyrienskies.core.impl.shadow.Fb;
import org.valkyrienskies.core.impl.shadow.Fl;
import org.valkyrienskies.core.impl.shadow.Fm;
import org.valkyrienskies.core.impl.shadow.ek;
import org.valkyrienskies.core.internal.collision.VsiSolidShapeUtils;

public final class VSCoreImpl_Factory implements ek<VSCoreImpl> {
   private final Provider<Fl> networkingProvider;
   private final Provider<EX> hooksProvider;
   private final Provider<Fm> configuratorProvider;
   private final Provider<Fb> tcpProvider;
   private final Provider<FP.a> pipelineComponentFactoryProvider;
   private final Provider<VSPipelineSerializer> pipelineSerializerProvider;
   private final Provider<DN> blockTypesProvider;
   private final Provider<SimplePacketNetworking> simplePacketNetworkingProvider;
   private final Provider<VsiSolidShapeUtils> solidShapeUtilsProvider;
   private final Provider<LiquidState.Builder> newLiquidStateBuilderProvider;
   private final Provider<SolidState.Builder> newSolidStateBuilderProvider;
   private final Provider<BoxesBlockShape.Builder> newBoxesBlockShapeBuilderProvider;
   private final Provider<CH> attachmentRegistryProvider;

   public VSCoreImpl_Factory(
      Provider<Fl> networkingProvider,
      Provider<EX> hooksProvider,
      Provider<Fm> configuratorProvider,
      Provider<Fb> tcpProvider,
      Provider<FP.a> pipelineComponentFactoryProvider,
      Provider<VSPipelineSerializer> pipelineSerializerProvider,
      Provider<DN> blockTypesProvider,
      Provider<SimplePacketNetworking> simplePacketNetworkingProvider,
      Provider<VsiSolidShapeUtils> solidShapeUtilsProvider,
      Provider<LiquidState.Builder> newLiquidStateBuilderProvider,
      Provider<SolidState.Builder> newSolidStateBuilderProvider,
      Provider<BoxesBlockShape.Builder> newBoxesBlockShapeBuilderProvider,
      Provider<CH> attachmentRegistryProvider
   ) {
      this.networkingProvider = networkingProvider;
      this.hooksProvider = hooksProvider;
      this.configuratorProvider = configuratorProvider;
      this.tcpProvider = tcpProvider;
      this.pipelineComponentFactoryProvider = pipelineComponentFactoryProvider;
      this.pipelineSerializerProvider = pipelineSerializerProvider;
      this.blockTypesProvider = blockTypesProvider;
      this.simplePacketNetworkingProvider = simplePacketNetworkingProvider;
      this.solidShapeUtilsProvider = solidShapeUtilsProvider;
      this.newLiquidStateBuilderProvider = newLiquidStateBuilderProvider;
      this.newSolidStateBuilderProvider = newSolidStateBuilderProvider;
      this.newBoxesBlockShapeBuilderProvider = newBoxesBlockShapeBuilderProvider;
      this.attachmentRegistryProvider = attachmentRegistryProvider;
   }

   public final VSCoreImpl get() {
      return newInstance(
         this.networkingProvider.get(),
         this.hooksProvider.get(),
         this.configuratorProvider.get(),
         this.tcpProvider.get(),
         this.pipelineComponentFactoryProvider.get(),
         this.pipelineSerializerProvider.get(),
         this.blockTypesProvider.get(),
         this.simplePacketNetworkingProvider.get(),
         this.solidShapeUtilsProvider.get(),
         this.newLiquidStateBuilderProvider,
         this.newSolidStateBuilderProvider,
         this.newBoxesBlockShapeBuilderProvider,
         this.attachmentRegistryProvider.get()
      );
   }

   public static VSCoreImpl_Factory create(
      Provider<Fl> networkingProvider,
      Provider<EX> hooksProvider,
      Provider<Fm> configuratorProvider,
      Provider<Fb> tcpProvider,
      Provider<FP.a> pipelineComponentFactoryProvider,
      Provider<VSPipelineSerializer> pipelineSerializerProvider,
      Provider<DN> blockTypesProvider,
      Provider<SimplePacketNetworking> simplePacketNetworkingProvider,
      Provider<VsiSolidShapeUtils> solidShapeUtilsProvider,
      Provider<LiquidState.Builder> newLiquidStateBuilderProvider,
      Provider<SolidState.Builder> newSolidStateBuilderProvider,
      Provider<BoxesBlockShape.Builder> newBoxesBlockShapeBuilderProvider,
      Provider<CH> attachmentRegistryProvider
   ) {
      return new VSCoreImpl_Factory(
         networkingProvider,
         hooksProvider,
         configuratorProvider,
         tcpProvider,
         pipelineComponentFactoryProvider,
         pipelineSerializerProvider,
         blockTypesProvider,
         simplePacketNetworkingProvider,
         solidShapeUtilsProvider,
         newLiquidStateBuilderProvider,
         newSolidStateBuilderProvider,
         newBoxesBlockShapeBuilderProvider,
         attachmentRegistryProvider
      );
   }

   public static VSCoreImpl newInstance(
      Fl networking,
      EX hooks,
      Fm configurator,
      Fb tcp,
      FP.a pipelineComponentFactory,
      VSPipelineSerializer pipelineSerializer,
      DN blockTypes,
      SimplePacketNetworking simplePacketNetworking,
      VsiSolidShapeUtils solidShapeUtils,
      Provider<LiquidState.Builder> newLiquidStateBuilder,
      Provider<SolidState.Builder> newSolidStateBuilder,
      Provider<BoxesBlockShape.Builder> newBoxesBlockShapeBuilder,
      CH attachmentRegistry
   ) {
      return new VSCoreImpl(
         networking,
         hooks,
         configurator,
         tcp,
         pipelineComponentFactory,
         pipelineSerializer,
         blockTypes,
         simplePacketNetworking,
         solidShapeUtils,
         newLiquidStateBuilder,
         newSolidStateBuilder,
         newBoxesBlockShapeBuilder,
         attachmentRegistry
      );
   }

   // $VF: synthetic method
   // $VF: bridge method
   @Override
   public final Object get() {
      return this.get();
   }
}
