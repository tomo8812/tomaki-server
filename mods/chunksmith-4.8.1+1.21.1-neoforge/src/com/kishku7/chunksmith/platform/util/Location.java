package com.kishku7.chunksmith.platform.util;

import com.kishku7.chunksmith.platform.World;

public class Location {
   private World world;
   private double x;
   private double y;
   private double z;
   private float yaw;
   private float pitch;

   public Location(World world, double x, double y, double z, float yaw, float pitch) {
      this.world = world;
      this.x = x;
      this.y = y;
      this.z = z;
      this.yaw = yaw;
      this.pitch = pitch;
   }

   public Location(World world, double x, double y, double z) {
      this.world = world;
      this.x = x;
      this.y = y;
      this.z = z;
   }

   public Vector3 toVector() {
      return new Vector3(this.x, this.y, this.z);
   }

   public Location add(Vector3 vector) {
      this.x = this.x + vector.getX();
      this.y = this.y + vector.getY();
      this.z = this.z + vector.getZ();
      return this;
   }

   public Location setDirection(Vector3 direction) {
      double dirX = direction.getX();
      double dirY = direction.getY();
      double dirZ = direction.getZ();
      if (dirX == 0.0 && dirZ == 0.0) {
         if (dirY == 0.0) {
            this.pitch = 0.0F;
         } else {
            this.pitch = dirY < 0.0 ? 90.0F : -90.0F;
         }

         return this;
      } else {
         this.yaw = (float)Math.toDegrees(Math.atan2(-dirX, dirZ));
         this.pitch = (float)Math.toDegrees(-Math.atan(dirY / Math.sqrt(dirX * dirX + dirZ * dirZ)));
         return this;
      }
   }

   public World getWorld() {
      return this.world;
   }

   public void setWorld(World world) {
      this.world = world;
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

   public float getYaw() {
      return this.yaw;
   }

   public void setYaw(float yaw) {
      this.yaw = yaw;
   }

   public float getPitch() {
      return this.pitch;
   }

   public void setPitch(float pitch) {
      this.pitch = pitch;
   }
}
