package com.fasterxml.jackson.module.kotlin

private fun objectSingletonInstance(beanClass: Class<*>): Any? {
   return if (!KotlinModuleKt.isKotlinClass(beanClass)) null else JvmClassMappingKt.getKotlinClass(beanClass).getObjectInstance()
}

@JvmSynthetic
fun `access$objectSingletonInstance`(beanClass: Class) {
   objectSingletonInstance(beanClass)
}
