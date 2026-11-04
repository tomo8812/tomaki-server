package org.joml.primitives;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.text.NumberFormat;
import org.joml.Options;
import org.joml.Runtime;
import org.joml.Vector3fc;

public class LineSegmentf implements Externalizable {
   public float aX;
   public float aY;
   public float aZ;
   public float bX;
   public float bY;
   public float bZ;

   public LineSegmentf() {
   }

   public LineSegmentf(LineSegmentf source) {
      this.aX = source.aX;
      this.aY = source.aY;
      this.aZ = source.aZ;
      this.aX = source.bX;
      this.bY = source.bY;
      this.bZ = source.bZ;
   }

   public LineSegmentf(Vector3fc a, Vector3fc b) {
      this.aX = a.x();
      this.aY = a.y();
      this.aZ = a.z();
      this.bX = b.x();
      this.bY = b.y();
      this.bZ = b.z();
   }

   public LineSegmentf(float aX, float aY, float aZ, float bX, float bY, float bZ) {
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
      result = 31 * result + Float.floatToIntBits(this.aX);
      result = 31 * result + Float.floatToIntBits(this.aY);
      result = 31 * result + Float.floatToIntBits(this.aZ);
      result = 31 * result + Float.floatToIntBits(this.bX);
      result = 31 * result + Float.floatToIntBits(this.bY);
      return 31 * result + Float.floatToIntBits(this.bZ);
   }

   public boolean equals(Object obj) {
      if (this == obj) {
         return true;
      } else if (obj == null) {
         return false;
      } else if (this.getClass() != obj.getClass()) {
         return false;
      } else {
         LineSegmentf other = (LineSegmentf)obj;
         if (Float.floatToIntBits(this.aX) != Float.floatToIntBits(other.aX)) {
            return false;
         } else if (Float.floatToIntBits(this.aY) != Float.floatToIntBits(other.aY)) {
            return false;
         } else if (Float.floatToIntBits(this.aZ) != Float.floatToIntBits(other.aZ)) {
            return false;
         } else if (Float.floatToIntBits(this.bX) != Float.floatToIntBits(other.bX)) {
            return false;
         } else {
            return Float.floatToIntBits(this.bY) != Float.floatToIntBits(other.bY) ? false : Float.floatToIntBits(this.bZ) == Float.floatToIntBits(other.bZ);
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
      out.writeFloat(this.aX);
      out.writeFloat(this.aY);
      out.writeFloat(this.aZ);
      out.writeFloat(this.bX);
      out.writeFloat(this.bY);
      out.writeFloat(this.bZ);
   }

   public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
      this.aX = in.readFloat();
      this.aY = in.readFloat();
      this.aZ = in.readFloat();
      this.bX = in.readFloat();
      this.bY = in.readFloat();
      this.bZ = in.readFloat();
   }
}
