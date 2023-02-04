package net.rhseung.reimagined.registration

import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents
import net.minecraft.block.Block
import net.minecraft.item.BlockItem
import net.minecraft.item.Item
import net.minecraft.item.ItemGroup
import net.minecraft.registry.Registries
import net.minecraft.registry.Registry
import net.minecraft.util.Identifier
import net.rhseung.reimagined.ReImagined

object ModBlocks {
	
	fun <T : Block> registerBlock(
		path: String,
		block: T,
		group: ItemGroup? = null
	): T {
		if (group != null) {
			ItemGroupEvents.modifyEntriesEvent(group)
				.register(ItemGroupEvents.ModifyEntries { entries -> entries.add(block) })
		}
		Registry.register(Registries.ITEM, Identifier.of(ReImagined.MOD_ID, path), BlockItem(block, Item.Settings()))
		return Registry.register(Registries.BLOCK, Identifier.of(ReImagined.MOD_ID, path), block)
	}
}