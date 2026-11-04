package com.fasterxml.jackson.module.kotlin

import com.fasterxml.jackson.databind.MapperFeature
import com.fasterxml.jackson.databind.Module
import com.fasterxml.jackson.databind.Module.SetupContext
import com.fasterxml.jackson.databind.module.SimpleModule
import java.util.BitSet
import kotlin.jvm.internal.DefaultConstructorMarker
import kotlin.reflect.KClass

public class KotlinModule @Deprecated(message = "Use KotlinModule.Builder instead of named constructor parameters.", replaceWith = @ReplaceWith(expression = "KotlinModule.Builder()\n            .withReflectionCacheSize(reflectionCacheSize)\n            .configure(KotlinFeature.NullToEmptyCollection, nullToEmptyCollection)\n            .configure(KotlinFeature.NullToEmptyMap, nullToEmptyMap)\n            .configure(KotlinFeature.NullIsSameAsDefault, nullIsSameAsDefault)\n            .configure(KotlinFeature.SingletonSupport, singletonSupport)\n            .configure(KotlinFeature.StrictNullChecks, strictNullChecks)\n            .build()", imports = ["com.fasterxml.jackson.module.kotlin.KotlinFeature"]), level = DeprecationLevel.WARNING)  public constructor(reflectionCacheSize: Int = 512,
   nullToEmptyCollection: Boolean = false,
   nullToEmptyMap: Boolean = false,
   nullIsSameAsDefault: Boolean = false,
   singletonSupport: SingletonSupport = SingletonSupport.DISABLED,
   strictNullChecks: Boolean = false
) : SimpleModule(KotlinModule.class.getName(), PackageVersion.VERSION) {
   public final val reflectionCacheSize: Int
   public final val nullToEmptyCollection: Boolean
   public final val nullToEmptyMap: Boolean
   public final val nullIsSameAsDefault: Boolean
   public final val singletonSupport: SingletonSupport
   public final val strictNullChecks: Boolean
   private final val ignoredClassesForImplyingJsonCreator: Set<KClass<*>>

   init {
      this.reflectionCacheSize = reflectionCacheSize
      this.nullToEmptyCollection = nullToEmptyCollection
      this.nullToEmptyMap = nullToEmptyMap
      this.nullIsSameAsDefault = nullIsSameAsDefault
      this.singletonSupport = singletonSupport
      this.strictNullChecks = strictNullChecks
      this.ignoredClassesForImplyingJsonCreator = SetsKt.emptySet()
   }

   @Deprecated(message = "For ABI compatibility", level = DeprecationLevel.HIDDEN)
   @JvmSynthetic
   public constructor(reflectionCacheSize: Int, nullToEmptyCollection: Boolean, nullToEmptyMap: Boolean) : this(
         KotlinModule.Builder()
            .withReflectionCacheSize(reflectionCacheSize)
            .configure(KotlinFeature.NullToEmptyCollection, nullToEmptyCollection)
            .configure(KotlinFeature.NullToEmptyMap, nullToEmptyMap)
            .disable(KotlinFeature.NullIsSameAsDefault)
      )
   @Deprecated(message = "For ABI compatibility", level = DeprecationLevel.HIDDEN)
   @JvmSynthetic
   public constructor(reflectionCacheSize: Int, nullToEmptyCollection: Boolean, nullToEmptyMap: Boolean, nullIsSameAsDefault: Boolean) : this(
         KotlinModule.Builder()
            .withReflectionCacheSize(reflectionCacheSize)
            .configure(KotlinFeature.NullToEmptyCollection, nullToEmptyCollection)
            .configure(KotlinFeature.NullToEmptyMap, nullToEmptyMap)
            .configure(KotlinFeature.NullIsSameAsDefault, nullIsSameAsDefault)
      )
   private constructor(builder: com.fasterxml.jackson.module.kotlin.KotlinModule.Builder) : this(
         builder.reflectionCacheSize,
         builder.isEnabled(KotlinFeature.NullToEmptyCollection),
         builder.isEnabled(KotlinFeature.NullToEmptyMap),
         builder.isEnabled(KotlinFeature.NullIsSameAsDefault),
         if (builder.isEnabled(KotlinFeature.SingletonSupport)) SingletonSupport.CANONICALIZE else SingletonSupport.DISABLED,
         builder.isEnabled(KotlinFeature.StrictNullChecks)
      )
   public override fun setupModule(context: SetupContext) {
      super.setupModule(context)
      if (!context.isEnabled(MapperFeature.USE_ANNOTATIONS)) {
         throw IllegalStateException("The Jackson Kotlin module requires USE_ANNOTATIONS to be true or it cannot function")
      } else {
         val cache: ReflectionCache = ReflectionCache(this.reflectionCacheSize)
context.addValueInstantiators(KotlinInstantiators(cache, this.nullToEmptyCollection, this.nullToEmptyMap, this.nullIsSameAsDefault, this.strictNullChecks))
         when (KotlinModule.WhenMappings.$EnumSwitchMapping$0[this.singletonSupport.ordinal()]) {
            2 -> context.addBeanDeserializerModifier(KotlinBeanDeserializerModifier.INSTANCE)
            1 -> {
               context.insertAnnotationIntrospector(
                  KotlinAnnotationIntrospector(context, cache, this.nullToEmptyCollection, this.nullToEmptyMap, this.nullIsSameAsDefault)
               )
               context.appendAnnotationIntrospector(KotlinNamesAnnotationIntrospector(this, cache, this.ignoredClassesForImplyingJsonCreator))
               context.addDeserializers(KotlinDeserializers())
               context.addKeyDeserializers(KotlinKeyDeserializers.INSTANCE)
               context.addSerializers(KotlinSerializers())
               context.addKeySerializers(KotlinKeySerializers())
               setupModule$addMixIn(context, IntRange::class.java, ClosedRangeMixin::class.java)
               setupModule$addMixIn(context, CharRange::class.java, ClosedRangeMixin::class.java)
               setupModule$addMixIn(context, LongRange::class.java, ClosedRangeMixin::class.java)
               setupModule$addMixIn(context, ClosedRange::class.java, ClosedRangeMixin::class.java)
            }
            else -> {
               context.insertAnnotationIntrospector(
                  KotlinAnnotationIntrospector(context, cache, this.nullToEmptyCollection, this.nullToEmptyMap, this.nullIsSameAsDefault)
               )
               context.appendAnnotationIntrospector(KotlinNamesAnnotationIntrospector(this, cache, this.ignoredClassesForImplyingJsonCreator))
               context.addDeserializers(KotlinDeserializers())
               context.addKeyDeserializers(KotlinKeyDeserializers.INSTANCE)
               context.addSerializers(KotlinSerializers())
               context.addKeySerializers(KotlinKeySerializers())
               setupModule$addMixIn(context, IntRange::class.java, ClosedRangeMixin::class.java)
               setupModule$addMixIn(context, CharRange::class.java, ClosedRangeMixin::class.java)
               setupModule$addMixIn(context, LongRange::class.java, ClosedRangeMixin::class.java)
               setupModule$addMixIn(context, ClosedRange::class.java, ClosedRangeMixin::class.java)
            }
         }
      }
   }

   @JvmStatic
   fun `setupModule$addMixIn`(`$context`: Module.SetupContext, clazz: Class<*>, mixin: Class<*>) {
      `$context`.setMixInAnnotations(clazz, mixin)
   }

   /** @deprecated */
   @Deprecated(message = "Use KotlinModule.Builder instead of named constructor parameters.", replaceWith = @ReplaceWith(expression = "KotlinModule.Builder()\n            .withReflectionCacheSize(reflectionCacheSize)\n            .configure(KotlinFeature.NullToEmptyCollection, nullToEmptyCollection)\n            .configure(KotlinFeature.NullToEmptyMap, nullToEmptyMap)\n            .configure(KotlinFeature.NullIsSameAsDefault, nullIsSameAsDefault)\n            .configure(KotlinFeature.SingletonSupport, singletonSupport)\n            .configure(KotlinFeature.StrictNullChecks, strictNullChecks)\n            .build()", imports = ["com.fasterxml.jackson.module.kotlin.KotlinFeature"]), level = DeprecationLevel.WARNING)
   fun KotlinModule() {
      this(0, false, false, false, null, false, 63, null)
   }

   @JvmSynthetic
   fun KotlinModule(builder: KotlinModule.Builder, `$constructor_marker`: DefaultConstructorMarker) {
      this(builder)
   }

   public class Builder {
      public final var reflectionCacheSize: Int = 512
         private set

      private final val bitSet: BitSet = KotlinFeature.Companion.defaults

      public fun withReflectionCacheSize(reflectionCacheSize: Int): com.fasterxml.jackson.module.kotlin.KotlinModule.Builder {
         this.reflectionCacheSize = reflectionCacheSize
         return this
      }

      public fun enable(feature: KotlinFeature): com.fasterxml.jackson.module.kotlin.KotlinModule.Builder {
         this.bitSet.or(feature.bitSet)
         return this
      }

      public fun disable(feature: KotlinFeature): com.fasterxml.jackson.module.kotlin.KotlinModule.Builder {
         this.bitSet.andNot(feature.bitSet)
         return this
      }

      public fun configure(feature: KotlinFeature, enabled: Boolean): com.fasterxml.jackson.module.kotlin.KotlinModule.Builder {
         return if (enabled) this.enable(feature) else this.disable(feature)
      }

      public fun isEnabled(feature: KotlinFeature): Boolean {
         return this.bitSet.intersects(feature.bitSet)
      }

      @Deprecated(message = "Deprecated, use withReflectionCacheSize(reflectionCacheSize) instead.", replaceWith = @ReplaceWith(expression = "withReflectionCacheSize(reflectionCacheSize)", imports = []))
      public fun reflectionCacheSize(reflectionCacheSize: Int): com.fasterxml.jackson.module.kotlin.KotlinModule.Builder {
         return this.withReflectionCacheSize(reflectionCacheSize)
      }

      @Deprecated(message = "Deprecated, use isEnabled(NullToEmptyCollection) instead.", replaceWith = @ReplaceWith(expression = "isEnabled(KotlinFeature.NullToEmptyCollection)", imports = ["com.fasterxml.jackson.module.kotlin.KotlinFeature"]))
      public fun getNullToEmptyCollection(): Boolean {
         return this.isEnabled(KotlinFeature.NullToEmptyCollection)
      }

      @Deprecated(message = "Deprecated, use configure(NullToEmptyCollection, enabled) instead.", replaceWith = @ReplaceWith(expression = "configure(KotlinFeature.NullToEmptyCollection, nullToEmptyCollection)", imports = ["com.fasterxml.jackson.module.kotlin.KotlinFeature"]))
      public fun nullToEmptyCollection(nullToEmptyCollection: Boolean): com.fasterxml.jackson.module.kotlin.KotlinModule.Builder {
         return this.configure(KotlinFeature.NullToEmptyCollection, nullToEmptyCollection)
      }

      @Deprecated(message = "Deprecated, use isEnabled(NullToEmptyMap) instead.", replaceWith = @ReplaceWith(expression = "isEnabled(KotlinFeature.NullToEmptyMap)", imports = ["com.fasterxml.jackson.module.kotlin.KotlinFeature"]))
      public fun getNullToEmptyMap(): Boolean {
         return this.isEnabled(KotlinFeature.NullToEmptyMap)
      }

      @Deprecated(message = "Deprecated, use configure(NullToEmptyMap, enabled) instead.", replaceWith = @ReplaceWith(expression = "configure(KotlinFeature.NullToEmptyMap, nullToEmptyMap)", imports = ["com.fasterxml.jackson.module.kotlin.KotlinFeature"]))
      public fun nullToEmptyMap(nullToEmptyMap: Boolean): com.fasterxml.jackson.module.kotlin.KotlinModule.Builder {
         return this.configure(KotlinFeature.NullToEmptyMap, nullToEmptyMap)
      }

      @Deprecated(message = "Deprecated, use isEnabled(NullIsSameAsDefault) instead.", replaceWith = @ReplaceWith(expression = "isEnabled(KotlinFeature.NullIsSameAsDefault)", imports = ["com.fasterxml.jackson.module.kotlin.KotlinFeature"]))
      public fun getNullIsSameAsDefault(): Boolean {
         return this.isEnabled(KotlinFeature.NullIsSameAsDefault)
      }

      @Deprecated(message = "Deprecated, use configure(NullIsSameAsDefault, enabled) instead.", replaceWith = @ReplaceWith(expression = "configure(KotlinFeature.NullIsSameAsDefault, nullIsSameAsDefault)", imports = ["com.fasterxml.jackson.module.kotlin.KotlinFeature"]))
      public fun nullIsSameAsDefault(nullIsSameAsDefault: Boolean): com.fasterxml.jackson.module.kotlin.KotlinModule.Builder {
         return this.configure(KotlinFeature.NullIsSameAsDefault, nullIsSameAsDefault)
      }

      @Deprecated(message = "Deprecated, use isEnabled(SingletonSupport) instead.", replaceWith = @ReplaceWith(expression = "isEnabled(KotlinFeature.SingletonSupport)", imports = ["com.fasterxml.jackson.module.kotlin.KotlinFeature"]))
      public fun getSingletonSupport(): SingletonSupport {
         return if (this.isEnabled(KotlinFeature.SingletonSupport)) SingletonSupport.CANONICALIZE else SingletonSupport.DISABLED
      }

      @Deprecated(message = "Deprecated, use configure(SingletonSupport, enabled) instead.", replaceWith = @ReplaceWith(expression = "configure(KotlinFeature.SingletonSupport, singletonSupport)", imports = ["com.fasterxml.jackson.module.kotlin.KotlinFeature"]))
      public fun singletonSupport(singletonSupport: SingletonSupport): com.fasterxml.jackson.module.kotlin.KotlinModule.Builder {
         return if (KotlinModule.Builder.WhenMappings.$EnumSwitchMapping$0[singletonSupport.ordinal()] == 1)
            this.enable(KotlinFeature.SingletonSupport)
            else
            this.disable(KotlinFeature.SingletonSupport)
         }

      @Deprecated(message = "Deprecated, use isEnabled(StrictNullChecks) instead.", replaceWith = @ReplaceWith(expression = "isEnabled(KotlinFeature.StrictNullChecks)", imports = ["com.fasterxml.jackson.module.kotlin.KotlinFeature"]))
      public fun getStrictNullChecks(): Boolean {
         return this.isEnabled(KotlinFeature.StrictNullChecks)
      }

      @Deprecated(message = "Deprecated, use configure(StrictNullChecks, enabled) instead.", replaceWith = @ReplaceWith(expression = "configure(KotlinFeature.StrictNullChecks, strictNullChecks)", imports = ["com.fasterxml.jackson.module.kotlin.KotlinFeature"]))
      public fun strictNullChecks(strictNullChecks: Boolean): com.fasterxml.jackson.module.kotlin.KotlinModule.Builder {
         return this.configure(KotlinFeature.StrictNullChecks, strictNullChecks)
      }

      public fun build(): KotlinModule {
         return KotlinModule(this, null)
      }

      // $VF: Class flags could not be determined
      @JvmSynthetic
      internal class WhenMappings {
         @JvmStatic
         @JvmSynthetic
         public int[] $EnumSwitchMapping$0;

         @JvmStatic
         fun {
            val var0: IntArray = IntArray(SingletonSupport.values().length)
            var0[SingletonSupport.CANONICALIZE.ordinal()] = 1
            $EnumSwitchMapping$0 = var0
         }
      }
   }

   public companion object {
      public const val serialVersionUID: Long = 1L
   }

   // $VF: Class flags could not be determined
   @JvmSynthetic
   internal class WhenMappings {
      @JvmStatic
      @JvmSynthetic
      public int[] $EnumSwitchMapping$0;

      @JvmStatic
      fun {
         val var0: IntArray = IntArray(SingletonSupport.values().length)
         var0[SingletonSupport.DISABLED.ordinal()] = 1
         var0[SingletonSupport.CANONICALIZE.ordinal()] = 2
         $EnumSwitchMapping$0 = var0
      }
   }
}
