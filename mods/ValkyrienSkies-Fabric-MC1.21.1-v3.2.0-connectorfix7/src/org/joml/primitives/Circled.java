package org.joml.primitives;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.text.NumberFormat;
import org.joml.Options;
import org.joml.Runtime;
import org.joml.Vector2dc;
import org.joml.Vector2fc;

public class Circled implements Externalizable {
   public double x;
   public double y;
   public double r;

   public Circled() {
   }

   public Circled(Circled source) {
      this.x = source.x;
      this.y = source.y;
      this.r = source.r;
   }

   public Circled(double x, double y, double r) {
      this.x = x;
      this.y = y;
      this.r = r;
   }

   public Circled translate(Vector2dc xy) {
      return this.translate(xy.x(), xy.y(), this);
   }

   public Circled translate(Vector2dc xy, Circled dest) {
      return this.translate(xy.x(), xy.y(), dest);
   }

   public Circled translate(Vector2fc xy) {
      return this.translate(xy.x(), xy.y(), this);
   }

   public Circled translate(Vector2fc xy, Circled dest) {
      return this.translate(xy.x(), xy.y(), dest);
   }

   public Circled translate(double x, double y) {
      return this.translate(x, y, this);
   }

   public Circled translate(double x, double y, Circled dest) {
      dest.x = this.x + x;
      dest.y = this.y + y;
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
         Circled other = (Circled)obj;
         if (Double.doubleToLongBits(this.r) != Double.doubleToLongBits(other.r)) {
            return false;
         } else {
            return Double.doubleToLongBits(this.x) != Double.doubleToLongBits(other.x)
               ? false
               : Double.doubleToLongBits(this.y) == Double.doubleToLongBits(other.y);
         }
      }
   }

   public String toString() {
      return Runtime.formatNumbers(this.toString(Options.NUMBER_FORMAT));
   }

   public String toString(NumberFormat formatter) {
      return "(" + Runtime.format(this.x, formatter) + " " + Runtime.format(this.y, formatter) + " " + Runtime.format(this.r, formatter) + ")";
   }

   public void writeExternal(ObjectOutput out) throws IOException {
      out.writeDouble(this.x);
      out.writeDouble(this.y);
      out.writeDouble(this.r);
   }

   public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
      this.x = in.readDouble();
      this.y = in.readDouble();
      this.r = in.readDouble();
   }
}
