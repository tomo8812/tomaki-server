package org.valkyrienskies.core.impl.shadow;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.databind.module.SimpleModule;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;
import org.joml.Matrix3d;
import org.joml.Matrix3dc;
import org.joml.Matrix3f;
import org.joml.Matrix3fc;
import org.joml.Matrix4d;
import org.joml.Matrix4dc;
import org.joml.Matrix4f;
import org.joml.Matrix4fc;
import org.joml.Quaterniond;
import org.joml.Quaterniondc;
import org.joml.Quaternionf;
import org.joml.Quaternionfc;
import org.joml.Vector2d;
import org.joml.Vector2dc;
import org.joml.Vector2f;
import org.joml.Vector2fc;
import org.joml.Vector2i;
import org.joml.Vector2ic;
import org.joml.Vector3d;
import org.joml.Vector3dc;
import org.joml.Vector3f;
import org.joml.Vector3fc;
import org.joml.Vector3i;
import org.joml.Vector3ic;
import org.joml.primitives.AABBd;
import org.joml.primitives.AABBdc;
import org.joml.primitives.AABBf;
import org.joml.primitives.AABBfc;
import org.joml.primitives.AABBi;
import org.joml.primitives.AABBic;
import org.valkyrienskies.core.api.ships.properties.ShipInertiaData;
import org.valkyrienskies.core.impl.game.ships.ShipInertiaDataImpl;

public final class GF extends SimpleModule {
   public GF() {
      GF var1 = this
      SimpleModule var2
      Intrinsics.checkNotNullExpressionValue((var2 = this).addAbstractTypeMapping(Vector2ic.class, Vector2i.class), "")
      Intrinsics.checkNotNullExpressionValue((var2 = var1).setMixInAnnotation(Vector2i.class, GF.a.class), "")
      var1 = this
      Intrinsics.checkNotNullExpressionValue((var2 = this).addAbstractTypeMapping(Vector2fc.class, Vector2f.class), "")
      Intrinsics.checkNotNullExpressionValue((var2 = var1).setMixInAnnotation(Vector2f.class, GF.a.class), "")
      var1 = this
      Intrinsics.checkNotNullExpressionValue((var2 = this).addAbstractTypeMapping(Vector2dc.class, Vector2d.class), "")
      Intrinsics.checkNotNullExpressionValue((var2 = var1).setMixInAnnotation(Vector2d.class, GF.a.class), "")
      var1 = this
      Intrinsics.checkNotNullExpressionValue((var2 = this).addAbstractTypeMapping(Vector3ic.class, Vector3i.class), "")
      Intrinsics.checkNotNullExpressionValue((var2 = var1).setMixInAnnotation(Vector3i.class, GF.a.class), "")
      var1 = this
      Intrinsics.checkNotNullExpressionValue((var2 = this).addAbstractTypeMapping(Vector3fc.class, Vector3f.class), "")
      Intrinsics.checkNotNullExpressionValue((var2 = var1).setMixInAnnotation(Vector3f.class, GF.a.class), "")
      var1 = this
      Intrinsics.checkNotNullExpressionValue((var2 = this).addAbstractTypeMapping(Vector3dc.class, Vector3d.class), "")
      Intrinsics.checkNotNullExpressionValue((var2 = var1).setMixInAnnotation(Vector3d.class, GF.a.class), "")
      var1 = this
      Intrinsics.checkNotNullExpressionValue((var2 = this).addAbstractTypeMapping(Quaternionfc.class, Quaternionf.class), "")
      Intrinsics.checkNotNullExpressionValue((var2 = var1).setMixInAnnotation(Quaternionf.class, GF.a.class), "")
      var1 = this
      Intrinsics.checkNotNullExpressionValue((var2 = this).addAbstractTypeMapping(Quaterniondc.class, Quaterniond.class), "")
      Intrinsics.checkNotNullExpressionValue((var2 = var1).setMixInAnnotation(Quaterniond.class, GF.a.class), "")
      var1 = this
      Intrinsics.checkNotNullExpressionValue((var2 = this).addAbstractTypeMapping(Matrix4fc.class, Matrix4f.class), "")
      Intrinsics.checkNotNullExpressionValue((var2 = var1).setMixInAnnotation(Matrix4f.class, GF.a.class), "")
      var1 = this
      Intrinsics.checkNotNullExpressionValue((var2 = this).addAbstractTypeMapping(Matrix4dc.class, Matrix4d.class), "")
      Intrinsics.checkNotNullExpressionValue((var2 = var1).setMixInAnnotation(Matrix4d.class, GF.a.class), "")
      var1 = this
      Intrinsics.checkNotNullExpressionValue((var2 = this).addAbstractTypeMapping(Matrix3fc.class, Matrix3f.class), "")
      Intrinsics.checkNotNullExpressionValue((var2 = var1).setMixInAnnotation(Matrix3f.class, GF.a.class), "")
      var1 = this
      Intrinsics.checkNotNullExpressionValue((var2 = this).addAbstractTypeMapping(Matrix3dc.class, Matrix3d.class), "")
      Intrinsics.checkNotNullExpressionValue((var2 = var1).setMixInAnnotation(Matrix3d.class, GF.a.class), "")
      var1 = this
      Intrinsics.checkNotNullExpressionValue((var2 = this).addAbstractTypeMapping(AABBic.class, AABBi.class), "")
      Intrinsics.checkNotNullExpressionValue((var2 = var1).setMixInAnnotation(AABBi.class, GF.a.class), "")
      var1 = this
      Intrinsics.checkNotNullExpressionValue((var2 = this).addAbstractTypeMapping(AABBfc.class, AABBf.class), "")
      Intrinsics.checkNotNullExpressionValue((var2 = var1).setMixInAnnotation(AABBf.class, GF.a.class), "")
      var1 = this
      Intrinsics.checkNotNullExpressionValue((var2 = this).addAbstractTypeMapping(AABBdc.class, AABBd.class), "")
      Intrinsics.checkNotNullExpressionValue((var2 = var1).setMixInAnnotation(AABBd.class, GF.a.class), "")
      var1 = this
      Intrinsics.checkNotNullExpressionValue((var2 = this).addAbstractTypeMapping(ShipInertiaData.class, ShipInertiaDataImpl.class), "")
      Intrinsics.checkNotNullExpressionValue((var2 = var1).setMixInAnnotation(ShipInertiaDataImpl.class, GF.a.class), "")
   }

