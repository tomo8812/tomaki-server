package maki.chunksmithcontroller;

import com.google.gson.Gson;
import com.kishku7.chunksmith.*;
import com.kishku7.chunksmith.lod.LodHorizon;
import com.kishku7.chunksmith.util.Parameter;
import java.nio.file.*;
import java.lang.invoke.MethodHandle;
import java.util.UUID;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicLong;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.nbt.*;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.LevelResource;
import net.minecraft.world.phys.Vec3;
import org.slf4j.*;

/** Owns only the ordinary overworld task. Native generation, LOD storage and safety remain intact. */
public final class PlayerCenter {
    private static final Logger LOG=LoggerFactory.getLogger("MakiChunksmithController");
    private static final Gson JSON=new Gson();
    private static final String WORLD="minecraft:overworld";
    private static volatile MinecraftServer owner;
    private static volatile Saved saved;
    private static Path stateFile;
    private static long boot,emptySince,lastCheck,lastSave,lastReport,lastCenterChange,lastStateSummary;
    private static String lastLoggedState;
    private static boolean hadPlayers;
    private static volatile boolean manualPause,requested,wasEnabled;
    private static final AtomicLong ADMISSION_EPOCH=new AtomicLong();
    private static volatile boolean admissionClosed=true;
    private static String reason="starting";
    private static MethodHandle toWorld,isShipyard;
    private static boolean checkedVs,positionError;
    private static Integer appliedX,appliedZ;
    private static GenerationTask lastTask;
    private static final ConcurrentLinkedQueue<NativeTaskLifecycle> nativeTaskLifecycle=new ConcurrentLinkedQueue<>();
    private static volatile GenerationTask controllerStopTask;
    private record NativeTaskLifecycle(GenerationTask task,boolean started,boolean cancelled){}
    static final class Saved {String player,uuid;Integer x,z;boolean disabled;long savedAt;}

    private static Settings settings(){return CpuGovernor.settings();}
    private static boolean enabled(){return saved!=null&&CpuGovernor.healthyConfig()&&settings().followEnabled&&!saved.disabled;}

