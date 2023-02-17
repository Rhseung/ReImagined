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
import net.rhseung.reimagined.tool.Material
import net.rhseung.reimagined.tool.gears.base.BasicGearItem
import net.rhseung.reimagined.tool.gears.enums.GearType
import net.rhseung.reimagined.tool.parts.PickaxeHeadPart
import net.rhseung.reimagined.tool.parts.base.BasicPartItem
import net.rhseung.reimagined.tool.parts.enums.PartType
import net.rhseung.reimagined.utils.Text.pathName
import kotlin.reflect.full.primaryConstructor

object RegistryHelper {
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
	
	fun registerGear(gearType: GearType): BasicGearItem {
		return registerItem("gear/${gearType.name.pathName()}", gearType.instance())
	}
	
	fun registerParts(partType: PartType): List<BasicPartItem> {
		val ret = mutableListOf<BasicPartItem>()
		
		for (material in Material.getValues()) {
			if (partType !in material.canParts) continue
			
			ret.add(registerItem(
				"parts/${partType.name.pathName()}_${material.name.pathName()}",
				partType.instance(material), ModItemGroups.PARTS
			))
		}
		
		return ret
	}
	
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