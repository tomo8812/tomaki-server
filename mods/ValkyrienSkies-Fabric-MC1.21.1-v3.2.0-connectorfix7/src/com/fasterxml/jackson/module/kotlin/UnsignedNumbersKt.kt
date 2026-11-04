package com.fasterxml.jackson.module.kotlin

import java.math.BigInteger

private final val uLongMaxValue: BigInteger = BigInteger(java.lang.Long.toUnsignedString(-1L))

public fun Short.asUByte(): UByte? {
   return if (`$this$asUByte` >= 0 && `$this$asUByte` <= (short)(-1 and 255))
      UByte.box_impl/* $VF was: box-impl */(UByte.constructor_impl/* $VF was: constructor-impl */((byte)`$this$asUByte`))
      else
      null
   }

public fun Int.asUShort(): UShort? {
   return if (`$this$asUShort` >= 0 && `$this$asUShort` <= (-1 and '\uffff'))
      UShort.box_impl/* $VF was: box-impl */(UShort.constructor_impl/* $VF was: constructor-impl */((short)`$this$asUShort`))
      else
      null
   }

public fun Long.asUInt(): UInt? {
   return if (`$this$asUInt` >= 0L && `$this$asUInt` <= (-1 and 4294967295L))
      UInt.box_impl/* $VF was: box-impl */(UInt.constructor_impl/* $VF was: constructor-impl */((int)`$this$asUInt`))
      else
      null
   }

public fun BigInteger.asULong(): ULong? {
   return if (`$this$asULong`.compareTo(BigInteger.ZERO) >= 0 && `$this$asULong`.compareTo(uLongMaxValue) <= 0)
      ULong.box_impl/* $VF was: box-impl */(ULong.constructor_impl/* $VF was: constructor-impl */(`$this$asULong`.longValue()))
      else
      null
   }
