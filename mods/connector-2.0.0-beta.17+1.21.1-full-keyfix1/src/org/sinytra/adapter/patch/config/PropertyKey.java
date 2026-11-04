package org.sinytra.adapter.patch.config;

import com.google.common.base.MoreObjects;
import java.util.Objects;
import org.jetbrains.annotations.Nullable;
import org.sinytra.adapter.env.ctx.RefMapper;

public class PropertyKey<T> {
   private final String name;
   @Nullable
   private final PropertyKey.Parser<T> parser;
   @Nullable
   private final PropertyKey.Serializer<T> serializer;

   public PropertyKey(String name, @Nullable PropertyKey.Parser<T> parser, @Nullable PropertyKey.Serializer<T> serializer) {
      this.name = Objects.requireNonNull(name);
      this.parser = parser;
      this.serializer = serializer;
   }

   public String name() {
      return this.name;
   }

   public PropertyKey.Parser<T> parser() {
      return this.parser;
   }

   public PropertyKey.Serializer<T> serializer() {
      return this.serializer;
   }

   public Object serialize(T value) {
      if (this.serializer == null) {
         throw new RuntimeException("Cannot serialize property '%s' as it does not define a serialized".formatted(this.name));
      } else {
         return this.serializer.serialize(value);
      }
   }

   @Override
   public String toString() {
      return MoreObjects.toStringHelper(this).add("name", this.name).toString();
   }

   public static <T> PropertyKey<T> create(String name) {
      return new PropertyKey<>(name, null, null);
   }

   public static <T> PropertyKey<T> create(String name, Class<T> type) {
      return builder(name).serializable().parseAs(type).build();
   }

   public static <T> PropertyKey.Builder<T> builder(String name) {
      return new PropertyKey.Builder<>(name);
   }

   public static class Builder<T> {
      private final String name;
      private PropertyKey.Parser<T> parser;
      private PropertyKey.Serializer<T> serializer;

      public Builder(String name) {
         this.name = name;
      }

      public PropertyKey.Builder<T> parseAs(Class<T> type) {
         return type.isEnum() ? this.parser((value, mapper) -> {
            String enumValue = ((String[])value)[1];
            return Enum.valueOf(type, enumValue);
         }).serializer(value -> {
            String name = ((Enum)value).name();
            return new String[]{type.descriptorString(), name};
         }) : this.parser((value, mapper) -> type.cast(value));
      }

      public PropertyKey.Builder<T> parser(PropertyKey.Parser<T> parser) {
         this.parser = parser;
         return this;
      }

      public PropertyKey.Builder<T> serializable() {
         this.serializer = o -> o;
         return this;
      }

      public PropertyKey.Builder<T> serializer(PropertyKey.Serializer<T> serializer) {
         this.serializer = serializer;
         return this;
      }

      public PropertyKey<T> build() {
         return new PropertyKey<>(this.name, this.parser, this.serializer);
      }
   }

   public interface Parser<T> {
      @Nullable
      T parse(Object var1, RefMapper var2);
   }

   public interface Serializer<T> {
      @Nullable
      Object serialize(T var1);
   }
}
