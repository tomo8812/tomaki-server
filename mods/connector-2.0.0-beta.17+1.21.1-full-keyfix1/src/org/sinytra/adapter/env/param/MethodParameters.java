package org.sinytra.adapter.env.param;

import com.google.common.collect.ImmutableMap;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Predicate;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.MethodNode;
import org.sinytra.adapter.env.util.TypeConstants;

public class MethodParameters implements Copiable<MethodParameters> {
   private final Map<MethodParameters.ParamGroup, List<Parameter>> groups = new HashMap<>();
   private final List<MethodParameters.ParamGroup> order;
   private final Map<Parameter, Parameter> mapping;

   private MethodParameters(Map<MethodParameters.ParamGroup, List<Parameter>> groups, List<MethodParameters.ParamGroup> order) {
      this(groups, order, new HashMap<>());
   }

   private MethodParameters(
      Map<MethodParameters.ParamGroup, List<Parameter>> groups, List<MethodParameters.ParamGroup> order, Map<Parameter, Parameter> mapping
   ) {
      groups.forEach((k, v) -> this.groups.put(k, new ArrayList<>((Collection<? extends Parameter>)v)));
      this.order = order;
      this.mapping = mapping;
   }

   public List<MethodParameters.ParamGroup> getOrder() {
      return this.order;
   }

   public void mapParameter(Parameter old, Parameter replacement) {
      this.mapping.put(old, replacement);
   }

   public boolean has(MethodParameters.ParamGroup group) {
      return this.groups.containsKey(group);
   }

   public List<Type> getTypes(MethodParameters.ParamGroup group) {
      return this.get(group).stream().map(Parameter::type).toList();
   }

   public List<Parameter> get(MethodParameters.ParamGroup group) {
      return Objects.requireNonNull(this.groups.get(group), "Group %s is not available".formatted(group.name()));
   }

   public void add(MethodParameters.ParamGroup group, Parameter param) {
      if (this.groups.containsKey(group)) {
         this.groups.get(group).add(param);
      }
   }

   public void set(MethodParameters.ParamGroup group, Parameter param) {
      this.set(group, List.of(param));
   }

   public void setTypes(MethodParameters.ParamGroup group, List<Type> params) {
      this.set(group, params.stream().map(Parameter::simple).toList());
   }

   public void set(MethodParameters.ParamGroup group, List<Parameter> params) {
      if (!this.groups.containsKey(group)) {
         throw new IllegalArgumentException("Group %s is not available".formatted(group.name()));
      }

      this.groups.put(group, new ArrayList<>(params));
   }

   public List<Type> mergeTypes() {
      return this.merge().stream().map(Parameter::type).toList();
   }

   public List<Parameter> merge() {
      List<Parameter> result = new ArrayList<>();

      for (MethodParameters.ParamGroup group : this.order) {
         result.addAll(this.get(group));
      }

      return result;
   }

   public Map<Parameter, Parameter> getMapping() {
      return ImmutableMap.copyOf(this.mapping);
   }

   public MethodParameters copy() {
      Map<MethodParameters.ParamGroup, List<Parameter>> groups = new HashMap<>();
      this.groups.forEach((group, params) -> groups.put(group, new ArrayList<>((Collection<? extends Parameter>)params)));
      List<MethodParameters.ParamGroup> order = new ArrayList<>(this.order);
      Map<Parameter, Parameter> mapping = new HashMap<>(this.mapping);
      return new MethodParameters(groups, order, mapping);
   }

   public MethodParameters.Builder mutableCopy() {
      MethodParameters.Builder builder = builder();
      this.order.forEach(group -> builder.put(group, new ArrayList<>(this.groups.get(group))));
      return builder;
   }

   public static MethodParameters create(MethodNode method, List<MethodParameters.ParamGroup> groups) {
      List<Parameter> parameters = Parameters.parse(method);
      return create(parameters, groups);
   }

