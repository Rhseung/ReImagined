package net.rhseung.reimagined.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.item.ItemStack;
import net.rhseung.reimagined.tool.gears.base.IGearItem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import net.rhseung.reimagined.tool.gears.util.GearHelper;

@Mixin(EnchantmentHelper.class)
public abstract class MixinEnchantability {
	@ModifyExpressionValue(
			method = "generateEnchantments",
			at = @At(value = "INVOKE", target = "net/minecraft/item/Item.getEnchantability ()I")
	)
	private static int mixinGetEnchantabilityInG(int original, ItemStack stack) {
		return GearHelper.isGear(stack) ? ((IGearItem) stack.getItem()).getEnchantability(stack) : original;
	}

	@ModifyExpressionValue(
			method = "calculateRequiredExperienceLevel",
			at = @At(value = "INVOKE", target = "net/minecraft/item/Item.getEnchantability ()I")
	)
	private static int mixinGetEnchantabilityInC(int original, ItemStack stack) {
		return GearHelper.isGear(stack) ? ((IGearItem) stack.getItem()).getEnchantability(stack) : original;
	}
}