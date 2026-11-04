package org.sinytra.adapter.util;

import com.google.common.base.Strings;
import com.mojang.datafixers.util.Pair;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.OptionalInt;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;
import org.objectweb.asm.Type;

public class GeneratedVariables {
   private static final String MOJMAP_PARAM_NAME_PREFIX = "p_";
   private static final String OBF_VAR_PATTERN = "^\\$\\$\\d+$";
   private static final Map<Type, GeneratedVariables.GeneratedVarName> GENERATED_VAR_NAMES = new HashMap<>();

   public static boolean isGeneratedVariableName(String name, Type type) {
      if (!name.startsWith("p_") && !name.matches("^\\$\\$\\d+$")) {
         GeneratedVariables.GeneratedVarName generator = GENERATED_VAR_NAMES.get(type);
         boolean knownGenerated = generator != null && generator.test(name);
         if (!knownGenerated && type.getSort() == 10 && !name.equals("this")) {
            String internalName = type.getInternalName();
            int index = internalName.lastIndexOf(47);
            String shortName = internalName.substring(index + 1).toLowerCase(Locale.ROOT);
            String pattern = "^(\\Q%s\\E)\\d*$".formatted(shortName);
            return name.matches(pattern);
         } else {
            return knownGenerated;
         }
      } else {
         return true;
      }
   }

   public static OptionalInt getGeneratedVariableOrdinal(String name, Type type) {
      GeneratedVariables.GeneratedVarName generator = GENERATED_VAR_NAMES.get(type);
      return generator != null ? generator.getOrdinal(name) : OptionalInt.empty();
   }

   static {
      Stream.of(
            new GeneratedVariables.GeneratedVarName(Set.of(Type.INT_TYPE, Type.LONG_TYPE), Set.of("i", "j", "k", "l")),
            new GeneratedVariables.GeneratedVarName(Type.BYTE_TYPE, "b"),
            new GeneratedVariables.GeneratedVarName(Type.CHAR_TYPE, "c"),
            new GeneratedVariables.GeneratedVarName(Type.SHORT_TYPE, "short"),
            new GeneratedVariables.GeneratedVarName(Set.of(Type.BOOLEAN_TYPE), Set.of("flag", "bl")),
            new GeneratedVariables.GeneratedVarName(Type.DOUBLE_TYPE, "d"),
            new GeneratedVariables.GeneratedVarName(Type.FLOAT_TYPE, "f"),
            new GeneratedVariables.GeneratedVarName(Type.getObjectType("java/io/File"), "file"),
            new GeneratedVariables.GeneratedVarName(Type.getObjectType("java/lang/String"), "s"),
            new GeneratedVariables.GeneratedVarName(Type.getObjectType("java/lang/Class"), "oclass"),
            new GeneratedVariables.GeneratedVarName(Type.getObjectType("java/lang/Long"), "olong"),
            new GeneratedVariables.GeneratedVarName(Type.getObjectType("java/lang/Byte"), "obyte"),
            new GeneratedVariables.GeneratedVarName(Type.getObjectType("java/lang/Short"), "oshort"),
            new GeneratedVariables.GeneratedVarName(Type.getObjectType("java/lang/Boolean"), "obool"),
            new GeneratedVariables.GeneratedVarName(Type.getObjectType("java/lang/Package"), "opackage"),
            new GeneratedVariables.GeneratedVarName(Type.getObjectType("java/lang/Enum"), "oenum")
         )
         .flatMap(generatedVarName -> generatedVarName.getTypes().stream().map(type -> Pair.of(type, generatedVarName)))
         .forEach(pair -> {
            if (GENERATED_VAR_NAMES.put((Type)pair.getFirst(), (GeneratedVariables.GeneratedVarName)pair.getSecond()) != null) {
               throw new IllegalArgumentException("Duplicate generator for type " + ((Type)pair.getFirst()).getDescriptor());
            }
         });
   }

   public static class GeneratedVarName {
      private final Set<Type> types;
      private final Pattern pattern;

      public GeneratedVarName(Type type, String prefix) {
         this(Set.of(type), Set.of(prefix));
      }

      public GeneratedVarName(Set<Type> types, Set<String> prefixes) {
         this.types = types;
         String matchingPrefixes = String.join("|", prefixes);
         String patternString = "^(%s)(?<index>\\d*)$".formatted(matchingPrefixes);
         this.pattern = Pattern.compile(patternString);
      }

      public Set<Type> getTypes() {
         return this.types;
      }

      public boolean test(String str) {
         return this.pattern.matcher(str).matches();
      }

      public OptionalInt getOrdinal(String str) {
         Matcher matcher = this.pattern.matcher(str);
         if (matcher.matches()) {
            String ordinal = matcher.group("index");
            if (Strings.isNullOrEmpty(ordinal)) {
               return OptionalInt.of(0);
            }

            int intOrdinal = Integer.parseInt(ordinal);
            return OptionalInt.of(intOrdinal);
         } else {
            return OptionalInt.empty();
         }
      }
   }
}
