package maki.chunksmithcontroller;

import com.kishku7.chunksmith.GenerationTask;
import java.util.function.Supplier;

/** Native asynchronous request identity, propagated through ticket safe points. */
public final class RequestContext {
    private static final ThreadLocal<GenerationTask> CURRENT=new ThreadLocal<>();
    private RequestContext() {}
    public static GenerationTask current(){return CURRENT.get();}
    public static <T> T with(GenerationTask owner,Supplier<T> action){
        GenerationTask previous=CURRENT.get();
        if(owner==null)CURRENT.remove();else CURRENT.set(owner);
        try{return action.get();}
        finally{if(previous==null)CURRENT.remove();else CURRENT.set(previous);}
    }
}
