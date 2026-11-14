Millénaire custom content — reference catalogs
==============================================

Available now:
  - core_used_blocks.json Minecraft blocks used by the shipped buildings. A sub-mod's
                          blocklist.json cannot price these (they are part of the core
                          villages' balance); see the comment at the top of the file.

This directory will eventually hold:
  - goals.txt             Registered goal IDs + descriptions
  - gathering_handlers.txt The 19 gathering handler types + usage notes
  - tags.txt              Building and villager tags + their effects
  - special_points.txt    Mock block types for NBT templates

These catalogs are not generated in the current release. To discover valid
values in the meantime, inspect the deployed built-in cultures under
millenaire-custom/cultures/ — every field you see there is by definition a
valid authoring target.
