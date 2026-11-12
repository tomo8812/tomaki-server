"""Execute the real borrow ledger against a minimal main-thread world fixture."""
import os
from pathlib import Path
import shutil
import subprocess
import tempfile
import unittest

ROOT = Path(__file__).resolve().parents[2]
SOURCE = ROOT / 'custom-mods/maki-chunksmith-chunk-cache/src/main/java/maki/chunksmithcache'

class BorrowOwnershipTest(unittest.TestCase):
    def test_loaded_full_and_exact_owner_are_required(self):
        sources = {
            'com/kishku7/chunksmith/GenerationTask.java': 'package com.kishku7.chunksmith; public class GenerationTask {}',
            'net/minecraft/world/level/ChunkPos.java': '''package net.minecraft.world.level;
public class ChunkPos {public final int x,z;public ChunkPos(int x,int z){this.x=x;this.z=z;}
public ChunkPos(long packed){this((int)packed,(int)(packed>>32));}public long toLong(){return ((long)z<<32)|(x&0xffffffffL);}}''',
            'net/minecraft/world/level/chunk/ChunkAccess.java': '''package net.minecraft.world.level.chunk;
public class ChunkAccess {public boolean dirty; public void setUnsaved(boolean value){dirty=value;}
public net.minecraft.world.level.ChunkPos getPos(){return new net.minecraft.world.level.ChunkPos(0,0);}}''',
            'net/minecraft/server/MinecraftServer.java': '''package net.minecraft.server;
public class MinecraftServer {public boolean isSameThread(){return true;}public void execute(Runnable action){action.run();}
public PlayerList getPlayerList(){return new PlayerList();}public static class PlayerList {public int getViewDistance(){return 2;}}}''',
            'net/minecraft/server/level/ServerPlayer.java': '''package net.minecraft.server.level;
public class ServerPlayer {public net.minecraft.world.level.ChunkPos chunkPosition(){return new net.minecraft.world.level.ChunkPos(0,0);}}''',
            'net/minecraft/server/level/ServerLevel.java': '''package net.minecraft.server.level;
public class ServerLevel {public boolean visible;public final net.minecraft.server.MinecraftServer server=new net.minecraft.server.MinecraftServer();
public net.minecraft.server.MinecraftServer getServer(){return server;}public Object getPoiManager(){return this;}
public java.util.List<ServerPlayer> players(){return java.util.List.of(new ServerPlayer());}
public Source getChunkSource(){return new Source();}public class Source {public Object getChunkNow(int x,int z){return visible?new Object():null;}}}''',
            'maki/chunksmithcache/CacheManager.java': '''package maki.chunksmithcache;
public class CacheManager {public static int handoffs;public static boolean pregenPersistenceSuppressed(net.minecraft.server.level.ServerLevel level,net.minecraft.world.level.ChunkPos pos){if(PregenBorrowLedger.handoffCandidate(level,pos)!=null)handoffs++;return PregenBorrowLedger.suppressPersistence(level,pos);}}''',
            'maki/chunksmithcache/Harness.java': '''package maki.chunksmithcache;
import java.nio.file.*;import com.kishku7.chunksmith.GenerationTask;
import net.minecraft.server.level.ServerLevel;import net.minecraft.world.level.ChunkPos;
public class Harness {
public static void main(String[] args)throws Exception{
 var level=new ServerLevel();var pos=new ChunkPos(0,0);var old=new GenerationTask();var newer=new GenerationTask();
 var file=Path.of(args[0]);Files.writeString(file,"saved FULL");
 check(PregenBorrowLedger.beginBorrowed(level,pos,file,old),"borrow accepted");
 check(PregenBorrowLedger.handoffCandidate(level,pos)==null,"a near player cannot hand off an unloaded borrow");
 PregenBorrowLedger.handoffPlayers(level.server);check(CacheManager.handoffs==0,"tick cannot adopt unloaded cache");
 check(!PregenBorrowLedger.cancel(level,pos,newer),"another owner cannot cancel the borrow");
 check(PregenBorrowLedger.cancel(level,pos,old),"rejected native load releases exact borrowed owner");
 check(Files.readString(file).equals("saved FULL"),"cancel preserves usable FULL payload");
 check(PregenBorrowLedger.beginBorrowed(level,pos,file,newer),"replacement borrow accepted");
 check(!PregenBorrowLedger.cancel(level,pos,old),"late old callback cannot cancel replacement");
 check(PregenBorrowLedger.generatedFullLoaded(level,pos,newer),"actual FULL future acknowledges borrowed owner");
 check(PregenBorrowLedger.handoffCandidate(level,pos)==null,"successful future alone is not published FULL visibility");
 level.visible=true;check(PregenBorrowLedger.handoffCandidate(level,pos).equals(file),"loaded visible FULL can hand off");
 check(!PregenBorrowLedger.cancel(level,pos,newer),"loaded FULL keeps persistence ownership");
 PregenBorrowLedger.unloaded(level,pos);check(PregenBorrowLedger.kind(level,pos)==null,"unload retires guard");
 System.out.println("Borrow lifecycle PASS");
}
static void check(boolean ok,String reason){if(!ok)throw new AssertionError(reason);}
}'''
        }
        with tempfile.TemporaryDirectory() as temporary:
            root = Path(temporary)
            for path, content in sources.items():
                target = root / path
                target.parent.mkdir(parents=True, exist_ok=True)
                target.write_text(content)
            for name in ('PregenBorrowLedger.java', 'PregenPoiDirtyBridge.java'):
                shutil.copy2(SOURCE / name, root / 'maki/chunksmithcache' / name)
            java_home = os.environ.get('JAVA_HOME')
            javac = str(Path(java_home) / 'bin/javac') if java_home else 'javac'
            java = str(Path(java_home) / 'bin/java') if java_home else 'java'
            subprocess.run([javac, '-d', str(root / 'classes'), *map(str, root.rglob('*.java'))], check=True, capture_output=True, text=True)
            result = subprocess.run([java, '-cp', str(root / 'classes'), 'maki.chunksmithcache.Harness', str(root / 'full.nbt.gz')], capture_output=True, text=True)
            self.assertEqual(result.returncode, 0, result.stdout + result.stderr)
            self.assertIn('Borrow lifecycle PASS', result.stdout)

if __name__ == '__main__':
    unittest.main()
