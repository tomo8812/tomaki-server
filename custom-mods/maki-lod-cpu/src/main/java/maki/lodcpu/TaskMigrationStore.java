package maki.lodcpu;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.HexFormat;
import java.util.UUID;
import java.util.stream.Stream;
import java.util.logging.Level;
import java.util.logging.Logger;

/** Durable, versioned preservation of runtime Chunksmith task files. */
final class TaskMigrationStore {
    static final int SCHEMA_VERSION=1;
    private static final Logger LOG=Logger.getLogger("MakiLodFollow");
    private static final DateTimeFormatter ID_TIME=DateTimeFormatter.ofPattern("yyyyMMdd'T'HHmmss.SSSX").withZone(ZoneOffset.UTC);
    private TaskMigrationStore(){}

    record Backup(String id,Path directory,Path payload,String sha256,long sourceModifiedAt,String action,String actor,String createdAt){}

    static Backup backup(Path source,Path root,String action,String actor)throws IOException {
        if(!Files.isRegularFile(source))throw new IOException("No persisted task file exists: "+source);
        byte[] input=Files.readAllBytes(source);
        long modified=Files.getLastModifiedTime(source).toMillis();
        String hash=sha256(input);
        String created=Instant.now().toString();
        String id=ID_TIME.format(Instant.now())+"-"+safe(action)+"-"+hash.substring(0,12)+"-"+UUID.randomUUID().toString().substring(0,8);
        Path directory=root.resolve(id);
        Files.createDirectories(directory);
        Path payload=directory.resolve("task.snapshot");
        writeAtomic(payload,input);
        String copied=sha256(Files.readAllBytes(payload));
        if(!hash.equals(copied))throw new IOException("Backup hash verification failed for "+payload);
        Path metadata=directory.resolve("migration.meta");
        String meta=""
            +"schemaVersion="+SCHEMA_VERSION+"\n"
            +"createdAt="+oneLine(created)+"\n"
            +"action="+oneLine(action)+"\n"
            +"actor="+oneLine(actor)+"\n"
            +"source="+oneLine(source.toAbsolutePath().normalize().toString())+"\n"
            +"sourceModifiedAtMillis="+modified+"\n"
            +"sourceModifiedAt="+Instant.ofEpochMilli(modified)+"\n"
            +"inputSha256="+hash+"\n";
        writeAtomic(metadata,meta.getBytes(StandardCharsets.UTF_8));
        appendAudit(root,action,actor,hash,modified,"backup-created",id);
        return new Backup(id,directory,payload,hash,modified,action,actor,created);
    }

    static Backup stash(Path source,Path root,String action,String actor)throws IOException {
        Backup backup=backup(source,root,action,actor);
        if(!Files.isRegularFile(source))throw new IOException("Task file disappeared before stash: "+source);
        byte[] current=Files.readAllBytes(source);
        long modified=Files.getLastModifiedTime(source).toMillis();
        String currentHash=sha256(current);
        if(modified!=backup.sourceModifiedAt()||!currentHash.equals(backup.sha256()))
            throw new IOException("Task file changed while it was being backed up; refusing to stash it");
        Files.delete(source);
        appendAuditAfterStateChange(root,action,actor,backup.sha256(),backup.sourceModifiedAt(),"source-stashed",backup.id());
        return backup;
    }

    static Backup restoreLatest(Path source,Path root,String actor)throws IOException {
        String id=latestBackupId(root);
        if(id==null)throw new IOException("No task backup is available");
        return restore(source,root,id,actor);
    }

    static Backup restore(Path source,Path root,String backupId,String actor)throws IOException {
        if(Files.exists(source))throw new IOException("A live persisted task already exists; refusing to overwrite it");
        if(backupId==null||backupId.isBlank()||backupId.contains("/")||backupId.contains("\\"))
            throw new IOException("Invalid backup id");
        Path directory=root.resolve(backupId).normalize();
        if(!directory.getParent().equals(root.normalize()))throw new IOException("Backup id escapes migration root");
        Path payload=directory.resolve("task.snapshot");
        if(!Files.isRegularFile(payload))throw new IOException("Task backup does not exist: "+backupId);
        byte[] input=Files.readAllBytes(payload);
        String hash=sha256(input);
        Files.createDirectories(source.getParent());
        writeAtomic(source,input);
        String restoredHash=sha256(Files.readAllBytes(source));
        if(!hash.equals(restoredHash))throw new IOException("Restored task hash verification failed");
        long modified=Files.getLastModifiedTime(source).toMillis();
        appendAuditAfterStateChange(root,"restore",actor,hash,modified,"restored",backupId);
        return new Backup(backupId,directory,payload,hash,modified,"restore",actor,Instant.now().toString());
    }

    static String latestBackupId(Path root)throws IOException {
        Path latest=latestDirectory(root);
        return latest==null?null:latest.getFileName().toString();
    }

    static String sha256(Path path)throws IOException {return sha256(Files.readAllBytes(path));}

    private static Path latestDirectory(Path root)throws IOException {
        if(!Files.isDirectory(root))return null;
        try(Stream<Path> stream=Files.list(root)){
            return stream.filter(Files::isDirectory)
                .filter(path->Files.isRegularFile(path.resolve("task.snapshot")))
                .max(Comparator.comparing(path->path.getFileName().toString()))
                .orElse(null);
        }
    }

    private static void appendAudit(Path root,String action,String actor,String hash,long modified,String result,String backupId)throws IOException {
        Files.createDirectories(root);
        String line=Instant.now()+"\t"+oneLine(action)+"\t"+oneLine(actor)+"\t"+hash+"\t"+modified+"\t"+oneLine(result)+"\t"+oneLine(backupId)+"\n";
        Files.writeString(root.resolve("audit.log"),line,StandardCharsets.UTF_8,StandardOpenOption.CREATE,StandardOpenOption.APPEND);
    }
    private static void appendAuditAfterStateChange(Path root,String action,String actor,String hash,long modified,String result,String backupId){
        try{appendAudit(root,action,actor,hash,modified,result,backupId);}
        catch(IOException e){LOG.log(Level.WARNING,"Task migration completed but its final audit record could not be appended: action="+action+" backup="+backupId,e);}
    }

    private static void writeAtomic(Path destination,byte[] bytes)throws IOException {
        Files.createDirectories(destination.getParent());
        Path tmp=destination.resolveSibling(destination.getFileName()+".tmp-"+UUID.randomUUID());
        Files.write(tmp,bytes,StandardOpenOption.CREATE_NEW,StandardOpenOption.WRITE);
        try{Files.move(tmp,destination,StandardCopyOption.ATOMIC_MOVE,StandardCopyOption.REPLACE_EXISTING);}
        catch(AtomicMoveNotSupportedException e){Files.move(tmp,destination,StandardCopyOption.REPLACE_EXISTING);}
        finally{Files.deleteIfExists(tmp);}
    }

    private static String sha256(byte[] bytes){
        try{return HexFormat.of().formatHex(MessageDigest.getInstance("SHA-256").digest(bytes));}
        catch(NoSuchAlgorithmException e){throw new IllegalStateException("SHA-256 is unavailable",e);}
    }
    private static String safe(String value){return oneLine(value).replaceAll("[^A-Za-z0-9._-]+","-");}
    private static String oneLine(String value){return value==null?"unknown":value.replace('\t',' ').replace('\r',' ').replace('\n',' ');}
}
