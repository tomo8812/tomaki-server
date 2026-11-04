package org.valkyrienskies.core.impl.shadow;

import java.io.PrintStream;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.ArrayDeque;
import java.util.Collection;
import java.util.Collections;
import java.util.Deque;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.Map.Entry;

public final class fv {
   private static SortedMap a;
   private static final String b = "    ";

   private fv() {
   }

   private static <K, V> V a(Map<? super K, V> var0, K var1) {
      return (V)(var0 != null ? var0.get(var1) : null);
   }

   private static <K> String b(Map<? super K, ?> var0, K var1) {
      Object var2;
      return var0 != null && (var2 = var0.get(var1)) != null ? var2.toString() : null;
   }

   private static <K> Boolean c(Map<? super K, ?> var0, K var1) {
      Object var2;
      if (var0 != null && (var2 = var0.get(var1)) != null) {
         if (var2 instanceof Boolean) {
            return (Boolean)var2;
         }

         if (var2 instanceof String) {
            return Boolean.valueOf((String)var2);
         }

         if (var2 instanceof Number) {
            Number var3;
            if ((var3 = (Number)var2).intValue() != 0) {
               return Boolean.TRUE;
            }

            return Boolean.FALSE;
         }
      }

      return null;
   }

   private static <K> Number d(Map<? super K, ?> var0, K var1) {
      Object var3;
      if (var0 != null && (var3 = var0.get(var1)) != null) {
         if (var3 instanceof Number) {
            return (Number)var3;
         }

         if (var3 instanceof String) {
            try {
               String var4 = (String)var3;
               return NumberFormat.getInstance().parse(var4);
            } catch (ParseException var2) {
            }
         }
      }

      return null;
   }

   private static <K> Byte e(Map<? super K, ?> var0, K var1) {
      Number var2;
      if ((var2 = d(var0, var1)) == null) {
         return null;
      } else {
         return var2 instanceof Byte ? (Byte)var2 : var2.byteValue();
      }
   }

   private static <K> Short f(Map<? super K, ?> var0, K var1) {
      Number var2;
      if ((var2 = d(var0, var1)) == null) {
         return null;
      } else {
         return var2 instanceof Short ? (Short)var2 : var2.shortValue();
      }
   }

   private static <K> Integer g(Map<? super K, ?> var0, K var1) {
      Number var2;
      if ((var2 = d(var0, var1)) == null) {
         return null;
      } else {
         return var2 instanceof Integer ? (Integer)var2 : var2.intValue();
      }
   }

   private static <K> Long h(Map<? super K, ?> var0, K var1) {
      Number var2;
      if ((var2 = d(var0, var1)) == null) {
         return null;
      } else {
         return var2 instanceof Long ? (Long)var2 : var2.longValue();
      }
   }

   private static <K> Float i(Map<? super K, ?> var0, K var1) {
      Number var2;
      if ((var2 = d(var0, var1)) == null) {
         return null;
      } else {
         return var2 instanceof Float ? (Float)var2 : var2.floatValue();
      }
   }

   private static <K> Double j(Map<? super K, ?> var0, K var1) {
      Number var2;
      if ((var2 = d(var0, var1)) == null) {
         return null;
      } else {
         return var2 instanceof Double ? (Double)var2 : var2.doubleValue();
      }
   }

   private static <K> Map<?, ?> k(Map<? super K, ?> var0, K var1) {
      Object var2;
      return var0 != null && (var2 = var0.get(var1)) != null && var2 instanceof Map ? (Map)var2 : null;
   }

   private static <K, V> V a(Map<K, V> var0, K var1, V var2) {
      Object var3;
      return (V)(var0 != null && (var3 = var0.get(var1)) != null ? var3 : var2);
   }

   private static <K> String a(Map<? super K, ?> var0, K var1, String var2) {
      var1 = var1;
      var0 = var0;
      Object var4;
      if ((var4 = var0 != null && (var4 = (String)var0.get(var1)) != null ? var4.toString() : null) == null) {
         var4 = var2;
      }

      return var4;
   }

