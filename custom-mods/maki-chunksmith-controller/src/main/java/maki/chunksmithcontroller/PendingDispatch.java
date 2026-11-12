package maki.chunksmithcontroller;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.BooleanSupplier;
import java.util.function.Supplier;

/** Keeps one native dispatch before its LOD mark while FULL ownership I/O retries. */
final class PendingDispatch {
    private PendingDispatch() {}
    static CompletableFuture<Void> prepare(BooleanSupplier open,Supplier<CompletableFuture<Void>> preparation,Executor retry){
        CompletableFuture<Void> result=new CompletableFuture<>();
        attempt(open,preparation,retry,result,3);return result;
    }
    private static void attempt(BooleanSupplier open,Supplier<CompletableFuture<Void>> preparation,Executor retry,CompletableFuture<Void> result,int remaining){
        if(!open.getAsBoolean()){result.completeExceptionally(new IllegalStateException("Dispatch stopped before native LOD mark"));return;}
        CompletableFuture<Void> prepared;
        try{prepared=preparation.get();}catch(RuntimeException error){prepared=CompletableFuture.failedFuture(error);}
        prepared.whenComplete((ignored,error)->{
            if(!open.getAsBoolean())result.completeExceptionally(new IllegalStateException("Dispatch stopped before native LOD mark"));
            else if(error==null)result.complete(null);
            else if(remaining<=1)result.completeExceptionally(error);
            else retry.execute(()->attempt(open,preparation,retry,result,remaining-1));
        });
    }
}
