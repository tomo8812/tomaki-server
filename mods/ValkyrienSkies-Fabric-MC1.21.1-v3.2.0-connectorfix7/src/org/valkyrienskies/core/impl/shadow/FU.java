package org.valkyrienskies.core.impl.shadow;

import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import kotlin.Metadata;
import kotlin.collections.ArraysKt;
import kotlin.collections.CollectionsKt;
import kotlin.collections.SetsKt;
import kotlin.jvm.JvmName;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.PropertyReference1;
import kotlin.jvm.internal.PropertyReference1Impl;
import kotlin.jvm.internal.Reflection;
import kotlin.reflect.KProperty;
import org.apache.logging.log4j.Logger;
import org.joml.Vector3d;
import org.joml.Vector3dc;
import org.valkyrienskies.core.api.events.JointSerializedEvent;
import org.valkyrienskies.core.api.physics.RayCastResult;
import org.valkyrienskies.core.api.ships.PhysShip;
import org.valkyrienskies.core.api.util.AerodynamicUtils;
import org.valkyrienskies.core.impl.api_impl.physics.JointSerializedEventImpl;
import org.valkyrienskies.core.impl.api_impl.physics.PhysTickEventImpl;
import org.valkyrienskies.core.impl.game.ships.PhysShipImpl;
import org.valkyrienskies.core.internal.joints.VSJoint;
import org.valkyrienskies.core.internal.joints.VSJointPose;
import org.valkyrienskies.core.internal.world.VsiPhysLevel;

public final class FU implements He, VsiPhysLevel {
   private static FU.a f = new FU.a(null);
   private final String g;
   public final He b;
   private final Function0<Long2ObjectMap<? extends PhysShip>> h;
   private final FN i;
   private final Ek j;
   private final Et k;
   private final Function1<Integer, PhysShip> l;
   private final Set<FB> m;
   public final Long2ObjectMap<Set<FB>> c;
   private final Map<Integer, VSJoint> n;
   private final Map<Long, Set<Integer>> o;
   public final HashSet<Long> d;
   public int e;
   private static final Logger p = Gb.a("(VS Core) Pie Factory");

   public FU(
      String var1,
      He var2,
      Function0<? extends Long2ObjectMap<? extends PhysShip>> var3,
      FN var4,
      Ek var5,
      Et var6,
      Function1<? super Integer, ? extends PhysShip> var7
   ) {
      Intrinsics.checkNotNullParameter(var1, "")
      Intrinsics.checkNotNullParameter(var2, "")
      Intrinsics.checkNotNullParameter(var3, "")
      Intrinsics.checkNotNullParameter(var4, "")
      Intrinsics.checkNotNullParameter(var5, "")
      Intrinsics.checkNotNullParameter(var6, "")
      Intrinsics.checkNotNullParameter(var7, "")
      super()
      this.g = var1
      this.b = var2
      this.h = var3
      this.i = var4
      this.j = var5
      this.k = var6
      this.l = var7
      this.m = new HashSet<>()
      this.c = EU.a()
      this.n = new HashMap<>()
      this.o = new HashMap<>()
      this.d = new HashSet<>()
      this.e = -1
   }

   @Override
   public final String getDimension() {
      return this.g
   }

   public final He a() {
      return this.b
   }

   @Override
   public final boolean a(HM var1, boolean var2) {
      Intrinsics.checkNotNullParameter(var1, "")
      return this.b.a(var1, var2)
   }

   @Override
   public final <T extends GW> Hc<T> a(T var1) {
      Intrinsics.checkNotNullParameter(var1, "")
      return this.b.a((T)var1)
   }

   @Override
   public final void b() {
      this.b.b()
   }

   @Override
   public final boolean a(int var1) {
      return this.b.a(var1)
   }

   @Override
   public final void a(int var1, int var2) {
      this.b.a(var1, var2)
   }

   @Override
   public final void b(int var1, int var2) {
      this.b.b(var1, var2)
   }

   @Override
   public final List<HW> c() {
      return this.b.c()
   }

   @Override
   public final List<HM> d() {
      return this.b.d()
   }

   @Override
   public final List<HM> e() {
      return this.b.e()
   }

   @Override
   public final List<HM> f() {
      return this.b.f()
   }

   @Override
   public final boolean g() {
      return this.b.g()
   }

   @Override
   public final void h() {
      this.b.h()
   }

   @Override
   public final List<He.c> a(Vector3dc var1, double var2) {
      Intrinsics.checkNotNullParameter(var1, "")
      return this.b.a(var1, var2)
   }

