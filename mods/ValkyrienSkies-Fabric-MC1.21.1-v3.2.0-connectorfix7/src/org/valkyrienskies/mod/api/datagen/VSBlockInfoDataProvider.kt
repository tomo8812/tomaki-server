package org.valkyrienskies.mod.api.datagen

import com.google.gson.JsonArray
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.google.gson.JsonPrimitive
import java.nio.file.Path
import java.util.HashMap
import java.util.concurrent.CompletableFuture
import kotlin.jvm.internal.SourceDebugExtension
import net.minecraft.class_2248
import net.minecraft.class_2405
import net.minecraft.class_2960
import net.minecraft.class_6862
import net.minecraft.class_7403
import net.minecraft.class_7784

@SourceDebugExtension(["SMAP\nVSBlockInfoDataProvider.kt\nKotlin\n*S Kotlin\n*F\n+ 1 VSBlockInfoDataProvider.kt\norg/valkyrienskies/mod/api/datagen/VSBlockInfoDataProvider\n+ 2 fake.kt\nkotlin/jvm/internal/FakeKt\n+ 3 _Maps.kt\nkotlin/collections/MapsKt___MapsKt\n*L\n1#1,149:1\n1#2:150\n216#3,2:151\n*S KotlinDebug\n*F\n+ 1 VSBlockInfoDataProvider.kt\norg/valkyrienskies/mod/api/datagen/VSBlockInfoDataProvider\n*L\n118#1:151,2\n*E\n"])
public abstract class VSBlockInfoDataProvider : class_2405 {
   public final val output: class_7784
   public final val modId: String
   private final val entries: HashMap<class_2960, org.valkyrienskies.mod.api.datagen.VSBlockInfoDataProvider.Info>

   open fun VSBlockInfoDataProvider(output: class_7784, modId: java.lang.String) {
      this.output = output
      this.modId = modId
      this.entries = HashMap<>()
   }

   protected abstract fun registerEntries() {
   }

   protected fun addBlock(id: class_2960, mass: Double? = null, friction: Double? = null, elasticity: Double? = null, priority: Int? = null) {
      if (this.entries.get(id) != null) {
         throw RuntimeException("Duplicate Block Into Entries for $id")
      } else {
         this.entries.put(id, VSBlockInfoDataProvider.Info(false, mass, friction, elasticity, priority))
      }
   }

   protected fun addBlockTag(id: class_2960, mass: Double? = null, friction: Double? = null, elasticity: Double? = null, priority: Int? = null) {
      if (this.entries.get(id) != null) {
         throw RuntimeException("Duplicate Block Into Entries for $id")
      } else {
         this.entries.put(id, VSBlockInfoDataProvider.Info(true, mass, friction, elasticity, priority))
      }
   }

   protected fun addBlock(block: class_2248, mass: Double? = null, friction: Double? = null, elasticity: Double? = null, priority: Int? = null) {
      val var10001: class_2960 = block.method_40142().method_40237().method_29177()
      this.addBlock(var10001, mass, friction, elasticity, priority)
   }

   protected fun addBlockTag(tag: class_6862<class_2248>, mass: Double? = null, friction: Double? = null, elasticity: Double? = null, priority: Int? = null) {
      val var10001: class_2960 = tag.comp_327()
      this.addBlockTag(var10001, mass, friction, elasticity, priority)
   }

   open fun method_10319(cachedOutput: class_7403): CompletableFuture<*> {
      val var10000: Any = CompletableFuture.supplyAsync({ 
         val path: Path = `this$0`.output.method_45971().resolve("data").resolve("valkyrienskies").resolve("vs_mass").resolve("${`this$0`.modId}.json")
         `this$0`.registerEntries()

         try {
            val array: JsonArray = JsonArray(`this$0`.entries.size())

            for (`element$iv` in `this$0`.entries.entrySet()) {
               array.add((`element$iv`.getValue() as VSBlockInfoDataProvider.Info).toJson(`element$iv`.getKey() as class_2960) as JsonElement)
            }

            class_2405.method_10320(`$cachedOutput`, array as JsonElement, path)
         } catch (var12: Exception) {
            throw RuntimeException("Failed to save $path", var12)
         }
      }).get()
      var10000 as CompletableFuture
   }

