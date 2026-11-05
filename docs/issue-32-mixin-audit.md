# Issue #32: Mixin audit

## Scope and outcome

Audited all 13 current `maki-*` JARs, all 13 registered JSON configs and all
52 registered Mixin classes. Primary source inspection used `decompiled` commit
`1e4a5e0e` (generated from main `a4f0a322`); its class inventory matched the shipped
JARs. Classfiles, not decompiler annotation presentation, determine retention.

One mod required changes: `maki-lod-cpu-online-fix` 1.0.1 -> 1.0.2. The other
12 JARs are byte-for-byte unchanged. No policy algorithm or third-party mod changed.

## Confirmed defects and fixes

1. `maki-lod-cpu-online-fix.mixins.json` reserved `maki.lodcpu.onlinefix`, which
   also contained `@Mod MakiLodCpuOnlineFix`. Reproduced a dedicated-server failure
   during mod-container initialization: `IllegalClassLoadError ... is in a defined
   mixin package ... and cannot be referenced directly`. Move only
   `AdaptivePolicyMixin` into `.onlinefix.mixin` and update the JSON package.
2. The two `@Unique` fields in `AdaptivePolicyMixin` were emitted in
   `RuntimeInvisibleAnnotations`, but Sponge's real `@Unique` is runtime-retained.
   Add a reproducible Java 21 / Gradle 8.10.2 / ModDev 2.0.148 build against
   NeoForge 21.1.262 and the shipped `maki-lod-cpu-1.3.1.jar`. Real Mixin API
   compilation corrects these annotations; no compile-only annotation stubs remain
   in this build. Handler source bodies are unchanged.
3. The old audit parsed localized `javap` output, mistook `ModifyArgs` for
   `ModifyArg`, and did not inspect package boundaries or `@Unique`. It also failed
   decoding Japanese strings on Windows. The audit now reads structured classfile
   attributes, checks top-level `@Mixin`, nested annotations, JSON/TOML registration,
   bundled API stubs and reserved package contents. With `--annotation-api`, it
   checks retention, declaration targets and encoded element types against the
   exact runtime API. Regression tests cover reserved packages, Unicode/nested
   annotations and scalar-vs-array annotation ABI.

## Validation

- `clean check jar` succeeded for the changed mod with Java 21 and Gradle 8.10.2.
- All 52 registered classes pass classfile/packaging checks and API comparison
  against `net.fabricmc:sponge-mixin:0.16.4+mixin.0.8.7` (the actual NeoForge
  21.1.262 runtime dependency). No MixinExtras annotations are used by these 52
  classes. No Mixin API/stub classes are bundled in the custom JARs.
- Checked all available target methods, descriptors, shadow members and accessor
  fields against the matching mod JARs, Connector's Mojmap-transformed VS/Eureka
  JARs, and the NeoForge 21.1.262 compiled Minecraft artifact. All 47 classes with
  available target binaries matched. The five Diorama client targets are unavailable
  in this server repository; see the limitations below.
- Installed NeoForge 21.1.262 in an isolated directory with copies of the repository's
  complete `mods/` and `config/`, Java 21, a new world, localhost-only port 25583,
  view/simulation distance 2 and 4 GiB heap. No existing server/world was touched.
- Baseline reproduced the exact online-fix `IllegalClassLoadError`.
- Fixed deployment reached `Done (21.347s)!` and ticked successfully with the
  transformed `AdaptivePolicy`. A second run with a temporary validation-only mod
  reached `Done (8.290s)!`, loaded all 30 distinct server target classes using
  `Class.forName(name, false, gameClassLoader)`, and confirmed all 39 common/server
  Mixins applied. The probe requested a normal server stop; all dimensions saved,
  exit code 0. The probe is **not** in the deployment JARs.
- Runtime evidence is in [issue-32-runtime.txt](issue-32-runtime.txt). This includes
  all 39 Mixin application messages and the 30 successful target loads. Runtime
  application validates actual injection points, descriptors, ordinals and required
  counts for the server path, beyond source compilation.
- `python -m unittest discover -s .github/scripts -p 'test_mixin*.py'`: 3 tests pass.
- CI builds the changed mod and both previously rebuilt mods, audits rebuilt JARs,
  and checks the shipped JARs against the real runtime annotation API.

## Boundaries, optional targets and interactions

