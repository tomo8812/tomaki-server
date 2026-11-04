package org.joml.primitives;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.text.NumberFormat;
import org.joml.Math;
import org.joml.Matrix4fc;
import org.joml.Options;
import org.joml.Runtime;
import org.joml.Vector2f;
import org.joml.Vector3d;
import org.joml.Vector3f;
import org.joml.Vector3fc;

public class AABBf implements Externalizable, AABBfc {
   public float minX = Float.POSITIVE_INFINITY;
   public float minY = Float.POSITIVE_INFINITY;
   public float minZ = Float.POSITIVE_INFINITY;
   public float maxX = Float.NEGATIVE_INFINITY;
   public float maxY = Float.NEGATIVE_INFINITY;
   public float maxZ = Float.NEGATIVE_INFINITY;

   public AABBf() {
   }

   public AABBf(AABBf source) {
      this.minX = source.minX;
      this.minY = source.minY;
      this.minZ = source.minZ;
      this.maxX = source.maxX;
      this.maxY = source.maxY;
      this.maxZ = source.maxZ;
   }

   public AABBf(Vector3fc min, Vector3fc max) {
      this.minX = min.x();
      this.minY = min.y();
      this.minZ = min.z();
      this.maxX = max.x();
      this.maxY = max.y();
      this.maxZ = max.z();
   }

   public AABBf(float minX, float minY, float minZ, float maxX, float maxY, float maxZ) {
      this.minX = minX;
      this.minY = minY;
      this.minZ = minZ;
      this.maxX = maxX;
      this.maxY = maxY;
      this.maxZ = maxZ;
   }

   public AABBf set(AABBfc source) {
      this.minX = source.minX();
      this.minY = source.minY();
      this.minZ = source.minZ();
      this.maxX = source.maxX();
      this.maxY = source.maxY();
      this.maxZ = source.maxZ();
      return this;
   }

   private AABBf validate() {
      if (!this.isValid()) {
         this.minX = Float.POSITIVE_INFINITY;
         this.minY = Float.POSITIVE_INFINITY;
         this.minZ = Float.POSITIVE_INFINITY;
         this.maxX = Float.NEGATIVE_INFINITY;
         this.maxY = Float.NEGATIVE_INFINITY;
         this.maxZ = Float.NEGATIVE_INFINITY;
      }

      return this;
   }

   public float minX() {
      return this.minX;
   }

   public float minY() {
      return this.minY;
   }

   public float minZ() {
      return this.minZ;
   }

   public float maxX() {
      return this.maxX;
   }

   public float maxY() {
      return this.maxY;
   }

   public float maxZ() {
      return this.maxZ;
   }

   public boolean isValid() {
      return this.minX < this.maxX && this.minY < this.maxY && this.minZ < this.maxZ;
   }

   public AABBf setMin(float minX, float minY, float minZ) {
      this.minX = minX;
      this.minY = minY;
      this.minZ = minZ;
      return this;
   }

   public AABBf setMax(float maxX, float maxY, float maxZ) {
      this.maxX = maxX;
      this.maxY = maxY;
      this.maxZ = maxZ;
      return this;
   }

   public AABBf setMin(Vector3fc min) {
      return this.setMin(min.x(), min.y(), min.z());
   }

   public AABBf setMax(Vector3fc max) {
      return this.setMax(max.x(), max.y(), max.z());
   }

   public float getMax(int component) throws IllegalArgumentException {
      switch (component) {
         case 0:
            return this.maxX;
         case 1:
            return this.maxY;
         case 2:
            return this.maxZ;
         default:
            throw new IllegalArgumentException();
      }
   }

   public float getMin(int component) throws IllegalArgumentException {
      switch (component) {
         case 0:
            return this.minX;
         case 1:
            return this.minY;
         case 2:
            return this.minZ;
         default:
            throw new IllegalArgumentException();
      }
   }

   public Vector3f center(Vector3f dest) {
      return dest.set(this.minX + (this.maxX - this.minX) / 2.0F, this.minY + (this.maxY - this.minY) / 2.0F, this.minZ + (this.maxZ - this.minZ) / 2.0F);
   }

