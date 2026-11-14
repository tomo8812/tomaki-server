package maki.chunksmithcontroller;

import java.nio.file.Files;
import java.nio.file.Path;

public record MemoryBudget(double headroomMb, double rssMb, long oomKills, double memoryPsi, double ioPsi, long throttledMicros) {
   public static MemoryBudget read() {
      return read(Path.of("/proc"), Path.of("/sys/fs/cgroup"));
   }

   static MemoryBudget read(Path proc, Path group) {
      double free = -1.0;
      double rss = -1.0;

      try {
         free = kbField(Files.readString(proc.resolve("meminfo")), "MemAvailable:") / 1024.0;
      } catch (Exception var24) {
      }

      try {
         rss = kbField(Files.readString(proc.resolve("self/status")), "VmRSS:") / 1024.0;
      } catch (Exception var23) {
      }

      Path currentGroup = group;

      try {
         String path = Files.readAllLines(proc.resolve("self/cgroup")).stream().filter(s -> s.startsWith("0::")).findFirst().orElseThrow().substring(3);
         Path candidate = group.resolve(path.replaceFirst("^/+", "")).normalize();
         if (candidate.startsWith(group) && Files.exists(candidate.resolve("memory.current"))) {
            currentGroup = candidate;
         }
      } catch (Exception var22) {
      }

      long oom = -1L;
      long throttled = -1L;

      for (Path node = currentGroup; node != null && node.startsWith(group); node = node.getParent()) {
         try {
            String max = Files.readString(node.resolve("memory.max")).trim();
            if (!max.equals("max")) {
               long limit = Long.parseLong(max);
               long used = Long.parseLong(Files.readString(node.resolve("memory.current")).trim());
               double remaining = Math.max(0L, limit - used) / 1048576.0;
               free = free < 0.0 ? remaining : Math.min(free, remaining);
            }
         } catch (Exception var21) {
         }

         if (node.equals(group)) {
            break;
         }
      }

      try {
         oom = plainField(Files.readString(currentGroup.resolve("memory.events")), "oom_kill");
      } catch (Exception var20) {
      }

      try {
         throttled = plainField(Files.readString(currentGroup.resolve("cpu.stat")), "throttled_usec");
      } catch (Exception var19) {
      }

      return new MemoryBudget(free, rss, oom, psi(proc.resolve("pressure/memory")), psi(proc.resolve("pressure/io")), throttled);
   }

   static double kbField(String text, String key) {
      for (String line : text.split("\\R")) {
         if (line.startsWith(key)) {
            return Double.parseDouble(line.substring(key.length()).trim().split("\\s+")[0]);
         }
      }

      throw new IllegalArgumentException(key);
   }

   static long plainField(String text, String key) {
      for (String line : text.split("\\R")) {
         String[] p = line.trim().split("\\s+");
         if (p.length == 2 && p[0].equals(key)) {
            return Long.parseLong(p[1]);
         }
      }

      throw new IllegalArgumentException(key);
   }

   private static double psi(Path path) {
      try {
         for (String line : Files.readAllLines(path)) {
            if (line.startsWith("some ")) {
               for (String part : line.split(" ")) {
                  if (part.startsWith("avg10=")) {
                     return Double.parseDouble(part.substring(6));
                  }
               }
            }
         }
      } catch (Exception var7) {
      }

      return -1.0;
   }
}
