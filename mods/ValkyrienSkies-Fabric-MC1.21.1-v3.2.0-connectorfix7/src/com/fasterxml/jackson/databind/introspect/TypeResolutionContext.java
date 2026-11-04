package com.fasterxml.jackson.databind.introspect;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.type.TypeBindings;
import com.fasterxml.jackson.databind.type.TypeFactory;
import java.lang.reflect.Type;

public interface TypeResolutionContext {
   JavaType resolveType(Type var1);

   class Basic implements TypeResolutionContext {
      private final TypeFactory _typeFactory;
      private final TypeBindings _bindings;

      public Basic(TypeFactory tf, TypeBindings b) {
         this._typeFactory = tf;
         this._bindings = b;
      }

      @Override
      public JavaType resolveType(Type type) {
         return this._typeFactory.resolveMemberType(type, this._bindings);
      }
   }

   class Empty implements TypeResolutionContext {
      private final TypeFactory _typeFactory;

      public Empty(TypeFactory tf) {
         this._typeFactory = tf;
      }

      @Override
      public JavaType resolveType(Type type) {
         return this._typeFactory.constructType(type);
      }
   }
}
