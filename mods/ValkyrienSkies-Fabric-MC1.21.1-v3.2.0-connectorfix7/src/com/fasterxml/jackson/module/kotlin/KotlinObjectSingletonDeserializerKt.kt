package com.fasterxml.jackson.module.kotlin

import com.fasterxml.jackson.databind.JsonDeserializer

internal fun JsonDeserializer<*>.asSingletonDeserializer(singleton: Any): KotlinObjectSingletonDeserializer {
   return KotlinObjectSingletonDeserializer(singleton, `$this$asSingletonDeserializer`)
}
