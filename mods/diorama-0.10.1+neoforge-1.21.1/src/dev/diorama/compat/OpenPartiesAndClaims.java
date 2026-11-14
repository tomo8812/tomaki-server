package dev.diorama.compat;

import dev.diorama.Claims;
import dev.diorama.Diorama;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Stream;
import net.minecraft.client.Minecraft;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.neoforged.fml.ModList;
import org.jspecify.annotations.Nullable;

public final class OpenPartiesAndClaims {
   private static final String API = "xaero.pac.";
   private static final UUID SERVER_CLAIM = new UUID(0L, 0L);
   private static final UUID EXPIRED_CLAIM = new UUID(0L, 1L);
   private static final int REGION_SIZE = 32;
   private static boolean started;
   private static boolean failed;
   private static Object manager;
   private static Method claimAt;
   private static Method dimensionOf;
   private static Method dimensionStream;
   private static Method dimensionId;
   private static Method regionOf;
   private static Method regionStream;
   private static Method regionX;
   private static Method regionZ;
   private static Method regionClaim;
   private static Method claimPlayer;
   private static Method claimSub;
   private static Method claimForceloadable;
   private static @Nullable Method fullName;
   private static @Nullable Method colour;
   private static @Nullable Method moderatorMode;
   private static Method playerInfo;
   private static Method username;
   private static Method claimsName;
   private static Method subClaimsName;
   private static Method claimsColour;
   private static Method subClaimsColour;
   private static boolean readAll = true;
   private static final Set<Object> dimensionsChanged = new LinkedHashSet<>();
   private static final Set<OpenPartiesAndClaims.Area> regionsChanged = new LinkedHashSet<>();
   private static final List<OpenPartiesAndClaims.Area> chunksChanged = new ArrayList<>();

   private OpenPartiesAndClaims() {
   }

   public static boolean installed() {
      return ModList.get().isLoaded("openpartiesandclaims");
   }

   public static boolean available() {
      if (!started) {
         started = true;
         if (installed()) {
            try {
               lookUp();
               Diorama.LOGGER.info("Open Parties and Claims is installed: showing its claims on the map");
            } catch (ReflectiveOperationException | RuntimeException | LinkageError e) {
               fail(e);
            }
         } else {
            failed = true;
         }
      }

      return !failed;
   }

   private static void lookUp() throws ReflectiveOperationException {
      Class<?> clientApi = Class.forName("xaero.pac.client.api.OpenPACClientAPI");
      Object api = clientApi.getMethod("get").invoke(null);
      manager = Objects.requireNonNull(clientApi.getMethod("getClaimsManager").invoke(api), "claims manager");
      Class<?> claims = Class.forName("xaero.pac.client.claims.api.IClientClaimsManagerAPI");
      Class<?> claim = Class.forName("xaero.pac.common.claims.player.api.IPlayerChunkClaimAPI");
      claimAt = claims.getMethod("get", ResourceLocation.class, int.class, int.class);
      dimensionOf = claims.getMethod("getDimension", ResourceLocation.class);
      dimensionStream = claims.getMethod("getDimensionStream");
      Class<?> dimension = Class.forName("xaero.pac.client.claims.api.IClientDimensionClaimsManagerAPI");
      dimensionId = dimension.getMethod("getDimension");
      regionOf = dimension.getMethod("getRegion", int.class, int.class);
      regionStream = dimension.getMethod("getRegionStream");
      Class<?> region = Class.forName("xaero.pac.client.claims.api.IClientRegionClaimsAPI");
      regionX = region.getMethod("getX");
      regionZ = region.getMethod("getZ");
      regionClaim = region.getMethod("get", int.class, int.class);
      claimPlayer = claim.getMethod("getPlayerId");
      claimSub = claim.getMethod("getSubConfigIndex");
      claimForceloadable = claim.getMethod("isForceloadable");

      try {
         fullName = claims.getMethod("getFullName", claim, ResourceLocation.class, boolean.class);
         colour = claims.getMethod("getColor", claim, ResourceLocation.class);
         moderatorMode = claims.getMethod("isModeratorMode");
      } catch (NoSuchMethodException e) {
         fullName = null;
         colour = null;
         moderatorMode = null;
      }

      playerInfo = claims.getMethod("getPlayerInfo", UUID.class);
      Class<?> info = Class.forName("xaero.pac.client.claims.player.api.IClientPlayerClaimInfoAPI");
      username = info.getMethod("getPlayerUsername");
      claimsName = info.getMethod("getClaimsName");
      subClaimsName = info.getMethod("getClaimsName", int.class);
      claimsColour = info.getMethod("getClaimsColor");
      subClaimsColour = info.getMethod("getClaimsColor", int.class);
      Class<?> listener = Class.forName("xaero.pac.common.claims.tracker.api.IClaimsManagerListenerAPI");
      Object tracker = claims.getMethod("getTracker").invoke(manager);
      Object proxy = Proxy.newProxyInstance(listener.getClassLoader(), new Class[]{listener}, new OpenPartiesAndClaims.Listener());
      Class.forName("xaero.pac.common.claims.tracker.api.IClaimsManagerTrackerAPI").getMethod("register", listener).invoke(tracker, proxy);
   }

