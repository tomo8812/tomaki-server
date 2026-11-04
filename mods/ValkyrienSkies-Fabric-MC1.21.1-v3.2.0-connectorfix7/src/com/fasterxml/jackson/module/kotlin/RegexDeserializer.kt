package com.fasterxml.jackson.module.kotlin

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.deser.std.StdDeserializer
import kotlin.jvm.internal.Intrinsics

public object RegexDeserializer : StdDeserializer(Regex::class.java) {
   public open fun deserialize(p: JsonParser, ctxt: DeserializationContext): Regex {
      // $VF: Couldn't be decompiled
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      // java.lang.NullPointerException: Cannot read field "decompileError" because "method" is null
      //   at org.vineflower.kotlin.expr.KFieldExprent.toJava(KFieldExprent.java:79)
      //   at org.vineflower.kotlin.expr.KFunctionExprent.toJava(KFunctionExprent.java:196)
      //   at org.jetbrains.java.decompiler.modules.decompiler.ExprProcessor.getCastedExprent(ExprProcessor.java:1054)
      //   at org.jetbrains.java.decompiler.modules.decompiler.exps.InvocationExprent.appendParamList(InvocationExprent.java:1151)
      //   at org.jetbrains.java.decompiler.modules.decompiler.exps.InvocationExprent.toJava(InvocationExprent.java:921)
      //
      // Bytecode:
      // 00: aload 1
      // 01: ldc "p"
      // 03: invokestatic kotlin/jvm/internal/Intrinsics.checkNotNullParameter (Ljava/lang/Object;Ljava/lang/String;)V
      // 06: aload 2
      // 07: ldc "ctxt"
      // 09: invokestatic kotlin/jvm/internal/Intrinsics.checkNotNullParameter (Ljava/lang/Object;Ljava/lang/String;)V
      // 0c: aload 2
      // 0d: aload 1
      // 0e: invokevirtual com/fasterxml/jackson/databind/DeserializationContext.readTree (Lcom/fasterxml/jackson/core/JsonParser;)Lcom/fasterxml/jackson/databind/JsonNode;
      // 11: astore 3
      // 12: aload 3
      // 13: invokevirtual com/fasterxml/jackson/databind/JsonNode.isTextual ()Z
      // 16: ifeq 30
      // 19: new kotlin/text/Regex
      // 1c: dup
      // 1d: aload 3
      // 1e: invokevirtual com/fasterxml/jackson/databind/JsonNode.asText ()Ljava/lang/String;
      // 21: astore 4
      // 23: aload 4
      // 25: ldc "node.asText()"
      // 27: invokestatic kotlin/jvm/internal/Intrinsics.checkNotNullExpressionValue (Ljava/lang/Object;Ljava/lang/String;)V
      // 2a: aload 4
      // 2c: invokespecial kotlin/text/Regex.<init> (Ljava/lang/String;)V
      // 2f: areturn
      // 30: aload 3
      // 31: invokevirtual com/fasterxml/jackson/databind/JsonNode.isObject ()Z
      // 34: ifeq a6
      // 37: aload 3
      // 38: ldc "pattern"
      // 3a: invokevirtual com/fasterxml/jackson/databind/JsonNode.get (Ljava/lang/String;)Lcom/fasterxml/jackson/databind/JsonNode;
      // 3d: invokevirtual com/fasterxml/jackson/databind/JsonNode.asText ()Ljava/lang/String;
      // 40: astore 4
      // 42: aload 3
      // 43: ldc "options"
      // 45: invokevirtual com/fasterxml/jackson/databind/JsonNode.has (Ljava/lang/String;)Z
      // 48: ifeq 8e
      // 4b: aload 3
      // 4c: ldc "options"
      // 4e: invokevirtual com/fasterxml/jackson/databind/JsonNode.get (Ljava/lang/String;)Lcom/fasterxml/jackson/databind/JsonNode;
      // 51: astore 6
      // 53: aload 6
      // 55: invokevirtual com/fasterxml/jackson/databind/JsonNode.isArray ()Z
      // 58: ifne 6c
      // 5b: new java/lang/IllegalStateException
      // 5e: dup
      // 5f: ldc "Expected an array of strings for RegexOptions, but type was "
      // 61: aload 3
      // 62: invokevirtual com/fasterxml/jackson/databind/JsonNode.getNodeType ()Lcom/fasterxml/jackson/databind/node/JsonNodeType;
      // 65: invokestatic kotlin/jvm/internal/Intrinsics.stringPlus (Ljava/lang/String;Ljava/lang/Object;)Ljava/lang/String;
      // 68: invokespecial java/lang/IllegalStateException.<init> (Ljava/lang/String;)V
      // 6b: athrow
      // 6c: aload 6
      // 6e: invokevirtual com/fasterxml/jackson/databind/JsonNode.elements ()Ljava/util/Iterator;
      // 71: astore 7
      // 73: aload 7
      // 75: ldc "optionsNode.elements()"
      // 77: invokestatic kotlin/jvm/internal/Intrinsics.checkNotNullExpressionValue (Ljava/lang/Object;Ljava/lang/String;)V
      // 7a: aload 7
      // 7c: invokestatic kotlin/sequences/SequencesKt.asSequence (Ljava/util/Iterator;)Lkotlin/sequences/Sequence;
      // 7f: getstatic com/fasterxml/jackson/module/kotlin/RegexDeserializer$deserialize$options$1.INSTANCE Lcom/fasterxml/jackson/module/kotlin/RegexDeserializer$deserialize$options$1;
      // 82: checkcast kotlin/jvm/functions/Function1
      // 85: invokestatic kotlin/sequences/SequencesKt.map (Lkotlin/sequences/Sequence;Lkotlin/jvm/functions/Function1;)Lkotlin/sequences/Sequence;
      // 88: invokestatic kotlin/sequences/SequencesKt.toSet (Lkotlin/sequences/Sequence;)Ljava/util/Set;
      // 8b: goto 91
      // 8e: invokestatic kotlin/collections/SetsKt.emptySet ()Ljava/util/Set;
      // 91: astore 5
      // 93: new kotlin/text/Regex
      // 96: dup
      // 97: aload 4
      // 99: ldc "pattern"
      // 9b: invokestatic kotlin/jvm/internal/Intrinsics.checkNotNullExpressionValue (Ljava/lang/Object;Ljava/lang/String;)V
      // 9e: aload 4
      // a0: aload 5
      // a2: invokespecial kotlin/text/Regex.<init> (Ljava/lang/String;Ljava/util/Set;)V
      // a5: areturn
      // a6: new java/lang/IllegalStateException
      // a9: dup
      // aa: ldc "Expected a string or an object to deserialize a Regex, but type was "
      // ac: aload 3
      // ad: invokevirtual com/fasterxml/jackson/databind/JsonNode.getNodeType ()Lcom/fasterxml/jackson/databind/node/JsonNodeType;
      // b0: invokestatic kotlin/jvm/internal/Intrinsics.stringPlus (Ljava/lang/String;Ljava/lang/Object;)Ljava/lang/String;
      // b3: invokespecial java/lang/IllegalStateException.<init> (Ljava/lang/String;)V
      // b6: athrow
   }

   // $VF: bridge methodSupplier
   @JvmSynthetic
   override fun deserialize(p0: JsonParser, p1: DeserializationContext) {
      this.deserialize(p0, p1)
   }
}
