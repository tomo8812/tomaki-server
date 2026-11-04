package org.valkyrienskies.mod.api.datagen

import com.google.gson.JsonElement
import com.google.gson.JsonObject
import java.nio.file.Path
import java.util.Arrays
import java.util.HashMap
import java.util.concurrent.CompletableFuture
import kotlin.jvm.internal.SourceDebugExtension
import net.minecraft.class_1299
import net.minecraft.class_2405
import net.minecraft.class_2960
import net.minecraft.class_7403
import net.minecraft.class_7784

@SourceDebugExtension(["SMAP\nVSShipyardEntityDataProvider.kt\nKotlin\n*S Kotlin\n*F\n+ 1 VSShipyardEntityDataProvider.kt\norg/valkyrienskies/mod/api/datagen/VSShipyardEntityDataProvider\n+ 2 ArraysJVM.kt\nkotlin/collections/ArraysKt__ArraysJVMKt\n*L\n1#1,71:1\n37#2:72\n36#2,3:73\n*S KotlinDebug\n*F\n+ 1 VSShipyardEntityDataProvider.kt\norg/valkyrienskies/mod/api/datagen/VSShipyardEntityDataProvider\n*L\n64#1:72\n64#1:73,3\n*E\n"])
public abstract class VSShipyardEntityDataProvider : class_2405 {
   public final val output: class_7784
   public final val modId: String
   private final val entries: HashMap<class_2960, CompletableFuture<*>>

   open fun VSShipyardEntityDataProvider(output: class_7784, modId: java.lang.String) {
      this.output = output
      this.modId = modId
      this.entries = HashMap<>()
   }

   protected abstract fun registerEntries() {
   }

   protected fun addEntity(id: class_2960) {
      if (this.entries.containsKey(id)) {
         throw RuntimeException("Duplicate Block Into Entries for $id")
      } else {
         val var10000: Path = this.output.method_45971().resolve("data").resolve(id.method_12836()).resolve("vs_entities").resolve("${id.method_12832()}.json")
         this.entries.put(id, CompletableFuture.supplyAsync({ 
            try {
               val json: JsonObject = JsonObject()
               json.addProperty("handler", "valkyrienskies:shipyard")
               class_2405.method_10320(class_7403.field_39439, json as JsonElement, `$path`)
            } catch (var3: Exception) {
               throw RuntimeException("Failed to save $`$path`", var3)
            }
         }).join())
      }
   }

   protected fun addEntity(type: class_1299<*>) {
      val var10001: class_2960 = type.method_40124().method_40237().method_29177()
      this.addEntity(var10001)
   }

   open fun method_10319(cachedOutput: class_7403): CompletableFuture<*> {
      this.registerEntries()
      val var10000: java.util.Collection = this.entries.values()
      val var2: Array<CompletableFuture> = var10000.toArray(arrayOfNulls(0))
      val var6: CompletableFuture = CompletableFuture.allOf(Arrays.copyOf(var2, var2.length))
      var6
   }

   open fun method_10321(): java.lang.String {
      "VS Shipyard Entity Data Provider: ${this.modId}"
   }
}
