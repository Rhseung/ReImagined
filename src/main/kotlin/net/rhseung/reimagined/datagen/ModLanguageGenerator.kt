package net.rhseung.reimagined.datagen

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput
import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider
import net.minecraft.item.Item
import net.minecraft.registry.Registries
import net.rhseung.reimagined.registration.ModItems
import net.rhseung.reimagined.tool.gears.base.IGearItem
import net.rhseung.reimagined.tool.parts.base.IPartItem
import net.rhseung.reimagined.utils.Name.toDisplayName

class ModLanguageGenerator(
	dataOutput: FabricDataOutput
) : FabricLanguageProvider(dataOutput, "en_us") {
	
	override fun generateTranslations(translationBuilder: TranslationBuilder) {
		ModItems.PARTS.values.forEach { they ->
			they.forEach {
				if (it is Item) translationBuilder.add(
					it, "${it.material.name.toDisplayName()} ${it.getType().name.toDisplayName()} Part"
				)
			}
		}
		ModItems.GEARS.forEach {
			if (it is Item) translationBuilder.add(it, it.getType().name.toDisplayName())
		}
	}
}