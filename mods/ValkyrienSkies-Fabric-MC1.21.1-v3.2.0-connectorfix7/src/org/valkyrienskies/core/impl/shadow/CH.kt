package org.valkyrienskies.core.impl.shadow

import com.fasterxml.jackson.databind.JsonNode
import java.lang.reflect.Modifier
import java.util.HashMap
import java.util.HashSet
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.jvm.internal.PropertyReference1
import kotlin.jvm.internal.PropertyReference1Impl
import kotlin.jvm.internal.Reflection
import kotlin.reflect.KProperty
import org.apache.logging.log4j.Logger
import org.valkyrienskies.core.api.attachment.AttachmentRegistration
import org.valkyrienskies.core.api.attachment.AttachmentSerializer
import org.valkyrienskies.core.api.attachment.BinaryAttachmentSerializer
import org.valkyrienskies.core.api.attachment.JacksonAttachmentSerializer
import org.valkyrienskies.core.api.attachment.LegacyAttachmentSerializer
import org.valkyrienskies.core.api.attachment.TransientAttachmentSerializer
import org.valkyrienskies.core.api.attachment.AttachmentRegistration.Builder
import org.valkyrienskies.core.impl.util.serialization.VSJacksonUtil

@Singleton
public class CH @Inject  public constructor(p0: CN) {
   @JvmStatic
   @JvmSynthetic
   private KProperty<Object>[] a;
   private final val d: CN
   private final val e: CN

   public final val f: Logger
      public final get() {
         val var2: KProperty = a[0]
         val var1: Logger = this.d
         return var1
      }


   private final val c: HashSet<String>
   private final val a: HashMap<Class<*>, CD>
   private final val b: HashMap<String, CD>

   init {
      this.b = var1
      this.c = CN(VSJacksonUtil.INSTANCE.defaultMapper)
      this.d = Gb.a("(VS Core) AttachmentRegistry")
      this.e = HashSet<>()
      this.f = HashMap<>()
      this.g = HashMap<>()
   }

   @JvmStatic
   private fun b(p0: AttachmentSerializer): Boolean {
      return var0 is JacksonAttachmentSerializer
         || var0 is TransientAttachmentSerializer
         || var0 is BinaryAttachmentSerializer
         || var0 is LegacyAttachmentSerializer
      }

   private fun c(p0: Class<*>) {
      if (this.f.containsKey(var1)) {
         throw IllegalArgumentException(("Attachment $var1 cannot be registered because it was already registered").toString())
      }
   }

   private fun a(p0: String, p1: Class<*>?) {
      var var10000: java.lang.String
      run label37@{
         if (var2 != null) {
            var10000 = var2.toString()
            if (var10000 != null) {
               return@label37
            }
         }

         var10000 = ""
      }

      if (this.g.containsKey(var1)) {
         throw IllegalArgumentException(
            ("Attachment $var10000 cannot be registered because another attachment with the same key, '$var1', was already registered").toString()
         )
      } else if (this.e.contains(var1)) {
         throw IllegalArgumentException(("Attachment $var10000 cannot be registered because the key '$var1' was already registered for removal").toString())
      }
   }

   public fun a(p0: String) {
      this.a(var1, (Class<?>)null)
      this.e.add(var1)
   }

   public fun <T> a(p0: Class<Any>, p1: (Builder<Any>) -> Unit) {
      val var3: CF = CF(var1)
      var2(var3)
      this.a(var3.build())
   }

   public fun <T> a(p0: AttachmentRegistration<Any>) {
      val var2: Class
      if (!Modifier.isFinal((var2 = var1.attachmentClass).getModifiers())) {
         throw IllegalArgumentException(("Attachment $var2 cannot be registered because it is not final").toString())
      } else if (var2.isLocalClass() || var2.isAnonymousClass()) {
         throw IllegalArgumentException(("Attachment $var2 cannot be registered because it is local or anonymous").toString())
      } else {
         val var3: CL = (var1 as CG).a
         if (this.a((var1 as CG).a)) {
            throw IllegalArgumentException(
               ("Attachment $var2 cannot be registered because adding an updater from ${if (var3 != null) var3.a else null} to ${if (var3 != null)
                     var3.b
                     else
                     null} will cause a cycle!")
                  .toString()
            )
         } else if (!b(var1.serializer)) {
            throw IllegalArgumentException(("Attachment $var2 cannot be registered with invalid serializer ${var1.serializer.getClass()::class}").toString())
         } else {
            val var13: java.lang.String = var1.key
            val var6: CD = CD(var2, var13, var1.serializer, var3)
            this.c(var2)
            this.a(var13, var2)
            if (this.f.putIfAbsent(var2, var6) != null) {
               throw IllegalArgumentException(("$var2 was already in the map").toString())
            } else if (this.g.putIfAbsent(var13, var6) != null) {
               throw IllegalArgumentException(("$var13 was already in the map").toString())
            }
         }
      }
   }

   private fun a(p0: CL?): Boolean {
      return var1 != null && this.e(var1.b) == var1.a
   }