   private static void changed(String what, Object[] args) {
      if (!readAll) {
         switch (what) {
            case "onChunkChange":
               chunksChanged.add(new OpenPartiesAndClaims.Area(args[0], (Integer)args[1], (Integer)args[2]));
               break;
            case "onWholeRegionChange":
               regionsChanged.add(new OpenPartiesAndClaims.Area(args[0], (Integer)args[1], (Integer)args[2]));
               break;
            default:
               dimensionsChanged.add(args[0]);
         }
      }
   }

   public static void cleared() {
      readAll = true;
      dimensionsChanged.clear();
      regionsChanged.clear();
      chunksChanged.clear();
   }

   public static void tick() {
      if (available()) {
         try {
            if (readAll) {
               readAll = false;

               try (Stream<?> dimensions = (Stream<?>)dimensionStream.invoke(manager)) {
                  Iterator<?> it = dimensions.iterator();

                  while (it.hasNext()) {
                     Object dimension = it.next();
                     readDimension(dimensionId.invoke(dimension), dimension);
                  }
               }
            }

            for (Object id : dimensionsChanged) {
               readDimension(id, dimensionOf.invoke(manager, id));
            }

            for (OpenPartiesAndClaims.Area region : regionsChanged) {
               if (!dimensionsChanged.contains(region.dimension())) {
                  Object dimension = dimensionOf.invoke(manager, region.dimension());
                  readRegion(region.dimension(), region.x(), region.z(), dimension == null ? null : regionOf.invoke(dimension, region.x(), region.z()), false);
               }
            }

            for (OpenPartiesAndClaims.Area chunk : chunksChanged) {
               if (!dimensionsChanged.contains(chunk.dimension())
                  && !regionsChanged.contains(new OpenPartiesAndClaims.Area(chunk.dimension(), chunk.x() >> 5, chunk.z() >> 5))) {
                  Object state = claimAt.invoke(manager, chunk.dimension(), chunk.x(), chunk.z());
                  Claims.updated(key(chunk.dimension()), chunk.x(), chunk.z(), OpenPartiesAndClaims.Owner.class, owner(state, chunk.dimension()), false);
               }
            }
         } catch (InvocationTargetException e) {
            Diorama.LOGGER.debug("Open Parties and Claims couldn't give its claims", e);
            readAll = true;
         } catch (ReflectiveOperationException | RuntimeException | LinkageError e) {
            fail(e);
         } finally {
            dimensionsChanged.clear();
            regionsChanged.clear();
            chunksChanged.clear();
         }
      }
   }

   private static void readDimension(Object id, @Nullable Object dimension) throws ReflectiveOperationException {
      Claims.forget(key(id), OpenPartiesAndClaims.Owner.class);
      if (dimension != null) {
         try (Stream<?> regions = (Stream<?>)regionStream.invoke(dimension)) {
            Iterator<?> it = regions.iterator();

            while (it.hasNext()) {
               Object region = it.next();
               readRegion(id, (Integer)regionX.invoke(region), (Integer)regionZ.invoke(region), region, true);
            }
         }
      }
   }

   private static void readRegion(Object id, int x, int z, @Nullable Object region, boolean forgotten) throws ReflectiveOperationException {
      ResourceKey<Level> key = key(id);
      Map<Object, OpenPartiesAndClaims.Owner> owners = new IdentityHashMap<>();

      for (int i = 0; i < 32; i++) {
         for (int j = 0; j < 32; j++) {
            Object state = region == null ? null : regionClaim.invoke(region, i, j);
            if (state != null || !forgotten) {
               OpenPartiesAndClaims.Owner owner = null;
               if (state != null) {
                  owner = owners.get(state);
                  if (owner == null) {
                     owner = owner(state, id);
                     owners.put(state, owner);
                  }
               }

               Claims.updated(key, x << 5 | i, z << 5 | j, OpenPartiesAndClaims.Owner.class, owner, false);
            }
         }
      }
   }

   private static ResourceKey<Level> key(Object id) {
      return ResourceKey.create(Registries.DIMENSION, (ResourceLocation)id);
   }

   private static OpenPartiesAndClaims.@Nullable Owner owner(@Nullable Object state, Object dimension) throws ReflectiveOperationException {
      return state == null
         ? null
         : new OpenPartiesAndClaims.Owner(
            (UUID)claimPlayer.invoke(state), (Integer)claimSub.invoke(state), (Boolean)claimForceloadable.invoke(state), dimension, state
         );
   }

