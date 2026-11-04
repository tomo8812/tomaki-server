package org.valkyrienskies.mod.common.config

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonArray
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import com.google.gson.JsonPrimitive
import java.nio.file.Files
import java.nio.file.Path
import java.util.HashMap
import java.util.LinkedHashMap
import java.util.LinkedHashSet
import java.util.Locale
import kotlin.jvm.internal.PropertyReference1
import kotlin.jvm.internal.PropertyReference1Impl
import kotlin.jvm.internal.Reflection
import kotlin.jvm.internal.SourceDebugExtension
import kotlin.reflect.KProperty
import org.apache.logging.log4j.Logger
import org.valkyrienskies.mod.util.ClassLogger
import org.valkyrienskies.mod.util.LoggingKt

@SourceDebugExtension(["SMAP\nRecipeOverrides.kt\nKotlin\n*S Kotlin\n*F\n+ 1 RecipeOverrides.kt\norg/valkyrienskies/mod/common/config/RecipeOverrides\n+ 2 fake.kt\nkotlin/jvm/internal/FakeKt\n+ 3 Maps.kt\nkotlin/collections/MapsKt__MapsKt\n+ 4 _Arrays.kt\nkotlin/collections/ArraysKt___ArraysKt\n+ 5 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n*L\n1#1,366:1\n1#2:367\n381#3,7:368\n13402#4,2:375\n1863#5,2:377\n*S KotlinDebug\n*F\n+ 1 RecipeOverrides.kt\norg/valkyrienskies/mod/common/config/RecipeOverrides\n*L\n149#1:368,7\n220#1:375,2\n241#1:377,2\n*E\n"])
public object RecipeOverrides {
   @JvmStatic
   @JvmSynthetic
   KProperty<Object>[] $$delegatedProperties = arrayOf(
      Reflection.property1(PropertyReference1Impl(RecipeOverrides.class, "logger", "getLogger()Lorg/apache/logging/log4j/Logger;", 0) as PropertyReference1)
   );

   private final val logger: Logger by LoggingKt.logger().provideDelegate_j8jxBIw/* $VF was: provideDelegate-j8jxBIw */(INSTANCE, $$delegatedProperties[0])
      private final get() {
         return ClassLogger.getValue_impl/* $VF was: getValue-impl */(logger$delegate, this, $$delegatedProperties[0])
      }


   private final val CONFIG_FILE: Path
   private final val gson: Gson = GsonBuilder().setPrettyPrinting().create()
   private final var overrideCache: Map<String, JsonObject> = LinkedHashMap() as java.util.Map
   private final var removalCache: Set<String> = LinkedHashSet() as java.util.Set
   private final val SHIP_HELM_WOODS: List<String> =
      CollectionsKt.listOf(arrayOf("oak", "spruce", "birch", "jungle", "acacia", "dark_oak", "crimson", "warped"))
      private final val BALLOON_COLORS: List<String> =
      CollectionsKt.listOf(
         arrayOf(
            "white",
            "light_gray",
            "gray",
            "black",
            "red",
            "orange",
            "yellow",
            "lime",
            "green",
            "light_blue",
            "cyan",
            "blue",
            "purple",
            "magenta",
            "pink",
            "brown"
         )
      )

   @JvmStatic
   public fun reload() {
      INSTANCE.load()
   }

   @JvmStatic
   public fun getOverrides(): Map<String, JsonObject> {
      return overrideCache
   }

   @JvmStatic
   public fun getRemovals(): Set<String> {
      return removalCache
   }

   @JvmStatic
   public fun logInfo(msg: String) {
      INSTANCE.logger.info(msg)
   }

   @JvmStatic
   public fun logError(msg: String, t: Throwable) {
      INSTANCE.logger.error(msg, t)
   }

