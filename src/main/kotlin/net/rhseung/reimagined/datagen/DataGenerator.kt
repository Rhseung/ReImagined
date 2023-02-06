package net.rhseung.reimagined.datagen

import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput

object DataGenerator : DataGeneratorEntrypoint {
	override fun onInitializeDataGenerator(fabricDataGenerator: FabricDataGenerator) {
		val pack = fabricDataGenerator.createPack()
		
		pack.addProvider { output: FabricDataOutput -> ModModelGenerator(output) }
		pack.addProvider { output: FabricDataOutput -> ModRecipeGenerator(output) }
		pack.addProvider { output: FabricDataOutput -> ModLanguageGenerator(output) }
		pack.addProvider { output: FabricDataOutput -> ModLootTableGenerator(output) }
		pack.addProvider { output: FabricDataOutput, registriesFuture -> ModItemTagGenerator(output, registriesFuture) }
		pack.addProvider { output: FabricDataOutput, registriesFuture -> ModBlockTagGenerator(output, registriesFuture) }
	}
}