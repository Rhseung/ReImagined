package net.rhseung.reimagined

import net.fabricmc.api.ClientModInitializer
import net.rhseung.reimagined.tool.gears.GearClient
import net.rhseung.reimagined.tool.parts.PartClient

object ReImaginedClient : ClientModInitializer {
	override fun onInitializeClient() {
		GearClient.textureProcess()
		PartClient.textureProcess()
	}
}