    private static void resetAdmission(){
        admissionClosed=true;
        ADMISSION_EPOCH.incrementAndGet();
    }
    private static void closeAdmission(){
        if(!admissionClosed){admissionClosed=true;ADMISSION_EPOCH.incrementAndGet();}
    }
    private static boolean admissionPolicyAllows(GenerationTask task){
        MinecraftServer server=owner;
        Saved state=saved;
        if(server==null||state==null||task==null||admissionClosed||!enabled()||manualPause
            ||server.getPlayerCount()>0||task.isStopping()||!TaskOwnership.isOwned(task))return false;
        try{
            if(!ChunksmithProvider.isLoaded()||ChunksmithProvider.get()==null
                ||ChunksmithProvider.get().getGenerationTasks().get(WORLD)!=task)return false;
            var selection=task.getSelection();
            return state.x!=null&&state.z!=null
                &&(int)selection.centerX()==state.x&&(int)selection.centerZ()==state.z
                &&selection.radiusX()==settings().followRadius&&selection.horizon()==1;
        }catch(LinkageError|RuntimeException error){return false;}
    }
    private static void openAdmission(GenerationTask task){
        if(task==null)return;
        // Evaluate with the gate temporarily open; the dynamic policy is checked
        // again by admissionOpen on every async candidate/final-guard boundary.
        admissionClosed=false;
        if(!admissionPolicyAllows(task))admissionClosed=true;
    }
    public static long admissionEpoch(GenerationTask task){
        return task!=null&&TaskOwnership.isOwned(task)?ADMISSION_EPOCH.get():-1L;
    }
    public static boolean admissionOpen(GenerationTask task){return admissionPolicyAllows(task);}
    private static void initialize(MinecraftServer server,long now){
        owner=server;resetAdmission();boot=now;lastCheck=lastSave=lastReport=lastCenterChange=lastStateSummary=0;lastLoggedState=null;
        emptySince=server.getPlayerCount()==0?now:0;hadPlayers=server.getPlayerCount()>0;
        manualPause=false;requested=true;positionError=false;reason="startup-wait";
        appliedX=appliedZ=null;lastTask=null;nativeTaskLifecycle.clear();controllerStopTask=null;
        stateFile=server.getWorldPath(LevelResource.ROOT).resolve("data/maki_lod_follow.json");
        saved=new Saved();saved.player=settings().followPlayer;saved.uuid=settings().followUuid;
        try{
            if(Files.exists(stateFile)){
                Saved old=JSON.fromJson(Files.readString(stateFile),Saved.class);
                if(old!=null&&settings().followUuid.equals(old.uuid)){
                    saved.disabled=old.disabled;
                    if(old.x!=null&&old.z!=null){FollowPolicy.center(old.x);FollowPolicy.center(old.z);saved=old;}
                }
            }
            Path player=server.getWorldPath(LevelResource.ROOT).resolve("playerdata").resolve(settings().followUuid+".dat");
            if(Files.exists(player)&&Files.getLastModifiedTime(player).toMillis()>=saved.savedAt){
                var tag=NbtIo.readCompressed(player,NbtAccounter.create(32L*1024*1024));
                if(tag.getString("Dimension").equals(WORLD)){
                    var pos=tag.getList("Pos",6);
                    if(pos.size()==3)accept(server.overworld(),new Vec3(pos.getDouble(0),pos.getDouble(1),pos.getDouble(2)));
                }
            }
        }catch(Exception|LinkageError e){LOG.warn("Could not load latest follow position; waiting for the named player when necessary",e);}
        if(saved.x==null||saved.z==null){saved.x=0;saved.z=0;LOG.info("No saved tracking coordinate is available; unified pregen starts from world center 0,0.");}
        wasEnabled=enabled();
        LOG.info("Following {} ({}), empty-server pregen only, radius {}, recenter at most every {}s, idle grace {}s; saved center {},{}",settings().followPlayer,settings().followUuid,settings().followRadius,settings().recenterSeconds,settings().emptyGraceSeconds,saved.x,saved.z);
    }
    private static Vec3 worldPosition(Level level,Vec3 pos)throws ReflectiveOperationException {
        if(!checkedVs){
            Class<?> vs=Class.forName("org.valkyrienskies.mod.common.VSGameUtilsKt",false,PlayerCenter.class.getClassLoader());
            toWorld=ExactStatic.find(vs,"toWorldCoordinates",Vec3.class,Level.class,Vec3.class);
            isShipyard=ExactStatic.find(vs,"isBlockInShipyard",boolean.class,Level.class,Vec3.class);
            checkedVs=true;
        }
        try {
            if((boolean)isShipyard.invokeExact(level,pos)){
                pos=(Vec3)toWorld.invokeExact(level,pos);
                if((boolean)isShipyard.invokeExact(level,pos))throw new IllegalStateException("Ship world transform is not available yet");
            }
        } catch(Throwable error) {
            if(error instanceof Error fatal)throw fatal;
            throw new ReflectiveOperationException("Could not resolve the player's ship-to-world position",error);
        }
        return pos;
    }
    private static void accept(Level level,Vec3 raw)throws ReflectiveOperationException {
        Vec3 pos=worldPosition(level,raw);
        int x=FollowPolicy.center(pos.x),z=FollowPolicy.center(pos.z);
        saved.x=x;saved.z=z;saved.player=settings().followPlayer;saved.uuid=settings().followUuid;
    }
    private static void capture(ServerPlayer player){
        if(!player.getUUID().toString().equals(settings().followUuid)||player.level().dimension()!=Level.OVERWORLD)return;
        try{accept(player.level(),player.position());positionError=false;}
        catch(Exception|LinkageError e){if(!positionError){LOG.warn("Keeping the previous LOD center until this player's world position is available",e);positionError=true;}}
    }
    public static void logout(ServerPlayer player){if(owner==player.getServer()&&saved!=null){capture(player);save(System.currentTimeMillis());}}
    public static void login(ServerPlayer player){
        if(owner==player.getServer()&&saved!=null&&enabled()){
            closeAdmission();
            capture(player);
            boolean followPaused=true;
            if(ChunksmithProvider.isLoaded())followPaused=pause(ChunksmithProvider.get());
            reason=followPaused?"players-online-paused":"external-task-active";
            save(System.currentTimeMillis());
        }
    }
    public static void shutdown(MinecraftServer server){if(owner==server&&saved!=null)save(System.currentTimeMillis());closeAdmission();PregenFlowController.clear();owner=null;nativeTaskLifecycle.clear();controllerStopTask=null;}
    private static void save(long now){
        if(stateFile==null)return;
        try{
            saved.savedAt=now;Files.createDirectories(stateFile.getParent());
            Path tmp=stateFile.resolveSibling(stateFile.getFileName()+".tmp");Files.writeString(tmp,JSON.toJson(saved));
            try{Files.move(tmp,stateFile,StandardCopyOption.ATOMIC_MOVE,StandardCopyOption.REPLACE_EXISTING);}
            catch(AtomicMoveNotSupportedException e){Files.move(tmp,stateFile,StandardCopyOption.REPLACE_EXISTING);}
            lastSave=now;
        }catch(Exception e){LOG.warn("Could not persist LOD follow center",e);}
    }
    private static boolean pause(Chunksmith chunky){
        closeAdmission();
        if(chunky==null)return true;
        GenerationTask current=chunky.getGenerationTasks().get(WORLD);
        if(current!=null&&!TaskOwnership.isOwned(current)){
            TaskOwnership.noteExternalActive();
            reason="external-task-active";
            return false;
        }
        if(current!=null&&!current.isStopping()){
            controllerStopTask=current;
            try{current.stop(false);}finally{controllerStopTask=null;}
        }
        return true;
    }
    public static void nativeTaskStarted(GenerationTask task){
        if(task!=null&&TaskOwnership.isOwned(task)){
            nativeTaskLifecycle.add(new NativeTaskLifecycle(task,true,false));
            openAdmission(task);
        }
    }
    public static void nativeTaskStopping(GenerationTask task,boolean cancelled){
        if(task!=null&&TaskOwnership.isOwned(task)){
            closeAdmission();
            if(task!=controllerStopTask)nativeTaskLifecycle.add(new NativeTaskLifecycle(task,false,cancelled));
        }
    }
    private static void markManualPause(){closeAdmission();manualPause=true;requested=false;reason="manual-pause";}
    private static boolean sameTaskSelection(GenerationTask first,GenerationTask second){
        if(first==null||second==null)return false;
        var a=first.getSelection();var b=second.getSelection();
        return WORLD.equals(a.world().getName())&&WORLD.equals(b.world().getName())
            &&Double.compare(a.centerX(),b.centerX())==0&&Double.compare(a.centerZ(),b.centerZ())==0
            &&Double.compare(a.radiusX(),b.radiusX())==0&&Double.compare(a.radiusZ(),b.radiusZ())==0
            &&Double.compare(a.horizon(),b.horizon())==0&&a.shape().equals(b.shape())
            &&a.pattern().toString().equals(b.pattern().toString());
    }
    private static void applyNativeTaskLifecycle(){
        NativeTaskLifecycle event;
        while((event=nativeTaskLifecycle.poll())!=null){
            GenerationTask task=event.task();
            if(!WORLD.equals(task.getSelection().world().getName())||!TaskOwnership.isOwned(task))continue;
            if(!event.started()){
                if(task==lastTask||event.cancelled()&&(lastTask==null||sameTaskSelection(lastTask,task)))markManualPause();
            }else if(manualPause&&sameTaskSelection(lastTask,task)){
                lastTask=task;
                appliedX=(int)task.getSelection().centerX();appliedZ=(int)task.getSelection().centerZ();
                manualPause=false;requested=false;reason="generating";
            }
        }
    }