   @Override
   public final Hh a(Vector3dc var1, Vector3dc var2, double var3, int... var5) {
      Intrinsics.checkNotNullParameter(var1, "")
      Intrinsics.checkNotNullParameter(var2, "")
      Intrinsics.checkNotNullParameter(var5, "")
      return this.b.a(var1, var2, var3, var5)
   }

   @Override
   public final Hi b(Vector3dc var1, Vector3dc var2, double var3, int... var5) {
      Intrinsics.checkNotNullParameter(var1, "")
      Intrinsics.checkNotNullParameter(var2, "")
      Intrinsics.checkNotNullParameter(var5, "")
      return this.b.b(var1, var2, var3, var5)
   }

   @Override
   public final boolean b(int var1) {
      return this.b.b(var1)
   }

   @Override
   public final void a(Integer var1) {
   }

   @Override
   public final boolean a(HM var1) {
      Intrinsics.checkNotNullParameter(var1, "")
      return this.b.a(var1)
   }

   public final Long2ObjectMap<Set<FB>> i() {
      return this.c
   }

   public final HashSet<Long> j() {
      return this.d
   }

   private int l() {
      return this.e
   }

   public final void c(int var1) {
      this.e = var1
   }

   @Override
   public final void physTick(double delta) {
      this.i.a().getPhysTickEvent().emit(new PhysTickEventImpl(this, delta))
   }

   @Override
   public final AerodynamicUtils getAerodynamicUtils() {
      Et var1 = this.k
      return this.k.y
   }

   @Override
   public final Vector3dc getGravity() {
      return new Vector3d(0.0, -1.0 * ((Number)this.getAerodynamicUtils().getAtmosphereForDimension(this.getDimension()).getThird()).doubleValue(), 0.0)
   }

   @Override
   public final Map<Integer, VSJoint> getAllJoints() {
      return this.n
   }

   @Override
   public final Map<Long, Set<Integer>> getJointsByShipIds() {
      return this.o
   }

   @Override
   public final Set<PhysShip> getAllPhysShips() {
      FN var1 = this.i
      Long2ObjectMap var10000 = this.i.d.get(this.getDimension())
      if (var10000 == null) {
         return SetsKt.emptySet()
      }

      Collection var6 = var10000.values()
      Intrinsics.checkNotNullExpressionValue(var6, "")
      Iterable var4 = var6
      LinkedHashSet var2 = new LinkedHashSet()

      for (PhysShip var3 : var4) {
         var2.add(var3)
      }

      return var2
   }

   private static Set<PhysShip> a(Iterable<? extends PhysShip> var0) {
      LinkedHashSet var1 = new LinkedHashSet()

      for (PhysShip var2 : var0) {
         var1.add(var2)
      }

      return var1
   }

   @Override
   public final PhysShip getShipById(long shipId) {
      FN var3 = this.i
      Object var10000 = this.i.d.get(this.getDimension())
      Intrinsics.checkNotNull(var10000)
      return (PhysShip)((Long2ObjectMap)var10000).get(shipId)
   }

   @Override
   public final RayCastResult rayCast(Vector3dc start, Vector3dc direction, double length, long... ignoredShips) {
      Intrinsics.checkNotNullParameter(start, "")
      Intrinsics.checkNotNullParameter(direction, "")
      Intrinsics.checkNotNullParameter(ignoredShips, "")
      Iterable var19 = ArraysKt.toList(ignoredShips)
      var var6 = new ArrayList()
      Iterator var20 = var19.iterator()

      while (var20.hasNext()) {
         Integer var24
         label34: {
            long var15 = ((Number)var20.next()).longValue()
            PhysShipImpl var10000 = (PhysShipImpl)this.getShipById(var15)
            if (var10000 != null) {
               Hc var23 = var10000.getRigidBodyReference$impl()
               if (var23 != null) {
                  var24 = var23.a()
                  break label34;
               }
            }

            var24 = null
         }

         if (var24 != null) {
            Integer var7 = var24
            var6.add(var7)
         }
      }

      int[] var21 = CollectionsKt.toIntArray(var6)
      Hi var25 = this.b.b(start, direction, length, Arrays.copyOf(var21, var21.length))
      if (var25 == null) {
         return null
      }

      Hi var22 = var25
      PhysShip var17
      if ((var17 = (PhysShip)this.l.invoke(var22.a())) == null) {
         if (var22.a() == this.e) {
            FN var18 = this.i
            Object var10001 = this.i.n.get(this.getDimension())
            Intrinsics.checkNotNull(var10001)
            var17 = this.getShipById(((Number)var10001).longValue())
         }

         if (var17 == null) {
            f.a().warn("Raycast hit a body with rigidbody id " + var22.a() + " that does not correspond to any known PhysShip!")
            return null
         }
      }

      return new RayCastResult(var17, var22.b(), var22.c(), var22.d(), var22.e())
   }

