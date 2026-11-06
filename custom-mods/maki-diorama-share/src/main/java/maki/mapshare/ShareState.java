package maki.mapshare;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public final class ShareState {
    public static final int MAX_HISTORY = 1000;
    public static final int MAX_MANUAL = 2048;

    public UUID worldId = UUID.randomUUID();
    public Map<String, Mark> marks = new LinkedHashMap<>();
    public Set<UUID> migrated = new HashSet<>();
    public long serial;

    public static int count(int requested) {
        return Math.max(1, Math.min(MAX_HISTORY, requested));
    }

    public static boolean valid(Mark mark) {
        return mark != null
            && mark.source() != null
            && mark.owner() != null
            && validText(mark.ownerName(), 64)
            && validText(mark.name(), 128)
            && validText(mark.initials(), 8)
            && mark.dimension() != null
            && mark.dimension().length() <= 256
            && mark.dimension().matches("[a-z0-9_.-]+:[a-z0-9_./-]+")
            && Math.abs((long) mark.x()) <= 30_000_000L
            && Math.abs((long) mark.z()) <= 30_000_000L
            && Math.abs((long) mark.y()) <= 20_000_000L
            && mark.created() >= 0L;
    }

    private static boolean validText(String value, int maxLength) {
        return value != null
            && value.length() <= maxLength
            && value.codePoints().noneMatch(Character::isISOControl);
    }

    public boolean putManual(UUID owner, String ownerName, Mark requested) {
        if (requested == null) {
            return false;
        }

        Mark mark = new Mark(
            requested.source(),
            owner,
            ownerName,
            false,
            requested.name(),
            requested.initials(),
            requested.x(),
            requested.y(),
            requested.z(),
            requested.dimension(),
            requested.color(),
            requested.created(),
            0L
        );
        if (!valid(mark)) {
            return false;
        }

        long manualCount = marks.values().stream()
            .filter(existing -> !existing.death() && existing.owner().equals(owner))
            .count();
        if (!marks.containsKey(mark.key()) && manualCount >= MAX_MANUAL) {
            return false;
        }

        Mark previous = marks.put(mark.key(), mark);
        return !mark.equals(previous);
    }

    public boolean deleteManual(UUID owner, UUID source) {
        return marks.remove(owner + "/manual/" + source) != null;
    }

    public boolean addDeath(Mark requested, boolean migration) {
        if (!valid(requested) || !requested.death() || migration && migrated.contains(requested.owner())) {
            return false;
        }
        if (marks.containsKey(requested.key())) {
            return false;
        }

        Mark stored = new Mark(
            requested.source(),
            requested.owner(),
            requested.ownerName(),
            true,
            "",
            "",
            requested.x(),
            requested.y(),
            requested.z(),
            requested.dimension(),
            requested.color(),
            requested.created(),
            ++serial
        );
        marks.put(stored.key(), stored);

        List<Mark> deaths = deaths(stored.owner());
        for (int index = MAX_HISTORY; index < deaths.size(); index++) {
            marks.remove(deaths.get(index).key());
        }
        return true;
    }

    public List<Mark> deaths(UUID owner) {
        return marks.values().stream()
            .filter(mark -> mark.death() && mark.owner().equals(owner))
            .sorted(
                Comparator.comparingLong(Mark::created)
                    .thenComparingLong(Mark::order)
                    .reversed()
                    .thenComparing(mark -> mark.source().toString())
            )
            .toList();
    }

    public List<View> view(int requestedCount) {
        int count = count(requestedCount);
        List<View> result = new ArrayList<>();

        for (Mark mark : marks.values()) {
            if (!mark.death()) {
                result.add(new View(mark, 0));
            }
        }

        Set<UUID> deathOwners = new LinkedHashSet<>();
        for (Mark mark : marks.values()) {
            if (mark.death()) {
                deathOwners.add(mark.owner());
            }
        }

        for (UUID owner : deathOwners) {
            List<Mark> deaths = deaths(owner);
            for (int index = 0; index < Math.min(count, deaths.size()); index++) {
                result.add(new View(deaths.get(index), index + 1));
            }
        }

        return List.copyOf(result);
    }

    public void validate() {
        if (worldId == null || marks == null || migrated == null) {
            throw new IllegalArgumentException("Missing sharing state");
        }

        for (Map.Entry<String, Mark> entry : marks.entrySet()) {
            if (!valid(entry.getValue()) || !entry.getKey().equals(entry.getValue().key())) {
                throw new IllegalArgumentException("Invalid stored marker");
            }
        }
    }

    public record Mark(
        UUID source,
        UUID owner,
        String ownerName,
        boolean death,
        String name,
        String initials,
        int x,
        int y,
        int z,
        String dimension,
        int color,
        long created,
        long order
    ) {
        public String key() {
            return owner + "/" + (death ? "death/" : "manual/") + source;
        }

        public UUID displayId() {
            return UUID.nameUUIDFromBytes(key().getBytes(StandardCharsets.UTF_8));
        }
    }

    public record View(Mark mark, int age) {
        public String label() {
            return mark.death()
                ? mark.ownerName() + " の死亡地点（" + (age == 1 ? "最新" : age - 1 + "つ前") + "）"
                : mark.name() + " [" + mark.ownerName() + "]";
        }
    }
}
