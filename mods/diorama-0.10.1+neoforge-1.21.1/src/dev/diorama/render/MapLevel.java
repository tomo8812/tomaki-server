package dev.diorama.render;

import dev.diorama.bake.TileKey;
import dev.diorama.data.ChunkSnapshot;
import dev.diorama.render.gpu.GpuTileBaker;
import java.nio.file.Path;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.Map.Entry;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Stream;

// $VF: Couldn't be decompiled
// Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
// java.lang.NullPointerException: Cannot invoke "org.jetbrains.java.decompiler.modules.decompiler.stats.Statement.getVarDefinitions()" because "stat" is null
//   at org.jetbrains.java.decompiler.modules.decompiler.vars.VarDefinitionHelper.iterateClashingNames(VarDefinitionHelper.java:1592)
//   at org.jetbrains.java.decompiler.modules.decompiler.vars.VarDefinitionHelper.iterateClashingExprent(VarDefinitionHelper.java:1835)
//   at org.jetbrains.java.decompiler.modules.decompiler.vars.VarDefinitionHelper.iterateClashingExprent(VarDefinitionHelper.java:2029)
//   at org.jetbrains.java.decompiler.modules.decompiler.vars.VarDefinitionHelper.iterateClashingNames(VarDefinitionHelper.java:1619)
//   at org.jetbrains.java.decompiler.modules.decompiler.vars.VarDefinitionHelper.iterateClashingNames(VarDefinitionHelper.java:1739)
//   at org.jetbrains.java.decompiler.modules.decompiler.vars.VarDefinitionHelper.remapClashingNames(VarDefinitionHelper.java:1584)
//   at org.jetbrains.java.decompiler.modules.decompiler.vars.VarProcessor.rerunClashing(VarProcessor.java:99)
//   at org.jetbrains.java.decompiler.main.ClassWriter.invokeProcessors(ClassWriter.java:145)
//   at org.jetbrains.java.decompiler.main.ClassWriter.writeClass(ClassWriter.java:379)
//   at org.jetbrains.java.decompiler.main.ClassesProcessor.writeClass(ClassesProcessor.java:521)
//   at org.jetbrains.java.decompiler.main.Fernflower.getClassContent(Fernflower.java:200)
//   at org.jetbrains.java.decompiler.struct.ContextUnit.lambda$save$3(ContextUnit.java:221)
