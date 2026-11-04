package org.valkyrienskies.core.impl.shadow;

import com.fasterxml.jackson.databind.JsonNode;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public enum dj implements ce, cw {
   ADDITIONAL_PROPERTIES("additionalProperties", "1001", new MessageFormat(cl.a("additionalProperties")), bP.class, dp.AllVersions),
   ALL_OF("allOf", "1002", new MessageFormat(cl.a("allOf")), bQ.class, dp.AllVersions),
   ANY_OF("anyOf", "1003", new MessageFormat(cl.a("anyOf")), bR.class, dp.AllVersions),
   CROSS_EDITS("crossEdits", "1004", new MessageFormat(cl.a("crossEdits")), null, dp.AllVersions),
   DEPENDENCIES("dependencies", "1007", new MessageFormat(cl.a("dependencies")), ca.class, dp.AllVersions),
   EDITS("edits", "1005", new MessageFormat(cl.a("edits")), null, dp.AllVersions),
   ENUM("enum", "1008", new MessageFormat(cl.a("enum")), cd.class, dp.AllVersions),
   FORMAT("format", "1009", new MessageFormat(cl.a("format")), null, dp.AllVersions) {
      @Override
      public final cv a(String var1, JsonNode var2, cp var3, df var4) {
         throw new UnsupportedOperationException("Use FormatKeyword instead");
      }
   },
   ITEMS("items", "1010", new MessageFormat(cl.a("items")), cn.class, dp.MaxV201909),
   MAXIMUM("maximum", "1011", new MessageFormat(cl.a("maximum")), cA.class, dp.AllVersions),
   MAX_ITEMS("maxItems", "1012", new MessageFormat(cl.a("maxItems")), cx.class, dp.AllVersions),
   MAX_LENGTH("maxLength", "1013", new MessageFormat(cl.a("maxLength")), cy.class, dp.AllVersions),
   MAX_PROPERTIES("maxProperties", "1014", new MessageFormat(cl.a("maxProperties")), cz.class, dp.AllVersions),
   MINIMUM("minimum", "1015", new MessageFormat(cl.a("minimum")), cE.class, dp.AllVersions),
   MIN_ITEMS("minItems", "1016", new MessageFormat(cl.a("minItems")), cB.class, dp.AllVersions),
   MIN_LENGTH("minLength", "1017", new MessageFormat(cl.a("minLength")), cC.class, dp.AllVersions),
   MIN_PROPERTIES("minProperties", "1018", new MessageFormat(cl.a("minProperties")), cD.class, dp.AllVersions),
   MULTIPLE_OF("multipleOf", "1019", new MessageFormat(cl.a("multipleOf")), cF.class, dp.AllVersions),
   NOT_ALLOWED("notAllowed", "1033", new MessageFormat(cl.a("notAllowed")), cH.class, dp.AllVersions),
   NOT("not", "1020", new MessageFormat(cl.a("not")), cI.class, dp.AllVersions),
   ONE_OF("oneOf", "1022", new MessageFormat(cl.a("oneOf")), cJ.class, dp.AllVersions),
   PATTERN_PROPERTIES("patternProperties", "1024", new MessageFormat(cl.a("patternProperties")), cL.class, dp.AllVersions),
   PATTERN("pattern", "1023", new MessageFormat(cl.a("pattern")), cM.class, dp.AllVersions),
   PREFIX_ITEMS("prefixItems", "1048", new MessageFormat(cl.a("prefixItems")), cN.class, dp.MinV202012),
   PROPERTIES("properties", "1025", new MessageFormat(cl.a("properties")), cO.class, dp.AllVersions),
   READ_ONLY("readOnly", "1032", new MessageFormat(cl.a("readOnly")), cQ.class, dp.AllVersions),
   REF("$ref", "1026", new MessageFormat(cl.a("$ref")), cR.class, dp.AllVersions),
   REQUIRED("required", "1028", new MessageFormat(cl.a("required")), cS.class, dp.AllVersions),
   TYPE("type", "1029", new MessageFormat(cl.a("type")), da.class, dp.AllVersions),
   UNION_TYPE("unionType", "1030", new MessageFormat(cl.a("unionType")), dd.class, dp.AllVersions),
   UNIQUE_ITEMS("uniqueItems", "1031", new MessageFormat(cl.a("uniqueItems")), de.class, dp.AllVersions),
   DATETIME("dateTime", "1034", new MessageFormat(cl.a("dateTime")), null, dp.AllVersions),
   UUID("uuid", "1035", new MessageFormat(cl.a("uuid")), null, dp.AllVersions),
   ID("id", "1036", new MessageFormat(cl.a("id")), null, dp.AllVersions),
   IF_THEN_ELSE("if", "1037", null, cm.class, dp.MinV7),
   EXCLUSIVE_MAXIMUM("exclusiveMaximum", "1038", new MessageFormat(cl.a("exclusiveMaximum")), cf.class, dp.MinV6),
   EXCLUSIVE_MINIMUM("exclusiveMinimum", "1039", new MessageFormat(cl.a("exclusiveMinimum")), cg.class, dp.MinV6),
   TRUE("true", "1040", null, cY.class, dp.MinV6),
   FALSE("false", "1041", new MessageFormat(cl.a("false")), ch.class, dp.MinV6),
   CONST("const", "1042", new MessageFormat(cl.a("const")), bW.class, dp.MinV6),
   CONTAINS("contains", "1043", new MessageFormat(cl.a("contains")), bX.class, dp.MinV6),
   PROPERTYNAMES("propertyNames", "1044", new MessageFormat(cl.a("propertyNames")), cP.class, dp.MinV6),
   DEPENDENT_REQUIRED("dependentRequired", "1045", new MessageFormat(cl.a("dependentRequired")), cb.class, dp.MinV201909),
   DEPENDENT_SCHEMAS("dependentSchemas", "1046", new MessageFormat(cl.a("dependentSchemas")), cc.class, dp.MinV201909),
   UNEVALUATED_PROPERTIES("unevaluatedProperties", "1047", new MessageFormat(cl.a("unevaluatedProperties")), dc.class, dp.MinV6);

   private static Map<String, dj> constants = new HashMap<>();
   private static cU specVersion = new cU();
   private final String value;
   private final String errorCode;
   private final MessageFormat messageFormat;
   private String customMessage;
   private final String errorCodeKey;
   private final Class<?> validator;
   private final long versionCode;

   dj(String var3, String var4, MessageFormat var5, Class<?> var6, dp var7) {
      this.value = var3;
      this.errorCode = var4;
      this.messageFormat = var5;
      this.errorCodeKey = var3 + "ErrorCode";
      this.validator = var6;
      this.versionCode = var7.a();
      this.customMessage = null;
   }

   public static List<dj> a(cU.a var0) {
      ArrayList var1 = new ArrayList();
      dj[] var2;
      int var3 = (var2 = values()).length;

      for (int var4 = 0; var4 < var3; var4++) {
         dj var5 = var2[var4];
         if (!FORMAT.equals(var5) && specVersion.a(var5.versionCode).contains(var0)) {
            var1.add(var5);
         }
      }

      return var1;
   }

   public static dj b(String var0) {
      dj var1;
      if ((var1 = constants.get(var0)) == null) {
         throw new IllegalArgumentException(var0);
      } else {
         return var1;
      }
   }

   @Override
   public cv a(String var1, JsonNode var2, cp var3, df var4) {
      if (this.validator == null) {
         throw new UnsupportedOperationException("No suitable validator for " + this.a());
      } else {
         return (cv)this.validator.getConstructor(String.class, JsonNode.class, cp.class, df.class).newInstance(var1 + "/" + this.a(), var2, var3, var4);
      }
   }

   @Override
   public String toString() {
      return this.value;
   }

   @Override
   public String a() {
      return this.value;
   }

   @Override
   public String b_() {
      return this.errorCode;
   }

   @Override
   public MessageFormat b() {
      return this.messageFormat;
   }

   @Override
   public void a(String var1) {
      this.customMessage = var1;
   }

   @Override
   public String c() {
      return this.customMessage;
   }

   public String d() {
      return this.errorCodeKey;
   }

   public long e() {
      return this.versionCode;
   }

   static {
      dj[] var0;
      int var1 = (var0 = values()).length;

      for (int var2 = 0; var2 < var1; var2++) {
         dj var3 = var0[var2];
         constants.put(var3.value, var3);
      }
   }
}
