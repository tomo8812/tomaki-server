package org.joml.primitives;

import org.joml.Matrix4dc;
import org.joml.Vector2d;
import org.joml.Vector3d;
import org.joml.Vector3dc;
import org.joml.Vector3f;
import org.joml.Vector3fc;

public interface AABBdc {
   double minX();

   double minY();

   double minZ();

   double maxX();

   double maxY();

   double maxZ();

   boolean isValid();

   double getMax(int var1);

   double getMin(int var1);

   Vector3d center(Vector3d var1);

   Vector3f center(Vector3f var1);

   Vector3d extent(Vector3d var1);

   Vector3f extent(Vector3f var1);

   AABBd union(double var1, double var3, double var5, AABBd var7);

   AABBd union(Vector3dc var1, AABBd var2);

   AABBd union(AABBdc var1, AABBd var2);

   AABBd translate(Vector3dc var1, AABBd var2);

   AABBd translate(Vector3fc var1, AABBd var2);

   AABBd translate(double var1, double var3, double var5, AABBd var7);

   AABBd intersection(AABBdc var1, AABBd var2);

   boolean containsAABB(AABBdc var1);

   boolean containsAABB(AABBfc var1);

   boolean containsAABB(AABBic var1);

   boolean containsPoint(double var1, double var3, double var5);

   boolean containsPoint(Vector3dc var1);

   boolean intersectsPlane(double var1, double var3, double var5, double var7);

   boolean intersectsPlane(Planed var1);

   boolean intersectsAABB(AABBd var1);

   boolean intersectsSphere(double var1, double var3, double var5, double var7);

   boolean intersectsSphere(Spheref var1);

   boolean intersectsRay(double var1, double var3, double var5, double var7, double var9, double var11);

   boolean intersectsRay(Rayd var1);

   boolean intersectsRay(double var1, double var3, double var5, double var7, double var9, double var11, Vector2d var13);

   boolean intersectsRay(Rayd var1, Vector2d var2);

   int intersectsLineSegment(double var1, double var3, double var5, double var7, double var9, double var11, Vector2d var13);

   int intersectsLineSegment(LineSegmentf var1, Vector2d var2);

   AABBd transform(Matrix4dc var1, AABBd var2);
}
