package maki.chunksmithcache;

import java.util.concurrent.TimeUnit;

public final class PregenRetryLedgerSelfTest {
    public static void main(String[] args) {
        PregenRetryLedger<String> ledger=new PregenRetryLedger<>("test");
        long now=TimeUnit.SECONDS.toNanos(10);

        require(ledger.defer("a",now,"failed"),"first retry is recorded");
        require(!ledger.due("a",now+TimeUnit.SECONDS.toNanos(4)),"retry respects initial delay");
        require(ledger.due("a",now+TimeUnit.SECONDS.toNanos(5)),"retry matures at initial delay");

        long second=now+TimeUnit.SECONDS.toNanos(5);
        require(ledger.defer("a",second,"failed-again"),"existing retry can be updated");
        require(!ledger.due("a",second+TimeUnit.SECONDS.toNanos(9)),"second failure backs off");
        require(ledger.due("a",second+TimeUnit.SECONDS.toNanos(10)),"second retry matures after exponential delay");
        ledger.complete("a");
        require(!ledger.contains("a"),"successful coordinate clears debt");

        for(int i=0;i<PregenRetryLedger.LIMIT;i++)
            require(ledger.defer("k"+i,now,"capacity"),"ledger accepts bounded entry "+i);
        require(!ledger.defer("overflow",now,"overflow"),"ledger refuses entries beyond its bound");
        PregenRetryLedger.Status status=ledger.status(now);
        require(status.entries()==PregenRetryLedger.LIMIT,"ledger stays at the configured bound");
        require(!status.ledgerComplete()&&status.overflowDirty(),"overflow makes completeness explicitly unknown");
        require(ledger.overflowRescanDue(now),"overflow requests an immediate conservative rescan");
        require(!ledger.overflowRescanDue(now+TimeUnit.SECONDS.toNanos(59)),"overflow rescans are rate limited");
        require(ledger.overflowRescanDue(now+TimeUnit.MINUTES.toNanos(1)),"overflow rescan becomes due again");

        ledger.removeIf(key->key.startsWith("k"));
        require(ledger.status(now).entries()==0,"predicate cleanup removes server-scoped debt");
        ledger.clear();
        require(ledger.status(now).ledgerComplete()&&!ledger.status(now).overflowDirty(),"clear resets overflow state");

        System.out.println("PregenRetryLedgerSelfTest PASS");
    }

    private static void require(boolean condition,String message) {
        if(!condition)throw new AssertionError(message);
    }
}
