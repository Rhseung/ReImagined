package net.rhseung.reimagined.tool

import net.minecraft.item.Item

enum class Trait {
	Fireproof;
	// todofar: trait 더 만들기
	//  - 바닐라틱한 것만 만들기
	//  - 일단은 Item.Settings에 있는 거만
	
	fun toSetting() = when (this) {
		Fireproof -> Item.Settings().fireproof()
    }
}