   private static <K> Boolean a(Map<? super K, ?> var0, K var1, Boolean var2) {
      Boolean var3;
      if ((var3 = c(var0, var1)) == null) {
         var3 = var2;
      }

      return var3;
   }

   private static <K> Number a(Map<? super K, ?> var0, K var1, Number var2) {
      Number var3;
      if ((var3 = d(var0, var1)) == null) {
         var3 = var2;
      }

      return var3;
   }

   private static <K> Byte a(Map<? super K, ?> var0, K var1, Byte var2) {
      Byte var3;
      if ((var3 = e(var0, var1)) == null) {
         var3 = var2;
      }

      return var3;
   }

   private static <K> Short a(Map<? super K, ?> var0, K var1, Short var2) {
      Short var3;
      if ((var3 = f(var0, var1)) == null) {
         var3 = var2;
      }

      return var3;
   }

   private static <K> Integer a(Map<? super K, ?> var0, K var1, Integer var2) {
      Integer var3;
      if ((var3 = g(var0, var1)) == null) {
         var3 = var2;
      }

      return var3;
   }

   private static <K> Long a(Map<? super K, ?> var0, K var1, Long var2) {
      Long var3;
      if ((var3 = h(var0, var1)) == null) {
         var3 = var2;
      }

      return var3;
   }

   private static <K> Float a(Map<? super K, ?> var0, K var1, Float var2) {
      Float var3;
      if ((var3 = i(var0, var1)) == null) {
         var3 = var2;
      }

      return var3;
   }

   private static <K> Double a(Map<? super K, ?> var0, K var1, Double var2) {
      Double var3;
      if ((var3 = j(var0, var1)) == null) {
         var3 = var2;
      }

      return var3;
   }

   private static <K> Map<?, ?> a(Map<? super K, ?> var0, K var1, Map<?, ?> var2) {
      var1 = var1;
      var0 = var0;
      if ((var0 = var0 != null && (var0 = (Map)var0.get(var1)) != null && var0 instanceof Map ? var0 : null) == null) {
         var0 = var2;
      }

      return var0;
   }

   private static <K> boolean l(Map<? super K, ?> var0, K var1) {
      return Boolean.TRUE.equals(c(var0, var1));
   }

   private static <K> byte m(Map<? super K, ?> var0, K var1) {
      Byte var2;
      return (var2 = e(var0, var1)) == null ? 0 : var2;
   }

   private static <K> short n(Map<? super K, ?> var0, K var1) {
      Short var2;
      return (var2 = f(var0, var1)) == null ? 0 : var2;
   }

   private static <K> int o(Map<? super K, ?> var0, K var1) {
      Integer var2;
      return (var2 = g(var0, var1)) == null ? 0 : var2;
   }

   private static <K> long p(Map<? super K, ?> var0, K var1) {
      Long var2;
      return (var2 = h(var0, var1)) == null ? 0L : var2;
   }

   private static <K> float q(Map<? super K, ?> var0, K var1) {
      Float var2;
      return (var2 = i(var0, var1)) == null ? 0.0F : var2;
   }

   private static <K> double r(Map<? super K, ?> var0, K var1) {
      Double var2;
      return (var2 = j(var0, var1)) == null ? 0.0 : var2;
   }

   private static <K> boolean a(Map<? super K, ?> var0, K var1, boolean var2) {
      Boolean var3;
      return (var3 = c(var0, var1)) == null ? var2 : var3;
   }

   private static <K> byte a(Map<? super K, ?> var0, K var1, byte var2) {
      Byte var3;
      return (var3 = e(var0, var1)) == null ? var2 : var3;
   }

   private static <K> short a(Map<? super K, ?> var0, K var1, short var2) {
      Short var3;
      return (var3 = f(var0, var1)) == null ? var2 : var3;
   }

   private static <K> int a(Map<? super K, ?> var0, K var1, int var2) {
      Integer var3;
      return (var3 = g(var0, var1)) == null ? var2 : var3;
   }

