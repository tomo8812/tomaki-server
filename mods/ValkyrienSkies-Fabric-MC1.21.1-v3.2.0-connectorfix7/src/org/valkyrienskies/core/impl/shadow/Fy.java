package org.valkyrienskies.core.impl.shadow;

import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import javax.inject.Inject;
import kotlin.collections.SetsKt;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.internal.Intrinsics;
import org.joml.Vector3d;
import org.joml.Vector3dc;
import org.valkyrienskies.core.impl.config.VSCoreConfig;
import org.valkyrienskies.core.internal.physics.PhysicsEntityServer;
import org.valkyrienskies.core.internal.world.VsiPlayer;

public final class Fy {
   private final VSCoreConfig.Server a;
   private final HashMap<VsiPlayer, Set<Long>> b;
   private final Long2ObjectOpenHashMap<Set<VsiPlayer>> c;
   private final HashMap<VsiPlayer, Set<Long>> d;
   private final HashMap<VsiPlayer, Set<Long>> e;
   private Set<? extends VsiPlayer> f;

   @Inject
   public Fy(VSCoreConfig.Server var1) {
      Intrinsics.checkNotNullParameter(var1, "")
      super()
      this.a = var1
      this.b = new HashMap<>()
      this.c = new Long2ObjectOpenHashMap()
      this.d = new HashMap<>()
      this.e = new HashMap<>()
      this.f = SetsKt.emptySet()
   }

   private final void a(Iterable<Long> var1) {
      Iterator var6 = var1.iterator()

      while (var6.hasNext()) {
         long var3 = ((Number)var6.next()).longValue()
         Collection var10000 = this.b.values()
         Intrinsics.checkNotNullExpressionValue(var10000, "")
         Iterator var2 = var10000.iterator()

         while (var2.hasNext()) {
            ((Set)var2.next()).remove(var3)
         }

         this.c.remove(var3)
      }
   }

   private final void a() {
      this.d.clear()
      this.e.clear()
   }

