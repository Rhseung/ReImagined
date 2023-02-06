package net.rhseung.reimagined.utils

import net.minecraft.entity.LivingEntity
import net.minecraft.sound.SoundCategory
import net.minecraft.sound.SoundEvent
import net.minecraft.sound.SoundEvents

object Sound {
	fun play(
		player: LivingEntity,
		sound: SoundEvent,
		category: SoundCategory,
		volume: Float = 1F,
		pitch: Float = 1F
	) {
		player.world.playSoundFromEntity(null, player, sound, category, volume, pitch)
	}
}