   private static <K> long a(Map<? super K, ?> var0, K var1, long var2) {
      Long var4;
      return (var4 = h(var0, var1)) == null ? var2 : var4;
   }

   private static <K> float a(Map<? super K, ?> var0, K var1, float var2) {
      Float var3;
      return (var3 = i(var0, var1)) == null ? var2 : var3;
   }

   private static <K> double a(Map<? super K, ?> var0, K var1, double var2) {
      Double var4;
      return (var4 = j(var0, var1)) == null ? var2 : var4;
   }

   private static <K, V> Properties b(Map<K, V> var0) {
      Properties var1 = new Properties();
      if (var0 != null) {
         Iterator var4 = var0.entrySet().iterator();

         while (var4.hasNext()) {
            Entry var2;
            Object var3 = (var2 = (Entry)var4.next()).getKey();
            Object var5 = var2.getValue();
            var1.put(var3, var5);
         }
      }

      return var1;
   }

   private static Map<String, Object> a(ResourceBundle var0) {
      Enumeration var1 = var0.getKeys();
      HashMap var2 = new HashMap();

      while (var1.hasMoreElements()) {
         String var3 = (String)var1.nextElement();
         Object var4 = var0.getObject(var3);
         var2.put(var3, var4);
      }

      return var2;
   }

   private static void a(PrintStream var0, Object var1, Map<?, ?> var2) {
      a(var0, var1, var2, new ArrayDeque<>(), false);
   }

   private static void b(PrintStream var0, Object var1, Map<?, ?> var2) {
      a(var0, var1, var2, new ArrayDeque<>(), true);
   }

