package org.valkyrienskies.mod.common.command.arguments

import com.mojang.brigadier.ImmutableStringReader
import com.mojang.brigadier.StringReader
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType
import com.mojang.brigadier.suggestion.SuggestionsBuilder
import java.util.ArrayList
import kotlin.jvm.internal.SourceDebugExtension
import net.minecraft.class_2172
import org.valkyrienskies.core.api.ships.QueryableShipData
import org.valkyrienskies.core.api.ships.Ship
import org.valkyrienskies.core.api.world.ShipWorld

@SourceDebugExtension(["SMAP\nShipArgumentParser.kt\nKotlin\n*S Kotlin\n*F\n+ 1 ShipArgumentParser.kt\norg/valkyrienskies/mod/common/command/arguments/ShipArgumentParser\n+ 2 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n+ 3 fake.kt\nkotlin/jvm/internal/FakeKt\n*L\n1#1,205:1\n1755#2,3:206\n1611#2,9:209\n1863#2:218\n1864#2:220\n1620#2:221\n774#2:222\n865#2,2:223\n1863#2,2:225\n1611#2,9:227\n1863#2:236\n1864#2:238\n1620#2:239\n774#2:240\n865#2,2:241\n1863#2,2:243\n1557#2:245\n1628#2,3:246\n774#2:249\n865#2,2:250\n1863#2,2:252\n1#3:219\n1#3:237\n*S KotlinDebug\n*F\n+ 1 ShipArgumentParser.kt\norg/valkyrienskies/mod/common/command/arguments/ShipArgumentParser\n*L\n158#1:206,3\n128#1:209,9\n128#1:218\n128#1:220\n128#1:221\n129#1:222\n129#1:223,2\n130#1:225,2\n138#1:227,9\n138#1:236\n138#1:238\n138#1:239\n139#1:240\n139#1:241,2\n140#1:243,2\n146#1:245\n146#1:246,3\n147#1:249\n147#1:250,2\n148#1:252,2\n128#1:219\n138#1:237\n*E\n"])
public open class ShipArgumentParser(source: class_2172?, selectorOnly: Boolean) {
   private final val source: class_2172?
   private final var selectorOnly: Boolean
   public final var suggestionProvider: (SuggestionsBuilder) -> Unit
   public final var slug: String?
   public final var limit: Int?
   public final var id: Long?

   init {
      this.source = source
      this.selectorOnly = selectorOnly
      this.suggestionProvider = { it: SuggestionsBuilder ->
         Unit.INSTANCE
      }
   }

   public fun parse(reader: StringReader, isForSuggestion: Boolean): ShipSelector {
      val start: Int = reader.getCursor()
      this.suggestSelectorOrSlug()
      if (!reader.canRead()) {
         throw ERROR_MISSING_SELECTOR_TYPE.createWithContext(reader as ImmutableStringReader)
      } else {
         if (reader.read() == '@') {
            if (!reader.canRead()) {
               this.suggest({ builder: SuggestionsBuilder, var1: class_2172 ->
                  builder.suggest("v")
                  Unit.INSTANCE
               })
            } else {
               reader.read()
               if (!reader.canRead()) {
                  this.suggestOpenOptions()
               } else if (reader.read() == '[') {
                  if (!reader.canRead()) {
                     suggestOptions$default(this, null, 1, null)
                  } else {
                     while (reader.canRead() && reader.peek() != ']') {
                        val i: Int = reader.getCursor()
                        val s: java.lang.String = reader.readString()
                        this.suggestOptions(s)
                        reader.skipWhitespace()
                        if (!this.isOption(s)) {
                           if (isForSuggestion) {
                              reader.setCursor(i)
                              throw ERROR_UNKNOWN_OPTION.createWithContext(reader as ImmutableStringReader, s)
                           }

                           return ShipSelector(null, null, 0)
                        }

                        reader.skipWhitespace()
                        this.suggestEquals()
                        if (!reader.canRead() || reader.peek() != '=') {
                           throw ERROR_EXPECTED_OPTION_VALUE.createWithContext(reader as ImmutableStringReader, s)
                        }

                        reader.skip()
                        reader.skipWhitespace()
                        this.suggestionsOfOption(s)
                        reader.skipWhitespace()
                        if (!reader.canRead()) {
                           throw ERROR_EXPECTED_OPTION_VALUE.createWithContext(reader as ImmutableStringReader, s)
                        }

                        this.parseOption(s, reader)
                        reader.skipWhitespace()
                        this.suggestOptionsNextOrClose()
                        reader.skipWhitespace()
                     }
                  }

                  if (!reader.canRead() || reader.read() != ']') {
                     if (isForSuggestion) {
                        throw ERROR_EXPECTED_END_OF_OPTIONS.createWithContext(reader as ImmutableStringReader)
                     } else {
                        return ShipSelector(null, null, 0)
                     }
                  }

                  this.suggest({ var0: SuggestionsBuilder, var1: class_2172 ->
                     Unit.INSTANCE
                  })
               }
            }
         } else if (!this.selectorOnly) {
            this.suggestionsOfOption("slug")
            reader.setCursor(start)
            this.slug = reader.readUnquotedString()
         }

         return ShipSelector(this.slug, this.id, if (this.limit != null) this.limit else Integer.MAX_VALUE)
      }
   }