   private static void fail(Throwable e) {
      if (!failed) {
         failed = true;
         Diorama.LOGGER.warn("Couldn't read Open Parties and Claims' claims; they won't show on the map", e);
      }
   }

   private record Area(Object dimension, int x, int z) {
   }

   private static final class Listener implements InvocationHandler {
      @Override
      public Object invoke(Object proxy, Method method, Object[] args) {
         switch (method.getName()) {
            case "onChunkChange":
            case "onWholeRegionChange":
            case "onDimensionChange":
               Minecraft mc = Minecraft.getInstance();
               if (mc.isSameThread()) {
                  OpenPartiesAndClaims.changed(method.getName(), args);
               } else {
                  mc.execute(() -> OpenPartiesAndClaims.changed(method.getName(), args));
               }

               return null;
            case "equals":
               return proxy == args[0];
            case "hashCode":
               return System.identityHashCode(proxy);
            case "toString":
               return "Diorama's claims listener";
            default:
               return null;
         }
      }
   }

   private static final class Owner implements Claims.Owner {
      private final UUID player;
      private final int sub;
      private final boolean forceloadable;
      private final Object dimension;
      private final Object state;

      Owner(UUID player, int sub, boolean forceloadable, Object dimension, Object state) {
         this.player = player;
         this.sub = sub;
         this.forceloadable = forceloadable;
         this.dimension = dimension;
         this.state = state;
      }

      @Override
      public @Nullable Optional<Claims.Claim> look() {
         if (!OpenPartiesAndClaims.available()) {
            return null;
         }

         try {
            String title;
            int rgb;
            if (OpenPartiesAndClaims.fullName != null) {
               boolean parties = !(Boolean)OpenPartiesAndClaims.moderatorMode.invoke(OpenPartiesAndClaims.manager);
               title = ((Component)OpenPartiesAndClaims.fullName.invoke(OpenPartiesAndClaims.manager, this.state, this.dimension, parties)).getString();
               rgb = (Integer)OpenPartiesAndClaims.colour.invoke(OpenPartiesAndClaims.manager, this.state, this.dimension);
            } else {
               Object info = OpenPartiesAndClaims.playerInfo.invoke(OpenPartiesAndClaims.manager, this.player);
               title = this.oldTitle(info);
               Integer own = (Integer)OpenPartiesAndClaims.subClaimsColour.invoke(info, this.sub);
               rgb = own != null ? own : (Integer)OpenPartiesAndClaims.claimsColour.invoke(info);
            }

            return Optional.of(new Claims.Claim(this, rgb & 16777215, title, this.forceloadable, true));
         } catch (InvocationTargetException e) {
            Diorama.LOGGER.debug("Open Parties and Claims couldn't give a claim's name", e);
            return null;
         } catch (ReflectiveOperationException | RuntimeException | LinkageError e) {
            OpenPartiesAndClaims.fail(e);
            return null;
         }
      }

      private String oldTitle(Object info) throws ReflectiveOperationException {
         String custom = (String)OpenPartiesAndClaims.subClaimsName.invoke(info, this.sub);
         if (this.sub != -1 && (custom == null || custom.isEmpty())) {
            custom = (String)OpenPartiesAndClaims.claimsName.invoke(info);
         }

         Component forceloaded = this.forceloadable ? Component.translatable("gui.xaero_pac_marked_for_forceload") : Component.literal("");
         Component owner = this.player.equals(OpenPartiesAndClaims.SERVER_CLAIM)
            ? Component.translatable("gui.xaero_pac_title_entered_server_claim", new Object[]{forceloaded})
            : (
               this.player.equals(OpenPartiesAndClaims.EXPIRED_CLAIM)
                  ? Component.translatable("gui.xaero_pac_title_entered_expired_claim", new Object[]{forceloaded})
                  : Component.translatable("gui.xaero_pac_title_entered_claim", new Object[]{(String)OpenPartiesAndClaims.username.invoke(info), forceloaded})
            );
         return custom != null && !custom.isEmpty() ? custom + " - " + owner.getString() : owner.getString();
      }

      @Override
      public boolean equals(Object o) {
         return o instanceof OpenPartiesAndClaims.Owner other
            && other.player.equals(this.player)
            && other.sub == this.sub
            && other.forceloadable == this.forceloadable
            && other.dimension.equals(this.dimension);
      }

      @Override
      public int hashCode() {
         return Objects.hash(this.player, this.sub, this.forceloadable, this.dimension);
      }

      @Override
      public String toString() {
         return "Open Parties and Claims claim of " + this.player + (this.sub == -1 ? "" : " #" + this.sub) + (this.forceloadable ? ", forceloadable" : "");
      }
   }
}