   // $VF: synthetic method
   private final <A, B extends A> void a() {
      SimpleModule var1
      SimpleModule var10000 = var1 = this
      Intrinsics.reifiedOperationMarker(4, "A")
      Class var10001 = Object.class
      Intrinsics.reifiedOperationMarker(4, "B")
      Intrinsics.checkNotNullExpressionValue(var10000.addAbstractTypeMapping(var10001, Object.class), "")
      var10000 = var1 = this
      Intrinsics.reifiedOperationMarker(4, "B")
      Intrinsics.checkNotNullExpressionValue(var10000.setMixInAnnotation(Object.class, GF.a.class), "")
   }

   @JsonAutoDetect(
      getterVisibility = JsonAutoDetect.Visibility.NONE,
      isGetterVisibility = JsonAutoDetect.Visibility.NONE,
      setterVisibility = JsonAutoDetect.Visibility.NONE,
      fieldVisibility = JsonAutoDetect.Visibility.ANY
   )
   @Metadata(
      mv = {1, 8, 0},
      k = 1,
      xi = 48,
      d1 = "\u0000\f\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\bÃ\u0002\u0018\u00002\u00020\u0001B\t\b\u0002¢\u0006\u0004\b\u0002\u0010\u0003",
      d2 = {"Lorg/valkyrienskies/core/impl/shadow/GF$a;", "", "<init>", "()V"}
   )
   static final class a {
      public static final GF.a INSTANCE = new GF.a();

      private a() {
      }
   }
}
