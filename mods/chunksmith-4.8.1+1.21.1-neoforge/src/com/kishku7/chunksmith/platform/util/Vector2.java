package com.kishku7.chunksmith.platform.util;

public class Vector2 {
   private double x;
   private double z;

   public Vector2(double x, double z) {
      this.x = x;
      this.z = z;
   }

   public static Vector2 of(double x, double z) {
      return new Vector2(x, z);
   }

   public Vector2 add(Vector2 other) {
      this.x = this.x + other.x;
      this.z = this.z + other.z;
      return this;
   }

   public Vector2 multiply(double value) {
      this.x *= value;
      this.z *= value;
      return this;
   }

   public Vector2 normalize() {
      double length = this.length();
      this.x /= length;
      this.z /= length;
      return this;
   }

   public double distance(Vector2 other) {
      return Math.sqrt(this.distanceSquared(other));
   }

   public double distanceSquared(Vector2 other) {
      double dx = this.x - other.x;
      double dz = this.z - other.z;
      return dx * dx + dz * dz;
   }

   public double length() {
      return Math.sqrt(this.lengthSquared());
   }

   public double lengthSquared() {
      return this.x * this.x + this.z * this.z;
   }

   public double getX() {
      return this.x;
   }

   public void setX(double x) {
      this.x = x;
   }

   public double getZ() {
      return this.z;
   }

   public void setZ(double z) {
      this.z = z;
   }
}
