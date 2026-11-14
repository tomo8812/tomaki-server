package maki.mapshare;

import static org.junit.jupiter.api.Assertions.*;

import dev.diorama.share.ShareSettings;
import dev.diorama.web.WebSettings;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.AnnotationNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;

final class DioramaCompatibilityTest {
    @Test
    void nativeDioramaReadsTheDeployedSharingSettings(@TempDir Path config) throws IOException {
        Files.copy(Path.of(System.getProperty("maki.serverConfig")), config.resolve("diorama-server.properties"));
        ShareSettings shared = ShareSettings.load(config);
        assertTrue(shared.share());
        assertEquals(ShareSettings.Land.GENERATED, shared.land());
        assertEquals(500, shared.kilobytesPerSecond());
        assertTrue(WebSettings.load(config).enabled());
    }

    @Test
    void clientMixinsMatchTheActualDioramaJar() throws IOException {
        for (String name : List.of("DeathMixin", "SettingsTabMixin", "StoreMixin", "WaypointMixin", "StoreAccess")) {
            ClassNode mixin = read("maki/mapshare/mixin/" + name);
            AnnotationNode annotation = annotations(mixin.visibleAnnotations, mixin.invisibleAnnotations).stream()
                    .filter(a -> a.desc.equals("Lorg/spongepowered/asm/mixin/Mixin;"))
                    .findFirst().orElseThrow();
            Object values = value(annotation, "value");
            String target = values != null
                    ? ((Type) ((List<?>) values).getFirst()).getInternalName()
                    : ((String) ((List<?>) value(annotation, "targets")).getFirst()).replace('.', '/');
            ClassNode actual = read(target);
            for (var field : mixin.fields) {
                if (annotations(field.visibleAnnotations, field.invisibleAnnotations).stream()
                        .anyMatch(a -> a.desc.equals("Lorg/spongepowered/asm/mixin/Shadow;"))) {
                    assertTrue(actual.fields.stream().anyMatch(f -> f.name.equals(field.name) && f.desc.equals(field.desc)),
                            target + ": missing shadow field " + field.name);
                }
            }
            for (MethodNode method : mixin.methods) {
                for (AnnotationNode a : annotations(method.visibleAnnotations, method.invisibleAnnotations)) {
                    if (a.desc.equals("Lorg/spongepowered/asm/mixin/injection/Inject;")) {
                        for (Object selector : (List<?>) value(a, "method")) {
                            List<MethodNode> matches = actual.methods.stream()
                                    .filter(m -> selector.equals(m.name + m.desc)).toList();
                            assertEquals(1, matches.size(), target + ": " + selector);
                            Type[] expected = Type.getArgumentTypes(matches.getFirst().desc);
                            Type[] callback = Type.getArgumentTypes(method.desc);
                            assertEquals(expected.length + 1, callback.length, method.name);
                            for (int i = 0; i < expected.length; i++) {
                                assertEquals(expected[i], callback[i], method.name);
                            }
                            String callbackType = Type.getReturnType(matches.getFirst().desc).equals(Type.VOID_TYPE)
                                    ? "CallbackInfo" : "CallbackInfoReturnable";
                            assertEquals("org/spongepowered/asm/mixin/injection/callback/" + callbackType,
                                    callback[callback.length - 1].getInternalName(), method.name);
                        }
                    } else if (a.desc.equals("Lorg/spongepowered/asm/mixin/gen/Accessor;")) {
                        assertTrue(actual.fields.stream().anyMatch(f -> f.name.equals(value(a, "value"))
                                && Type.getReturnType(method.desc).getDescriptor().equals(f.desc)), method.name);
                    }
                }
            }
        }
    }

    private static List<AnnotationNode> annotations(List<AnnotationNode> visible, List<AnnotationNode> invisible) {
        List<AnnotationNode> result = new ArrayList<>();
        if (visible != null) result.addAll(visible);
        if (invisible != null) result.addAll(invisible);
        return result;
    }

    private static Object value(AnnotationNode annotation, String key) {
        for (int i = 0; annotation.values != null && i < annotation.values.size(); i += 2) {
            if (annotation.values.get(i).equals(key)) return annotation.values.get(i + 1);
        }
        return null;
    }

    private static ClassNode read(String name) throws IOException {
        try (var stream = DioramaCompatibilityTest.class.getClassLoader().getResourceAsStream(name + ".class")) {
            if (stream == null) throw new IOException("Missing class: " + name);
            ClassNode node = new ClassNode();
            new ClassReader(stream).accept(node, ClassReader.SKIP_CODE);
            return node;
        }
    }
}
