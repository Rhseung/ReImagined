package net.rhseung.reimagined

import com.llamalad7.mixinextras.MixinExtrasBootstrap
import net.fabricmc.api.ModInitializer
import net.fabricmc.loader.api.entrypoint.PreLaunchEntrypoint
import net.rhseung.reimagined.registration.ModItems
import net.rhseung.reimagined.registration.ModItems.PARTS
import net.rhseung.reimagined.registration.ModRecipes
import net.rhseung.reimagined.tool.parts.enums.PartType
import org.slf4j.LoggerFactory

object ReImagined : ModInitializer {
	const val MOD_ID = "reimagined"
	val LOGGER = LoggerFactory.getLogger(ReImagined.MOD_ID)

	override fun onInitialize() {
//		println(PartType.PICKAXE_HEAD)
		ModRecipes.registerAll()
	}
}