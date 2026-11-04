package org.valkyrienskies.core.impl.shadow

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.node.ObjectNode
import java.util.ArrayList
import java.util.LinkedHashMap
import java.util.LinkedHashSet
import java.util.Map.Entry
import java.util.concurrent.locks.Lock
import java.util.concurrent.locks.ReentrantLock
import java.util.function.Supplier
import javax.inject.Inject
import kotlin.jvm.internal.PropertyReference1
import kotlin.jvm.internal.PropertyReference1Impl
import kotlin.jvm.internal.Reflection
import kotlin.reflect.KProperty
import org.apache.logging.log4j.Logger
import org.valkyrienskies.core.api.attachment.AttachmentHolder
import org.valkyrienskies.core.api.attachment.AttachmentSerializer
import org.valkyrienskies.core.api.attachment.TransientAttachmentSerializer

public class CA @Inject  public constructor(p0: CH, p1: ObjectMapper) : AttachmentHolder {
   @JvmStatic
   @JvmSynthetic
   private KProperty<Object>[] g;
   private final val f: CH

   private final val e: Logger
      private final get() {
         val var2: KProperty = g[0];
         val var1: Logger = this.h;
         return var1;
      }


   private final val d: ReentrantLock
   private final var c: ObjectNode
   private final val g: MutableSet<String>
   private final val a: MutableMap<Class<*>, org.valkyrienskies.core.impl.shadow.CA.a>
   private final val b: MutableMap<String, String>

   init {
      this.a = var1;
      this.h = Gb.a("(VS Core) AttachmentHolder");
      this.b = ReentrantLock();
      val var10001: ObjectNode = var2.createObjectNode();
      this.c = var10001;
      this.d = LinkedHashSet<>();
      this.e = LinkedHashMap<>();
      this.f = LinkedHashMap<>();
   }

   public override fun getAllAttachments(): Iterable<Any> {
      val var1: Lock;
      (var1 = this.b).lock();

      var var9: java.util.List;
      try {
         val var3: java.lang.Iterable = var9 = this.e.values();
         var9 = ArrayList(CollectionsKt.collectionSizeOrDefault(var9, 10));

         for (var4 in var3) {
            var9.add((var4 as CA.a).a);
         }

         var9 = var9;
      } finally {
         var1.unlock();
      }

      return var9;
   }

   public fun <T> a(p0: Class<Any>): Iterable<Any> {
      val var2: Lock;
      (var2 = this.b).lock();

      try {
         val var3: java.lang.Iterable = this.e.values();
         val var4: java.util.Collection = ArrayList();

         for (var5 in var3) {
            val var10000: Any = if (var1.isInstance(var5)) var1.cast(var5) else null;
            if (var10000 != null) {
               var4.add(var10000);
            }
         }

         var9 = var4 as java.util.List;
      } finally {
         var2.unlock();
      }

      return var9;
   }

   public override fun <T : Any> getAttachment(p0: Class<Any>): Any? {
      val var2: Lock;
      (var2 = this.b).lock();

      try {
         val var10000: CA.a = this.e.get(clazz);
         return (T)(if (var10000 != null) var10000.a else null);
      } finally {
         var2.unlock();
      }
   }

   public override fun <T : Any> getOrPutAttachment(p0: Class<Any>, p1: Supplier<Any>): Any {
      val var3: Lock;
      (var3 = this.b).lock();

      try {
         val var6: Any;
         if ((var6 = this.getAttachment(clazz)) != null) {
            return (T)var6;
         } else {
            val var10000: Any = supplier.get();
            this.setAttachment(var10000);
            return (T)var10000;
         }
      } finally {
         var3.unlock();
      }
   }

   public override fun <T : Any> setAttachment(p0: Any): Any? {
      val var2: Lock;
      (var2 = this.b).lock();

      try {
         val var3: Class = value.getClass();
         val var10000: CD = this.a.a(var3);
         if (var10000 == null) {
            throw IllegalArgumentException(
               ("setAttachment: attempted to set an attachment with unregistered $var3. Please register the attachment class.").toString()
            );
         } else if (var10000.d != null) {
            throw IllegalArgumentException(("setAttachment: failed to set attachment for $var3 because it has a registered updater.").toString());
         } else {
            val var5: java.lang.String;
            if ((var5 = this.f.get(var10000.b)) != null) {
               throw IllegalArgumentException(
                  ("setAttachment: attempted to set attachment for $var3 (key = ${var10000.b}). Cannot set this attachment because we tried to deserialize it earlier, and the deserialization failed. $var5")
                     .toString()
               );
            } else {
               this.d.remove(var10000.b);
               val var12: CA.a = this.e.put(var3, CA.a(value, var10000));
               return (T)(if (var12 != null) var12.a else null);
            }
         }
      } finally {
         var2.unlock();
      }
   }

