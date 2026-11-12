package maki.chunksmithcontroller;

import com.kishku7.chunksmith.GenerationTask;
import com.kishku7.chunksmith.lod.CsLodChunk;
import com.kishku7.chunksmith.lod.LodSupport;
import java.util.IdentityHashMap;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.chunk.LevelChunk;

/** Observes the native writer by record identity; owns no LOD data or reuse policy. */
public final class NativeLodTelemetry {
    private static final class Offer {
        final String token;final int x,z;boolean registered;
        Offer(String token,int x,int z){this.token=token;this.x=x;this.z=z;}
    }
    private static final ThreadLocal<Offer> CURRENT=new ThreadLocal<>();
    private static final IdentityHashMap<CsLodChunk,String> PENDING=new IdentityHashMap<>();
    private NativeLodTelemetry() {}
    public static void offer(ServerLevel level,LevelChunk chunk){
        GenerationTask owner=RequestContext.current();
        String token=owner==null?null:ResourceBudgetApi.attemptToken(level.dimension().location().toString(),chunk.getPos().x,chunk.getPos().z,owner);
        Offer previous=CURRENT.get();
        Offer offer=token==null?null:new Offer(token,chunk.getPos().x,chunk.getPos().z);
        if(offer==null)CURRENT.remove();else CURRENT.set(offer);
        try{LodSupport.offer(level,chunk);}
        catch(RuntimeException|Error error){ResourceBudgetApi.captureFailed(token);throw error;}
        finally{
            if(token!=null){
                synchronized(PENDING){
                    if(!offer.registered)ResourceBudgetApi.stageUnavailable(token,"LOD");
                }
            }
            if(previous==null)CURRENT.remove();else CURRENT.set(previous);
        }
    }
    // The native sink takes ownership here (queue or synchronous fallback).
    // Keep its credit until store.write really terminates, including queue wait.
    public static void offered(Object value){
        Offer offer=CURRENT.get();
        if(offer!=null&&value instanceof CsLodChunk record&&record.getChunkX()==offer.x&&record.getChunkZ()==offer.z){
            offer.registered=true;
            synchronized(PENDING){
                String existing=PENDING.putIfAbsent(record,offer.token);
                if(existing==null)PregenControlHooks.lodWriteStarted(offer.token);
                else if(!existing.equals(offer.token))ResourceBudgetApi.captureFailed(offer.token);
            }
        }
    }
    public static void rejected(Object value){
        if(value instanceof CsLodChunk record){
            String token;
            synchronized(PENDING){token=PENDING.remove(record);}
            if(token!=null)PregenControlHooks.lodWriteFinished(token,0L,false);
        }
    }
    public static String writing(CsLodChunk record){
        String token;
        synchronized(PENDING){token=PENDING.remove(record);}
        return token;
    }
    public static void clear(){synchronized(PENDING){PENDING.clear();}}
    public static void written(String token,long nanos,boolean success){
        if(token!=null)PregenControlHooks.lodWriteFinished(token,nanos,success);
    }
}
