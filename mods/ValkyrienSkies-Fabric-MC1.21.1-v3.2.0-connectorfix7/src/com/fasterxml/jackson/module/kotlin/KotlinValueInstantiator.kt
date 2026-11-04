package com.fasterxml.jackson.module.kotlin

import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JsonDeserializer
import com.fasterxml.jackson.databind.JsonMappingException
import com.fasterxml.jackson.databind.deser.SettableBeanProperty
import com.fasterxml.jackson.databind.deser.impl.NullsAsEmptyProvider
import com.fasterxml.jackson.databind.deser.impl.PropertyValueBuffer
import com.fasterxml.jackson.databind.deser.std.StdValueInstantiator
import com.fasterxml.jackson.databind.introspect.AnnotatedWithParams
import java.lang.reflect.Type
import java.lang.reflect.TypeVariable
import java.util.ArrayList
import java.util.LinkedHashMap
import kotlin.reflect.KParameter
import kotlin.reflect.KType
import kotlin.reflect.KTypeProjection
import kotlin.reflect.jvm.ReflectJvmMapping

internal class KotlinValueInstantiator(src: StdValueInstantiator,
   cache: ReflectionCache,
   nullToEmptyCollection: Boolean,
   nullToEmptyMap: Boolean,
   nullIsSameAsDefault: Boolean,
   strictNullChecks: Boolean
) : StdValueInstantiator(src) {
   private final val cache: ReflectionCache
   private final val nullToEmptyCollection: Boolean
   private final val nullToEmptyMap: Boolean
   private final val nullIsSameAsDefault: Boolean
   private final val strictNullChecks: Boolean

   init {
      this.cache = cache
      this.nullToEmptyCollection = nullToEmptyCollection
      this.nullToEmptyMap = nullToEmptyMap
      this.nullIsSameAsDefault = nullIsSameAsDefault
      this.strictNullChecks = strictNullChecks
   }

   public override fun createFromObjectWith(ctxt: DeserializationContext, props: Array<out SettableBeanProperty>, buffer: PropertyValueBuffer): Any? {
      val var10000: ReflectionCache = this.cache
      val numCallableParameters: AnnotatedWithParams = this._withArgsCreator
      val propCount: ValueCreator = var10000.valueCreatorFromJava(numCallableParameters)
      if (propCount == null) {
         return super.createFromObjectWith(ctxt, props, buffer)
      } else {
         var var40: Int
         val var41: Array<Any>
         val var42: Array<Any>
         if (propCount is MethodValueCreator) {
            val var37: Int = props.length + 1
            var40 = 1
            var callableParametersByName: Array<KParameter> = arrayOfNulls(var37)
            callableParametersByName[0] = (propCount as MethodValueCreator).instanceParameter
            var41 = callableParametersByName
            callableParametersByName = arrayOfNulls(var37)
            callableParametersByName[0] = (KParameter)(propCount as MethodValueCreator).companionObjectInstance
            var42 = callableParametersByName
         } else {
            val var38: Int = props.length
            var40 = 0
            var41 = arrayOfNulls(var38)
            var42 = arrayOfNulls(var38)
         }

         val var44: java.lang.Iterable = propCount.valueParameters
         var var51: Int = 0
         val var54: java.util.Iterator = var44.iterator()

         while (true) {
            val paramDef: KParameter
            val `item$iv$iv`: SettableBeanProperty
            val paramDefx: Boolean
            while (true) {
               if (!var54.hasNext()) {
                  val var111: Any
                  if (var40 == var42.length && propCount is ConstructorValueCreator) {
                     var111 = super.createFromObjectWith(ctxt, var42)
                  } else {
                     propCount.checkAccessibility(ctxt)
                     val var45: LinkedHashMap = LinkedHashMap()
                     val var58: java.util.Collection = ArrayList(var41.length)
                     var var60: Int = 0
                     val var61: Array<Any> = var41
                     val var62: Int = var41.length

                     repeat(var62) { var63 ->
                        val var64: Any = var61[var63]
                        val var113: Int = var60++
                        if (var64 != null) {
                           var45.put(var64, var42[var113])
                        }

                        var58.add(Unit.INSTANCE)
                     }

                     var111 = propCount.callBy(var45)
                  }

                  return var111
               }

               val var57: Any = var54.next()
               val `$i$f$mapIndexedTo`: Int = var51++
               if (`$i$f$mapIndexedTo` < 0) {
                  CollectionsKt.throwIndexOverflow()
               }

               paramDef = var57 as KParameter
               `item$iv$iv` = props[`$i$f$mapIndexedTo`]
               paramDefx = !buffer.hasParameter(props[`$i$f$mapIndexedTo`])
               if (!paramDefx || !paramDef.isOptional()) {
                  if (paramDefx && !this.isPrimitive(paramDef) && !this.hasInjectableValueId(`item$iv$iv`)) {
                     if (paramDef.getType().isMarkedNullable()) {
                        var102 = null
                     } else {
                        val var66: JsonDeserializer = `item$iv$iv`.getValueDeserializer()
                        var102 = if (var66 == null) null else var66.getAbsentValue(ctxt)
                     }
                     break
                  }

                  val idx: Any = buffer.getParameter(`item$iv$iv`)
                  if (!this.nullIsSameAsDefault || idx != null || !paramDef.isOptional()) {
                     var102 = idx
                     break
                  }
               }
            }

            var var22: Any = var102
            if (var102 == null
               && (this.nullToEmptyCollection && `item$iv$iv`.getType().isCollectionLikeType() || this.nullToEmptyMap && `item$iv$iv`.getType().isMapLikeType())
               )
             {
               var22 = NullsAsEmptyProvider(`item$iv$iv`.getValueDeserializer()).getNullValue(ctxt)
            }

            if (var22 == null && paramDefx && `item$iv$iv`.isRequired()
               || ReflectJvmMapping.getJavaType(paramDef.getType()) !is TypeVariable && var22 == null && !paramDef.getType().isMarkedNullable()) {
               val var110: JsonMappingException = MissingKotlinParameterException(
                  paramDef,
                  ctxt.getParser(),
                  "Instantiation of ${this.getValueTypeDesc()} value failed for JSON property ${`item$iv$iv`.getName()} due to missing (therefore NULL) value for creator parameter ${paramDef.getName()} which is a non-nullable type"
               )
               val var112: Class = this.getValueClass()
               val var71: java.lang.String = `item$iv$iv`.getName()
               val var74: JsonMappingException = ExtensionsKt.wrapWithPath(var110, var112, var71)
               throw var74 as java.lang.Throwable
            }

            if (this.strictNullChecks && var22 != null) {
               var paramType: java.lang.String = null
               var itemType: KType = null
               if (`item$iv$iv`.getType().isCollectionLikeType()) {
                  val `$this$any$iv`: KTypeProjection = CollectionsKt.getOrNull(paramDef.getType().getArguments(), 0) as KTypeProjection
                  val var103: Boolean
                  if (`$this$any$iv` == null) {
                     var103 = false
                  } else {
                     val `$i$f$any`: KType = `$this$any$iv`.getType()
                     var103 = `$i$f$any` != null && !`$i$f$any`.isMarkedNullable()
                  }

                  if (var103) {
                     val var77: java.lang.Iterable = var22 as java.util.Collection
                     var var104: Boolean
                     if (var22 as java.util.Collection is java.util.Collection && ((var22 as java.util.Collection) as java.util.Collection).isEmpty()) {
                        var104 = false
                     } else {
                        run label345@{
                           for (`element$iv` in var77) {
                              if (`element$iv` == null) {
                                 var104 = true
                                 return@label345
                              }
                           }

                           var104 = false
                        }
                     }

                     if (var104) {
                        paramType = "collection"
                        itemType = (paramDef.getType().getArguments().get(0) as KTypeProjection).getType()
                     }
                  }
               }

               if (`item$iv$iv`.getType().isMapLikeType()) {
                  val var78: KTypeProjection = CollectionsKt.getOrNull(paramDef.getType().getArguments(), 1) as KTypeProjection
                  val var105: Boolean
                  if (var78 == null) {
                     var105 = false
                  } else {
                     val var86: KType = var78.getType()
                     var105 = var86 != null && !var86.isMarkedNullable()
                  }

                  if (var105) {
                     val var80: java.util.Map = var22 as java.util.Map
                     var var106: Boolean
                     if ((var22 as java.util.Map).isEmpty()) {
                        var106 = false
                     } else {
                        run label355@{
                           for (var97 in var80.entrySet()) {
                              if (var97.getValue() == null) {
                                 var106 = true
                                 return@label355
                              }
                           }

                           var106 = false
                        }
                     }

                     if (var106) {
                        paramType = "map"
                        itemType = (paramDef.getType().getArguments().get(1) as KTypeProjection).getType()
                     }
                  }
               }

               if (`item$iv$iv`.getType().isArrayType()) {
                  val var81: KTypeProjection = CollectionsKt.getOrNull(paramDef.getType().getArguments(), 0) as KTypeProjection
                  val var107: Boolean
                  if (var81 == null) {
                     var107 = false
                  } else {
                     val var88: KType = var81.getType()
                     var107 = var88 != null && !var88.isMarkedNullable()
                  }

                  if (var107) {
                     val var92: Array<Any> = var22 as Array<Any>
                     val var94: Int = (var22 as Array<Any>).length
                     var var96: Int = 0

                     while (true) {
                        if (var96 >= var94) {
                           var108 = false
                           break
                        }

                        if (var92[var96] == null) {
                           var108 = true
                           break
                        }

                        var96++
                     }

                     if (var108) {
                        paramType = "array"
                        itemType = (paramDef.getType().getArguments().get(0) as KTypeProjection).getType()
                     }
                  }
               }

               if (paramType != null && itemType != null) {
                  val var109: JsonMappingException = MissingKotlinParameterException(
                     paramDef,
                     ctxt.getParser(),
                     "Instantiation of $itemType $paramType failed for JSON property ${`item$iv$iv`.getName()} due to null value in a $paramType that does not allow null values"
                  )
                  val var10001: Class = this.getValueClass()
                  val var90: java.lang.String = `item$iv$iv`.getName()
                  val var84: JsonMappingException = ExtensionsKt.wrapWithPath(var109, var10001, var90)
                  throw var84 as java.lang.Throwable
               }
            }

            var42[var40] = var22
            var41[var40] = paramDef
            val var73: Int = var40++
         }
      }
   }

   private fun KParameter.isPrimitive(): Boolean {
      val javaType: Type = ReflectJvmMapping.getJavaType(`$this$isPrimitive`.getType())
      return javaType is Class && (javaType as Class).isPrimitive()
   }

   private fun SettableBeanProperty.hasInjectableValueId(): Boolean {
      return `$this$hasInjectableValueId`.getInjectableValueId() != null
   }
}
