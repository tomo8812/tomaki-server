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

   private static boolean ready(ClientLevel var0, ClientboundSetPassengersPacket var1) {
      Entity var2 = var0.getEntity(var1.getVehicle());
      if (var2 == null) {
         return false;
      }

      for (int var6 : var1.getPassengers()) {
         if (var0.getEntity(var6) == null) {
            return false;
         }
      }

      if (var1.getPassengers().length > 0) {
         if (VSGameUtilsKt.isBlockInShipyard(var0, var2.position()) && VSGameUtilsKt.getShipManaging(var2) == null) {
            return false;
         }

         if (var2 instanceof ShipMountingEntity var7 && var7.provideShipMountedToData(var0.getEntity(var1.getPassengers()[0]), 0.0F) == null) {
            return false;
         }
      }

      return true;
   }

   private static void reset(ClientPacketListener var0) {
      PENDING.clear();
      ASSIGNMENTS.clear();
      owner = var0;
      world = Minecraft.getInstance().level;
      ticks = 0;
      revision = 0L;
   }

   public static boolean defer(ClientPacketListener var0, ClientboundSetPassengersPacket var1) {
      Minecraft var2 = Minecraft.getInstance();
      if (var2.level == null) {
         return false;
      }

      if (owner != var0 || world != var2.level) {
         reset(var0);
      }

      if (replaying) {
         return false;
      }

      long var3 = ++revision;

      for (int var8 : var1.getPassengers()) {
         ASSIGNMENTS.remove(var8);
         ASSIGNMENTS.put(var8, var3);
      }

      while (ASSIGNMENTS.size() > 4096) {
         ASSIGNMENTS.remove(ASSIGNMENTS.keySet().iterator().next());
      }

      if (ready(var2.level, var1)) {
         PENDING.remove(var1.getVehicle());
         return false;
      }

      if (PENDING.size() >= 128 && !PENDING.containsKey(var1.getVehicle())) {
         PENDING.remove(PENDING.keySet().iterator().next());
      }

      PENDING.put(var1.getVehicle(), new PassengerRecovery.Pending(var1, ticks + 600, var3));
      LOG.debug("Deferred passengers until entities are available: vehicle={}", var1.getVehicle());
      return true;
   }

   public static void tick() {
      Minecraft var0 = Minecraft.getInstance();
      ClientPacketListener var1 = var0.getConnection();
      if (var1 == owner && world == var0.level && var0.level != null) {
         ticks++;
         ArrayList var2 = new ArrayList();
         Iterator var3 = PENDING.entrySet().iterator();

         while (var3.hasNext()) {
            Entry var4 = (Entry)var3.next();
            PassengerRecovery.Pending var5 = (PassengerRecovery.Pending)var4.getValue();
            boolean var6 = false;

            for (int var10 : var5.packet().getPassengers()) {
               if (!Objects.equals(ASSIGNMENTS.get(var10), var5.revision())) {
                  var6 = true;
                  break;
               }
            }

            if (var6) {
               var3.remove();
            } else if (ready(var0.level, var5.packet())) {
               var2.add(var5.packet());
               var3.remove();
            } else if (ticks >= var5.expires()) {
               LOG.debug("Expired pending passengers: vehicle={}", var4.getKey());
               var3.remove();
            }
         }

         replaying = true;

         try {
            for (ClientboundSetPassengersPacket var15 : var2) {
               var1.handleSetEntityPassengersPacket(var15);
               LOG.info("Recovered delayed passenger state: vehicle={}", var15.getVehicle());
            }
         } finally {
            replaying = false;
         }
      } else {
         reset(var1);
      }
   }

   private record Pending(ClientboundSetPassengersPacket packet, int expires, long revision) {
   }
}
