package org.joml.primitives;

import java.text.NumberFormat;
import org.joml.Matrix4fc;
import org.joml.Vector2f;
import org.joml.Vector3d;
import org.joml.Vector3f;
import org.joml.Vector3fc;

public interface AABBfc {
   float minX();

   float minY();

   float minZ();

   float maxX();

   float maxY();

   float maxZ();

   boolean isValid();

   float getMax(int var1);

   float getMin(int var1);

   Vector3f center(Vector3f var1);

   Vector3d center(Vector3d var1);

   Vector3d extent(Vector3d var1);

   Vector3f extent(Vector3f var1);

   AABBf union(float var1, float var2, float var3, AABBf var4);

   AABBf union(Vector3fc var1, AABBf var2);

   AABBf union(AABBf var1, AABBf var2);

   AABBf translate(Vector3fc var1, AABBf var2);

   AABBf translate(float var1, float var2, float var3, AABBf var4);

   AABBf intersection(AABBfc var1, AABBf var2);

   boolean containsAABB(AABBdc var1);

   boolean containsAABB(AABBfc var1);

   boolean containsAABB(AABBic var1);

   boolean containsPoint(float var1, float var2, float var3);

   boolean containsPoint(Vector3fc var1);

   boolean intersectsPlane(float var1, float var2, float var3, float var4);

   boolean intersectsPlane(Planef var1);

   boolean intersectsAABB(AABBfc var1);

   boolean intersectsSphere(float var1, float var2, float var3, float var4);

   boolean intersectsSphere(Spheref var1);

   boolean intersectsRay(float var1, float var2, float var3, float var4, float var5, float var6);

   boolean intersectsRay(Rayf var1);

   boolean intersectsRay(float var1, float var2, float var3, float var4, float var5, float var6, Vector2f var7);

   boolean intersectsRay(Rayf var1, Vector2f var2);

   int intersectsLineSegment(float var1, float var2, float var3, float var4, float var5, float var6, Vector2f var7);

   int intersectsLineSegment(LineSegmentf var1, Vector2f var2);

   AABBf transform(Matrix4fc var1, AABBf var2);

   String toString(NumberFormat var1);
}
