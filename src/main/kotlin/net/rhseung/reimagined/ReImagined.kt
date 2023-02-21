package net.rhseung.reimagined

import net.fabricmc.api.ModInitializer
import net.rhseung.reimagined.registration.ModItems
import net.rhseung.reimagined.registration.ModRecipes
import net.rhseung.reimagined.tool.gears.BasicGear
import net.rhseung.reimagined.tool.gears.Gear
import org.slf4j.LoggerFactory

object ReImagined : ModInitializer {
	const val MOD_ID = "reimagined"
	private val LOGGER = LoggerFactory.getLogger(MOD_ID)

	override fun onInitialize() {
		ModRecipes.registerAll()
	}
}