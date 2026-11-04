package org.codeberg.zenxarch.fastnoise;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class FastNoiseConstants {
   public static final Logger LOGGER = LoggerFactory.getLogger("zfastnoise");
   public static final String MOD_ID = "zfastnoise";

   private FastNoiseConstants() {
      throw new IllegalStateException("Utility class");
   }
}
