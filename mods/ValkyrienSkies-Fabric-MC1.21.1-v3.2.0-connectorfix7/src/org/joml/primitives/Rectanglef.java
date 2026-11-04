package org.joml.primitives;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.text.NumberFormat;
import org.joml.Math;
import org.joml.Options;
import org.joml.Runtime;
import org.joml.Vector2f;
import org.joml.Vector2fc;
import org.joml.Vector2ic;

public class Rectanglef implements Externalizable {
   public float minX;
   public float minY;
   public float maxX;
   public float maxY;

   public Rectanglef() {
   }

   public Rectanglef(Rectanglef source) {
      this.minX = source.minX;
      this.minY = source.minY;
      this.maxX = source.maxX;
      this.maxY = source.maxY;
   }

   public Rectanglef(Vector2fc min, Vector2fc max) {
      this.minX = min.x();
      this.minY = min.y();
      this.maxX = max.x();
      this.maxY = max.y();
   }

   public Rectanglef(float minX, float minY, float maxX, float maxY) {
      this.minX = minX;
      this.minY = minY;
      this.maxX = maxX;
      this.maxY = maxY;
   }

   public Rectanglef set(Rectanglef source) {
      this.minX = source.minX;
      this.minY = source.minY;
      this.maxX = source.maxX;
      this.maxY = source.maxY;
      return this;
   }

   public Rectanglef setMin(float minX, float minY) {
      this.minX = minX;
      this.minY = minY;
      return this;
   }

   public Rectanglef setMin(Vector2fc min) {
      this.minX = min.x();
      this.minY = min.y();
      return this;
   }

   public Rectanglef setMax(float maxX, float maxY) {
      this.maxX = maxX;
      this.maxY = maxY;
      return this;
   }

   public Rectanglef setMax(Vector2fc max) {
      this.maxX = max.x();
      this.maxY = max.y();
      return this;
   }

   public float lengthX() {
      return this.maxX - this.minX;
   }

   public float lengthY() {
      return this.maxY - this.minY;
   }

   public float area() {
      return this.lengthX() * this.lengthY();
   }

   public Rectanglef union(float x, float y) {
      return this.union(x, y, this);
   }

   public Rectanglef union(Vector2fc p) {
      return this.union(p.x(), p.y(), this);
   }

   public Rectanglef union(float x, float y, Rectanglef dest) {
      dest.minX = this.minX < x ? this.minX : x;
      dest.minY = this.minY < y ? this.minY : y;
      dest.maxX = this.maxX > x ? this.maxX : x;
      dest.maxY = this.maxY > y ? this.maxY : y;
      return dest;
   }

   public Rectanglef union(Vector2ic p, Rectanglef dest) {
      return this.union(p.x(), p.y(), dest);
   }

   public Rectanglef union(Rectanglef other) {
      return this.union(other, this);
   }

   public Rectanglef union(Rectanglef other, Rectanglef dest) {
      dest.minX = this.minX < other.minX ? this.minX : other.minX;
      dest.minY = this.minY < other.minY ? this.minY : other.minY;
      dest.maxX = this.maxX > other.maxX ? this.maxX : other.maxX;
      dest.maxY = this.maxY > other.maxY ? this.maxY : other.maxY;
      return dest;
   }

   public boolean intersectsRectangle(Rectangled other) {
      return this.minX < other.maxX && this.maxX > other.minX && this.maxY > other.minY && this.minY < other.maxY;
   }

   public boolean intersectsRectangle(Rectanglef other) {
      return this.minX < other.maxX && this.maxX > other.minX && this.maxY > other.minY && this.minY < other.maxY;
   }

   public boolean intersectsRectangle(Rectanglei other) {
      return this.minX < other.maxX && this.maxX > other.minX && this.maxY > other.minY && this.minY < other.maxY;
   }

   private Rectanglef validate() {
      if (!this.isValid()) {
         this.minX = Float.NaN;
         this.minY = Float.NaN;
         this.maxX = Float.NaN;
         this.maxY = Float.NaN;
      }

      return this;
   }

   public boolean isValid() {
      return this.minX < this.maxX && this.minY < this.maxY;
   }

   public Rectanglef intersection(Rectanglef other) {
      return this.intersection(other, this);
   }

   public Rectanglef intersection(Rectanglei other) {
      return this.intersection(other, this);
   }

   public Rectanglef intersection(Rectanglef other, Rectanglef dest) {
      dest.minX = Math.max(this.minX, other.minX);
      dest.minY = Math.max(this.minY, other.minY);
      dest.maxX = Math.min(this.maxX, other.maxX);
      dest.maxY = Math.min(this.maxY, other.maxY);
      return dest.validate();
   }

   public Rectanglef intersection(Rectanglei other, Rectanglef dest) {
      dest.minX = Math.max(this.minX, other.minX);
      dest.minY = Math.max(this.minY, other.minY);
      dest.maxX = Math.min(this.maxX, other.maxX);
      dest.maxY = Math.min(this.maxY, other.maxY);
      return dest.validate();
   }

   public Vector2f lengths(Vector2f dest) {
      return dest.set(this.lengthX(), this.lengthY());
   }

   public boolean containsRectangle(Rectangled rectangle) {
      return rectangle.minX >= this.minX && rectangle.maxX <= this.maxX && rectangle.minY >= this.minY && rectangle.maxY <= this.maxY;
   }

