package org.sinytra.adapter.patch.config;

import org.jetbrains.annotations.Nullable;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.sinytra.adapter.env.ann.AtData;
import org.sinytra.adapter.env.param.Copiable;
import org.sinytra.adapter.env.param.MethodParameters;
import org.sinytra.adapter.patch.config.key.ControlKeys;
import org.sinytra.adapter.patch.config.key.MixinKeys;
import org.sinytra.adapter.util.MethodQualifier;

public class ConfigurationImpl extends BasePropertyContainer implements MutableConfiguration {
   @Nullable
   private final Configuration parent;

   public ConfigurationImpl(@Nullable PropertyContainerTemplate template) {
      this(template, null);
   }

   public ConfigurationImpl(@Nullable PropertyContainerTemplate template, @Nullable Configuration parent) {
      super(template);
      this.parent = parent;
   }

   @Override
   public <T> MutableConfiguration setProperty(PropertyKey<T> key, @Nullable T value) {
      return (MutableConfiguration)super.setProperty(key, value);
   }

   @Override
   public <T> MutableConfiguration removeProperty(PropertyKey<T> key) {
      return (MutableConfiguration)super.removeProperty(key);
   }

   @Override
   public MutableConfiguration inheritMixinType() {
      return this.inheritProperty(ControlKeys.MIXIN_TYPE);
   }

   @Override
   public MutableConfiguration inheritTargetClass() {
      return this.inheritProperty(ControlKeys.TARGET_CLASS);
   }

   @Override
   public MutableConfiguration inheritTargetMethod() {
      return this.inheritProperty(MixinKeys.TARGET_METHOD);
   }

   @Override
   public MutableConfiguration inheritAtData() {
      return this.inheritProperty(MixinKeys.TARGET_AT);
   }

   @Override
   public MutableConfiguration inheritParameters() {
      return this.inheritProperty(ControlKeys.PARAMETERS);
   }

   @Override
   public MutableConfiguration inheritReturnType() {
      return this.inheritProperty(ControlKeys.RETURN_TYPE);
   }

   @Override
   public MutableConfiguration inheritShouldDelete() {
      return this.inheritProperty(ControlKeys.DELETE);
   }

   @Override
   public String getMixinType() {
      return this.getPropertyOrNull(ControlKeys.MIXIN_TYPE);
   }

   @Override
   public String getTargetClass() {
      return this.getPropertyOrNull(ControlKeys.TARGET_CLASS);
   }

   @Override
   public MethodQualifier getTargetMethod() {
      return this.getPropertyOrNull(MixinKeys.TARGET_METHOD);
   }

   @Override
   public AtData getAtData() {
      return this.getPropertyOrNull(MixinKeys.TARGET_AT);
   }

   @Override
   public MethodParameters getParameters() {
      return this.getPropertyOrNull(ControlKeys.PARAMETERS);
   }

   @Override
   public Type getReturnType() {
      return this.getPropertyOrNull(ControlKeys.RETURN_TYPE);
   }

   @Override
   public boolean shouldDelete() {
      Boolean boxed = this.getProperty(ControlKeys.DELETE).orElse(null);
      return boxed != null && boxed;
   }

   @Override
   public boolean isCancellable() {
      Boolean boxed = this.getProperty(MixinKeys.CANCELLABLE).orElse(null);
      return boxed != null && boxed;
   }

   @Override
   public MutableConfiguration setTargetClass(String targetClass) {
      this.setProperty(ControlKeys.TARGET_CLASS, targetClass);
      return this;
   }

   @Override
   public MutableConfiguration setMixinType(String mixinType) {
      this.setProperty(ControlKeys.MIXIN_TYPE, mixinType);
      return this;
   }

   @Override
   public MutableConfiguration setTargetMethod(MethodInsnNode insn) {
      this.setTargetMethod(MethodQualifier.create(insn));
      return this;
   }

   @Override
   public MutableConfiguration setTargetMethod(MethodNode methodNode) {
      this.setTargetMethod(MethodQualifier.create(methodNode));
      return this;
   }

   @Override
   public MutableConfiguration setTargetMethod(MethodQualifier targetMethod) {
      this.setProperty(MixinKeys.TARGET_METHOD, targetMethod);
      return this;
   }

   @Override
   public MutableConfiguration setAtData(AtData atData) {
      this.setProperty(MixinKeys.TARGET_AT, atData);
      return this;
   }

   @Override
   public MutableConfiguration setParameters(MethodParameters parameters) {
      this.setProperty(ControlKeys.PARAMETERS, parameters);
      return this;
   }

   @Override
   public MutableConfiguration setReturnType(Type returnType) {
      this.setProperty(ControlKeys.RETURN_TYPE, returnType);
      return this;
   }

   @Override
   public MutableConfiguration setShouldDelete(boolean delete) {
      this.setProperty(ControlKeys.DELETE, delete);
      return this;
   }

   @Override
   public MutableConfiguration inheritProperyIfAbsent(PropertyKey<?> key) {
      if (this.parent == null) {
         throw new IllegalStateException("Missing parent, cannot inherit property " + key);
      }

      if (!this.hasProperty(key)) {
         this.inheritProperty(key);
      }

      return this;
   }

   private ConfigurationImpl inheritProperty(PropertyKey<?> key) {
      if (this.parent != null) {
         this.parent.getProperty(key).ifPresent(o -> {
            Object entry = o instanceof Copiable c ? c.copy() : o;
            this.setProperty(key, entry);
         });
      }

      return this;
   }

   @Nullable
   private <T> T getPropertyOrNull(PropertyKey<T> key) {
      return this.getProperty(key).orElse(null);
   }

   @Override
   public MutableConfiguration copyClean() {
      return new ConfigurationImpl(this.template, this.parent);
   }

   @Override
   public MutableConfiguration copyClean(PropertyContainerTemplate template) {
      return new ConfigurationImpl(template, this.parent);
   }

   @Override
   protected MutablePropertyContainer createCopyImpl() {
      return this.copyClean();
   }

   @Override
   public MutableConfiguration copy() {
      return (MutableConfiguration)super.copy();
   }

   @Override
   public MutableConfiguration childConfig() {
      return new ConfigurationImpl(this.template, this);
   }

   @Override
   public MutableConfiguration mergeFrom(@Nullable PropertyContainer other) {
      super.mergeFrom(other);
      return this;
   }

   // $VF: synthetic method
   // $VF: bridge method
   @Override
   public MutablePropertyContainer mergeFrom(@Nullable PropertyContainer param1) {
      return this.mergeFrom(var1);
   }

   // $VF: synthetic method
   // $VF: bridge method
   @Override
   public MutablePropertyContainer copy() {
      return this.copy();
   }

   // $VF: synthetic method
   // $VF: bridge method
   @Override
   public MutablePropertyContainer removeProperty(PropertyKey param1) {
      return this.removeProperty(var1);
   }

   // $VF: synthetic method
   // $VF: bridge method
   @Override
   public MutablePropertyContainer setProperty(PropertyKey param1, @Nullable Object nullx) {
      return this.setProperty(var1, nullx);
   }
}
