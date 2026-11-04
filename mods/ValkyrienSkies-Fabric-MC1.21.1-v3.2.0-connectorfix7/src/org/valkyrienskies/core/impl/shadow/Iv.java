package org.valkyrienskies.core.impl.shadow;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.Iterator;
import java.util.List;
import kotlin.Pair;
import kotlin.Triple;
import kotlin.jvm.internal.Intrinsics;
import org.joml.Quaterniond;
import org.joml.Quaterniondc;
import org.joml.Vector3d;
import org.joml.Vector3dc;

public final class Iv {
   private static void a(HN var0, ByteBuffer var1) {
      var1.putInt(var0.p().ordinal());
      switch (var0.p()) {
         case FIXED:
            HK var82 = (HK)var0;
            ByteBuffer var75 = var1;
            HK var68 = var82;
            IH var60 = IH.INSTANCE;
            Intrinsics.checkNotNullParameter(var68, "");
            Intrinsics.checkNotNullParameter(var75, "");
            var60.a(var68, var75);
            return;
         case DISTANCE:
            HJ var81 = (HJ)var0;
            ByteBuffer var74 = var1;
            HJ var67 = var81;
            IH var49 = IH.INSTANCE;
            Intrinsics.checkNotNullParameter(var67, "");
            Intrinsics.checkNotNullParameter(var74, "");
            var49.a(var67, var74);
            boolean var50;
            var74.put(II.a(var50 = var67.b != null));
            if (var67.b != null) {
               float var51 = var67.b.floatValue();
               var74.putFloat(var51);
            }

            boolean var52;
            var74.put(II.a(var52 = var67.c != null));
            if (var67.c != null) {
               float var53 = var67.c.floatValue();
               var74.putFloat(var53);
            }

            boolean var54;
            var74.put(II.a(var54 = var67.d != null));
            if (var67.d != null) {
               float var55 = var67.d.floatValue();
               var74.putFloat(var55);
            }

            boolean var56;
            var74.put(II.a(var56 = var67.e != null));
            if (var67.e != null) {
               float var57 = var67.e.floatValue();
               var74.putFloat(var57);
            }

            boolean var58;
            var74.put(II.a(var58 = var67.f != null));
            if (var67.f != null) {
               float var59 = var67.f.floatValue();
               var74.putFloat(var59);
               return;
            }
            break;
         case SPHERICAL:
            HT var80 = (HT)var0;
            ByteBuffer var73 = var1;
            HT var66 = var80;
            IH var46 = IH.INSTANCE;
            Intrinsics.checkNotNullParameter(var66, "");
            Intrinsics.checkNotNullParameter(var73, "");
            var46.a(var66, var73);
            boolean var47;
            var73.put(II.a(var47 = var66.b != null));
            if (var66.b != null) {
               HI.i var48 = var66.b;
               IH.a(var66.b, var73);
               return;
            }
            break;
         case REVOLUTE:
            HS var79 = (HS)var0;
            ByteBuffer var72 = var1;
            HS var65 = var79;
            IH var35 = IH.INSTANCE;
            Intrinsics.checkNotNullParameter(var65, "");
            Intrinsics.checkNotNullParameter(var72, "");
            var35.a(var65, var72);
            boolean var36;
            var72.put(II.a(var36 = var65.b != null));
            if (var65.b != null) {
               HI.a var37 = var65.b;
               IH.a(var65.b, var72);
            }

            boolean var38;
            var72.put(II.a(var38 = var65.c != null));
            if (var65.c != null) {
               HS.a var39 = var65.c;
               IH.a(var65.c, var72);
            }

            boolean var40;
            var72.put(II.a(var40 = var65.d != null));
            if (var65.d != null) {
               float var41 = var65.d.floatValue();
               var72.putFloat(var41);
            }

            boolean var42;
            var72.put(II.a(var42 = var65.e != null));
            if (var65.e != null) {
               float var43 = var65.e.floatValue();
               var72.putFloat(var43);
            }

            boolean var44;
            var72.put(II.a(var44 = var65.f != null));
            if (var65.f != null) {
               boolean var45 = var65.f;
               var72.put(II.a(var45));
               return;
            }
            break;
         case PRISMATIC:
            HQ var78 = (HQ)var0;
            ByteBuffer var71 = var1;
            HQ var64 = var78;
            IH var32 = IH.INSTANCE;
            Intrinsics.checkNotNullParameter(var64, "");
            Intrinsics.checkNotNullParameter(var71, "");
            var32.a(var64, var71);
            boolean var33;
            var71.put(II.a(var33 = var64.b != null));
            if (var64.b != null) {
               HI.l var34 = var64.b;
               IH.a(var64.b, var71);
               return;
            }
            break;
         case GEAR:
            HL var77 = (HL)var0;
            ByteBuffer var70 = var1;
            HL var63 = var77;
            IH var27 = IH.INSTANCE;
            Intrinsics.checkNotNullParameter(var63, "");
            Intrinsics.checkNotNullParameter(var70, "");
            var27.a(var63, var70);
            boolean var28;
            var70.put(II.a(var28 = var63.b != null));
            if (var63.b != null) {
               HI.h var29 = var63.b;
               IH.a(var63.b, var70);
            }

            boolean var30;
            var70.put(II.a(var30 = var63.c != null));
            if (var63.c != null) {
               float var31 = var63.c.floatValue();
               var70.putFloat(var31);
               return;
            }
            break;
         case RACK_AND_PINION:
            HR var76 = (HR)var0;
            ByteBuffer var69 = var1;
            HR var62 = var76;
            IH var20 = IH.INSTANCE;
            Intrinsics.checkNotNullParameter(var62, "");
            Intrinsics.checkNotNullParameter(var69, "");
            var20.a(var62, var69);
            boolean var21;
            var69.put(II.a(var21 = var62.b != null));
            if (var62.b != null) {
               HI.h var22 = var62.b;
               IH.a(var62.b, var69);
            }

            boolean var23;
            var69.put(II.a(var23 = var62.c != null));
            if (var62.c != null) {
               float var24 = var62.c.floatValue();
               var69.putFloat(var24);
            }

            boolean var25;
            var69.put(II.a(var25 = var62.d != null));
            if (var62.d != null) {
               HR.a var26 = var62.d;
               IH.a(var62.d, var69);
               return;
            }
            break;
         case D6:
            HI var10001 = (HI)var0;
            ByteBuffer var2 = var1;
            HI var61 = var10001;
            IH var5 = IH.INSTANCE;
            Intrinsics.checkNotNullParameter(var61, "");
            Intrinsics.checkNotNullParameter(var2, "");
            var5.a(var61, var2);
            IH.a(var61.b, var2);
            boolean var6;
            var2.put(II.a(var6 = var61.c != null));
            if (var61.c != null) {
               HI.k var7 = var61.c;
               IH.a(var61.c, var2);
            }

            IH.a(var61.d, var2);
            boolean var8;
            var2.put(II.a(var8 = var61.e != null));
            if (var61.e != null) {
               HI.a var9 = var61.e;
               IH.a(var61.e, var2);
            }

            boolean var10;
            var2.put(II.a(var10 = var61.f != null));
            if (var61.f != null) {
               HI.i var11 = var61.f;
               IH.a(var61.f, var2);
            }

            boolean var12;
            var2.put(II.a(var12 = var61.g != null));
            if (var61.g != null) {
               HI.j var13 = var61.g;
               IH.a(var61.g, var2);
            }

            IH.a(var61.h, var2);
            boolean var14;
            var2.put(II.a(var14 = var61.i != null));
            if (var61.i != null) {
               HI.f var15 = var61.i;
               ByteBuffer var4 = var2;
               HI.f var3 = var15;
               IH var16 = IH.INSTANCE;
               IH.a(var3.a, var4);
               boolean var17 = var3.b;
               var4.put(II.a(var3.b));
            }

            boolean var18;
            var2.put(II.a(var18 = var61.j != null));
            if (var61.j != null) {
               HI.g var19 = var61.j;
               IH.a(var61.j, var2);
               return;
            }
            break;
         default:
            throw new IllegalArgumentException("Unknown JointType " + var0.p());
      }
   }

