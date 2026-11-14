package maki.chunksmithcontroller;

import java.util.function.Supplier;
import java.util.concurrent.Executor;

/** Exact native request identity, retained through asynchronous ticket safe points. */
public final class RequestContext {
    public record Request(String token,String dimension,int x,int z) {}
    private static final ThreadLocal<Request> CURRENT=new ThreadLocal<>();
    private RequestContext() {}
    public static Request current(){return CURRENT.get();}
    public static String token(String dimension,int x,int z){
        Request request=CURRENT.get();
        return request!=null&&request.dimension().equals(dimension)&&request.x()==x&&request.z()==z?request.token():null;
    }
    public static Executor wrap(Executor executor){
        Request request=CURRENT.get();
        return action->executor.execute(()->with(request,()->{action.run();return null;}));
    }
    public static <T> T with(Request request,Supplier<T> action){
        Request previous=CURRENT.get();
        if(request==null)CURRENT.remove();else CURRENT.set(request);
        try{return action.get();}
        finally{if(previous==null)CURRENT.remove();else CURRENT.set(previous);}
    }
}
