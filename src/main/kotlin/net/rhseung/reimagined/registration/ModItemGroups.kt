package net.rhseung.reimagined.registration

import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup
import net.minecraft.item.ItemGroup
import net.minecraft.item.ItemStack
import net.minecraft.item.Items
import net.minecraft.text.Text
import net.minecraft.util.Identifier
import net.rhseung.reimagined.ReImagined
import net.rhseung.reimagined.ReImagined.ID
import java.util.function.Supplier

object ModItemGroups {
	val PARTS: ItemGroup = FabricItemGroup.builder(ID("parts"))
		.displayName(Text.translatable("Parts"))
		.icon { ItemStack(Items.NETHERITE_PICKAXE) }
		.build()
}