package org.joml.primitives;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.text.NumberFormat;
import org.joml.Math;
import org.joml.Options;
import org.joml.Runtime;
import org.joml.Vector3f;
import org.joml.Vector3fc;
import org.joml.Vector4f;

public class Planef implements Externalizable {
   public float a;
   public float b;
   public float c;
   public float d;

   public Planef() {
   }

   public Planef(Planef source) {
      this.a = source.a;
      this.b = source.b;
      this.c = source.c;
      this.d = source.d;
   }

   public Planef(Vector3fc point, Vector3fc normal) {
      this.a = normal.x();
      this.b = normal.y();
      this.c = normal.z();
      this.d = -this.a * point.x() - this.b * point.y() - this.c * point.z();
   }

   public Planef(Vector3fc pointA, Vector3fc pointB, Vector3fc pointC) {
      float abX = pointB.x() - pointA.x();
      float abY = pointB.y() - pointA.y();
      float abZ = pointB.z() - pointA.z();
      float acX = pointC.x() - pointA.x();
      float acY = pointC.y() - pointA.y();
      float acZ = pointC.z() - pointA.z();
      this.a = abY * acZ - abZ * acY;
      this.b = abZ * acX - abX * acZ;
      this.c = abX * acY - abY * acX;
      this.d = -this.a * pointA.x() - this.b * pointA.y() - this.c * pointA.z();
   }

   public Planef(float a, float b, float c, float d) {
      this.a = a;
      this.b = b;
      this.c = c;
      this.d = d;
   }

   public Planef set(float a, float b, float c, float d) {
      this.a = a;
      this.b = b;
      this.c = c;
      this.d = d;
      return this;
   }

   public Planef normalize() {
      return this.normalize(this);
   }

   public Planef normalize(Planef dest) {
      float invLength = Math.invsqrt(this.a * this.a + this.b * this.b + this.c * this.c);
      dest.a = this.a * invLength;
      dest.b = this.b * invLength;
      dest.c = this.c * invLength;
      dest.d = this.d * invLength;
      return dest;
   }

   public float distance(float x, float y, float z) {
      return Intersectionf.distancePointPlane(x, y, z, this.a, this.b, this.c, this.d);
   }

   public static Vector4f equationFromPoints(Vector3f v0, Vector3f v1, Vector3f v2, Vector4f dest) {
      return equationFromPoints(v0.x, v0.y, v0.z, v1.x, v1.y, v1.z, v2.x, v2.y, v2.z, dest);
   }

   public static Vector4f equationFromPoints(float v0X, float v0Y, float v0Z, float v1X, float v1Y, float v1Z, float v2X, float v2Y, float v2Z, Vector4f dest) {
      float v1Y0Y = v1Y - v0Y;
      float v2Z0Z = v2Z - v0Z;
      float v2Y0Y = v2Y - v0Y;
      float v1Z0Z = v1Z - v0Z;
      float v2X0X = v2X - v0X;
      float v1X0X = v1X - v0X;
      float a = v1Y0Y * v2Z0Z - v2Y0Y * v1Z0Z;
      float b = v1Z0Z * v2X0X - v2Z0Z * v1X0X;
      float c = v1X0X * v2Y0Y - v2X0X * v1Y0Y;
      float d = -(a * v0X + b * v0Y + c * v0Z);
      dest.x = a;
      dest.y = b;
      dest.z = c;
      dest.w = d;
      return dest;
   }

   public int hashCode() {
      int prime = 31;
      int result = 1;
      result = 31 * result + Float.floatToIntBits(this.a);
      result = 31 * result + Float.floatToIntBits(this.b);
      result = 31 * result + Float.floatToIntBits(this.c);
      return 31 * result + Float.floatToIntBits(this.d);
   }

   public boolean equals(Object obj) {
      if (this == obj) {
         return true;
      } else if (obj == null) {
         return false;
      } else if (this.getClass() != obj.getClass()) {
         return false;
      } else {
         Planef other = (Planef)obj;
         if (Float.floatToIntBits(this.a) != Float.floatToIntBits(other.a)) {
            return false;
         } else if (Float.floatToIntBits(this.b) != Float.floatToIntBits(other.b)) {
            return false;
         } else {
            return Float.floatToIntBits(this.c) != Float.floatToIntBits(other.c) ? false : Float.floatToIntBits(this.d) == Float.floatToIntBits(other.d);
         }
      }
   }

   public String toString() {
      return Runtime.formatNumbers(this.toString(Options.NUMBER_FORMAT));
   }

   public String toString(NumberFormat formatter) {
      return "["
         + Runtime.format(this.a, formatter)
         + " "
         + Runtime.format(this.b, formatter)
         + " "
         + Runtime.format(this.c, formatter)
         + " "
         + Runtime.format(this.d, formatter)
         + "]";
   }

   public void writeExternal(ObjectOutput out) throws IOException {
      out.writeFloat(this.a);
      out.writeFloat(this.b);
      out.writeFloat(this.c);
      out.writeFloat(this.d);
   }

   public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
      this.a = in.readFloat();
      this.b = in.readFloat();
      this.c = in.readFloat();
      this.d = in.readFloat();
   }
}