   public static MethodParameters create(List<Parameter> params, List<MethodParameters.ParamGroup> groups) {
      Map<MethodParameters.ParamGroup, List<Parameter>> results = new HashMap<>();

      for (MethodParameters.ParamGroup type : groups) {
         results.put(type, new ArrayList<>());
      }

      int groupIndex = 0;
      int paramIndex = 0;

      while (paramIndex < params.size() && groupIndex < groups.size()) {
         MethodParameters.ParamGroup group = groups.get(groupIndex);
         MethodParameters.ParamGroup nextGroup = groupIndex + 1 < groups.size() ? groups.get(groupIndex + 1) : null;
         Parameter param = params.get(paramIndex);
         List<Parameter> output = results.get(group);
         if (group.type() == MethodParameters.ParamGroupType.SINGLE) {
            if (!group.predicate().test(param)) {
               throw new IllegalStateException("Unexpected single parameter: " + param);
            }

            output.add(param);
            paramIndex++;
            groupIndex++;
         } else if (group.type() == MethodParameters.ParamGroupType.VARIABLE) {
            if (group.predicate().test(param)) {
               if (nextGroup != null && nextGroup.predicate().test(param)) {
                  if (nextGroup.type() != MethodParameters.ParamGroupType.SINGLE) {
                     throw new IllegalStateException(
                        "Ambiguous match for param %s in groups %s and %s".formatted(param.type(), group.name(), groups.get(groupIndex + 1).name())
                     );
                  }

                  groupIndex++;
               } else {
                  output.add(param);
                  paramIndex++;
               }
            } else {
               groupIndex++;
            }
         }
      }

      return new MethodParameters(results, groups);
   }

   public static MethodParameters.Builder builder() {
      return new MethodParameters.Builder();
   }

   // $VF: synthetic method
   // $VF: bridge method
   @Override
   public Object copy() {
      return this.copy();
   }

   public static class Builder {
      private final Map<MethodParameters.ParamGroup, List<Parameter>> groups = new HashMap<>();
      private final List<MethodParameters.ParamGroup> order = new ArrayList<>();

      public MethodParameters.Builder putType(MethodParameters.ParamGroup group, Type param) {
         return this.put(group, Parameter.simple(param));
      }

      public MethodParameters.Builder put(MethodParameters.ParamGroup group, Parameter param) {
         return this.put(group, List.of(param));
      }

      public MethodParameters.Builder putTypes(MethodParameters.ParamGroup group, List<Type> params) {
         return this.put(group, params.stream().map(Parameter::simple).toList());
      }

      public MethodParameters.Builder put(MethodParameters.ParamGroup group, List<Parameter> params) {
         this.groups.put(group, new ArrayList<>(params));
         if (!this.order.contains(group)) {
            this.order.add(group);
         }

         return this;
      }

      public MethodParameters build() {
         return new MethodParameters(this.groups, this.order);
      }
   }

   public record ParamGroup(MethodParameters.ParamGroupType type, String name, Predicate<Parameter> predicate) {
      public static final MethodParameters.ParamGroup METHOD_PARAMS = new MethodParameters.ParamGroup(
         MethodParameters.ParamGroupType.VARIABLE, "method_params", i -> true
      );
      public static final MethodParameters.ParamGroup CAPTURED_PARAMS = new MethodParameters.ParamGroup(
         MethodParameters.ParamGroupType.VARIABLE, "captured_params", i -> !i.isLocalOrShare()
      );
      public static final MethodParameters.ParamGroup SINGLE_ANY = new MethodParameters.ParamGroup(
         MethodParameters.ParamGroupType.SINGLE, "single_any", i -> true
      );
      public static final MethodParameters.ParamGroup CI_CIR = new MethodParameters.ParamGroup(
         MethodParameters.ParamGroupType.SINGLE, "ci_cir", i -> i.type().equals(TypeConstants.CI_TYPE) || i.type().equals(TypeConstants.CIR_TYPE)
      );
      public static final MethodParameters.ParamGroup OPERATION = new MethodParameters.ParamGroup(
         MethodParameters.ParamGroupType.SINGLE, "operation", i -> i.type().equals(TypeConstants.OPERATION_TYPE)
      );
      public static final MethodParameters.ParamGroup LOCALS = new MethodParameters.ParamGroup(
         MethodParameters.ParamGroupType.VARIABLE, "locals", Parameter::isLocalOrShare
      );
   }

   public enum ParamGroupType {
      SINGLE,
      VARIABLE;

      // $VF: synthetic method
      private static MethodParameters.ParamGroupType[] $values() {
         return new MethodParameters.ParamGroupType[]{SINGLE, VARIABLE};
      }
   }
}
