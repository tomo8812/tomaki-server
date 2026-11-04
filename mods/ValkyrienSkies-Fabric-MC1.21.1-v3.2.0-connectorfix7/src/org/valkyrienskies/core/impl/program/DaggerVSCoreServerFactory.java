package org.valkyrienskies.core.impl.program;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Map;
import org.valkyrienskies.core.impl.api_impl.physics.blockstates.BoxesBlockShapeImpl_BuilderImpl_Factory;
import org.valkyrienskies.core.impl.api_impl.physics.blockstates.LiquidStateImpl_BuilderImpl_Factory;
import org.valkyrienskies.core.impl.api_impl.physics.blockstates.SolidStateImpl;
import org.valkyrienskies.core.impl.api_impl.physics.blockstates.SolidStateImpl_BuilderImpl_Factory;
import org.valkyrienskies.core.impl.config.VSCoreConfig;
import org.valkyrienskies.core.impl.game.ChunkAllocator;
import org.valkyrienskies.core.impl.game.ships.ShipData;
import org.valkyrienskies.core.impl.game.ships.ShipData_Factory_Factory;
import org.valkyrienskies.core.impl.game.ships.serialization.ChainUpdater;
import org.valkyrienskies.core.impl.game.ships.serialization.shipinertia.ShipInertiaConverter;
import org.valkyrienskies.core.impl.game.ships.serialization.shipinertia.ShipInertiaConverter_Factory;
import org.valkyrienskies.core.impl.game.ships.serialization.shipserver.ServerShipDataConverterImpl;
import org.valkyrienskies.core.impl.game.ships.serialization.shipserver.ServerShipDataConverterImpl_Factory;
import org.valkyrienskies.core.impl.game.ships.serialization.shipserver.dto.ServerShipDataV0UpdaterImpl;
import org.valkyrienskies.core.impl.game.ships.serialization.shipserver.dto.ServerShipDataV0UpdaterImpl_Factory;
import org.valkyrienskies.core.impl.game.ships.serialization.shipserver.dto.ServerShipDataV1UpdaterImpl;
import org.valkyrienskies.core.impl.game.ships.serialization.shipserver.dto.ServerShipDataV1UpdaterImpl_Factory;
import org.valkyrienskies.core.impl.game.ships.serialization.shipserver.dto.ServerShipDataV2UpdaterImpl;
import org.valkyrienskies.core.impl.game.ships.serialization.shipserver.dto.ServerShipDataV2UpdaterImpl_Factory;
import org.valkyrienskies.core.impl.game.ships.serialization.shipserver.dto.ServerShipDataV3UpdaterImpl;
import org.valkyrienskies.core.impl.game.ships.serialization.shipserver.dto.ServerShipDataV3UpdaterImpl_Factory;
import org.valkyrienskies.core.impl.game.ships.serialization.shiptransform.ShipTransformConverterImpl;
import org.valkyrienskies.core.impl.game.ships.serialization.shiptransform.ShipTransformConverterImpl_Factory;
import org.valkyrienskies.core.impl.game.ships.serialization.vspipeline.VSPipelineChainUpdaterModule;
import org.valkyrienskies.core.impl.game.ships.serialization.vspipeline.VSPipelineChainUpdaterModule_UpdaterFactory;
import org.valkyrienskies.core.impl.game.ships.serialization.vspipeline.VSPipelineDataConverterImpl;
import org.valkyrienskies.core.impl.game.ships.serialization.vspipeline.VSPipelineDataConverterImpl_Factory;
import org.valkyrienskies.core.impl.game.ships.serialization.vspipeline.VSPipelineSerializer;
import org.valkyrienskies.core.impl.game.ships.serialization.vspipeline.VSPipelineSerializer_Factory;
import org.valkyrienskies.core.impl.game.ships.serialization.vspipeline.dto.VSPipelineDataV1UpdaterImpl;
import org.valkyrienskies.core.impl.game.ships.serialization.vspipeline.dto.VSPipelineDataV1UpdaterImpl_Factory;
import org.valkyrienskies.core.impl.game.ships.serialization.vspipeline.dto.VSPipelineDataV2UpdaterImpl;
import org.valkyrienskies.core.impl.game.ships.serialization.vspipeline.dto.VSPipelineDataV2UpdaterImpl_Factory;
import org.valkyrienskies.core.impl.game.ships.serialization.vspipeline.dto.VSPipelineDataV3UpdaterImpl;
import org.valkyrienskies.core.impl.game.ships.serialization.vspipeline.dto.VSPipelineDataV3UpdaterImpl_Factory;
import org.valkyrienskies.core.impl.game.ships.serialization.vspipeline.dto.VSPipelineDataV4UpdaterImpl;
import org.valkyrienskies.core.impl.game.ships.serialization.vspipeline.dto.VSPipelineDataV4UpdaterImpl_Factory;
import org.valkyrienskies.core.impl.game.ships.serialization.vspipeline.dto.VSPipelineDataV5;
import org.valkyrienskies.core.impl.networking.simple.SimplePacketNetworking;
import org.valkyrienskies.core.impl.shadow.CA;
import org.valkyrienskies.core.impl.shadow.CC;
import org.valkyrienskies.core.impl.shadow.CH;
import org.valkyrienskies.core.impl.shadow.CJ;
import org.valkyrienskies.core.impl.shadow.CN;
import org.valkyrienskies.core.impl.shadow.CO;
import org.valkyrienskies.core.impl.shadow.CQ;
import org.valkyrienskies.core.impl.shadow.CY;
import org.valkyrienskies.core.impl.shadow.CZ;
import org.valkyrienskies.core.impl.shadow.Cy;
import org.valkyrienskies.core.impl.shadow.Cz;
import org.valkyrienskies.core.impl.shadow.DN;
import org.valkyrienskies.core.impl.shadow.DO;
import org.valkyrienskies.core.impl.shadow.DP;
import org.valkyrienskies.core.impl.shadow.DQ;
import org.valkyrienskies.core.impl.shadow.DR;
import org.valkyrienskies.core.impl.shadow.DW;
import org.valkyrienskies.core.impl.shadow.DX;
import org.valkyrienskies.core.impl.shadow.Do;
import org.valkyrienskies.core.impl.shadow.Dp;
import org.valkyrienskies.core.impl.shadow.EB;
import org.valkyrienskies.core.impl.shadow.EC;
import org.valkyrienskies.core.impl.shadow.EI;
import org.valkyrienskies.core.impl.shadow.EJ;
import org.valkyrienskies.core.impl.shadow.EK;
import org.valkyrienskies.core.impl.shadow.EL;
import org.valkyrienskies.core.impl.shadow.EM;
import org.valkyrienskies.core.impl.shadow.EN;
import org.valkyrienskies.core.impl.shadow.ER;
import org.valkyrienskies.core.impl.shadow.ES;
import org.valkyrienskies.core.impl.shadow.EX;
import org.valkyrienskies.core.impl.shadow.EZ;
import org.valkyrienskies.core.impl.shadow.Ea;
import org.valkyrienskies.core.impl.shadow.Ek;
import org.valkyrienskies.core.impl.shadow.Et;
import org.valkyrienskies.core.impl.shadow.Eu;
import org.valkyrienskies.core.impl.shadow.FH;
import org.valkyrienskies.core.impl.shadow.FI;
import org.valkyrienskies.core.impl.shadow.FJ;
import org.valkyrienskies.core.impl.shadow.FK;
import org.valkyrienskies.core.impl.shadow.FN;
import org.valkyrienskies.core.impl.shadow.FO;
import org.valkyrienskies.core.impl.shadow.FP;
import org.valkyrienskies.core.impl.shadow.FQ;
import org.valkyrienskies.core.impl.shadow.FR;
import org.valkyrienskies.core.impl.shadow.FV;
import org.valkyrienskies.core.impl.shadow.Fb;
import org.valkyrienskies.core.impl.shadow.Fe;
import org.valkyrienskies.core.impl.shadow.Ff;
import org.valkyrienskies.core.impl.shadow.Fl;
import org.valkyrienskies.core.impl.shadow.Fm;
import org.valkyrienskies.core.impl.shadow.Fn;
import org.valkyrienskies.core.impl.shadow.Fo;
import org.valkyrienskies.core.impl.shadow.Fp;
import org.valkyrienskies.core.impl.shadow.Fu;
import org.valkyrienskies.core.impl.shadow.Fv;
import org.valkyrienskies.core.impl.shadow.Fy;
import org.valkyrienskies.core.impl.shadow.Fz;
import org.valkyrienskies.core.impl.shadow.GK;
import org.valkyrienskies.core.impl.shadow.GM;
import org.valkyrienskies.core.impl.shadow.GO;
import org.valkyrienskies.core.impl.shadow.eD;
import org.valkyrienskies.core.impl.shadow.ei;
import org.valkyrienskies.core.impl.shadow.ej;
import org.valkyrienskies.core.impl.shadow.ev;
import org.valkyrienskies.core.impl.shadow.ew;
import org.valkyrienskies.core.internal.hooks.VsiCoreHooksOut;
import org.valkyrienskies.core.internal.ships.VsiMutableQueryableShipData;

