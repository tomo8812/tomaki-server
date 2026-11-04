package com.fasterxml.jackson.module.kotlin

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.databind.PropertyName
import com.fasterxml.jackson.databind.cfg.MapperConfig
import com.fasterxml.jackson.databind.introspect.Annotated
import com.fasterxml.jackson.databind.introspect.AnnotatedConstructor
import com.fasterxml.jackson.databind.introspect.AnnotatedField
import com.fasterxml.jackson.databind.introspect.AnnotatedMember
import com.fasterxml.jackson.databind.introspect.AnnotatedMethod
import com.fasterxml.jackson.databind.introspect.AnnotatedParameter
import com.fasterxml.jackson.databind.introspect.NopAnnotationIntrospector
import com.fasterxml.jackson.databind.util.BeanUtil
import java.lang.annotation.Annotation
import java.lang.reflect.Constructor
import java.lang.reflect.Member
import java.lang.reflect.Method
import java.util.ArrayList
import java.util.Locale
import kotlin.reflect.KAnnotatedElement
import kotlin.reflect.KClass
import kotlin.reflect.KFunction
import kotlin.reflect.KParameter
import kotlin.reflect.KProperty1
import kotlin.reflect.KParameter.Kind
import kotlin.reflect.full.KClasses
import kotlin.reflect.jvm.ReflectJvmMapping

