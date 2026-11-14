package maki.chunksmithcontroller;

import com.kishku7.chunksmith.Chunksmith;
import com.kishku7.chunksmith.GenerationTask;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Collections;
import java.util.Properties;
import java.util.Set;
import java.util.UUID;
import java.util.WeakHashMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** Tracks which Chunksmith tasks are actually owned by the follow controller. */
public final class TaskOwnership {
    private static final Logger LOG=LoggerFactory.getLogger("MakiChunksmithController");
    private static final String OWNER_KEY="maki-chunksmith-controller-owner";
    private static final String LEGACY_OWNER_KEY="maki-lod-cpu-owner";
    private static final String OWNER_PREFIX="follow:";
    private static final String SESSION_OWNER=OWNER_PREFIX+UUID.randomUUID();
    private static final Set<GenerationTask> OWNED=Collections.newSetFromMap(Collections.synchronizedMap(new WeakHashMap<>()));
    private static boolean loggedExternalActive,loggedExternalPersisted,loggedFreshRestart;

    public enum PersistedOwner {NONE,EXTERNAL,CURRENT_SESSION,PREVIOUS_SESSION}
    private TaskOwnership(){}

    public static void claim(GenerationTask task,String source){
        if(task!=null&&OWNED.add(task))LOG.debug("Claimed follow task ownership from {}",source);
    }
    public static boolean isOwned(GenerationTask task){return task!=null&&OWNED.contains(task);}

    public static PersistedOwner persistedOwner(Chunksmith chunky,String world){
        Path path=taskFile(chunky,world);
        if(!Files.isRegularFile(path))return PersistedOwner.NONE;
        String owner=readOwner(path);
        if(owner==null)return PersistedOwner.EXTERNAL;
        return SESSION_OWNER.equals(owner)?PersistedOwner.CURRENT_SESSION:PersistedOwner.PREVIOUS_SESSION;
    }

    public static void markSaved(GenerationTask task){
        if(!isOwned(task))return;
        String world=task.getSelection().world().getName();
        Path path=taskFile(task.getChunky(),world);
        if(!Files.isRegularFile(path))return;
        try{Files.writeString(path,OWNER_KEY+"="+SESSION_OWNER+"\n",StandardOpenOption.APPEND);}
        catch(IOException e){LOG.error("Could not persist follow task ownership for {}; it will be treated as external after restart for safety",world,e);}
    }

    public static void noteExternalActive(){
        if(!loggedExternalActive){
            loggedExternalActive=true;
            LOG.warn("LOD follow is waiting: an active minecraft:overworld Chunksmith task is external/unowned and will not be stopped, recentered, or CPU-capped by maki-chunksmith-controller.");
        }
        loggedExternalPersisted=false;
    }
    public static void noteExternalPersisted(){
        if(!loggedExternalPersisted){
            loggedExternalPersisted=true;
            LOG.warn("LOD follow is waiting: persisted minecraft:overworld Chunksmith state has no Maki Chunksmith Controller ownership marker and will not be resumed, replaced, or overwritten.");
        }
        loggedExternalActive=false;
    }
    public static void noteOwnedOrAbsent(){loggedExternalActive=false;loggedExternalPersisted=false;}
    public static void noteFreshRestart(){
        if(!loggedFreshRestart){
            loggedFreshRestart=true;
            LOG.info("Previous-session follow task state found, but Chunksmith continueOnRestart=false; starting a fresh follow task instead of resuming it.");
        }
    }
    public static String status(PersistedOwner owner){
        return switch(owner){case NONE->"none";case EXTERNAL->"external";case CURRENT_SESSION,PREVIOUS_SESSION->"follow";};
    }

    private static Path taskFile(Chunksmith chunky,String world){
        return chunky.getConfig().getDirectory().resolve("tasks").resolve(world.replace(':','/')+".properties");
    }
    private static String readOwner(Path path){
        Properties props=new Properties();
        try(InputStream in=Files.newInputStream(path)){props.load(in);}
        catch(IOException|RuntimeException e){return null;}
        String value=props.getProperty(OWNER_KEY,props.getProperty(LEGACY_OWNER_KEY));
        return value!=null&&value.startsWith(OWNER_PREFIX)&&value.length()>OWNER_PREFIX.length()?value:null;
    }
}
