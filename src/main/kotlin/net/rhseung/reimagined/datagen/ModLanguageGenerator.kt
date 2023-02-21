package net.rhseung.reimagined.datagen

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput
import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider
import net.rhseung.reimagined.registration.ModItems
import net.rhseung.reimagined.utils.Text.displayName

class ModLanguageGenerator(
	dataOutput: FabricDataOutput
) : FabricLanguageProvider(dataOutput, "en_us") {
	
	override fun generateTranslations(translationBuilder: TranslationBuilder) {
		ModItems.PARTS_LIST.forEach {
			translationBuilder.add(it, "${it.material.name.displayName()} ${it.className.displayName()} Part")
		}
		ModItems.GEARS_LIST.forEach {
			translationBuilder.add(it, "%s " + it.className.displayName())
		}
	}
}