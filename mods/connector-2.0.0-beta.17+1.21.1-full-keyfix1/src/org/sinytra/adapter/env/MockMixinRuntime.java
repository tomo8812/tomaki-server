package org.sinytra.adapter.env;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.invoke.VarHandle;
import java.lang.invoke.MethodHandles.Lookup;
import java.lang.reflect.Field;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.AnnotationNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.MethodNode;
import org.sinytra.adapter.env.ctx.PatchEnvironment;
import org.sinytra.adapter.env.ctx.TargetPair;
import org.spongepowered.asm.mixin.MixinEnvironment;
import org.spongepowered.asm.mixin.MixinEnvironment.Option;
import org.spongepowered.asm.mixin.MixinEnvironment.Phase;
import org.spongepowered.asm.mixin.extensibility.IMixinConfig;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigSource;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;
import org.spongepowered.asm.mixin.injection.InjectionPoint;
import org.spongepowered.asm.mixin.injection.code.ISliceContext;
import org.spongepowered.asm.mixin.injection.code.MethodSlice;
import org.spongepowered.asm.mixin.injection.selectors.ISelectorContext;
import org.spongepowered.asm.mixin.injection.struct.CallbackInjectionInfo;
import org.spongepowered.asm.mixin.injection.struct.InjectionInfo;
import org.spongepowered.asm.mixin.injection.struct.ModifyVariableInjectionInfo;
import org.spongepowered.asm.mixin.injection.struct.Target;
import org.spongepowered.asm.mixin.refmap.IMixinContext;
import org.spongepowered.asm.mixin.refmap.IReferenceMapper;
import org.spongepowered.asm.mixin.struct.AnnotatedMethodInfo;
import org.spongepowered.asm.mixin.transformer.ClassInfo;
import org.spongepowered.asm.mixin.transformer.ext.Extensions;
import org.spongepowered.asm.util.asm.IAnnotationHandle;
import sun.misc.Unsafe;

public class MockMixinRuntime {
   private static final Lookup TRUSTED_LOOKUP;
   private static final Unsafe UNSAFE;
   private static final MethodHandle TARGET_CTR;
   private static final Class<?> LOCAL_VARIABLE_INJECTION_POINT_CLASS;
   private static final VarHandle ANNOTATED_METHOD_INFO_CONTEXT;
   private static final MethodHandle LOCAL_VARIABLE_INJECTION_POINT_FIND;

   public static Target createMixinTarget(TargetPair pair) {
      ClassInfo info = ClassInfo.forName(pair.classNode().name);

      try {
         return (Target)TARGET_CTR.invoke((ClassInfo)info, (ClassNode)pair.classNode(), (MethodNode)pair.methodNode());
      } catch (Throwable t) {
         throw new RuntimeException(t);
      }
   }

   public static IMixinContext forClass(String className, String targetClass, PatchEnvironment environment) {
      return new MockMixinRuntime.ClassMixinContext(className, targetClass, environment);
   }

   public static ISliceContext forSlice(IMixinContext context, MethodNode methodNode) {
      return new MockMixinRuntime.MethodSliceContext(context, methodNode);
   }

   public static InjectionInfo forInjectionInfo(String className, String targetClass, PatchEnvironment environment) {
      try {
         InjectionInfo injectionInfo = (InjectionInfo)UNSAFE.allocateInstance(CallbackInjectionInfo.class);
         IMixinContext context = forClass(className, targetClass, environment);
         ANNOTATED_METHOD_INFO_CONTEXT.set((InjectionInfo)injectionInfo, (IMixinContext)context);
         return injectionInfo;
      } catch (Throwable t) {
         throw new RuntimeException(t);
      }
   }

   public static boolean findModifyVariableInjectionInsns(
      InjectionPoint injectionPoint, IMixinContext mixinContext, InsnList insns, List<AbstractInsnNode> list, Target target
   ) throws Throwable {
      InjectionInfo info = createModifyVariableInjectionInfo(mixinContext);
      return (boolean)LOCAL_VARIABLE_INJECTION_POINT_FIND.invoke(
         (InjectionPoint)injectionPoint, (InjectionInfo)info, (InsnList)insns, (List)list, (Target)target
      );
   }

   public static boolean injectionPointNeedsSpecialCare(InjectionPoint injectionPoint) {
      return LOCAL_VARIABLE_INJECTION_POINT_CLASS.isInstance(injectionPoint);
   }

   public static InjectionInfo createModifyVariableInjectionInfo(IMixinContext mixinContext) throws Exception {
      ModifyVariableInjectionInfo info = (ModifyVariableInjectionInfo)UNSAFE.allocateInstance(ModifyVariableInjectionInfo.class);
      ANNOTATED_METHOD_INFO_CONTEXT.set((ModifyVariableInjectionInfo)info, (IMixinContext)mixinContext);
      return info;
   }