   @Override
   public final boolean enableCollisionBetween(long shipId0, long shipId1) {
      FB var5 = new FB(shipId0, shipId1)
      if (!this.m.contains(var5)) {
         return false
      }

      PhysShipImpl var10000 = (PhysShipImpl)this.getShipById(shipId0)
      if (var10000 != null) {
         Hc var8 = var10000.getRigidBodyReference$impl()
         if (var8 != null) {
            int var6 = var8.a()
            var10000 = (PhysShipImpl)this.getShipById(shipId1)
            if (var10000 != null) {
               Hc var10 = var10000.getRigidBodyReference$impl()
               if (var10 != null) {
                  int var7 = var10.a()
                  this.b.b(var6, var7)
                  this.m.remove(var5)
                  Set var11 = (Set)this.c.get(shipId0)
                  if (var11 != null) {
                     var11.remove(var5)
                  }

                  Set var12 = (Set)this.c.get(shipId1)
                  if (var12 != null) {
                     var12.remove(var5)
                  }

                  return true
               }
            }

            return false
         }
      }

      return false
   }

   @Override
   public final boolean disableCollisionBetween(long shipId0, long shipId1) {
      FB var5 = new FB(shipId0, shipId1)
      if (this.m.contains(var5)) {
         return false
      }

      PhysShipImpl var10000 = (PhysShipImpl)this.getShipById(shipId0)
      if (var10000 != null) {
         Hc var8 = var10000.getRigidBodyReference$impl()
         if (var8 != null) {
            int var6 = var8.a()
            var10000 = (PhysShipImpl)this.getShipById(shipId1)
            if (var10000 != null) {
               Hc var10 = var10000.getRigidBodyReference$impl()
               if (var10 != null) {
                  int var7 = var10.a()
                  this.b.a(var6, var7)
                  this.m.add(var5)
                  ((Set)this.c.computeIfAbsent(shipId0, FU::a)).add(var5)
                  ((Set)this.c.computeIfAbsent(shipId1, FU::b)).add(var5)
                  return true
               }
            }

            return false
         }
      }

      return false
   }

   @Override
   public final VSJoint getJointById(int jointId) {
      return this.n.get(jointId)
   }

   @Override
   public final Set<Integer> getJointsFromShip(long shipId) {
      Set var10000 = this.o.get(shipId)
      if (var10000 == null) {
         var10000 = SetsKt.emptySet()
      }

      return var10000
   }

   @Override
   public final int addJoint(VSJoint newJoint) {
      Intrinsics.checkNotNullParameter(newJoint, "")
      if (!this.a(newJoint, "make")) {
         return -1
      }

      Ek var2 = this.j
      int var3 = this.j.b.getAndIncrement()
      return this.a(var3, newJoint, JointSerializedEvent.Operation.CREATED) ? var3 : -1
   }

