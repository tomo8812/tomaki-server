package dev.diorama.waypoint;

import dev.diorama.Diorama;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.neoforged.fml.ModList;
import org.jspecify.annotations.Nullable;

public final class Waystones {
   private static final String API = "net.blay09.mods.waystones.api.";
   private static final String WAYSTONE = "waystones:waystone";
   private static final int COLOUR = -7450938;
   private static final Map<String, Integer> DYES = Map.ofEntries(
      Map.entry("white", -393218),
      Map.entry("orange", -425955),
      Map.entry("magenta", -3715395),
      Map.entry("light_blue", -12930086),
      Map.entry("yellow", -75715),
      Map.entry("lime", -8337633),
      Map.entry("pink", -816214),
      Map.entry("gray", -12103854),
      Map.entry("light_gray", -6447721),
      Map.entry("cyan", -15295332),
      Map.entry("purple", -7785800),
      Map.entry("blue", -12827478),
      Map.entry("brown", -8170446),
      Map.entry("green", -10585066),
      Map.entry("red", -5231066),
      Map.entry("black", -14869215)
   );
   private static final long CHECK_MS = 1000L;
   private static final Map<String, Map<UUID, Waypoint>> KNOWN = new LinkedHashMap<>();
   private static final Map<UUID, String> PICKED_UP = new HashMap<>();
   private static int changes;
   private static final Map<String, Optional<Method>> METHODS = new ConcurrentHashMap<>();
   private static boolean started;
   private static volatile boolean failed;
   private static @Nullable Method activated;
   private static long nextCheck;

   private Waystones() {
   }

   public static void start() {
      if (!started) {
         started = true;
         if (installed()) {
            try {
               listen("WaystonesListReceivedEvent", Waystones::listReceived);
               listen("WaystoneUpdateReceivedEvent", Waystones::updateReceived);
               listen("WaystoneRemoveReceivedEvent", Waystones::removeReceived);
               Class<?> api = Class.forName("net.blay09.mods.waystones.api.WaystonesAPI");

               for (Method method : api.getMethods()) {
                  if (method.getName().equals("getActivatedWaystones") && method.getParameterCount() == 1 && Modifier.isStatic(method.getModifiers())) {
                     activated = method;
                  }
               }

               Diorama.LOGGER.info("Waystones is installed: showing known waystones on the map");
            } catch (ReflectiveOperationException | RuntimeException | LinkageError e) {
               fail(e);
            }
         }
      }
   }

   private static boolean installed() {
      return ModList.get().isLoaded("waystones");
   }

   private static void listen(String name, Waystones.Handler handler) throws ReflectiveOperationException {
      Class<?> event = Class.forName("net.blay09.mods.waystones.api.event." + name);
      Consumer<Object> guarded = e -> {
         if (!failed) {
            try {
               handler.handle(e);
            } catch (ReflectiveOperationException | RuntimeException | LinkageError ex) {
               fail(ex);
            }
         }
      };
      Field field = null;

      try {
         field = event.getField("EVENT");
      } catch (NoSuchFieldException var7) {
      }

      if (field != null) {
         Object mapper = field.get(null);
         one(field.getType(), "register", 1).invoke(mapper, guarded);
      } else {
         Method getEvents = Class.forName("net.blay09.mods.balm.api.Balm").getMethod("getEvents");
         Object events = getEvents.invoke(null);
         getEvents.getReturnType().getMethod("onEvent", Class.class, Consumer.class).invoke(events, event, guarded);
      }
   }

   private static Method one(Class<?> type, String name, int parameters) throws NoSuchMethodException {
      for (Method method : type.getMethods()) {
         if (method.getName().equals(name) && method.getParameterCount() == parameters) {
            return method;
         }
      }

      throw new NoSuchMethodException(type.getName() + "." + name);
   }

   private static void listReceived(Object event) throws ReflectiveOperationException {
      String kind = String.valueOf(get(event, "getWaystoneType", "waystoneType"));
      if (shown(kind)) {
         List<Waypoint> waypoints = new ArrayList<>();

         for (Object waystone : (Collection)get(event, "getWaystones", "waystones")) {
            Waypoint waypoint = waypoint(waystone);
            if (waypoint != null) {
               waypoints.add(waypoint);
            }
         }

         onClient(() -> {
            Map<UUID, Waypoint> waystones = new LinkedHashMap<>();

            for (Waypoint waypointx : waypoints) {
               waystones.put(waypointx.id, waypointx);
            }

            KNOWN.put(kind, waystones);
            changes++;
         });
      }
   }

   private static void updateReceived(Object event) throws ReflectiveOperationException {
      Object waystone = get(event, "getWaystone", "waystone");
      UUID id = (UUID)get(waystone, "getWaystoneUid");
      String kind = kind(waystone);
      Waypoint waypoint = waypoint(waystone);
      onClient(() -> {
         forget(id);
         if (waypoint != null) {
            KNOWN.computeIfAbsent(kind, k -> new LinkedHashMap<>()).put(id, waypoint);
         }

         changes++;
      });
   }

   private static void removeReceived(Object event) throws ReflectiveOperationException {
      UUID id = (UUID)get(event, "getWaystoneId", "waystoneId");
      boolean destroyed = (Boolean)get(event, "wasDestroyed");
      onClient(() -> {
         if (!destroyed) {
            for (Map<UUID, Waypoint> waystones : KNOWN.values()) {
               Waypoint waypoint = waystones.get(id);
               if (waypoint != null) {
                  PICKED_UP.put(id, spot(waypoint));
               }
            }
         }

         forget(id);
         changes++;
      });
   }

