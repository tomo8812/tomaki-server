package org.joml.primitives;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.text.NumberFormat;
import org.joml.Options;
import org.joml.Runtime;
import org.joml.Vector2fc;

public class Circlef implements Externalizable {
   public float x;
   public float y;
   public float r;

   public Circlef() {
   }

   public Circlef(Circlef source) {
      this.x = source.x;
      this.y = source.y;
      this.r = source.r;
   }

   public Circlef(float x, float y, float r) {
      this.x = x;
      this.y = y;
      this.r = r;
   }

   public Circlef translate(Vector2fc xy) {
      return this.translate(xy.x(), xy.y(), this);
   }

   public Circlef translate(Vector2fc xy, Circlef dest) {
      return this.translate(xy.x(), xy.y(), dest);
   }

   public Circlef translate(float x, float y) {
      return this.translate(x, y, this);
   }

   public Circlef translate(float x, float y, Circlef dest) {
      dest.x = this.x + x;
      dest.y = this.y + y;
      return dest;
   }

   public int hashCode() {
      int prime = 31;
      int result = 1;
      result = 31 * result + Float.floatToIntBits(this.r);
      result = 31 * result + Float.floatToIntBits(this.x);
      return 31 * result + Float.floatToIntBits(this.y);
   }

   public boolean equals(Object obj) {
      if (this == obj) {
         return true;
      } else if (obj == null) {
         return false;
      } else if (this.getClass() != obj.getClass()) {
         return false;
      } else {
         Circlef other = (Circlef)obj;
         if (Float.floatToIntBits(this.r) != Float.floatToIntBits(other.r)) {
            return false;
         } else {
            return Float.floatToIntBits(this.x) != Float.floatToIntBits(other.x) ? false : Float.floatToIntBits(this.y) == Float.floatToIntBits(other.y);
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
      out.writeFloat(this.x);
      out.writeFloat(this.y);
      out.writeFloat(this.r);
   }

   public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
      this.x = in.readFloat();
      this.y = in.readFloat();
      this.r = in.readFloat();
   }
}
