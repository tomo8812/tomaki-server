package org.valkyrienskies.core.impl.shadow

import java.util.ArrayList
import java.util.HashMap
import java.util.function.Predicate
import kotlin.jvm.internal.CollectionToArray
import org.joml.primitives.AABBdc
import org.valkyrienskies.core.api.ships.Ship
import org.valkyrienskies.core.internal.ships.VsiMutableQueryableShipData
import org.valkyrienskies.core.util.AABBdUtilKt

public open class Eo<ShipType extends Ship>(p0: Iterable<Any> = CollectionsKt.emptyList() as java.lang.Iterable) : VsiMutableQueryableShipData<ShipType> {
   private final val a: HashMap<Long, Any> = HashMap()
   private final val b: Ds<Any> = Ds()

   @Deprecated(message = "Use the specific functions instead, such as #getById or #iterator")
   public open val c: Map<Long, Any>
      public open get() {
         return this.a;
      }


   public override operator fun iterator(): MutableIterator<Any> {
      // $VF: Couldn't be decompiled
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      // java.lang.IllegalStateException: Anonymous class does not have Class Kotlin metadata
      //   at org.vineflower.kotlin.KotlinWriter.writeClassDefinition(KotlinWriter.java:742)
      //   at org.vineflower.kotlin.KotlinWriter.writeClass(KotlinWriter.java:309)
      //   at org.vineflower.kotlin.expr.KNewExprent.toJava(KNewExprent.java:178)
      //   at org.vineflower.kotlin.expr.KFunctionExprent.toJava(KFunctionExprent.java:196)
      //   at org.jetbrains.java.decompiler.modules.decompiler.ExprProcessor.getCastedExprent(ExprProcessor.java:1054)
      //   at org.jetbrains.java.decompiler.modules.decompiler.exps.ExitExprent.toJava(ExitExprent.java:85)
      //
      // Bytecode:
      // 00: aload 0
      // 01: getfield org/valkyrienskies/core/impl/shadow/Eo.a Ljava/util/HashMap;
      // 04: invokevirtual java/util/HashMap.values ()Ljava/util/Collection;
      // 07: invokeinterface java/util/Collection.iterator ()Ljava/util/Iterator; 1
      // 0c: astore 1
      // 0d: new org/valkyrienskies/core/impl/shadow/Eo$a
      // 10: dup
      // 11: aload 1
      // 12: aload 0
      // 13: invokespecial org/valkyrienskies/core/impl/shadow/Eo$a.<init> (Ljava/util/Iterator;Lorg/valkyrienskies/core/impl/shadow/Eo;)V
      // 16: checkcast java/util/Iterator
      // 19: areturn
   }

   public override fun add(p0: Any) {
      if (this.getById(ship.id) != null) {
         throw IllegalArgumentException("Adding ship id:${ship.id} failed because of duplicated ID.");
      } else {
         this.a.put(ship.id, (ShipType)ship);
         this.b.a(ship.chunkClaim, (ShipType)ship);
      }
   }

   public override fun remove(p0: Any) {
      this.remove(ship.id);
   }

   public override fun getById(p0: Long): Any? {
      return this.a.get(shipId);
   }

   public override fun getByChunkPos(p0: Int, p1: Int): Any? {
      return this.b.a(chunkX, chunkZ);
   }

   public override fun getByChunkPos(p0: Int, p1: Int, p2: String): Any? {
      val var4: Ship;
      return (ShipType)(if ((var4 = this.b.a(chunkX, chunkZ)) != null && var4.chunkClaimDimension == dimensionId) var4 else null);
   }

   public override fun remove(p0: Long): Any {
      val var10000: Ship = this.getById(id);
      if (var10000 == null) {
         throw IllegalArgumentException("Removing ship id:$id failed because it couldn't be found.");
      } else {
         this.a.remove(var10000.id);
         this.b.a(var10000.chunkClaim);
         return (ShipType)var10000;
      }
   }

   public open val d: Int
      public open get() {
         return this.a.size();
      }


   @Deprecated(message = "This gets all ships in the AABB, irrespective of dimension. This is pretty much never what you want.", replaceWith = @ReplaceWith(expression = "getIntersecting(aabb, dimensionId)", imports = []))
   public override fun getIntersecting(p0: AABBdc): Iterable<Any> {
      val var6: java.lang.Iterable = this;
      val var3: java.util.Collection = ArrayList();
      val var7: java.util.Iterator = var6.iterator();

      while (var7.hasNext()) {
         val var4: Any;
         if (AABBdUtilKt.intersectsAABB(((var4 = var7.next()) as Ship).worldAABB, aabb)) {
            var3.add(var4);
         }
      }

      return var3;
   }

   public override fun getIntersecting(p0: AABBdc, p1: String): Iterable<Any> {
      val var7: java.lang.Iterable = this;
      val var4: java.util.Collection = ArrayList();
      val var8: java.util.Iterator = var7.iterator();

      while (var8.hasNext()) {
         val var5: Any;
         val var6: Ship;
         if (AABBdUtilKt.intersectsAABB((var6 = (var5 = var8.next()) as Ship).worldAABB, aabb) && var6.chunkClaimDimension == dimensionId) {
            var4.add(var5);
         }
      }

      return var4;
   }

   public override operator fun equals(p0: Any?): Boolean {
      if (this === other) {
         return true;
      } else if (!(this.getClass() == (if (other != null) other.getClass() else null))) {
         return false;
      } else {
         return this.a == (other as Eo).a;
      }
   }

   public override fun hashCode(): Int {
      return this.a.hashCode();
   }

   @JvmStatic
   fun c(): Boolean {
      throw UnsupportedOperationException("Operation is not supported for read-only collection");
   }

   override fun addAll(elements: MutableCollection<ShipType>): Boolean {
      throw UnsupportedOperationException("Operation is not supported for read-only collection");
   }

   override fun clear() {
      throw UnsupportedOperationException("Operation is not supported for read-only collection");
   }

   override fun remove(element: Any): Boolean {
      throw UnsupportedOperationException("Operation is not supported for read-only collection");
   }

   override fun removeAll(elements: MutableCollection<Any>): Boolean {
      throw UnsupportedOperationException("Operation is not supported for read-only collection");
   }

   override fun removeIf(p0: Predicate<in ShipType>): Boolean {
      throw UnsupportedOperationException("Operation is not supported for read-only collection");
   }

   override fun retainAll(elements: MutableCollection<Any>): Boolean {
      throw UnsupportedOperationException("Operation is not supported for read-only collection");
   }

   open fun Eo() {
      this(null, 1, null);
   }

   // $VF: bridge methodSupplier
   override final fun size(): Int {
      this.a.size();
   }

   @JvmSynthetic
   override fun add(element: Any): Boolean {
      throw UnsupportedOperationException("Operation is not supported for read-only collection");
   }

   override fun <T> toArray(array: Array<T>): Array<T> {
      CollectionToArray.toArray(this, array);
   }

   override fun toArray(): Array<Any> {
      CollectionToArray.toArray(this);
   }
}
