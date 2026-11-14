package maki.idlepregen;

import java.io.IOException;
import java.util.List;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.AnnotationNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;

/** Checks the injector against the actual Minecraft bytecode, including overloads. */
public final class NavigationMixinTargetHarness {
    public static void main(String[] args) throws IOException {
        ClassNode target = read("net/minecraft/world/level/PathNavigationRegion");
        ClassNode mixin = read("maki/idlepregen/mixin/PathNavigationRegionMixin");
        MethodNode callback = mixin.methods.stream()
                .filter(m -> m.name.equals("maki$countEmptyChunkAccess"))
                .findFirst().orElseThrow();
        AnnotationNode inject = callback.visibleAnnotations.stream()
                .filter(a -> a.desc.equals("Lorg/spongepowered/asm/mixin/injection/Inject;"))
                .findFirst().orElseThrow();
        List<?> selectors = null;
        for (int i = 0; i < inject.values.size(); i += 2) {
            if (inject.values.get(i).equals("method")) {
                selectors = (List<?>) inject.values.get(i + 1);
            }
        }
        if (selectors == null || selectors.size() != 1) {
            throw new AssertionError("Expected one explicit method selector");
        }
        String selector = (String) selectors.getFirst();
        List<MethodNode> matches = target.methods.stream()
                .filter(m -> selector.equals(m.name + m.desc)).toList();
        if (matches.size() != 1) {
            throw new AssertionError("Injector must resolve exactly one Minecraft method: " + selector);
        }
        MethodNode method = matches.getFirst();
        Type[] expected = Type.getArgumentTypes(method.desc);
        Type[] actual = Type.getArgumentTypes(callback.desc);
        if (actual.length != expected.length + 1) {
            throw new AssertionError("Callback argument count differs from target");
        }
        for (int i = 0; i < expected.length; i++) {
            if (!actual[i].equals(expected[i])) {
                throw new AssertionError("Callback argument " + i + " differs from target");
            }
        }
        if (!actual[actual.length - 1].getInternalName().equals(
                "org/spongepowered/asm/mixin/injection/callback/CallbackInfoReturnable")) {
            throw new AssertionError("Missing return callback");
        }
        // Keep the overload that caused the production crash in this regression check.
        if (target.methods.stream().noneMatch(m -> m.name.equals(method.name)
                && !m.desc.equals(method.desc))) {
            throw new AssertionError("Expected overloaded Minecraft getChunk methods");
        }
        System.out.println("Navigation injector resolves one overload with matching callback arguments");
    }

    private static ClassNode read(String name) throws IOException {
        try (var input = NavigationMixinTargetHarness.class.getClassLoader()
                .getResourceAsStream(name + ".class")) {
            if (input == null) throw new IOException("Missing class: " + name);
            ClassNode node = new ClassNode();
            new ClassReader(input).accept(node, ClassReader.SKIP_CODE);
            return node;
        }
    }
}
