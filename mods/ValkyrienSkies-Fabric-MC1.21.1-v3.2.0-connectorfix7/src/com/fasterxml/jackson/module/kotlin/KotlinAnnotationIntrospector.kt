package com.fasterxml.jackson.module.kotlin

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonCreator.Mode
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.Module.SetupContext
import com.fasterxml.jackson.databind.cfg.MapperConfig
import com.fasterxml.jackson.databind.introspect.Annotated
import com.fasterxml.jackson.databind.introspect.AnnotatedField
import com.fasterxml.jackson.databind.introspect.AnnotatedMember
import com.fasterxml.jackson.databind.introspect.AnnotatedMethod
import com.fasterxml.jackson.databind.introspect.AnnotatedParameter
import com.fasterxml.jackson.databind.introspect.NopAnnotationIntrospector
import com.fasterxml.jackson.databind.jsontype.NamedType
import com.fasterxml.jackson.databind.ser.std.StdSerializer
import java.lang.annotation.Annotation
import java.lang.reflect.AccessibleObject
import java.lang.reflect.Constructor
import java.lang.reflect.Field
import java.lang.reflect.Member
import java.lang.reflect.Method
import java.lang.reflect.Type
import java.util.ArrayList
import kotlin.reflect.KClass
import kotlin.reflect.KClassifier
import kotlin.reflect.KFunction
import kotlin.reflect.KMutableProperty
import kotlin.reflect.KMutableProperty1
import kotlin.reflect.KParameter
import kotlin.reflect.KProperty
import kotlin.reflect.KProperty1
import kotlin.reflect.KType
import kotlin.reflect.full.KClasses
import kotlin.reflect.jvm.ReflectJvmMapping

