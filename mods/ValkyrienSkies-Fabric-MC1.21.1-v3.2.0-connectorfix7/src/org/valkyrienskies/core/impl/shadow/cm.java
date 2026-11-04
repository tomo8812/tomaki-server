package org.valkyrienskies.core.impl.shadow;

import com.fasterxml.jackson.databind.JsonNode;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class cm extends bT implements cv {
   private static final Logger g = LoggerFactory.getLogger(cm.class);
   private static final ArrayList<String> i = new ArrayList<>(Arrays.asList("if", "then", "else"));
   private final cp j;
   private final cp k;
   private final cp l;

   public cm(String var1, JsonNode var2, cp var3, df var4) {
      super(var1, var2, var3, dj.IF_THEN_ELSE, var4);
      cp var10 = null;
      cp var5 = null;
      cp var6 = null;

      for (String var8 : i) {
         JsonNode var9 = var2.get(var8);
         if (var8.equals("if")) {
            var10 = new cp(var4, this.e().a(), var3.i(), var9, var3);
         } else if (var8.equals("then") && var9 != null) {
            var5 = new cp(var4, this.e().a(), var3.i(), var9, var3);
         } else if (var8.equals("else") && var9 != null) {
            var6 = new cp(var4, this.e().a(), var3.i(), var9, var3);
         }
      }

      this.j = var10;
      this.k = var5;
      this.l = var6;
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   @Override
   public Set<dg> a(JsonNode var1, JsonNode var2, String var3) {
      this.a(g, (JsonNode)var1, var2, var3);
      Object var4 = bV.a().a("com.networknt.schema.UnEvaluatedPropertiesValidator.EvaluatedProperties");
      Object var5 = null;
      Object var6 = null;
      Object var7 = null;
      bV.a().a("com.networknt.schema.UnEvaluatedPropertiesValidator.EvaluatedProperties", new ArrayList());
      LinkedHashSet var8 = new LinkedHashSet();
      boolean var9 = false;
      boolean var12 = false /* VF: Semaphore variable */;

      try {
         try {
            var12 = true;
            var9 = this.j.a(var1, var2, var3).isEmpty();
         } catch (cq var13) {
            var9 = false;
         }

         var5 = bV.a().a("com.networknt.schema.UnEvaluatedPropertiesValidator.EvaluatedProperties");
         if (var9 && this.k != null) {
            bV.a().a("com.networknt.schema.UnEvaluatedPropertiesValidator.EvaluatedProperties", new ArrayList());
            var8.addAll(this.k.a(var1, var2, var3));
            var6 = bV.a().a("com.networknt.schema.UnEvaluatedPropertiesValidator.EvaluatedProperties");
            var12 = false;
         } else if (!var9) {
            if (this.l != null) {
               bV.a().a("com.networknt.schema.UnEvaluatedPropertiesValidator.EvaluatedProperties", new ArrayList());
               var8.addAll(this.l.a(var1, var2, var3));
               var7 = bV.a().a("com.networknt.schema.UnEvaluatedPropertiesValidator.EvaluatedProperties");
               var12 = false;
            } else {
               var12 = false;
            }
         } else {
            var12 = false;
         }
      } finally {
         if (var12) {
            if (var8.isEmpty()) {
               List var16 = var4 == null ? new ArrayList() : (List)var4;
               if (var5 != null && var9) {
                  var16.addAll((List)var5);
               }

               if (var6 != null) {
                  var16.addAll((List)var6);
               }

               bV.a().a("com.networknt.schema.UnEvaluatedPropertiesValidator.EvaluatedProperties", var16);
            } else {
               bV.a().a("com.networknt.schema.UnEvaluatedPropertiesValidator.EvaluatedProperties", var4);
            }
         }
      }

      if (var8.isEmpty()) {
         List var15 = var4 == null ? new ArrayList() : (List)var4;
         if (var5 != null && var9) {
            var15.addAll((List)var5);
         }

         if (var6 != null) {
            var15.addAll((List)var6);
         }

         if (var7 != null) {
            var15.addAll((List)var7);
         }

         bV.a().a("com.networknt.schema.UnEvaluatedPropertiesValidator.EvaluatedProperties", var15);
      } else {
         bV.a().a("com.networknt.schema.UnEvaluatedPropertiesValidator.EvaluatedProperties", var4);
      }

      return Collections.unmodifiableSet(var8);
   }

   @Override
   public void a_() {
      if (this.j != null) {
         this.j.o();
      }

      if (this.k != null) {
         this.k.o();
      }

      if (this.l != null) {
         this.l.o();
      }
   }
}