   private fun isOption(s: String): Boolean {
      when (s.hashCode()) {
         3355 -> {
            if (s.equals("id")) {
               return true
            }
         }
         3533483 -> {
            if (s.equals("slug")) {
               return true
            }
         }
         102976443 -> {
            if (s.equals("limit")) {
               return true
            }
         }
         else -> {}
      }

      return false
   }

   private fun suggestOptions(textSoFar: String? = null) {
      this.suggest({ builder: SuggestionsBuilder, source: class_2172 ->
         if (`$textSoFar` == null) {
            builder.suggest("slug=")
            builder.suggest("limit=")
            builder.suggest("id=")
         } else {
            if (StringsKt.startsWith$default("slug=", `$textSoFar`, false, 2, null)) {
               builder.suggest("slug=")
            }

            if (StringsKt.startsWith$default("limit=", `$textSoFar`, false, 2, null)) {
               builder.suggest("limit=")
            }

            if (StringsKt.startsWith$default("id=", `$textSoFar`, false, 2, null)) {
               builder.suggest("id=")
            }
         }

         Unit.INSTANCE
      })
   }

   private fun suggestSelectorOrSlug() {
      this.suggest({ builder: SuggestionsBuilder, source: class_2172 ->
         builder.suggest("@v")
         if (!`this$0`.selectorOnly) {
            var `$this$filterTo$iv$iv`: java.lang.Iterable = shipWorld.allShips
            var `element$iv`: java.util.Collection = ArrayList()

            for (var11 in `$this$filterTo$iv$iv`) {
               val var10000: Any = (var11 as Ship).slug
               if (var10000 != null) {
                  `element$iv`.add(var10000)
               }
            }

            `$this$filterTo$iv$iv` = `element$iv` as java.util.List
            `element$iv` = ArrayList()

            for (var30 in `$this$filterTo$iv$iv`) {
               val var31: java.lang.String = var30 as java.lang.String
               val var10001: java.lang.String = builder.getRemaining()
               if (StringsKt.startsWith$default(var31, var10001, false, 2, null)) {
                  `element$iv`.add(var30)
               }
            }

            for (var25 in `element$iv` as java.util.List) {
               builder.suggest(var25 as java.lang.String)
            }
         }

         Unit.INSTANCE
      })
   }

   public fun suggestionsOfOption(option: String) {
      when (option.hashCode()) {
         3355 -> {
            if (!option.equals("id")) {
               throw ERROR_UNKNOWN_OPTION.create(option)
            }

            this.suggest({ builder: SuggestionsBuilder, source: class_2172 ->
               val `$this$forEach$iv`: java.lang.Iterable = shipWorld.allShips
               var `element$iv`: java.util.Collection = ArrayList(CollectionsKt.collectionSizeOrDefault(`$this$forEach$iv`, 10))

               for (`element$iv$iv` in `$this$forEach$iv`) {
                  `element$iv`.add(java.lang.String.valueOf((`element$iv$iv` as Ship).id))
               }

               val var16: java.lang.Iterable = `element$iv` as java.util.List
               `element$iv` = ArrayList()

               for (var24 in var16) {
                  val var25: java.lang.String = var24 as java.lang.String
                  val var10001: java.lang.String = builder.getRemaining()
                  if (StringsKt.startsWith$default(var25, var10001, false, 2, null)) {
                     `element$iv`.add(var24)
                  }
               }

               for (var19 in `element$iv` as java.util.List) {
                  builder.suggest(var19 as java.lang.String)
               }

               Unit.INSTANCE
            })
            break
         }
         3533483 -> {
            if (!option.equals("slug")) {
               throw ERROR_UNKNOWN_OPTION.create(option)
            }

            this.suggest({ builder: SuggestionsBuilder, source: class_2172 ->
               var `$this$filterTo$iv$iv`: java.lang.Iterable = shipWorld.allShips
               var `element$iv`: java.util.Collection = ArrayList()

               for (var10 in `$this$filterTo$iv$iv`) {
                  val var10000: Any = (var10 as Ship).slug
                  if (var10000 != null) {
                     `element$iv`.add(var10000)
                  }
               }

               `$this$filterTo$iv$iv` = `element$iv` as java.util.List
               `element$iv` = ArrayList()

               for (var29 in `$this$filterTo$iv$iv`) {
                  val var30: java.lang.String = var29 as java.lang.String
                  val var10001: java.lang.String = builder.getRemaining()
                  if (StringsKt.startsWith$default(var30, var10001, false, 2, null)) {
                     `element$iv`.add(var29)
                  }
               }

               for (var24 in `element$iv` as java.util.List) {
                  builder.suggest(var24 as java.lang.String)
               }

               Unit.INSTANCE
            })
            break
         }
         102976443 -> {
            if (!option.equals("limit")) {
               throw ERROR_UNKNOWN_OPTION.create(option)
            }
         }
         else -> throw ERROR_UNKNOWN_OPTION.create(option)
      }
   }