- Each JSON is registered through `[[mixins]]` in its JAR's NeoForge metadata.
  All registered classes exist. No config plugin is used. Required configs use
  JAVA_21 and compatible minimum Mixin versions. Server-only hooks are in `server`;
  13 client-only hooks are in `client` and are not prepared on the dedicated server.
- All ordinary entrypoints/helpers are outside reserved packages after the fix.
  Ship-edge/footsteps intentionally have no `@Mod` entrypoint. No concrete Mixin is
  instantiated or directly loaded by ordinary runtime code. The references from
  AssemblyTracking and ClientSharing are valid accessor-interface casts; the
  interfaces carry `@Mixin` and their getter fields/descriptors were inspected
  where target binaries are available. There are no Invokers.
- `AirshipSleepCompatMixin` uses `@Pseudo`, a string target and `require=0` for
  optional `maki.airship.SleepCompat`. `require=0` alone is not absence protection.
  The optional Millenaire bridge in chunk-cache uses reflection rather than an
  unconditional Mixin target. Other external targets have required dependencies
  on the relevant side. This audit does not claim compatibility with every version
  permitted by those ranges or every possible missing-mod combination.
- Airship and anchor both inject into LivingEntity tick/wake and EntityDragger:
  both drag guards return false for the authoritative sleep anchor. Anchor replaces
  the legacy SleepCompat follow/anchored methods. Combined application succeeded;
  multiplayer sleep/movement behavior was not play-tested.
- Idle-pregen and Millenaire-LOD both hook VillageSpawnQueue.trySpawnNext HEAD;
  cancellation can suppress subsequent work while idle pregen is active. They do
  not redirect the same instruction. Their combined application succeeded.
- LOD CPU hooks GenerationTask.run HEAD; ship-save redirects LodHorizon.arm and
  hooks run RETURN. Cache/idle hook distinct MinecraftServer tick points. All were
  applied together. No custom `@Overwrite` or competing same-site Redirect pair
  was found. Priority 500 anchor and priority 1100 ship-edge remain unchanged.
- Minecraft hooks use the named 1.21.1 runtime; third-party hooks use remap=false.
  Raw Fabric intermediary names were not mistaken for Connector runtime names.

## Limitations and separate observations

- A dedicated server cannot apply the 13 client-only Mixins. Eight airship client
  Mixins passed static target/member checks, including the actual PacketUtils
  invocation and first PoseStack.pushPose injection sites. A client launch/play test
  was not performed. Diorama itself is absent from this server repository and the
  scoped local caches; its five client Mixins have verified config/classfile/package
  structure but their target/injection compatibility is **not verified**. Their
  client-only registration is safe for the tested dedicated server.
- An additional run with global `mixin.debug.countInjections=true` failed in
  third-party Valkyrien Skies `feature.screen_distance_check.MixinScreenHandler`:
  its optional redirect expected 1 but matched 0. That debug option enforces
  `expect` counts beyond normal `require`. Normal settings successfully boot and
  apply every custom server Mixin. No third-party/Connector changes were made.
- The successful test still prints third-party optional-integration warnings and
  Millenaire content warnings; these are not claimed fixed by this Mixin audit.

## Complete registered-class inventory

`server APPLY` means observed combined runtime application. `client static` means
target members were checked but no client runtime was launched. Every row passed
the annotation/config/package audit after the fix. Short names below retain the
full target names to disambiguate different mods' identically named Mixins.

