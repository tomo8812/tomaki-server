package org.valkyrienskies.core.impl.shadow;

import com.fasterxml.jackson.core.json.JsonWriteFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public final class aL {
   public final ObjectMapper a;
   private final aF e;
   private final aQ f;
   private final Map<aE, Boolean> g = new HashMap<>();
   public final aN b = new aN();
   public final aM<ay> c = new aM<>();
   public final aM<aC> d = new aM<>();

   private static ObjectMapper e() {
      ObjectMapper var0;
      (var0 = new ObjectMapper()).getSerializationConfig().with(JsonWriteFeature.WRITE_NUMBERS_AS_STRINGS);
      var0.setNodeFactory(JsonNodeFactory.withExactBigDecimals(true));
      return var0;
   }

   @Deprecated
   private aL(ObjectMapper var1) {
      this(var1, aQ.DRAFT_7, aF.a);
   }

   private aL(ObjectMapper var1, aQ var2) {
      this(var1, var2, aF.a);
   }

   private aL(aQ var1) {
      this(e(), var1, aF.a);
   }

   @Deprecated
   private aL(ObjectMapper var1, aF var2) {
      this(var1, aQ.DRAFT_7, var2);
   }

   private aL(aQ var1, aF var2) {
      this(e(), var1, var2);
   }

   private aL(ObjectMapper var1, aQ var2, aF var3) {
      this.a = var1;
      this.f = var2;
      this.e = var3;
   }

   private bc f() {
      EnumSet var1 = EnumSet.allOf(aE.class);
      Set var2 = EnumSet.allOf(aE.class).stream().filter(var1x -> {
         aE var2x;
         aE var10001 = var2x = var1x;
         aF var3x = this.e;
         return this.g.getOrDefault(var10001, this.e.b.contains(var2x));
      }).collect(Collectors.toSet());
      Map var3;
      (var3 = var1.stream().filter(var1x -> var2.stream().noneMatch(var1xx -> {
         aE var2x = var1x;
         return var1xx.overriddenOptions.contains(var2x);
      })).collect(Collectors.toMap(var0 -> (aE)var0, var2::contains, (var0, var1x) -> var0, LinkedHashMap::new))).entrySet().stream().map(var0 -> {
         aE var10000 = (aE)var0.getKey();
         boolean var1x = (Boolean)var0.getValue();
         aE var2x = var10000;
         Supplier var3x;
         return (var3x = var1x ? var2x.enabledModuleProvider : var2x.disabledModuleProvider) == null ? null : (aD)var3x.get();
      }).filter(Objects::nonNull).forEach(this::a);
      var2.retainAll(var3.keySet());
      return new bc(this.a, this.f, var2, this.b, this.c, this.d);
   }

   public final aN a() {
      return this.b;
   }

   public final aM<ay> b() {
      return this.c;
   }

   public final aM<aC> c() {
      return this.d;
   }

   public final ObjectMapper d() {
      return this.a;
   }

   private Boolean a(aE var1) {
      return this.g.get(var1);
   }

   public final aL a(aD var1) {
      var1.a(this);
      return this;
   }

   @Deprecated
   private aL a(av var1) {
      this.b.a(var1);
      return this;
   }

   @Deprecated
   private aL a(aS var1) {
      aS var2 = var1;
      aN var3 = this.b;
      this.b.e.add(var2);
      return this;
   }

   private aL a(aE var1, aE... var2) {
      return this.a(var1, var2, true);
   }

   private aL b(aE var1, aE... var2) {
      return this.a(var1, var2, false);
   }

   private aL a(aE var1, aE[] var2, boolean var3) {
      this.g.put(var1, var3);
      if (var2 != null) {
         aE[] var6 = var2;
         int var7 = var2.length;

         for (int var4 = 0; var4 < var7; var4++) {
            aE var5 = var6[var4];
            this.g.put(var5, var3);
         }
      }

      return this;
   }
}
