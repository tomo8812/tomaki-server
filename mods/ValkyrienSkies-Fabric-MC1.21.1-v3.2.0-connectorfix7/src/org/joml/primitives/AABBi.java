package org.joml.primitives;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import org.joml.Math;
import org.joml.Matrix4fc;
import org.joml.Vector2f;
import org.joml.Vector3d;
import org.joml.Vector3f;
import org.joml.Vector3fc;
import org.joml.Vector3i;
import org.joml.Vector3ic;

public class AABBi implements Externalizable, AABBic {
   public int minX = Integer.MAX_VALUE;
   public int minY = Integer.MAX_VALUE;
   public int minZ = Integer.MAX_VALUE;
   public int maxX = Integer.MIN_VALUE;
   public int maxY = Integer.MIN_VALUE;
   public int maxZ = Integer.MIN_VALUE;

   public AABBi() {
   }

   public AABBi(AABBic source) {
      this.minX = source.minX();
      this.minY = source.minY();
      this.minZ = source.minZ();
      this.maxX = source.maxX();
      this.maxY = source.maxY();
      this.maxZ = source.maxZ();
   }

   public AABBi(Vector3ic min, Vector3ic max) {
      this.minX = min.x();
      this.minY = min.y();
      this.minZ = min.z();
      this.maxX = max.x();
      this.maxY = max.y();
      this.maxZ = max.z();
   }

   public AABBi(int minX, int minY, int minZ, int maxX, int maxY, int maxZ) {
      this.minX = minX;
      this.minY = minY;
      this.minZ = minZ;
      this.maxX = maxX;
      this.maxY = maxY;
      this.maxZ = maxZ;
   }

   public int minX() {
      return this.minX;
   }

   public int minY() {
      return this.minY;
   }

   public int minZ() {
      return this.minZ;
   }

   public int maxX() {
      return this.maxX;
   }

   public int maxY() {
      return this.maxY;
   }

   public int maxZ() {
      return this.maxZ;
   }

   public AABBi setMin(int minX, int minY, int minZ) {
      this.minX = minX;
      this.minY = minY;
      this.minZ = minZ;
      return this;
   }

   public AABBi setMax(int maxX, int maxY, int maxZ) {
      this.maxX = maxX;
      this.maxY = maxY;
      this.maxZ = maxZ;
      return this;
   }

   public AABBi set(AABBic source) {
      this.minX = source.minX();
      this.minY = source.minY();
      this.minZ = source.minZ();
      this.maxX = source.maxX();
      this.maxY = source.maxY();
      this.maxZ = source.maxZ();
      return this;
   }

   private AABBi validate() {
      if (!this.isValid()) {
         this.minX = Integer.MAX_VALUE;
         this.minY = Integer.MAX_VALUE;
         this.minZ = Integer.MAX_VALUE;
         this.maxX = Integer.MIN_VALUE;
         this.maxY = Integer.MIN_VALUE;
         this.maxZ = Integer.MIN_VALUE;
      }

      return this;
   }

   public boolean isValid() {
      return this.minX < this.maxX && this.minY < this.maxY && this.minZ < this.maxZ;
   }

   public AABBi setMin(Vector3ic min) {
      return this.setMin(min.x(), min.y(), min.z());
   }

   public AABBi setMax(Vector3ic max) {
      return this.setMax(max.x(), max.y(), max.z());
   }

