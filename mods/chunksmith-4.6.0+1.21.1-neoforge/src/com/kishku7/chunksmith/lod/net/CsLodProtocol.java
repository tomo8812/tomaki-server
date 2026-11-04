package com.kishku7.chunksmith.lod.net;

public final class CsLodProtocol {
   public static final String NAMESPACE = "chunksmith";
   public static final String CHANNEL = "lod";
   public static final int VERSION = 4;
   public static final String HTTP_PREFIX = "/lod/";
   public static final String HEADER_TOKEN = "X-Chunksmith-Token";
   public static final long TOKEN_TTL_MILLIS = 600000L;
   public static final int DEFAULT_RADIUS_BLOCKS = 256;
   public static final int MAX_HELLO_DIMENSIONS = 4096;
   public static final int MAX_INDEX_REGIONS = 65536;
   public static final int MAX_SLICE_BYTES = 1048576;
   public static final int MAX_PALETTE_SIZE = 65536;
   public static final int MAX_SECTIONS = 256;
   public static final int MAX_RECORD_BYTES = 33554432;
   public static final byte C2S_HELLO = 1;
   public static final byte C2S_REQUEST_INDEX = 2;
   public static final byte C2S_REQUEST_REGIONS = 3;
   public static final byte C2S_CANCEL = 4;
   public static final byte C2S_REQUEST_SUMMARY = 5;
   public static final byte S2C_SUMMARY = 105;
   public static final byte S2C_HELLO = 101;
   public static final byte S2C_INDEX = 102;
   public static final byte S2C_CHUNK = 103;
   public static final byte S2C_DONE = 104;
   public static final byte S2C_CLIENT_SETTING = 106;
   public static final byte SETTING_LIST = 0;
   public static final byte SETTING_SHOW = 1;
   public static final byte SETTING_SET = 2;

   private CsLodProtocol() {
   }

   public static int httpPort(int gamePort) {
      int port = gamePort + 1;
      return port > 65535 ? 0 : port;
   }

   public static int httpPort(int gamePort, int configured) {
      if (configured == 0) {
         return httpPort(gamePort);
      } else {
         return configured != gamePort && configured >= 1024 && configured <= 65535 ? configured : 0;
      }
   }
}
