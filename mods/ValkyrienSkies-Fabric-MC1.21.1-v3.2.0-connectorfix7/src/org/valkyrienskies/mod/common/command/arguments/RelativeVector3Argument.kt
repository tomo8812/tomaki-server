package org.valkyrienskies.mod.common.command.arguments

import com.mojang.brigadier.ImmutableStringReader
import com.mojang.brigadier.Message
import com.mojang.brigadier.StringReader
import com.mojang.brigadier.arguments.ArgumentType
import com.mojang.brigadier.context.CommandContext
import com.mojang.brigadier.exceptions.CommandSyntaxException
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType
import com.mojang.brigadier.suggestion.Suggestions
import com.mojang.brigadier.suggestion.SuggestionsBuilder
import java.util.concurrent.CompletableFuture
import kotlin.jvm.functions.Function1
import kotlin.jvm.internal.SourceDebugExtension
import net.minecraft.class_2168
import net.minecraft.class_2270
import net.minecraft.class_2278
import net.minecraft.class_2561

public open class RelativeVector3Argument : ArgumentType<RelativeVector3> {
   public open fun parse(reader: StringReader): RelativeVector3 {
      return RelativeVector3Argument.Companion.RelativeVector3ArgumentParser().parse(reader, false)
   }

   public open fun <S> listSuggestions(context: CommandContext<Any>?, builder: SuggestionsBuilder): CompletableFuture<Suggestions?>? {
      val reader: StringReader = StringReader(builder.getInput())
      reader.setCursor(builder.getStart())
      val parser: RelativeVector3Argument.Companion.RelativeVector3ArgumentParser = RelativeVector3Argument.Companion.RelativeVector3ArgumentParser()

      try {
         parser.parse(reader, true)
      } catch (var7: CommandSyntaxException) {
      }

      reader.setCursor(builder.getStart())
      val nBuilder: SuggestionsBuilder = builder.createOffset(reader.getCursor())
      val var10000: Function1 = parser.suggestionProvider
      var10000(nBuilder)
      return nBuilder.buildFuture()
   }

   public open fun getExamples(): Collection<String> {
      return EXAMPLES
   }

   // $VF: bridge methodSupplier
   @JvmSynthetic
   open fun parse(p0: StringReader) {
      this.parse(p0)
   }

