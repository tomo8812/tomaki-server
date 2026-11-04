# Disabler configuration guide

The server config is `config/disabler-server.json`. Resource ids use the `namespace:path` format. An empty list disables that blocking category.

## `blocked_mobs`
Entity type ids that cannot spawn or join a server level. Example: `minecraft:zombie`.

## `blocked_biomes`
Biome ids replaced during new chunk generation. Replacements are climate-compatible and keep land biomes on land and water biomes in water.

## `biome_exceptions`
Biome ids that cannot be selected as replacements. `minecraft:mushroom_fields` is excluded by default because it has special terrain requirements.

## `blocked_structures`
Structure ids prevented from starting in newly generated chunks. Example: `minecraft:village_plains`.

## `blocked_dimensions`
Vanilla or modded dimension ids that entities cannot enter. Example: `minecraft:the_nether`.

## `blocked_items`
Item ids removed from generated loot, item entities, player inventories, open menus, and supported loaded storages.

## `storage_scan`
Controls periodic removal of blocked items from compatible loaded block-entity inventories.

- `enabled`: enables or disables the periodic storage scan.
- `interval_ticks`: delay between complete scan cycles. 20 ticks are approximately one second; the default is 300 ticks.
- `block_entities_per_tick`: maximum block entities processed per tick during a scan cycle.
- `skipped_namespaces`: mod namespaces whose physical storages must not be scanned. Lootr is protected internally and does not need to be listed.

Lootr personal inventories are filtered through dedicated compatibility. Its physical chests, barrels, frames, and other containers are not converted or scanned as ordinary inventories.