   private static void a(PrintStream var0, Object var1, Map<?, ?> var2, Deque<Map<?, ?>> var3, boolean var4) {
      // $VF: Couldn't be decompiled
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      // java.lang.IllegalStateException: No common supertype for ternary expression
      //   at org.jetbrains.java.decompiler.modules.decompiler.exps.FunctionExprent.getExprType(FunctionExprent.java:224)
      //   at org.jetbrains.java.decompiler.modules.decompiler.exps.InvocationExprent.matches(InvocationExprent.java:1363)
      //   at org.jetbrains.java.decompiler.modules.decompiler.exps.InvocationExprent.getMatchedDescriptors(InvocationExprent.java:1317)
      //   at org.jetbrains.java.decompiler.modules.decompiler.exps.InvocationExprent.getMatchedDescriptors(InvocationExprent.java:1290)
      //   at org.jetbrains.java.decompiler.modules.decompiler.exps.InvocationExprent.appendParamList(InvocationExprent.java:987)
      //   at org.jetbrains.java.decompiler.modules.decompiler.exps.InvocationExprent.toJava(InvocationExprent.java:921)
      //   at org.jetbrains.java.decompiler.modules.decompiler.ExprProcessor.listToJava(ExprProcessor.java:925)
      //   at org.jetbrains.java.decompiler.modules.decompiler.stats.BasicBlockStatement.toJava(BasicBlockStatement.java:87)
      //   at org.jetbrains.java.decompiler.modules.decompiler.ExprProcessor.jmpWrapper(ExprProcessor.java:860)
      //   at org.jetbrains.java.decompiler.modules.decompiler.stats.IfStatement.toJava(IfStatement.java:238)
      //   at org.jetbrains.java.decompiler.modules.decompiler.ExprProcessor.jmpWrapper(ExprProcessor.java:860)
      //   at org.jetbrains.java.decompiler.modules.decompiler.stats.DoStatement.toJava(DoStatement.java:126)
      //   at org.jetbrains.java.decompiler.modules.decompiler.ExprProcessor.jmpWrapper(ExprProcessor.java:860)
      //   at org.jetbrains.java.decompiler.modules.decompiler.stats.SequenceStatement.toJava(SequenceStatement.java:107)
      //   at org.jetbrains.java.decompiler.modules.decompiler.ExprProcessor.jmpWrapper(ExprProcessor.java:860)
      //   at org.jetbrains.java.decompiler.modules.decompiler.stats.IfStatement.toJava(IfStatement.java:258)
      //   at org.jetbrains.java.decompiler.modules.decompiler.stats.RootStatement.toJava(RootStatement.java:36)
      //   at org.jetbrains.java.decompiler.main.ClassWriter.writeMethod(ClassWriter.java:1351)
      //
      // Bytecode:
      // 000: aload 0
      // 001: aload 3
      // 002: invokeinterface java/util/Deque.size ()I 1
      // 007: invokestatic org/valkyrienskies/core/impl/shadow/fv.a (Ljava/io/PrintStream;I)V
      // 00a: aload 2
      // 00b: ifnonnull 026
      // 00e: aload 1
      // 00f: ifnull 01e
      // 012: aload 0
      // 013: aload 1
      // 014: invokevirtual java/io/PrintStream.print (Ljava/lang/Object;)V
      // 017: aload 0
      // 018: ldc_w " = "
      // 01b: invokevirtual java/io/PrintStream.print (Ljava/lang/String;)V
      // 01e: aload 0
      // 01f: ldc_w "null"
      // 022: invokevirtual java/io/PrintStream.println (Ljava/lang/String;)V
      // 025: return
      // 026: aload 1
      // 027: ifnull 036
      // 02a: aload 0
      // 02b: aload 1
      // 02c: invokevirtual java/io/PrintStream.print (Ljava/lang/Object;)V
      // 02f: aload 0
      // 030: ldc_w " = "
      // 033: invokevirtual java/io/PrintStream.println (Ljava/lang/String;)V
      // 036: aload 0
      // 037: aload 3
      // 038: invokeinterface java/util/Deque.size ()I 1
      // 03d: invokestatic org/valkyrienskies/core/impl/shadow/fv.a (Ljava/io/PrintStream;I)V
      // 040: aload 0
      // 041: ldc_w "{"
      // 044: invokevirtual java/io/PrintStream.println (Ljava/lang/String;)V
      // 047: aload 3
      // 048: aload 2
      // 049: invokeinterface java/util/Deque.addLast (Ljava/lang/Object;)V 2
      // 04e: aload 2
      // 04f: invokeinterface java/util/Map.entrySet ()Ljava/util/Set; 1
      // 054: invokeinterface java/util/Set.iterator ()Ljava/util/Iterator; 1
      // 059: astore 1
      // 05a: aload 1
      // 05b: invokeinterface java/util/Iterator.hasNext ()Z 1
      // 060: ifeq 144
      // 063: aload 1
      // 064: invokeinterface java/util/Iterator.next ()Ljava/lang/Object; 1
      // 069: checkcast java/util/Map$Entry
      // 06c: dup
      // 06d: astore 5
      // 06f: invokeinterface java/util/Map$Entry.getKey ()Ljava/lang/Object; 1
      // 074: astore 6
      // 076: aload 5
      // 078: invokeinterface java/util/Map$Entry.getValue ()Ljava/lang/Object; 1
      // 07d: dup
      // 07e: astore 5
      // 080: instanceof java/util/Map
      // 083: ifeq 0ad
      // 086: aload 3
      // 087: aload 5
      // 089: invokeinterface java/util/Deque.contains (Ljava/lang/Object;)Z 2
      // 08e: ifne 0ad
      // 091: aload 0
      // 092: aload 6
      // 094: ifnonnull 09d
      // 097: ldc_w "null"
      // 09a: goto 09f
      // 09d: aload 6
      // 09f: aload 5
      // 0a1: checkcast java/util/Map
      // 0a4: aload 3
      // 0a5: iload 4
      // 0a7: invokestatic org/valkyrienskies/core/impl/shadow/fv.a (Ljava/io/PrintStream;Ljava/lang/Object;Ljava/util/Map;Ljava/util/Deque;Z)V
      // 0aa: goto 05a
      // 0ad: aload 0
      // 0ae: aload 3
      // 0af: invokeinterface java/util/Deque.size ()I 1
      // 0b4: invokestatic org/valkyrienskies/core/impl/shadow/fv.a (Ljava/io/PrintStream;I)V
      // 0b7: aload 0
      // 0b8: aload 6
      // 0ba: invokevirtual java/io/PrintStream.print (Ljava/lang/Object;)V
      // 0bd: aload 0
      // 0be: ldc_w " = "
      // 0c1: invokevirtual java/io/PrintStream.print (Ljava/lang/String;)V
      // 0c4: aload 3
      // 0c5: aload 5
      // 0c7: dup
      // 0c8: astore 6
      // 0ca: invokestatic org/valkyrienskies/core/impl/shadow/gX.b (Ljava/lang/Object;)Lorg/valkyrienskies/core/impl/shadow/fF;
      // 0cd: invokestatic org/valkyrienskies/core/impl/shadow/fp.c (Ljava/lang/Iterable;Lorg/valkyrienskies/core/impl/shadow/fF;)I
      // 0d0: dup
      // 0d1: istore 6
      // 0d3: bipush -1
      // 0d4: if_icmpne 0e0
      // 0d7: aload 0
      // 0d8: aload 5
      // 0da: invokevirtual java/io/PrintStream.print (Ljava/lang/Object;)V
      // 0dd: goto 11e
      // 0e0: aload 3
      // 0e1: invokeinterface java/util/Deque.size ()I 1
      // 0e6: bipush 1
      // 0e7: isub
      // 0e8: iload 6
      // 0ea: if_icmpne 0f7
      // 0ed: aload 0
      // 0ee: ldc_w "(this Map)"
      // 0f1: invokevirtual java/io/PrintStream.print (Ljava/lang/String;)V
      // 0f4: goto 11e
      // 0f7: aload 0
      // 0f8: new java/lang/StringBuilder
      // 0fb: dup
      // 0fc: ldc_w "(ancestor["
      // 0ff: invokespecial java/lang/StringBuilder.<init> (Ljava/lang/String;)V
      // 102: aload 3
      // 103: invokeinterface java/util/Deque.size ()I 1
      // 108: bipush 1
      // 109: isub
      // 10a: iload 6
      // 10c: isub
      // 10d: bipush 1
      // 10e: isub
      // 10f: invokevirtual java/lang/StringBuilder.append (I)Ljava/lang/StringBuilder;
      // 112: ldc_w "] Map)"
      // 115: invokevirtual java/lang/StringBuilder.append (Ljava/lang/String;)Ljava/lang/StringBuilder;
      // 118: invokevirtual java/lang/StringBuilder.toString ()Ljava/lang/String;
      // 11b: invokevirtual java/io/PrintStream.print (Ljava/lang/String;)V
      // 11e: iload 4
      // 120: ifeq 13d
      // 123: aload 5
      // 125: ifnull 13d
      // 128: aload 0
      // 129: bipush 32
      // 12b: invokevirtual java/io/PrintStream.print (C)V
      // 12e: aload 0
      // 12f: aload 5
      // 131: invokevirtual java/lang/Object.getClass ()Ljava/lang/Class;
      // 134: invokevirtual java/lang/Class.getName ()Ljava/lang/String;
      // 137: invokevirtual java/io/PrintStream.println (Ljava/lang/String;)V
      // 13a: goto 05a
      // 13d: aload 0
      // 13e: invokevirtual java/io/PrintStream.println ()V
      // 141: goto 05a
      // 144: aload 3
      // 145: invokeinterface java/util/Deque.removeLast ()Ljava/lang/Object; 1
      // 14a: pop
      // 14b: aload 0
      // 14c: aload 3
      // 14d: invokeinterface java/util/Deque.size ()I 1
      // 152: invokestatic org/valkyrienskies/core/impl/shadow/fv.a (Ljava/io/PrintStream;I)V
      // 155: aload 0
      // 156: iload 4
      // 158: ifeq 175
      // 15b: new java/lang/StringBuilder
      // 15e: dup
      // 15f: ldc_w "} "
      // 162: invokespecial java/lang/StringBuilder.<init> (Ljava/lang/String;)V
      // 165: aload 2
      // 166: invokevirtual java/lang/Object.getClass ()Ljava/lang/Class;
      // 169: invokevirtual java/lang/Class.getName ()Ljava/lang/String;
      // 16c: invokevirtual java/lang/StringBuilder.append (Ljava/lang/String;)Ljava/lang/StringBuilder;
      // 16f: invokevirtual java/lang/StringBuilder.toString ()Ljava/lang/String;
      // 172: goto 178
      // 175: ldc_w "}"
      // 178: invokevirtual java/io/PrintStream.println (Ljava/lang/String;)V
      // 17b: return
   }