internal class KotlinAnnotationIntrospector(context: SetupContext,
      cache: ReflectionCache,
      nullToEmptyCollection: Boolean,
      nullToEmptyMap: Boolean,
      nullIsSameAsDefault: Boolean
   )
   : NopAnnotationIntrospector {
   private final val context: SetupContext
   private final val cache: ReflectionCache
   private final val nullToEmptyCollection: Boolean
   private final val nullToEmptyMap: Boolean
   private final val nullIsSameAsDefault: Boolean

   init {
      this.context = context
      this.cache = cache
      this.nullToEmptyCollection = nullToEmptyCollection
      this.nullToEmptyMap = nullToEmptyMap
      this.nullIsSameAsDefault = nullIsSameAsDefault
   }

   public override fun hasRequiredMarker(m: AnnotatedMember): Boolean? {
      return this.cache.javaMemberIsRequired(m, {
         // $VF: Could not decompile lambda - root function was not found. Is this a suspend lambda?
      } as (AnnotatedMember?) -> java.lang.Boolean)
   }

   public override fun findCreatorAnnotation(config: MapperConfig<*>, a: Annotated): Mode? {
      return super.findCreatorAnnotation(config, a)
   }

   public open fun findSerializer(am: Annotated): StdSerializer<*>? {
      val var49: StdSerializer
      if (am is AnnotatedMethod) {
         if (KotlinVersion.CURRENT.isAtLeast(1, 5)) {
            val kotlinProperty: Method = (am as AnnotatedMethod).getMember()
            val it: Class = kotlinProperty.getReturnType()
            if (ExtensionsKt.isUnboxableValueClass(it)) {
               return null
            }

            val getter: Method = kotlinProperty
            val var20: Class = kotlinProperty.getDeclaringClass()
            val var35: KClass = JvmClassMappingKt.getKotlinClass(var20)

            var outerClazz: java.util.Collection
            try {
               outerClazz = KClasses.getMemberProperties(var35)
            } catch (var16: Error) {
               outerClazz = null as java.util.Collection
            }

            val var47: KProperty1
            if (outerClazz == null) {
               var47 = null
            } else {
               val var39: java.util.Iterator = outerClazz.iterator()

               while (true) {
                  if (var39.hasNext()) {
                     val var42: Any = var39.next()
                     if (!(ReflectJvmMapping.getJavaGetter((var42 as KProperty1) as KProperty) == getter)) {
                        continue
                     }

                     var10000 = var42
                     break
                  }

                  var10000 = null
                  break
               }

               var47 = var10000 as KProperty1
            }

            val var48: KClassifier
            if (var47 == null) {
               var48 = null
            } else {
               val var32: KType = var47.getReturnType()
               var48 = if (var32 == null) null else var32.getClassifier()
            }

            val itx: KClass = var48 as? KClass
            if ((var48 as? KClass) == null) {
               var49 = null
            } else {
               val var24: KClass = if (itx.isValue()) itx else null
               if (var24 == null) {
                  var49 = null
               } else {
                  val var29: Class = JvmClassMappingKt.getJavaClass(var24)
                  if (var29 == null) {
                     var49 = null
                  } else {
                     val var46: Class = getter.getReturnType()
                     val var50: ValueClassStaticJsonValueSerializer.Companion = ValueClassStaticJsonValueSerializer.Companion
                     val var15: ValueClassStaticJsonValueSerializer = var50.createdOrNull(var29, var46)
                     var49 = if (var15 == null) ValueClassBoxSerializer(var29, var46) else var15
                  }
               }
            }
         } else {
            var49 = null
         }
      } else {
         var49 = null
      }

      return var49
   }

   public open fun findNullSerializer(am: Annotated): StdSerializer<*>? {
      return this.findSerializer(am)
   }

   public override fun findSubtypes(a: Annotated): MutableList<NamedType>? {
      val var3: Class = a.getRawType()
      val var2: Class = if (KotlinModuleKt.isKotlinClass(var3)) var3 else null
      val var10000: java.util.List
      if (var2 == null) {
         var10000 = null
      } else {
         var `$this$map$iv`: java.util.Collection = JvmClassMappingKt.getKotlinClass(var2).getSealedSubclasses()
         val `destination$iv$iv`: java.util.Collection = ArrayList(CollectionsKt.collectionSizeOrDefault(`$this$map$iv`, 10))

         for (`item$iv$iv` in `$this$map$iv`) {
            `destination$iv$iv`.add(NamedType(JvmClassMappingKt.getJavaClass(`item$iv$iv` as KClass)))
         }

         `$this$map$iv` = CollectionsKt.toMutableList(`destination$iv$iv` as java.util.List)
         var10000 = (if (`$this$map$iv`.isEmpty()) null else `$this$map$iv`) as java.util.List
      }

      return var10000
   }

   private fun AnnotatedField.hasRequiredMarker(): Boolean? {
      val byNullability: Member = `$this$hasRequiredMarker`.getMember()
      if (byNullability == null) {
         throw NullPointerException("null cannot be cast to non-null type java.lang.reflect.Field")
      } else {
         val byAnnotation: java.lang.Boolean = this.isRequiredByAnnotation(byNullability as Field)
         val var5: Member = `$this$hasRequiredMarker`.getMember()
         if (var5 == null) {
            throw NullPointerException("null cannot be cast to non-null type java.lang.reflect.Field")
         } else {
            val var4: KProperty = ReflectJvmMapping.getKotlinProperty(var5 as Field)
            val var10000: java.lang.Boolean
            if (var4 == null) {
               var10000 = null
            } else {
               val var7: KType = var4.getReturnType()
               var10000 = if (var7 == null) null else this.isRequired(var7)
            }

            return this.requiredAnnotationOrNullability(byAnnotation, var10000)
         }
      }
   }

   private fun AccessibleObject.isRequiredByAnnotation(): Boolean? {
      val var2: Array<Annotation> = `$this$isRequiredByAnnotation`.getAnnotations()
      val var10000: java.lang.Boolean
      if (var2 == null) {
         var10000 = null
      } else {
         val var6: Array<Annotation> = var2
         val var7: Int = var2.length
         var it: Int = 0

         while (true) {
            if (it >= var7) {
               var18 = null
               break
            }

            val var9: Any = var6[it]
            if (JvmClassMappingKt.getAnnotationClass(var6[it]) == JsonProperty::class) {
               var18 = (Annotation)var9
               break
            }

            it++
         }

         var10000 = if (var18 == null) null else (var18 as JsonProperty).required()
      }

      return var10000
   }

   private fun requiredAnnotationOrNullability(byAnnotation: Boolean?, byNullability: Boolean?): Boolean? {
      if (byAnnotation != null && byNullability != null) {
         return byAnnotation || byNullability
      } else {
         return byNullability ?: byAnnotation
      }
   }

   private fun Method.isRequiredByAnnotation(): Boolean? {
      val `$this$firstOrNull$iv`: Array<Annotation> = `$this$isRequiredByAnnotation`.getAnnotations()
      val var6: Array<Any> = `$this$firstOrNull$iv`
      val var7: Int = `$this$firstOrNull$iv`.length
      var var8: Int = 0

      var var10000: Any
      while (true) {
         if (var8 >= var7) {
            var10000 = null
            break
         }

         val `element$iv`: Any = var6[var8]
         if (JvmClassMappingKt.getJavaClass(JvmClassMappingKt.getAnnotationClass(var6[var8] as Annotation)) == JsonProperty::class.java) {
            var10000 = `element$iv`
            break
         }

         var8++
      }

      return if ((var10000 as? JsonProperty) == null) null else (var10000 as? JsonProperty).required()
   }

   private fun KProperty1<*, *>.isRequiredByNullability(): Boolean {
      return this.isRequired(`$this$isRequiredByNullability`.getReturnType())
   }

   private fun AnnotatedMethod.hasRequiredMarker(): Boolean? {
      val var2: java.lang.Boolean = this.getRequiredMarkerFromCorrespondingAccessor(`$this$hasRequiredMarker`)
      val var10000: java.lang.Boolean
      if (var2 == null) {
         val var3: Method = `$this$hasRequiredMarker`.getMember()
         var10000 = this.getRequiredMarkerFromAccessorLikeMethod(var3)
      } else {
         var10000 = var2
      }

      return var10000
   }

   private fun AnnotatedMethod.getRequiredMarkerFromCorrespondingAccessor(): Boolean? {
      val `$this$forEach$iv`: Class = `$this$getRequiredMarkerFromCorrespondingAccessor`.getMember().getDeclaringClass()
      val var4: java.util.Iterator = KClasses.getDeclaredMemberProperties(JvmClassMappingKt.getKotlinClass(`$this$forEach$iv`)).iterator()

      val kProperty: KProperty1
      do {
         if (!var4.hasNext()) {
            return null
         }

         kProperty = var4.next() as KProperty1
         if (ReflectJvmMapping.getJavaGetter(kProperty as KProperty) == `$this$getRequiredMarkerFromCorrespondingAccessor`.getMember()) {
            break
         }
      } while (
         !(
            (if ((kProperty as? KMutableProperty1) == null) null else ReflectJvmMapping.getJavaSetter((kProperty as? KMutableProperty1) as KMutableProperty))
               == `$this$getRequiredMarkerFromCorrespondingAccessor`.getMember()
         )
      )

      val byNullability: Method = `$this$getRequiredMarkerFromCorrespondingAccessor`.getMember()
      return this.requiredAnnotationOrNullability(this.isRequiredByAnnotation(byNullability), this.isRequiredByNullability(kProperty))
   }

   private fun Method.getRequiredMarkerFromAccessorLikeMethod(): Boolean? {
      val var2: KFunction = ReflectJvmMapping.getKotlinFunction(`$this$getRequiredMarkerFromAccessorLikeMethod`)
      if (var2 == null) {
         return null
      } else {
         val byAnnotation: java.lang.Boolean = this.isRequiredByAnnotation(`$this$getRequiredMarkerFromAccessorLikeMethod`)
         return if (this.isGetterLike(var2))
            this.requiredAnnotationOrNullability(byAnnotation, this.isRequired(var2.getReturnType()))
            else
            (if (this.isSetterLike(var2)) this.requiredAnnotationOrNullability(byAnnotation, this.isMethodParameterRequired(var2, 0)) else null)
         }
   }

   private fun KFunction<*>.isGetterLike(): Boolean {
      return `$this$isGetterLike`.getParameters().size() == 1
   }

   private fun KFunction<*>.isSetterLike(): Boolean {
      return `$this$isSetterLike`.getParameters().size() == 2 && `$this$isSetterLike`.getReturnType() == UNIT_TYPE
   }

   private fun AnnotatedParameter.hasRequiredMarker(): Boolean? {
      val member: Member = `$this$hasRequiredMarker`.getMember()
      val byNullability: JsonProperty = `$this$hasRequiredMarker`.getAnnotation(JsonProperty.class)
      val byAnnotation: java.lang.Boolean = if (byNullability == null) null else byNullability.required()
      val var10000: java.lang.Boolean
      if (member is Constructor) {
         val var6: KFunction = ReflectJvmMapping.getKotlinFunction(member as Constructor)
         var10000 = if (var6 == null) null else this.isConstructorParameterRequired(var6, `$this$hasRequiredMarker`.getIndex())
      } else if (member is Method) {
         val var8: KFunction = ReflectJvmMapping.getKotlinFunction(member as Method)
         var10000 = if (var8 == null) null else this.isMethodParameterRequired(var8, `$this$hasRequiredMarker`.getIndex())
      } else {
         var10000 = null
      }

      return this.requiredAnnotationOrNullability(byAnnotation, var10000)
   }

   private fun KFunction<*>.isConstructorParameterRequired(index: Int): Boolean {
      return this.isParameterRequired(`$this$isConstructorParameterRequired`, index)
   }

   private fun KFunction<*>.isMethodParameterRequired(index: Int): Boolean {
      return this.isParameterRequired(`$this$isMethodParameterRequired`, index + 1)
   }

   private fun KFunction<*>.isParameterRequired(index: Int): Boolean {
      val param: KParameter = `$this$isParameterRequired`.getParameters().get(index) as KParameter
      val paramType: KType = param.getType()
      val javaType: Type = ReflectJvmMapping.getJavaType(paramType)
      return !paramType.isMarkedNullable()
         && !param.isOptional()
         && (javaType !is Class || !(javaType as Class).isPrimitive() || this.context.isEnabled(DeserializationFeature.FAIL_ON_NULL_FOR_PRIMITIVES))
      }

   private fun KType.isRequired(): Boolean {
      return !`$this$isRequired`.isMarkedNullable()
   }

   // $VF: bridge methodSupplier
   @JvmSynthetic
   override fun findSerializer(p0: Annotated) {
      this.findSerializer(p0)
   }

   // $VF: bridge methodSupplier
   @JvmSynthetic
   override fun findNullSerializer(p0: Annotated) {
      this.findNullSerializer(p0)
   }

   public companion object {
      public final val UNIT_TYPE: KType
   }
}
