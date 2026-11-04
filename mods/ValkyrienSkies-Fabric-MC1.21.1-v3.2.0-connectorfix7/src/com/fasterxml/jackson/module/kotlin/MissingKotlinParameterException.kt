package com.fasterxml.jackson.module.kotlin

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.exc.MismatchedInputException
import java.io.Closeable
import kotlin.reflect.KParameter

public class MissingKotlinParameterException(parameter: KParameter, processor: JsonParser? = null, msg: String) : MismatchedInputException(processor, msg) {
   public final val parameter: KParameter

   init {
      this.parameter = parameter
   }

}
