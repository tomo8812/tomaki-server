package org.sinytra.adapter.env.ann;

import com.google.common.base.MoreObjects;
import com.google.common.base.MoreObjects.ToStringHelper;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import org.jetbrains.annotations.Nullable;
import org.objectweb.asm.tree.AnnotationNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.sinytra.adapter.analysis.selector.AnnotationHandle;
import org.sinytra.adapter.env.ctx.RefMapper;
import org.sinytra.adapter.env.util.MixinUtil;
import org.sinytra.adapter.patch.config.MutablePropertyContainer;
import org.sinytra.adapter.patch.config.PropertyContainer;
import org.sinytra.adapter.patch.config.PropertyContainerTemplate;
import org.sinytra.adapter.patch.config.PropertyKey;
import org.sinytra.adapter.util.MethodQualifier;
import org.spongepowered.asm.mixin.injection.At.Shift;

public class AtData {
   public static final PropertyContainerTemplate TEMPLATE = PropertyContainerTemplate.builder()
      .require(AtData.Keys.VALUE)
      .keys(AtData.Keys.TARGET, AtData.Keys.ORDINAL, AtData.Keys.SHIFT, AtData.Keys.BY, AtData.Keys.ARGS)
      .addConstraint(AtData::validateInjectionPointType)
      .build();
   private final PropertyContainer properties;

   private AtData(PropertyContainer properties) {
      this.properties = properties;
   }

   public <T> Optional<T> getProperty(PropertyKey<T> key) {
      return this.properties.getProperty(key);
   }

   public String getValue() {
      return this.getProperty(AtData.Keys.VALUE).orElseThrow();
   }

   public Optional<String> getTarget() {
      return this.getProperty(AtData.Keys.TARGET);
   }

   public String getTargetOrThrow() {
      return this.getTarget().orElseThrow();
   }

   public Optional<Integer> getOrdinal() {
      return this.getProperty(AtData.Keys.ORDINAL);
   }

   public void apply(AnnotationHandle handle) {
      this.properties.apply(handle);
   }

   public AnnotationNode toAnnotationNode() {
      AnnotationNode node = new AnnotationNode("Lorg/spongepowered/asm/mixin/injection/At;");
      this.properties.getProperties().forEach((key, value) -> node.visit(key.name(), ((PropertyKey<? super Object>)key).serialize(value)));
      return node;
   }

   public AtData withValue(String value) {
      return this.withProperty(AtData.Keys.VALUE, value);
   }

   public AtData withTarget(MethodInsnNode insn) {
      return this.withTarget(MethodQualifier.create(insn));
   }

   public AtData withTarget(MethodQualifier target) {
      return this.withTarget(target.asDescriptor());
   }

   public AtData withTarget(String target) {
      return this.withProperty(AtData.Keys.TARGET, target);
   }

   public AtData withOrdinal(Integer ordinal) {
      return this.withProperty(AtData.Keys.ORDINAL, ordinal);
   }

   public <T> AtData withProperty(PropertyKey<T> key, T value) {
      MutablePropertyContainer copy = this.properties.copy();
      copy.setProperty(key, value);
      return new AtData(copy);
   }

   @Override
   public boolean equals(Object o) {
      if (o != null && this.getClass() == o.getClass()) {
         AtData atData = (AtData)o;
         return Objects.equals(this.properties, atData.properties);
      } else {
         return false;
      }
   }

   @Override
   public int hashCode() {
      return Objects.hash(this.properties);
   }

   @Override
   public String toString() {
      ToStringHelper helper = MoreObjects.toStringHelper(this);
      this.properties.getProperties().forEach((k, v) -> helper.add(k.name(), v));
      return helper.toString();
   }

   public static Optional<AtData> parse(AnnotationHandle annotation, RefMapper mapper) {
      PropertyContainer container = MutablePropertyContainer.parseValid(annotation, TEMPLATE, mapper);
      return Optional.ofNullable(container).map(AtData::new);
   }

   public static AtData create(String value) {
      return builder(value).build();
   }

   public static AtData create(String value, MethodInsnNode target) {
      return create(value, MethodQualifier.create(target).asDescriptor());
   }

   public static AtData create(String value, @Nullable String target) {
      AtData.Builder builder = builder(value);
      if (value != null) {
         builder.property(AtData.Keys.TARGET, target);
      }

      return builder.build();
   }

   public static AtData.Builder builder(String value) {
      return new AtData.Builder(value);
   }

   private static boolean validateInjectionPointType(PropertyContainer container) {
      return container.getProperty(AtData.Keys.VALUE).map(MixinUtil::isKnownInjectionPointType).orElse(true);
   }

   public static class Builder {
      private final MutablePropertyContainer properties = MutablePropertyContainer.create(AtData.TEMPLATE);

      public Builder(String value) {
         this.properties.setProperty(AtData.Keys.VALUE, value);
      }

      public AtData.Builder target(String target) {
         return this.property(AtData.Keys.TARGET, target);
      }

      public AtData.Builder ordinal(Integer ordinal) {
         return this.property(AtData.Keys.ORDINAL, ordinal);
      }

      public <T> AtData.Builder property(PropertyKey<T> key, T value) {
         this.properties.setProperty(key, value);
         return this;
      }

      public AtData build() {
         return new AtData(this.properties);
      }
   }

   public static class Keys {
      public static final PropertyKey<String> VALUE = PropertyKey.create("value", String.class);
      public static final PropertyKey<String> TARGET = PropertyKey.<String>builder("target")
         .parser((value, mapper) -> mapper.remap((String)value))
         .serializable()
         .build();
      public static final PropertyKey<Integer> ORDINAL = PropertyKey.create("ordinal", Integer.class);
      public static final PropertyKey<Shift> SHIFT = PropertyKey.create("shift", Shift.class);
      public static final PropertyKey<Integer> BY = PropertyKey.create("by", Integer.class);
      public static final PropertyKey<List<String>> ARGS = PropertyKey.create("args", List.class);
   }
}
