package com.kishku7.chunksmith.util;

import java.util.Objects;

public class Version implements Comparable<Version> {
   public static final Version INVALID = new Version(Integer.MIN_VALUE, Integer.MIN_VALUE, Integer.MIN_VALUE);
   public static final Version MINECRAFT_1_13_2 = new Version(1, 13, 2);
   private int major = 0;
   private int minor = 0;
   private int patch = 0;

   public Version(int major, int minor, int patch) {
      this.major = major;
      this.minor = minor;
      this.patch = patch;
   }

   public Version(String version) {
      if (version != null && !version.isEmpty()) {
         String[] semVer = version.split("\\.");
         if (semVer.length > 0) {
            this.major = Input.tryInteger(semVer[0]).orElse(Integer.MIN_VALUE);
         }

         if (semVer.length > 1) {
            this.minor = Input.tryInteger(semVer[1]).orElse(Integer.MIN_VALUE);
         }

         if (semVer.length > 2) {
            this.patch = Input.tryInteger(semVer[2]).orElse(Integer.MIN_VALUE);
         }
      } else {
         this.major = Integer.MIN_VALUE;
      }
   }

   public Version(String version, boolean minecraft) {
      this(minecraft && version.indexOf(45) > -1 ? version.substring(0, version.indexOf(45)) : version);
   }

   public boolean isEqualTo(Version o) {
      return this.compareTo(o) == 0;
   }

   public boolean isHigherThan(Version o) {
      return this.compareTo(o) > 0;
   }

   public boolean isHigherThanOrEqualTo(Version o) {
      return this.compareTo(o) >= 0;
   }

   public boolean isLowerThan(Version o) {
      return this.compareTo(o) < 0;
   }

   public boolean isLowerThanOrEqualTo(Version o) {
      return this.compareTo(o) <= 0;
   }

   public boolean isValid() {
      return this.major != Integer.MIN_VALUE && this.minor != Integer.MIN_VALUE && this.patch != Integer.MIN_VALUE;
   }

   public int compareTo(Version o) {
      if (this.major != o.major) {
         return this.major - o.major;
      } else {
         return this.minor != o.minor ? this.minor - o.minor : this.patch - o.patch;
      }
   }

   @Override
   public boolean equals(Object o) {
      if (this == o) {
         return true;
      } else if (o != null && this.getClass() == o.getClass()) {
         Version version = (Version)o;
         return this.major == version.major && this.minor == version.minor && this.patch == version.patch;
      } else {
         return false;
      }
   }

   @Override
   public String toString() {
      return this.isValid() ? this.major + "." + this.minor + "." + this.patch : "invalid";
   }

   @Override
   public int hashCode() {
      return Objects.hash(this.major, this.minor, this.patch);
   }

   // $VF: synthetic method
   // $VF: bridge method
   @Override
   public int compareTo(Object param1) {
      return this.compareTo((Version)var1);
   }
}