   static {
      try {
         Field theUnsafe = Unsafe.class.getDeclaredField("theUnsafe");
         theUnsafe.setAccessible(true);
         UNSAFE = (Unsafe)theUnsafe.get(null);
         Field hackfield = Lookup.class.getDeclaredField("IMPL_LOOKUP");
         TRUSTED_LOOKUP = (Lookup)UNSAFE.getObject(UNSAFE.staticFieldBase(hackfield), UNSAFE.staticFieldOffset(hackfield));
         TARGET_CTR = MethodHandles.privateLookupIn(Target.class, MethodHandles.lookup())
            .findConstructor(Target.class, MethodType.methodType(void.class, ClassInfo.class, ClassNode.class, MethodNode.class));
         LOCAL_VARIABLE_INJECTION_POINT_CLASS = Class.forName("org.spongepowered.asm.mixin.injection.modify.ModifyVariableInjector$LocalVariableInjectionPoint");
         ANNOTATED_METHOD_INFO_CONTEXT = TRUSTED_LOOKUP.findVarHandle(AnnotatedMethodInfo.class, "context", IMixinContext.class);
         LOCAL_VARIABLE_INJECTION_POINT_FIND = MethodHandles.privateLookupIn(LOCAL_VARIABLE_INJECTION_POINT_CLASS, MethodHandles.lookup())
            .findVirtual(
               LOCAL_VARIABLE_INJECTION_POINT_CLASS,
               "find",
               MethodType.methodType(boolean.class, InjectionInfo.class, InsnList.class, Collection.class, Target.class)
            );
      } catch (Throwable t) {
         throw new RuntimeException(t);
      }
   }

   private static final class ClassMixinContext implements IMixinContext {
      private final String className;
      private final String targetClass;
      private final MockMixinRuntime.ReferenceRemapper referenceRemapper;
      private final IMixinInfo mixinInfo;

      public ClassMixinContext(String className, String targetClass, PatchEnvironment env) {
         this.className = className;
         this.targetClass = targetClass;
         this.referenceRemapper = new MockMixinRuntime.ReferenceRemapper(env);
         this.mixinInfo = new MockMixinRuntime.DummyMixinInfo(new MockMixinRuntime.DummyMixinConfig());
      }

      public IMixinInfo getMixin() {
         return this.mixinInfo;
      }

      public Extensions getExtensions() {
         throw new UnsupportedOperationException();
      }

      public String getClassName() {
         return this.className.replace('/', '.');
      }

      public String getClassRef() {
         return this.className;
      }

      public String getTargetClassName() {
         return this.targetClass.replace('/', '.');
      }

      public String getTargetClassRef() {
         return this.targetClass;
      }

      public IReferenceMapper getReferenceMapper() {
         return this.referenceRemapper;
      }

      public boolean getOption(Option option) {
         return false;
      }

      public int getPriority() {
         return 0;
      }
   }

   private static class DummyMixinConfig implements IMixinConfig {
      public MixinEnvironment getEnvironment() {
         return MixinEnvironment.getCurrentEnvironment();
      }

      public IMixinConfigSource getSource() {
         throw new UnsupportedOperationException();
      }

      public String getCleanSourceId() {
         return "dummy";
      }

      public String getName() {
         throw new UnsupportedOperationException();
      }

      public String getMixinPackage() {
         throw new UnsupportedOperationException();
      }

      public int getPriority() {
         return 0;
      }

      public IMixinConfigPlugin getPlugin() {
         return null;
      }

      public boolean isRequired() {
         throw new UnsupportedOperationException();
      }

      public Set<String> getTargets() {
         throw new UnsupportedOperationException();
      }

      public <V> void decorate(String key, V value) {
      }

      public boolean hasDecoration(String key) {
         return false;
      }

      public <V> V getDecoration(String key) {
         return null;
      }
   }

   private record DummyMixinInfo(IMixinConfig config) implements IMixinInfo {
      public IMixinConfig getConfig() {
         return this.config;
      }

      public String getName() {
         throw new UnsupportedOperationException();
      }

      public String getClassName() {
         throw new UnsupportedOperationException();
      }

      public String getClassRef() {
         throw new UnsupportedOperationException();
      }

      public byte[] getClassBytes() {
         throw new UnsupportedOperationException();
      }

      public boolean isDetachedSuper() {
         throw new UnsupportedOperationException();
      }

      public ClassNode getClassNode(int flags) {
         throw new UnsupportedOperationException();
      }

      public List<String> getTargetClasses() {
         throw new UnsupportedOperationException();
      }

      public int getPriority() {
         throw new UnsupportedOperationException();
      }

      public Phase getPhase() {
         throw new UnsupportedOperationException();
      }
   }

   private record MethodSliceContext(IMixinContext context, MethodNode methodNode) implements ISliceContext {
      public IMixinContext getMixin() {
         return this.context;
      }

      public String remap(String reference) {
         return this.context.getReferenceMapper().remap(this.context.getClassName(), reference);
      }

      public String getElementDescription() {
         return "dummy";
      }

      public MethodSlice getSlice(String id) {
         throw new UnsupportedOperationException();
      }

      public MethodNode getMethod() {
         return this.methodNode;
      }

      public AnnotationNode getAnnotationNode() {
         throw new UnsupportedOperationException();
      }

      public ISelectorContext getParent() {
         throw new UnsupportedOperationException();
      }

      public IAnnotationHandle getAnnotation() {
         throw new UnsupportedOperationException();
      }

      public IAnnotationHandle getSelectorAnnotation() {
         throw new UnsupportedOperationException();
      }

      public String getSelectorCoordinate(boolean leaf) {
         throw new UnsupportedOperationException();
      }

      public void addMessage(String format, Object... args) {
      }

      // $VF: synthetic method
      // $VF: bridge method
      public Object getMethod() {
         return this.getMethod();
      }
   }

   private record ReferenceRemapper(PatchEnvironment env) implements IReferenceMapper {
      public String remapWithContext(String context, String className, String reference) {
         return this.env.refmapHolder().remap(className, reference);
      }

      public boolean isDefault() {
         return false;
      }

      public String getResourceName() {
         return null;
      }

      public String getStatus() {
         return null;
      }

      public String getContext() {
         return null;
      }

      public void setContext(String context) {
      }

      public String remap(String className, String reference) {
         return this.remapWithContext(null, className, reference);
      }
   }
}