   private static HN c(ByteBuffer var0) {
      HO var1 = HO.values()[var0.getInt()];
      switch (var1) {
         case FIXED:
            ByteBuffer var35 = var0;
            IH var27 = IH.INSTANCE;
            Intrinsics.checkNotNullParameter(var35, "");
            Triple var49;
            Pair var64 = (Pair)(var49 = var27.a(var35)).component1();
            Pair var79 = (Pair)var49.component2();
            Pair var50 = (Pair)var49.component3();
            Integer var87 = (Integer)var64.getFirst();
            Hf var65 = (Hf)var64.getSecond();
            Integer var94 = (Integer)var79.getFirst();
            Hf var80 = (Hf)var79.getSecond();
            return new HK(var87, var65, var94, var80, (HP)var50.getFirst(), ((Number)var50.getSecond()).doubleValue());
         case DISTANCE:
            ByteBuffer var34 = var0;
            IH var26 = IH.INSTANCE;
            Intrinsics.checkNotNullParameter(var34, "");
            Triple var47;
            Pair var62 = (Pair)(var47 = var26.a(var34)).component1();
            Pair var77 = (Pair)var47.component2();
            Pair var48 = (Pair)var47.component3();
            Integer var86 = (Integer)var62.getFirst();
            Hf var63 = (Hf)var62.getSecond();
            Integer var93 = (Integer)var77.getFirst();
            Hf var78 = (Hf)var77.getSecond();
            Float var100 = II.a(var34.get()) ? var34.getFloat() : null;
            Float var104 = II.a(var34.get()) ? var34.getFloat() : null;
            Float var109 = II.a(var34.get()) ? var34.getFloat() : null;
            Float var112 = II.a(var34.get()) ? var34.getFloat() : null;
            Float var115 = II.a(var34.get()) ? var34.getFloat() : null;
            return new HJ(var86, var63, var93, var78, (HP)var48.getFirst(), var100, var104, var109, var112, var115, ((Number)var48.getSecond()).doubleValue());
         case SPHERICAL:
            ByteBuffer var33 = var0;
            IH var25 = IH.INSTANCE;
            Intrinsics.checkNotNullParameter(var33, "");
            Triple var45;
            Pair var60 = (Pair)(var45 = var25.a(var33)).component1();
            Pair var75 = (Pair)var45.component2();
            Pair var46 = (Pair)var45.component3();
            Integer var85 = (Integer)var60.getFirst();
            Hf var61 = (Hf)var60.getSecond();
            Integer var92 = (Integer)var75.getFirst();
            Hf var76 = (Hf)var75.getSecond();
            HI.i var99 = II.a(var33.get()) ? IH.n(var33) : null;
            return new HT(var85, var61, var92, var76, (HP)var46.getFirst(), var99, ((Number)var46.getSecond()).doubleValue());
         case REVOLUTE:
            ByteBuffer var32 = var0;
            IH var24 = IH.INSTANCE;
            Intrinsics.checkNotNullParameter(var32, "");
            Triple var43;
            Pair var58 = (Pair)(var43 = var24.a(var32)).component1();
            Pair var73 = (Pair)var43.component2();
            Pair var44 = (Pair)var43.component3();
            Integer var84 = (Integer)var58.getFirst();
            Hf var59 = (Hf)var58.getSecond();
            Integer var91 = (Integer)var73.getFirst();
            Hf var74 = (Hf)var73.getSecond();
            HI.a var98 = II.a(var32.get()) ? IH.m(var32) : null;
            HS.a var103 = II.a(var32.get()) ? IH.t(var32) : null;
            Float var108 = II.a(var32.get()) ? var32.getFloat() : null;
            Float var111 = II.a(var32.get()) ? var32.getFloat() : null;
            Boolean var114 = II.a(var32.get()) ? II.a(var32.get()) : null;
            return new HS(var84, var59, var91, var74, (HP)var44.getFirst(), var98, var103, var108, var111, var114, ((Number)var44.getSecond()).doubleValue());
         case PRISMATIC:
            ByteBuffer var31 = var0;
            IH var23 = IH.INSTANCE;
            Intrinsics.checkNotNullParameter(var31, "");
            Triple var41;
            Pair var56 = (Pair)(var41 = var23.a(var31)).component1();
            Pair var71 = (Pair)var41.component2();
            Pair var42 = (Pair)var41.component3();
            Integer var83 = (Integer)var56.getFirst();
            Hf var57 = (Hf)var56.getSecond();
            Integer var90 = (Integer)var71.getFirst();
            Hf var72 = (Hf)var71.getSecond();
            HI.l var97 = II.a(var31.get()) ? IH.l(var31) : null;
            return new HQ(var83, var57, var90, var72, (HP)var42.getFirst(), var97, ((Number)var42.getSecond()).doubleValue());
         case GEAR:
            ByteBuffer var30 = var0;
            IH var22 = IH.INSTANCE;
            Intrinsics.checkNotNullParameter(var30, "");
            Triple var39;
            Pair var54 = (Pair)(var39 = var22.a(var30)).component1();
            Pair var69 = (Pair)var39.component2();
            Pair var40 = (Pair)var39.component3();
            Integer var82 = (Integer)var54.getFirst();
            Hf var55 = (Hf)var54.getSecond();
            Integer var89 = (Integer)var69.getFirst();
            Hf var70 = (Hf)var69.getSecond();
            HI.h var96 = II.a(var30.get()) ? IH.p(var30) : null;
            Float var102 = II.a(var30.get()) ? var30.getFloat() : null;
            return new HL(var82, var55, var89, var70, (HP)var40.getFirst(), var96, var102, ((Number)var40.getSecond()).doubleValue());
         case RACK_AND_PINION:
            ByteBuffer var29 = var0;
            IH var21 = IH.INSTANCE;
            Intrinsics.checkNotNullParameter(var29, "");
            Triple var37;
            Pair var52 = (Pair)(var37 = var21.a(var29)).component1();
            Pair var67 = (Pair)var37.component2();
            Pair var38 = (Pair)var37.component3();
            Integer var81 = (Integer)var52.getFirst();
            Hf var53 = (Hf)var52.getSecond();
            Integer var88 = (Integer)var67.getFirst();
            Hf var68 = (Hf)var67.getSecond();
            HI.h var95 = II.a(var29.get()) ? IH.p(var29) : null;
            Float var101 = II.a(var29.get()) ? var29.getFloat() : null;
            HR.a var107 = II.a(var29.get()) ? IH.q(var29) : null;
            return new HR(var81, var53, var88, var68, (HP)var38.getFirst(), var95, var101, var107, ((Number)var38.getSecond()).doubleValue());
         case D6:
            ByteBuffer var28 = var0;
            IH var18 = IH.INSTANCE;
            Intrinsics.checkNotNullParameter(var28, "");
            Triple var2;
            Pair var3 = (Pair)(var2 = var18.a(var28)).component1();
            Pair var4 = (Pair)var2.component2();
            Pair var36 = (Pair)var2.component3();
            Integer var5 = (Integer)var3.getFirst();
            Hf var51 = (Hf)var3.getSecond();
            Integer var6 = (Integer)var4.getFirst();
            Hf var66 = (Hf)var4.getSecond();
            EnumMap var10000;
            if (var28.get() == 0) {
               var10000 = null;
            } else {
               int var10 = var28.getInt();
               EnumMap var11 = new EnumMap<>(HI.b.class);
               int var12 = 0;

               while (var12 < var10) {
                  var12++;
                  Enum var13 = HI.b.class.getEnumConstants()[var28.getInt()];
                  Class<HI.e> var14 = HI.e.class;
                  Object var129;
                  if (Intrinsics.areEqual(HI.e.class, HI.e.class)) {
                     var129 = HI.e.values()[var28.getInt()];
                  } else if (Intrinsics.areEqual(var14, HI.l.class)) {
                     var129 = IH.l(var28);
                  } else {
                     if (!Intrinsics.areEqual(var14, HI.d.class)) {
                        throw new IllegalArgumentException(Intrinsics.stringPlus("Unsupported enum map value type: ", HI.e.class));
                     }

                     var129 = IH.r(var28);
                  }

                  if (var129 == null) {
                     throw new NullPointerException("null cannot be cast to non-null type org.valkyrienskies.physics_api.joints.D6Joint.D6Motion");
                  }

                  HI.e var15 = (HI.e)var129;
                  var11.put(var13, var15);
               }

               var10000 = var11;
            }

            EnumMap var7 = var10000;
            HI.k var8 = II.a(var28.get()) ? IH.k(var28) : null;
            if (var28.get() == 0) {
               var10000 = null;
            } else {
               int var116 = var28.getInt();
               EnumMap var118 = new EnumMap<>(HI.b.class);
               int var121 = 0;

               while (var121 < var116) {
                  var121++;
                  Enum var126 = HI.b.class.getEnumConstants()[var28.getInt()];
                  Class<HI.l> var9 = HI.l.class;
                  Object var131;
                  if (Intrinsics.areEqual(HI.l.class, HI.e.class)) {
                     var131 = HI.e.values()[var28.getInt()];
                  } else if (Intrinsics.areEqual(var9, HI.l.class)) {
                     var131 = IH.l(var28);
                  } else {
                     if (!Intrinsics.areEqual(var9, HI.d.class)) {
                        throw new IllegalArgumentException(Intrinsics.stringPlus("Unsupported enum map value type: ", HI.l.class));
                     }

                     var131 = IH.r(var28);
                  }

                  if (var131 == null) {
                     throw new NullPointerException("null cannot be cast to non-null type org.valkyrienskies.physics_api.joints.D6Joint.LinearLimitPair");
                  }

                  HI.l var105 = (HI.l)var131;
                  var118.put(var126, var105);
               }

               var10000 = var118;
            }

            EnumMap var106 = var10000;
            HI.a var110 = II.a(var28.get()) ? IH.m(var28) : null;
            HI.i var113 = II.a(var28.get()) ? IH.n(var28) : null;
            HI.j var117 = II.a(var28.get()) ? IH.o(var28) : null;
            if (var28.get() == 0) {
               var10000 = null;
            } else {
               int var119 = var28.getInt();
               EnumMap var122 = new EnumMap<>(HI.c.class);
               int var127 = 0;

               while (var127 < var119) {
                  var127++;
                  Enum var16 = HI.c.class.getEnumConstants()[var28.getInt()];
                  Class<HI.d> var17 = HI.d.class;
                  Object var133;
                  if (Intrinsics.areEqual(HI.d.class, HI.e.class)) {
                     var133 = HI.e.values()[var28.getInt()];
                  } else if (Intrinsics.areEqual(var17, HI.l.class)) {
                     var133 = IH.l(var28);
                  } else {
                     if (!Intrinsics.areEqual(var17, HI.d.class)) {
                        throw new IllegalArgumentException(Intrinsics.stringPlus("Unsupported enum map value type: ", HI.d.class));
                     }

                     var133 = IH.r(var28);
                  }

                  if (var133 == null) {
                     throw new NullPointerException("null cannot be cast to non-null type org.valkyrienskies.physics_api.joints.D6Joint.D6JointDrive");
                  }

                  HI.d var128 = (HI.d)var133;
                  var122.put(var16, var128);
               }

               var10000 = var122;
            }

            EnumMap var120 = var10000;
            HI.f var134;
            if (II.a(var28.get())) {
               ByteBuffer var123 = var28;
               Hf var19 = IH.j(var123);
               boolean var124 = II.a(var123.get());
               var134 = new HI.f(var19, var124);
            } else {
               var134 = null;
            }

            HI.f var125 = var134;
            HI.g var20 = II.a(var28.get()) ? IH.s(var28) : null;
            return new HI(
               var5,
               var51,
               var6,
               var66,
               (HP)var36.getFirst(),
               var7,
               var8,
               var106,
               var110,
               var113,
               var117,
               var120,
               var125,
               var20,
               ((Number)var36.getSecond()).doubleValue()
            );
         default:
            throw new IllegalArgumentException("Unknown JointType " + var1);
      }
   }

