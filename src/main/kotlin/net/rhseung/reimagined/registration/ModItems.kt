package net.rhseung.reimagined.registration

import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents
import net.minecraft.item.Item
import net.minecraft.item.ItemGroup
import net.minecraft.registry.Registries
import net.minecraft.registry.Registry
import net.minecraft.util.Identifier
import net.rhseung.reimagined.ReImagined
import net.rhseung.reimagined.tool.gears.PickaxeGear
import net.rhseung.reimagined.tool.gears.enums.GearType
import net.rhseung.reimagined.tool.parts.BindingPart
import net.rhseung.reimagined.tool.parts.HandlePart
import net.rhseung.reimagined.tool.parts.PickaxeHeadPart
import net.rhseung.reimagined.tool.parts.base.BasicPartItem
import net.rhseung.reimagined.tool.parts.enums.PartType
import net.rhseung.reimagined.utils.Text.pathName

object ModItems {
	val GEARS_MAP = GearType.getValues()
		.associateWith { registerItem("gear/${it.name.pathName()}", PickaxeGear()) }
	var GEARS = GEARS_MAP.values.toList()
	val PARTS: Map<PartType, List<BasicPartItem>> = mapOf(
		PartType.PICKAXE_HEAD to PickaxeHeadPart.registerAll(),
		PartType.BINDING to BindingPart.registerAll(),
		PartType.HANDLE to HandlePart.registerAll()
	)
	
	fun <T : Item> registerItem(
		path: String,
		item: T,
		group: ItemGroup? = null,
	): T {
		if (group != null) {
			ItemGroupEvents.modifyEntriesEvent(group)
				.register(ItemGroupEvents.ModifyEntries { entries -> entries.add(item) })
		}
		return Registry.register(Registries.ITEM, Identifier.of(ReImagined.MOD_ID, path), item)
	}
}
