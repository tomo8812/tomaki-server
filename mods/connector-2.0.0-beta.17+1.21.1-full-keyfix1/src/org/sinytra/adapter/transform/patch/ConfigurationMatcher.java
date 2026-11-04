package org.sinytra.adapter.transform.patch;

import com.google.common.collect.ImmutableList;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Predicate;
import org.sinytra.adapter.patch.config.PropertyContainer;
import org.sinytra.adapter.patch.config.PropertyKey;

public class ConfigurationMatcher {
   private final List<ConfigurationMatcher.SubMatcher> matchers;

   private ConfigurationMatcher(List<ConfigurationMatcher.SubMatcher> matchers) {
      this.matchers = ImmutableList.copyOf(matchers);
   }

   public boolean match(PropertyContainer container) {
      for (ConfigurationMatcher.SubMatcher subMatcher : this.matchers) {
         if (!subMatcher.match(container)) {
            return false;
         }
      }

      return true;
   }

   public static ConfigurationMatcher.Builder builder() {
      return new ConfigurationMatcher.Builder();
   }

   public static class Builder {
      private final Map<PropertyKey<?>, Predicate<?>> singleMatchers = new HashMap<>();
      private final List<ConfigurationMatcher.SubMatcher> matchers = new ArrayList<>();

      public <T> ConfigurationMatcher.Builder match(PropertyKey<T> prop, Predicate<T> predicate) {
         this.singleMatchers.compute(prop, (k, v) -> v == null ? predicate : v.or(predicate));
         return this;
      }

      public ConfigurationMatcher.Builder match(ConfigurationMatcher.SubMatcher subMatcher) {
         this.matchers.add(subMatcher);
         return this;
      }

      public ConfigurationMatcher.Builder or(Consumer<ConfigurationMatcher.Builder> subBuilder) {
         ConfigurationMatcher.Builder sub = new ConfigurationMatcher.Builder();
         subBuilder.accept(sub);
         this.match(new ConfigurationMatcher.OrMatcher(sub.buildMatchers()));
         return this;
      }

      public List<ConfigurationMatcher.SubMatcher> buildMatchers() {
         List<ConfigurationMatcher.SubMatcher> all = new ArrayList<>();
         this.singleMatchers.forEach((key, pred) -> {
            ConfigurationMatcher.SingleMatcher matcher = new ConfigurationMatcher.SingleMatcher<>((PropertyKey<?>)key, (Predicate<?>)pred);
            all.addFirst(matcher);
         });
         all.addAll(this.matchers);
         return all;
      }

      public ConfigurationMatcher build() {
         return new ConfigurationMatcher(this.buildMatchers());
      }
   }

   private record OrMatcher(List<ConfigurationMatcher.SubMatcher> matchers) implements ConfigurationMatcher.SubMatcher {
      @Override
      public boolean match(PropertyContainer container) {
         for (ConfigurationMatcher.SubMatcher matcher : this.matchers) {
            if (matcher.match(container)) {
               return true;
            }
         }

         return false;
      }
   }

   private record SingleMatcher<T>(PropertyKey<T> key, Predicate<T> predicate) implements ConfigurationMatcher.SubMatcher {
      private SingleMatcher(PropertyKey<T> key, Predicate<T> predicate) {
         this.key = Objects.requireNonNull(key);
         this.predicate = Objects.requireNonNull(predicate);
      }

      @Override
      public boolean match(PropertyContainer container) {
         T value = container.getProperty(this.key).orElse(null);
         return value != null && this.predicate.test(value);
      }
   }

   public interface SubMatcher {
      boolean match(PropertyContainer var1);
   }
}