public final class DaggerVSCoreServerFactory {
   private DaggerVSCoreServerFactory() {
   }

   public static DaggerVSCoreServerFactory.Builder builder() {
      return new DaggerVSCoreServerFactory.Builder();
   }

   public static final class Builder {
      private VSCoreModule vSCoreModule;
      private GK vSJacksonModule;
      private VSPipelineChainUpdaterModule vSPipelineChainUpdaterModule;

      Builder() {
      }

      public final DaggerVSCoreServerFactory.Builder vSCoreModule(VSCoreModule vSCoreModule) {
         this.vSCoreModule = ev.a(vSCoreModule);
         return this;
      }

      public final DaggerVSCoreServerFactory.Builder vSJacksonModule(GK vSJacksonModule) {
         this.vSJacksonModule = ev.a(vSJacksonModule);
         return this;
      }

      public final DaggerVSCoreServerFactory.Builder vSPipelineChainUpdaterModule(VSPipelineChainUpdaterModule vSPipelineChainUpdaterModule) {
         this.vSPipelineChainUpdaterModule = ev.a(vSPipelineChainUpdaterModule);
         return this;
      }

      public final VSCoreServerFactory build() {
         ev.a(this.vSCoreModule, VSCoreModule.class);
         if (this.vSJacksonModule == null) {
            this.vSJacksonModule = new GK();
         }

         if (this.vSPipelineChainUpdaterModule == null) {
            this.vSPipelineChainUpdaterModule = new VSPipelineChainUpdaterModule();
         }

         return new DaggerVSCoreServerFactory.VSCoreServerFactoryImpl(this.vSCoreModule, this.vSJacksonModule, this.vSPipelineChainUpdaterModule);
      }
   }

