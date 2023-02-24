package net.rhseung.reimagined.mixin.enchantment;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.minecraft.item.Item;
import net.rhseung.reimagined.tool.gears.definition.BasicGear;
import net.rhseung.reimagined.tool.gears.definition.Gear;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

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
public abstract class DiggerEnchantmentTargetMixin {
    @ModifyReturnValue(
        method = "isAcceptableItem(Lnet/minecraft/item/Item;)Z",
        at = @At("RETURN")
    )
    private boolean isAcceptableItem_mixin(boolean original, Item item) {
        if (item instanceof Gear gear) {
            return gear.getBelongInstance().contains(new BasicGear.MiningTool());
        } else {
            return original;
        }
    }
}
