package org.valkyrienskies.core.impl.shadow;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public final class aF {
   public static final aF a = new aF(
      aE.VALUES_FROM_CONSTANT_FIELDS,
      aE.PUBLIC_STATIC_FIELDS,
      aE.PUBLIC_NONSTATIC_FIELDS,
      aE.NONPUBLIC_STATIC_FIELDS,
      aE.NONPUBLIC_NONSTATIC_FIELDS_WITH_GETTERS,
      aE.NONPUBLIC_NONSTATIC_FIELDS_WITHOUT_GETTERS,
      aE.TRANSIENT_FIELDS,
      aE.STATIC_METHODS,
      aE.VOID_METHODS,
      aE.GETTER_METHODS,
      aE.NONSTATIC_NONVOID_NONGETTER_METHODS,
      aE.SIMPLIFIED_ENUMS,
      aE.SIMPLIFIED_OPTIONALS,
      aE.DEFINITIONS_FOR_ALL_OBJECTS,
      aE.NULLABLE_FIELDS_BY_DEFAULT,
      aE.NULLABLE_METHOD_RETURN_VALUES_BY_DEFAULT,
      aE.ALLOF_CLEANUP_AT_THE_END
   );
   private static aF c;
   private static aF d;
   final Set<aE> b;

   private aF(aE... var1) {
      this.b = new HashSet<>(Arrays.asList(var1));
   }

   public final boolean a(aE var1) {
      return this.b.contains(var1);
   }

   static {
      new aF(
         aE.SCHEMA_VERSION_INDICATOR,
         aE.ADDITIONAL_FIXED_TYPES,
         aE.FLATTENED_ENUMS,
         aE.FLATTENED_OPTIONALS,
         aE.FLATTENED_SUPPLIERS,
         aE.VALUES_FROM_CONSTANT_FIELDS,
         aE.PUBLIC_NONSTATIC_FIELDS,
         aE.NONPUBLIC_NONSTATIC_FIELDS_WITH_GETTERS,
         aE.NONPUBLIC_NONSTATIC_FIELDS_WITHOUT_GETTERS,
         aE.ALLOF_CLEANUP_AT_THE_END
      );
      new aF(
         aE.VALUES_FROM_CONSTANT_FIELDS,
         aE.PUBLIC_STATIC_FIELDS,
         aE.PUBLIC_NONSTATIC_FIELDS,
         aE.STATIC_METHODS,
         aE.VOID_METHODS,
         aE.GETTER_METHODS,
         aE.NONSTATIC_NONVOID_NONGETTER_METHODS,
         aE.SIMPLIFIED_ENUMS,
         aE.SIMPLIFIED_OPTIONALS,
         aE.ALLOF_CLEANUP_AT_THE_END
      );
   }
}