    public static void tick(MinecraftServer server){
        long now=System.currentTimeMillis();
        if(owner!=server)initialize(server,now);
        if(now-lastCheck<1000)return;lastCheck=now;
        if(!settings().followUuid.equals(saved.uuid))initialize(server,now);
        var player=server.getPlayerList().getPlayer(UUID.fromString(settings().followUuid));
        if(player!=null)capture(player);
        if(now-lastSave>=10_000)save(now);
        if(!ChunksmithProvider.isLoaded())return;
        var chunky=ChunksmithProvider.get();if(chunky==null)return;
        applyNativeTaskLifecycle();
        boolean online=server.getPlayerCount()>0,active=enabled();
        if(hadPlayers&&!online){emptySince=now;requested=true;pause(chunky);}
        if(!hadPlayers&&online){requested=true;emptySince=0;}
        hadPlayers=online;
        GenerationTask observedTask=chunky.getGenerationTasks().get(WORLD);
        if(observedTask!=null&&!TaskOwnership.isOwned(observedTask)){
            TaskOwnership.noteExternalActive();
            reason="external-task-active";
            report(server,chunky,now);
            return;
        }
        if(!active){if(wasEnabled)pause(chunky);wasEnabled=false;reason="disabled";report(server,chunky,now);return;}
        if(!wasEnabled){requested=true;wasEnabled=true;}
        if(manualPause){reason="manual-pause";report(server,chunky,now);return;}
        if(online){
            pause(chunky);requested=true;
            reason="players-online-paused";report(server,chunky,now);return;
        }
        if(!CpuGovernor.pregenReady(server)){reason="waiting-cache-ready";report(server,chunky,now);return;}
        String pipelinePressure=CpuGovernor.pipelinePressure();
        if(pipelinePressure!=null){
            pause(chunky);requested=true;
            reason="pipeline-pressure:"+pipelinePressure;report(server,chunky,now);return;
        }
        if(now-boot<10_000||!online&&now-emptySince<settings().emptyGraceSeconds*1000L){pause(chunky);reason="grace-period";report(server,chunky,now);return;}
        var current=chunky.getGenerationTasks().get(WORLD);
        TaskOwnership.PersistedOwner persistedOwner=current==null?TaskOwnership.persistedOwner(chunky,WORLD):TaskOwnership.PersistedOwner.NONE;
        if(current==null&&persistedOwner==TaskOwnership.PersistedOwner.EXTERNAL){
            TaskOwnership.noteExternalPersisted();
            requested=true;
            reason="external-task-persisted";
            report(server,chunky,now);
            return;
        }
        TaskOwnership.noteOwnedOrAbsent();
        if(current==null&&!requested&&lastTask!=null&&lastTask.getCount()<lastTask.getChunkIterator().total()){
            // E-lite parks before native dispatch, so it never advances the
            // Chunksmith progress for work merely waiting on admission.
            // An interrupted native run is a native pause, not retry debt.
            manualPause=true;reason="native-safety-pause";report(server,chunky,now);return;
        }
        if(current==null&&!requested&&lastTask!=null&&lastTask.getCount()>=lastTask.getChunkIterator().total()&&CpuGovernor.fullWorkPending(server,saved.x,saved.z,settings().followRadius)){
            reason="draining-full-cache";report(server,chunky,now);return;
        }
        if(current==null&&!requested&&appliedX!=null&&FollowPolicy.moved(saved.x,saved.z,appliedX,appliedZ)&&FollowPolicy.due(now,lastCenterChange,settings().recenterSeconds))requested=true;
        if(current==null&&!requested&&lastTask!=null){
            int incomplete=PregenFlowController.incompleteInRange(WORLD,saved.x,saved.z,settings().followRadius);
            if(incomplete>0){reason="incomplete-pregen:"+incomplete;report(server,chunky,now);return;}
        }
        if(current!=null){
            var selection=current.getSelection();
            boolean moved=FollowPolicy.moved(saved.x,saved.z,(int)selection.centerX(),(int)selection.centerZ());
            if((moved||selection.radiusX()!=settings().followRadius||selection.horizon()!=1)&&FollowPolicy.due(now,lastCenterChange,settings().recenterSeconds)){
                requested=true;pause(chunky);reason="draining-before-recenter";
            }else {
                openAdmission(current);
                reason="generating";
            }
        }else if(requested&&!LodHorizon.isArmed(WORLD)){
            var world=chunky.getServer().getWorld(WORLD);
            if(world.isPresent()){
                try{
                    GenerationTask task=null;
                    // Explicit resume can revisit isolated preparation/native failures through
                    // an ordinary fresh Chunksmith scan, without an independent generator.
                    if(!PregenFlowController.hasScanFailures(WORLD)
                        &&(persistedOwner==TaskOwnership.PersistedOwner.CURRENT_SESSION
                        ||persistedOwner==TaskOwnership.PersistedOwner.PREVIOUS_SESSION&&chunky.getConfig().getContinueOnRestart())){
                        var prior=chunky.getTaskLoader().loadTask(world.get());
                        if(prior.isPresent()&&!prior.get().isCancelled()){
                            var selection=prior.get().getSelection();
                            if(!FollowPolicy.moved(saved.x,saved.z,(int)selection.centerX(),(int)selection.centerZ())&&selection.radiusX()==settings().followRadius&&selection.horizon()==1){
                                if(persistedOwner==TaskOwnership.PersistedOwner.PREVIOUS_SESSION&&chunky.getConfig().getContinueOnRestart()){
                                    task=new GenerationTask(chunky,selection);
                                    TaskOwnership.claim(task,"PlayerCenter-persisted-rewind");
                                    LOG.info("Previous-session follow task will rescan from the selection start so incomplete FULL+LOD pairs can be retried; confirmed pairs remain skippable.");
                                }else{
                                    task=prior.get();
                                    TaskOwnership.claim(task,"PlayerCenter-persisted");
                                }
                            }
                        }
                    }else if(persistedOwner==TaskOwnership.PersistedOwner.PREVIOUS_SESSION){
                        TaskOwnership.noteFreshRestart();
                    }
                    if(task==null){
                        var selection=Selection.builder(chunky,world.get()).center(saved.x,saved.z).radius(settings().followRadius).radiusZ(settings().followRadius).shape("circle").pattern(Parameter.of("concentric")).horizon(1).build();
                        if(selection.radiusX()>chunky.getLimit())throw new IllegalStateException("Configured radius exceeds Chunksmith's limit");
                        task=new GenerationTask(chunky,selection);
                        TaskOwnership.claim(task,"PlayerCenter-new");
                    }
                    if(chunky.getGenerationTasks().putIfAbsent(WORLD,task)==null){
                        try{chunky.getScheduler().runTask(task);}catch(RuntimeException e){chunky.getGenerationTasks().remove(WORLD,task);throw e;}
                        lastTask=task;appliedX=saved.x;appliedZ=saved.z;lastCenterChange=now;requested=false;openAdmission(task);reason="generating";
                        LOG.info("Unified pregen follows {} at {},{}; radius {}, concentric FULL+LOD completion flow; previous confirmed data retained",settings().followPlayer,saved.x,saved.z,settings().followRadius);
                    }
                }catch(Exception|LinkageError e){requested=false;manualPause=true;reason="start-failed";LOG.error("LOD follow start failed; paused until explicit resume",e);}
            }
        }else if(!requested)reason="task-paused-or-completed";
        else if(LodHorizon.isArmed(WORLD))reason="waiting-for-lod-save-protection";
        report(server,chunky,now);
    }
    private static void report(MinecraftServer server,Chunksmith chunky,long now){
        if(now-lastReport<1_000L)return;lastReport=now;
        try{
            var current=chunky.getGenerationTasks().get(WORLD);
            var persistedOwner=TaskOwnership.persistedOwner(chunky,WORLD);
            int players=server.getPlayerCount();
            boolean onlineBlocked=players>0;
            boolean graceBlocked=now-boot<10_000L||players==0&&now-emptySince<settings().emptyGraceSeconds*1000L;
            boolean policyReady=enabled()&&!manualPause&&!onlineBlocked&&!graceBlocked&&saved.x!=null&&saved.z!=null;
            boolean canRun=policyReady&&(
                current!=null&&TaskOwnership.isOwned(current)&&!current.isStopping()
                ||current==null&&requested&&persistedOwner!=TaskOwnership.PersistedOwner.EXTERNAL&&!LodHorizon.isArmed(WORLD));
            String activeOwner=current==null?"none":TaskOwnership.isOwned(current)?"follow":"external";
            String state=reason+"|"+activeOwner+"|"+TaskOwnership.status(persistedOwner)+"|"+requested+"|"+manualPause+"|"+canRun+"|"+players+"|"+saved.x+"|"+saved.z;
            if(!state.equals(lastLoggedState)){
                LOG.info("LOD follow state changed: reason={}, activeOwner={}, persistedOwner={}, requested={}, manualPause={}, canRun={}, players={}, center={},{}",
                    reason,activeOwner,TaskOwnership.status(persistedOwner),requested,manualPause,canRun,players,saved.x,saved.z);
                lastLoggedState=state;
                lastStateSummary=now;
            }else if(now-lastStateSummary>=60_000L){
                LOG.info("LOD follow summary: reason={}, activeOwner={}, persistedOwner={}, canRun={}, players={}, center={},{}",
                    reason,activeOwner,TaskOwnership.status(persistedOwner),canRun,players,saved.x,saved.z);
                lastStateSummary=now;
            }
        }catch(Exception e){LOG.warn("Could not summarize LOD follow state; controller continues with the last valid state",e);}
    }
    private static String actor(net.minecraft.commands.CommandSourceStack source){
        return source.getTextName();
    }
    private static Chunksmith commandChunksmith(net.minecraft.commands.CommandSourceStack source){
        if(!ChunksmithProvider.isLoaded()){
            source.sendFailure(Component.literal("Chunksmith is not loaded."));
            return null;
        }
        Chunksmith chunky=ChunksmithProvider.get();
        if(chunky==null)source.sendFailure(Component.literal("Chunksmith is not ready."));
        return chunky;
    }
    private static int commandStatus(net.minecraft.commands.CommandSourceStack source){
        if(saved==null){
            source.sendFailure(Component.literal("LOD follow controller is not initialized yet."));
            return 0;
        }
        source.sendSuccess(()->Component.literal("LOD follows "+settings().followPlayer+"; center "+(saved.x==null?"pending":saved.x+","+saved.z)+"; reason="+reason+"; requested="+requested+"; manualPause="+manualPause),false);
        return commandTaskStatus(source);
    }
    private static int commandTaskStatus(net.minecraft.commands.CommandSourceStack source){
        Chunksmith chunky=commandChunksmith(source);if(chunky==null)return 0;
        GenerationTask current=chunky.getGenerationTasks().get(WORLD);
        var persistedOwner=TaskOwnership.persistedOwner(chunky,WORLD);
        var snapshot=TaskOwnership.snapshot(chunky,WORLD);
        String activeOwner=current==null?"none":TaskOwnership.isOwned(current)?"follow":"external";
        source.sendSuccess(()->Component.literal("Chunksmith overworld: activeOwner="+activeOwner+", persistedOwner="+TaskOwnership.status(persistedOwner)),false);
        if(snapshot.present()){
            String modified=snapshot.modifiedAt()>=0?java.time.Instant.ofEpochMilli(snapshot.modifiedAt()).toString():"unknown";
            String details="persisted: cancelled="+snapshot.cancelled()+", chunks="+snapshot.chunks()+", time="+snapshot.time()
                +", center="+snapshot.centerX()+","+snapshot.centerZ()+", radius="+snapshot.radius()+"/"+snapshot.radiusZ()
                +", horizon="+snapshot.lodHorizon()+", shape="+snapshot.shape()+", pattern="+snapshot.pattern()
                +", modifiedAt="+modified+", sha256="+snapshot.sha256();
            source.sendSuccess(()->Component.literal(details),false);
        }
        String latest=ExternalTaskMigration.latestBackupId(chunky,source.getServer());
        if(latest!=null)source.sendSuccess(()->Component.literal("latest migration backup: "+latest),false);
        if(current!=null&&!TaskOwnership.isOwned(current)){
            source.sendSuccess(()->Component.literal("External active task is protected. Let it finish or pause it with Chunksmith first; maki-lod will not stop or replace it."),false);
        }else if(current==null&&persistedOwner==TaskOwnership.PersistedOwner.EXTERNAL){
            String choices=Boolean.TRUE.equals(snapshot.cancelled())
                ?"Safe choices: /makilod task backup, /makilod task stash, /makilod task switch-follow, /makilod task restore-latest (after stash)."
                :"Safe choices: /makilod task backup, /makilod task resume-external, /makilod task stash, /makilod task switch-follow.";
            source.sendSuccess(()->Component.literal(choices),false);
        }
        return 1;
    }
    private static int commandResume(net.minecraft.commands.CommandSourceStack source){
        if(saved==null){
            source.sendFailure(Component.literal("LOD follow controller is not initialized yet."));
            return 0;
        }
        Chunksmith chunky=commandChunksmith(source);if(chunky==null)return 0;
        GenerationTask current=chunky.getGenerationTasks().get(WORLD);
        if(current!=null&&!TaskOwnership.isOwned(current)){
            source.sendFailure(Component.literal("Follow resume blocked: an external minecraft:overworld task is active and will not be stopped or adopted."));
            source.sendSuccess(()->Component.literal("Use /makilod task status; let the external task finish or pause it with Chunksmith before choosing a migration action."),false);
            return 0;
        }
        var persistedOwner=TaskOwnership.persistedOwner(chunky,WORLD);
        if(current==null&&persistedOwner==TaskOwnership.PersistedOwner.EXTERNAL){
            var snapshot=TaskOwnership.snapshot(chunky,WORLD);
            source.sendFailure(Component.literal("Follow resume blocked by protected external persisted state (chunks="+snapshot.chunks()+", center="+snapshot.centerX()+","+snapshot.centerZ()+", sha256="+snapshot.sha256()+"). No state was overwritten."));
            source.sendSuccess(()->Component.literal(Boolean.TRUE.equals(snapshot.cancelled())
                ?"Use /makilod task backup, stash, or switch-follow."
                :"Use /makilod task resume-external to continue it, or /makilod task switch-follow to preserve it and start follow."),false);
            return 0;
        }
        closeAdmission();saved.disabled=false;manualPause=false;requested=true;emptySince=0;save(System.currentTimeMillis());
        source.sendSuccess(()->Component.literal("LOD follow resume requested. This does not assert generation has started; use /makilod status to confirm activeTaskOwner=follow and current blockers."),false);
        return 1;
    }
    private static int commandBackup(net.minecraft.commands.CommandSourceStack source){
        Chunksmith chunky=commandChunksmith(source);if(chunky==null)return 0;
        var result=ExternalTaskMigration.backup(chunky,source.getServer(),actor(source));
        if(!result.ok()){source.sendFailure(Component.literal(result.message()));return 0;}
        source.sendSuccess(()->Component.literal(result.message()),false);return 1;
    }
    private static int commandResumeExternal(net.minecraft.commands.CommandSourceStack source){
        Chunksmith chunky=commandChunksmith(source);if(chunky==null)return 0;
        var result=ExternalTaskMigration.resumeExternal(chunky,source.getServer(),actor(source));
        if(!result.ok()){source.sendFailure(Component.literal(result.message()));return 0;}
        closeAdmission();reason="external-task-active";
        source.sendSuccess(()->Component.literal(result.message()),false);return 1;
    }
    private static int commandStash(net.minecraft.commands.CommandSourceStack source){
        if(saved==null){source.sendFailure(Component.literal("LOD follow controller is not initialized yet."));return 0;}
        Chunksmith chunky=commandChunksmith(source);if(chunky==null)return 0;
        var result=ExternalTaskMigration.stash(chunky,source.getServer(),actor(source),"stash");
        if(!result.ok()){source.sendFailure(Component.literal(result.message()));return 0;}
        closeAdmission();manualPause=true;requested=false;reason="external-task-stashed";save(System.currentTimeMillis());
        source.sendSuccess(()->Component.literal(result.message()+" Follow remains paused; use /makilod resume when ready."),false);return 1;
    }
    private static int commandSwitchFollow(net.minecraft.commands.CommandSourceStack source){
        if(saved==null){source.sendFailure(Component.literal("LOD follow controller is not initialized yet."));return 0;}
        Chunksmith chunky=commandChunksmith(source);if(chunky==null)return 0;
        var result=ExternalTaskMigration.stash(chunky,source.getServer(),actor(source),"switch-follow");
        if(!result.ok()){source.sendFailure(Component.literal(result.message()));return 0;}
        closeAdmission();saved.disabled=false;manualPause=false;requested=true;emptySince=0;reason="follow-switch-requested";save(System.currentTimeMillis());
        source.sendSuccess(()->Component.literal(result.message()+" Follow is now requested, not assumed started. Confirm /makilod status shows activeTaskOwner=follow and runtime progress."),false);return 1;
    }
    private static int commandRestoreLatest(net.minecraft.commands.CommandSourceStack source){
        if(saved==null){source.sendFailure(Component.literal("LOD follow controller is not initialized yet."));return 0;}
        Chunksmith chunky=commandChunksmith(source);if(chunky==null)return 0;
        var result=ExternalTaskMigration.restoreLatest(chunky,source.getServer(),actor(source));
        if(!result.ok()){source.sendFailure(Component.literal(result.message()));return 0;}
        closeAdmission();manualPause=true;requested=false;reason="external-task-restored";save(System.currentTimeMillis());
        source.sendSuccess(()->Component.literal(result.message()+" Follow is paused so the restored external state cannot be overwritten."),false);return 1;
    }