   public companion object {
      private final val EXAMPLES: Collection<String>
      public const val VECTOR_ARG_FAIL_MESSAGE: String = "command.valkyrienskies.vector_arg.fail"
      private final val DUMMY_EULER_ANGLES: RelativeVector3

      public fun relativeVector3(): RelativeVector3Argument {
         return RelativeVector3Argument()
      }

      public fun getRelativeVector3(commandContext: CommandContext<class_2168?>, string: String?): RelativeVector3 {
         val var10000: Any = commandContext.getArgument(string, RelativeVector3.class)
         return var10000 as RelativeVector3
      }

      @SourceDebugExtension(["SMAP\nRelativeVector3Argument.kt\nKotlin\n*S Kotlin\n*F\n+ 1 RelativeVector3Argument.kt\norg/valkyrienskies/mod/common/command/arguments/RelativeVector3Argument$Companion$RelativeVector3ArgumentParser\n+ 2 fake.kt\nkotlin/jvm/internal/FakeKt\n*L\n1#1,228:1\n1#2:229\n*E\n"])
      private class RelativeVector3ArgumentParser {
         public final var suggestionProvider: (SuggestionsBuilder) -> Unit = { it: SuggestionsBuilder ->
            Unit.INSTANCE
         }

         public fun parse(reader: StringReader, invokedByListSuggestions: Boolean): RelativeVector3 {
            val i: Int = reader.getCursor()
            this.suggest({ builder: SuggestionsBuilder ->
               builder.suggest("(0 0 0)")
               Unit.INSTANCE
            })
            if (!reader.canRead()) {
               throw class_2270.field_10736.createWithContext(reader as ImmutableStringReader)
            } else {
               if (!reader.canRead()) {
                  if (!invokedByListSuggestions) {
                     throw SimpleCommandExceptionType(class_2561.method_43471("command.valkyrienskies.vector_arg.fail") as Message)
                        .createWithContext(reader as ImmutableStringReader)
                     }

                  this.suggest({ builder: SuggestionsBuilder ->
                     builder.suggest("(0 0 0)")
                     Unit.INSTANCE
                  })
               }

               if (reader.canRead() && reader.peek() == '(') {
                  reader.skip()
                  if (!reader.canRead()) {
                     if (!invokedByListSuggestions) {
                        throw SimpleCommandExceptionType(class_2561.method_43471("command.valkyrienskies.vector_arg.fail") as Message)
                           .createWithContext(reader as ImmutableStringReader)
                        }

                     this.suggest({ builder: SuggestionsBuilder ->
                        builder.suggest("${builder.getRemaining()}0 0 0)")
                        Unit.INSTANCE
                     })
                  }

                  val worldCoordinate: class_2278 = class_2278.method_9743(reader, false)
                  if (!reader.canRead()) {
                     if (!invokedByListSuggestions) {
                        throw SimpleCommandExceptionType(class_2561.method_43471("command.valkyrienskies.vector_arg.fail") as Message)
                           .createWithContext(reader as ImmutableStringReader)
                        }

                     this.suggest({ builder: SuggestionsBuilder ->
                        builder.suggest("${builder.getRemaining()} 0 0)")
                        Unit.INSTANCE
                     })
                  }

                  if (reader.canRead() && reader.peek() == ' ') {
                     reader.skip()
                     if (!reader.canRead()) {
                        if (!invokedByListSuggestions) {
                           throw SimpleCommandExceptionType(class_2561.method_43471("command.valkyrienskies.vector_arg.fail") as Message)
                              .createWithContext(reader as ImmutableStringReader)
                           }

                        this.suggest({ builder: SuggestionsBuilder ->
                           builder.suggest("${builder.getRemaining()}0 0)")
                           Unit.INSTANCE
                        })
                     }

                     val worldCoordinate2: class_2278 = class_2278.method_9743(reader, false)
                     if (!reader.canRead()) {
                        if (!invokedByListSuggestions) {
                           throw SimpleCommandExceptionType(class_2561.method_43471("command.valkyrienskies.vector_arg.fail") as Message)
                              .createWithContext(reader as ImmutableStringReader)
                           }

                        this.suggest({ builder: SuggestionsBuilder ->
                           builder.suggest("${builder.getRemaining()} 0)")
                           Unit.INSTANCE
                        })
                     }

                     if (reader.canRead() && reader.peek() == ' ') {
                        reader.skip()
                        if (!reader.canRead()) {
                           if (!invokedByListSuggestions) {
                              throw SimpleCommandExceptionType(class_2561.method_43471("command.valkyrienskies.vector_arg.fail") as Message)
                                 .createWithContext(reader as ImmutableStringReader)
                              }

                           this.suggest({ builder: SuggestionsBuilder ->
                              builder.suggest("${builder.getRemaining()}0)")
                              Unit.INSTANCE
                           })
                        }

                        val var10000: class_2278
                        if (reader.getRemainingLength() > 1 && reader.peek(1) == ')') {
                           var var10002: java.lang.String = reader.getString()
                           var10002 = var10002.substring(0, reader.getCursor() + 1)
                           val var13: StringReader = StringReader(var10002)
                           var13.setCursor(reader.getCursor())
                           val itx: class_2278 = class_2278.method_9743(var13, false)
                           reader.read()
                           var10000 = itx
                        } else {
                           var10000 = class_2278.method_9743(reader, false)
                        }

                        if (reader.canRead()) {
                           if (reader.peek() == ')') {
                              reader.skip()
                              val var14: RelativeValue = RelativeValue(worldCoordinate.method_9740(0.0), worldCoordinate.method_9741())
                              val var17: RelativeValue = RelativeValue(worldCoordinate2.method_9740(0.0), worldCoordinate2.method_9741())
                              val var20: RelativeValue = RelativeValue(var10000.method_9740(0.0), var10000.method_9741())
                              this.suggest({ builder: SuggestionsBuilder ->
                                 builder.suggest(builder.getRemaining())
                                 Unit.INSTANCE
                              })
                              return RelativeVector3(var14, var17, var20)
                           }
                        } else {
                           if (!invokedByListSuggestions) {
                              throw SimpleCommandExceptionType(class_2561.method_43471("command.valkyrienskies.vector_arg.fail") as Message)
                                 .createWithContext(reader as ImmutableStringReader)
                              }

                           this.suggest({ builder: SuggestionsBuilder ->
                              builder.suggest("${builder.getRemaining()})")
                              Unit.INSTANCE
                           })
                        }
                     }
                  }

                  if (!reader.canRead()) {
                     reader.setCursor(i)
                     throw class_2270.field_10736.createWithContext(reader as ImmutableStringReader)
                  }
               }

               reader.setCursor(i)
               throw class_2270.field_10736.createWithContext(reader as ImmutableStringReader)
            }
         }

         private fun suggest(builder: (SuggestionsBuilder) -> Unit) {
            this.suggestionProvider = { it: SuggestionsBuilder ->
               `$builder`(it)
               Unit.INSTANCE
            }
         }
      }
   }
}