   private static void a(List<HM> var0, ByteBuffer var1) {
      var1.putInt(0);
      Iterator var3 = var0.iterator();

      while (var3.hasNext()) {
         a((HM)var3.next(), var1);
      }

      int var4 = var1.position();
      var1.putInt(0, var4);
   }

   public static List<HM> a(ByteBuffer var0) {
      ArrayList var1 = new ArrayList();
      int var2 = var0.getInt();

      while (var0.position() != var2) {
         ByteBuffer var3 = var0;
         int var4 = var0.getInt();
         var3 = var3;
         HO var5 = HO.values()[var3.getInt()];
         HN var142;
         switch (var5) {
            case FIXED:
               ByteBuffer var42 = var3;
               IH var32 = IH.INSTANCE;
               Intrinsics.checkNotNullParameter(var42, "");
               Triple var56;
               Pair var71 = (Pair)(var56 = var32.a(var42)).component1();
               Pair var86 = (Pair)var56.component2();
               Pair var57 = (Pair)var56.component3();
               Integer var94 = (Integer)var71.getFirst();
               Hf var72 = (Hf)var71.getSecond();
               Integer var101 = (Integer)var86.getFirst();
               Hf var87 = (Hf)var86.getSecond();
               var142 = new HK(var94, var72, var101, var87, (HP)var57.getFirst(), ((Number)var57.getSecond()).doubleValue());
               break;
            case DISTANCE:
               ByteBuffer var41 = var3;
               IH var31 = IH.INSTANCE;
               Intrinsics.checkNotNullParameter(var41, "");
               Triple var54;
               Pair var69 = (Pair)(var54 = var31.a(var41)).component1();
               Pair var84 = (Pair)var54.component2();
               Pair var55 = (Pair)var54.component3();
               Integer var93 = (Integer)var69.getFirst();
               Hf var70 = (Hf)var69.getSecond();
               Integer var100 = (Integer)var84.getFirst();
               Hf var85 = (Hf)var84.getSecond();
               Float var107 = II.a(var41.get()) ? var41.getFloat() : null;
               Float var111 = II.a(var41.get()) ? var41.getFloat() : null;
               Float var116 = II.a(var41.get()) ? var41.getFloat() : null;
               Float var119 = II.a(var41.get()) ? var41.getFloat() : null;
               Float var122 = II.a(var41.get()) ? var41.getFloat() : null;
               var142 = new HJ(
                  var93, var70, var100, var85, (HP)var55.getFirst(), var107, var111, var116, var119, var122, ((Number)var55.getSecond()).doubleValue()
               );
               break;
            case SPHERICAL:
               ByteBuffer var40 = var3;
               IH var30 = IH.INSTANCE;
               Intrinsics.checkNotNullParameter(var40, "");
               Triple var52;
               Pair var67 = (Pair)(var52 = var30.a(var40)).component1();
               Pair var82 = (Pair)var52.component2();
               Pair var53 = (Pair)var52.component3();
               Integer var92 = (Integer)var67.getFirst();
               Hf var68 = (Hf)var67.getSecond();
               Integer var99 = (Integer)var82.getFirst();
               Hf var83 = (Hf)var82.getSecond();
               HI.i var106 = II.a(var40.get()) ? IH.n(var40) : null;
               var142 = new HT(var92, var68, var99, var83, (HP)var53.getFirst(), var106, ((Number)var53.getSecond()).doubleValue());
               break;
            case REVOLUTE:
               ByteBuffer var39 = var3;
               IH var29 = IH.INSTANCE;
               Intrinsics.checkNotNullParameter(var39, "");
               Triple var50;
               Pair var65 = (Pair)(var50 = var29.a(var39)).component1();
               Pair var80 = (Pair)var50.component2();
               Pair var51 = (Pair)var50.component3();
               Integer var91 = (Integer)var65.getFirst();
               Hf var66 = (Hf)var65.getSecond();
               Integer var98 = (Integer)var80.getFirst();
               Hf var81 = (Hf)var80.getSecond();
               HI.a var105 = II.a(var39.get()) ? IH.m(var39) : null;
               HS.a var110 = II.a(var39.get()) ? IH.t(var39) : null;
               Float var115 = II.a(var39.get()) ? var39.getFloat() : null;
               Float var118 = II.a(var39.get()) ? var39.getFloat() : null;
               Boolean var121 = II.a(var39.get()) ? II.a(var39.get()) : null;
               var142 = new HS(
                  var91, var66, var98, var81, (HP)var51.getFirst(), var105, var110, var115, var118, var121, ((Number)var51.getSecond()).doubleValue()
               );
               break;
            case PRISMATIC:
               ByteBuffer var38 = var3;
               IH var28 = IH.INSTANCE;
               Intrinsics.checkNotNullParameter(var38, "");
               Triple var48;
               Pair var63 = (Pair)(var48 = var28.a(var38)).component1();
               Pair var78 = (Pair)var48.component2();
               Pair var49 = (Pair)var48.component3();
               Integer var90 = (Integer)var63.getFirst();
               Hf var64 = (Hf)var63.getSecond();
               Integer var97 = (Integer)var78.getFirst();
               Hf var79 = (Hf)var78.getSecond();
               HI.l var104 = II.a(var38.get()) ? IH.l(var38) : null;
               var142 = new HQ(var90, var64, var97, var79, (HP)var49.getFirst(), var104, ((Number)var49.getSecond()).doubleValue());
               break;
            case GEAR:
               ByteBuffer var37 = var3;
               IH var27 = IH.INSTANCE;
               Intrinsics.checkNotNullParameter(var37, "");
               Triple var46;
               Pair var61 = (Pair)(var46 = var27.a(var37)).component1();
               Pair var76 = (Pair)var46.component2();
               Pair var47 = (Pair)var46.component3();
               Integer var89 = (Integer)var61.getFirst();
               Hf var62 = (Hf)var61.getSecond();
               Integer var96 = (Integer)var76.getFirst();
               Hf var77 = (Hf)var76.getSecond();
               HI.h var103 = II.a(var37.get()) ? IH.p(var37) : null;
               Float var109 = II.a(var37.get()) ? var37.getFloat() : null;
               var142 = new HL(var89, var62, var96, var77, (HP)var47.getFirst(), var103, var109, ((Number)var47.getSecond()).doubleValue());
               break;
            case RACK_AND_PINION:
               ByteBuffer var36 = var3;
               IH var26 = IH.INSTANCE;
               Intrinsics.checkNotNullParameter(var36, "");
               Triple var44;
               Pair var59 = (Pair)(var44 = var26.a(var36)).component1();
               Pair var74 = (Pair)var44.component2();
               Pair var45 = (Pair)var44.component3();
               Integer var88 = (Integer)var59.getFirst();
               Hf var60 = (Hf)var59.getSecond();
               Integer var95 = (Integer)var74.getFirst();
               Hf var75 = (Hf)var74.getSecond();
               HI.h var102 = II.a(var36.get()) ? IH.p(var36) : null;
               Float var108 = II.a(var36.get()) ? var36.getFloat() : null;
               HR.a var114 = II.a(var36.get()) ? IH.q(var36) : null;
               var142 = new HR(var88, var60, var95, var75, (HP)var45.getFirst(), var102, var108, var114, ((Number)var45.getSecond()).doubleValue());
               break;
            case D6:
               ByteBuffer var35 = var3;
               IH var23 = IH.INSTANCE;
               Intrinsics.checkNotNullParameter(var35, "");
               Triple var6;
               Pair var7 = (Pair)(var6 = var23.a(var35)).component1();
               Pair var8 = (Pair)var6.component2();
               Pair var43 = (Pair)var6.component3();
               Integer var9 = (Integer)var7.getFirst();
               Hf var58 = (Hf)var7.getSecond();
               Integer var10 = (Integer)var8.getFirst();
               Hf var73 = (Hf)var8.getSecond();
               EnumMap var136;
               if (var35.get() == 0) {
                  var136 = null;
               } else {
                  int var14 = var35.getInt();
                  EnumMap var15 = new EnumMap<>(HI.b.class);
                  int var16 = 0;

                  while (var16 < var14) {
                     var16++;
                     Enum var17 = HI.b.class.getEnumConstants()[var35.getInt()];
                     Class<HI.e> var18 = HI.e.class;
                     Object var10000;
                     if (Intrinsics.areEqual(HI.e.class, HI.e.class)) {
                        var10000 = HI.e.values()[var35.getInt()];
                     } else if (Intrinsics.areEqual(var18, HI.l.class)) {
                        var10000 = IH.l(var35);
                     } else {
                        if (!Intrinsics.areEqual(var18, HI.d.class)) {
                           throw new IllegalArgumentException(Intrinsics.stringPlus("Unsupported enum map value type: ", HI.e.class));
                        }

                        var10000 = IH.r(var35);
                     }

                     if (var10000 == null) {
                        throw new NullPointerException("null cannot be cast to non-null type org.valkyrienskies.physics_api.joints.D6Joint.D6Motion");
                     }

                     HI.e var19 = (HI.e)var10000;
                     var15.put(var17, var19);
                  }

                  var136 = var15;
               }

               EnumMap var11 = var136;
               HI.k var12 = II.a(var35.get()) ? IH.k(var35) : null;
               EnumMap var138;
               if (var35.get() == 0) {
                  var138 = null;
               } else {
                  int var123 = var35.getInt();
                  EnumMap var125 = new EnumMap<>(HI.b.class);
                  int var128 = 0;

                  while (var128 < var123) {
                     var128++;
                     Enum var133 = HI.b.class.getEnumConstants()[var35.getInt()];
                     Class<HI.l> var13 = HI.l.class;
                     Object var137;
                     if (Intrinsics.areEqual(HI.l.class, HI.e.class)) {
                        var137 = HI.e.values()[var35.getInt()];
                     } else if (Intrinsics.areEqual(var13, HI.l.class)) {
                        var137 = IH.l(var35);
                     } else {
                        if (!Intrinsics.areEqual(var13, HI.d.class)) {
                           throw new IllegalArgumentException(Intrinsics.stringPlus("Unsupported enum map value type: ", HI.l.class));
                        }

                        var137 = IH.r(var35);
                     }

                     if (var137 == null) {
                        throw new NullPointerException("null cannot be cast to non-null type org.valkyrienskies.physics_api.joints.D6Joint.LinearLimitPair");
                     }

                     HI.l var112 = (HI.l)var137;
                     var125.put(var133, var112);
                  }

                  var138 = var125;
               }

               EnumMap var113 = var138;
               HI.a var117 = II.a(var35.get()) ? IH.m(var35) : null;
               HI.i var120 = II.a(var35.get()) ? IH.n(var35) : null;
               HI.j var124 = II.a(var35.get()) ? IH.o(var35) : null;
               EnumMap var140;
               if (var35.get() == 0) {
                  var140 = null;
               } else {
                  int var126 = var35.getInt();
                  EnumMap var129 = new EnumMap<>(HI.c.class);
                  int var134 = 0;

                  while (var134 < var126) {
                     var134++;
                     Enum var20 = HI.c.class.getEnumConstants()[var35.getInt()];
                     Class<HI.d> var21 = HI.d.class;
                     Object var139;
                     if (Intrinsics.areEqual(HI.d.class, HI.e.class)) {
                        var139 = HI.e.values()[var35.getInt()];
                     } else if (Intrinsics.areEqual(var21, HI.l.class)) {
                        var139 = IH.l(var35);
                     } else {
                        if (!Intrinsics.areEqual(var21, HI.d.class)) {
                           throw new IllegalArgumentException(Intrinsics.stringPlus("Unsupported enum map value type: ", HI.d.class));
                        }

                        var139 = IH.r(var35);
                     }

                     if (var139 == null) {
                        throw new NullPointerException("null cannot be cast to non-null type org.valkyrienskies.physics_api.joints.D6Joint.D6JointDrive");
                     }

                     HI.d var135 = (HI.d)var139;
                     var129.put(var20, var135);
                  }

                  var140 = var129;
               }

               EnumMap var127 = var140;
               HI.f var141;
               if (II.a(var35.get())) {
                  ByteBuffer var130 = var35;
                  Hf var24 = IH.j(var130);
                  boolean var131 = II.a(var130.get());
                  var141 = new HI.f(var24, var131);
               } else {
                  var141 = null;
               }

               HI.f var132 = var141;
               HI.g var25 = II.a(var35.get()) ? IH.s(var35) : null;
               var142 = new HI(
                  var9,
                  var58,
                  var10,
                  var73,
                  (HP)var43.getFirst(),
                  var11,
                  var12,
                  var113,
                  var117,
                  var120,
                  var124,
                  var127,
                  var132,
                  var25,
                  ((Number)var43.getSecond()).doubleValue()
               );
               break;
            default:
               throw new IllegalArgumentException("Unknown JointType " + var5);
         }

         HN var33 = var142;
         HM var34 = new HM(var4, var33);
         var1.add(var34);
      }

      return var1;
   }

   public static void a(HM var0, ByteBuffer var1) {
      var1.putInt(var0.a);
      a(var0.b, var1);
   }

   private static HM d(ByteBuffer var0) {
      int var1 = var0.getInt();
      HN var2 = c(var0);
      return new HM(var1, var2);
   }

   public static void a(Vector3dc var0, ByteBuffer var1) {
      var1.putDouble(var0.x());
      var1.putDouble(var0.y());
      var1.putDouble(var0.z());
   }

   public static Vector3d b(ByteBuffer var0) {
      return new Vector3d(var0.getDouble(), var0.getDouble(), var0.getDouble());
   }

   private static void a(Quaterniondc var0, ByteBuffer var1) {
      var1.putDouble(var0.w());
      var1.putDouble(var0.x());
      var1.putDouble(var0.y());
      var1.putDouble(var0.z());
   }

   private static Quaterniond e(ByteBuffer var0) {
      double var1 = var0.getDouble();
      double var3 = var0.getDouble();
      double var5 = var0.getDouble();
      double var7 = var0.getDouble();
      return new Quaterniond(var3, var5, var7, var1);
   }
}
