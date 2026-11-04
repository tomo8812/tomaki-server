package maki.lodcpu;

import com.google.gson.Gson;
import com.kishku7.chunksmith.*;
import com.kishku7.chunksmith.lod.LodHorizon;
import com.kishku7.chunksmith.util.Parameter;
import java.nio.file.*;
import java.lang.invoke.MethodHandle;
import java.util.UUID;
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
    private static final Logger LOG=LoggerFactory.getLogger("MakiLodFollow");
    private static final Gson JSON=new Gson();
    private static final String WORLD="minecraft:overworld";
    private static MinecraftServer owner;
    private static Saved saved;
    private static Path stateFile;
    private static long boot,emptySince,lastCheck,lastSave,lastReport,lastCenterChange,healthySince;
    private static boolean hadPlayers,manualPause,loadPause,requested,wasEnabled;
    private static String reason="starting";
    private static MethodHandle toWorld,isShipyard;
    private static boolean checkedVs,positionError;
    private static Integer appliedX,appliedZ;
    private static GenerationTask lastTask;
    static final class Saved {String player,uuid;Integer x,z;boolean disabled;long savedAt;}

    private static Settings settings(){return CpuGovernor.settings();}
    private static boolean enabled(){return CpuGovernor.healthyConfig()&&settings().followEnabled&&!saved.disabled;}
    private static void initialize(MinecraftServer server,long now){
        owner=server;boot=now;lastCheck=lastSave=lastReport=lastCenterChange=healthySince=0;
        emptySince=server.getPlayerCount()==0?now:0;hadPlayers=server.getPlayerCount()>0;
        manualPause=loadPause=false;requested=true;positionError=false;reason="startup-wait";
        appliedX=appliedZ=null;lastTask=null;
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
        wasEnabled=enabled();
        LOG.info("Following {} ({}), online {} slots, radius {}, recenter at most every {}s, idle grace {}s; saved center {},{}",settings().followPlayer,settings().followUuid,settings().generateWhileOnline?settings().onlineMax:0,settings().followRadius,settings().recenterSeconds,settings().emptyGraceSeconds,saved.x,saved.z);
    }
    private static Vec3 worldPosition(Level level,Vec3 pos)throws ReflectiveOperationException {
        if(!checkedVs){
            try{
                Class<?> vs=Class.forName("org.valkyrienskies.mod.common.VSGameUtilsKt",false,PlayerCenter.class.getClassLoader());
                MethodHandle transform=ExactStatic.find(vs,"toWorldCoordinates",Vec3.class,Level.class,Vec3.class);
                MethodHandle contains=ExactStatic.find(vs,"isBlockInShipyard",boolean.class,Level.class,Vec3.class);
                toWorld=transform;isShipyard=contains;checkedVs=true;
            }catch(ClassNotFoundException ignored){checkedVs=true;}
        }
        try {
            if(toWorld!=null&&isShipyard!=null&&(boolean)isShipyard.invokeExact(level,pos)){
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
        if(owner==player.getServer()&&saved!=null&&enabled()&&!settings().generateWhileOnline){
            capture(player);
            boolean followPaused=true;
            if(ChunksmithProvider.isLoaded())followPaused=pause(ChunksmithProvider.get());
            reason=followPaused?"players-online-paused":"external-task-active";
            save(System.currentTimeMillis());
        }
    }
    public static void shutdown(MinecraftServer server){if(owner==server&&saved!=null)save(System.currentTimeMillis());owner=null;}
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
        if(chunky==null)return true;
        GenerationTask current=chunky.getGenerationTasks().get(WORLD);
        if(current!=null&&!TaskOwnership.isOwned(current)){
            TaskOwnership.noteExternalActive();
            reason="external-task-active";
            return false;
        }
        if(current!=null&&!current.isStopping())current.stop(false);
        return true;
    }
    public static void nativePause(){manualPause=true;requested=false;reason="manual-pause";}
    public static void nativeResume(){manualPause=false;requested=true;}

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
        if(online&&!settings().generateWhileOnline){
            pause(chunky);requested=true;loadPause=false;healthySince=0;
            reason="players-online-paused";report(server,chunky,now);return;
        }
        if(now-boot<10_000||!online&&now-emptySince<settings().emptyGraceSeconds*1000L){pause(chunky);reason="grace-period";report(server,chunky,now);return;}
        if(online&&CpuGovernor.onlineOverload()){loadPause=true;healthySince=0;pause(chunky);reason="online-overload";report(server,chunky,now);return;}
        if(loadPause){
            if(!CpuGovernor.recovered())healthySince=0;else if(healthySince==0)healthySince=now;
            if(healthySince==0||now-healthySince<30_000){reason="load-recovery-wait";report(server,chunky,now);return;}
            loadPause=false;requested=true;
        }
        if(saved.x==null||saved.z==null){reason="waiting-for-tomo-position";report(server,chunky,now);return;}
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
            manualPause=true;reason="native-safety-pause";report(server,chunky,now);return;
        }
        if(current==null&&!requested&&appliedX!=null&&FollowPolicy.moved(saved.x,saved.z,appliedX,appliedZ)&&FollowPolicy.due(now,lastCenterChange,settings().recenterSeconds))requested=true;
        if(current!=null){
            var selection=current.getSelection();
            boolean moved=FollowPolicy.moved(saved.x,saved.z,(int)selection.centerX(),(int)selection.centerZ());
            if((moved||selection.radiusX()!=settings().followRadius||selection.horizon()!=1)&&FollowPolicy.due(now,lastCenterChange,settings().recenterSeconds)){
                requested=true;pause(chunky);reason="draining-before-recenter";
            }else reason="generating";
        }else if(requested&&!LodHorizon.isArmed(WORLD)){
            var world=chunky.getServer().getWorld(WORLD);
            if(world.isPresent()){
                try{
                    GenerationTask task=null;
                    if(persistedOwner==TaskOwnership.PersistedOwner.CURRENT_SESSION
                        ||persistedOwner==TaskOwnership.PersistedOwner.PREVIOUS_SESSION&&chunky.getConfig().getContinueOnRestart()){
                        var prior=chunky.getTaskLoader().loadTask(world.get());
                        if(prior.isPresent()&&!prior.get().isCancelled()){
                            var selection=prior.get().getSelection();
                            if(!FollowPolicy.moved(saved.x,saved.z,(int)selection.centerX(),(int)selection.centerZ())&&selection.radiusX()==settings().followRadius&&selection.horizon()==1){
                                task=prior.get();
                                TaskOwnership.claim(task,"PlayerCenter-persisted");
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
                        lastTask=task;appliedX=saved.x;appliedZ=saved.z;lastCenterChange=now;requested=false;reason="generating";
                        LOG.info("LOD task follows {} at {},{}; radius {}, LOD-only, {} online slots; previous LOD retained",settings().followPlayer,saved.x,saved.z,settings().followRadius,settings().onlineMax);
                    }
                }catch(Exception|LinkageError e){requested=false;manualPause=true;reason="start-failed";LOG.error("LOD follow start failed; paused until explicit resume",e);}
            }
        }else if(!requested)reason="task-paused-or-completed";
        else if(LodHorizon.isArmed(WORLD))reason="waiting-for-lod-save-protection";
        report(server,chunky,now);
    }
    private static void report(MinecraftServer server,Chunksmith chunky,long now){
        if(now-lastReport<10_000)return;lastReport=now;
        try{
            var current=chunky.getGenerationTasks().get(WORLD);
            var data=new java.util.LinkedHashMap<String,Object>();
            data.put("time",java.time.Instant.now().toString());data.put("player",settings().followPlayer);data.put("targetOnline",server.getPlayerList().getPlayer(UUID.fromString(settings().followUuid))!=null);
            data.put("centerX",saved.x);data.put("centerZ",saved.z);data.put("radius",settings().followRadius);data.put("players",server.getPlayerCount());data.put("onlineSlots",settings().generateWhileOnline?settings().onlineMax:0);data.put("generateWhileOnline",settings().generateWhileOnline);data.put("reason",reason);
            data.put("activeTaskOwner",current==null?"none":TaskOwnership.isOwned(current)?"follow":"external");
            data.put("persistedTaskOwner",TaskOwnership.status(TaskOwnership.persistedOwner(chunky,WORLD)));
            data.put("activeCenterX",current==null?null:current.getSelection().centerX());data.put("activeCenterZ",current==null?null:current.getSelection().centerZ());
            Files.writeString(Path.of("logs/maki-lod-follow-status.json"),JSON.toJson(data));
        }catch(Exception e){LOG.debug("Could not report LOD follow status",e);}
    }
    public static void register(com.mojang.brigadier.CommandDispatcher<net.minecraft.commands.CommandSourceStack> dispatcher){
        dispatcher.register(Commands.literal("makilod").requires(s->s.hasPermission(2))
            .then(Commands.literal("status").executes(c->{c.getSource().sendSuccess(()->Component.literal("LOD follows "+settings().followPlayer+"; center "+(saved==null?"pending":saved.x+","+saved.z)+"; "+reason),false);return 1;}))
            .then(Commands.literal("disable").executes(c->{if(saved==null)return 0;saved.disabled=true;pause(ChunksmithProvider.get());save(System.currentTimeMillis());return 1;}))
            .then(Commands.literal("resume").executes(c->{if(saved==null)return 0;saved.disabled=false;manualPause=false;requested=true;emptySince=0;save(System.currentTimeMillis());return 1;})));
    }
}
