package org.joml.primitives;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.text.NumberFormat;
import org.joml.Math;
import org.joml.Options;
import org.joml.Runtime;
import org.joml.Vector2fc;
import org.joml.Vector2i;
import org.joml.Vector2ic;

public class Rectanglei implements Externalizable {
   public int minX;
   public int minY;
   public int maxX;
   public int maxY;

   public Rectanglei() {
   }

   public Rectanglei(Rectanglei source) {
      this.minX = source.minX;
      this.minY = source.minY;
      this.maxX = source.maxX;
      this.maxY = source.maxY;
   }

   public Rectanglei(Vector2ic min, Vector2ic max) {
      this.minX = min.x();
      this.minY = min.y();
      this.maxX = max.x();
      this.maxY = max.y();
   }

   public Rectanglei(int minX, int minY, int maxX, int maxY) {
      this.minX = minX;
      this.minY = minY;
      this.maxX = maxX;
      this.maxY = maxY;
   }

   public Rectanglei set(Rectanglei source) {
      this.minX = source.minX;
      this.minY = source.minY;
      this.maxX = source.maxX;
      this.maxY = source.maxY;
      return this;
   }

   public Rectanglei setMin(int minX, int minY) {
      this.minX = minX;
      this.minY = minY;
      return this;
   }

   public Rectanglei setMin(Vector2ic min) {
      this.minX = min.x();
      this.minY = min.y();
      return this;
   }

   public Rectanglei setMax(int maxX, int maxY) {
      this.maxX = maxX;
      this.maxY = maxY;
      return this;
   }

   public Rectanglei setMax(Vector2ic max) {
      this.maxX = max.x();
      this.maxY = max.y();
      return this;
   }

   public int lengthX() {
      return this.maxX - this.minX;
   }

   public int lengthY() {
      return this.maxY - this.minY;
   }

   public int area() {
      return this.lengthX() * this.lengthY();
   }

   public Rectanglei union(int x, int y) {
      return this.union(x, y, this);
   }

   public Rectanglei union(Vector2ic p) {
      return this.union(p.x(), p.y(), this);
   }

   public Rectanglei union(int x, int y, Rectanglei dest) {
      dest.minX = this.minX < x ? this.minX : x;
      dest.minY = this.minY < y ? this.minY : y;
      dest.maxX = this.maxX > x ? this.maxX : x;
      dest.maxY = this.maxY > y ? this.maxY : y;
      return dest;
   }

   public Rectanglei union(Vector2ic p, Rectanglei dest) {
      return this.union(p.x(), p.y(), dest);
   }

   public Rectanglei union(Rectanglei other) {
      return this.union(other, this);
   }

   public Rectanglei union(Rectanglei other, Rectanglei dest) {
      dest.minX = this.minX < other.minX ? this.minX : other.minX;
      dest.minY = this.minY < other.minY ? this.minY : other.minY;
      dest.maxX = this.maxX > other.maxX ? this.maxX : other.maxX;
      dest.maxY = this.maxY > other.maxY ? this.maxY : other.maxY;
      return dest;
   }

   public boolean intersectsRectangle(Rectangled other) {
      return this.minX <= other.maxX && this.maxX >= other.minX && this.maxY >= other.minY && this.minY <= other.maxY;
   }

   public boolean intersectsRectangle(Rectanglef other) {
      return this.minX <= other.maxX && this.maxX >= other.minX && this.maxY >= other.minY && this.minY <= other.maxY;
   }

   public boolean intersectsRectangle(Rectanglei other) {
      return this.minX <= other.maxX && this.maxX >= other.minX && this.maxY >= other.minY && this.minY <= other.maxY;
   }

   private Rectanglei validate() {
      if (!this.isValid()) {
         this.minX = Integer.MAX_VALUE;
         this.minY = Integer.MAX_VALUE;
         this.maxX = Integer.MIN_VALUE;
         this.maxY = Integer.MIN_VALUE;
      }

      return this;
   }

   public boolean isValid() {
      return this.minX < this.maxX && this.minY < this.maxY;
   }

   public Rectanglei intersection(Rectanglei other) {
      return this.intersection(other, this);
   }

   public Rectanglei intersection(Rectanglei other, Rectanglei dest) {
      dest.minX = Math.max(this.minX, other.minX);
      dest.minY = Math.max(this.minY, other.minY);
      dest.maxX = Math.min(this.maxX, other.maxX);
      dest.maxY = Math.min(this.maxY, other.maxY);
      return dest.validate();
   }