    public static void register(com.mojang.brigadier.CommandDispatcher<net.minecraft.commands.CommandSourceStack> dispatcher){
        dispatcher.register(Commands.literal("makilod").requires(s->s.hasPermission(2))
            .then(Commands.literal("status").executes(c->commandStatus(c.getSource())))
            .then(Commands.literal("disable").executes(c->{if(saved==null)return 0;saved.disabled=true;pause(ChunksmithProvider.get());save(System.currentTimeMillis());c.getSource().sendSuccess(()->Component.literal("LOD follow disabled; external tasks, if any, were not stopped."),false);return 1;}))
            .then(Commands.literal("resume").executes(c->commandResume(c.getSource())))
            .then(Commands.literal("task")
                .then(Commands.literal("status").executes(c->commandTaskStatus(c.getSource())))
                .then(Commands.literal("backup").executes(c->commandBackup(c.getSource())))
                .then(Commands.literal("resume-external").executes(c->commandResumeExternal(c.getSource())))
                .then(Commands.literal("stash").executes(c->commandStash(c.getSource())))
                .then(Commands.literal("switch-follow").executes(c->commandSwitchFollow(c.getSource())))
                .then(Commands.literal("restore-latest").executes(c->commandRestoreLatest(c.getSource())))));
    }
}
