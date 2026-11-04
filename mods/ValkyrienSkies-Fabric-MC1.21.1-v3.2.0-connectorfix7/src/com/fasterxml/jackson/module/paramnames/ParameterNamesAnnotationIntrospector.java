package com.fasterxml.jackson.module.paramnames;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.databind.cfg.MapperConfig;
import com.fasterxml.jackson.databind.introspect.Annotated;
import com.fasterxml.jackson.databind.introspect.AnnotatedConstructor;
import com.fasterxml.jackson.databind.introspect.AnnotatedMember;
import com.fasterxml.jackson.databind.introspect.AnnotatedMethod;
import com.fasterxml.jackson.databind.introspect.AnnotatedParameter;
import com.fasterxml.jackson.databind.introspect.AnnotatedWithParams;
import com.fasterxml.jackson.databind.introspect.NopAnnotationIntrospector;
import java.lang.reflect.MalformedParametersException;
import java.lang.reflect.Parameter;

public class ParameterNamesAnnotationIntrospector extends NopAnnotationIntrospector {
   private static final long serialVersionUID = 1L;
   private final JsonCreator.Mode creatorBinding;
   private final ParameterExtractor parameterExtractor;

   ParameterNamesAnnotationIntrospector(JsonCreator.Mode creatorBinding, ParameterExtractor parameterExtractor) {
      this.creatorBinding = creatorBinding;
      this.parameterExtractor = parameterExtractor;
   }

   @Override
   public String findImplicitPropertyName(AnnotatedMember m) {
      return m instanceof AnnotatedParameter ? this.findParameterName((AnnotatedParameter)m) : null;
   }

   private String findParameterName(AnnotatedParameter annotatedParameter) {
      Parameter[] params;
      try {
         params = this.getParameters(annotatedParameter.getOwner());
      } catch (MalformedParametersException e) {
         return null;
      }

      Parameter p = params[annotatedParameter.getIndex()];
      return p.isNamePresent() ? p.getName() : null;
   }

   private Parameter[] getParameters(AnnotatedWithParams owner) {
      if (owner instanceof AnnotatedConstructor) {
         return this.parameterExtractor.getParameters(((AnnotatedConstructor)owner).getAnnotated());
      } else {
         return owner instanceof AnnotatedMethod ? this.parameterExtractor.getParameters(((AnnotatedMethod)owner).getAnnotated()) : null;
      }
   }

   @Override
   public JsonCreator.Mode findCreatorAnnotation(MapperConfig<?> config, Annotated a) {
      JsonCreator ann = this._findAnnotation(a, JsonCreator.class);
      if (ann != null) {
         JsonCreator.Mode mode = ann.mode();
         if (this.creatorBinding != null && mode == JsonCreator.Mode.DEFAULT) {
            mode = this.creatorBinding;
         }

         return mode;
      } else {
         return null;
      }
   }
}
