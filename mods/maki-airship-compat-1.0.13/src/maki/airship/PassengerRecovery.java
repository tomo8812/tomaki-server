package maki.airship;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Map.Entry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.protocol.game.ClientboundSetPassengersPacket;
import net.minecraft.world.entity.Entity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.valkyrienskies.mod.common.VSGameUtilsKt;
import org.valkyrienskies.mod.common.entity.ShipMountingEntity;

public final class PassengerRecovery {
   private static final Logger LOG = LoggerFactory.getLogger("MakiPassengerSync");
   private static final Map<Integer, PassengerRecovery.Pending> PENDING = new LinkedHashMap<>();
   private static final Map<Integer, Long> ASSIGNMENTS = new LinkedHashMap<>();
   private static ClientPacketListener owner;
   private static ClientLevel world;
   private static int ticks;
   private static long revision;
   private static boolean replaying;

   private PassengerRecovery() {
   }

   private static boolean ready(ClientLevel level, ClientboundSetPassengersPacket packet) {
      Entity vehicle = level.getEntity(packet.getVehicle());
      if (vehicle == null) {
         return false;
      }

      for (int passenger : packet.getPassengers()) {
         if (level.getEntity(passenger) == null) {
            return false;
         }
      }

      if (packet.getPassengers().length > 0) {
         if (VSGameUtilsKt.isBlockInShipyard(level, vehicle.position()) && VSGameUtilsKt.getShipManaging(vehicle) == null) {
            return false;
         }

         if (vehicle instanceof ShipMountingEntity mounting && mounting.provideShipMountedToData(level.getEntity(packet.getPassengers()[0]), 0.0F) == null) {
            return false;
         }
      }

      return true;
   }

   private static void reset(ClientPacketListener connection) {
      PENDING.clear();
      ASSIGNMENTS.clear();
      owner = connection;
      world = Minecraft.getInstance().level;
      ticks = 0;
      revision = 0L;
   }

   public static boolean defer(ClientPacketListener connection, ClientboundSetPassengersPacket packet) {
      Minecraft minecraft = Minecraft.getInstance();
      if (minecraft.level == null) {
         return false;
      }

      if (owner != connection || world != minecraft.level) {
         reset(connection);
      }

      if (replaying) {
         return false;
      }

      long currentRevision = ++revision;

      for (int passenger : packet.getPassengers()) {
         ASSIGNMENTS.remove(passenger);
         ASSIGNMENTS.put(passenger, currentRevision);
      }

      while (ASSIGNMENTS.size() > 4096) {
         ASSIGNMENTS.remove(ASSIGNMENTS.keySet().iterator().next());
      }

      if (ready(minecraft.level, packet)) {
         PENDING.remove(packet.getVehicle());
         return false;
      }

      if (PENDING.size() >= 128 && !PENDING.containsKey(packet.getVehicle())) {
         PENDING.remove(PENDING.keySet().iterator().next());
      }

      PENDING.put(packet.getVehicle(), new PassengerRecovery.Pending(packet, ticks + 600, currentRevision));
      LOG.debug("Deferred passenger state until ship/entity state is ready: vehicle={}", packet.getVehicle());
      return true;
   }

   public static void tick() {
      Minecraft minecraft = Minecraft.getInstance();
      ClientPacketListener connection = minecraft.getConnection();
      if (connection == owner && world == minecraft.level && minecraft.level != null) {
         ticks++;
         ArrayList<ClientboundSetPassengersPacket> replay = new ArrayList<>();
         Iterator<Entry<Integer, PassengerRecovery.Pending>> iterator = PENDING.entrySet().iterator();

         while (iterator.hasNext()) {
            Entry<Integer, PassengerRecovery.Pending> entry = iterator.next();
            PassengerRecovery.Pending pending = entry.getValue();
            boolean stale = false;

            for (int passenger : pending.packet().getPassengers()) {
               if (!Objects.equals(ASSIGNMENTS.get(passenger), pending.revision())) {
                  stale = true;
                  break;
               }
            }

            if (stale) {
               iterator.remove();
            } else if (ready(minecraft.level, pending.packet())) {
               replay.add(pending.packet());
               iterator.remove();
            } else if (ticks >= pending.expires()) {
               LOG.warn("Passenger recovery expired after 600 client ticks: vehicle={}", entry.getKey());
               iterator.remove();
            }
         }

         replaying = true;

         try {
            for (ClientboundSetPassengersPacket packet : replay) {
               connection.handleSetEntityPassengersPacket(packet);
               LOG.info("Recovered delayed passenger state: vehicle={}", packet.getVehicle());
            }
         } finally {
            replaying = false;
         }
      } else {
         reset(connection);
      }
   }

   private record Pending(ClientboundSetPassengersPacket packet, int expires, long revision) {
   }
}
