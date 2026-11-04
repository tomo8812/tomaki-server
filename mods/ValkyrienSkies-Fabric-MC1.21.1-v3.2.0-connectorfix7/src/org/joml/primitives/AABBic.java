package org.joml.primitives;

import org.joml.Matrix4fc;
import org.joml.Vector2f;
import org.joml.Vector3d;
import org.joml.Vector3f;
import org.joml.Vector3fc;
import org.joml.Vector3ic;

public interface AABBic {
   int minX();

   int minY();

   int minZ();

   int maxX();

   int maxY();

   int maxZ();

   boolean isValid();

   int getMax(int var1);

   int getMin(int var1);

   Vector3f center(Vector3f var1);

   Vector3d center(Vector3d var1);

   Vector3d extent(Vector3d var1);

   Vector3f extent(Vector3f var1);

   AABBi union(int var1, int var2, int var3, AABBi var4);

   AABBi union(Vector3ic var1, AABBi var2);

   AABBi union(AABBic var1, AABBi var2);

   AABBi translate(int var1, int var2, int var3, AABBi var4);

   AABBi intersection(AABBic var1, AABBi var2);

   boolean containsAABB(AABBdc var1);

   boolean containsAABB(AABBfc var1);

   boolean containsAABB(AABBic var1);

   boolean containsPoint(int var1, int var2, int var3);

   boolean containsPoint(float var1, float var2, float var3);

   boolean containsPoint(Vector3ic var1);

   boolean containsPoint(Vector3fc var1);

   boolean intersectsPlane(float var1, float var2, float var3, float var4);

   boolean intersectsPlane(Planef var1);

   boolean intersectsAABB(AABBic var1);

   boolean intersectsAABB(AABBfc var1);

   boolean intersectsSphere(float var1, float var2, float var3, float var4);

   boolean intersectsSphere(Spheref var1);

   boolean intersectsRay(float var1, float var2, float var3, float var4, float var5, float var6);

   boolean intersectsRay(Rayf var1);

   boolean intersectsRay(float var1, float var2, float var3, float var4, float var5, float var6, Vector2f var7);

   boolean intersectsRay(Rayf var1, Vector2f var2);

   int intersectLineSegment(float var1, float var2, float var3, float var4, float var5, float var6, Vector2f var7);

   int intersectLineSegment(LineSegmentf var1, Vector2f var2);

   AABBi transform(Matrix4fc var1, AABBi var2);
}
