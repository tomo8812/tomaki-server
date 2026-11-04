package org.joml.primitives;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.text.NumberFormat;
import org.joml.Options;
import org.joml.Runtime;
import org.joml.Vector3dc;
import org.joml.Vector3fc;

public class Sphered implements Externalizable {
   public double x;
   public double y;
   public double z;
   public double r;

   public Sphered() {
   }

   public Sphered(Sphered source) {
      this.x = source.x;
      this.y = source.y;
      this.z = source.z;
      this.r = source.r;
   }

   public Sphered(Vector3fc c, double r) {
      this.x = c.x();
      this.y = c.y();
      this.z = c.z();
      this.r = r;
   }

   public Sphered(Vector3dc c, double r) {
      this.x = c.x();
      this.y = c.y();
      this.z = c.z();
      this.r = r;
   }

   public Sphered(double x, double y, double z, double r) {
      this.x = x;
      this.y = y;
      this.z = z;
      this.r = r;
   }

   public Sphered translate(Vector3dc xyz) {
      return this.translate(xyz.x(), xyz.y(), xyz.z(), this);
   }

   public Sphered translate(Vector3dc xyz, Sphered dest) {
      return this.translate(xyz.x(), xyz.y(), xyz.z(), dest);
   }

   public Sphered translate(Vector3fc xyz) {
      return this.translate(xyz.x(), xyz.y(), xyz.z(), this);
   }

   public Sphered translate(Vector3fc xyz, Sphered dest) {
      return this.translate(xyz.x(), xyz.y(), xyz.z(), dest);
   }

   public Sphered translate(double x, double y, double z) {
      return this.translate(x, y, z, this);
   }

   public Sphered translate(double x, double y, double z, Sphered dest) {
      dest.x = this.x + x;
      dest.y = this.y + y;
      dest.z = this.z + z;
      return dest;
   }

   public int hashCode() {
      int prime = 31;
      int result = 1;
      long temp = Double.doubleToLongBits(this.r);
      result = 31 * result + (int)(temp ^ temp >>> 32);
      temp = Double.doubleToLongBits(this.x);
      result = 31 * result + (int)(temp ^ temp >>> 32);
      temp = Double.doubleToLongBits(this.y);
      result = 31 * result + (int)(temp ^ temp >>> 32);
      temp = Double.doubleToLongBits(this.z);
      return 31 * result + (int)(temp ^ temp >>> 32);
   }

   public boolean equals(Object obj) {
      if (this == obj) {
         return true;
      } else if (obj == null) {
         return false;
      } else if (this.getClass() != obj.getClass()) {
         return false;
      } else {
         Sphered other = (Sphered)obj;
         if (Double.doubleToLongBits(this.r) != Double.doubleToLongBits(other.r)) {
            return false;
         } else if (Double.doubleToLongBits(this.x) != Double.doubleToLongBits(other.x)) {
            return false;
         } else {
            return Double.doubleToLongBits(this.y) != Double.doubleToLongBits(other.y)
               ? false
               : Double.doubleToLongBits(this.z) == Double.doubleToLongBits(other.z);
         }
      }
   }

   public String toString() {
      return Runtime.formatNumbers(this.toString(Options.NUMBER_FORMAT));
   }

   public String toString(NumberFormat formatter) {
      return "["
         + Runtime.format(this.x, formatter)
         + " "
         + Runtime.format(this.y, formatter)
         + " "
         + Runtime.format(this.z, formatter)
         + " "
         + Runtime.format(this.r, formatter)
         + "]";
   }

   public void writeExternal(ObjectOutput out) throws IOException {
      out.writeDouble(this.x);
      out.writeDouble(this.y);
      out.writeDouble(this.z);
      out.writeDouble(this.r);
   }

   public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
      this.x = in.readDouble();
      this.y = in.readDouble();
      this.z = in.readDouble();
      this.r = in.readDouble();
   }
}
