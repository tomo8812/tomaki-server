package maki.chunksmithfixes;

import com.kishku7.chunksmith.lod.CsLodChunk;
import java.nio.file.*;
import java.util.*;

/** Pending LOD snapshots; journal I/O is isolated from save hooks. */
public final class ExploredLodRefresh {
    private static final Map<Path, Journal> JOURNALS = new HashMap<>();
    private static final IdentityHashMap<CsLodChunk, Request> RECORDS = new IdentityHashMap<>();
    private static final ThreadLocal<Request> CAPTURE = new ThreadLocal<>();
    private static final class Journal {
        final AsyncLodJournal receipts;
        final Map<Long,Request> pending=new HashMap<>();
        Journal(Path root){receipts=new AsyncLodJournal(root);}
    }
    private static final class Request {
        final Journal journal; final long key; boolean submitted;
        final java.util.concurrent.CompletableFuture<Void> saved=new java.util.concurrent.CompletableFuture<>();
        Request(Journal journal, long key) { this.journal=journal; this.key=key; }
    }
    private ExploredLodRefresh() {}
    private static long key(int x, int z) { return ((long)x << 32) | (z & 0xffffffffL); }
    private static Journal journal(Path root) {
        return JOURNALS.computeIfAbsent(root.toAbsolutePath().normalize(), Journal::new);
    }
    public static synchronized java.util.concurrent.CompletableFuture<Void> flush(Path root){return journal(root).receipts.flush();}
    public static synchronized boolean needsRefresh(Path root, int x, int z) {
        return journal(root).receipts.contains(key(x, z));
    }
    public static synchronized boolean pending(Path root, int x, int z) {
        return journal(root).pending.containsKey(key(x, z));
    }
    public static java.util.concurrent.CompletableFuture<Void> capture(Path root,int x,int z,Runnable offer){
        Request request;
        synchronized(ExploredLodRefresh.class){
            Journal journal=journal(root);long key=key(x,z);request=new Request(journal,key);
            journal.receipts.change(key,true);
        }
        Request previous=CAPTURE.get();CAPTURE.set(request);
        try{offer.run();}
        catch(RuntimeException error){request.saved.completeExceptionally(error);}
        finally{
            if(previous==null)CAPTURE.remove();else CAPTURE.set(previous);
            if(!request.submitted)request.saved.completeExceptionally(new IllegalStateException("LOD snapshot was not submitted"));
        }
        return request.saved;
    }
    public static synchronized void request(Path root,int x,int z){journal(root).receipts.change(key(x,z),true);}
    public static synchronized void resolved(Path root,int x,int z){journal(root).receipts.change(key(x,z),false);}
    public static synchronized Set<Long> targets(Path root){return journal(root).receipts.keys();}
    public static synchronized java.util.concurrent.CompletableFuture<Void> submitted(Path root, CsLodChunk record) {
        Journal journal=journal(root);long key=key(record.getChunkX(),record.getChunkZ());
        Request request=CAPTURE.get();
        if(request==null||request.journal!=journal||request.key!=key)request=new Request(journal,key);
        else request.submitted=true;
        journal.pending.put(key,request);RECORDS.put(record,request);
        return request.saved;
    }
    public static void completed(CsLodChunk record,LodWriteEvents.Outcome outcome){
        Request request;
        synchronized(ExploredLodRefresh.class){
            request=RECORDS.remove(record);if(request==null)return;
            request.journal.pending.remove(request.key,request);
        }
        if(outcome==LodWriteEvents.Outcome.FAILED)request.saved.completeExceptionally(new IllegalStateException("LOD write failed"));
        else request.saved.complete(null);
    }
    public static void clear() {
        List<Journal> journals;
        synchronized(ExploredLodRefresh.class){journals=new ArrayList<>(JOURNALS.values());JOURNALS.clear();RECORDS.clear();CAPTURE.remove();}
        for(Journal journal:journals)try{journal.receipts.close();}
        catch(java.util.concurrent.CompletionException error){org.slf4j.LoggerFactory.getLogger("MakiChunksmithFixes").error("Could not flush LOD refresh receipts",error);}
    }

}
