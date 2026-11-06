package maki.lodcpu;

import com.kishku7.chunksmith.Chunksmith;
import com.kishku7.chunksmith.GenerationTask;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.storage.LevelResource;
import maki.lodcpu.mixin.TaskLoaderAccessor;

/** Explicit administrator-only transitions for external Chunksmith overworld state. */
final class ExternalTaskMigration {
    static final String WORLD="minecraft:overworld";
    private ExternalTaskMigration(){}

    record Result(boolean ok,String message,TaskMigrationStore.Backup backup){}

    static Result backup(Chunksmith chunky,MinecraftServer server,String actor){
        try{
            String blocked=preflightExternalPersisted(chunky);
            if(blocked!=null)return new Result(false,blocked,null);
            var backup=TaskMigrationStore.backup(taskFile(chunky),backupRoot(server),"backup",actor);
            return new Result(true,"Preserved external task as backup "+backup.id()+" (sha256 "+backup.sha256()+").",backup);
        }catch(IOException|RuntimeException e){
            return new Result(false,"Could not back up external task: "+e.getMessage(),null);
        }
    }

    static Result stash(Chunksmith chunky,MinecraftServer server,String actor,String action){
        try{
            String blocked=preflightExternalPersisted(chunky);
            if(blocked!=null)return new Result(false,blocked,null);
            var backup=TaskMigrationStore.stash(taskFile(chunky),backupRoot(server),action,actor);
            forgetCachedTask(chunky);
            return new Result(true,"External task was preserved and removed from the live task slot as backup "+backup.id()+" (sha256 "+backup.sha256()+").",backup);
        }catch(IOException|RuntimeException e){
            return new Result(false,"Could not stash external task: "+e.getMessage(),null);
        }
    }

    static Result resumeExternal(Chunksmith chunky,MinecraftServer server,String actor){
        try{
            String blocked=preflightExternalPersisted(chunky);
            if(blocked!=null)return new Result(false,blocked,null);
            var snapshot=TaskOwnership.snapshot(chunky,WORLD);
            if(Boolean.TRUE.equals(snapshot.cancelled()))
                return new Result(false,"The persisted external task is already cancelled/completed. Use /makilod task switch-follow or /makilod task stash.",null);
            var backup=TaskMigrationStore.backup(taskFile(chunky),backupRoot(server),"resume-external",actor);
            refreshCachedTask(chunky);
            var world=chunky.getServer().getWorld(WORLD);
            if(world.isEmpty())return new Result(false,"minecraft:overworld is not available.",backup);
            var loaded=chunky.getTaskLoader().loadTask(world.get());
            if(loaded.isEmpty())return new Result(false,"Chunksmith could not reload the preserved external task.",backup);
            GenerationTask task=loaded.get();
            if(task.isCancelled())return new Result(false,"The reloaded external task is cancelled and will not be scheduled.",backup);
            GenerationTask existing=chunky.getGenerationTasks().putIfAbsent(WORLD,task);
            if(existing!=null)return new Result(false,"Another Chunksmith task became active before resume; nothing was replaced.",backup);
            try{chunky.getScheduler().runTask(task);}
            catch(RuntimeException e){chunky.getGenerationTasks().remove(WORLD,task);throw e;}
            TaskOwnership.noteExternalActive();
            return new Result(true,"Resumed the external task without adopting it. Original input is backed up as "+backup.id()+". Follow remains blocked until that external state is explicitly stashed/switched.",backup);
        }catch(IOException|RuntimeException e){
            return new Result(false,"Could not resume external task: "+e.getMessage(),null);
        }
    }

