package net.rhseung.reimagined.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.rhseung.reimagined.tool.gears.base.IGearItem;
import net.rhseung.reimagined.tool.gears.util.GearHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ItemStack.class)
public abstract class MixinItemStack {
    @Shadow public abstract Item getItem();

    @ModifyReturnValue(
        method = "damage(ILnet/minecraft/util/math/random/Random;Lnet/minecraft/server/network/ServerPlayerEntity;)Z",
        at = @At("RETURN")
    )
    private boolean isBroken(boolean original, ItemStack stack) {
        return (stack.getItem() instanceof IGearItem) ? GearHelper.isBroken(stack) : original;
    }
}
