package net.rhseung.reimagined

import com.llamalad7.mixinextras.MixinExtrasBootstrap
import net.fabricmc.loader.api.entrypoint.PreLaunchEntrypoint

object ReImaginedPreLaunch : PreLaunchEntrypoint {
	override fun onPreLaunch() {
		MixinExtrasBootstrap.init();
	}
}