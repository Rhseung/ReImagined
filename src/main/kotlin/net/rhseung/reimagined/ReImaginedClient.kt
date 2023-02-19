package net.rhseung.reimagined

import net.fabricmc.api.ClientModInitializer
import net.rhseung.reimagined.registration.ModItems
import net.rhseung.reimagined.tool.gears.util.GearHelperClient
import net.rhseung.reimagined.tool.parts.enums.PartType
import net.rhseung.reimagined.tool.parts.util.PartHelperClient

object ReImaginedClient : ClientModInitializer {
	override fun onInitializeClient() {
		GearHelperClient.textureProcess()
		PartHelperClient.textureProcess()
	}
}