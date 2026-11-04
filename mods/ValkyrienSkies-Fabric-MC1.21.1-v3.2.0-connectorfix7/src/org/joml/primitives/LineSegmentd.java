package org.joml.primitives;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.text.NumberFormat;
import org.joml.Options;
import org.joml.Runtime;
import org.joml.Vector3dc;

public class LineSegmentd implements Externalizable {
   public double aX;
   public double aY;
   public double aZ;
   public double bX;
   public double bY;
   public double bZ;

   public LineSegmentd() {
   }

   public LineSegmentd(LineSegmentd source) {
      this.aX = source.aX;
      this.aY = source.aY;
      this.aZ = source.aZ;
      this.aX = source.bX;
      this.bY = source.bY;
      this.bZ = source.bZ;
   }

   public LineSegmentd(Vector3dc a, Vector3dc b) {
      this.aX = a.x();
      this.aY = a.y();
      this.aZ = a.z();
      this.bX = b.x();
      this.bY = b.y();
      this.bZ = b.z();
   }

   public LineSegmentd(double aX, double aY, double aZ, double bX, double bY, double bZ) {
      this.aX = aX;
      this.aY = aY;
      this.aZ = aZ;
      this.bX = bX;
      this.bY = bY;
      this.bZ = bZ;
   }

   public int hashCode() {
      int prime = 31;
      int result = 1;
      long temp = Double.doubleToLongBits(this.aX);
      result = 31 * result + (int)(temp ^ temp >>> 32);
      temp = Double.doubleToLongBits(this.aY);
      result = 31 * result + (int)(temp ^ temp >>> 32);
      temp = Double.doubleToLongBits(this.aZ);
      result = 31 * result + (int)(temp ^ temp >>> 32);
      temp = Double.doubleToLongBits(this.bX);
      result = 31 * result + (int)(temp ^ temp >>> 32);
      temp = Double.doubleToLongBits(this.bY);
      result = 31 * result + (int)(temp ^ temp >>> 32);
      temp = Double.doubleToLongBits(this.bZ);
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
         LineSegmentd other = (LineSegmentd)obj;
         if (Double.doubleToLongBits(this.aX) != Double.doubleToLongBits(other.aX)) {
            return false;
         } else if (Double.doubleToLongBits(this.aY) != Double.doubleToLongBits(other.aY)) {
            return false;
         } else if (Double.doubleToLongBits(this.aZ) != Double.doubleToLongBits(other.aZ)) {
            return false;
         } else if (Double.doubleToLongBits(this.bX) != Double.doubleToLongBits(other.bX)) {
            return false;
         } else {
            return Double.doubleToLongBits(this.bY) != Double.doubleToLongBits(other.bY)
               ? false
               : Double.doubleToLongBits(this.bZ) == Double.doubleToLongBits(other.bZ);
         }
      }
   }

   public String toString() {
      return Runtime.formatNumbers(this.toString(Options.NUMBER_FORMAT));
   }

   public String toString(NumberFormat formatter) {
      return "("
         + Runtime.format(this.aX, formatter)
         + " "
         + Runtime.format(this.aY, formatter)
         + " "
         + Runtime.format(this.aZ, formatter)
         + ") - ("
         + Runtime.format(this.bX, formatter)
         + " "
         + Runtime.format(this.bY, formatter)
         + " "
         + Runtime.format(this.bZ, formatter)
         + ")";
   }

   public void writeExternal(ObjectOutput out) throws IOException {
      out.writeDouble(this.aX);
      out.writeDouble(this.aY);
      out.writeDouble(this.aZ);
      out.writeDouble(this.bX);
      out.writeDouble(this.bY);
      out.writeDouble(this.bZ);
   }

   public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
      this.aX = in.readDouble();
      this.aY = in.readDouble();
      this.aZ = in.readDouble();
      this.bX = in.readDouble();
      this.bY = in.readDouble();
      this.bZ = in.readDouble();
   }
}
