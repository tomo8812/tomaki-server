@MixinConfigOption(description = "Reduces hopper lag using caching, notification systems and BlockEntity sleeping", depends = {@MixinConfigDependency(dependencyPath = "mixin.util.entity_movement_tracking"), @MixinConfigDependency(dependencyPath = "mixin.util.block_entity_retrieval"), @MixinConfigDependency(dependencyPath = "mixin.util.inventory_change_listening"), @MixinConfigDependency(dependencyPath = "mixin.util.item_component_and_count_tracking")})
package net.caffeinemc.mods.lithium.mixin.block.hopper;

import net.caffeinemc.gradle.MixinConfigDependency;
import net.caffeinemc.gradle.MixinConfigOption;
