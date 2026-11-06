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
    @Test
    void selectedExternalBackupCanBeRestoredAfterPreservingFollowStateSeparately() throws Exception {
        Path source=temp.resolve("config/chunksmith/tasks/minecraft/overworld.properties");
        Path externalRoot=temp.resolve("world/data/maki_lod_task_backups/v1/minecraft/overworld");
        Path followRoot=temp.resolve("world/data/maki_lod_task_backups/v1/minecraft/overworld-follow");
        Files.createDirectories(source.getParent());
        Files.writeString(source,TASK,StandardCharsets.UTF_8);
        var external=TaskMigrationStore.stash(source,externalRoot,"switch-follow","test-admin");

        String follow=TASK+"maki-lod-cpu-owner=follow:test-session\n";
        Files.writeString(source,follow,StandardCharsets.UTF_8);
        var displaced=TaskMigrationStore.stash(source,followRoot,"restore-replaced-follow","test-admin");

        var restored=TaskMigrationStore.restore(source,externalRoot,external.id(),"test-admin");
        assertEquals(external.id(),restored.id());
        assertEquals(TASK,Files.readString(source,StandardCharsets.UTF_8));
        assertEquals(follow,Files.readString(displaced.payload(),StandardCharsets.UTF_8));
    }

    @Test
    void restoreRejectsBackupIdTraversal() throws Exception {
        Path source=temp.resolve("config/chunksmith/tasks/minecraft/overworld.properties");
        Path root=temp.resolve("world/data/maki_lod_task_backups/v1/minecraft/overworld");
        var error=assertThrows(Exception.class,()->TaskMigrationStore.restore(source,root,"../escape","test-admin"));
        assertTrue(error.getMessage().contains("Invalid backup id"));
        assertFalse(Files.exists(source));
    }

    @Test
    void restoreRejectsSnapshotThatNoLongerMatchesSavedHash() throws Exception {
        Path source=temp.resolve("config/chunksmith/tasks/minecraft/overworld.properties");
        Path root=temp.resolve("world/data/maki_lod_task_backups/v1/minecraft/overworld");
        Files.createDirectories(source.getParent());
        Files.writeString(source,TASK,StandardCharsets.UTF_8);
        var backup=TaskMigrationStore.backup(source,root,"backup","test-admin");
        Files.delete(source);
        Files.writeString(backup.payload(),TASK.replace("chunks=5660","chunks=0"),StandardCharsets.UTF_8);

        var error=assertThrows(Exception.class,()->TaskMigrationStore.restore(source,root,backup.id(),"test-admin"));
        assertTrue(error.getMessage().contains("hash mismatch"));
        assertFalse(Files.exists(source));
    }

    @Test
    void conditionalDeleteNeverRemovesUnexpectedLiveState() throws Exception {
        Path source=temp.resolve("config/chunksmith/tasks/minecraft/overworld.properties");
        Files.createDirectories(source.getParent());
        Files.writeString(source,TASK,StandardCharsets.UTF_8);
        assertFalse(TaskMigrationStore.deleteIfMatches(source,"0".repeat(64)));
        assertTrue(Files.exists(source));
        String expected=TaskMigrationStore.sha256(source);
        assertTrue(TaskMigrationStore.deleteIfMatches(source,expected));
        assertFalse(Files.exists(source));
    }

    @Test
    void restoreRejectsBackupWithMissingMetadata() throws Exception {
        Path source=temp.resolve("config/chunksmith/tasks/minecraft/overworld.properties");
        Path root=temp.resolve("world/data/maki_lod_task_backups/v1/minecraft/overworld");
        Files.createDirectories(source.getParent());
        Files.writeString(source,TASK,StandardCharsets.UTF_8);
        var backup=TaskMigrationStore.backup(source,root,"backup","test-admin");
        Files.delete(source);
        Files.delete(backup.directory().resolve("migration.meta"));

        var error=assertThrows(Exception.class,()->TaskMigrationStore.restore(source,root,backup.id(),"test-admin"));
        assertTrue(error.getMessage().contains("metadata is missing"));
        assertFalse(Files.exists(source));
    }

}
