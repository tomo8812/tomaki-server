package org.valkyrienskies.mod.common.util

import net.minecraft.class_1923
import net.minecraft.class_2338
import net.minecraft.class_2350
import net.minecraft.class_2374
import net.minecraft.class_238
import net.minecraft.class_2382
import net.minecraft.class_243
import net.minecraft.class_4587
import net.minecraft.class_4587.class_4665
import org.joml.Matrix3f
import org.joml.Matrix4d
import org.joml.Matrix4dc
import org.joml.Matrix4fc
import org.joml.Quaterniondc
import org.joml.Quaternionf
import org.joml.Vector2i
import org.joml.Vector2ic
import org.joml.Vector3d
import org.joml.Vector3dc
import org.joml.Vector3f
import org.joml.Vector3i
import org.joml.Vector3ic
import org.joml.Vector4f
import org.joml.primitives.AABBd
import org.joml.primitives.AABBdc

public fun Vector3i.set(v: class_2382): Vector3i {
   `$this$set`.x = v.method_10263()
   `$this$set`.y = v.method_10264()
   `$this$set`.z = v.method_10260()
   return `$this$set`
}

public fun Vector3d.set(v: class_2382): Vector3d {
   `$this$set`.x = v.method_10263()
   `$this$set`.y = v.method_10264()
   `$this$set`.z = v.method_10260()
   return `$this$set`
}

public fun Vector3f.set(v: class_2382): Vector3f {
   `$this$set`.x = v.method_10263()
   `$this$set`.y = v.method_10264()
   `$this$set`.z = v.method_10260()
   return `$this$set`
}

public fun Vector3d.set(v: class_2374): Vector3d {
   `$this$set`.x = v.method_10216()
   `$this$set`.y = v.method_10214()
   `$this$set`.z = v.method_10215()
   return `$this$set`
}

public fun class_2382.toDoubles(): class_243 {
   return class_243(`$this$toDoubles`.method_10263(), `$this$toDoubles`.method_10264(), `$this$toDoubles`.method_10260())
}

public fun AABBd.set(v: class_238): AABBd {
   `$this$set`.minX = v.field_1323
   `$this$set`.minY = v.field_1322
   `$this$set`.minZ = v.field_1321
   `$this$set`.maxX = v.field_1320
   `$this$set`.maxY = v.field_1325
   `$this$set`.maxZ = v.field_1324
   return `$this$set`
}

public fun Vector3ic.toBlockPos(): class_2338 {
   return class_2338(`$this$toBlockPos`.x(), `$this$toBlockPos`.y(), `$this$toBlockPos`.z())
}

public fun Vector3dc.toMinecraft(): class_243 {
   return class_243(`$this$toMinecraft`.x(), `$this$toMinecraft`.y(), `$this$toMinecraft`.z())
}

public fun Matrix4d.mul(m: Matrix4fc): Matrix4d {
   val var10000: Matrix4d = `$this$mul`.mul(m, `$this$mul`)
   return var10000
}

public fun AABBdc.toMinecraft(): class_238 {
   return class_238(
      `$this$toMinecraft`.minX(),
      `$this$toMinecraft`.minY(),
      `$this$toMinecraft`.minZ(),
      `$this$toMinecraft`.maxX(),
      `$this$toMinecraft`.maxY(),
      `$this$toMinecraft`.maxZ()
   )
}

public fun class_238.toJOML(): AABBd {
   return set(AABBd(), `$this$toJOML`)
}

public fun Vector2ic.toMinecraft(): class_1923 {
   return class_1923(`$this$toMinecraft`.x(), `$this$toMinecraft`.y())
}

public fun class_1923.toJOML(): Vector2i {
   return set(Vector2i(), `$this$toJOML`)
}

public fun class_243.toJOML(): Vector3d {
   return set(Vector3d(), `$this$toJOML`)
}

public fun Vector3d.set(v: class_243): Vector3d {
   `$this$set`.x = v.field_1352
   `$this$set`.y = v.field_1351
   `$this$set`.z = v.field_1350
   return `$this$set`
}

