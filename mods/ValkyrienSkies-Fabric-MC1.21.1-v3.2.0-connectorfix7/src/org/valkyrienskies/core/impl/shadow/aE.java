package org.valkyrienskies.core.impl.shadow;

import java.util.Collections;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public enum aE {
   SCHEMA_VERSION_INDICATOR(null, null),
   ADDITIONAL_FIXED_TYPES(bm::b, bm::a),
   SIMPLIFIED_ENUMS(bg::c, null),
   FLATTENED_ENUMS(bg::a, null, SIMPLIFIED_ENUMS),
   FLATTENED_ENUMS_FROM_TOSTRING(bg::b, null, FLATTENED_ENUMS, SIMPLIFIED_ENUMS),
   SIMPLIFIED_OPTIONALS(() -> new bn(), null),
   FLATTENED_OPTIONALS(bi::new, null, SIMPLIFIED_OPTIONALS),
   FLATTENED_SUPPLIERS(() -> new bj<>(Supplier.class), null),
   VALUES_FROM_CONSTANT_FIELDS(bf::new, null),
   PUBLIC_STATIC_FIELDS(null, null),
   PUBLIC_NONSTATIC_FIELDS(null, bh::a),
   NONPUBLIC_STATIC_FIELDS(null, null),
   NONPUBLIC_NONSTATIC_FIELDS_WITH_GETTERS(null, bh::b),
   NONPUBLIC_NONSTATIC_FIELDS_WITHOUT_GETTERS(null, bh::c),
   TRANSIENT_FIELDS(null, bh::d),
   STATIC_METHODS(null, null),
   VOID_METHODS(null, bl::a),
   GETTER_METHODS(null, bl::b),
   NONSTATIC_NONVOID_NONGETTER_METHODS(null, bl::c),
   NULLABLE_FIELDS_BY_DEFAULT(null, null),
   NULLABLE_METHOD_RETURN_VALUES_BY_DEFAULT(null, null),
   NULLABLE_ARRAY_ITEMS_ALLOWED(null, null),
   FIELDS_DERIVED_FROM_ARGUMENTFREE_METHODS(null, null),
   MAP_VALUES_AS_ADDITIONAL_PROPERTIES(be::a, null),
   ENUM_KEYWORD_FOR_SINGLE_VALUES(null, null),
   FORBIDDEN_ADDITIONAL_PROPERTIES_BY_DEFAULT(be::b, null),
   DEFINITIONS_FOR_ALL_OBJECTS(null, null),
   DEFINITION_FOR_MAIN_SCHEMA(null, null),
   INLINE_ALL_SCHEMAS(bk::new, null, DEFINITIONS_FOR_ALL_OBJECTS, DEFINITION_FOR_MAIN_SCHEMA),
   PLAIN_DEFINITION_KEYS(null, null),
   EXTRA_OPEN_API_FORMAT_VALUES(null, null),
   ALLOF_CLEANUP_AT_THE_END(null, null);

   final Supplier<aD> enabledModuleProvider;
   final Supplier<aD> disabledModuleProvider;
   final Set<aE> overriddenOptions;

   aE(Supplier<aD> var3, Supplier<aD> var4, aE... var5) {
      this.enabledModuleProvider = var3;
      this.disabledModuleProvider = var4;
      if (var5.length == 0) {
         this.overriddenOptions = Collections.emptySet();
      } else {
         this.overriddenOptions = Stream.of(var5).collect(Collectors.toSet());
      }
   }

   public final boolean a(aE var1) {
      return this.overriddenOptions.contains(var1);
   }

   final aD a(boolean var1) {
      Supplier var2;
      return (var2 = var1 ? this.enabledModuleProvider : this.disabledModuleProvider) == null ? null : (aD)var2.get();
   }
}