   static final class VSCoreServerFactoryImpl implements VSCoreServerFactory {
      final VSCoreModule vSCoreModule;
      final DaggerVSCoreServerFactory.VSCoreServerFactoryImpl vSCoreServerFactoryImpl = this;
      private ew<VsiCoreHooksOut> getHooksProvider;
      ew<EX> coreHooksImplProvider;
      ew<Fb> tcpProvider;
      ew<Fb> udpProvider;
      private ew<Fu> simplePacketNetworkingImplProvider;
      ew<SimplePacketNetworking> simplePacketNetworkingProvider;
      ew<Fe> packetsProvider;
      ew<Fl> vSNetworkingProvider;
      private ew<Fm> getConfiguratorProvider;
      private ew<FP.a> vSPipelineComponentFactoryProvider;
      private ew<ObjectMapper> dtoMapperProvider;
      private ew<ShipInertiaConverter> shipInertiaConverterProvider;
      private ew<ShipTransformConverterImpl> shipTransformConverterImplProvider;
      private ew<ObjectMapper> defaultMapperProvider;
      private ew<CN> jacksonAttachmentSerializerInternalProvider;
      private ew<CH> attachmentRegistryProvider;
      private ew<CA> attachmentHolderImplProvider;
      ew<Cy> attachmentHolderFactoryProvider;
      private ew<ServerShipDataConverterImpl> serverShipDataConverterImplProvider;
      private ew<ServerShipDataV1UpdaterImpl> serverShipDataV1UpdaterImplProvider;
      private ew<VSPipelineDataV1UpdaterImpl> vSPipelineDataV1UpdaterImplProvider;
      private ew<ServerShipDataV2UpdaterImpl> serverShipDataV2UpdaterImplProvider;
      private ew<VSPipelineDataV2UpdaterImpl> vSPipelineDataV2UpdaterImplProvider;
      private ew<ServerShipDataV3UpdaterImpl> serverShipDataV3UpdaterImplProvider;
      private ew<VSPipelineDataV3UpdaterImpl> vSPipelineDataV3UpdaterImplProvider;
      private ew<VSPipelineDataV4UpdaterImpl> vSPipelineDataV4UpdaterImplProvider;
      private ew<ChainUpdater<VSPipelineDataV5>> updaterProvider;
      private ew<ServerShipDataV0UpdaterImpl> serverShipDataV0UpdaterImplProvider;
      private ew<VSPipelineDataConverterImpl> vSPipelineDataConverterImplProvider;
      private ew<VSPipelineSerializer> vSPipelineSerializerProvider;
      private ew<SolidStateImpl.BuilderImpl> builderImplProvider;
      ew<DN> blockTypesImplProvider;
      ew<VSCoreImpl> vSCoreImplProvider;
      private ew<Do> solidShapeUtilsImplProvider;
      ew<VSCoreConfig.Server> serverConfigProvider;
      ew<FV> getPhysicsThreadCheckerProvider;