public fun Vector2i.set(pos: class_1923): Vector2i {
   `$this$set`.x = pos.field_9181
   `$this$set`.y = pos.field_9180
   return `$this$set`
}

@JvmOverloads
public fun Matrix4dc.transformDirection(v: class_2382, dest: Vector3d = Vector3d()): Vector3d {
   return `$this$transformDirection`.transformDirection(dest.set((double)v.method_10263(), (double)v.method_10264(), (double)v.method_10260()))
}

@JvmOverloads
public fun Matrix4dc.transformDirection(dir: class_2350, dest: Vector3d = Vector3d()): Vector3d {
   val var10001: class_2382 = dir.method_10163()
   return transformDirection(`$this$transformDirection`, var10001, dest)
}

public fun Matrix4dc.transform(v: Vector4f): Vector4f {
   v.set(
      (float)(
         `$this$transform`.m00() * (double)v.x()
            + `$this$transform`.m01() * (double)v.y()
            + `$this$transform`.m02() * (double)v.z()
            + `$this$transform`.m03() * (double)v.w()
      ),
      (float)(
         `$this$transform`.m10() * (double)v.x()
            + `$this$transform`.m11() * (double)v.y()
            + `$this$transform`.m12() * (double)v.z()
            + `$this$transform`.m13() * (double)v.w()
      ),
      (float)(
         `$this$transform`.m20() * (double)v.x()
            + `$this$transform`.m21() * (double)v.y()
            + `$this$transform`.m22() * (double)v.z()
            + `$this$transform`.m23() * (double)v.w()
      ),
      (float)(
         `$this$transform`.m30() * (double)v.x()
            + `$this$transform`.m31() * (double)v.y()
            + `$this$transform`.m32() * (double)v.z()
            + `$this$transform`.m33() * (double)v.w()
      )
   )
   return v
}

public fun Matrix4dc.transformPosition(v: class_243): class_243 {
   val var10000: Vector3d = `$this$transformPosition`.transformPosition(toJOML(v))
   return toMinecraft(var10000)
}

public fun class_4587.multiply(modelTransform: Matrix4dc, normalTransform: Quaterniondc): class_4587 {
   val last: class_4665 = `$this$multiply`.method_23760()
   val newPose: Matrix4d = Matrix4d().set(last.method_23761()).mul(modelTransform)
   val newNormal: Matrix3f = last.method_23762().mul(Matrix3f().set(normalTransform))
   last.method_23761().set(newPose)
   last.method_23762().set(newNormal)
   return `$this$multiply`
}

public fun class_4587.multiply(modelTransform: Matrix4dc): class_4587 {
   val last: class_4665 = `$this$multiply`.method_23760()
   last.method_23761().set(Matrix4d().set(last.method_23761()).mul(modelTransform))
   return `$this$multiply`
}

public fun class_2382.toJOML(): Vector3i {
   return set(Vector3i(), `$this$toJOML`)
}

public fun class_2382.toJOMLD(): Vector3d {
   return set(Vector3d(), `$this$toJOMLD`)
}

public fun class_2382.toJOMLF(): Vector3f {
   return set(Vector3f(), `$this$toJOMLF`)
}

public fun class_2374.toJOML(): Vector3d {
   return set(Vector3d(), `$this$toJOML`)
}

public fun Quaterniondc.toFloat(): Quaternionf {
   return Quaternionf(`$this$toFloat`.x(), `$this$toFloat`.y(), `$this$toFloat`.z(), `$this$toFloat`.w())
}

@JvmOverloads
fun Matrix4dc.transformDirection(v: class_2382): Vector3d {
   transformDirection$default(`$this$transformDirection`, v, null, 2, null)
}

@JvmOverloads
fun Matrix4dc.transformDirection(dir: class_2350): Vector3d {
   transformDirection$default(`$this$transformDirection`, dir, null, 2, null)
}