   public Vector3d center(Vector3d dest) {
      return dest.set(this.minX + (this.maxX - this.minX) / 2.0, this.minY + (this.maxY - this.minY) / 2.0, this.minZ + (this.maxZ - this.minZ) / 2.0);
   }

   public Vector3d extent(Vector3d dest) {
      return dest.set((this.maxX - this.minX) / 2.0, (this.maxY - this.minY) / 2.0, (this.maxZ - this.minZ) / 2.0);
   }

   public Vector3f extent(Vector3f dest) {
      return dest.set((this.maxX - this.minX) / 2.0F, (this.maxY - this.minY) / 2.0F, (this.maxZ - this.minZ) / 2.0F);
   }

   public float lengthX() {
      return this.maxX - this.minX;
   }

   public float lengthY() {
      return this.maxY - this.minY;
   }

   public float lengthZ() {
      return this.maxZ - this.minZ;
   }

   public Vector3f getSize(Vector3f dest) {
      return dest.set(this.lengthX(), this.lengthY(), this.lengthZ());
   }

   public Vector3d getSize(Vector3d dest) {
      return dest.set(this.lengthX(), this.lengthY(), this.lengthZ());
   }

   public AABBf union(float x, float y, float z) {
      return this.union(x, y, z, this);
   }

   public AABBf union(Vector3fc p) {
      return this.union(p.x(), p.y(), p.z(), this);
   }

   public AABBf union(float x, float y, float z, AABBf dest) {
      dest.minX = this.minX < x ? this.minX : x;
      dest.minY = this.minY < y ? this.minY : y;
      dest.minZ = this.minZ < z ? this.minZ : z;
      dest.maxX = this.maxX > x ? this.maxX : x;
      dest.maxY = this.maxY > y ? this.maxY : y;
      dest.maxZ = this.maxZ > z ? this.maxZ : z;
      return dest;
   }

   public AABBf union(Vector3fc p, AABBf dest) {
      return this.union(p.x(), p.y(), p.z(), dest);
   }

   public AABBf union(AABBf other) {
      return this.union(other, this);
   }

   public AABBf union(AABBf other, AABBf dest) {
      dest.minX = this.minX < other.minX ? this.minX : other.minX;
      dest.minY = this.minY < other.minY ? this.minY : other.minY;
      dest.minZ = this.minZ < other.minZ ? this.minZ : other.minZ;
      dest.maxX = this.maxX > other.maxX ? this.maxX : other.maxX;
      dest.maxY = this.maxY > other.maxY ? this.maxY : other.maxY;
      dest.maxZ = this.maxZ > other.maxZ ? this.maxZ : other.maxZ;
      return dest;
   }

   public AABBf correctBounds() {
      if (this.minX > this.maxX) {
         float tmp = this.minX;
         this.minX = this.maxX;
         this.maxX = tmp;
      }

      if (this.minY > this.maxY) {
         float tmp = this.minY;
         this.minY = this.maxY;
         this.maxY = tmp;
      }

      if (this.minZ > this.maxZ) {
         float tmp = this.minZ;
         this.minZ = this.maxZ;
         this.maxZ = tmp;
      }

      return this;
   }

   public AABBf translate(Vector3fc xyz) {
      return this.translate(xyz.x(), xyz.y(), xyz.z(), this);
   }

   public AABBf translate(Vector3fc xyz, AABBf dest) {
      return this.translate(xyz.x(), xyz.y(), xyz.z(), dest);
   }

   public AABBf translate(float x, float y, float z) {
      return this.translate(x, y, z, this);
   }

   public AABBf translate(float x, float y, float z, AABBf dest) {
      dest.minX = this.minX + x;
      dest.minY = this.minY + y;
      dest.minZ = this.minZ + z;
      dest.maxX = this.maxX + x;
      dest.maxY = this.maxY + y;
      dest.maxZ = this.maxZ + z;
      return dest;
   }

   public AABBf intersection(AABBfc other, AABBf dest) {
      dest.minX = Math.max(this.minX, other.minX());
      dest.minY = Math.max(this.minY, other.minY());
      dest.minZ = Math.max(this.minZ, other.minZ());
      dest.maxX = Math.min(this.maxX, other.maxX());
      dest.maxY = Math.min(this.maxY, other.maxY());
      dest.maxZ = Math.min(this.maxZ, other.maxZ());
      return dest.validate();
   }