   private static void a(PrintStream var0, int var1) {
      for (int var2 = 0; var2 < var1; var2++) {
         var0.print("    ");
      }
   }

   private static <K, V> Map<V, K> c(Map<K, V> var0) {
      HashMap var1 = new HashMap(var0.size());

      for (Entry var2 : var0.entrySet()) {
         var1.put(var2.getValue(), var2.getKey());
      }

      return var1;
   }

   private static <K> void b(Map<? super K, Object> var0, K var1, Object var2) {
      var0.put(var1, var2 == null ? "" : var2);
   }

   private static <K, V> Map<K, V> a(Map<K, V> var0, Object[] var1) {
      if (var0 == null) {
         throw new NullPointerException("The map must not be null");
      }

      if (var1 != null && var1.length != 0) {
         Object var2;
         if ((var2 = (int)var1[0]) instanceof Entry) {
            var2 = var1;
            int var3 = var1.length;

            for (int var5 = 0; var5 < var3; var5++) {
               Object var4;
               var4 = var4 = (Entry)((Object[])var2)[var5];
               var0.put(var4.getKey(), var4.getValue());
            }
         } else if (var2 instanceof fr) {
            var2 = var1;
            int var12 = var1.length;

            for (int var6 = 0; var6 < var12; var6++) {
               Object var15;
               var15 = var15 = (fr)((Object[])var2)[var6];
               var0.put(var15.getKey(), var15.getValue());
            }
         } else if (var2 instanceof Object[]) {
            for (int var9 = 0; var9 < var1.length; var9++) {
               Object[] var13;
               if ((var13 = (Object[])var1[var9]) == null || var13.length < 2) {
                  throw new IllegalArgumentException("Invalid array element: " + var9);
               }

               var0.put(var13[0], var13[1]);
            }
         } else {
            var2 = 0;

            while (var2 < var1.length - 1) {
               var0.put(var1[var2++], var1[var2++]);
            }
         }

         return var0;
      } else {
         return var0;
      }
   }