   public int getMax(int component) throws IllegalArgumentException {
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

   public int getMin(int component) throws IllegalArgumentException {
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

   public int lengthX() {
      return this.maxX - this.minX;
   }

   public int lengthY() {
      return this.maxY - this.minY;
   }

   public int lengthZ() {
      return this.maxZ - this.minZ;
   }

   public Vector3i getSize(Vector3i dest) {
      return dest.set(this.maxX - this.minX, this.maxY - this.minY, this.maxZ - this.minZ);
   }

   public Vector3f getSize(Vector3f dest) {
      return dest.set(this.maxX - this.minX, this.maxY - this.minY, this.maxZ - this.minZ);
   }

   public Vector3d getSize(Vector3d dest) {
      return dest.set(this.maxX - this.minX, this.maxY - this.minY, this.maxZ - this.minZ);
   }

   public AABBi union(int x, int y, int z) {
      return this.union(x, y, z, this);
   }

   public AABBi union(Vector3ic p) {
      return this.union(p.x(), p.y(), p.z(), this);
   }

   public AABBi union(int x, int y, int z, AABBi dest) {
      dest.minX = this.minX < x ? this.minX : x;
      dest.minY = this.minY < y ? this.minY : y;
      dest.minZ = this.minZ < z ? this.minZ : z;
      dest.maxX = this.maxX > x ? this.maxX : x;
      dest.maxY = this.maxY > y ? this.maxY : y;
      dest.maxZ = this.maxZ > z ? this.maxZ : z;
      return dest;
   }

   public AABBi union(Vector3ic p, AABBi dest) {
      return this.union(p.x(), p.y(), p.z(), dest);
   }

   public AABBi union(AABBic other) {
      return this.union(other, this);
   }

   public AABBi union(AABBic other, AABBi dest) {
      dest.minX = this.minX < other.minX() ? this.minX : other.minX();
      dest.minY = this.minY < other.minY() ? this.minY : other.minY();
      dest.minZ = this.minZ < other.minZ() ? this.minZ : other.minZ();
      dest.maxX = this.maxX > other.maxX() ? this.maxX : other.maxX();
      dest.maxY = this.maxY > other.maxY() ? this.maxY : other.maxY();
      dest.maxZ = this.maxZ > other.maxZ() ? this.maxZ : other.maxZ();
      return dest;
   }

   public AABBi correctBounds() {
      if (this.minX > this.maxX) {
         int tmp = this.minX;
         this.minX = this.maxX;
         this.maxX = tmp;
      }

      if (this.minY > this.maxY) {
         int tmp = this.minY;
         this.minY = this.maxY;
         this.maxY = tmp;
      }

      if (this.minZ > this.maxZ) {
         int tmp = this.minZ;
         this.minZ = this.maxZ;
         this.maxZ = tmp;
      }

      return this;
   }

   public AABBi translate(Vector3ic xyz) {
      return this.translate(xyz.x(), xyz.y(), xyz.z(), this);
   }

   public AABBi translate(Vector3ic xyz, AABBi dest) {
      return this.translate(xyz.x(), xyz.y(), xyz.z(), dest);
   }

   public AABBi translate(int x, int y, int z) {
      return this.translate(x, y, z, this);
   }

   public AABBi translate(int x, int y, int z, AABBi dest) {
      dest.minX = this.minX + x;
      dest.minY = this.minY + y;
      dest.minZ = this.minZ + z;
      dest.maxX = this.maxX + x;
      dest.maxY = this.maxY + y;
      dest.maxZ = this.maxZ + z;
      return dest;
   }

   public AABBi intersection(AABBic other, AABBi dest) {
      dest.minX = Math.max(this.minX, other.minX());
      dest.minY = Math.max(this.minY, other.minY());
      dest.minZ = Math.max(this.minZ, other.minZ());
      dest.maxX = Math.min(this.maxX, other.maxX());
      dest.maxY = Math.min(this.maxY, other.maxY());
      dest.maxZ = Math.min(this.maxZ, other.maxZ());
      return dest.validate();
   }

   public AABBi intersection(AABBic other) {
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

   public boolean containsPoint(int x, int y, int z) {
      return x > this.minX && y > this.minY && z > this.minZ && x < this.maxX && y < this.maxY && z < this.maxZ;
   }

   public boolean containsPoint(float x, float y, float z) {
      return x > this.minX && y > this.minY && z > this.minZ && x < this.maxX && y < this.maxY && z < this.maxZ;
   }

   public boolean containsPoint(Vector3ic point) {
      return this.containsPoint(point.x(), point.y(), point.z());
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

   public boolean intersectsAABB(AABBic other) {
      return this.maxX >= other.minX()
         && this.maxY >= other.minY()
         && this.maxZ >= other.minZ()
         && this.minX <= other.maxX()
         && this.minY <= other.maxY()
         && this.minZ <= other.maxZ();
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

   public int intersectLineSegment(float p0X, float p0Y, float p0Z, float p1X, float p1Y, float p1Z, Vector2f result) {
      return Intersectionf.intersectLineSegmentAab(p0X, p0Y, p0Z, p1X, p1Y, p1Z, this.minX, this.minY, this.minZ, this.maxX, this.maxY, this.maxZ, result);
   }

   public int intersectLineSegment(LineSegmentf lineSegment, Vector2f result) {
      return Intersectionf.intersectLineSegmentAab(lineSegment, this, result);
   }

   public AABBi transform(Matrix4fc m) {
      return this.transform(m, this);
   }

   public AABBi transform(Matrix4fc m, AABBi dest) {
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

      dest.minX = Math.roundUsing(minx, 2);
      dest.minY = Math.roundUsing(miny, 2);
      dest.minZ = Math.roundUsing(minz, 2);
      dest.maxX = Math.roundUsing(maxx, 1);
      dest.maxY = Math.roundUsing(maxy, 1);
      dest.maxZ = Math.roundUsing(maxz, 1);
      return dest;
   }

   public int hashCode() {
      int prime = 31;
      int result = 1;
      result = 31 * result + this.minX;
      result = 31 * result + this.minY;
      result = 31 * result + this.minZ;
      result = 31 * result + this.maxX;
      result = 31 * result + this.maxY;
      return 31 * result + this.maxZ;
   }

   public boolean equals(Object obj) {
      if (this == obj) {
         return true;
      } else if (obj != null && this.getClass() == obj.getClass()) {
         AABBi aabBi = (AABBi)obj;
         return this.minX == aabBi.minX
            && this.minY == aabBi.minY
            && this.minZ == aabBi.minZ
            && this.maxX == aabBi.maxX
            && this.maxY == aabBi.maxY
            && this.maxZ == aabBi.maxZ;
      } else {
         return false;
      }
   }

   public String toString() {
      return "(" + this.minX + " " + this.minY + " " + this.minZ + ") < (" + this.maxX + " " + this.maxY + " " + this.maxZ + ")";
   }

   public void writeExternal(ObjectOutput out) throws IOException {
      out.writeInt(this.minX);
      out.writeInt(this.minY);
      out.writeInt(this.minZ);
      out.writeInt(this.maxX);
      out.writeInt(this.maxY);
      out.writeInt(this.maxZ);
   }

   public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
      this.minX = in.readInt();
      this.minY = in.readInt();
      this.minZ = in.readInt();
      this.maxX = in.readInt();
      this.maxY = in.readInt();
      this.maxZ = in.readInt();
   }
}
