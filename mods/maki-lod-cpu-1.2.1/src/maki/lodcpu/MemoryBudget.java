package maki.lodcpu;

import java.nio.file.Files;
import java.nio.file.Path;

public record MemoryBudget(double headroomMb, double rssMb, long oomKills, double memoryPsi, double ioPsi, long throttledMicros) {
   public static MemoryBudget read() {
      return read(Path.of("/proc"), Path.of("/sys/fs/cgroup"));
   }

   static MemoryBudget read(Path var0, Path var1) {
      double var2 = -1.0;
      double var4 = -1.0;

      try {
         var2 = kbField(Files.readString(var0.resolve("meminfo")), "MemAvailable:") / 1024.0;
      } catch (Exception var24) {
      }

      try {
         var4 = kbField(Files.readString(var0.resolve("self/status")), "VmRSS:") / 1024.0;
      } catch (Exception var23) {
      }

      Path var6 = var1;

      try {
         String var7 = Files.readAllLines(var0.resolve("self/cgroup")).stream().filter(var0x -> var0x.startsWith("0::")).findFirst().orElseThrow().substring(3);
         Path var8 = var1.resolve(var7.replaceFirst("^/+", "")).normalize();
         if (var8.startsWith(var1) && Files.exists(var8.resolve("memory.current"))) {
            var6 = var8;
         }
      } catch (Exception var22) {
      }

      long var25 = -1L;
      long var9 = -1L;

      for (Path var11 = var6; var11 != null && var11.startsWith(var1); var11 = var11.getParent()) {
         try {
            String var12 = Files.readString(var11.resolve("memory.max")).trim();
            if (!var12.equals("max")) {
               long var13 = Long.parseLong(var12);
               long var15 = Long.parseLong(Files.readString(var11.resolve("memory.current")).trim());
               double var17 = Math.max(0L, var13 - var15) / 1048576.0;
               var2 = var2 < 0.0 ? var17 : Math.min(var2, var17);
            }
         } catch (Exception var21) {
         }

         if (var11.equals(var1)) {
            break;
         }
      }

      try {
         var25 = plainField(Files.readString(var6.resolve("memory.events")), "oom_kill");
      } catch (Exception var20) {
      }

      try {
         var9 = plainField(Files.readString(var6.resolve("cpu.stat")), "throttled_usec");
      } catch (Exception var19) {
      }

      return new MemoryBudget(var2, var4, var25, psi(var0.resolve("pressure/memory")), psi(var0.resolve("pressure/io")), var9);
   }

   static double kbField(String var0, String var1) {
      for (String var5 : var0.split("\\R")) {
         if (var5.startsWith(var1)) {
            return Double.parseDouble(var5.substring(var1.length()).trim().split("\\s+")[0]);
         }
      }

      throw new IllegalArgumentException(var1);
   }

   static long plainField(String var0, String var1) {
      for (String var5 : var0.split("\\R")) {
         String[] var6 = var5.trim().split("\\s+");
         if (var6.length == 2 && var6[0].equals(var1)) {
            return Long.parseLong(var6[1]);
         }
      }

      throw new IllegalArgumentException(var1);
   }

   private static double psi(Path var0) {
      try {
         for (String var2 : Files.readAllLines(var0)) {
            if (var2.startsWith("some ")) {
               for (String var6 : var2.split(" ")) {
                  if (var6.startsWith("avg10=")) {
                     return Double.parseDouble(var6.substring(6));
                  }
               }
            }
         }
      } catch (Exception var7) {
      }

      return -1.0;
   }
}