| JAR | Registered Mixin | Side | Target | Target validation |
| --- | --- | --- | --- | --- |
| `maki-airship-compat-1.0.13.jar` | `maki.airship.mixin.LivingSleepMixin` | mixins | `net.minecraft.world.entity.LivingEntity` | server APPLY |
| `maki-airship-compat-1.0.13.jar` | `maki.airship.mixin.SleepDragMixin` | mixins | `org.valkyrienskies.mod.common.util.EntityDragger` | server APPLY |
| `maki-airship-compat-1.0.13.jar` | `maki.airship.mixin.ClientPassengerMixin` | client | `net.minecraft.client.multiplayer.ClientPacketListener` | client static |
| `maki-airship-compat-1.0.13.jar` | `maki.airship.mixin.ClientTickMixin` | client | `net.minecraft.client.Minecraft` | client static |
| `maki-airship-compat-1.0.13.jar` | `maki.airship.mixin.ClientRenderMixin` | client | `net.minecraft.client.renderer.GameRenderer` | client static |
| `maki-airship-compat-1.0.13.jar` | `maki.airship.mixin.MountedCameraMixin` | client | `net.minecraft.client.Camera` | client static |
| `maki-airship-compat-1.0.13.jar` | `maki.airship.mixin.SleepingRenderMixin` | client | `net.minecraft.client.renderer.entity.LivingEntityRenderer` | client static |
| `maki-airship-compat-1.0.13.jar` | `maki.airship.mixin.EntityLightMixin` | client | `net.minecraft.client.renderer.entity.EntityRenderer` | client static |
| `maki-airship-compat-1.0.13.jar` | `maki.airship.mixin.ShipShiftMixin` | client | `net.minecraft.client.player.LocalPlayer` | client static |
| `maki-airship-compat-1.0.13.jar` | `maki.airship.mixin.ShipZoomResetMixin` | client | `org.valkyrienskies.mod.client.ShipCameraZoom` | client static |
| `maki-airship-foundation-compat-1.0.0.jar` | `maki.fndseat.mixin.FoundationSeatUtilMixin` | mixins | `net.satisfy.foundation.seat.SeatUtil` | server APPLY |
| `maki-airship-foundation-compat-1.0.0.jar` | `maki.fndseat.mixin.FoundationChairMixin` | mixins | `net.satisfy.foundation.seat.ChairEntity` | server APPLY |
| `maki-airship-foundation-compat-1.0.0.jar` | `maki.fndseat.mixin.FurnitureVehicleMixin` | mixins | `org.valkyrienskies.mod.common.entity.ShipMountingEntity` | server APPLY |
| `maki-assembly-tracking-1.0.0.jar` | `maki.shiptracking.mixin.ChunkMapAccess` | server | `net.minecraft.server.level.ChunkMap` | server APPLY |
| `maki-assembly-tracking-1.0.0.jar` | `maki.shiptracking.mixin.TrackedEntityAccess` | server | `net.minecraft.server.level.ChunkMap$TrackedEntity` | server APPLY |
| `maki-assembly-tracking-1.0.0.jar` | `maki.shiptracking.mixin.EurekaAssemblyMixin` | server | `org.valkyrienskies.eureka.util.ShipAssembler` | server APPLY |
| `maki-chunksmith-chunk-cache-1.0.1.jar` | `maki.chunksmithcache.mixin.LodSupportMixin` | mixins | `com.kishku7.chunksmith.lod.LodSupport` | server APPLY |
| `maki-chunksmith-chunk-cache-1.0.1.jar` | `maki.chunksmithcache.mixin.ChunkMapReadMixin` | mixins | `net.minecraft.server.level.ChunkMap` | server APPLY |
| `maki-chunksmith-chunk-cache-1.0.1.jar` | `maki.chunksmithcache.mixin.ChunkMapSaveMixin` | mixins | `net.minecraft.server.level.ChunkMap` | server APPLY |
| `maki-chunksmith-chunk-cache-1.0.1.jar` | `maki.chunksmithcache.mixin.SectionStorageMixin` | mixins | `net.minecraft.world.level.chunk.storage.SectionStorage` | server APPLY |
| `maki-chunksmith-chunk-cache-1.0.1.jar` | `maki.chunksmithcache.mixin.EntityStorageMixin` | mixins | `net.minecraft.world.level.chunk.storage.EntityStorage` | server APPLY |
| `maki-chunksmith-chunk-cache-1.0.1.jar` | `maki.chunksmithcache.mixin.ServerLevelUnloadMixin` | mixins | `net.minecraft.server.level.ServerLevel` | server APPLY |
| `maki-chunksmith-chunk-cache-1.0.1.jar` | `maki.chunksmithcache.mixin.MinecraftServerMixin` | mixins | `net.minecraft.server.MinecraftServer` | server APPLY |
| `maki-diorama-share-1.0.0.jar` | `maki.mapshare.mixin.StoreAccess` | client | `dev.diorama.waypoint.WaypointStore` | client target unavailable |
| `maki-diorama-share-1.0.0.jar` | `maki.mapshare.mixin.StoreMixin` | client | `dev.diorama.waypoint.WaypointStore` | client target unavailable |
| `maki-diorama-share-1.0.0.jar` | `maki.mapshare.mixin.WaypointMixin` | client | `dev.diorama.waypoint.Waypoint` | client target unavailable |
| `maki-diorama-share-1.0.0.jar` | `maki.mapshare.mixin.DeathMixin` | client | `dev.diorama.waypoint.Waypoints` | client target unavailable |
| `maki-diorama-share-1.0.0.jar` | `maki.mapshare.mixin.SettingsTabMixin` | client | `dev.diorama.SettingsScreen$SettingsTab` | client target unavailable |
| `maki-idle-pregen-1.0.1.jar` | `maki.idlepregen.mixin.MinecraftServerMixin` | server | `net.minecraft.server.MinecraftServer` | server APPLY |
| `maki-idle-pregen-1.0.1.jar` | `maki.idlepregen.mixin.VillageManagerMixin` | server | `org.millenaire.village.VillageManager` | server APPLY |
| `maki-idle-pregen-1.0.1.jar` | `maki.idlepregen.mixin.VillageSpawnQueueMixin` | server | `org.millenaire.world.VillageSpawnQueue` | server APPLY |
| `maki-lod-cpu-1.3.1.jar` | `maki.lodcpu.mixin.GenerationTaskMixin` | server | `com.kishku7.chunksmith.GenerationTask` | server APPLY |
| `maki-lod-cpu-1.3.1.jar` | `maki.lodcpu.mixin.TaskLoaderMixin` | server | `com.kishku7.chunksmith.util.TaskLoader` | server APPLY |
| `maki-lod-cpu-online-fix-1.0.2.jar` | `maki.lodcpu.onlinefix.mixin.AdaptivePolicyMixin` | server | `maki.lodcpu.AdaptivePolicy` | server APPLY |
| `maki-millenaire-lod-compat-1.0.4.jar` | `maki.millenairelod.mixin.MillenaireMixin` | mixins | `org.millenaire.Millenaire` | server APPLY |
| `maki-millenaire-lod-compat-1.0.4.jar` | `maki.millenairelod.mixin.LoneCandidateAccessor` | mixins | `org.millenaire.world.VillageSpawnQueue$LBCandidate` | server APPLY |
| `maki-millenaire-lod-compat-1.0.4.jar` | `maki.millenairelod.mixin.SpawnQueueMixin` | mixins | `org.millenaire.world.VillageSpawnQueue` | server APPLY |
| `maki-millenaire-lod-compat-1.0.4.jar` | `maki.millenairelod.mixin.VillageSpawnerMixin` | mixins | `org.millenaire.world.VillageSpawner` | server APPLY |
| `maki-millenaire-lod-compat-1.0.4.jar` | `maki.millenairelod.mixin.VillageManagerMixin` | mixins | `org.millenaire.village.VillageManager` | server APPLY |
| `maki-millenaire-lod-compat-1.0.4.jar` | `maki.millenairelod.mixin.VillageMixin` | mixins | `org.millenaire.village.Village` | server APPLY |
| `maki-millenaire-lod-compat-1.0.4.jar` | `maki.millenairelod.mixin.BuildingInstanceMixin` | mixins | `org.millenaire.building.BuildingInstance` | server APPLY |
| `maki-millenaire-lod-compat-1.0.4.jar` | `maki.millenairelod.mixin.HorizonGuardMixin` | mixins | `com.kishku7.chunksmith.lod.HorizonGuard` | server APPLY |
| `maki-millenaire-lod-compat-1.0.4.jar` | `maki.millenairelod.mixin.ExploredLodMixin` | mixins | `com.kishku7.chunksmith.lod.ExploredLodGuard` | server APPLY |
| `maki-ship-anchor-fix-1.1.1.jar` | `maki.shipanchor.mixin.EntityPassengerAnchorMixin` | mixins | `net.minecraft.world.entity.Entity` | server APPLY |
| `maki-ship-anchor-fix-1.1.1.jar` | `maki.shipanchor.mixin.LivingSleepAnchorMixin` | mixins | `net.minecraft.world.entity.LivingEntity` | server APPLY |
| `maki-ship-anchor-fix-1.1.1.jar` | `maki.shipanchor.mixin.SleepDragMixin` | mixins | `org.valkyrienskies.mod.common.util.EntityDragger` | server APPLY |
| `maki-ship-anchor-fix-1.1.1.jar` | `maki.shipanchor.mixin.AirshipSleepCompatMixin` | mixins | `maki.airship.SleepCompat` | server APPLY |
| `maki-ship-anchor-fix-1.1.1.jar` | `maki.shipanchor.mixin.ServerGamePacketListenerBaselineMixin` | mixins | `net.minecraft.server.network.ServerGamePacketListenerImpl` | server APPLY |
| `maki-ship-edge-1.0.0.jar` | `maki.shipedge.mixin.ShipEdgeMixin` | mixins | `net.minecraft.world.entity.player.Player` | server APPLY |
| `maki-ship-footsteps-1.0.1.jar` | `maki.shipsteps.mixin.ShipStepMixin` | mixins | `net.minecraft.world.entity.Entity` | server APPLY |
| `maki-ship-save-1.0.2.jar` | `maki.shipsave.mixin.LodHorizonMixin` | mixins | `com.kishku7.chunksmith.lod.LodHorizon` | server APPLY |
| `maki-ship-save-1.0.2.jar` | `maki.shipsave.mixin.GenerationTaskMixin` | mixins | `com.kishku7.chunksmith.GenerationTask` | server APPLY |