internal class KotlinNamesAnnotationIntrospector(module: KotlinModule, cache: ReflectionCache, ignoredClassesForImplyingJsonCreator: Set<KClass<*>>)
   : NopAnnotationIntrospector {
   public final val module: KotlinModule
   public final val cache: ReflectionCache
   public final val ignoredClassesForImplyingJsonCreator: Set<KClass<*>>

   init {
      this.module = module
      this.cache = cache
      this.ignoredClassesForImplyingJsonCreator = ignoredClassesForImplyingJsonCreator
   }

   public override fun findImplicitPropertyName(member: AnnotatedMember): String? {
      var var10000: java.lang.String
      if (member is AnnotatedMethod) {
         val var3: java.lang.String = (member as AnnotatedMethod).getName()
         if (StringsKt.contains$default(var3, '-', false, 2, null) && (member as AnnotatedMethod).getParameterCount() == 0) {
            var var4: java.lang.String = (member as AnnotatedMethod).getName()
            if (StringsKt.startsWith$default(var4, "get", false, 2, null)) {
               var4 = (member as AnnotatedMethod).getName()
               var10000 = StringsKt.substringAfter$default(var4, "get", null, 2, null)
            } else {
               var4 = (member as AnnotatedMethod).getName()
               if (StringsKt.startsWith$default(var4, "is", false, 2, null)) {
                  var4 = (member as AnnotatedMethod).getName()
                  var10000 = StringsKt.substringAfter$default(var4, "is", null, 2, null)
               } else {
                  var10000 = null
               }
            }

            if (var10000 == null) {
               var10000 = null
            } else {
               if (var10000.length() > 0) {
                  val var23: StringBuilder = StringBuilder()
                  val it: Char = var10000.charAt(0)
                  val var11: Locale = Locale.getDefault()
                  val var24: StringBuilder = var23.append(CharsKt.lowercase(it, var11).toString())
                  val var10001: java.lang.String = var10000.substring(1)
                  var10000 = var24.append(var10001).toString()
               } else {
                  var10000 = var10000
               }

               var10000 = if (var10000 == null) null else StringsKt.substringBefore$default(var10000, '-', null, 2, null)
            }
         } else {
            var10000 = null
         }
      } else {
         var10000 = if (member is AnnotatedParameter) this.findKotlinParameterName(member as AnnotatedParameter) else null
      }

      return var10000
   }

   public override fun findRenameByField(config: MapperConfig<*>, field: AnnotatedField, implName: PropertyName): PropertyName? {
      val origSimple: java.lang.String = implName.getSimpleName()
      val mangledName: Class = field.getDeclaringClass()
      if (KotlinModuleKt.isKotlinClass(mangledName)) {
         if (StringsKt.startsWith$default(origSimple, "is", false, 2, null)) {
            val var6: java.lang.String = BeanUtil.stdManglePropertyName(origSimple, 2)
            if (var6 != null && !var6.equals(origSimple)) {
               return PropertyName.construct(var6)
            }
         }
      }

      return null
   }

   private fun hasCreatorAnnotation(member: AnnotatedConstructor): Boolean {
      var var10000: ReflectionCache = this.cache
      val kConstructor: Class = member.getDeclaringClass()
      if (kConstructor == null) {
         throw NullPointerException("null cannot be cast to non-null type java.lang.Class<kotlin.Any>")
      } else {
         val var29: KClass = var10000.kotlinFromJava(kConstructor)
         if (this.ignoredClassesForImplyingJsonCreator.contains(var29)) {
            return false
         } else {
            var10000 = this.cache
            val var33: Constructor = member.getAnnotated()
            if (var33 == null) {
               throw NullPointerException("null cannot be cast to non-null type java.lang.reflect.Constructor<kotlin.Any>")
            } else {
               val var31: KFunction = var10000.kotlinFromJava(var33)
               if (var31 == null) {
                  return false
               } else {
                  val var34: java.lang.Iterable = KClasses.getMemberProperties(var29)
                  var `element$iv`: java.util.Collection = ArrayList(CollectionsKt.collectionSizeOrDefault(var34, 10))

                  for (`$this$any$iv` in var34) {
                     `element$iv`.add((`$this$any$iv` as KProperty1).getName())
                  }

                  val var32: java.util.Set = CollectionsKt.toSet(`element$iv` as java.util.List)
                  val var80: Boolean
                  if (KotlinNamesAnnotationIntrospectorKt.access$isPossibleSingleString(var31, var32)) {
                     var80 = false
                  } else {
                     val var35: java.lang.Iterable = var31.getParameters()
                     var var81: Boolean
                     if (var35 is java.util.Collection && (var35 as java.util.Collection).isEmpty()) {
                        var81 = false
                     } else {
                        run label212@{
                           for (var45 in var35) {
                              if ((var45 as KParameter).getName() == null) {
                                 var81 = true
                                 return@label212
                              }
                           }

                           var81 = false
                        }
                     }

                     if (var81) {
                        var80 = false
                     } else if (!KotlinNamesAnnotationIntrospectorKt.access$isPrimaryConstructor(var29, var31)) {
                        var80 = false
                     } else {
                        val var39: java.lang.Iterable = KotlinNamesAnnotationIntrospectorKt.access$filterOutSingleStringCallables(
                           var29.getConstructors(), var32
                        )
                        var var83: Boolean
                        if (var39 is java.util.Collection && (var39 as java.util.Collection).isEmpty()) {
                           var83 = false
                        } else {
                           val var46: java.util.Iterator = var39.iterator()

                           while (true) {
                              if (!var46.hasNext()) {
                                 var83 = false
                                 break
                              }

                              val `$i$f$hasAnnotation`: java.util.Iterator = ((var46.next() as KFunction) as KAnnotatedElement).getAnnotations().iterator()

                              while (true) {
                                 if (!`$i$f$hasAnnotation`.hasNext()) {
                                    var82 = null
                                    break
                                 }

                                 val `$this$findAnnotation$iv$iv`: Any = `$i$f$hasAnnotation`.next()
                                 if (`$this$findAnnotation$iv$iv` as Annotation is JsonCreator) {
                                    var82 = `$this$findAnnotation$iv$iv`
                                    break
                                 }
                              }

                              if (var82 as JsonCreator != null) {
                                 var83 = true
                                 break
                              }
                           }
                        }

                        val var47: Class = member.getType().getRawClass()
                        val var44: KClass = KClasses.getCompanionObject(JvmClassMappingKt.getKotlinClass(var47))
                        val var88: Boolean
                        if (var44 == null) {
                           var88 = false
                        } else {
                           `element$iv` = KClasses.getDeclaredFunctions(var44)
                           if (`element$iv` == null) {
                              var88 = false
                           } else {
                              val var56: java.lang.Iterable = KotlinNamesAnnotationIntrospectorKt.access$filterOutSingleStringCallables(`element$iv`, var32)
                              var var87: Boolean
                              if (var56 is java.util.Collection && (var56 as java.util.Collection).isEmpty()) {
                                 var87 = false
                              } else {
                                 val var60: java.util.Iterator = var56.iterator()

                                 while (true) {
                                    if (!var60.hasNext()) {
                                       var87 = false
                                       break
                                    }

                                    val var62: KFunction = var60.next() as KFunction
                                    var var23: java.util.Iterator = (var62 as KAnnotatedElement).getAnnotations().iterator()

                                    while (true) {
                                       if (!var23.hasNext()) {
                                          var84 = null
                                          break
                                       }

                                       val `element$iv$iv$iv`: Any = var23.next()
                                       if (`element$iv$iv$iv` as Annotation is JsonCreator) {
                                          var84 = `element$iv$iv$iv`
                                          break
                                       }
                                    }

                                    run label236@{
                                       if (var84 as JsonCreator != null) {
                                          var23 = (var62 as KAnnotatedElement).getAnnotations().iterator()

                                          while (true) {
                                             if (!var23.hasNext()) {
                                                var85 = null
                                                break
                                             }

                                             val var76: Any = var23.next()
                                             if (var76 as Annotation is JvmStatic) {
                                                var85 = var76
                                                break
                                             }
                                          }

                                          if ((var85 as JvmStatic) as Annotation != null) {
                                             var86 = true
                                             return@label236
                                          }
                                       }

                                       var86 = false
                                    }

                                    if (var86) {
                                       var87 = true
                                       break
                                    }
                                 }
                              }

                              var88 = var87
                           }
                        }

                        var80 = !var83 && !var88
                     }
                  }

                  return var80
               }
            }
         }
      }
   }

   public override fun hasCreatorAnnotation(member: Annotated): Boolean {
      return member is AnnotatedConstructor
         && KotlinNamesAnnotationIntrospectorKt.access$isKotlinConstructorWithParameters(member as AnnotatedConstructor)
         && this.cache.checkConstructorIsCreatorAnnotated(member as AnnotatedConstructor, {
            // $VF: Could not decompile lambda - root function was not found. Is this a suspend lambda?
         } as (AnnotatedConstructor?) -> java.lang.Boolean)
      }

   private fun findKotlinParameterName(param: AnnotatedParameter): String? {
      val member: Class = param.getDeclaringClass()
      var var31: java.lang.String
      if (KotlinModuleKt.isKotlinClass(member)) {
         val var13: Member = param.getOwner().getMember()
         if (var13 is Constructor) {
            val temp: Constructor = var13 as Constructor

            var parmCount: Int
            try {
               val var19: KFunction = ReflectJvmMapping.getKotlinFunction(temp)
               val var10000: Int
               if (var19 == null) {
                  var10000 = 0
               } else {
                  val ex: java.util.List = var19.getParameters()
                  var10000 = if (ex == null) 0 else ex.size()
               }

               parmCount = var10000
            } catch (var11: Error) {
               parmCount = 0
            } catch (var12: UnsupportedOperationException) {
               parmCount = 0
            }

            if (parmCount > 0 && parmCount == (var13 as Constructor).getParameterTypes().length) {
               val var20: KFunction = ReflectJvmMapping.getKotlinFunction(var13 as Constructor)
               if (var20 == null) {
                  var31 = null
               } else {
                  val var23: java.util.List = var20.getParameters()
                  if (var23 == null) {
                     var31 = null
                  } else {
                     val var27: KParameter = var23.get(param.getIndex()) as KParameter
                     var31 = if (var27 == null) null else var27.getName()
                  }
               }
            } else {
               var31 = null as java.lang.String
            }
         } else if (var13 is Method) {
            var var14: java.lang.String
            try {
               val var15: KFunction = ReflectJvmMapping.getKotlinFunction(var13 as Method)
               val var32: Kind
               if (var15 == null) {
                  var32 = null
               } else {
                  val var21: java.util.List = var15.getParameters()
                  if (var21 == null) {
                     var32 = null
                  } else {
                     val var24: KParameter = CollectionsKt.firstOrNull(var21) as KParameter
                     var32 = if (var24 == null) null else var24.getKind()
                  }
               }

               val var18: Int = if (var32 != Kind.VALUE) param.getIndex() + 1 else param.getIndex()
               val var33: Int
               if (var15 == null) {
                  var33 = 0
               } else {
                  val var28: java.util.List = var15.getParameters()
                  var33 = if (var28 == null) 0 else var28.size()
               }

               if (var33 > var18) {
                  if (var15 == null) {
                     var31 = null
                  } else {
                     val var29: java.util.List = var15.getParameters()
                     if (var29 == null) {
                        var31 = null
                     } else {
                        val var30: KParameter = var29.get(var18) as KParameter
                        var31 = if (var30 == null) null else var30.getName()
                     }
                  }
               } else {
                  var31 = null as java.lang.String
               }

               var14 = var31
            } catch (var10: Error) {
               var14 = null as java.lang.String
            }

            var31 = var14
         } else {
            var31 = null as java.lang.String
         }
      } else {
         var31 = null as java.lang.String
      }

      return var31
   }
}
