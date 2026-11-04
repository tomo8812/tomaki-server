package reloc.net.minecraftforge.srgutils;

import java.util.Locale;

public class MinecraftVersion implements Comparable<MinecraftVersion> {
   public static MinecraftVersion NEGATIVE = from("-1");
   private final MinecraftVersion.Type type;
   private final String full;
   private final int[] nearest;
   private final int week;
   private final int year;
   private final int pre;
   private final String revision;

   public static MinecraftVersion from(String version) {
      try {
         return get(version);
      } catch (IllegalArgumentException e) {
         throw e;
      } catch (Exception e) {
         throw new IllegalArgumentException("Unknown version: " + version, e);
      }
   }

   private static String fromSnapshot(int year, int week) {
      int value = year * 100 + week;
      if (value >= 1147 && value <= 1201) {
         return "1.1";
      } else if (value >= 1203 && value <= 1208) {
         return "1.2";
      } else if (value >= 1215 && value <= 1230) {
         return "1.3";
      } else if (value >= 1232 && value <= 1242) {
         return "1.4";
      } else if (value >= 1249 && value <= 1250) {
         return "1.4.6";
      } else if (value >= 1301 && value <= 1310) {
         return "1.5";
      } else if (value >= 1311 && value <= 1312) {
         return "1.5.1";
      } else if (value >= 1316 && value <= 1326) {
         return "1.6";
      } else if (value >= 1336 && value <= 1343) {
         return "1.7";
      } else if (value >= 1347 && value <= 1349) {
         return "1.7.4";
      } else if (value >= 1402 && value <= 1434) {
         return "1.8";
      } else if (value >= 1531 && value <= 1607) {
         return "1.9";
      } else if (value >= 1614 && value <= 1615) {
         return "1.9.3";
      } else if (value >= 1620 && value <= 1621) {
         return "1.10";
      } else if (value >= 1632 && value <= 1644) {
         return "1.11";
      } else if (value >= 1650 && value <= 1650) {
         return "1.11.1";
      } else if (value >= 1706 && value <= 1718) {
         return "1.12";
      } else if (value >= 1731 && value <= 1731) {
         return "1.12.1";
      } else if (value >= 1743 && value <= 1822) {
         return "1.13";
      } else if (value >= 1830 && value <= 1833) {
         return "1.13.1";
      } else if (value >= 1843 && value <= 1914) {
         return "1.14";
      } else if (value >= 1934 && value <= 1946) {
         return "1.15";
      } else if (value >= 2006 && value <= 2022) {
         return "1.16";
      } else if (value >= 2027 && value <= 2030) {
         return "1.16.2";
      } else if (value >= 2045 && value <= 2120) {
         return "1.17";
      } else if (value >= 2137 && value <= 2144) {
         return "1.18";
      } else if (value >= 2203 && value <= 2207) {
         return "1.18.2";
      } else if (value >= 2211 && value <= 2219) {
         return "1.19";
      } else if (value >= 2224 && value <= 2224) {
         return "1.19.1";
      } else if (value >= 2242 && value <= 2246) {
         return "1.19.3";
      } else if (value >= 2303 && value <= 2307) {
         return "1.19.4";
      } else if (value >= 2312 && value <= 9999) {
         return "1.20";
      } else {
         throw new IllegalArgumentException("Invalid snapshot date: " + value);
      }
   }

   private static int[] splitDots(String version) {
      String[] pts = version.split("\\.");
      int[] values = new int[pts.length];

      for (int x = 0; x < pts.length; x++) {
         values[x] = Integer.parseInt(pts[x]);
      }

      return values;
   }

   private MinecraftVersion(MinecraftVersion.Type type, String full, int week, int year, int pre, String revision, int[] nearest) {
      this.type = type;
      this.full = full;
      this.nearest = nearest;
      this.week = week;
      this.year = year;
      this.pre = pre;
      this.revision = revision;
   }

