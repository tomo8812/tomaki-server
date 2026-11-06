package maki.chunksmithcache;

import java.util.Set;

public final class BackfillScopesSelfTest {
    public static void main(String[] args) {
        BackfillScopes scopes = new BackfillScopes();
        scopes.clear("overworld", 1L); // unload without a registered session
        scopes.arm("overworld", 1L, Set.of(1L, 2L));
        scopes.arm("overworld", 3L, Set.of(3L, 4L));
        scopes.arm("nether", 1L, Set.of(1L, 2L));
        require(scopes.sessions("overworld").size() == 2, "second backfill must retain retired first scope");
        require(scopes.get("overworld", 1L).suppressed().contains(2L), "dependency survives target completion");
        scopes.get("overworld", 1L).suppressed().remove(2L);
        require(!scopes.get("overworld", 1L).suppressed().contains(2L), "player handoff is permanent");
        require(scopes.get("nether", 1L).suppressed().contains(2L), "dimensions isolate player handoff");
        scopes.clear("overworld", 3L);
        require(scopes.get("overworld", 1L) != null, "draining one scope must not clear another");
        require(scopes.get("nether", 1L) != null, "draining must not clear another dimension");
        boolean refused = false;
        try { scopes.arm("overworld", 1L, Set.of(9L)); }
        catch (IllegalStateException expected) { refused = true; }
        require(refused && scopes.get("overworld", 1L).suppressed().contains(1L), "duplicate admission must preserve ownership");
        scopes.clear("overworld", 1L);
        require(scopes.sessions("overworld").isEmpty(), "fully drained scopes release ownership");
        System.out.println("BackfillScopesSelfTest: PASS");
    }

    private static void require(boolean condition, String message) {
        if (!condition) throw new AssertionError(message);
    }
}
