package maki.millenairelod;

/** Transfer limits for this server's 4096-block pregen radius. */
final class ReplayBudget {
    // A circle of radius 4096/16 chunks contains about 205,887 positions.
    // One candidate per chunk, rounded up to 2^18, fits an entire pregen area.
    // The persistent deferred set is separate and may span millions of chunks.
    static final int QUEUE_LIMIT = 262_144;
    // Native SiteHunter examines at most 64 entries per tick. Transfer one such
    // batch per second; Millenaire retains all scoring/build/TTL scheduling.
    static final int TRANSFER_BATCH = 64;
    private ReplayBudget() {}
    static int mainCandidates(int main,int hunter){
        return room((long)main+hunter);
    }
    static int lone(int count){return room(count);}
    private static int room(long count){return (int)Math.max(0L,Math.min(TRANSFER_BATCH,QUEUE_LIMIT-count));}
}