   public override fun <T : Any> removeAttachment(p0: Class<Any>): Any? {
      val var2: Lock;
      (var2 = this.b).lock();

      try {
         val var10000: CD = this.a.a(clazz);
         if (var10000 == null) {
            throw IllegalArgumentException(
               ("removeAttachment: attempted to remove an attachment with unregistered class $clazz. Please register the attachment class.").toString()
            );
         } else {
            this.d.add(var10000.b);
            val var7: CA.a = this.e.remove(clazz);
            return (T)(if (var7 != null) var7.a else null);
         }
      } finally {
         var2.unlock();
      }
   }

   public fun a(p0: ObjectNode) {
      val var2: Lock;
      (var2 = this.b).lock();

      try {
         this.d.clear();
         this.c = var1;
         this.e.clear();
         val var10000: java.util.Iterator = var1.fields();
         val var8: java.util.Iterator = var10000;

         while (var8.hasNext()) {
            val var3: Entry;
            val var4: java.lang.String = var3.getKey() as java.lang.String;
            val var9: JsonNode = var3.getValue() as JsonNode;
            val var15: CH = this.a;
            val var10: CE;
            if ((var10 = var15.a(var4, var9)) is CE.d) {
               if (!this.a(var4, (var10 as CE.d).a)) {
               }
            } else if (var10 is CE.f) {
               if (this.a(var4, (var10 as CE.f).a)) {
                  this.d.add(var4);
               }
            } else if (var10 !is CE.c) {
               if (var10 is CE.a) {
                  this.f.put(var4, (var10 as CE.a).a);
               } else if (var10 is CE.b) {
                  this.f.put((var10 as CE.b).b, (var10 as CE.b).a);
               } else if (var10 is CE.e) {
                  this.d.add(var4);
               }
            }
         }
      } finally {
         var2.unlock();
      }
   }

   private fun a(p0: String, p1: Any): Boolean {
      try {
         this.setAttachment(var2);
         var2 = true;
      } catch (var5: IllegalArgumentException) {
         var var10000: java.lang.String = var5.getMessage();
         if (var10000 == null) {
            var10000 = var5.toString();
         }

         this.f.put(var1, var10000);
         val var4: KProperty = g[0];
         val var3: Logger = this.h;
         var3.warn("Skipping attachment '{}' while loading ship data: {}", var1, var10000);
         var7 = false;
      }

      return var7;
   }

   public fun a(): ObjectNode {
      val var1: Lock;
      (var1 = this.b).lock();

      try {
         val var2: ObjectNode;
         (var2 = this.c.deepCopy()).remove(this.d);
         val var3: java.util.Iterator = this.e.values().iterator();

         while (var3.hasNext()) {
            val var4: CA.a;
            val var5: java.lang.String = (var4 = var3.next() as CA.a).b.b;

            try {
               val var12: Any = var4.a;
               val var6: CH = this.a;
               val var16: AttachmentSerializer = var6.b(var12.getClass()).c;
               val var17: JsonNode = if (var16 == TransientAttachmentSerializer.INSTANCE) null else var6.a(var16).a(var12);
               if (var17 != null) {
                  var2.replace(var5, var17);
               }
            } catch (var10: Exception) {
            }
         }

         this.c = var2;
         this.d.clear();
         return var2;
      } finally {
         var1.unlock();
      }
   }

   @JvmStatic
   fun {
      val var0: Array<KProperty>;
      (var0 = arrayOfNulls(1))[0] = Reflection.property1(
         PropertyReference1Impl(CA.class, "log", "getLog()Lorg/apache/logging/log4j/Logger;", 0) as PropertyReference1
      );
      g = var0;
   }

   // $VF: Class flags could not be determined
   internal class a {
      Object a;
      CD b;

      fun a(var1: Any, var2: CD) {
         this.a = var1;
         this.b = var2;
      }

      fun a() {
         this.a;
      }

      fun b(): CD {
         this.b;
      }
   }
}
