package org.millenaire.content.legacy;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public final class LegacyRoot {
   public static final String DEFAULT_BRANCH = "millenaire-1.12-base";
   private static final String REPO_DIR = "millenaire-1.12";
   private static final String CONTENT_SUBPATH = "content/millenaire";
   public static final String OVERRIDE_PROPERTY = "millenaire.legacyRoot";
   private static final Path PROJECT_DIR = Path.of(System.getProperty("user.dir"));

   private LegacyRoot() {
   }

   public static Optional<Path> find() {
      Path override = override();
      if (override != null) {
         return Optional.of(override);
      }

      for (Path repo : candidateRepositories(null)) {
         Path content = repo.resolve("content/millenaire");
         if (Files.isDirectory(content)) {
            return Optional.of(content);
         }
      }

      return Optional.empty();
   }

   public static Path findOrConventional() {
      return find().orElseGet(() -> PROJECT_DIR.resolve("../millenaire-1.12/content/millenaire"));
   }

   public static Path require() {
      return find().orElseThrow(() -> new IllegalStateException(notFoundMessage(null)));
   }

   public static Optional<Path> findRepository() {
      return find().map(content -> content.resolve("../..").normalize());
   }

   public static Path requireLanguages() {
      Path languages = require().resolve("languages");
      if (!Files.isDirectory(languages)) {
         throw new IllegalStateException("Legacy languages directory not found: " + languages);
      } else {
         return languages;
      }
   }

   public static Optional<Path> findForBranch(String branch) {
      Path override = override();
      if (override != null) {
         return Optional.of(override);
      }

      for (Path repo : candidateRepositories(branch)) {
         Path content = repo.resolve("content/millenaire");
         if (Files.isDirectory(content) && branch.equals(currentBranch(repo))) {
            return Optional.of(content);
         }
      }

      return Optional.empty();
   }

   public static Path requireForBranch(String branch) {
      return findForBranch(branch).orElseThrow(() -> new IllegalStateException(notFoundMessage(branch)));
   }

   public static String branchOf(Path contentRoot) {
      Path repo = contentRoot.resolve("../..").normalize();
      String branch = currentBranch(repo);
      return branch == null ? "(unknown)" : branch;
   }

   private static Path override() {
      String raw = System.getProperty("millenaire.legacyRoot");
      if (raw != null && !raw.isBlank()) {
         Path path = Path.of(raw).toAbsolutePath().normalize();
         if (!Files.isDirectory(path)) {
            throw new IllegalStateException("-Dmillenaire.legacyRoot points at a directory that does not exist: " + path);
         }

         System.err.println("[WARN] Legacy root forced by -Dmillenaire.legacyRoot=" + path + " — the per-culture branch check is skipped.");
         return path;
      } else {
         return null;
      }
   }

   private static List<Path> candidateRepositories(String branch) {
      List<Path> repos = new ArrayList<>();

      for (Path parent : parentDirectories()) {
         if (branch != null) {
            repos.add(parent.resolve("millenaire-1.12-" + branch));
         }

         repos.add(parent.resolve("millenaire-1.12"));
      }

      return repos;
   }

   private static List<Path> parentDirectories() {
      List<Path> parents = new ArrayList<>();
      Path workingParent = PROJECT_DIR.toAbsolutePath().normalize().getParent();
      if (workingParent != null) {
         parents.add(workingParent);
      }

      Path mainRepo = mainRepositoryRoot();
      if (mainRepo != null) {
         Path mainParent = mainRepo.getParent();
         if (mainParent != null && !parents.contains(mainParent)) {
            parents.add(mainParent);
         }
      }

      return parents;
   }

   private static Path mainRepositoryRoot() {
      String gitCommonDir = git(PROJECT_DIR, "rev-parse", "--path-format=absolute", "--git-common-dir");
      return gitCommonDir == null ? null : Path.of(gitCommonDir).getParent();
   }

   private static String currentBranch(Path repo) {
      if (!Files.isDirectory(repo)) {
         return null;
      }

      String branch = git(repo, "rev-parse", "--abbrev-ref", "HEAD");
      return branch != null && !branch.isBlank() && !"HEAD".equals(branch) ? branch : null;
   }

   private static String git(Path dir, String... args) {
      List<String> command = new ArrayList<>();
      command.add("git");
      command.addAll(List.of(args));

      try {
         ProcessBuilder pb = new ProcessBuilder(command);
         pb.directory(dir.toFile());
         pb.redirectErrorStream(true);
         Process p = pb.start();
         String out = new String(p.getInputStream().readAllBytes()).trim();
         p.waitFor();
         return p.exitValue() == 0 ? out : null;
      } catch (InterruptedException e) {
         Thread.currentThread().interrupt();
         return null;
      } catch (Exception e) {
         return null;
      }
   }

   private static String notFoundMessage(String branch) {
      StringBuilder sb = new StringBuilder();
      sb.append("Legacy content root not found");
      if (branch != null) {
         sb.append(" for branch '").append(branch).append('\'');
      }

      sb.append(".\nLooked for ").append("content/millenaire").append(" under:");

      for (Path repo : candidateRepositories(branch)) {
         sb.append("\n  - ").append(repo);
         String actual = currentBranch(repo);
         if (Files.isDirectory(repo)) {
            sb.append("  (exists, on branch ").append(actual == null ? "(detached)" : actual).append(')');
         } else {
            sb.append("  (missing)");
         }
      }

      if (branch == null) {
         sb.append("\nClone the legacy repository next to this one: bash scripts/clone-legacy.sh");
      } else {
         sb.append("\nThis culture is pinned to legacy branch '")
            .append(branch)
            .append("' (see docs/bugs-known.md BUG-372). Create the dedicated worktree once:")
            .append("\n  git -C ../")
            .append("millenaire-1.12")
            .append(" worktree add ../")
            .append("millenaire-1.12")
            .append('-')
            .append(branch)
            .append(' ')
            .append(branch)
            .append("\nDo NOT switch the branch of the shared legacy clone.");
      }

      sb.append("\nOr point the resolver at a checkout explicitly: -D")
         .append("millenaire.legacyRoot")
         .append("=<path to ")
         .append("content/millenaire")
         .append('>');
      return sb.toString();
   }
}