   private static <K, V> Map<K, V> d(Map<K, V> var0) {
      return var0 == null ? Collections.emptyMap() : var0;
   }

   private static boolean e(Map<?, ?> var0) {
      return var0 == null || var0.isEmpty();
   }

   private static boolean f(Map<?, ?> var0) {
      Map var1;
      return (var1 = var0) != null && !var1.isEmpty();
   }

   private static <K, V> Map<K, V> g(Map<K, V> var0) {
      return Collections.synchronizedMap(var0);
   }

   private static <K, V> Map<K, V> h(Map<? extends K, ? extends V> var0) {
      return jG.a(var0);
   }

   private static <K, V> fn<K, V> a(Map<K, V> var0, fF<? super K> var1, fF<? super V> var2) {
      return jx.a(var0, var1, var2);
   }

   private static <K, V> fn<K, V> a(Map<K, V> var0, fQ<? super K, ? extends K> var1, fQ<? super V, ? extends V> var2) {
      return jD.a(var0, var1, var2);
   }

   private static <K, V> fn<K, V> i(Map<K, V> var0) {
      return jl.a(var0);
   }

   private static <K, V> fn<K, V> a(Map<K, V> var0, fh<? extends V> var1) {
      return jq.a(var0, var1);
   }

   private static <K, V> fn<K, V> a(Map<K, V> var0, fQ<? super K, ? extends V> var1) {
      return jq.a(var0, var1);
   }

