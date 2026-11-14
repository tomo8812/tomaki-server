package maki.mapshare;

import static org.junit.jupiter.api.Assertions.*;

import dev.diorama.waypoint.Waypoint;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import org.junit.jupiter.api.Test;

final class LocalWaypointsTest {
    private static final UUID OWNER = UUID.fromString("00000000-0000-0000-0000-000000000001");

    @Test
    void switchingMapsAndReturningDoesNotEmitDeletesOrPuts() {
        Waypoint main = waypoint("main", null);
        Waypoint custom = waypoint("custom", "custom-map");
        List<Waypoint> active = new ArrayList<>(List.of(main));
        List<Waypoint> elsewhere = new ArrayList<>(List.of(custom));
        LocalWaypoints local = new LocalWaypoints(active, elsewhere);
        Map<UUID, ShareState.Mark> original = local.own(LocalWaypointsTest::draft);
        ClientSyncState sync = new ClientSyncState();
        sync.markSent(original);

        active.clear();
        active.add(custom);
        elsewhere.clear();
        elsewhere.add(main);
        assertEquals(original, local.own(LocalWaypointsTest::draft));
        assertTrue(sync.unsentChanges(local.own(LocalWaypointsTest::draft)).isEmpty());

        active.clear();
        active.add(main);
        elsewhere.clear();
        elsewhere.add(custom);
        assertEquals(original, local.own(LocalWaypointsTest::draft));
        assertTrue(sync.unsentChanges(local.own(LocalWaypointsTest::draft)).isEmpty());
    }

    @Test
    void snapshotReconciliationFindsTheInactiveMarkerWithoutMovingOrDuplicatingIt() {
        Waypoint marker = waypoint("old", "inactive-map");
        marker.folder = "Landmarks";
        marker.icon = "home";
        List<Waypoint> active = new ArrayList<>();
        List<Waypoint> elsewhere = new ArrayList<>(List.of(marker));
        LocalWaypoints local = new LocalWaypoints(active, elsewhere);

        Waypoint found = local.findManual(marker.id);
        assertSame(marker, found);
        found.name = "server edit";
        assertTrue(active.isEmpty());
        assertEquals(List.of(marker), elsewhere);
        assertEquals("inactive-map", found.map);
        assertEquals("Landmarks", found.folder);
        assertEquals("home", found.icon);
        assertEquals("server edit", local.own(LocalWaypointsTest::draft).get(marker.id).name());
    }

    @Test
    void actualDeletionOfAnInactiveMapStillEmitsDeletion() {
        Waypoint marker = waypoint("custom", "deleted-map");
        List<Waypoint> elsewhere = new ArrayList<>(List.of(marker));
        LocalWaypoints local = new LocalWaypoints(new ArrayList<>(), elsewhere);
        ClientSyncState sync = new ClientSyncState();
        sync.markSent(local.own(LocalWaypointsTest::draft));

        elsewhere.clear();
        assertEquals(Set.of(marker.id), sync.unsentChanges(local.own(LocalWaypointsTest::draft)));
        assertFalse(local.own(LocalWaypointsTest::draft).containsKey(marker.id));
    }

    @Test
    void acknowledgedServerDeletionRemovesMarkersFromBothPartitions() {
        Waypoint main = waypoint("main", null);
        Waypoint custom = waypoint("custom", "custom-map");
        List<Waypoint> active = new ArrayList<>(List.of(main));
        List<Waypoint> elsewhere = new ArrayList<>(List.of(custom));
        LocalWaypoints local = new LocalWaypoints(active, elsewhere);

        assertTrue(local.removeMissing(Set.of(main.id, custom.id), Set.of(), Set.of()));
        assertTrue(active.isEmpty());
        assertTrue(elsewhere.isEmpty());
    }

    @Test
    void unsentInactiveEditsAndUnsharedMarkersSurviveOlderServerSnapshots() {
        Waypoint edited = waypoint("edited locally", "custom-map");
        Waypoint unshared = waypoint("new", null);
        Waypoint death = waypoint("death", "custom-map");
        death.kind = Waypoint.Kind.DEATH;
        List<Waypoint> elsewhere = new ArrayList<>(List.of(edited, unshared, death));
        LocalWaypoints local = new LocalWaypoints(new ArrayList<>(), elsewhere);

        assertFalse(local.removeMissing(Set.of(edited.id, death.id), Set.of(), Set.of(edited.id)));
        assertEquals(List.of(edited, unshared, death), local.all());
        assertEquals(Set.of(edited.id, unshared.id), local.own(LocalWaypointsTest::draft).keySet());
    }

    private static Waypoint waypoint(String name, String map) {
        Waypoint marker = new Waypoint();
        marker.name = name;
        marker.map = map;
        marker.dimension = "minecraft:overworld";
        return marker;
    }

    private static ShareState.Mark draft(Waypoint waypoint) {
        return new ShareState.Mark(waypoint.id, OWNER, "Tester", false, waypoint.name, waypoint.initials,
                waypoint.x, waypoint.y, waypoint.z, waypoint.dimension, waypoint.color, waypoint.created, 0L);
    }
}
