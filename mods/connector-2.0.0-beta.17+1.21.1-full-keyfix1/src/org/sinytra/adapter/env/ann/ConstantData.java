package org.sinytra.adapter.env.ann;

import java.util.Optional;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.AnnotationNode;
import org.sinytra.adapter.analysis.selector.AnnotationHandle;
import org.sinytra.adapter.patch.config.MutablePropertyContainer;
import org.sinytra.adapter.patch.config.PropertyContainer;
import org.sinytra.adapter.patch.config.PropertyContainerTemplate;
import org.sinytra.adapter.patch.config.PropertyKey;

public class ConstantData {
   private static final PropertyContainerTemplate TEMPLATE = PropertyContainerTemplate.builder()
      .requireOne(ConstantData.Keys.DOUBLE_VALUE, ConstantData.Keys.CLASS_VALUE)
      .build();
   private final PropertyContainer properties;

   private ConstantData(PropertyContainer properties) {
      this.properties = properties;
   }

   public Optional<Double> doubleValue() {
      return this.properties.getProperty(ConstantData.Keys.DOUBLE_VALUE);
   }

   public static ConstantData classValue(Type value) {
      PropertyContainer container = MutablePropertyContainer.create().setProperty(ConstantData.Keys.CLASS_VALUE, value);
      return new ConstantData(container);
   }

   public void apply(AnnotationHandle handle) {
      this.properties.apply(handle);
   }

   public AnnotationNode toAnnotationNode() {
      AnnotationNode node = new AnnotationNode("Lorg/spongepowered/asm/mixin/injection/At;");
      this.properties.getProperties().forEach((key, value) -> node.visit(key.name(), value));
      return node;
   }

   public static Optional<ConstantData> parse(AnnotationHandle handle) {
      MutablePropertyContainer container = MutablePropertyContainer.parseValid(handle, TEMPLATE, s -> s);
      return Optional.ofNullable(container).map(ConstantData::new);
   }

   public static class Keys {
      public static final PropertyKey<Double> DOUBLE_VALUE = PropertyKey.create("doubleValue", Double.class);
      public static final PropertyKey<Type> CLASS_VALUE = PropertyKey.create("classValue", Type.class);
   }
}