   open fun method_10321(): java.lang.String {
      "VS Block Info Data Provider: ${this.modId}.json"
   }

   @SourceDebugExtension(["SMAP\nVSBlockInfoDataProvider.kt\nKotlin\n*S Kotlin\n*F\n+ 1 VSBlockInfoDataProvider.kt\norg/valkyrienskies/mod/api/datagen/VSBlockInfoDataProvider$Info\n+ 2 fake.kt\nkotlin/jvm/internal/FakeKt\n*L\n1#1,149:1\n1#2:150\n*E\n"])
   private data class Info(tag: Boolean, mass: Double?, friction: Double?, elasticity: Double?, priority: Int?) {
      public final val tag: Boolean
      public final val mass: Double?
      public final val friction: Double?
      public final val elasticity: Double?
      public final val priority: Int?

      init {
         this.tag = tag
         this.mass = mass
         this.friction = friction
         this.elasticity = elasticity
         this.priority = priority
      }

      public fun toJson(id: class_2960): JsonObject {
         val obj: JsonObject = JsonObject()
         obj.add(if (this.tag) "tag" else "block", JsonPrimitive(id.toString()) as JsonElement)
         if (this.mass != null) {
            obj.add("mass", JsonPrimitive(this.mass.doubleValue()) as JsonElement)
         }

         if (this.friction != null) {
            obj.add("friction", JsonPrimitive(this.friction.doubleValue()) as JsonElement)
         }

         if (this.elasticity != null) {
            obj.add("elasticity", JsonPrimitive(this.elasticity.doubleValue()) as JsonElement)
         }

         if (this.priority != null) {
            obj.add("priority", JsonPrimitive(this.priority.intValue()) as JsonElement)
         }

         return obj
      }

      public operator fun component1(): Boolean {
         return this.tag
      }

      public operator fun component2(): Double? {
         return this.mass
      }

      public operator fun component3(): Double? {
         return this.friction
      }

      public operator fun component4(): Double? {
         return this.elasticity
      }

      public operator fun component5(): Int? {
         return this.priority
      }

      public fun copy(
         tag: Boolean = this.tag,
         mass: Double? = this.mass,
         friction: Double? = this.friction,
         elasticity: Double? = this.elasticity,
         priority: Int? = this.priority
      ): org.valkyrienskies.mod.api.datagen.VSBlockInfoDataProvider.Info {
         return VSBlockInfoDataProvider.Info(tag, mass, friction, elasticity, priority)
      }

      public override fun toString(): String {
         return "Info(tag=${this.tag}, mass=${this.mass}, friction=${this.friction}, elasticity=${this.elasticity}, priority=${this.priority})"
      }

      public override fun hashCode(): Int {
         return (
                  (
                           (java.lang.Boolean.hashCode(this.tag) * 31 + (if (this.mass == null) 0 else this.mass.hashCode())) * 31
                              + (if (this.friction == null) 0 else this.friction.hashCode())
                        )
                        * 31
                     + (if (this.elasticity == null) 0 else this.elasticity.hashCode())
               )
               * 31
            + (if (this.priority == null) 0 else this.priority.hashCode())
         }

      public override operator fun equals(other: Any?): Boolean {
         label46@
         if (this === other) {
            return true
         } else {
            return other is VSBlockInfoDataProvider.Info
               && this.tag == (other as VSBlockInfoDataProvider.Info).tag
               && this.mass == (other as VSBlockInfoDataProvider.Info).mass
               && this.friction == (other as VSBlockInfoDataProvider.Info).friction
               && this.elasticity == (other as VSBlockInfoDataProvider.Info).elasticity
               && this.priority == (other as VSBlockInfoDataProvider.Info).priority
            }
      }
   }
}
