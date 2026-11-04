package reloc.net.fabricmc.classtweaker.api;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.ApiStatus.NonExtendable;
import org.objectweb.asm.ClassVisitor;
import reloc.net.fabricmc.classtweaker.api.visitor.ClassTweakerVisitor;
import reloc.net.fabricmc.classtweaker.impl.ClassTweakerImpl;

@NonExtendable
public interface ClassTweaker extends ClassTweakerVisitor {
   int AW_V1 = 1;
   int AW_V2 = 2;
   int CT_V1 = 3;
   int CT_V2 = 4;
   int CT_LATEST = 4;

   static ClassTweaker newInstance() {
      return new ClassTweakerImpl();
   }

   String getNamespace();

   Set<String> getTargets();

   AccessWidener getAccessWidener(String var1);

   Map<String, AccessWidener> getAllAccessWideners();

   List<InjectedInterface> getInjectedInterfaces(String var1);

   Map<String, List<InjectedInterface>> getAllInjectedInterfaces();

   List<EnumExtension> getEnumExtensions(String var1);

   Map<String, List<EnumExtension>> getAllEnumExtensions();

   ClassVisitor createClassVisitor(int var1, @Nullable ClassVisitor var2, @Nullable BiConsumer<String, byte[]> var3);
}