   private static MinecraftVersion get(String version) {
      String lower = version.toLowerCase(Locale.ENGLISH);
      char first = version.charAt(0);
      String preA = Character.toString('`');
      if ("15w14a".equals(lower)) {
         return new MinecraftVersion(MinecraftVersion.Type.APRIL_FOOLS, version, 14, 15, 0, "a", splitDots("1.10"));
      }

      if ("1.rv-pre1".equals(lower)) {
         return new MinecraftVersion(MinecraftVersion.Type.APRIL_FOOLS, version, 14, 16, 0, preA, splitDots("1.9.3"));
      }

      if ("3d shareware v1.34".equals(lower)) {
         return new MinecraftVersion(MinecraftVersion.Type.APRIL_FOOLS, version, 14, 19, 0, preA, splitDots("1.14"));
      }

      if ("20w14infinite".equals(lower)) {
         return new MinecraftVersion(MinecraftVersion.Type.APRIL_FOOLS, version, 14, 20, 0, preA, splitDots("1.16"));
      }

      if ("22w13oneblockatatime".equals(lower)) {
         return new MinecraftVersion(MinecraftVersion.Type.APRIL_FOOLS, version, 13, 22, 0, "b", splitDots("1.19"));
      }

      if ("23w13a_or_b".equals(lower)) {
         return new MinecraftVersion(MinecraftVersion.Type.APRIL_FOOLS, version, 13, 23, 0, "b", splitDots("1.20"));
      }

      if ("inf-20100618".equals(lower)) {
         return new MinecraftVersion(MinecraftVersion.Type.ALPHA, version, 25, 10, 0, "a", splitDots("1.0.4"));
      }

      if ("c0.0.13a_03".equals(lower)) {
         return new MinecraftVersion(MinecraftVersion.Type.ALPHA, version, -1, -1, 0, preA, splitDots("0.0.13"));
      }

      if (lower.startsWith("rd-")) {
         String rev;
         switch (lower) {
            case "rd-132211":
               rev = "a";
               break;
            case "rd-132328":
               rev = "b";
               break;
            case "rd-20090515":
               rev = "c";
               break;
            case "rd-160052":
               rev = "d";
               break;
            case "rd-161348":
               rev = "e";
               break;
            default:
               throw new IllegalArgumentException("Unknown 'rd' version: " + version);
         }

         return new MinecraftVersion(MinecraftVersion.Type.ALPHA, version, 20, 9, 0, rev, splitDots("0.0.1"));
      } else if (first != 'a' && first != 'b' && first != 'c') {
         if (version.length() == 6 && version.charAt(2) == 'w') {
            int year = Integer.parseInt(version.substring(0, 2));
            int week = Integer.parseInt(version.substring(3, 5));
            return new MinecraftVersion(MinecraftVersion.Type.SNAPSHOT, version, week, year, 0, version.substring(5), splitDots(fromSnapshot(year, week)));
         }

         int pre = 0;
         String nearest = version;
         if (version.contains("-pre")) {
            String[] pts = version.split("-pre");
            pre = Integer.parseInt(pts[1]);
            nearest = pts[0];
         } else if (version.contains("_Pre-Release_")) {
            String[] pts = version.split("_Pre-Release_");
            pre = Integer.parseInt(pts[1]);
            nearest = pts[0];
         } else if (version.contains(" Pre-Release ")) {
            String[] pts = version.split(" Pre-Release ");
            pre = Integer.parseInt(pts[1]);
            nearest = pts[0];
         } else if (version.contains("-rc")) {
            String[] pts = version.split("-rc");
            pre = -1 * Integer.parseInt(pts[1]);
            nearest = pts[0];
         }

         return new MinecraftVersion(MinecraftVersion.Type.RELEASE, version, -1, -1, pre, null, splitDots(nearest));
      } else {
         String clean = version.substring(1).replace('_', '.');
         String nearest = clean;
         String rev = null;
         char end = clean.charAt(clean.length() - 1);
         if (end < '0' || end > '9') {
            rev = Character.toString(end);
            nearest = clean.substring(0, clean.length() - 1);
         }

         return new MinecraftVersion(first == 'b' ? MinecraftVersion.Type.BETA : MinecraftVersion.Type.ALPHA, version, -1, -1, 0, rev, splitDots(nearest));
      }
   }

