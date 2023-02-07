package net.rhseung.reimagined.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.minecraft.enchantment.EnchantmentTarget;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.item.Item;
import net.minecraft.item.MiningToolItem;
import net.rhseung.reimagined.tool.gears.PickaxeGear;
import net.rhseung.reimagined.tool.gears.base.IMiningGearItem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * $1: ARMOR
 * $2: BREAKABLE
 * $3: BOW
 * $4: WEARABLE
 * $5: CROSSBOW
 * $6: VANISHABLE
 * $7: ARMOR_FEET
 * $8: ARMOR_LEGS
 * $9: ARMOR_CHEST
 * $10: ARMOR_HEAD
 * $11: WEAPON
 * $12: DIGGER
 * $13: FISHING_ROD
 * $14: TRIDENT
 */
@Mixin(targets = {"net/minecraft/enchantment/EnchantmentTarget$12"})
public class MixinEnchantmentTarget {
    @ModifyReturnValue(
        method = "isAcceptableItem(Lnet/minecraft/item/Item;)Z",
        at = @At("RETURN")
    )
    private boolean diggerEnchantmentExpand(boolean original, Item item) {
        return original || item instanceof IMiningGearItem;
    }

//    @Inject(
//        method = "isAcceptableItem(Lnet/minecraft/item/Item;)Z",
//        at = @At("RETURN"),
//        cancellable = true
//    )
//    private void diggerEnchantmentExpand(Item item, CallbackInfoReturnable<Boolean> cir) {
//        cir.setReturnValue(cir.getReturnValue() || item instanceof IMiningGearItem);
//    }
}