    static Result restoreLatest(Chunksmith chunky,MinecraftServer server,String actor){
        TaskMigrationStore.Backup displacedFollow=null;
        TaskMigrationStore.VerifiedBackup target=null;
        Path live=taskFile(chunky);
        try{
            if(chunky.getGenerationTasks().get(WORLD)!=null)
                return new Result(false,"An active minecraft:overworld task exists; refusing to restore over it. Stop the follow task first.",null);
            Path root=backupRoot(server);
            String targetId=TaskMigrationStore.latestBackupId(root);
            if(targetId==null)return new Result(false,"No external task backup is available.",null);
            target=TaskMigrationStore.verifyBackup(root,targetId);
            if(Files.exists(live)){
                var owner=TaskOwnership.persistedOwner(chunky,WORLD);
                if(owner==TaskOwnership.PersistedOwner.EXTERNAL)
                    return new Result(false,"A live external persisted task exists; stash it first rather than overwriting it.",null);
                if(owner==TaskOwnership.PersistedOwner.CURRENT_SESSION||owner==TaskOwnership.PersistedOwner.PREVIOUS_SESSION){
                    displacedFollow=TaskMigrationStore.stash(live,followBackupRoot(server),"restore-replaced-follow",actor);
                    forgetCachedTask(chunky);
                }
            }
            var backup=TaskMigrationStore.restore(live,root,targetId,actor);
            refreshCachedTask(chunky);
            String preserved=displacedFollow==null?"":" The displaced follow-owned state was also preserved as "+displacedFollow.id()+".";
            return new Result(true,"Restored external backup "+backup.id()+" to the live task slot."+preserved+" Follow is not started automatically.",backup);
        }catch(IOException|RuntimeException e){
            if(displacedFollow!=null&&target!=null){
                try{
                    if(Files.exists(live)&&!TaskMigrationStore.deleteIfMatches(live,target.sha256()))
                        return new Result(false,"External restore failed after the live task changed again; refusing to overwrite it. Displaced follow backup "+displacedFollow.id()+" remains preserved. Cause: "+e.getMessage(),null);
                    forgetCachedTask(chunky);
                    TaskMigrationStore.restore(live,followBackupRoot(server),displacedFollow.id(),actor+"-rollback");
                    refreshCachedTask(chunky);
                    return new Result(false,"External restore failed and the displaced follow state was rolled back: "+e.getMessage(),null);
                }catch(IOException|RuntimeException rollback){
                    return new Result(false,"External restore failed; displaced follow backup "+displacedFollow.id()+" is preserved but automatic rollback also failed: "+rollback.getMessage(),null);
                }
            }
            if(target!=null&&Files.exists(live)){
                try{
                    if(TaskMigrationStore.deleteIfMatches(live,target.sha256()))forgetCachedTask(chunky);
                }catch(IOException|RuntimeException rollback){
                    return new Result(false,"External restore failed and cleanup of the newly restored live file also failed: "+rollback.getMessage(),null);
                }
            }
            return new Result(false,"Could not restore latest task backup: "+e.getMessage(),null);
        }
    }

    static String latestBackupId(Chunksmith chunky,MinecraftServer server){
        try{return TaskMigrationStore.latestBackupId(backupRoot(server));}
        catch(IOException e){return null;}
    }

    static String taskSha256(Chunksmith chunky){
        try{return Files.isRegularFile(taskFile(chunky))?TaskMigrationStore.sha256(taskFile(chunky)):null;}
        catch(IOException e){return null;}
    }

    static Path taskFile(Chunksmith chunky){
        return chunky.getConfig().getDirectory().resolve("tasks").resolve("minecraft/overworld.properties");
    }

    private static Path backupRoot(MinecraftServer server){
        return server.getWorldPath(LevelResource.ROOT).resolve("data").resolve("maki_lod_task_backups").resolve("v1").resolve("minecraft").resolve("overworld");
    }
    private static Path followBackupRoot(MinecraftServer server){
        return server.getWorldPath(LevelResource.ROOT).resolve("data").resolve("maki_lod_task_backups").resolve("v1").resolve("minecraft").resolve("overworld-follow");
    }

    private static String preflightExternalPersisted(Chunksmith chunky){
        GenerationTask current=chunky.getGenerationTasks().get(WORLD);
        if(current!=null)
            return "An active minecraft:overworld task exists ("+(TaskOwnership.isOwned(current)?"follow-owned":"external")+
                "). No active task is stopped or replaced by migration commands; pause/finish it first.";
        var owner=TaskOwnership.persistedOwner(chunky,WORLD);
        if(owner==TaskOwnership.PersistedOwner.NONE)return "No persisted minecraft:overworld task exists.";
        if(owner!=TaskOwnership.PersistedOwner.EXTERNAL)
            return "The persisted task is follow-owned, not external; normal /makilod resume lifecycle applies.";
        return null;
    }

    private static void forgetCachedTask(Chunksmith chunky){
        ((TaskLoaderAccessor)(Object)chunky.getTaskLoader()).maki$getTasks().remove(WORLD);
    }

    private static void refreshCachedTask(Chunksmith chunky){
        forgetCachedTask(chunky);
        chunky.getTaskLoader().reload();
    }
}