## Shipped custom JAR hashes

| JAR | SHA-256 |
| --- | --- |
| `maki-airship-compat-1.0.13.jar` | `862628a6634b42776fe1a8b926be6175846e383b1b1a2bcdfc90c6a0a97d8baf` |
| `maki-airship-foundation-compat-1.0.0.jar` | `20c9db310f2461f4d134bbf65b0aa5b44a8340ad153f2e594d9064478c0ff305` |
| `maki-assembly-tracking-1.0.0.jar` | `134af4d11ee35c3884307ef110f349b2e78d95bf39b5cebd88a5e625ec5555ee` |
| `maki-chunksmith-chunk-cache-1.0.1.jar` | `fc5532d06aa483008a4726842708bc5583b6515f529c7302af8bbd711665fe02` |
| `maki-diorama-share-1.0.0.jar` | `43a3d604ff3b65c1f198d877575cafa539bdf6972281cb4afbe9980675aa2939` |
| `maki-idle-pregen-1.0.1.jar` | `29c9b8699aba8278b3c334a1b6166c353fdeafff316956588286de25341bc5d9` |
| `maki-lod-cpu-1.3.1.jar` | `ccd3c8637f51d27012c65fdcef115b294ff3df70413224f88e6dec581afedcf4` |
| `maki-lod-cpu-online-fix-1.0.2.jar` | `7ff011e71149398c98a633b8a6e2c3ba94ccd1793b80ab7e9508d41c7746b911` |
| `maki-millenaire-lod-compat-1.0.4.jar` | `cdbd70cf4e3e803e4a74e14c60d75793b1ae894e34c01f23b02f9e330db9b9a6` |
| `maki-ship-anchor-fix-1.1.1.jar` | `96be6d6b107a49326a5ed3cfecbd0d85374e159c217f37c6ed521b6b6715dffe` |
| `maki-ship-edge-1.0.0.jar` | `8117779ea276655a3a0079079f290a93faee3ba6404270b40649ea263e891c72` |
| `maki-ship-footsteps-1.0.1.jar` | `6a6b5151989d7ebf2dc3815aea319852b44f796a63e76f4fc395d05b89dcafbe` |
| `maki-ship-save-1.0.2.jar` | `4c218f4d8ebcbb320854f6100bd1b26e8d2746021ba3ff5b56f9041f68bcf50d` |

## Reproduce static checks

```sh
gradle -p custom-mods/maki-lod-cpu-online-fix clean check jar
python3 .github/scripts/audit_mixin_annotations.py --annotation-api /path/to/sponge-mixin-0.16.4+mixin.0.8.7.jar --json-output audit.json
python3 -m unittest discover -s .github/scripts -p 'test_mixin*.py'
```

For a runtime repeat, install the specified NeoForge version into a disposable
server directory, copy the current mods/config, use a fresh world and localhost,
then start with `-Dmixin.debug.verbose=true -Dmixin.debug.export=true` and the
installer's platform argument file. A temporary `ServerStartedEvent` listener can
load the 30 targets listed in the evidence file using `Class.forName` without
initialization, then call `server.halt(false)`. Do not deploy that probe to production.