   private static boolean placed(Waypoint waypoint) {
      String spot = PICKED_UP.get(waypoint.id);
      if (spot == null) {
         return true;
      }

      if (spot.equals(spot(waypoint)) && !blockThere(waypoint)) {
         return false;
      }

      PICKED_UP.remove(waypoint.id);
      return true;
   }

   private static boolean blockThere(Waypoint waypoint) {
      Minecraft mc = Minecraft.getInstance();
      BlockPos pos = new BlockPos(waypoint.x, waypoint.y, waypoint.z);
      return mc.level != null && mc.level.dimension().location().toString().equals(waypoint.dimension) && mc.level.hasChunkAt(pos)
         ? BuiltInRegistries.BLOCK.getKey(mc.level.getBlockState(pos).getBlock()).getNamespace().equals("waystones")
         : false;
   }

   private static String spot(Waypoint waypoint) {
      return waypoint.dimension + " " + waypoint.x + " " + waypoint.y + " " + waypoint.z;
   }

   private static void onClient(Runnable task) {
      Minecraft mc = Minecraft.getInstance();
      if (mc.isSameThread()) {
         task.run();
      } else {
         mc.execute(task);
      }
   }

   private static void forget(UUID id) {
      for (Map<UUID, Waypoint> waystones : KNOWN.values()) {
         waystones.remove(id);
      }
   }

   private static boolean shown(String kind) {
      return kind.equals("waystones:waystone") || kind.endsWith("_sharestone");
   }

   private static String kind(Object waystone) throws ReflectiveOperationException {
      return String.valueOf(get(waystone, "getWaystoneType", "getWaystoneKind"));
   }

   private static @Nullable Waypoint waypoint(Object waystone) throws ReflectiveOperationException {
      String kind = kind(waystone);
      if (shown(kind) && !(Boolean)get(waystone, "isTransient")) {
         Component name = (Component)(get(waystone, "hasName")
            ? (Component)get(waystone, "getEffectiveName", "getName")
            : Component.translatable("diorama.waystones.untitled"));
         BlockPos pos = (BlockPos)get(waystone, "getPos");
         ResourceKey<?> dimension = (ResourceKey<?>)get(waystone, "getDimension");
         String dye = kind.substring(kind.indexOf(58) + 1, kind.length() - (kind.endsWith("_sharestone") ? "_sharestone".length() : 0));
         Waypoint waypoint = new Waypoint(
            name.getString(), pos.getX(), pos.getY(), pos.getZ(), dimension.location().toString(), DYES.getOrDefault(dye, -7450938)
         );
         waypoint.id = (UUID)get(waystone, "getWaystoneUid");
         waypoint.kind = Waypoint.Kind.WAYSTONE;
         return waypoint;
      } else {
         return null;
      }
   }

   private static Object get(Object target, String... names) throws ReflectiveOperationException {
      Class<?> type = target.getClass();

      for (String name : names) {
         Optional<Method> method = METHODS.computeIfAbsent(type.getName() + "." + name, key -> Optional.ofNullable(publicMethod(type, name)));
         if (method.isPresent()) {
            return method.get().invoke(target);
         }
      }

      throw new NoSuchMethodException(type.getName() + "." + String.join("/", names));
   }

   private static @Nullable Method publicMethod(Class<?> type, String name) {
      Set<Class<?>> seen = new HashSet<>();
      List<Class<?>> queue = new ArrayList<>(List.of(type));

      while (!queue.isEmpty()) {
         Class<?> next = queue.remove(0);
         if (seen.add(next)) {
            if (Modifier.isPublic(next.getModifiers())) {
               try {
                  return next.getMethod(name);
               } catch (NoSuchMethodException var6) {
               }
            }

            if (next.getSuperclass() != null) {
               queue.add(next.getSuperclass());
            }

            queue.addAll(List.of(next.getInterfaces()));
         }
      }

      return null;
   }

   private static void fail(Throwable e) {
      if (!failed) {
         failed = true;
         onClient(Waystones::clear);
         Diorama.LOGGER.warn("Couldn't read Waystones' waystones, so they won't show on the map", e);
      }
   }

   static List<Waypoint> in(String dimension) {
      check();
      List<Waypoint> out = new ArrayList<>();

      for (Map<UUID, Waypoint> waystones : KNOWN.values()) {
         for (Waypoint waypoint : waystones.values()) {
            if (waypoint.dimension.equals(dimension) && placed(waypoint)) {
               out.add(waypoint);
            }
         }
      }

      return out;
   }

   static List<Waypoint> all() {
      check();
      List<Waypoint> out = new ArrayList<>();

      for (Map<UUID, Waypoint> waystones : KNOWN.values()) {
         for (Waypoint waypoint : waystones.values()) {
            if (placed(waypoint)) {
               out.add(waypoint);
            }
         }
      }

      return out;
   }

   private static void check() {
      Map<UUID, Waypoint> ordinary = KNOWN.get("waystones:waystone");
      Method method = activated;
      long now = System.currentTimeMillis();
      if (!failed && method != null && ordinary != null && !ordinary.isEmpty() && now >= nextCheck) {
         nextCheck = now + 1000L;
         Minecraft mc = Minecraft.getInstance();
         if (mc.player != null) {
            try {
               Set<UUID> ids = new HashSet<>();

               for (Object waystone : (Collection)method.invoke(null, mc.player)) {
                  ids.add((UUID)get(waystone, "getWaystoneUid"));
               }

               if (ordinary.keySet().retainAll(ids)) {
                  changes++;
               }
            } catch (ReflectiveOperationException | RuntimeException | LinkageError e) {
               fail(e);
            }
         }
      }
   }

   static int changes() {
      check();
      return changes;
   }

   public static void clear() {
      KNOWN.clear();
      changes++;
   }

   private interface Handler {
      void handle(Object var1) throws ReflectiveOperationException;
   }
}
