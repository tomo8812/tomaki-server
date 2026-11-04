package org.sinytra.connector.locator.filter;

import com.google.common.collect.Sets;
import com.mojang.logging.LogUtils;
import cpw.mods.jarhandling.SecureJar;
import cpw.mods.jarhandling.impl.Jar;
import cpw.mods.jarhandling.impl.JarContentsImpl;
import cpw.mods.modlauncher.api.LambdaExceptionUtils;
import cpw.mods.niofs.union.UnionFileSystem;
import cpw.mods.niofs.union.UnionPathFilter;
import java.lang.invoke.VarHandle;
import java.util.Set;
import net.neoforged.neoforgespi.locating.IModFile;
import org.sinytra.connector.util.ConnectorUtil;
import org.slf4j.Logger;

public final class ForgeModPackageFilter {
   private static final Logger LOGGER = LogUtils.getLogger();
   private static final VarHandle JAR_CONTENTS = (VarHandle)LambdaExceptionUtils.uncheck(
      () -> ConnectorUtil.TRUSTED_LOOKUP.findVarHandle(Jar.class, "contents", JarContentsImpl.class)
   );
   private static final VarHandle JAR_CONTENTS_PACKAGES = (VarHandle)LambdaExceptionUtils.uncheck(
      () -> ConnectorUtil.TRUSTED_LOOKUP.findVarHandle(JarContentsImpl.class, "packages", Set.class)
   );
   private static final VarHandle UPFS_FILTER = (VarHandle)LambdaExceptionUtils.uncheck(
      () -> ConnectorUtil.TRUSTED_LOOKUP.findVarHandle(UnionFileSystem.class, "pathFilter", UnionPathFilter.class)
   );

   public static void filterPackages(Iterable<IModFile> fmlMods) {
      Set<String> existingPackages = ForgeModPackageFilter.class.getModule().getPackages();

      for (IModFile modFile : fmlMods) {
         filterModFile(modFile, existingPackages);
      }
   }

   private static void filterModFile(IModFile modFile, Set<String> existingPackages) {
      SecureJar jar = modFile.getSecureJar();
      Set<String> packages = jar.moduleDataProvider().descriptor().packages();
      Set<String> common = Sets.intersection(packages, existingPackages);
      if (!common.isEmpty() && jar instanceof Jar jarImpl) {
         LOGGER.debug("Filtering {} packages from mod file {}", common.size(), jar.getPrimaryPath().getFileName());
         UnionPathFilter filter = new PackageTracker(common);
         if (jarImpl.getRootPath().getFileSystem() instanceof UnionFileSystem ufs) {
            injectUFSFilter(ufs, filter);
            forceRecomputeJarPackages(jarImpl);
         }
      }
   }

   private static void injectUFSFilter(UnionFileSystem ufs, UnionPathFilter filter) {
      UnionPathFilter existing = ufs.getFilesystemFilter();
      UnionPathFilter merged = existing != null ? (a, b) -> existing.test(a, b) && filter.test(a, b) : filter;
      UPFS_FILTER.set((UnionFileSystem)ufs, (UnionPathFilter)merged);
   }

   private static void forceRecomputeJarPackages(Jar jar) {
      JarContentsImpl jarContents = (JarContentsImpl)LambdaExceptionUtils.uncheck(() -> (Object)JAR_CONTENTS.get((Jar)jar));
      LambdaExceptionUtils.uncheck(() -> JAR_CONTENTS_PACKAGES.set((JarContentsImpl)jarContents, (Void)null));
   }
}
