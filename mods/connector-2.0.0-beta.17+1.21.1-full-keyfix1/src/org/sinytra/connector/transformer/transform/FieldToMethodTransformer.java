package org.sinytra.connector.transformer.transform;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;
import com.mojang.logging.LogUtils;
import java.util.Collection;
import java.util.ListIterator;
import java.util.Map;
import java.util.Objects;
import java.util.Map.Entry;
import org.jetbrains.annotations.Nullable;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.sinytra.adapter.env.ctx.PatchResult;
import org.sinytra.connector.transformer.patch.ClassNodeTransformer;
import org.slf4j.Logger;
import reloc.net.fabricmc.classtweaker.api.ClassTweakerReader;
import reloc.net.fabricmc.classtweaker.api.ClassTweakerWriter;
import reloc.net.fabricmc.classtweaker.api.visitor.AccessWidenerVisitor;
import reloc.net.fabricmc.classtweaker.api.visitor.ClassTweakerVisitor;
import reloc.net.fabricmc.classtweaker.visitors.ForwardingVisitor;
import reloc.net.minecraftforge.fart.api.Transformer;
import reloc.net.minecraftforge.srgutils.IMappingFile;

public class FieldToMethodTransformer implements ClassNodeTransformer.ClassProcessor {
   public static final Map<String, Map<String, String>> REPLACEMENTS = ImmutableMap.builder()
      .put("net.minecraft.world.level.biome.Biome", Map.of("climateSettings", "getModifiedClimateSettings", "specialEffects", "getModifiedSpecialEffects"))
      .put("net.minecraft.world.level.levelgen.structure.Structure", Map.of("settings", "getModifiedStructureSettings"))
      .put("net.minecraft.world.level.block.FlowerPotBlock", Map.of("potted", "getPotted"))
      .buildOrThrow();
   private static final Logger LOGGER = LogUtils.getLogger();
   private final String accessWidenerResource;
   private final Map<String, String> mappedReplacements;

   public FieldToMethodTransformer(String accessWidenerResource, IMappingFile mappings) {
      this.accessWidenerResource = accessWidenerResource;
      Builder<String, String> builder = ImmutableMap.builder();
      REPLACEMENTS.forEach((cls, replacements) -> {
         IMappingFile.IClass classMap = Objects.requireNonNull(mappings.getClass(cls.replace('.', '/')));
         replacements.forEach((field, getter) -> builder.put(classMap.remapField(field), getter));
      });
      this.mappedReplacements = builder.build();
   }

   @Override
   public PatchResult process(ClassNode node) {
      return this.processClass(node) ? PatchResult.APPLY : PatchResult.PASS;
   }

   @Override
   public Transformer.ResourceEntry process(Transformer.ResourceEntry entry) {
      if (entry.getName().equals(this.accessWidenerResource)) {
         ClassTweakerWriter writer = ClassTweakerWriter.create(4);
         ClassTweakerVisitor visitor = new FieldToMethodTransformer.FilteringClassTweakerVisitor(this.mappedReplacements.keySet(), writer);
         ClassTweakerReader.create(visitor).read(entry.getData());
         return Transformer.ResourceEntry.create(entry.getName(), entry.getTime(), writer.getOutput());
      } else {
         return entry;
      }
   }

   private boolean processClass(ClassNode cls) {
      boolean replaced = false;

      for (MethodNode method : cls.methods) {
         ListIterator<AbstractInsnNode> iterator = method.instructions.iterator();

         while (iterator.hasNext()) {
            AbstractInsnNode insn = iterator.next();
            if (insn instanceof FieldInsnNode fieldInsn && (fieldInsn.getOpcode() == 180 || fieldInsn.getOpcode() == 178)) {
               for (Entry<String, String> entry : this.mappedReplacements.entrySet()) {
                  String source = entry.getKey();
                  if (source.equals(fieldInsn.name)) {
                     LOGGER.trace("Replacing field getter {} to method {} in {}#{}", new Object[]{source, entry.getValue(), cls.name, method.name});
                     iterator.remove();
                     String getterDesc = "()" + fieldInsn.desc;
                     MethodInsnNode getterCall = new MethodInsnNode(182, fieldInsn.owner, entry.getValue(), getterDesc, false);
                     iterator.add(getterCall);
                     replaced = true;
                  }
               }
            }
         }
      }

      return replaced;
   }

   private static class FilteringAccessWidenerVisitor implements AccessWidenerVisitor {
      private final Collection<String> exclude;
      private final AccessWidenerVisitor parent;

      public FilteringAccessWidenerVisitor(AccessWidenerVisitor parent, Collection<String> exclude) {
         this.parent = parent;
         this.exclude = exclude;
      }

      @Override
      public void visitClass(AccessWidenerVisitor.AccessType access, boolean transitive) {
         this.parent.visitClass(access, transitive);
      }

      @Override
      public void visitMethod(String name, String descriptor, AccessWidenerVisitor.AccessType access, boolean transitive) {
         if (!this.exclude.contains(name)) {
            this.parent.visitMethod(name, descriptor, access, transitive);
         }
      }

      @Override
      public void visitField(String name, String descriptor, AccessWidenerVisitor.AccessType access, boolean transitive) {
         if (!this.exclude.contains(name)) {
            this.parent.visitField(name, descriptor, access, transitive);
         }
      }
   }

   private static class FilteringClassTweakerVisitor extends ForwardingVisitor {
      private final Collection<String> exclude;

      public FilteringClassTweakerVisitor(Collection<String> exclude, ClassTweakerVisitor... visitors) {
         super(visitors);
         this.exclude = exclude;
      }

      @Nullable
      @Override
      public AccessWidenerVisitor visitAccessWidener(String owner) {
         return new FieldToMethodTransformer.FilteringAccessWidenerVisitor(super.visitAccessWidener(owner), this.exclude);
      }
   }
}