   private fun load() {
      val overrides: LinkedHashMap = LinkedHashMap()
      val removals: LinkedHashSet = LinkedHashSet()

      try {
         if (!Files.exists(CONFIG_FILE)) {
            val var10000: Path = CONFIG_FILE.getParent()
            if (var10000 != null) {
               Files.createDirectories(var10000)
            }

            Files.writeString(CONFIG_FILE, gson.toJson(this.defaultConfig() as JsonElement))
            this.logger.info("Created default recipe config at ${CONFIG_FILE.toAbsolutePath()}")
         }

         for (var12 in JsonParser.parseString(Files.readString(CONFIG_FILE)).getAsJsonObject().entrySet()) {
            val var13: java.lang.String = var12.getKey() as java.lang.String
            val specEl: JsonElement = var12.getValue() as JsonElement
            if (!StringsKt.startsWith$default(var13, "_", false, 2, null)) {
               if (specEl.isJsonPrimitive() && StringsKt.equals(specEl.getAsString(), "remove", true)) {
                  removals.add(var13)
               } else if (specEl.isJsonObject()) {
                  val spec: JsonObject = specEl.getAsJsonObject()

                  try {
                     val var14: JsonElement = spec.get("remove")
                     if (var14 != null && var14.getAsBoolean()) {
                        removals.add(var13)
                     } else {
                        val var15: java.util.Map = overrides
                        var15.put(var13, this.toStandardRecipe(spec))
                     }
                  } catch (var10: Exception) {
                     this.logger.warn("Skipping malformed recipe override '$var13': ${var10.getMessage()}")
                  }
               }
            }
         }
      } catch (var11: Exception) {
         this.logger.warn("Failed to load recipe config at ${CONFIG_FILE.toAbsolutePath()} (${var11.getMessage()}); built-in recipes unchanged.")
      }

      overrideCache = overrides
      removalCache = removals
   }

   private fun toStandardRecipe(spec: JsonObject): JsonObject {
      var var32: java.lang.String
      run label106@{
         val var10000: JsonElement = spec.get("type")
         if (var10000 != null) {
            var32 = var10000.getAsString()
            if (var32 != null) {
               var32 = var32.toLowerCase(Locale.ROOT)
               if (var32 != null) {
                  return@label106
               }
            }
         }

         var32 = "shaped"
      }

      var var10001: JsonElement = spec.get("result")
      if (var10001 != null) {
         val var38: java.lang.String = var10001.getAsString()
         if (var38 != null) {
            val resultId: java.lang.String = this.normalizeId(var38)
            val var33: JsonElement = spec.get("count")
            val count: Int = if (var33 != null) var33.getAsInt() else 1
            val var34: JsonElement = spec.get("group")
            val group: java.lang.String = if (var34 != null) var34.getAsString() else null
            val var35: JsonArray = spec.getAsJsonArray("slots")
            if (var35 == null) {
               throw IllegalArgumentException("missing 'slots'")
            }

            val slots: JsonArray = var35
            if (var35.size() != 9) {
               throw IllegalArgumentException("'slots' must have exactly 9 entries (got ${var35.size()})")
            }

            var out: JsonObject = JsonObject()
            out.addProperty("id", resultId)
            out.addProperty("count", count)
            out = JsonObject()
            if (var32 == "shapeless") {
               out.addProperty("type", "minecraft:crafting_shapeless")
               val var26: JsonArray = JsonArray()

               repeat(8) { var28 ->
                  var10001 = this.slotToIngredient(slots.get(var28))
                  if (var10001 != null) {
                     var26.add(var10001)
                  }
               }

               if (var26.isEmpty()) {
                  throw IllegalArgumentException("shapeless recipe has no ingredients")
               }

               out.add("ingredients", var26 as JsonElement)
            } else {
               out.addProperty("type", "minecraft:crafting_shaped")
               val var27: JsonObject = JsonObject()
               val var29: HashMap = HashMap()
               var var30: Char = 'A'
               val rows: JsonArray = JsonArray()

               repeat(2) { r ->
                  val sb: StringBuilder = StringBuilder(3)

                  repeat(2) { c ->
                     val ing: JsonElement = this.slotToIngredient(slots.get(r * 3 + c))
                     if (ing == null) {
                        sb.append(' ')
                     } else {
                        var32 = ing.toString()
                        val `$this$getOrPut$iv`: java.util.Map = var29
                        val `value$iv`: Any = var29.get(var32)
                        if (`value$iv` == null) {
                           val assigned: java.lang.String = java.lang.String.valueOf(var30)
                           var30++
                           var27.add(assigned, ing)
                           `$this$getOrPut$iv`.put(var32, assigned)
                           var32 = assigned
                        } else {
                           var32 = (java.lang.String)`value$iv`
                        }

                        sb.append(var32)
                     }
                  }

                  rows.add(sb.toString())
               }

               out.add("pattern", rows as JsonElement)
               out.add("key", var27 as JsonElement)
            }

            out.add("result", out as JsonElement)
            if (group != null) {
               out.addProperty("group", group)
            }

            return out
         }
      }

      throw IllegalArgumentException("missing 'result'")
   }

