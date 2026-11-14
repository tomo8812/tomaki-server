package maki.dioramacamera;

import java.io.IOException;
import java.util.List;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.AnnotationNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;

public final class CameraCompatibilityHarness {
    public static void main(String[] args) throws IOException {
        ClassNode mixin = read("maki/dioramacamera/mixin/FinalProjectionMixin");
        MethodNode wrapper = mixin.methods.stream().filter(m -> m.name.equals("maki$finalWorldProjection"))
                .findFirst().orElseThrow();
        AnnotationNode wrap = wrapper.visibleAnnotations.stream()
                .filter(a -> a.desc.endsWith("/WrapOperation;")).findFirst().orElseThrow();
        String selector = (String) ((List<?>) value(wrap, "method")).getFirst();
        ClassNode renderer = read("net/minecraft/client/renderer/GameRenderer");
        List<MethodNode> targets = renderer.methods.stream().filter(m -> selector.equals(m.name + m.desc)).toList();
        require(targets.size() == 1, "GameRenderer target must be unique: " + selector);
        Object at = value(wrap, "at");
        AnnotationNode injection = (AnnotationNode) (at instanceof List<?> list ? list.getFirst() : at);
        String invoke = (String) value(injection, "target");
        int calls = 0;
        for (var instruction : targets.getFirst().instructions) {
            if (instruction instanceof MethodInsnNode call
                    && invoke.equals("L" + call.owner + ";" + call.name + call.desc)) {
                calls++;
                ClassNode level = read(call.owner);
                require(level.methods.stream().anyMatch(m -> m.name.equals(call.name) && m.desc.equals(call.desc)),
                        "LevelRenderer call must resolve");
                Type[] wrapped = Type.getArgumentTypes(call.desc);
                Type[] handler = Type.getArgumentTypes(wrapper.desc);
                require(handler.length == wrapped.length + 2, "Wrapper must include receiver and Operation");
                require(handler[0].getInternalName().equals(call.owner), "Receiver must match");
                for (int i = 0; i < wrapped.length; i++) {
                    require(wrapped[i].equals(handler[i + 1]), "Wrapper argument mismatch at " + i);
                }
                require(handler[handler.length - 1].getInternalName().equals(
                        "com/llamalad7/mixinextras/injector/wrapoperation/Operation"), "Operation must be last");
            }
        }
        require(calls == 1, "Exactly one final world render invocation is required");
        ClassNode projection = read("dev/diorama/waypoint/WorldProjection");
        int capture = 0;
        for (var instruction : wrapper.instructions) {
            if (instruction instanceof MethodInsnNode call && call.owner.equals(projection.name)) {
                require(projection.methods.stream().anyMatch(m -> m.name.equals(call.name) && m.desc.equals(call.desc)),
                        "Diorama API mismatch: " + call.name + call.desc);
                capture++;
            }
        }
        require(capture == 1, "Exactly one Diorama projection capture is required");
        System.out.println("Camera render call, wrapper arguments and Diorama projection API match");
    }

    private static Object value(AnnotationNode annotation, String key) {
        for (int i = 0; annotation.values != null && i < annotation.values.size(); i += 2) {
            if (annotation.values.get(i).equals(key)) return annotation.values.get(i + 1);
        }
        throw new AssertionError("Missing annotation value: " + key);
    }

    private static ClassNode read(String name) throws IOException {
        try (var stream = CameraCompatibilityHarness.class.getClassLoader().getResourceAsStream(name + ".class")) {
            if (stream == null) throw new IOException("Missing class: " + name);
            ClassNode node = new ClassNode();
            new ClassReader(stream).accept(node, 0);
            return node;
        }
    }

    private static void require(boolean condition, String message) {
        if (!condition) throw new AssertionError(message);
    }
}
