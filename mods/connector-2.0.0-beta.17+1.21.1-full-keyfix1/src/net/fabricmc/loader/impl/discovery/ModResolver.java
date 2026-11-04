package net.fabricmc.loader.impl.discovery;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.stream.Collectors;
import net.fabricmc.api.EnvType;
import net.fabricmc.loader.api.metadata.ModDependency;
import net.fabricmc.loader.impl.metadata.ModDependencyImpl;
import net.fabricmc.loader.impl.util.log.Log;
import net.fabricmc.loader.impl.util.log.LogCategory;
import reloc.org.sat4j.specs.ContradictionException;
import reloc.org.sat4j.specs.TimeoutException;

public class ModResolver {
   private static final Comparator<ModCandidateImpl> modPrioComparator = new Comparator<ModCandidateImpl>() {
      public int compare(ModCandidateImpl a, ModCandidateImpl b) {
         if (a.isRoot()) {
            if (!b.isRoot()) {
               return -1;
            }
         } else if (b.isRoot()) {
            return 1;
         }

         int idCmp = a.getId().compareTo(b.getId());
         if (idCmp != 0) {
            return idCmp;
         } else {
            int versionCmp = b.getVersion().compareTo(a.getVersion());
            if (versionCmp != 0) {
               return versionCmp;
            } else {
               int nestCmp = a.getMinNestLevel() - b.getMinNestLevel();
               if (nestCmp != 0) {
                  return nestCmp;
               } else if (a.isRoot()) {
                  return 0;
               } else {
                  List<ModCandidateImpl> parents = new ArrayList<>(a.getParentMods().size() + b.getParentMods().size());
                  parents.addAll(a.getParentMods());
                  parents.addAll(b.getParentMods());
                  parents.sort(this);
                  if (a.getParentMods().contains(parents.get(0))) {
                     return b.getParentMods().contains(parents.get(0)) ? 0 : -1;
                  } else {
                     return 1;
                  }
               }
            }
         }
      }

      // $VF: synthetic method
      // $VF: bridge method
      @Override
      public int compare(Object param1, Object nullx) {
         return this.compare((ModCandidateImpl)var1, (ModCandidateImpl)nullx);
      }
   };

   public static List<ModCandidateImpl> resolve(Collection<ModCandidateImpl> candidates, EnvType envType, Map<String, Set<ModCandidateImpl>> envDisabledMods) throws ModResolutionException {
      long startTime = System.nanoTime();
      List<ModCandidateImpl> result = findCompatibleSet(candidates, envType, envDisabledMods);
      long endTime = System.nanoTime();
      Log.debug(LogCategory.RESOLUTION, "Mod resolution time: %.1f ms", (endTime - startTime) * 1.0E-6);
      return result;
   }

