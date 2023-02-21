package net.rhseung.reimagined.mixin.compat;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.random.Random;
import net.rhseung.reimagined.tool.gears.Gear;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(EnchantmentHelper.class)
public abstract class EnchantabilityMixin {
	@ModifyExpressionValue(
		method = "generateEnchantments",
		at = @At(value = "INVOKE", target = "net/minecraft/item/Item.getEnchantability ()I")
	)
	private static int generateEnchantments_mixin(int original, Random random, ItemStack stack, int level, boolean treasureAllowed) {
		return stack.getItem() instanceof Gear ? ((Gear) stack.getItem()).getEnchantability(stack) : original;
	}

	@ModifyExpressionValue(
		method = "calculateRequiredExperienceLevel",
		at = @At(value = "INVOKE", target = "net/minecraft/item/Item.getEnchantability ()I")
	)
	private static int calculateRequiredExperienceLevel_mixin(int original, Random random, int slotIndex, int bookshelfCount, ItemStack stack) {
		return stack.getItem() instanceof Gear ? ((Gear) stack.getItem()).getEnchantability(stack) : original;
	}
}