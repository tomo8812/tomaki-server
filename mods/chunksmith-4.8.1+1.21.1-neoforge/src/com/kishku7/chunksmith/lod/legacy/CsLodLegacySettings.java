package com.kishku7.chunksmith.lod.legacy;

import com.kishku7.chunksmith.lod.client.CsLodClientSettings;
import com.kishku7.chunksmith.lod.client.net.CsLodClientNet;
import com.kishku7.chunksmith.lod.net.CsLodMessages;
import java.util.List;
import java.util.Optional;

public final class CsLodLegacySettings {
   private CsLodLegacySettings() {
   }

   public static void handle(CsLodMessages.ClientSetting request) {
      if (request.action() != 0) {
         Optional<CsLodClientSettings.Setting> found = CsLodClientSettings.find(request.name());
         if (found.isEmpty()) {
            CsLodClientNet.chat("[chunksmith] no LOD client setting called '" + request.name() + "'. Known: " + String.join(", ", CsLodClientSettings.names()));
         } else {
            CsLodClientSettings.Setting setting = found.get();
            if (request.action() == 1) {
               CsLodClientNet.chat("[chunksmith] " + setting.name() + " = " + setting.read() + "  -- " + setting.help());
            } else if (!setting.write(request.value())) {
               List<String> expected = setting.kind().completions();
               CsLodClientNet.chat(
                  "[chunksmith] '"
                     + request.value()
                     + "' is not a valid value for "
                     + setting.name()
                     + (expected.isEmpty() ? " (expected a whole number)" : " (expected one of: " + String.join(", ", expected) + ")")
               );
            } else {
               CsLodClientNet.chat("[chunksmith] " + setting.name() + " = " + setting.read() + ", applied now and saved to config/chunksmith-lod.properties");
               CsLodClientNet.chat("[chunksmith] (this server is on Chunksmith 3.x; on 4.x the command is /csclient set)");
            }
         }
      } else {
         CsLodClientNet.chat("[chunksmith] LOD client settings (config/chunksmith-lod.properties):");

         for (CsLodClientSettings.Setting setting : CsLodClientSettings.all()) {
            CsLodClientNet.chat("[chunksmith]   " + setting.name() + " = " + setting.read() + "  -- " + setting.help());
         }
      }
   }
}