   @Override
   public final boolean updateJoint(int jointId, VSJoint updatedJoint) {
      label73: {
         Intrinsics.checkNotNullParameter(updatedJoint, "")
         if (updatedJoint.getShipId0() != null) {
            Long var10001 = updatedJoint.getShipId0()
            Intrinsics.checkNotNull(var10001)
            if (this.getShipById(var10001) == null) {
               break label73;
            }
         }

         if (updatedJoint.getShipId1() != null) {
            Long var16 = updatedJoint.getShipId1()
            Intrinsics.checkNotNull(var16)
            if (this.getShipById(var16) == null) {
               break label73;
            }
         }

         if (!CollectionsKt.contains(this.d, updatedJoint.getShipId0()) && !CollectionsKt.contains(this.d, updatedJoint.getShipId1())) {
            if (Intrinsics.areEqual(updatedJoint.getShipId0(), updatedJoint.getShipId1())) {
               f.a().warn("You can't update a joint to connect the same body to itself!")
               return false
            }

            VSJoint var10000 = this.n.get(jointId)
            if (var10000 == null) {
               return false
            }

            VSJoint var3 = var10000
            Long var10 = updatedJoint.getShipId0()
            long var4 = var10 != null ? var10 : -1L
            Long var11 = updatedJoint.getShipId1()
            long var6 = var11 != null ? var11 : -1L
            boolean var8
            if (var8 = this.b.a(new HM(jointId, this.i.a(updatedJoint, this.getDimension())))) {
               if (!Intrinsics.areEqual(var3.getShipId0(), updatedJoint.getShipId0()) || !Intrinsics.areEqual(var3.getShipId1(), updatedJoint.getShipId1())) {
                  Map var12 = this.o
                  Long var17 = var3.getShipId0()
                  Set var13 = (Set)var12.get(var17 != null ? var17 : -1L)
                  if (var13 != null) {
                     var13.remove(jointId)
                  }

                  Map var14 = this.o
                  var17 = var3.getShipId1()
                  Set var15 = (Set)var14.get(var17 != null ? var17 : -1L)
                  if (var15 != null) {
                     var15.remove(jointId)
                  }
               }

               Integer var9 = jointId
               this.n.put(var9, updatedJoint)
               this.j.a(jointId, updatedJoint)
               this.b(jointId, updatedJoint, JointSerializedEvent.Operation.UPDATED)
               this.o.computeIfAbsent(var4, FU::a).add(jointId)
               this.o.computeIfAbsent(var6, FU::b).add(jointId)
            } else {
               f.a().warn("Failed to update joint with id " + jointId + " between " + var4 + " and " + var6 + "! STAGE: Krunch Bridge")
            }

            return var8
         }

         f.a().warn("Attempted to update a joint with a ship that is being removed! Ship IDs: " + updatedJoint.getShipId0() + ", " + updatedJoint.getShipId1())
         return false
      }

      f.a().warn("Attempted to update a joint with ships that don't exist! Has the ship been loaded yet?")
      return false
   }

   @Override
   public final boolean removeJoint(int jointId) {
      Integer var3 = jointId
      if (!this.n.containsKey(var3)) {
         f.a().warn("Tried to remove a joint with id " + jointId + " that does not exist!")
         return false
      }

      VSJoint var10000 = this.n.get(jointId)
      if (var10000 == null) {
         return false
      }

      VSJoint var2 = var10000
      boolean var5
      if (var5 = this.b.b(jointId)) {
         this.n.remove(jointId)
         this.j.b(jointId)
         Map var6 = this.o
         Long var10001 = var2.getShipId0()
         Set var7 = (Set)var6.get(var10001 != null ? var10001 : -1L)
         if (var7 != null) {
            var7.remove(jointId)
         }

         Map var8 = this.o
         var10001 = var2.getShipId1()
         Set var9 = (Set)var8.get(var10001 != null ? var10001 : -1L)
         if (var9 != null) {
            var9.remove(jointId)
         }

         this.b(jointId, var2, JointSerializedEvent.Operation.REMOVED)
      } else {
         f.a().warn("Failed to remove joint with id " + jointId + " between " + var2.getShipId0() + " and " + var2.getShipId1() + "! STAGE: Krunch Bridge")
      }

      return var5
   }

   public final boolean a(int var1, VSJoint var2) {
      Intrinsics.checkNotNullParameter(var2, "")
      if (!this.a(var2, "restore")) {
         return false
      }

      this.j.a(var1 + 1)
      return this.a(var1, var2, JointSerializedEvent.Operation.RESTORED)
   }

   private final boolean a(VSJoint var1, String var2) {
      label32: {
         if (var1.getShipId0() != null) {
            Long var10001 = var1.getShipId0()
            Intrinsics.checkNotNull(var10001)
            if (this.getShipById(var10001) == null) {
               break label32;
            }
         }

         if (var1.getShipId1() != null) {
            Long var3 = var1.getShipId1()
            Intrinsics.checkNotNull(var3)
            if (this.getShipById(var3) == null) {
               break label32;
            }
         }

         if (!this.a(var1)) {
            f.a().warn("Attempted to " + var2 + " a joint with non-finite pose values! Ship IDs: " + var1.getShipId0() + ", " + var1.getShipId1())
            return false
         }

         if (!CollectionsKt.contains(this.d, var1.getShipId0()) && !CollectionsKt.contains(this.d, var1.getShipId1())) {
            if (Intrinsics.areEqual(var1.getShipId0(), var1.getShipId1())) {
               f.a().warn("You can't " + var2 + " a joint that connects the same body to itself!")
               return false
            }

            return true
         }

         f.a().warn("Attempted to " + var2 + " a joint with a ship that is being removed! Ship IDs: " + var1.getShipId0() + ", " + var1.getShipId1())
         return false
      }

      f.a().warn("Attempted to " + var2 + " a joint with ships that don't exist! Has the ship been loaded yet?")
      return false
   }

