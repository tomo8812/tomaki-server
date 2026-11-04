package org.embeddedt.modernfix.render.font;

import java.lang.reflect.Array;
import net.minecraft.client.gui.font.providers.UnihexProvider.LineData;

public class CompactUnihexContents {
   private static final boolean TEST_ROUNDTRIP = false;

   private static long extract8Bytes(byte[] arr, int off) {
      long l = 0L;

      for (int i = 0; i < 8; i++) {
         l |= (arr[off + i] & 255L) << i * 8;
      }

      return l;
   }

   private static byte extractByte(long compressed, int off) {
      return (byte)(compressed >> off * 8 & 255L);
   }

   private static long extract4Shorts(short[] arr, int off) {
      long l = 0L;

      for (int i = 0; i < 4; i++) {
         l |= (arr[off + i] & 65535L) << i * 16;
      }

      return l;
   }

   private static short extractShort(long compressed, int off) {
      return (short)(compressed >> off * 16 & 65535L);
   }

   private static void verifyRoundTrip(Object originalArray, LineData data, int shift) {
      for (int i = 0; i < 16; i++) {
         int val = Array.getInt(originalArray, i) << shift;
         int actualVal = data.line(i);
         if (val != actualVal) {
            throw new AssertionError("Value at index %d differs. Expected %08x, got %08x".formatted(i, val, actualVal));
         }
      }
   }

   public static class Bytes implements LineData {
      private final long b0;
      private final long b8;

      public Bytes(byte[] contents) {
         this.b0 = CompactUnihexContents.extract8Bytes(contents, 0);
         this.b8 = CompactUnihexContents.extract8Bytes(contents, 8);
      }

      public int line(int index) {
         if (index < 0 || index >= 16) {
            throw new ArrayIndexOutOfBoundsException();
         } else {
            return index < 8 ? CompactUnihexContents.extractByte(this.b0, index) << 24 : CompactUnihexContents.extractByte(this.b8, index - 8) << 24;
         }
      }

      public int bitWidth() {
         return 8;
      }
   }

   public static class Shorts implements LineData {
      private final long b0;
      private final long b4;
      private final long b8;
      private final long b12;

      public Shorts(short[] contents) {
         this.b0 = CompactUnihexContents.extract4Shorts(contents, 0);
         this.b4 = CompactUnihexContents.extract4Shorts(contents, 4);
         this.b8 = CompactUnihexContents.extract4Shorts(contents, 8);
         this.b12 = CompactUnihexContents.extract4Shorts(contents, 12);
      }

      public int line(int index) {
         if (index < 0 || index >= 16) {
            throw new ArrayIndexOutOfBoundsException();
         } else if (index < 4) {
            return CompactUnihexContents.extractShort(this.b0, index) << 16;
         } else if (index < 8) {
            return CompactUnihexContents.extractShort(this.b4, index - 4) << 16;
         } else {
            return index < 12 ? CompactUnihexContents.extractShort(this.b8, index - 8) << 16 : CompactUnihexContents.extractShort(this.b12, index - 12) << 16;
         }
      }

      public int bitWidth() {
         return 16;
      }
   }
}