      VSCoreServerFactoryImpl(VSCoreModule vSCoreModuleParam, GK vSJacksonModuleParam, VSPipelineChainUpdaterModule vSPipelineChainUpdaterModuleParam) {
         this.vSCoreModule = vSCoreModuleParam;
         this.initialize(vSCoreModuleParam, vSJacksonModuleParam, vSPipelineChainUpdaterModuleParam);
         this.initialize2(vSCoreModuleParam, vSJacksonModuleParam, vSPipelineChainUpdaterModuleParam);
      }

      private void initialize(VSCoreModule vSCoreModuleParam, GK vSJacksonModuleParam, VSPipelineChainUpdaterModule vSPipelineChainUpdaterModuleParam) {
         this.getHooksProvider = VSCoreModule_GetHooksFactory.create(vSCoreModuleParam);
         this.coreHooksImplProvider = new ei<>();
         this.tcpProvider = ej.a(Fo.a(this.coreHooksImplProvider));
         ei.a(this.coreHooksImplProvider, EZ.a(this.getHooksProvider, this.tcpProvider));
         this.udpProvider = ej.a(Fp.a(this.coreHooksImplProvider));
         this.simplePacketNetworkingImplProvider = Fv.a(this.tcpProvider);
         this.simplePacketNetworkingProvider = ej.a(this.simplePacketNetworkingImplProvider);
         this.packetsProvider = ej.a(Ff.a(this.tcpProvider, this.udpProvider, this.simplePacketNetworkingProvider));
         this.vSNetworkingProvider = ej.a(Fn.a(this.udpProvider, this.tcpProvider, this.packetsProvider, this.simplePacketNetworkingProvider));
         this.getConfiguratorProvider = VSCoreModule_GetConfiguratorFactory.create(vSCoreModuleParam);
         this.vSPipelineComponentFactoryProvider = new ew<FP.a>() {
            public FP.a get() {
               return new DaggerVSCoreServerFactory.VSPipelineComponentFactory(VSCoreServerFactoryImpl.this.vSCoreServerFactoryImpl);
            }

            // $VF: synthetic method
            // $VF: bridge method
            @Override
            public Object get() {
               return this.get();
            }
         };
         this.dtoMapperProvider = GO.a(vSJacksonModuleParam);
         this.shipInertiaConverterProvider = eD.a(ShipInertiaConverter_Factory.create());
         this.shipTransformConverterImplProvider = eD.a(ShipTransformConverterImpl_Factory.create());
         this.defaultMapperProvider = GM.a(vSJacksonModuleParam);
         this.jacksonAttachmentSerializerInternalProvider = CO.a(this.defaultMapperProvider);
         this.attachmentRegistryProvider = ej.a(CJ.a(this.jacksonAttachmentSerializerInternalProvider));
         this.attachmentHolderImplProvider = CC.a(this.attachmentRegistryProvider, this.defaultMapperProvider);
         this.attachmentHolderFactoryProvider = eD.a(Cz.a(this.attachmentHolderImplProvider));
         this.serverShipDataConverterImplProvider = eD.a(
            ServerShipDataConverterImpl_Factory.create(
               this.shipInertiaConverterProvider, this.shipTransformConverterImplProvider, this.attachmentHolderFactoryProvider
            )
         );
         this.serverShipDataV1UpdaterImplProvider = eD.a(ServerShipDataV1UpdaterImpl_Factory.create());
         this.vSPipelineDataV1UpdaterImplProvider = eD.a(VSPipelineDataV1UpdaterImpl_Factory.create(this.serverShipDataV1UpdaterImplProvider));
         this.serverShipDataV2UpdaterImplProvider = eD.a(ServerShipDataV2UpdaterImpl_Factory.create());
         this.vSPipelineDataV2UpdaterImplProvider = eD.a(VSPipelineDataV2UpdaterImpl_Factory.create(this.serverShipDataV2UpdaterImplProvider));
         this.serverShipDataV3UpdaterImplProvider = eD.a(ServerShipDataV3UpdaterImpl_Factory.create());
      }