   private fun slotToIngredient(el: JsonElement?): JsonElement? {
      if (el != null && !el.isJsonNull()) {
         if (el.isJsonArray()) {
            val arr: JsonArray = JsonArray()
            val var10000: java.util.Iterator = el.getAsJsonArray().iterator()
            val var3: java.util.Iterator = var10000

            while (var3.hasNext()) {
               val item: JsonElement = var3.next() as JsonElement
               if (item.isJsonPrimitive()) {
                  val var8: java.lang.String = item.getAsString()
                  val var7: JsonElement = this.stringToIngredient(var8)
                  if (var7 != null) {
                     arr.add(var7)
                  }
               }
            }

            return if (arr.isEmpty()) null else arr as JsonElement
         } else if (el.isJsonPrimitive()) {
            val var10001: java.lang.String = el.getAsString()
            return this.stringToIngredient(var10001)
         } else {
            return null
         }
      } else {
         return null
      }
   }

   private fun stringToIngredient(raw: String): JsonElement? {
      val s: java.lang.String = StringsKt.trim(raw).toString()
      if (s.length() == 0) {
         return null
      } else {
         val var3: JsonObject = JsonObject()
         if (StringsKt.startsWith$default(s, "#", false, 2, null)) {
            val var10002: RecipeOverrides = INSTANCE
            val var10003: java.lang.String = s.substring(1)
            var3.addProperty("tag", var10002.normalizeId(var10003))
         } else {
            var3.addProperty("item", INSTANCE.normalizeId(s))
         }

         return var3 as JsonElement
      }
   }

   private fun normalizeId(s: String): String {
      val t: java.lang.String = StringsKt.trim(s).toString()
      return if (StringsKt.contains$default(t, ':', false, 2, null)) t else "minecraft:$t"
   }

   private fun s(id: String): JsonElement {
      return JsonPrimitive(id) as JsonElement
   }

   private fun none(): JsonElement {
      return JsonPrimitive("") as JsonElement
   }

   private fun anyOf(vararg ids: String): JsonElement {
      val var2: JsonArray = JsonArray()
      val `$this$anyOf_u24lambda_u246`: JsonArray = var2

      for (`element$iv` in ids) {
         `$this$anyOf_u24lambda_u246`.add(JsonPrimitive((java.lang.String)`element$iv`) as JsonElement)
      }

      return var2 as JsonElement
   }

   private fun anyGlassPane(): JsonElement {
      return this.anyOf(
         "minecraft:glass_pane",
         "minecraft:white_stained_glass_pane",
         "minecraft:orange_stained_glass_pane",
         "minecraft:magenta_stained_glass_pane",
         "minecraft:light_blue_stained_glass_pane",
         "minecraft:yellow_stained_glass_pane",
         "minecraft:lime_stained_glass_pane",
         "minecraft:pink_stained_glass_pane",
         "minecraft:gray_stained_glass_pane",
         "minecraft:light_gray_stained_glass_pane",
         "minecraft:cyan_stained_glass_pane",
         "minecraft:purple_stained_glass_pane",
         "minecraft:blue_stained_glass_pane",
         "minecraft:brown_stained_glass_pane",
         "minecraft:green_stained_glass_pane",
         "minecraft:red_stained_glass_pane",
         "minecraft:black_stained_glass_pane"
      )
   }

   private fun recipeJson(type: String, slots: List<JsonElement>, result: String, count: Int, group: String?): JsonObject {
      val var6: JsonObject = JsonObject()
      var6.addProperty("type", type)
      val var9: JsonArray = JsonArray()
      val `$this$recipeJson_u24lambda_u249_u24lambda_u248`: JsonArray = var9

      for (`element$iv` in slots) {
         `$this$recipeJson_u24lambda_u249_u24lambda_u248`.add(`element$iv` as JsonElement)
      }

      var6.add("slots", var9 as JsonElement)
      var6.addProperty("result", result)
      var6.addProperty("count", count)
      if (group != null) {
         var6.addProperty("group", group)
      }

      return var6
   }

   private fun shaped(slots: List<JsonElement>, result: String, count: Int, group: String? = null): JsonObject {
      return this.recipeJson("shaped", slots, result, count, group)
   }

   private fun shapeless(ingredients: List<JsonElement>, result: String, count: Int, group: String? = null): JsonObject {
      val slots: java.util.List = CollectionsKt.toMutableList(ingredients)

      while (slots.size() < 9) {
         slots.add(this.none())
      }

      return this.recipeJson("shapeless", slots, result, count, group)
   }

