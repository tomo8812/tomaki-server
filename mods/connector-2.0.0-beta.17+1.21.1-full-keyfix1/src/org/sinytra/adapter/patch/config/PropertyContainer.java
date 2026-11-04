package org.sinytra.adapter.patch.config;

import java.util.Map;
import java.util.Optional;
import org.sinytra.adapter.analysis.selector.AnnotationHandle;

public interface PropertyContainer {
   boolean hasProperty(PropertyKey<?> var1);

   <T> Optional<T> getProperty(PropertyKey<T> var1);

   Map<PropertyKey<?>, Object> getProperties();

   MutablePropertyContainer copy();

   boolean validate();

   void apply(AnnotationHandle var1);
}
