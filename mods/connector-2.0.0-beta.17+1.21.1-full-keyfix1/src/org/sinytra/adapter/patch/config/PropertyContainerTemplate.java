package org.sinytra.adapter.patch.config;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

public class PropertyContainerTemplate {
   private final Set<PropertyKey<?>> keys;
   private final Set<PropertyKey<?>> pluralKeys;
   private final List<PropertyContainerTemplate.Validator> constraints;

   private PropertyContainerTemplate(Set<PropertyKey<?>> keys, Set<PropertyKey<?>> pluralKeys, List<PropertyContainerTemplate.Validator> constraints) {
      this.keys = ImmutableSet.copyOf(keys);
      this.pluralKeys = ImmutableSet.copyOf(pluralKeys);
      this.constraints = ImmutableList.copyOf(constraints);
   }

   public Set<PropertyKey<?>> getKeys() {
      return this.keys;
   }

   public Set<PropertyKey<?>> getPluralKeys() {
      return this.pluralKeys;
   }

   public boolean validate(PropertyContainer container) {
      for (PropertyContainerTemplate.Validator constraint : this.constraints) {
         if (!constraint.validate(container)) {
            return false;
         }
      }

      return true;
   }

   public PropertyContainerTemplate.Builder extend() {
      return new PropertyContainerTemplate.Builder(this.keys, this.pluralKeys, this.constraints);
   }

   public static PropertyContainerTemplate.Builder builder() {
      return new PropertyContainerTemplate.Builder();
   }

   public static class Builder {
      private final Set<PropertyKey<?>> keys;
      private final Set<PropertyKey<?>> pluralKeys;
      private final List<PropertyContainerTemplate.Validator> constraints;

      public Builder() {
         this.keys = new HashSet<>();
         this.pluralKeys = new HashSet<>();
         this.constraints = new ArrayList<>();
      }

      public Builder(Set<PropertyKey<?>> keys, Set<PropertyKey<?>> pluralKeys, List<PropertyContainerTemplate.Validator> constraints) {
         this.keys = new HashSet<>(keys);
         this.pluralKeys = new HashSet<>(pluralKeys);
         this.constraints = new ArrayList<>(constraints);
      }

      public PropertyContainerTemplate.Builder keys(PropertyKey<?>... keys) {
         this.keys.addAll(List.of(keys));
         return this;
      }

      public PropertyContainerTemplate.Builder pluralKeys(PropertyKey<?>... keys) {
         this.pluralKeys.addAll(List.of(keys));
         return this;
      }

      public PropertyContainerTemplate.Builder require(PropertyKey<?>... keys) {
         this.keys(keys);
         return this.addConstraint(c -> Stream.of(keys).allMatch(c::hasProperty));
      }

      public PropertyContainerTemplate.Builder requireOne(PropertyKey<?>... keys) {
         this.keys(keys);
         return this.addConstraint(c -> Stream.of(keys).filter(c::hasProperty).count() == 1L);
      }

      public PropertyContainerTemplate.Builder addConstraint(PropertyContainerTemplate.Validator validator) {
         this.constraints.add(validator);
         return this;
      }

      public PropertyContainerTemplate build() {
         return new PropertyContainerTemplate(this.keys, this.pluralKeys, this.constraints);
      }
   }

   public interface Validator {
      boolean validate(PropertyContainer var1);
   }
}
