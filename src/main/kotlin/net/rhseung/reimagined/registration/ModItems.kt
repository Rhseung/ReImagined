package net.rhseung.reimagined.registration

import net.minecraft.item.Item
import net.minecraft.registry.Registries
import net.minecraft.registry.Registry
import net.minecraft.registry.entry.RegistryEntry
import net.minecraft.util.Identifier
import net.rhseung.reimagined.tool.gears.definition.Gear
import net.rhseung.reimagined.tool.parts.definitions.Part
import kotlin.reflect.KClass

object ModItems {
	val GEARS_LIST: List<Gear> = Gear.instanceMap.values.toList()
	val PARTS_LIST: List<Part> = Part.instanceMap.values.toList().flatten()
	
	fun <T: Item> getItems(clazz: KClass<out T>): List<T> {
		return Registries.ITEM.streamEntries().map { it.value() }.filter { clazz.isInstance(it) }.map { it as T }.toList()
	}
	
	fun getId(item: Item): Identifier {
		return Registries.ITEM.getId(item)
	}
}