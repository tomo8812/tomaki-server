package maki.lodcpu;

import static org.junit.jupiter.api.Assertions.*;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

final class TaskMigrationStoreTest {
    @TempDir Path temp;

    private static final String TASK=""
        +"world=minecraft:overworld\n"
        +"cancelled=false\n"
        +"center-x=6984.0\n"
        +"center-z=-200.0\n"
        +"radius=4096.0\n"
        +"radius-z=4096\n"
        +"shape=circle\n"
        +"lod-horizon=1.0\n"
        +"pattern=concentric\n"
        +"chunks=5660\n"
        +"time=1898133\n";

    @Test
    void backupPreservesExactRuntimeStateAndAuditMetadata() throws Exception {
        Path source=temp.resolve("config/chunksmith/tasks/minecraft/overworld.properties");
        Path root=temp.resolve("world/data/maki_lod_task_backups/v1/minecraft/overworld");
        Files.createDirectories(source.getParent());
        Files.writeString(source,TASK,StandardCharsets.UTF_8);

        var backup=TaskMigrationStore.backup(source,root,"backup","test-admin");

        assertTrue(Files.exists(source));
        assertEquals(TASK,Files.readString(backup.payload(),StandardCharsets.UTF_8));
        assertEquals(TaskMigrationStore.sha256(source),backup.sha256());
        String metadata=Files.readString(backup.directory().resolve("migration.meta"),StandardCharsets.UTF_8);
        assertTrue(metadata.contains("schemaVersion=1"));
        assertTrue(metadata.contains("action=backup"));
        assertTrue(metadata.contains("actor=test-admin"));
        assertTrue(metadata.contains("inputSha256="+backup.sha256()));
        assertTrue(metadata.contains("sourceModifiedAtMillis="+backup.sourceModifiedAt()));
        assertTrue(Files.readString(root.resolve("audit.log")).contains("backup-created"));
    }

    @Test
    void stashAndRestoreLatestAreLossless() throws Exception {
        Path source=temp.resolve("config/chunksmith/tasks/minecraft/overworld.properties");
        Path root=temp.resolve("world/data/maki_lod_task_backups/v1/minecraft/overworld");
        Files.createDirectories(source.getParent());
        Files.writeString(source,TASK,StandardCharsets.UTF_8);

        var backup=TaskMigrationStore.stash(source,root,"switch-follow","test-admin");
        assertFalse(Files.exists(source));
        assertEquals(TASK,Files.readString(backup.payload(),StandardCharsets.UTF_8));

        var restored=TaskMigrationStore.restoreLatest(source,root,"test-admin");
        assertEquals(backup.id(),restored.id());
        assertEquals(TASK,Files.readString(source,StandardCharsets.UTF_8));
        assertEquals(backup.sha256(),TaskMigrationStore.sha256(source));
        String audit=Files.readString(root.resolve("audit.log"),StandardCharsets.UTF_8);
        assertTrue(audit.contains("source-stashed"));
        assertTrue(audit.contains("restored"));
    }

    @Test
    void restoreNeverOverwritesLiveTask() throws Exception {
        Path source=temp.resolve("config/chunksmith/tasks/minecraft/overworld.properties");
        Path root=temp.resolve("world/data/maki_lod_task_backups/v1/minecraft/overworld");
        Files.createDirectories(source.getParent());
        Files.writeString(source,TASK,StandardCharsets.UTF_8);
        TaskMigrationStore.backup(source,root,"backup","test-admin");

        var error=assertThrows(Exception.class,()->TaskMigrationStore.restoreLatest(source,root,"test-admin"));
        assertTrue(error.getMessage().contains("refusing to overwrite"));
        assertEquals(TASK,Files.readString(source,StandardCharsets.UTF_8));
    }
}
