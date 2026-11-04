package org.sinytra.adapter.patch;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import org.jetbrains.annotations.Nullable;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.AnnotationNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;
import org.sinytra.adapter.analysis.selector.AnnotationHandle;
import org.sinytra.adapter.env.ann.ClassTarget;
import org.sinytra.adapter.env.ctx.MethodHelper;
import org.sinytra.adapter.env.ctx.PatchEnvironment;
import org.sinytra.adapter.env.ctx.RefMapper;
import org.sinytra.adapter.patch.config.MutablePropertyContainer;
import org.sinytra.adapter.patch.config.PropertyContainer;
import org.sinytra.adapter.patch.config.PropertyContainerTemplate;
import org.sinytra.adapter.patch.config.key.ControlKeys;
import org.sinytra.adapter.patch.config.key.SpecialKeys;
import org.sinytra.adapter.patch.mixin.MixinType;
import org.sinytra.adapter.patch.mixin.MixinTypes;

public class MixinParser {
   public static ClassTarget prepareMixinClass(ClassNode classNode, PatchEnvironment environment) {
      RefMapper mapper = mapperFor(classNode, environment);
      return parseTargetClass(classNode, mapper);
   }

   @Nullable
   public static MixinParser.MixinClassHandle parseMixins(ClassTarget cls, ClassNode classNode, PatchEnvironment environment) {
      RefMapper mapper = mapperFor(classNode, environment);
      List<MixinParser.MixinMethodHandle> methods = new ArrayList<>();

      for (MethodNode method : classNode.methods) {
         MixinParser.MixinMethodHandle handle = parseMixin(cls, method, mapper);
         if (handle != null) {
            methods.add(handle);
         }
      }

      return new MixinParser.MixinClassHandle(cls, methods);
   }

   public static MixinParser.MixinMethodHandle parseMixin(ClassTarget cls, MethodNode method, RefMapper mapper) {
      if (method.visibleAnnotations == null) {
         return null;
      }

      for (AnnotationNode annotation : method.visibleAnnotations) {
         AnnotationHandle handle = new AnnotationHandle(annotation);
         String internalName = Type.getType(annotation.desc).getInternalName();
         MixinType mixinType = MixinTypes.getMixinType(internalName);
         if (mixinType != null) {
            PropertyContainerTemplate template = mixinType.getConfigurationTemplate();
            MutablePropertyContainer properties = MutablePropertyContainer.parse(handle, template, mapper);
            properties.setProperty(ControlKeys.MIXIN_TYPE, annotation.desc);
            String targetClass = Objects.requireNonNullElseGet(cls.getSingle(), () -> cls.getTypes().getFirst()).getInternalName();
            properties.setProperty(ControlKeys.TARGET_CLASS, targetClass);
            properties.setProperty(ControlKeys.RETURN_TYPE, Type.getReturnType(method.desc));
            properties.setProperty(SpecialKeys.STATIC, MethodHelper.isStatic(method));
            return new MixinParser.MixinMethodHandle(mixinType, method, handle, properties);
         }
      }

      return null;
   }

   @Nullable
   private static ClassTarget parseTargetClass(ClassNode classNode, RefMapper mapper) {
      if (classNode.invisibleAnnotations == null) {
         return null;
      }

      for (AnnotationNode annotation : classNode.invisibleAnnotations) {
         if (annotation.desc.equals("Lorg/spongepowered/asm/mixin/Mixin;")) {
            AnnotationHandle ann = new AnnotationHandle(annotation);
            return ClassTarget.parse(ann, mapper);
         }
      }

      return null;
   }

   private static RefMapper mapperFor(ClassNode classNode, PatchEnvironment environment) {
      return ref -> environment.refmapHolder().remap(classNode.name, ref);
   }

   public record MixinClassHandle(ClassTarget classTarget, List<MixinParser.MixinMethodHandle> mixins) {
   }

   public record MixinMethodHandle(MixinType mixinType, MethodNode methodNode, AnnotationHandle methodAnnotation, PropertyContainer properties) {
   }
}