   public final Fx a(Set<? extends VsiPlayer> var1, Set<? extends VsiPlayer> var2, Iterable<PhysicsEntityServer> var3, Iterable<Long> var4) {
      Intrinsics.checkNotNullParameter(var1, "")
      Intrinsics.checkNotNullParameter(var2, "")
      Intrinsics.checkNotNullParameter(var3, "")
      Intrinsics.checkNotNullParameter(var4, "")
      Fy var5 = this
      this.d.clear()
      var5.e.clear()
      Iterator var47 = var4.iterator()

      while (var47.hasNext()) {
         long var6 = ((Number)var47.next()).longValue()
         Collection var10000 = this.b.values()
         Intrinsics.checkNotNullExpressionValue(var10000, "")
         Iterator var10 = var10000.iterator()

         while (var10.hasNext()) {
            ((Set)var10.next()).remove(var6)
         }

         Set var8
         if ((var8 = (Set)this.c.get(var6)) != null) {
            this.c.remove(var6)

            Iterable var9
            for (VsiPlayer var13 : var9 = var8) {
               this.e.computeIfAbsent(var13, Fy::a).add(var6)
            }
         }
      }

      var4 = var4
      var5 = this
      Iterator var51 = var4.iterator()

      while (var51.hasNext()) {
         long var30 = ((Number)var51.next()).longValue()
         Collection var63 = var5.b.values()
         Intrinsics.checkNotNullExpressionValue(var63, "")
         Iterator var42 = var63.iterator()

         while (var42.hasNext()) {
            ((Set)var42.next()).remove(var30)
         }

         var5.c.remove(var30)
      }

      var4 = SetsKt.minus(var2, var1)
      var5 = this
      if (!var4.isEmpty()) {
         for (VsiPlayer var53 : var52 = var4) {
            var5.b.remove(var53)
         }

         var51 = ((Map)var5.c).entrySet().iterator()

         while (var51.hasNext()) {
            Set var62
            (var62 = (Set)((Entry)var51.next()).getValue()).removeAll(var4)
            if (var62.isEmpty()) {
               var51.remove()
            }
         }
      }

      this.f = SetsKt.minus(var1, var2)
      Vector3d var50 = new Vector3d()
      double var55 = this.a.getShipLoadDistance()
      double var58 = this.a.getShipUnloadDistance()
      double var59 = var55 * var55
      double var12 = var58 * var58

      for (PhysicsEntityServer var38 : var3) {
         Iterator var44 = var1.iterator()

         while (var44.hasNext()) {
            VsiPlayer var56
            Vector3dc var7 = (var56 = (VsiPlayer)var44.next()).getPosition(var50)
            double var21 = var38.getShipTransform().getPositionInWorld().distanceSquared(var7)
            Set var64 = this.b.get(var56)
            boolean var57 = var64 != null ? var64.contains(var38.getId()) : false
            if (!Intrinsics.areEqual(var38.getDimensionId(), var56.getDimension())) {
               if (var57) {
                  this.e.computeIfAbsent(var56, Fy::b).add(var38.getId())
               }
            } else if (var21 < var59) {
               if (!var57) {
                  this.d.computeIfAbsent(var56, Fy::c).add(var38.getId())
               }
            } else if (var21 > var12 && var57) {
               this.e.computeIfAbsent(var56, Fy::d).add(var38.getId())
            }
         }
      }

      Map var34
      Iterator var35 = (var34 = this.e).entrySet().iterator()

      while (var35.hasNext()) {
         Entry var39
         VsiPlayer var45 = (VsiPlayer)(var39 = (Entry)var35.next()).getKey()
         Iterator var22 = ((Set)var39.getValue()).iterator()

         while (var22.hasNext()) {
            long var24 = ((Number)var22.next()).longValue()
            Object var65 = this.b.get(var45)
            Intrinsics.checkNotNull(var65)
            ((Set)var65).remove(var24)
            if ((var1 = (Set)this.c.get(var24)) != null) {
               var1.remove(var45)
               if (var1.isEmpty()) {
                  this.c.remove(var24)
               }
            }
         }
      }

      Map var36
      Iterator var37 = (var36 = this.d).entrySet().iterator()

      while (var37.hasNext()) {
         Entry var40
         VsiPlayer var46 = (VsiPlayer)(var40 = (Entry)var37.next()).getKey()
         Iterator var60 = ((Set)var40.getValue()).iterator()

         while (var60.hasNext()) {
            long var61 = ((Number)var60.next()).longValue()
            this.b.computeIfAbsent(var46, Fy::e).add(var61)
            ((Set)this.c.computeIfAbsent(var61, Fy::a)).add(var46)
         }
      }

      return new Fx(this.b, this.c, this.d, this.e)
   }

   private final void a(Set<? extends VsiPlayer> var1) {
      if (!var1.isEmpty()) {
         Iterable var2
         for (VsiPlayer var4 : var2 = var1) {
            this.b.remove(var4)
         }

         Iterator var5 = ((Map)this.c).entrySet().iterator()

         while (var5.hasNext()) {
            Set var6
            (var6 = (Set)((Entry)var5.next()).getValue()).removeAll(var1)
            if (var6.isEmpty()) {
               var5.remove()
            }
         }
      }
   }

   private static final Set a(Function1 var0, Object var1) {
      return (Set<Long>)var0.invoke(var1)
   }

   private static final Set b(Function1 var0, Object var1) {
      return (Set<Long>)var0.invoke(var1)
   }

   private static final Set c(Function1 var0, Object var1) {
      return (Set<Long>)var0.invoke(var1)
   }

   private static final Set d(Function1 var0, Object var1) {
      return (Set<Long>)var0.invoke(var1)
   }

   private static final Set e(Function1 var0, Object var1) {
      return (Set<Long>)var0.invoke(var1)
   }

   private static final HashSet a(long var0) {
      return new HashSet()
   }
}