   public MinecraftVersion.Type getType() {
      return this.type;
   }

   @Override
   public String toString() {
      return this.full;
   }

   @Override
   public int hashCode() {
      return this.full.hashCode();
   }

   @Override
   public boolean equals(Object o) {
      return !(o instanceof MinecraftVersion) ? false : this.full.equals(((MinecraftVersion)o).full);
   }

   public int compareTo(MinecraftVersion o) {
      if (o == null) {
         return 1;
      }

      if (!this.type.canEasyCompare(o.type)) {
         int ret = 0;
         switch (this.type) {
            case ALPHA:
               return -1;
            case BETA:
               return o.type == MinecraftVersion.Type.ALPHA ? 1 : -1;
            case SNAPSHOT:
            case APRIL_FOOLS:
               ret = this.compareFull(o);
               return ret == 0 ? -1 : ret;
            case RELEASE:
               switch (o.type) {
                  case ALPHA:
                  case BETA:
                     return 1;
                  case SNAPSHOT:
                  case APRIL_FOOLS:
                  case RELEASE:
                     ret = this.compareFull(o);
                     return ret == 0 ? 1 : ret;
               }
         }
      }

      switch (this.type) {
         case ALPHA:
         case BETA:
            int ret = this.compareFull(o);
            if (ret != 0) {
               return ret;
            } else if (this.revision == null && o.revision != null) {
               return 1;
            } else {
               if (this.revision != null && o.revision == null) {
                  return -1;
               }

               return this.revision.compareTo(o.revision);
            }
         case SNAPSHOT:
         case APRIL_FOOLS:
            if (this.year != o.year) {
               return this.year - o.year;
            } else {
               if (this.week != o.week) {
                  return this.week - o.week;
               }

               return this.revision.compareTo(o.revision);
            }
         case RELEASE:
            return this.compareFull(o);
         default:
            throw new IllegalArgumentException("Invalid type: " + this.type);
      }
   }

   private int compareFull(MinecraftVersion o) {
      for (int x = 0; x < this.nearest.length; x++) {
         if (x >= o.nearest.length) {
            return 1;
         }

         if (this.nearest[x] != o.nearest[x]) {
            return this.nearest[x] - o.nearest[x];
         }
      }

      if (this.nearest.length < o.nearest.length) {
         return -1;
      } else if (this.type == MinecraftVersion.Type.RELEASE && o.type != MinecraftVersion.Type.RELEASE) {
         return 1;
      } else if (this.type != MinecraftVersion.Type.RELEASE && o.type == MinecraftVersion.Type.RELEASE) {
         return -1;
      } else if (this.pre == 0 && o.pre != 0) {
         return 1;
      } else if (this.pre != 0 && o.pre == 0) {
         return -1;
      } else if (this.pre < 0 && o.pre > 0) {
         return 1;
      } else if (this.pre > 0 && o.pre < 0) {
         return -1;
      } else {
         return this.pre > 0 ? this.pre - o.pre : -this.pre - -o.pre;
      }
   }

   // $VF: synthetic method
   // $VF: bridge method
   @Override
   public int compareTo(Object var1) {
      return this.compareTo((MinecraftVersion)var1);
   }

   public enum Type {
      RELEASE,
      SNAPSHOT,
      BETA,
      ALPHA,
      APRIL_FOOLS(true);

      private final boolean special;

      Type() {
         this(false);
      }

      Type(boolean special) {
         this.special = special;
      }

      public boolean isSpecial() {
         return this.special;
      }

      boolean canEasyCompare(MinecraftVersion.Type other) {
         if (this == other) {
            return true;
         }

         boolean thisSnapshot = this == APRIL_FOOLS || this == SNAPSHOT;
         boolean otherSnapshot = other == APRIL_FOOLS || other == SNAPSHOT;
         return thisSnapshot && otherSnapshot;
      }
   }
}