   public fun parseOption(option: String, reader: StringReader) {
      when (option.hashCode()) {
         3355 -> {
            if (!option.equals("id")) {
               throw ERROR_UNKNOWN_OPTION.create(option)
            }

            this.id = reader.readLong()
            break
         }
         3533483 -> {
            if (!option.equals("slug")) {
               throw ERROR_UNKNOWN_OPTION.create(option)
            }

            var start: Int
            var slug: java.lang.String
            var var13: Boolean
            run label85@{
               start = reader.getCursor()
               slug = reader.readUnquotedString()
               if (this.source != null) {
                  val var10000: ShipWorld = shipWorld
                  if (var10000 != null) {
                     val var12: QueryableShipData = var10000.allShips
                     if (var12 != null) {
                        val `$this$any$iv`: java.lang.Iterable = var12
                        if (var12 is java.util.Collection && (var12 as java.util.Collection).isEmpty()) {
                           var13 = false
                        } else {
                           val var8: java.util.Iterator = `$this$any$iv`.iterator()

                           while (true) {
                              if (!var8.hasNext()) {
                                 var13 = false
                                 break
                              }

                              if ((var8.next() as Ship).slug == slug) {
                                 var13 = true
                                 break
                              }
                           }
                        }

                        var13 = !var13
                        return@label85
                     }
                  }
               }

               var13 = false
            }

            if (var13) {
               reader.setCursor(start)
               throw ERROR_INVALID_SLUG_OR_ID.create()
            }

            this.slug = slug
            break
         }
         102976443 -> {
            if (option.equals("limit")) {
               this.limit = reader.readInt()
               break
            }

            throw ERROR_UNKNOWN_OPTION.create(option)
         }
         else -> throw ERROR_UNKNOWN_OPTION.create(option)
      }
   }

   private fun suggestEquals() {
      this.suggest({ builder: SuggestionsBuilder, var1: class_2172 ->
         builder.suggest("=")
         Unit.INSTANCE
      })
   }

   private fun suggestOpenOptions() {
      this.suggest({ builder: SuggestionsBuilder, var1: class_2172 ->
         builder.suggest("[")
         Unit.INSTANCE
      })
   }

   private fun suggestOptionsNextOrClose() {
      this.suggest({ builder: SuggestionsBuilder, var1: class_2172 ->
         builder.suggest(",")
         builder.suggest("]")
         Unit.INSTANCE
      })
   }

   private fun suggest(builder: (SuggestionsBuilder, class_2172) -> Unit) {
      if (this.source != null) {
         this.suggestionProvider = { it: SuggestionsBuilder ->
            `$builder`(it, `this$0`.source)
            Unit.INSTANCE
         }
      }
   }

   private fun StringReader.expectR(s: Char) {
      this.suggest({ builder: SuggestionsBuilder, source: class_2172 ->
         builder.suggest(java.lang.String.valueOf(`$s`))
         Unit.INSTANCE
      })
      `$this$expectR`.expect(s)
   }

   public companion object {
      public final val ERROR_MISSING_SELECTOR_TYPE: SimpleCommandExceptionType
      public final val ERROR_INVALID_SLUG_OR_ID: SimpleCommandExceptionType
      public final val ERROR_EXPECTED_END_OF_OPTIONS: SimpleCommandExceptionType
      public final val ERROR_EXPECTED_OPTION_VALUE: DynamicCommandExceptionType
      public final val ERROR_UNKNOWN_OPTION: DynamicCommandExceptionType
   }
}
