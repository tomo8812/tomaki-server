package maki.lodcpu;

import com.kishku7.chunksmith.Chunksmith;
import com.kishku7.chunksmith.GenerationTask;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import maki.lodcpu.mixin.TaskLoaderAccessor;

/** Explicit administrator-only transitions for external Chunksmith overworld state. */
final class ExternalTaskMigration {
    static final String WORLD="minecraft:overworld";
    private ExternalTaskMigration(){}

    record Result(boolean ok,String message,TaskMigrationStore.Backup backup){}

    static Result backup(Chunksmith chunky,String actor){
        try{
            String blocked=preflightExternalPersisted(chunky);
            if(blocked!=null)return new Result(false,blocked,null);
            var backup=TaskMigrationStore.backup(taskFile(chunky),backupRoot(chunky),"backup",actor);
            return new Result(true,"Preserved external task as backup "+backup.id()+" (sha256 "+backup.sha256()+").",backup);
        }catch(IOException|RuntimeException e){
            return new Result(false,"Could not back up external task: "+e.getMessage(),null);
        }
    }

    static Result stash(Chunksmith chunky,String actor,String action){
        try{
            String blocked=preflightExternalPersisted(chunky);
            if(blocked!=null)return new Result(false,blocked,null);
            var backup=TaskMigrationStore.stash(taskFile(chunky),backupRoot(chunky),action,actor);
            forgetCachedTask(chunky);
            return new Result(true,"External task was preserved and removed from the live task slot as backup "+backup.id()+" (sha256 "+backup.sha256()+").",backup);
        }catch(IOException|RuntimeException e){
            return new Result(false,"Could not stash external task: "+e.getMessage(),null);
        }
    }

    static Result resumeExternal(Chunksmith chunky,String actor){
        try{
            String blocked=preflightExternalPersisted(chunky);
            if(blocked!=null)return new Result(false,blocked,null);
            var snapshot=TaskOwnership.snapshot(chunky,WORLD);
            if(Boolean.TRUE.equals(snapshot.cancelled()))
                return new Result(false,"The persisted external task is already cancelled/completed. Use /makilod task switch-follow or /makilod task stash.",null);
            var backup=TaskMigrationStore.backup(taskFile(chunky),backupRoot(chunky),"resume-external",actor);
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

    static Result restoreLatest(Chunksmith chunky,String actor){
        try{
            if(chunky.getGenerationTasks().get(WORLD)!=null)
                return new Result(false,"An active minecraft:overworld task exists; refusing to restore over it.",null);
            if(Files.exists(taskFile(chunky)))
                return new Result(false,"A live persisted task exists; stash it first rather than overwriting it.",null);
            var backup=TaskMigrationStore.restoreLatest(taskFile(chunky),backupRoot(chunky),actor);
            refreshCachedTask(chunky);
            return new Result(true,"Restored backup "+backup.id()+" to the live external task slot. Follow is not started automatically.",backup);
        }catch(IOException|RuntimeException e){
            return new Result(false,"Could not restore latest task backup: "+e.getMessage(),null);
        }
    }

    static String latestBackupId(Chunksmith chunky){
        try{return TaskMigrationStore.latestBackupId(backupRoot(chunky));}
        catch(IOException e){return null;}
    }

    static String taskSha256(Chunksmith chunky){
        try{return Files.isRegularFile(taskFile(chunky))?TaskMigrationStore.sha256(taskFile(chunky)):null;}
        catch(IOException e){return null;}
    }

    static Path taskFile(Chunksmith chunky){
        return chunky.getConfig().getDirectory().resolve("tasks").resolve("minecraft/overworld.properties");
    }

    private static Path backupRoot(Chunksmith chunky){
        return chunky.getConfig().getDirectory().resolve("tasks").resolve(".maki-lod-cpu-backups").resolve("v1").resolve("minecraft").resolve("overworld");
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