   public AABBf intersection(AABBfc other) {
      return this.intersection(other, this);
   }

   public boolean containsAABB(AABBdc aabb) {
      return aabb.minX() >= this.minX
         && aabb.maxX() <= this.maxX
         && aabb.minY() >= this.minY
         && aabb.maxY() <= this.maxY
         && aabb.minZ() >= this.minZ
         && aabb.maxZ() <= this.maxZ;
   }

   public boolean containsAABB(AABBfc aabb) {
      return aabb.minX() >= this.minX
         && aabb.maxX() <= this.maxX
         && aabb.minY() >= this.minY
         && aabb.maxY() <= this.maxY
         && aabb.minZ() >= this.minZ
         && aabb.maxZ() <= this.maxZ;
   }

   public boolean containsAABB(AABBic aabb) {
      return aabb.minX() >= this.minX
         && aabb.maxX() <= this.maxX
         && aabb.minY() >= this.minY
         && aabb.maxY() <= this.maxY
         && aabb.minZ() >= this.minZ
         && aabb.maxZ() <= this.maxZ;
   }

   public boolean containsPoint(float x, float y, float z) {
      return x > this.minX && y > this.minY && z > this.minZ && x < this.maxX && y < this.maxY && z < this.maxZ;
   }

   public boolean containsPoint(Vector3fc point) {
      return this.containsPoint(point.x(), point.y(), point.z());
   }

   public boolean intersectsPlane(float a, float b, float c, float d) {
      return Intersectionf.testAabPlane(this.minX, this.minY, this.minZ, this.maxX, this.maxY, this.maxZ, a, b, c, d);
   }

   public boolean intersectsPlane(Planef plane) {
      return Intersectionf.testAabPlane(this, plane);
   }

   public boolean intersectsAABB(AABBfc other) {
      return this.maxX >= other.minX()
         && this.maxY >= other.minY()
         && this.maxZ >= other.minZ()
         && this.minX <= other.maxX()
         && this.minY <= other.maxY()
         && this.minZ <= other.maxZ();
   }

   public boolean intersectsSphere(float centerX, float centerY, float centerZ, float radiusSquared) {
      return Intersectionf.testAabSphere(this.minX, this.minY, this.minZ, this.maxX, this.maxY, this.maxZ, centerX, centerY, centerZ, radiusSquared);
   }

   public boolean intersectsSphere(Spheref sphere) {
      return Intersectionf.testAabSphere(this, sphere);
   }

   public boolean intersectsRay(float originX, float originY, float originZ, float dirX, float dirY, float dirZ) {
      return Intersectionf.testRayAab(originX, originY, originZ, dirX, dirY, dirZ, this.minX, this.minY, this.minZ, this.maxX, this.maxY, this.maxZ);
   }

   public boolean intersectsRay(Rayf ray) {
      return Intersectionf.testRayAab(ray, this);
   }

   public boolean intersectsRay(float originX, float originY, float originZ, float dirX, float dirY, float dirZ, Vector2f result) {
      return Intersectionf.intersectRayAab(
         originX, originY, originZ, dirX, dirY, dirZ, this.minX, this.minY, this.minZ, this.maxX, this.maxY, this.maxZ, result
      );
   }

   public boolean intersectsRay(Rayf ray, Vector2f result) {
      return Intersectionf.intersectRayAab(ray, this, result);
   }

   public int intersectsLineSegment(float p0X, float p0Y, float p0Z, float p1X, float p1Y, float p1Z, Vector2f result) {
      return Intersectionf.intersectLineSegmentAab(p0X, p0Y, p0Z, p1X, p1Y, p1Z, this.minX, this.minY, this.minZ, this.maxX, this.maxY, this.maxZ, result);
   }

   public int intersectsLineSegment(LineSegmentf lineSegment, Vector2f result) {
      return Intersectionf.intersectLineSegmentAab(lineSegment, this, result);
   }