      private void initialize2(VSCoreModule vSCoreModuleParam, GK vSJacksonModuleParam, VSPipelineChainUpdaterModule vSPipelineChainUpdaterModuleParam) {
         this.vSPipelineDataV3UpdaterImplProvider = eD.a(VSPipelineDataV3UpdaterImpl_Factory.create(this.serverShipDataV3UpdaterImplProvider));
         this.vSPipelineDataV4UpdaterImplProvider = eD.a(VSPipelineDataV4UpdaterImpl_Factory.create());
         this.updaterProvider = VSPipelineChainUpdaterModule_UpdaterFactory.create(
            vSPipelineChainUpdaterModuleParam,
            this.vSPipelineDataV1UpdaterImplProvider,
            this.vSPipelineDataV2UpdaterImplProvider,
            this.vSPipelineDataV3UpdaterImplProvider,
            this.vSPipelineDataV4UpdaterImplProvider
         );
         this.serverShipDataV0UpdaterImplProvider = eD.a(ServerShipDataV0UpdaterImpl_Factory.create());
         this.vSPipelineDataConverterImplProvider = eD.a(VSPipelineDataConverterImpl_Factory.create(this.serverShipDataConverterImplProvider));
         this.vSPipelineSerializerProvider = VSPipelineSerializer_Factory.create(
            this.dtoMapperProvider,
            this.serverShipDataConverterImplProvider,
            this.updaterProvider,
            this.serverShipDataV0UpdaterImplProvider,
            this.vSPipelineDataConverterImplProvider
         );
         this.builderImplProvider = SolidStateImpl_BuilderImpl_Factory.create(BoxesBlockShapeImpl_BuilderImpl_Factory.create());
         this.blockTypesImplProvider = ej.a(
            DO.a(CQ.a(), LiquidStateImpl_BuilderImpl_Factory.create(), this.builderImplProvider, BoxesBlockShapeImpl_BuilderImpl_Factory.create())
         );
         this.vSCoreImplProvider = new ei<>();
         this.solidShapeUtilsImplProvider = eD.a(Dp.a(this.vSCoreImplProvider));
         ei.a(
            this.vSCoreImplProvider,
            ej.a(
               VSCoreImpl_Factory.create(
                  this.vSNetworkingProvider,
                  this.coreHooksImplProvider,
                  this.getConfiguratorProvider,
                  this.tcpProvider,
                  this.vSPipelineComponentFactoryProvider,
                  this.vSPipelineSerializerProvider,
                  this.blockTypesImplProvider,
                  this.simplePacketNetworkingProvider,
                  this.solidShapeUtilsImplProvider,
                  LiquidStateImpl_BuilderImpl_Factory.create(),
                  this.builderImplProvider,
                  BoxesBlockShapeImpl_BuilderImpl_Factory.create(),
                  this.attachmentRegistryProvider
               )
            )
         );
         this.serverConfigProvider = ej.a(VSCoreModule_ServerConfigFactory.create(vSCoreModuleParam));
         this.getPhysicsThreadCheckerProvider = VSCoreModule_GetPhysicsThreadCheckerFactory.create(vSCoreModuleParam);
      }

