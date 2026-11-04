package de.maxhenkel.gravestone.corelib;

import de.maxhenkel.gravestone.corelib.config.ConfigBase;
import de.maxhenkel.gravestone.corelib.config.DynamicConfig;
import de.maxhenkel.gravestone.corelib.net.Message;
import java.nio.file.Path;
import java.util.function.Consumer;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.PacketFlow;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.EntityType.Builder;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.LevelResource;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.ModList;
import net.neoforged.fml.ModLoadingContext;
import net.neoforged.fml.config.ModConfig.Type;
import net.neoforged.fml.event.config.ModConfigEvent.Loading;
import net.neoforged.fml.event.config.ModConfigEvent.Reloading;
import net.neoforged.fml.loading.FMLConfig;
import net.neoforged.fml.loading.FMLPaths;
import net.neoforged.neoforge.common.ModConfigSpec;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.server.ServerAboutToStartEvent;
import net.neoforged.neoforge.network.handling.IPayloadHandler;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

public class CommonRegistry {
   private static final LevelResource SERVERCONFIG = new LevelResource("serverconfig");
   private static final Path DEFAULT_CONFIG_PATH = FMLPaths.GAMEDIR.get().resolve(FMLConfig.defaultConfigPath());

   public static <T extends Message<T>> void registerMessage(PayloadRegistrar registrar, final Class<T> message) {
      try {
         T dummy = (T)message.getDeclaredConstructor().newInstance();
         StreamCodec<RegistryFriendlyByteBuf, T> codec = new StreamCodec<RegistryFriendlyByteBuf, T>() {
            public void encode(RegistryFriendlyByteBuf buf, T packet) {
               packet.toBytes(buf);
            }

            public T decode(RegistryFriendlyByteBuf buf) {
               try {
                  T packet = message.getDeclaredConstructor().newInstance();
                  packet.fromBytes(buf);
                  return packet;
               } catch (Exception e) {
                  throw new RuntimeException(e);
               }
            }

            // $VF: synthetic method
            // $VF: bridge method
            public Object decode(Object param1) {
               return this.decode((RegistryFriendlyByteBuf)var1);
            }

            // $VF: synthetic method
            // $VF: bridge method
            public void encode(Object param1, Object nullx) {
               this.encode((RegistryFriendlyByteBuf)var1, (T)nullx);
            }
         };
         IPayloadHandler<T> handler = (payload, context) -> {
            if (payload.getExecutingSide().equals(context.flow())) {
               if (PacketFlow.CLIENTBOUND.equals(context.flow())) {
                  context.enqueueWork(() -> payload.executeClientSide(context));
               } else {
                  context.enqueueWork(() -> payload.executeServerSide(context));
               }
            }
         };
         if (dummy.getExecutingSide().equals(PacketFlow.CLIENTBOUND)) {
            registrar.playToClient(dummy.type(), codec, handler);
         } else {
            if (!dummy.getExecutingSide().equals(PacketFlow.SERVERBOUND)) {
               throw new RuntimeException("Unknown packet flow: %s".formatted(dummy.getExecutingSide()));
            }

            registrar.playToServer(dummy.type(), codec, handler);
         }
      } catch (Exception e) {
         throw new RuntimeException(e);
      }
   }

   public static <T extends Entity> EntityType<T> registerEntity(
      String modId, String name, MobCategory classification, Class<? extends Entity> entityClass, Consumer<Builder<T>> builderConsumer
   ) {
      Builder<T> builder = Builder.of((type, world) -> {
         try {
            try {
               return entityClass.getConstructor(EntityType.class, Level.class).newInstance(type, world);
            } catch (NoSuchMethodException e) {
               return entityClass.getConstructor(Level.class).newInstance(world);
            }
         } catch (Throwable e) {
            throw new RuntimeException(e);
         }
      }, classification);
      builderConsumer.accept(builder);
      return builder.build(modId + ":" + name);
   }

   public static <T extends ConfigBase> T registerConfig(String modId, Type type, Class<T> configClass, boolean registerListener) {
      net.neoforged.neoforge.common.ModConfigSpec.Builder builder = new net.neoforged.neoforge.common.ModConfigSpec.Builder();

      T config;
      try {
         config = (T)configClass.getConstructor(net.neoforged.neoforge.common.ModConfigSpec.Builder.class).newInstance(builder);
      } catch (Throwable e) {
         throw new RuntimeException(e);
      }

      ModConfigSpec spec = builder.build();
      ModContainer modContainer = (ModContainer)ModList.get()
         .getModContainerById(modId)
         .orElseThrow(() -> new RuntimeException("Could not find mod %s".formatted(modId)));
      modContainer.registerConfig(type, spec);
      config.setConfigSpec(spec);
      if (registerListener) {
         Consumer<Loading> load = evt -> {
            if (evt.getConfig().getType() == type) {
               config.onLoad(evt);
            }
         };
         ModLoadingContext.get().getActiveContainer().getEventBus().addListener(load);
         Consumer<Reloading> reload = evt -> {
            if (evt.getConfig().getType() == type) {
               config.onReload(evt);
            }
         };
         ModLoadingContext.get().getActiveContainer().getEventBus().addListener(reload);
      }

      return config;
   }

   public static <T extends ConfigBase> T registerConfig(String modId, Type type, Class<T> configClass) {
      return registerConfig(modId, type, configClass, false);
   }

   public static <T extends DynamicConfig> T registerDynamicConfig(
      DynamicConfig.DynamicConfigType type, String folderName, String configName, Class<T> configClass
   ) {
      try {
         T config = (T)configClass.getDeclaredConstructor().newInstance();
         String configFileName = configName + ".toml";
         if (type.equals(DynamicConfig.DynamicConfigType.SERVER)) {
            Consumer<ServerAboutToStartEvent> consumer = event -> {
               Path serverConfig = event.getServer().getWorldPath(SERVERCONFIG).resolve(folderName);
               serverConfig.toFile().mkdirs();
               Path configPathx = serverConfig.resolve(configFileName);
               Path defaultPath = DEFAULT_CONFIG_PATH.resolve(folderName).resolve(configFileName);
               config.init(configPathx, defaultPath);
            };
            NeoForge.EVENT_BUS.addListener(consumer);
         } else {
            Path commonConfig = FMLPaths.CONFIGDIR.get().resolve(folderName);
            commonConfig.toFile().mkdirs();
            Path configPath = commonConfig.resolve(configFileName);
            config.init(configPath);
         }

         return config;
      } catch (Exception e) {
         throw new RuntimeException(e);
      }
   }
}