   private static <K, V> fD<K, V> j(Map<K, V> var0) {
      return jt.a(var0);
   }

   @Deprecated
   private static <K, V> jv<K, V> k(Map<K, ? super Collection<V>> var0) {
      return jv.a(var0);
   }

   @Deprecated
   private static <K, V, C extends Collection<V>> jv<K, V> a(Map<K, C> var0, Class<C> var1) {
      return jv.a(var0, var1);
   }

   @Deprecated
   private static <K, V, C extends Collection<V>> jv<K, V> b(Map<K, C> var0, fh<C> var1) {
      return jv.a(var0, var1);
   }

   private static <K, V> SortedMap<K, V> a(SortedMap<K, V> var0) {
      return Collections.synchronizedSortedMap(var0);
   }

   private static <K, V> SortedMap<K, V> b(SortedMap<K, ? extends V> var0) {
      return jI.a(var0);
   }

   private static <K, V> SortedMap<K, V> a(SortedMap<K, V> var0, fF<? super K> var1, fF<? super V> var2) {
      return jy.a(var0, var1, var2);
   }

   private static <K, V> SortedMap<K, V> a(SortedMap<K, V> var0, fQ<? super K, ? extends K> var1, fQ<? super V, ? extends V> var2) {
      return jE.a(var0, var1, var2);
   }

   private static <K, V> SortedMap<K, V> c(SortedMap<K, V> var0) {
      return jm.a(var0);
   }

   private static <K, V> SortedMap<K, V> a(SortedMap<K, V> var0, fh<? extends V> var1) {
      return jr.a(var0, var1);
   }

   private static <K, V> SortedMap<K, V> a(SortedMap<K, V> var0, fQ<? super K, ? extends V> var1) {
      return jr.a(var0, var1);
   }

   private static <K, V> void a(Map<K, V> var0, Iterable<? extends V> var1, fQ<V, K> var2) {
      fQ var3 = hp.a();
      var2 = var2;
      var1 = var1;
      var0 = var0;

      for (Object var4 : var1) {
         var0.put(var2.a(var4), var3.a(var4));
      }
   }

   private static <K, V, E> void a(Map<K, V> var0, Iterable<? extends E> var1, fQ<E, K> var2, fQ<E, V> var3) {
      for (Object var4 : var1) {
         var0.put(var2.a(var4), var3.a(var4));
      }
   }

   private static <K, V> void a(fw<K, V> var0, Iterable<? extends V> var1, fQ<V, K> var2) {
      fQ var3 = hp.a();
      var2 = var2;
      var1 = var1;
      var0 = var0;

      for (Object var4 : var1) {
         var0.put(var2.a(var4), var3.a(var4));
      }
   }

   private static <K, V, E> void a(fw<K, V> var0, Iterable<? extends E> var1, fQ<E, K> var2, fQ<E, V> var3) {
      for (Object var4 : var1) {
         var0.put(var2.a(var4), var3.a(var4));
      }
   }

   public static <K, V> fn<K, V> a(Map<K, V> var0) {
      if (var0 == null) {
         throw new NullPointerException("Map must not be null");
      } else {
         return var0 instanceof fn ? (fn)var0 : new jd<K, V>(var0) {};
      }
   }

   private static <K, V> fo<K, V> d(SortedMap<K, V> var0) {
      if (var0 == null) {
         throw new NullPointerException("Map must not be null");
      } else {
         return var0 instanceof fo ? (fo)var0 : new jg<K, V>(var0) {};
      }
   }

   private static int l(Map<?, ?> var0) {
      return var0 == null ? 0 : var0.size();
   }

   static {
      jI.a(new TreeMap());
   }
}