   private final boolean a(VSJoint var1) {
      return this.a(var1.getPose0()) && this.a(var1.getPose1())
   }

   private final boolean a(VSJointPose var1) {
      double var2
      double var4
      double var5
      double var6
      return a(var1.getPos())
         && !Double.isInfinite(var2 = var1.getRot().x())
         && !Double.isNaN(var2)
         && !Double.isInfinite(var4 = var1.getRot().y())
         && !Double.isNaN(var4)
         && !Double.isInfinite(var5 = var1.getRot().z())
         && !Double.isNaN(var5)
         && !Double.isInfinite(var6 = var1.getRot().w())
         && !Double.isNaN(var6)
      }

   private static boolean a(Vector3dc var0) {
      double var1
      double var3
      double var4
      return !Double.isInfinite(var1 = var0.x())
         && !Double.isNaN(var1)
         && !Double.isInfinite(var3 = var0.y())
         && !Double.isNaN(var3)
         && !Double.isInfinite(var4 = var0.z())
         && !Double.isNaN(var4)
      }

   private final boolean a(int var1, VSJoint var2, JointSerializedEvent.Operation var3) {
      Long var10000 = var2.getShipId0()
      long var4 = var10000 != null ? var10000 : -1L
      var10000 = var2.getShipId1()
      long var6 = var10000 != null ? var10000 : -1L
      boolean var8 = var3 != JointSerializedEvent.Operation.RESTORED
      if (!this.b.a(new HM(var1, this.i.a(var2, this.getDimension())), var8)) {
         f.a().warn("Failed to create joint with prospective id " + var1 + " between " + var4 + " and " + var6 + "! STAGE: Krunch Bridge")
         return false
      } else {
         Integer var9 = var1
         this.n.put(var9, var2)
         this.j.a(var1, var2)
         this.o.computeIfAbsent(var4, FU::c).add(var1)
         this.o.computeIfAbsent(var6, FU::d).add(var1)
         this.b(var1, var2, var3)
         return true
      }
   }

   private final void b(int var1, VSJoint var2, JointSerializedEvent.Operation var3) {
      this.i
         .a()
         .getJointSerializedEvent()
         .emit(new JointSerializedEventImpl(var1, var2.getShipId0(), var2.getShipId1(), var2.getJointType().name(), this.getDimension(), var3))
      }

   private static final Set a(long var0) {
      return new HashSet()
   }

   private static final Set b(long var0) {
      return new HashSet()
   }

   private static final Set a(Function1 var0, Object var1) {
      return (Set<Integer>)var0.invoke(var1)
   }

   private static final Set b(Function1 var0, Object var1) {
      return (Set<Integer>)var0.invoke(var1)
   }

   private static final Set c(Function1 var0, Object var1) {
      return (Set<Integer>)var0.invoke(var1)
   }

   private static final Set d(Function1 var0, Object var1) {
      return (Set<Integer>)var0.invoke(var1)
   }

   @Metadata(
      mv = {1, 8, 0},
      k = 1,
      xi = 48,
      d1 = "\u0000\u0014\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0004\b\u0086\u0003\u0018\u00002\u00020\u0001B\t\b\u0002¢\u0006\u0004\b\u0002\u0010\u0003R\u001b\u0010\u0007\u001a\u00020\u00048GX\u0086\u0084\u0002¢\u0006\f\n\u0004\b\u0005\u0010\u0006\u001a\u0004\b\u0007\u0010\b",
      d2 = {
            "Lorg/valkyrienskies/core/impl/shadow/FU$a;",
            "",
            "<init>",
            "()V",
            "Lorg/apache/logging/log4j/Logger;",
            "p",
            "Lorg/apache/logging/log4j/Logger;",
            "a",
            "()Lorg/apache/logging/log4j/Logger;"
      }
   )
   public static final class a {
      // $VF: synthetic field
      private static KProperty<Object>[] a;

      private a() {
      }

      @JvmName(name = "a")
      public final Logger a() {
         Logger var10000 = FU.p
         KProperty var2 = a[0]
         Logger var1 = var10000
         Intrinsics.checkNotNullParameter(var2, "")
         return var1
      }

      // $VF: synthetic method
      public a(DefaultConstructorMarker var1) {
         this()
      }

      static {
         KProperty[] var0
         (var0 = new KProperty[1])[0] = Reflection.property1(
            (PropertyReference1)(new PropertyReference1Impl(FU.a.class, "logger", "getLogger()Lorg/apache/logging/log4j/Logger;", 0))
         )
         a = var0
      }
   }
}
