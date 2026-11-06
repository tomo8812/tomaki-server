package maki.mapshare;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import org.junit.jupiter.api.Test;

final class ClientSyncStateTest {
    private static final UUID OWNER = UUID.fromString("00000000-0000-0000-0000-000000000001");

    @Test
    void unsentAddSurvivesSnapshotReconciliation() {
        ClientSyncState state = new ClientSyncState();
        state.markSent(Map.of());

        UUID source = UUID.fromString("00000000-0000-0000-0000-000000000101");
        Map<UUID, ShareState.Mark> local = Map.of(source, mark(source, "new"));

        Set<UUID> unsent = state.unsentChanges(local);
        state.acceptSnapshot(Map.of(), 4L);
        state.alignSent(local, unsent);

        assertEquals(Set.of(source), unsent);
        assertFalse(state.sent().containsKey(source), "unsent add must remain different from the sent baseline");
    }

    @Test
    void unsentEditSurvivesSnapshotReconciliation() {
        ClientSyncState state = new ClientSyncState();
        UUID source = UUID.fromString("00000000-0000-0000-0000-000000000102");
        ShareState.Mark oldMark = mark(source, "old");
        ShareState.Mark editedMark = mark(source, "edited");
        state.markSent(Map.of(source, oldMark));

        Map<UUID, ShareState.Mark> local = Map.of(source, editedMark);
        Set<UUID> unsent = state.unsentChanges(local);
        state.acceptSnapshot(Map.of(source, oldMark), 5L);
        state.alignSent(local, unsent);

        assertEquals(Set.of(source), unsent);
        assertEquals(oldMark, state.sent().get(source), "unsent edit must still differ from sent state");
    }

    @Test
    void unsentDeleteSurvivesSnapshotReconciliation() {
        ClientSyncState state = new ClientSyncState();
        UUID source = UUID.fromString("00000000-0000-0000-0000-000000000103");
        ShareState.Mark oldMark = mark(source, "delete-me");
        state.markSent(Map.of(source, oldMark));

        Map<UUID, ShareState.Mark> local = Map.of();
        Set<UUID> unsent = state.unsentChanges(local);
        state.acceptSnapshot(Map.of(source, oldMark), 6L);
        state.alignSent(local, unsent);

        assertEquals(Set.of(source), unsent);
        assertEquals(oldMark, state.sent().get(source), "unsent delete needs the old baseline so the next poll emits delete");
    }

    @Test
    void sentButUnacknowledgedRevisionBlocksSnapshotMerge() {
        ClientSyncState state = new ClientSyncState();
        UUID source = UUID.fromString("00000000-0000-0000-0000-000000000104");
        state.markSent(Map.of(source, mark(source, "sent")));

        state.acceptSnapshot(Map.of(), 7L);

        assertFalse(state.isAcknowledgedThrough(8L));
        assertTrue(state.isAcknowledgedThrough(7L));
    }

    @Test
    void acknowledgedSnapshotCanRealignNonDirtyBaseline() {
        ClientSyncState state = new ClientSyncState();
        UUID source = UUID.fromString("00000000-0000-0000-0000-000000000105");
        ShareState.Mark serverMark = mark(source, "server");
        Map<UUID, ShareState.Mark> local = new LinkedHashMap<>();
        local.put(source, serverMark);

        state.markSent(local);
        state.acceptSnapshot(local, 9L);
        Set<UUID> unsent = state.unsentChanges(local);
        state.alignSent(local, unsent);

        assertTrue(unsent.isEmpty());
        assertEquals(local, state.sent());
        assertEquals(local, state.acknowledged());
        assertTrue(state.isAcknowledgedThrough(9L));
    }

    @Test
    void delayedSnapshotCannotRegressAcknowledgedState() {
        ClientSyncState state = new ClientSyncState();
        UUID source = UUID.fromString("00000000-0000-0000-0000-000000000106");
        ShareState.Mark latest = mark(source, "latest");

        state.acceptSnapshot(Map.of(source, latest), 12L);
        state.acceptSnapshot(Map.of(), 11L);

        assertEquals(12L, state.acknowledgedRevision());
        assertEquals(latest, state.acknowledged().get(source));
    }

    private static ShareState.Mark mark(UUID source, String name) {
        return new ShareState.Mark(
            source,
            OWNER,
            "Tester",
            false,
            name,
            "T",
            10,
            64,
            20,
            "minecraft:overworld",
            0x336699,
            1234L,
            0L
        );
    }
}