   private fun defaultConfig(): JsonObject {
      val root: JsonObject = JsonObject()
      root.addProperty(
         "_README",
         "vs_eureka recipe overrides (defaults = the original Eureka recipes). 9 slots: 1=top-left .. 5=centre .. 9=bottom-right. Slot = \"namespace:item\", \"#namespace:tag\", [\"a\",\"b\"] for alternatives, or \"\" for empty. type=shaped|shapeless, plus result + count. Set a recipe to \"remove\" to disable it. Edit then /reload."
      )

      for (c in SHIP_HELM_WOODS) {
         root.add(
            "vs_eureka:$c_ship_helm",
            this.shaped(
               CollectionsKt.listOf(
                  arrayOf(
                     this.s("minecraft:iron_bars"),
                     this.s("minecraft:$c_fence"),
                     this.s("minecraft:iron_bars"),
                     this.s("minecraft:$c_fence"),
                     this.s("minecraft:heart_of_the_sea"),
                     this.s("minecraft:$c_fence"),
                     this.s("minecraft:$c_slab"),
                     this.s("minecraft:lodestone"),
                     this.s("minecraft:$c_slab")
                  )
               ),
               "vs_eureka:$c_ship_helm",
               1,
               "ship_helm"
            ) as JsonElement
         )
      }

      root.add(
         "vs_eureka:engine",
         shaped$default(
            this,
            CollectionsKt.listOf(
               arrayOf(
                  this.s("minecraft:smooth_stone"),
                  this.s("minecraft:smooth_stone"),
                  this.s("minecraft:smooth_stone"),
                  this.s("minecraft:blast_furnace"),
                  this.s("minecraft:lightning_rod"),
                  this.anyGlassPane(),
                  this.s("minecraft:iron_block"),
                  this.s("minecraft:iron_block"),
                  this.s("minecraft:iron_trapdoor")
               )
            ),
            "vs_eureka:engine",
            1,
            null,
            8,
            null
         ) as JsonElement
      )
      root.add(
         "vs_eureka:floater",
         shaped$default(
            this,
            CollectionsKt.listOf(
               arrayOf(
                  this.s("#minecraft:wooden_slabs"),
                  this.s("minecraft:barrel"),
                  this.s("#minecraft:wooden_slabs"),
                  this.s("minecraft:barrel"),
                  this.none(),
                  this.s("minecraft:barrel"),
                  this.s("#minecraft:wooden_slabs"),
                  this.s("minecraft:barrel"),
                  this.s("#minecraft:wooden_slabs")
               )
            ),
            "vs_eureka:floater",
            16,
            null,
            8,
            null
         ) as JsonElement
      )
      root.add(
         "vs_eureka:anchor",
         shaped$default(
            this,
            CollectionsKt.listOf(
               arrayOf(
                  this.s("minecraft:lead"),
                  this.s("minecraft:iron_ingot"),
                  this.s("minecraft:lead"),
                  this.none(),
                  this.s("minecraft:iron_ingot"),
                  this.none(),
                  this.s("minecraft:iron_ingot"),
                  this.s("minecraft:iron_block"),
                  this.s("minecraft:iron_ingot")
               )
            ),
            "vs_eureka:anchor",
            1,
            null,
            8,
            null
         ) as JsonElement
      )
      root.add(
         "vs_eureka:ballast",
         shaped$default(
            this,
            CollectionsKt.listOf(
               arrayOf(
                  this.s("minecraft:stone"),
                  this.s("minecraft:cobblestone"),
                  this.s("minecraft:stone"),
                  this.s("minecraft:cobblestone"),
                  this.none(),
                  this.s("minecraft:cobblestone"),
                  this.s("minecraft:stone"),
                  this.s("minecraft:cobblestone"),
                  this.s("minecraft:stone")
               )
            ),
            "vs_eureka:ballast",
            8,
            null,
            8,
            null
         ) as JsonElement
      )
      root.add(
         "vs_eureka:balloon",
         this.shaped(
            CollectionsKt.listOf(
               arrayOf(
                  this.s("minecraft:leather"),
                  this.s("minecraft:phantom_membrane"),
                  this.s("minecraft:leather"),
                  this.s("minecraft:phantom_membrane"),
                  this.s("minecraft:nether_star"),
                  this.s("minecraft:phantom_membrane"),
                  this.s("minecraft:leather"),
                  this.s("minecraft:phantom_membrane"),
                  this.s("minecraft:leather")
               )
            ),
            "vs_eureka:balloon",
            10,
            "balloons"
         ) as JsonElement
      )

      for (var11 in BALLOON_COLORS) {
         root.add(
            "vs_eureka:$var11_balloon",
            this.shapeless(
               CollectionsKt.listOf(arrayOf(this.s("#vs_eureka:balloons"), this.s("minecraft:$var11_dye"))), "vs_eureka:$var11_balloon", 1, "colored_balloons"
            ) as JsonElement
         )
      }

      return root
   }

   @JvmStatic
   fun {
      val var10000: Path = Path.of("config", "vs_eureka_recipes.json")
      CONFIG_FILE = var10000
   }
}
