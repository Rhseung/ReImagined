package net.rhseung.reimagined.registration

import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents
import net.minecraft.item.Item
import net.minecraft.item.ItemGroup
import net.minecraft.registry.Registries
import net.minecraft.registry.Registry
import net.minecraft.util.Identifier
import net.rhseung.reimagined.ReImagined
import net.rhseung.reimagined.tool.parts.PickaxeHeadPart

object ModItems {
	fun registerAll() {
		ReImagined.LOGGER.debug("ModItems registering...")
		PickaxeHeadPart.registerAll()
	}
	
	fun <T : Item> registerItem(
		path: String,
		item: T,
		group: ItemGroup? = null
	): T {
		if (group != null) {
			ItemGroupEvents.modifyEntriesEvent(group)
				.register(ItemGroupEvents.ModifyEntries { entries -> entries.add(item) })
		}
		return Registry.register(Registries.ITEM, Identifier.of(ReImagined.MOD_ID, path), item)
	}
}