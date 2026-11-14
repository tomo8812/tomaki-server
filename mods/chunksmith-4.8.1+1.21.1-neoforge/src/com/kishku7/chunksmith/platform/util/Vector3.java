package com.kishku7.chunksmith.platform.util;

public class Vector3 {
   private double x;
   private double y;
   private double z;

   public Vector3(double x, double y, double z) {
      this.x = x;
      this.y = y;
      this.z = z;
   }

   public static Vector3 of(double x, double y, double z) {
      return new Vector3(x, y, z);
   }

   public Vector3 add(Vector3 other) {
      this.x = this.x + other.x;
      this.y = this.y + other.y;
      this.z = this.z + other.z;
      return this;
   }

   public Vector3 multiply(double value) {
      this.x *= value;
      this.y *= value;
      this.z *= value;
      return this;
   }

   public Vector3 normalize() {
      double length = this.length();
      this.x /= length;
      this.y /= length;
      this.z /= length;
      return this;
   }

   public double distance(Vector3 other) {
      return Math.sqrt(this.distanceSquared(other));
   }

   public double distanceSquared(Vector3 other) {
      double dx = this.x - other.x;
      double dy = this.y - other.y;
      double dz = this.z - other.z;
      return dx * dx + dy * dy + dz * dz;
   }

   public double length() {
      return Math.sqrt(this.lengthSquared());
   }

   public double lengthSquared() {
      return this.x * this.x + this.y * this.y + this.z * this.z;
   }

   public double getX() {
      return this.x;
   }

   public void setX(double x) {
      this.x = x;
   }

   public double getY() {
      return this.y;
   }

   public void setY(double y) {
      this.y = y;
   }

   public double getZ() {
      return this.z;
   }

   public void setZ(double z) {
      this.z = z;
   }
}
