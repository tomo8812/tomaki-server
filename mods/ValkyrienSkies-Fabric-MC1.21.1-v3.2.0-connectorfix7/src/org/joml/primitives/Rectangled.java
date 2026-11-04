package org.joml.primitives;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.text.NumberFormat;
import org.joml.Math;
import org.joml.Options;
import org.joml.Runtime;
import org.joml.Vector2d;
import org.joml.Vector2dc;
import org.joml.Vector2fc;

public class Rectangled implements Externalizable {
   public double minX;
   public double minY;
   public double maxX;
   public double maxY;

   public Rectangled() {
   }

   public Rectangled(Rectangled source) {
      this.minX = source.minX;
      this.minY = source.minY;
      this.maxX = source.maxX;
      this.maxY = source.maxY;
   }

   public Rectangled(Vector2dc min, Vector2dc max) {
      this.minX = min.x();
      this.minY = min.y();
      this.maxX = max.x();
      this.maxY = max.y();
   }

   public Rectangled(double minX, double minY, double maxX, double maxY) {
      this.minX = minX;
      this.minY = minY;
      this.maxX = maxX;
      this.maxY = maxY;
   }

   public Rectangled set(Rectangled source) {
      this.minX = source.minX;
      this.minY = source.minY;
      this.maxX = source.maxX;
      this.maxY = source.maxY;
      return this;
   }

   public Rectangled setMin(double minX, double minY) {
      this.minX = minX;
      this.minY = minY;
      return this;
   }

   public Rectangled setMin(Vector2dc min) {
      this.minX = min.x();
      this.minY = min.y();
      return this;
   }

   public Rectangled setMax(double maxX, double maxY) {
      this.maxX = maxX;
      this.maxY = maxY;
      return this;
   }

   public Rectangled setMax(Vector2dc max) {
      this.maxX = max.x();
      this.maxY = max.y();
      return this;
   }

   public double lengthX() {
      return this.maxX - this.minX;
   }

   public double lengthY() {
      return this.maxY - this.minY;
   }

   public double area() {
      return this.lengthX() * this.lengthY();
   }

   private Rectangled validate() {
      if (!this.isValid()) {
         this.minX = Double.NaN;
         this.minY = Double.NaN;
         this.maxX = Double.NaN;
         this.maxY = Double.NaN;
      }

      return this;
   }

   public boolean isValid() {
      return this.minX < this.maxX && this.minY < this.maxY;
   }

   public Rectangled intersection(Rectangled other) {
      return this.intersection(other, this);
   }

   public Rectangled intersection(Rectangled other, Rectangled dest) {
      dest.minX = Math.max(this.minX, other.minX);
      dest.minY = Math.max(this.minY, other.minY);
      dest.maxX = Math.min(this.maxX, other.maxX);
      dest.maxY = Math.min(this.maxY, other.maxY);
      return dest.validate();
   }

   public Rectangled intersection(Rectanglef other, Rectangled dest) {
      dest.minX = Math.max(this.minX, other.minX);
      dest.minY = Math.max(this.minY, other.minY);
      dest.maxX = Math.min(this.maxX, other.maxX);
      dest.maxY = Math.min(this.maxY, other.maxY);
      return dest.validate();
   }

   public Rectangled intersection(Rectanglei other, Rectangled dest) {
      dest.minX = Math.max(this.minX, other.minX);
      dest.minY = Math.max(this.minY, other.minY);
      dest.maxX = Math.min(this.maxX, other.maxX);
      dest.maxY = Math.min(this.maxY, other.maxY);
      return dest.validate();
   }