   public AABBf transform(Matrix4fc m) {
      return this.transform(m, this);
   }

   public AABBf transform(Matrix4fc m, AABBf dest) {
      float dx = this.maxX - this.minX;
      float dy = this.maxY - this.minY;
      float dz = this.maxZ - this.minZ;
      float minx = Float.POSITIVE_INFINITY;
      float miny = Float.POSITIVE_INFINITY;
      float minz = Float.POSITIVE_INFINITY;
      float maxx = Float.NEGATIVE_INFINITY;
      float maxy = Float.NEGATIVE_INFINITY;
      float maxz = Float.NEGATIVE_INFINITY;

      for (int i = 0; i < 8; i++) {
         float x = this.minX + (i & 1) * dx;
         float y = this.minY + (i >> 1 & 1) * dy;
         float z = this.minZ + (i >> 2 & 1) * dz;
         float tx = m.m00() * x + m.m10() * y + m.m20() * z + m.m30();
         float ty = m.m01() * x + m.m11() * y + m.m21() * z + m.m31();
         float tz = m.m02() * x + m.m12() * y + m.m22() * z + m.m32();
         minx = Math.min(tx, minx);
         miny = Math.min(ty, miny);
         minz = Math.min(tz, minz);
         maxx = Math.max(tx, maxx);
         maxy = Math.max(ty, maxy);
         maxz = Math.max(tz, maxz);
      }

      dest.minX = minx;
      dest.minY = miny;
      dest.minZ = minz;
      dest.maxX = maxx;
      dest.maxY = maxy;
      dest.maxZ = maxz;
      return dest;
   }

   public int hashCode() {
      int prime = 31;
      int result = 1;
      result = 31 * result + Float.floatToIntBits(this.maxX);
      result = 31 * result + Float.floatToIntBits(this.maxY);
      result = 31 * result + Float.floatToIntBits(this.maxZ);
      result = 31 * result + Float.floatToIntBits(this.minX);
      result = 31 * result + Float.floatToIntBits(this.minY);
      return 31 * result + Float.floatToIntBits(this.minZ);
   }

   public boolean equals(Object obj) {
      if (this == obj) {
         return true;
      } else if (obj == null) {
         return false;
      } else if (this.getClass() != obj.getClass()) {
         return false;
      } else {
         AABBf other = (AABBf)obj;
         if (Float.floatToIntBits(this.maxX) != Float.floatToIntBits(other.maxX)) {
            return false;
         } else if (Float.floatToIntBits(this.maxY) != Float.floatToIntBits(other.maxY)) {
            return false;
         } else if (Float.floatToIntBits(this.maxZ) != Float.floatToIntBits(other.maxZ)) {
            return false;
         } else if (Float.floatToIntBits(this.minX) != Float.floatToIntBits(other.minX)) {
            return false;
         } else {
            return Float.floatToIntBits(this.minY) != Float.floatToIntBits(other.minY)
               ? false
               : Float.floatToIntBits(this.minZ) == Float.floatToIntBits(other.minZ);
         }
      }
   }

   public String toString() {
      return Runtime.formatNumbers(this.toString(Options.NUMBER_FORMAT));
   }

   public String toString(NumberFormat formatter) {
      return "("
         + Runtime.format(this.minX, formatter)
         + " "
         + Runtime.format(this.minY, formatter)
         + " "
         + Runtime.format(this.minZ, formatter)
         + ") < ("
         + Runtime.format(this.maxX, formatter)
         + " "
         + Runtime.format(this.maxY, formatter)
         + " "
         + Runtime.format(this.maxZ, formatter)
         + ")";
   }

   public void writeExternal(ObjectOutput out) throws IOException {
      out.writeFloat(this.minX);
      out.writeFloat(this.minY);
      out.writeFloat(this.minZ);
      out.writeFloat(this.maxX);
      out.writeFloat(this.maxY);
      out.writeFloat(this.maxZ);
   }

   public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
      this.minX = in.readFloat();
      this.minY = in.readFloat();
      this.minZ = in.readFloat();
      this.maxX = in.readFloat();
      this.maxY = in.readFloat();
      this.maxZ = in.readFloat();
   }
}