   public Vector2i lengths(Vector2i dest) {
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

   public boolean containsPoint(Vector2ic point) {
      return this.containsPoint(point.x(), point.y());
   }

   public boolean containsPoint(float x, float y) {
      return x > this.minX && y > this.minY && x < this.maxX && y < this.maxY;
   }

   public boolean containsPoint(Vector2fc point) {
      return this.containsPoint(point.x(), point.y());
   }

   public boolean containsPoint(int x, int y) {
      return x > this.minX && y > this.minY && x < this.maxX && y < this.maxY;
   }

   public Rectanglei translate(Vector2ic xy) {
      return this.translate(xy.x(), xy.y(), this);
   }

   public Rectanglei translate(Vector2ic xy, Rectanglei dest) {
      return this.translate(xy.x(), xy.y(), dest);
   }

   public Rectanglei translate(int x, int y) {
      return this.translate(x, y, this);
   }

   public Rectanglei translate(int x, int y, Rectanglei dest) {
      dest.minX = this.minX + x;
      dest.minY = this.minY + y;
      dest.maxX = this.maxX + x;
      dest.maxY = this.maxY + y;
      return dest;
   }

   public Rectanglei scale(int sf) {
      return this.scale(sf, sf);
   }

   public Rectanglei scale(int sf, Rectanglei dest) {
      return this.scale(sf, sf, dest);
   }

   public Rectanglei scale(int sf, int ax, int ay) {
      return this.scale(sf, sf, ax, ay);
   }

   public Rectanglei scale(int sf, int ax, int ay, Rectanglei dest) {
      return this.scale(sf, sf, ax, ay, dest);
   }

   public Rectanglei scale(int sf, Vector2ic anchor) {
      return this.scale(sf, anchor.x(), anchor.y());
   }

   public Rectanglei scale(int sf, Vector2ic anchor, Rectanglei dest) {
      return this.scale(sf, anchor.x(), anchor.y(), dest);
   }

   public Rectanglei scale(int sx, int sy) {
      return this.scale(sx, sy, 0, 0);
   }

   public Rectanglei scale(int sx, int sy, Rectanglei dest) {
      return this.scale(sx, sy, 0, 0, dest);
   }

   public Rectanglei scale(int sx, int sy, int ax, int ay) {
      this.minX = (this.minX - ax) * sx + ax;
      this.minY = (this.minY - ay) * sy + ay;
      this.maxX = (this.maxX - ax) * sx + ax;
      this.maxY = (this.maxY - ay) * sy + ay;
      return this;
   }

   public Rectanglei scale(int sx, int sy, Vector2ic anchor) {
      return this.scale(sx, sy, anchor.x(), anchor.y());
   }

   public Rectanglei scale(int sx, int sy, int ax, int ay, Rectanglei dest) {
      dest.minX = (this.minX - ax) * sx + ax;
      dest.minY = (this.minY - ay) * sy + ay;
      dest.maxX = (this.maxX - ax) * sx + ax;
      dest.maxY = (this.maxY - ay) * sy + ay;
      return dest;
   }

   public Rectanglei scale(int sx, int sy, Vector2ic anchor, Rectanglei dest) {
      return this.scale(sx, sy, anchor.x(), anchor.y(), dest);
   }

   public int hashCode() {
      int prime = 31;
      int result = 1;
      result = 31 * result + this.maxX;
      result = 31 * result + this.maxY;
      result = 31 * result + this.minX;
      return 31 * result + this.minY;
   }

   public boolean equals(Object obj) {
      if (this == obj) {
         return true;
      } else if (obj == null) {
         return false;
      } else if (this.getClass() != obj.getClass()) {
         return false;
      } else {
         Rectanglei other = (Rectanglei)obj;
         if (this.maxX != other.maxX) {
            return false;
         } else if (this.maxY != other.maxY) {
            return false;
         } else {
            return this.minX != other.minX ? false : this.minY == other.minY;
         }
      }
   }

   public String toString() {
      return Runtime.formatNumbers(this.toString(Options.NUMBER_FORMAT));
   }

   public String toString(NumberFormat formatter) {
      return "("
         + formatter.format(this.minX)
         + " "
         + formatter.format(this.minY)
         + ") < ("
         + formatter.format(this.maxX)
         + " "
         + formatter.format(this.maxY)
         + ")";
   }

   public void writeExternal(ObjectOutput out) throws IOException {
      out.writeInt(this.minX);
      out.writeInt(this.minY);
      out.writeInt(this.maxX);
      out.writeInt(this.maxY);
   }

   public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
      this.minX = in.readInt();
      this.minY = in.readInt();
      this.maxX = in.readInt();
      this.maxY = in.readInt();
   }
}