   public Vector2d lengths(Vector2d dest) {
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

   public Rectangled union(double x, double y) {
      return this.union(x, y, this);
   }

   public Rectangled union(Vector2dc p) {
      return this.union(p.x(), p.y(), this);
   }

   public Rectangled union(double x, double y, Rectangled dest) {
      dest.minX = this.minX < x ? this.minX : x;
      dest.minY = this.minY < y ? this.minY : y;
      dest.maxX = this.maxX > x ? this.maxX : x;
      dest.maxY = this.maxY > y ? this.maxY : y;
      return dest;
   }

   public Rectangled union(Vector2dc p, Rectangled dest) {
      return this.union(p.x(), p.y(), dest);
   }

   public Rectangled union(Rectangled other) {
      return this.union(other, this);
   }

   public Rectangled union(Rectangled other, Rectangled dest) {
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

   public boolean containsPoint(Vector2dc point) {
      return this.containsPoint(point.x(), point.y());
   }

   public boolean containsPoint(double x, double y) {
      return x > this.minX && y > this.minY && x < this.maxX && y < this.maxY;
   }

   public Rectangled translate(Vector2dc xy) {
      return this.translate(xy.x(), xy.y(), this);
   }

   public Rectangled translate(Vector2dc xy, Rectangled dest) {
      return this.translate(xy.x(), xy.y(), dest);
   }

   public Rectangled translate(Vector2fc xy) {
      return this.translate(xy.x(), xy.y(), this);
   }

   public Rectangled translate(Vector2fc xy, Rectangled dest) {
      return this.translate(xy.x(), xy.y(), dest);
   }

   public Rectangled translate(double x, double y) {
      return this.translate(x, y, this);
   }

   public Rectangled translate(double x, double y, Rectangled dest) {
      dest.minX = this.minX + x;
      dest.minY = this.minY + y;
      dest.maxX = this.maxX + x;
      dest.maxY = this.maxY + y;
      return dest;
   }

   public Rectangled scale(double sf) {
      return this.scale(sf, sf);
   }

   public Rectangled scale(double sf, Rectangled dest) {
      return this.scale(sf, sf, dest);
   }

   public Rectangled scale(double sf, double ax, double ay) {
      return this.scale(sf, sf, ax, ay);
   }

   public Rectangled scale(double sf, double ax, double ay, Rectangled dest) {
      return this.scale(sf, sf, ax, ay, dest);
   }

   public Rectangled scale(double sf, Vector2dc anchor) {
      return this.scale(sf, anchor.x(), anchor.y());
   }

   public Rectangled scale(double sf, Vector2dc anchor, Rectangled dest) {
      return this.scale(sf, anchor.x(), anchor.y(), dest);
   }

   public Rectangled scale(double sx, double sy) {
      return this.scale(sx, sy, 0.0, 0.0);
   }

   public Rectangled scale(double sx, double sy, Rectangled dest) {
      return this.scale(sx, sy, 0.0, 0.0, dest);
   }

   public Rectangled scale(double sx, double sy, double ax, double ay) {
      this.minX = (this.minX - ax) * sx + ax;
      this.minY = (this.minY - ay) * sy + ay;
      this.maxX = (this.maxX - ax) * sx + ax;
      this.maxY = (this.maxY - ay) * sy + ay;
      return this;
   }

   public Rectangled scale(double sx, double sy, Vector2dc anchor) {
      return this.scale(sx, sy, anchor.x(), anchor.y());
   }

   public Rectangled scale(double sx, double sy, double ax, double ay, Rectangled dest) {
      dest.minX = (this.minX - ax) * sx + ax;
      dest.minY = (this.minY - ay) * sy + ay;
      dest.maxX = (this.maxX - ax) * sx + ax;
      dest.maxY = (this.maxY - ay) * sy + ay;
      return dest;
   }

   public Rectangled scale(double sx, double sy, Vector2dc anchor, Rectangled dest) {
      return this.scale(sx, sy, anchor.x(), anchor.y(), dest);
   }

   public int hashCode() {
      int prime = 31;
      int result = 1;
      long temp = Double.doubleToLongBits(this.maxX);
      result = 31 * result + (int)(temp ^ temp >>> 32);
      temp = Double.doubleToLongBits(this.maxY);
      result = 31 * result + (int)(temp ^ temp >>> 32);
      temp = Double.doubleToLongBits(this.minX);
      result = 31 * result + (int)(temp ^ temp >>> 32);
      temp = Double.doubleToLongBits(this.minY);
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
         Rectangled other = (Rectangled)obj;
         if (Double.doubleToLongBits(this.maxX) != Double.doubleToLongBits(other.maxX)) {
            return false;
         } else if (Double.doubleToLongBits(this.maxY) != Double.doubleToLongBits(other.maxY)) {
            return false;
         } else {
            return Double.doubleToLongBits(this.minX) != Double.doubleToLongBits(other.minX)
               ? false
               : Double.doubleToLongBits(this.minY) == Double.doubleToLongBits(other.minY);
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
      out.writeDouble(this.minX);
      out.writeDouble(this.minY);
      out.writeDouble(this.maxX);
      out.writeDouble(this.maxY);
   }

   public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
      this.minX = in.readDouble();
      this.minY = in.readDouble();
      this.maxX = in.readDouble();
      this.maxY = in.readDouble();
   }
}
