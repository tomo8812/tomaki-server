package org.joml.primitives;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.text.NumberFormat;
import org.joml.Options;
import org.joml.Runtime;
import org.joml.Vector3fc;

public class Rayf implements Externalizable {
   public float oX;
   public float oY;
   public float oZ;
   public float dX;
   public float dY;
   public float dZ;

   public Rayf() {
   }

   public Rayf(Rayf source) {
      this.oX = source.oX;
      this.oY = source.oY;
      this.oZ = source.oZ;
      this.dX = source.dX;
      this.dY = source.dY;
      this.dZ = source.dZ;
   }

   public Rayf(Vector3fc origin, Vector3fc direction) {
      this.oX = origin.x();
      this.oY = origin.y();
      this.oZ = origin.z();
      this.dX = direction.x();
      this.dY = direction.y();
      this.dZ = direction.z();
   }

   public Rayf(float oX, float oY, float oZ, float dX, float dY, float dZ) {
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
      result = 31 * result + Float.floatToIntBits(this.dX);
      result = 31 * result + Float.floatToIntBits(this.dY);
      result = 31 * result + Float.floatToIntBits(this.dZ);
      result = 31 * result + Float.floatToIntBits(this.oX);
      result = 31 * result + Float.floatToIntBits(this.oY);
      return 31 * result + Float.floatToIntBits(this.oZ);
   }

   public boolean equals(Object obj) {
      if (this == obj) {
         return true;
      } else if (obj == null) {
         return false;
      } else if (this.getClass() != obj.getClass()) {
         return false;
      } else {
         Rayf other = (Rayf)obj;
         if (Float.floatToIntBits(this.dX) != Float.floatToIntBits(other.dX)) {
            return false;
         } else if (Float.floatToIntBits(this.dY) != Float.floatToIntBits(other.dY)) {
            return false;
         } else if (Float.floatToIntBits(this.dZ) != Float.floatToIntBits(other.dZ)) {
            return false;
         } else if (Float.floatToIntBits(this.oX) != Float.floatToIntBits(other.oX)) {
            return false;
         } else {
            return Float.floatToIntBits(this.oY) != Float.floatToIntBits(other.oY) ? false : Float.floatToIntBits(this.oZ) == Float.floatToIntBits(other.oZ);
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
      out.writeFloat(this.oX);
      out.writeFloat(this.oY);
      out.writeFloat(this.oZ);
      out.writeFloat(this.dX);
      out.writeFloat(this.dY);
      out.writeFloat(this.dZ);
   }

   public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
      this.oX = in.readFloat();
      this.oY = in.readFloat();
      this.oZ = in.readFloat();
      this.dX = in.readFloat();
      this.dY = in.readFloat();
      this.dZ = in.readFloat();
   }
}
