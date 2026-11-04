package dev.emi.emi.mixinsupport;

import com.google.common.collect.Maps;
import dev.emi.emi.mixinsupport.annotation.AdditionalField;
import dev.emi.emi.mixinsupport.annotation.Extends;
import dev.emi.emi.mixinsupport.annotation.InvokeTarget;
import dev.emi.emi.mixinsupport.annotation.StripConstructors;
import java.lang.annotation.Annotation;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.jetbrains.annotations.Nullable;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.AnnotationNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.FieldNode;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.TypeInsnNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;
import org.spongepowered.asm.util.Annotations;

public class EmiMixinPlugin implements IMixinConfigPlugin {
   private static final String MIXIN_PLACEHOLDER = MixinPlaceholder.class.getName().replace(".", "/");

   public void onLoad(String mixinPackage) {
   }

   public String getRefMapperConfig() {
      return null;
   }

   public boolean shouldApplyMixin(String targetClassName, String mixinClassName) {
      return true;
   }

   public void acceptTargets(Set<String> myTargets, Set<String> otherTargets) {
   }

   public List<String> getMixins() {
      return null;
   }

   public void preApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {
   }

   public void postApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {
      this.processClassAnnotations(targetClassName, targetClass, mixinClassName, mixinInfo);
      EmiMixinTransformation.relinkTransforms(targetClass);
      this.processMethodAnnotations(targetClassName, targetClass, mixinClassName, mixinInfo);
      EmiMixinTransformation.exposeSinners(targetClassName, targetClass, mixinClassName);
   }

   private void processClassAnnotations(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {
      EmiMixinTransformation.applyTransform(targetClass);
      AnnotationNode extendsAnnot = popInvisible(targetClass, Extends.class);
      if (extendsAnnot != null) {
         targetClass.superName = (String)Annotations.getValue(extendsAnnot, "value", targetClass.superName);
      }

      AnnotationNode stripConstructors = popInvisible(targetClass, StripConstructors.class);
      if (stripConstructors != null) {
         for (int i = 0; i < targetClass.methods.size(); i++) {
            if (((MethodNode)targetClass.methods.get(i)).name.equals("<init>")) {
               targetClass.methods.remove(i--);
            }
         }
      }
   }

   private void processMethodAnnotations(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {
      String thisOwner = targetClassName.replace(".", "/");
      ClassNode mixinClass = mixinInfo.getClassNode(0);
      Map<String, EmiMixinPlugin.InvokeTargetInfo> targets = Maps.newHashMap();

      for (MethodNode method : mixinClass.methods) {
         AnnotationNode invokeTarget = popInvisible(method, InvokeTarget.class);
         if (invokeTarget != null) {
            String owner = (String)Annotations.getValue(invokeTarget, "owner", targetClassName);

            owner = switch (owner) {
               case "this" -> thisOwner;
               case "super" -> targetClass.superName;
               default -> owner;
            };
            String name = (String)Annotations.getValue(invokeTarget, "name", method.name);
            String desc = (String)Annotations.getValue(invokeTarget, "desc", method.desc);

            int type = switch ((String)Annotations.getValue(invokeTarget, "type", "")) {
               case "VIRTUAL" -> 182;
               case "SPECIAL" -> 183;
               case "STATIC" -> 184;
               case "INTERFACE" -> 185;
               case "NEW" -> 187;
               default -> -1;
            };
            targets.put(method.name + method.desc, new EmiMixinPlugin.InvokeTargetInfo(owner, name, type, desc));
         }

         AnnotationNode additionalField = popInvisible(method, AdditionalField.class);
         if (additionalField != null) {
            String value = (String)Annotations.getValue(additionalField, "value", "");
            String ret = method.desc.split("\\)")[1];
            targetClass.fields.add(new FieldNode(25, value, ret, null, null));
            MethodNode clinit = null;

            for (MethodNode mn : targetClass.methods) {
               if ("<clinit>".equals(mn.name)) {
                  clinit = mn;
               }
            }

            if (clinit == null) {
               clinit = new MethodNode(8, "<clinit>", "()V", null, null);
               clinit.instructions.add(new LabelNode());
               clinit.instructions.add(new InsnNode(177));
               targetClass.methods.add(clinit);
            }

            clinit.instructions
               .insertBefore(
                  clinit.instructions.getLast(),
                  new FieldInsnNode(
                     178, (String)Annotations.getValue(additionalField, "owner", ""), (String)Annotations.getValue(additionalField, "name", ""), ret
                  )
               );
            clinit.instructions.insertBefore(clinit.instructions.getLast(), new FieldInsnNode(179, thisOwner, value, ret));
         }
      }

      for (int i = 0; i < targetClass.methods.size(); i++) {
         MethodNode method = (MethodNode)targetClass.methods.get(i);
         if (popInvisible(method, InvokeTarget.class) != null || popInvisible(method, AdditionalField.class) != null) {
            targetClass.methods.remove(i--);
         }
      }

      for (MethodNode method : targetClass.methods) {
         FieldInsnNode lastNewDup = null;

         for (int i = 0; i < method.instructions.size(); i++) {
            AbstractInsnNode node = method.instructions.get(i);
            if (node instanceof MethodInsnNode min && thisOwner.equals(min.owner)) {
               String desc = min.name + min.desc;
               if (targets.containsKey(desc)) {
                  EmiMixinPlugin.InvokeTargetInfo info = targets.get(desc);
                  int type = info.type;
                  if (type == -1) {
                     if (info.name.equals("<init>")) {
                        type = 183;
                     } else {
                        type = min.getOpcode();
                     }
                  }

                  if (type == 187) {
                     if (lastNewDup != null) {
                        method.instructions.insertBefore(lastNewDup, new TypeInsnNode(187, info.owner));
                        method.instructions.insertBefore(lastNewDup, new InsnNode(89));
                        method.instructions.remove(lastNewDup);
                        lastNewDup = null;
                        i += 2;
                     }

                     type = 183;
                  }

                  method.instructions.set(min, new MethodInsnNode(type, info.owner, info.name, info.desc));
                  if (info.name.equals("<init>")) {
                  }
               }
            } else if (node instanceof FieldInsnNode field && MIXIN_PLACEHOLDER.equals(field.owner) && field.name.equals("NEW_DUP")) {
               lastNewDup = field;
            }
         }
      }
   }

   @Nullable
   public static AnnotationNode popInvisible(ClassNode node, Class<? extends Annotation> type) {
      return pop(node.invisibleAnnotations, type);
   }

   @Nullable
   public static AnnotationNode popInvisible(MethodNode node, Class<? extends Annotation> type) {
      return pop(node.invisibleAnnotations, type);
   }

   @Nullable
   public static AnnotationNode popInvisible(FieldNode node, Class<? extends Annotation> type) {
      return pop(node.invisibleAnnotations, type);
   }

   @Nullable
   private static AnnotationNode pop(List<AnnotationNode> anns, Class<? extends Annotation> type) {
      if (anns == null) {
         return null;
      }

      String desc = Type.getDescriptor(type);
      Iterator<AnnotationNode> iter = anns.iterator();

      while (iter.hasNext()) {
         AnnotationNode an = iter.next();
         if (desc.equals(an.desc)) {
            iter.remove();
            return an;
         }
      }

      return null;
   }

   private record InvokeTargetInfo(String owner, String name, int type, String desc) {
   }
}