      @Override
      public final VSCoreServerImpl server() {
         return new VSCoreServerImpl(this.vSCoreImplProvider.get());
      }
   }

   static final class VSPipelineComponentFactory implements FP.a {
      private final DaggerVSCoreServerFactory.VSCoreServerFactoryImpl vSCoreServerFactoryImpl;

      VSPipelineComponentFactory(DaggerVSCoreServerFactory.VSCoreServerFactoryImpl vSCoreServerFactoryImpl) {
         this.vSCoreServerFactoryImpl = vSCoreServerFactoryImpl;
      }

      @Override
      public final FP newPipelineComponent(EI module) {
         ev.a(module);
         return new DaggerVSCoreServerFactory.VSPipelineComponentImpl(this.vSCoreServerFactoryImpl, module);
      }
   }

   static final class VSPipelineComponentImpl implements FP {
      private final DaggerVSCoreServerFactory.VSCoreServerFactoryImpl vSCoreServerFactoryImpl;
      private final DaggerVSCoreServerFactory.VSPipelineComponentImpl vSPipelineComponentImpl = this;
      private ew<VsiMutableQueryableShipData<ShipData>> getAllShipsProvider;
      private ew<Ek> getPersistedJointStoreProvider;
      private ew<ChunkAllocator> getChunkAllocatorProvider;
      private ew<DQ> singletonChunkAllocatorProviderImplProvider;
      private ew<Et> shipObjectServerWorldProvider;
      private ew<ER> shipObjectNetworkManagerServerProvider;
      private ew<Map<String, DP>> getDimensionInfoProvider;
      private ew<CY> shipObjectServerWorldChunkTrackerProvider;
      private ew<Fy> shipObjectServerWorldPhysEntityTrackerProvider;
      private ew<EB> shipLoadManagerServerProvider;
      private ew<Map<String, DP>> getMutableDimensionInfoProvider;
      private ew<DW> connectivityHandlerProvider;
      private ew<ShipData.Factory> factoryProvider;
      private ew<FH> vSGamePipelineStageProvider;
      private ew<FN> vSPhysicsPipelineStageProvider;
      private ew<FJ> vSNetworkPipelineStageProvider;
      private ew<FQ> vsiPipelineImplProvider;

      VSPipelineComponentImpl(DaggerVSCoreServerFactory.VSCoreServerFactoryImpl vSCoreServerFactoryImpl, EI shipWorldModuleParam) {
         this.vSCoreServerFactoryImpl = vSCoreServerFactoryImpl;
         this.initialize(shipWorldModuleParam);
      }

