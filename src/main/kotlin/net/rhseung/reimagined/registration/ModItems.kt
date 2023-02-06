package net.rhseung.reimagined.registration

import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents
import net.minecraft.item.Item
import net.minecraft.item.ItemGroup
import net.minecraft.registry.Registries
import net.minecraft.registry.Registry
import net.minecraft.util.Identifier
import net.rhseung.reimagined.ReImagined
import net.rhseung.reimagined.tool.gears.PickaxeGear
import net.rhseung.reimagined.tool.gears.base.IGearItem
import net.rhseung.reimagined.tool.parts.BindingPart
import net.rhseung.reimagined.tool.parts.HandlePart
import net.rhseung.reimagined.tool.parts.PickaxeHeadPart
import net.rhseung.reimagined.tool.parts.base.IPartItem
import net.rhseung.reimagined.tool.parts.enums.PartType
import kotlin.math.PI

object ModItems {
	val PICKAXE = registerItem("pickaxe", PickaxeGear())
	
	val PICKAXE_HEAD_PARTS = PickaxeHeadPart.registerAll()
	val BINDING_PARTS = BindingPart.registerAll()
	val HANDLE_PARTS = HandlePart.registerAll()
	
	val GEARS: List<IGearItem> = listOf(PICKAXE)
	val PARTS: Map<PartType, List<IPartItem>> = mapOf(
		PartType.PICKAXE_HEAD to PICKAXE_HEAD_PARTS,
		PartType.BINDING to BINDING_PARTS,
		PartType.HANDLE to HANDLE_PARTS
	)
	
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