   public fun a(p0: Class<*>): CD? {
      return this.f.get(var1)
   }

   public fun b(p0: String): CD? {
      return this.g.get(var1)
   }

   public fun b(p0: Class<*>): CD {
      val var10000: CD = this.a(var1)
      if (var10000 == null) {
         throw IllegalArgumentException(("$var1 is not registered as an attachment!").toString())
      } else {
         return var10000
      }
   }

   public fun a(p0: Any): JsonNode? {
      return this.a(var1, this.b(var1.getClass()).c)
   }

   public fun a(p0: String, p1: JsonNode): CE {
      if (this.e.contains(var1)) {
         return CE.e.INSTANCE
      } else {
         val var10000: CD = this.g.get(var1)
         if (var10000 == null) {
            return CE.a("Deserialization was requested for attachment with key $var1 but no attachment with that key was registered")
         } else {
            val var4: Class = var10000.a
            val var5: AttachmentSerializer = var10000.c
            if (var10000.c == TransientAttachmentSerializer.INSTANCE) {
               return CE.c.INSTANCE
            } else {
               var var6: Any
               try {
                  val var17: Any = this.a(var5).a(var2, var4)
                  if (var17 == null) {
                     return CE.a("Deserialization failed for attachment $var1 (strategy = $var5, clazz = $var4)")
                  }

                  var6 = var17
               } catch (var9: Exception) {
                  return CE.a("Deserialization failed for attachment $var1 with exception (strategy = $var5, clazz = $var4) $var9")
               }

               var var18: CE.b = (CE.b)this.a(var6, var10000)
               if (var18 == null) {
                  var18 = CE.b
                  val var10002: java.lang.String = "Failed to update $var1"
                  val var16: Class = this.e(var4)
                  val var10003: Any = this.f.get(var16)
                  if (var10003 == null) {
                     throw IllegalArgumentException(
                        ("getFinalUpdateKey: could not find the key for the end of the upgrade chain ($var16) for initial attachment ($var4). This should not be possible because the initial and final attachment class of every updater should be registered.")
                           .toString()
                     )
                  } else {
                     var18./* $VF: Unable to resugar constructor */<init>(var10002, (var10003 as CD).b)
                     return var18
                  }
               } else {
                  return if (var18 === var6) CE.d(var18) else CE.f(var18)
               }
            }
         }
      }
   }

   private fun d(p0: Class<*>): String {
      val var2: Class = this.e(var1)
      val var10000: Any = this.f.get(var2)
      if (var10000 == null) {
         throw IllegalArgumentException(
            ("getFinalUpdateKey: could not find the key for the end of the upgrade chain ($var2) for initial attachment ($var1). This should not be possible because the initial and final attachment class of every updater should be registered.")
               .toString()
         )
      } else {
         return (var10000 as CD).b
      }
   }

   private fun e(p0: Class<*>): Class<*> {
      var var2: Class = var1
      var1 = var1

      while (var1 != null) {
         var2 = var1
         val var10000: CD = this.f.get(var1)
         var1 = if (var10000 != null && var10000.d != null) var10000.d.b else null
      }

      return var2
   }

   private fun a(p0: Any, p1: CD): Any? {
      if (var2.d == null) {
         return var1
      } else {
         val var6: CL = var2.d

         try {
            val var10000: Any = var6.c(var1)
            if (var10000 == null) {
               return null
            } else {
               label33@
               if (!(var6.b == var10000.getClass())) {
                  return null
               } else {
                  val var8: CD = this.f.get(var6.b)
                  return if (var8 == null) null else this.a(var10000, var8)
               }
            }
         } catch (var4: Exception) {
            return null
         }
      }
   }

   private fun a(p0: AttachmentSerializer): CK {
      if (var1 is JacksonAttachmentSerializer) {
         return this.b
      } else if (var1 is BinaryAttachmentSerializer) {
         return CM(var1 as BinaryAttachmentSerializer<*>)
      } else if (var1 is TransientAttachmentSerializer) {
         throw IllegalArgumentException("Cannot convert TransientAttachmentSerializer. Please handle this case separately")
      } else if (var1 is LegacyAttachmentSerializer) {
         return this.c
      } else {
         throw IllegalArgumentException("Unrecognized serializer ${var1.getClass()::class}")
      }
   }

   private fun a(p0: Any, p1: AttachmentSerializer): JsonNode? {
      return if (var2 == TransientAttachmentSerializer.INSTANCE) null else this.a(var2).a(var1)
   }

   private fun a(p0: Class<*>, p1: JsonNode, p2: AttachmentSerializer): Any? {
      return this.a(var3).a(var2, var1)
   }

   @JvmStatic
   fun {
      val var0: Array<KProperty>
      (var0 = arrayOfNulls(1))[0] = Reflection.property1(
         PropertyReference1Impl(CH.class, "log", "getLog()Lorg/apache/logging/log4j/Logger;", 0) as PropertyReference1
      )
      a = var0
   }
}