      private void initialize(EI shipWorldModuleParam) {
         this.getAllShipsProvider = ej.a(EJ.a(shipWorldModuleParam));
         this.getPersistedJointStoreProvider = ej.a(EN.a(shipWorldModuleParam));
         this.getChunkAllocatorProvider = ej.a(EK.a(shipWorldModuleParam));
         this.singletonChunkAllocatorProviderImplProvider = DR.a(this.getChunkAllocatorProvider);
         this.shipObjectServerWorldProvider = new ei<>();
         this.shipObjectNetworkManagerServerProvider = ES.a(
            this.shipObjectServerWorldProvider,
            this.vSCoreServerFactoryImpl.tcpProvider,
            this.vSCoreServerFactoryImpl.udpProvider,
            this.vSCoreServerFactoryImpl.simplePacketNetworkingProvider,
            this.vSCoreServerFactoryImpl.packetsProvider
         );
         this.getDimensionInfoProvider = ej.a(EL.a(shipWorldModuleParam));
         this.shipObjectServerWorldChunkTrackerProvider = CZ.a(this.vSCoreServerFactoryImpl.serverConfigProvider, this.getDimensionInfoProvider);
         this.shipObjectServerWorldPhysEntityTrackerProvider = Fz.a(this.vSCoreServerFactoryImpl.serverConfigProvider);
         this.shipLoadManagerServerProvider = EC.a(
            this.shipObjectNetworkManagerServerProvider,
            this.shipObjectServerWorldChunkTrackerProvider,
            this.singletonChunkAllocatorProviderImplProvider,
            this.shipObjectServerWorldPhysEntityTrackerProvider
         );
         this.getMutableDimensionInfoProvider = ej.a(EM.a(shipWorldModuleParam));
         this.connectivityHandlerProvider = DX.a(this.getDimensionInfoProvider, Ea.a(), this.singletonChunkAllocatorProviderImplProvider);
         this.factoryProvider = ShipData_Factory_Factory.create(this.vSCoreServerFactoryImpl.attachmentHolderFactoryProvider);
         ei.a(
            this.shipObjectServerWorldProvider,
            ej.a(
               Eu.a(
                  this.getAllShipsProvider,
                  this.getPersistedJointStoreProvider,
                  this.singletonChunkAllocatorProviderImplProvider,
                  this.shipLoadManagerServerProvider,
                  this.vSCoreServerFactoryImpl.vSNetworkingProvider,
                  this.vSCoreServerFactoryImpl.blockTypesImplProvider,
                  this.getMutableDimensionInfoProvider,
                  this.connectivityHandlerProvider,
                  this.factoryProvider,
                  this.vSCoreServerFactoryImpl.vSCoreImplProvider
               )
            )
         );
         this.vSGamePipelineStageProvider = FI.a(this.shipObjectServerWorldProvider, this.vSCoreServerFactoryImpl.vSCoreImplProvider);
         this.vSPhysicsPipelineStageProvider = FO.a(
            this.vSCoreServerFactoryImpl.blockTypesImplProvider,
            this.vSCoreServerFactoryImpl.getPhysicsThreadCheckerProvider,
            this.shipObjectServerWorldProvider,
            this.vSCoreServerFactoryImpl.vSCoreImplProvider
         );
         this.vSNetworkPipelineStageProvider = FK.a(
            this.shipObjectServerWorldProvider, this.vSCoreServerFactoryImpl.vSNetworkingProvider, this.vSCoreServerFactoryImpl.packetsProvider
         );
         this.vsiPipelineImplProvider = ej.a(
            FR.a(
               this.shipObjectServerWorldProvider,
               this.vSGamePipelineStageProvider,
               this.vSPhysicsPipelineStageProvider,
               this.vSNetworkPipelineStageProvider,
               this.vSCoreServerFactoryImpl.coreHooksImplProvider
            )
         );
      }

      @Override
      public final FQ newPipeline() {
         return this.vsiPipelineImplProvider.get();
      }

      @Override
      public final FN newPhysicsStage() {
         return new FN(
            this.vSCoreServerFactoryImpl.blockTypesImplProvider.get(),
            VSCoreModule_GetPhysicsThreadCheckerFactory.getPhysicsThreadChecker(this.vSCoreServerFactoryImpl.vSCoreModule),
            this.shipObjectServerWorldProvider.get(),
            this.vSCoreServerFactoryImpl.vSCoreImplProvider
         );
      }
   }
}
