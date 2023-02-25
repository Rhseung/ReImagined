package net.rhseung.reimagined

import net.fabricmc.api.ModInitializer
import net.minecraft.util.Identifier
import net.rhseung.reimagined.registration.ModRecipes
import org.slf4j.LoggerFactory

object ReImagined : ModInitializer {
	const val MOD_ID = "reimagined"
	private val LOGGER = LoggerFactory.getLogger(MOD_ID)

	override fun onInitialize() {
		ModRecipes.registerAll()
	}
	
	fun modID(path: String): Identifier {
		return Identifier(MOD_ID, path)
	}
	
	fun minecraftID(path: String): Identifier {
		return Identifier("minecraft", path)
	}
}