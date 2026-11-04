package org.joml.primitives;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.text.NumberFormat;
import org.joml.Options;
import org.joml.Runtime;
import org.joml.Vector3dc;

public class Rayd implements Externalizable {
   public double oX;
   public double oY;
   public double oZ;
   public double dX;
   public double dY;
   public double dZ;

   public Rayd() {
   }

   public Rayd(Rayd source) {
      this.oX = source.oX;
      this.oY = source.oY;
      this.oZ = source.oZ;
      this.dX = source.dX;
      this.dY = source.dY;
      this.dZ = source.dZ;
   }

   public Rayd(Vector3dc origin, Vector3dc direction) {
      this.oX = origin.x();
      this.oY = origin.y();
      this.oZ = origin.z();
      this.dX = direction.x();
      this.dY = direction.y();
      this.dZ = direction.z();
   }

   public Rayd(double oX, double oY, double oZ, double dX, double dY, double dZ) {
      this.oX = oX;
      this.oY = oY;
      this.oZ = oZ;
      this.dX = dX;
      this.dY = dY;
      this.dZ = dZ;
   }

   public int hashCode() {
      int prime = 31;
      int result = 1;
      long temp = Double.doubleToLongBits(this.dX);
      result = 31 * result + (int)(temp ^ temp >>> 32);
      temp = Double.doubleToLongBits(this.dY);
      result = 31 * result + (int)(temp ^ temp >>> 32);
      temp = Double.doubleToLongBits(this.dZ);
      result = 31 * result + (int)(temp ^ temp >>> 32);
      temp = Double.doubleToLongBits(this.oX);
      result = 31 * result + (int)(temp ^ temp >>> 32);
      temp = Double.doubleToLongBits(this.oY);
      result = 31 * result + (int)(temp ^ temp >>> 32);
      temp = Double.doubleToLongBits(this.oZ);
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
         Rayd other = (Rayd)obj;
         if (Double.doubleToLongBits(this.dX) != Double.doubleToLongBits(other.dX)) {
            return false;
         } else if (Double.doubleToLongBits(this.dY) != Double.doubleToLongBits(other.dY)) {
            return false;
         } else if (Double.doubleToLongBits(this.dZ) != Double.doubleToLongBits(other.dZ)) {
            return false;
         } else if (Double.doubleToLongBits(this.oX) != Double.doubleToLongBits(other.oX)) {
            return false;
         } else {
            return Double.doubleToLongBits(this.oY) != Double.doubleToLongBits(other.oY)
               ? false
               : Double.doubleToLongBits(this.oZ) == Double.doubleToLongBits(other.oZ);
         }
      }
   }

   public String toString() {
      return Runtime.formatNumbers(this.toString(Options.NUMBER_FORMAT));
   }

   public String toString(NumberFormat formatter) {
      return "("
         + Runtime.format(this.oX, formatter)
         + " "
         + Runtime.format(this.oY, formatter)
         + " "
         + Runtime.format(this.oZ, formatter)
         + ") -> ("
         + Runtime.format(this.dX, formatter)
         + " "
         + Runtime.format(this.dY, formatter)
         + " "
         + Runtime.format(this.dZ, formatter)
         + ")";
   }

   public void writeExternal(ObjectOutput out) throws IOException {
      out.writeDouble(this.oX);
      out.writeDouble(this.oY);
      out.writeDouble(this.oZ);
      out.writeDouble(this.dX);
      out.writeDouble(this.dY);
      out.writeDouble(this.dZ);
   }

   public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
      this.oX = in.readDouble();
      this.oY = in.readDouble();
      this.oZ = in.readDouble();
      this.dX = in.readDouble();
      this.dY = in.readDouble();
      this.dZ = in.readDouble();
   }
}
