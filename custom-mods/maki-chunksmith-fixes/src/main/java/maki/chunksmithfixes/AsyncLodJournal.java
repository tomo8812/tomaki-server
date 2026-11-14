package maki.chunksmithfixes;

import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.concurrent.*;

/** Per-store receipt I/O; callers only update small in-memory state. */
final class AsyncLodJournal {
    private final Path file;
    private final ExecutorService io=Executors.newSingleThreadExecutor(action->{
        Thread thread=new Thread(action,"maki-lod-receipts");thread.setDaemon(true);return thread;
    });
    private final Set<Long> debt=new HashSet<>(),diskDebt=new HashSet<>();
    private final Map<Long,Boolean> overrides=new HashMap<>();
    private final Map<Long,CompletableFuture<Void>> markers=new HashMap<>();
    private boolean loaded;
    private boolean ioInitialized,repair;
    private int entries;
    private CompletableFuture<Void> tail;
    AsyncLodJournal(Path root){
        file=root.resolve("maki-lod-refresh.pending");
        tail=CompletableFuture.runAsync(this::load,io);
    }
    private void load(){
        try{
            diskDebt.clear();entries=0;
            if(Files.exists(file)){
                try(var in=new DataInputStream(Files.newInputStream(file))){
                    while(true){
                        try{boolean add=in.readBoolean();long key=in.readLong();if(add)diskDebt.add(key);else diskDebt.remove(key);entries++;}
                        catch(EOFException end){break;}
                    }
                }
                compact();
            }
            Files.deleteIfExists(file.resolveSibling(file.getFileName()+".tmp"));
            ioInitialized=true;
            synchronized(this){
                debt.addAll(diskDebt);
                overrides.forEach((key,add)->{if(add)debt.add(key);else debt.remove(key);});
                overrides.clear();loaded=true;
            }
        }catch(IOException error){throw new CompletionException(error);}
    }
    synchronized boolean contains(long key){return loaded&&debt.contains(key);}
    synchronized Set<Long> keys(){return loaded?Set.copyOf(debt):Set.of();}
    synchronized void change(long key,boolean add){
        if(loaded&&debt.contains(key)==add&&!markers.getOrDefault(key,CompletableFuture.completedFuture(null)).isCompletedExceptionally())return;
        if(add)debt.add(key);else debt.remove(key);
        if(!loaded)overrides.put(key,add);
        CompletableFuture<Void> marker=tail.handle((unused,error)->null).thenRunAsync(()->{
            try{
                if(!ioInitialized)load();
                if(repair){compact();repair=false;}
                append(key,add);
            }catch(IOException|CompletionException failure){repair=ioInitialized;throw new CompletionException(failure);}
        },io);
        tail=marker;markers.put(key,marker);
        marker.whenComplete((unused,error)->{
            if(error==null)synchronized(this){markers.remove(key,marker);}
        });
    }
    synchronized CompletableFuture<Void> flush(){return tail;}
    private void append(long key,boolean add){
        try{
            if(diskDebt.contains(key)==add)return;
            Files.createDirectories(file.getParent());
            try(var out=new DataOutputStream(Files.newOutputStream(file,StandardOpenOption.CREATE,StandardOpenOption.APPEND))){
                out.writeBoolean(add);out.writeLong(key);
            }
            if(add)diskDebt.add(key);else diskDebt.remove(key);
            entries++;
            if(diskDebt.isEmpty()||entries>Math.max(4096,diskDebt.size()*4L))compact();
        }catch(IOException error){throw new CompletionException(error);}
    }
    private void compact()throws IOException{
        if(diskDebt.isEmpty()){Files.deleteIfExists(file);entries=0;return;}
        Path tmp=file.resolveSibling(file.getFileName()+".tmp");
        try(var out=new DataOutputStream(Files.newOutputStream(tmp))){
            for(long key:diskDebt){out.writeBoolean(true);out.writeLong(key);}
        }
        Files.move(tmp,file,StandardCopyOption.ATOMIC_MOVE,StandardCopyOption.REPLACE_EXISTING);entries=diskDebt.size();
    }
    void close(){try{flush().join();}finally{io.shutdown();}}
}
