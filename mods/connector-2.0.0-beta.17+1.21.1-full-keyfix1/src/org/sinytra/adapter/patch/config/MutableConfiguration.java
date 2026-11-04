package org.sinytra.adapter.patch.config;

import org.jetbrains.annotations.Nullable;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.sinytra.adapter.env.ann.AtData;
import org.sinytra.adapter.env.param.MethodParameters;
import org.sinytra.adapter.util.MethodQualifier;

public interface MutableConfiguration extends Configuration, MutablePropertyContainer {
   static MutableConfiguration create() {
      return create(null);
   }

   static MutableConfiguration create(@Nullable PropertyContainerTemplate template) {
      return new ConfigurationImpl(template);
   }

   MutableConfiguration inheritMixinType();

   MutableConfiguration setMixinType(String var1);

   MutableConfiguration inheritTargetClass();

   default MutableConfiguration setTargetClass(ClassNode targetClass) {
      return this.setTargetClass(targetClass.name);
   }

   MutableConfiguration setTargetClass(String var1);

   MutableConfiguration inheritTargetMethod();

   MutableConfiguration setTargetMethod(MethodInsnNode var1);

   MutableConfiguration setTargetMethod(MethodQualifier var1);

   MutableConfiguration setTargetMethod(MethodNode var1);

   MutableConfiguration inheritAtData();

   MutableConfiguration setAtData(AtData var1);

   MutableConfiguration inheritParameters();

   MutableConfiguration setParameters(MethodParameters var1);

   MutableConfiguration inheritReturnType();

   MutableConfiguration setReturnType(Type var1);

   MutableConfiguration inheritShouldDelete();

   MutableConfiguration setShouldDelete(boolean var1);

   <T> MutableConfiguration setProperty(PropertyKey<T> var1, @Nullable T var2);

   <T> MutableConfiguration removeProperty(PropertyKey<T> var1);

   MutableConfiguration mergeFrom(@Nullable PropertyContainer var1);

   MutableConfiguration inheritProperyIfAbsent(PropertyKey<?> var1);

   // $VF: synthetic method
   // $VF: bridge method
   @Override
   default MutablePropertyContainer mergeFrom(@Nullable PropertyContainer param1) {
      return this.mergeFrom(var1);
   }

   // $VF: synthetic method
   // $VF: bridge method
   @Override
   default MutablePropertyContainer removeProperty(PropertyKey param1) {
      return this.removeProperty(var1);
   }

   // $VF: synthetic method
   // $VF: bridge method
   @Override
   default MutablePropertyContainer setProperty(PropertyKey param1, @Nullable Object nullx) {
      return this.setProperty(var1, nullx);
   }
}