   public boolean containsRectangle(Rectanglef rectangle) {
      return rectangle.minX >= this.minX && rectangle.maxX <= this.maxX && rectangle.minY >= this.minY && rectangle.maxY <= this.maxY;
   }

   public boolean containsRectangle(Rectanglei rectangle) {
      return rectangle.minX >= this.minX && rectangle.maxX <= this.maxX && rectangle.minY >= this.minY && rectangle.maxY <= this.maxY;
   }

   public boolean containsPoint(Vector2fc point) {
      return this.containsPoint(point.x(), point.y());
   }

   public boolean containsPoint(float x, float y) {
      return x > this.minX && y > this.minY && x < this.maxX && y < this.maxY;
   }

   public Rectanglef translate(Vector2fc xy) {
      return this.translate(xy.x(), xy.y(), this);
   }

   public Rectanglef translate(Vector2fc xy, Rectanglef dest) {
      return this.translate(xy.x(), xy.y(), dest);
   }

   public Rectanglef translate(float x, float y) {
      return this.translate(x, y, this);
   }

   public Rectanglef translate(float x, float y, Rectanglef dest) {
      dest.minX = this.minX + x;
      dest.minY = this.minY + y;
      dest.maxX = this.maxX + x;
      dest.maxY = this.maxY + y;
      return dest;
   }

   public Rectanglef scale(float sf) {
      return this.scale(sf, sf);
   }

   public Rectanglef scale(float sf, Rectanglef dest) {
      return this.scale(sf, sf, dest);
   }

   public Rectanglef scale(float sf, float ax, float ay) {
      return this.scale(sf, sf, ax, ay);
   }

   public Rectanglef scale(float sf, float ax, float ay, Rectanglef dest) {
      return this.scale(sf, sf, ax, ay, dest);
   }

   public Rectanglef scale(float sf, Vector2fc anchor) {
      return this.scale(sf, anchor.x(), anchor.y());
   }

   public Rectanglef scale(float sf, Vector2fc anchor, Rectanglef dest) {
      return this.scale(sf, anchor.x(), anchor.y(), dest);
   }

   public Rectanglef scale(float sx, float sy) {
      return this.scale(sx, sy, 0.0F, 0.0F);
   }

   public Rectanglef scale(float sx, float sy, Rectanglef dest) {
      return this.scale(sx, sy, 0.0F, 0.0F, dest);
   }

   public Rectanglef scale(float sx, float sy, float ax, float ay) {
      this.minX = (this.minX - ax) * sx + ax;
      this.minY = (this.minY - ay) * sy + ay;
      this.maxX = (this.maxX - ax) * sx + ax;
      this.maxY = (this.maxY - ay) * sy + ay;
      return this;
   }

   public Rectanglef scale(float sx, float sy, Vector2fc anchor) {
      return this.scale(sx, sy, anchor.x(), anchor.y());
   }

   public Rectanglef scale(float sx, float sy, float ax, float ay, Rectanglef dest) {
      dest.minX = (this.minX - ax) * sx + ax;
      dest.minY = (this.minY - ay) * sy + ay;
      dest.maxX = (this.maxX - ax) * sx + ax;
      dest.maxY = (this.maxY - ay) * sy + ay;
      return dest;
   }

   public Rectanglef scale(float sx, float sy, Vector2fc anchor, Rectanglef dest) {
      return this.scale(sx, sy, anchor.x(), anchor.y(), dest);
   }

   public int hashCode() {
      int prime = 31;
      int result = 1;
      result = 31 * result + Float.floatToIntBits(this.maxX);
      result = 31 * result + Float.floatToIntBits(this.maxY);
      result = 31 * result + Float.floatToIntBits(this.minX);
      return 31 * result + Float.floatToIntBits(this.minY);
   }

   public boolean equals(Object obj) {
      if (this == obj) {
         return true;
      } else if (obj == null) {
         return false;
      } else if (this.getClass() != obj.getClass()) {
         return false;
      } else {
         Rectanglef other = (Rectanglef)obj;
         if (Float.floatToIntBits(this.maxX) != Float.floatToIntBits(other.maxX)) {
            return false;
         } else if (Float.floatToIntBits(this.maxY) != Float.floatToIntBits(other.maxY)) {
            return false;
         } else {
            return Float.floatToIntBits(this.minX) != Float.floatToIntBits(other.minX)
               ? false
               : Float.floatToIntBits(this.minY) == Float.floatToIntBits(other.minY);
         }
      }
   }

   public String toString() {
      return Runtime.formatNumbers(this.toString(Options.NUMBER_FORMAT));
   }

   public String toString(NumberFormat formatter) {
      return "("
         + Runtime.format(this.minX, formatter)
         + " "
         + Runtime.format(this.minY, formatter)
         + ") < ("
         + Runtime.format(this.maxX, formatter)
         + " "
         + Runtime.format(this.maxY, formatter)
         + ")";
   }

   public void writeExternal(ObjectOutput out) throws IOException {
      out.writeFloat(this.minX);
      out.writeFloat(this.minY);
      out.writeFloat(this.maxX);
      out.writeFloat(this.maxY);
   }

   public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
      this.minX = in.readFloat();
      this.minY = in.readFloat();
      this.maxX = in.readFloat();
      this.maxY = in.readFloat();
   }
}