   private static List<ModCandidateImpl> findCompatibleSet(
      Collection<ModCandidateImpl> candidates, EnvType envType, Map<String, Set<ModCandidateImpl>> envDisabledMods
   ) throws ModResolutionException {
      List<ModCandidateImpl> allModsSorted = new ArrayList<>(candidates);
      allModsSorted.sort(modPrioComparator);
      Map<String, List<ModCandidateImpl>> modsById = new LinkedHashMap<>();

      for (ModCandidateImpl mod : allModsSorted) {
         modsById.computeIfAbsent(mod.getId(), ignore -> new ArrayList<>()).add(mod);

         for (String provided : mod.getProvides()) {
            modsById.computeIfAbsent(provided, ignore -> new ArrayList<>()).add(mod);
         }
      }

      for (ModCandidateImpl mod : allModsSorted) {
         if (mod.getMetadata().getSchemaVersion() < 2) {
            for (ModDependency dep : mod.getMetadata().getDependencies()) {
               if (dep.getKind().isPositive()
                  && dep.getKind() != ModDependency.Kind.SUGGESTS
                  && dep instanceof ModDependencyImpl
                  && !modsById.containsKey(dep.getModId())) {
                  Collection<ModCandidateImpl> disabledMatches = envDisabledMods.get(dep.getModId());
                  if (disabledMatches != null) {
                     for (ModCandidateImpl m : disabledMatches) {
                        if (dep.matches(m.getVersion())) {
                           ((ModDependencyImpl)dep).setKind(ModDependency.Kind.SUGGESTS);
                           break;
                        }
                     }
                  }
               }
            }
         }
      }

      List<ModCandidateImpl> preselectedMods = new ArrayList<>();

      for (List<ModCandidateImpl> mods : modsById.values()) {
         ModCandidateImpl builtinMod = null;

         for (ModCandidateImpl mod : mods) {
            if (mod.isBuiltin()) {
               builtinMod = mod;
               break;
            }
         }

         if (builtinMod != null) {
            if (mods.size() > 1) {
               mods.remove(builtinMod);
               throw new ModResolutionException("Mods share ID with builtin mod %s: %s", builtinMod, mods);
            }

            preselectedMods.add(builtinMod);
         }
      }

      Map<String, ModCandidateImpl> selectedMods = new HashMap<>(allModsSorted.size());
      List<ModCandidateImpl> uniqueSelectedMods = new ArrayList<>(allModsSorted.size());

      for (ModCandidateImpl mod : preselectedMods) {
         preselectMod(mod, allModsSorted, modsById, selectedMods, uniqueSelectedMods);
      }

      ModSolver.Result result;
      try {
         result = ModSolver.solve(allModsSorted, modsById, selectedMods, uniqueSelectedMods);
      } catch (ContradictionException | TimeoutException e) {
         throw new ModResolutionException("Solving failed", e);
      }

      if (!result.success) {
         Log.warn(LogCategory.RESOLUTION, "Mod resolution failed");
         Log.info(LogCategory.RESOLUTION, "Immediate reason: %s%n", result.immediateReason);
         Log.info(LogCategory.RESOLUTION, "Reason: %s%n", result.reason);
         if (!envDisabledMods.isEmpty()) {
            Log.info(LogCategory.RESOLUTION, "%s environment disabled: %s%n", envType.name(), envDisabledMods.keySet());
         }

         if (result.fix == null) {
            Log.info(LogCategory.RESOLUTION, "No fix?");
         } else {
            Log.info(
               LogCategory.RESOLUTION,
               "Fix: add %s, remove %s, replace [%s]%n",
               result.fix.modsToAdd,
               result.fix.modsToRemove,
               result.fix.modReplacements.entrySet().stream().map(e -> String.format("%s -> %s", e.getValue(), e.getKey())).collect(Collectors.joining(", "))
            );

            for (Collection<ModCandidateImpl> mods : envDisabledMods.values()) {
               for (ModCandidateImpl m : mods) {
                  result.fix.inactiveMods.put(m, ModSolver.InactiveReason.WRONG_ENVIRONMENT);
               }
            }
         }

         throw new ModResolutionException(
            "Some of your mods are incompatible with the game or each other!%s",
            ResultAnalyzer.gatherErrors(result, selectedMods, modsById, envDisabledMods, envType)
         );
      } else {
         uniqueSelectedMods.sort(Comparator.comparing(ModCandidateImpl::getId));
         Queue<ModCandidateImpl> queue = new ArrayDeque<>();

         for (ModCandidateImpl mod : allModsSorted) {
            if (selectedMods.get(mod.getId()) != mod) {
               mod.clearCachedData();

               for (ModCandidateImpl m : mod.getNestedMods()) {
                  m.getParentMods().remove(mod);
               }

               for (ModCandidateImpl m : mod.getParentMods()) {
                  m.getNestedMods().remove(mod);
               }
            } else if (!mod.resetMinNestLevel()) {
               queue.add(mod);
            }
         }

         ModCandidateImpl mod;
         while ((mod = queue.poll()) != null) {
            for (ModCandidateImpl child : mod.getNestedMods()) {
               if (child.updateMinNestLevel(mod)) {
                  queue.add(child);
               }
            }
         }

         String warnings = ResultAnalyzer.gatherWarnings(uniqueSelectedMods, selectedMods, envDisabledMods, envType);
         if (warnings != null) {
            Log.warn(LogCategory.RESOLUTION, "Warnings were found!%s", warnings);
         }

         return uniqueSelectedMods;
      }
   }

   static void preselectMod(
      ModCandidateImpl mod,
      List<ModCandidateImpl> allModsSorted,
      Map<String, List<ModCandidateImpl>> modsById,
      Map<String, ModCandidateImpl> selectedMods,
      List<ModCandidateImpl> uniqueSelectedMods
   ) throws ModResolutionException {
      selectMod(mod, selectedMods, uniqueSelectedMods);
      allModsSorted.removeAll(modsById.remove(mod.getId()));

      for (String provided : mod.getProvides()) {
         allModsSorted.removeAll(modsById.remove(provided));
      }
   }

   static void selectMod(ModCandidateImpl mod, Map<String, ModCandidateImpl> selectedMods, List<ModCandidateImpl> uniqueSelectedMods) throws ModResolutionException {
      ModCandidateImpl prev = selectedMods.put(mod.getId(), mod);
      if (prev != null) {
         throw new ModResolutionException("duplicate mod %s", mod.getId());
      }

      for (String provided : mod.getProvides()) {
         prev = selectedMods.put(provided, mod);
         if (prev != null) {
            throw new ModResolutionException("duplicate mod %s", provided);
         }
      }

      uniqueSelectedMods.add(mod);
   }
}
