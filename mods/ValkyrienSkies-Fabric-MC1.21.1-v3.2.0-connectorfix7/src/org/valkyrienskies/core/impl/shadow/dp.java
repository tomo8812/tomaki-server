package org.valkyrienskies.core.impl.shadow;

import java.util.Arrays;
import java.util.HashSet;

enum dp {
   AllVersions(new cU.a[]{cU.a.V4, cU.a.V6, cU.a.V7, cU.a.V201909, cU.a.V202012}),
   MinV6(new cU.a[]{cU.a.V6, cU.a.V7, cU.a.V201909, cU.a.V202012}),
   MinV7(new cU.a[]{cU.a.V7, cU.a.V201909, cU.a.V202012}),
   MaxV201909(new cU.a[]{cU.a.V4, cU.a.V6, cU.a.V7, cU.a.V201909}),
   MinV201909(new cU.a[]{cU.a.V201909, cU.a.V202012}),
   MinV202012(new cU.a[]{cU.a.V202012});

   private static final cU specVersion = new cU();
   private final cU.a[] versionFlags;

   dp(cU.a[] var3) {
      this.versionFlags = var3;
   }

   final long a() {
      return specVersion.a(new HashSet<>(Arrays.asList(this.versionFlags)));
   }
}
