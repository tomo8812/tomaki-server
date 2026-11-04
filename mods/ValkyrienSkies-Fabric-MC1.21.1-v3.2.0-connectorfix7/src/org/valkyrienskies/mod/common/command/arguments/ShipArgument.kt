package org.valkyrienskies.mod.common.command.arguments

import com.mojang.brigadier.StringReader
import com.mojang.brigadier.arguments.ArgumentType
import com.mojang.brigadier.context.CommandContext
import com.mojang.brigadier.exceptions.CommandSyntaxException
import com.mojang.brigadier.suggestion.Suggestions
import com.mojang.brigadier.suggestion.SuggestionsBuilder
import java.util.ArrayList
import java.util.concurrent.CompletableFuture
import kotlin.jvm.functions.Function1
import kotlin.jvm.internal.DefaultConstructorMarker
import kotlin.jvm.internal.SourceDebugExtension
import net.minecraft.class_2168
import net.minecraft.class_2172
import org.valkyrienskies.core.api.ships.Ship
import org.valkyrienskies.core.api.world.ShipWorld

public open class ShipArgument private constructor(selectorOnly: Boolean) : ArgumentType<ShipSelector> {
   public final val selectorOnly: Boolean
   private final val EXAMPLES: List<String>

   init {
      this.selectorOnly = selectorOnly
      this.EXAMPLES = CollectionsKt.listOf(arrayOf("the-mogus", "@v", "@v[slug=the-mogus]"))
   }

   public open fun <S : Any> listSuggestions(context: CommandContext<Any>, builder: SuggestionsBuilder): CompletableFuture<Suggestions> {
      val reader: StringReader = StringReader(builder.getInput())
      reader.setCursor(builder.getStart())
      val startsWithAt: Boolean = reader.canRead() && reader.peek() == '@'
      val nBuilder: Any = context.getSource()
      val parser: ShipArgumentParser = ShipArgumentParser(nBuilder as? class_2172, this.selectorOnly)

      try {
         parser.parse(reader, true)
      } catch (var8: CommandSyntaxException) {
      }

      if (!startsWithAt) {
         reader.setCursor(builder.getStart())
      }

      val var9: SuggestionsBuilder = builder.createOffset(reader.getCursor())
      val var10000: Function1 = parser.suggestionProvider
      var10000(var9)
      val var10: CompletableFuture = var9.buildFuture()
      return var10
   }

   public open fun parse(reader: StringReader): ShipSelector {
      return ShipArgumentParser(null, this.selectorOnly).parse(reader, false)
   }

   public open fun getExamples(): Collection<String> {
      return this.EXAMPLES
   }

   // $VF: bridge methodSupplier
   @JvmSynthetic
   open fun parse(p0: StringReader) {
      this.parse(p0)
   }

   @JvmSynthetic
   open fun ShipArgument(selectorOnly: Boolean, `$constructor_marker`: DefaultConstructorMarker) {
      this(selectorOnly)
   }

   @SourceDebugExtension(["SMAP\nShipArgument.kt\nKotlin\n*S Kotlin\n*F\n+ 1 ShipArgument.kt\norg/valkyrienskies/mod/common/command/arguments/ShipArgument$Companion\n+ 2 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n*L\n1#1,110:1\n1557#2:111\n1628#2,3:112\n774#2:115\n865#2,2:116\n*S KotlinDebug\n*F\n+ 1 ShipArgument.kt\norg/valkyrienskies/mod/common/command/arguments/ShipArgument$Companion\n*L\n74#1:111\n74#1:112,3\n79#1:115\n79#1:116,2\n*E\n"])
   public companion object {
      private final val ERROR_NO_SHIP_FOUND: CommandSyntaxException
      private final val ERROR_MANY_SHIP_FOUND: CommandSyntaxException

      public fun selectorOnly(): ShipArgument {
         return ShipArgument(true, null)
      }

      public fun ships(): ShipArgument {
         return ShipArgument(false, null)
      }

      public fun getShips(context: CommandContext<class_2168>, argName: String): Set<Ship> {
         val selector: ShipSelector = context.getArgument(argName, ShipSelector.class) as ShipSelector
         val var10000: Any = context.getSource()
         val shipWorld: ShipWorld = shipWorld
         val fromLoadedShips: java.util.Set = selector.select(shipWorld.loadedShips)
         val `$i$f$filter`: java.lang.Iterable = fromLoadedShips
         val `$this$filterTo$iv$iv`: java.util.Collection = ArrayList(CollectionsKt.collectionSizeOrDefault(fromLoadedShips, 10))

         for (`item$iv$iv` in `$i$f$filter`) {
            `$this$filterTo$iv$iv`.add((`item$iv$iv` as Ship).id)
         }

         val fromLoadedShipIds: java.util.Set = CollectionsKt.toSet(`$this$filterTo$iv$iv` as java.util.List)
         val var19: java.lang.Iterable = selector.select(shipWorld.allShips)
         val `destination$iv$ivx`: java.util.Collection = ArrayList()

         for (var25 in var19) {
            if (!fromLoadedShipIds.contains((var25 as Ship).id)) {
               `destination$iv$ivx`.add(var25)
            }
         }

         return SetsKt.plus(fromLoadedShips, `destination$iv$ivx` as java.util.List)
      }

      public fun getShip(context: CommandContext<class_2168>, argName: String): Ship {
         val selector: ShipSelector = context.getArgument(argName, ShipSelector.class) as ShipSelector
         var var10001: Any = context.getSource()
         val loadedShips: java.util.Set = selector.select(shipWorld.loadedShips)
         if (loadedShips.size() == 1) {
            return CollectionsKt.first(loadedShips) as Ship
         } else {
            var10001 = context.getSource()
            val r: java.util.Set = selector.select(shipWorld.allShips)
            if (r.isEmpty()) {
               throw ShipArgument.ERROR_NO_SHIP_FOUND
            } else if (r.size() == 1) {
               return CollectionsKt.first(r) as Ship
            } else {
               throw ShipArgument.ERROR_MANY_SHIP_FOUND
            }
         }
      }
   }
}
