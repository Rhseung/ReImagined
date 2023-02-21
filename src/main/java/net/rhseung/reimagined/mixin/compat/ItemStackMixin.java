package net.rhseung.reimagined.mixin.compat;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.rhseung.reimagined.tool.gears.Gear;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ItemStack.class)
public abstract class ItemStackMixin {
    @ModifyReturnValue(
            method = "getMaxDamage",
            at = @At("RETURN")
    )
    private int getMaxDamage_mixin(int original) {
        ItemStack that = (ItemStack) (Object) this;

        if (that.getItem() instanceof Gear gear) return gear.getMaxDurability(that);
        else return original;
    }

    @Inject(
            method = "isDamageable",
            at = @At("HEAD"),
            cancellable = true
    )
    private void isDamageable_mixin(CallbackInfoReturnable<Boolean> cir) {
        ItemStack that = (ItemStack) (Object) this;

        if (that.getItem() instanceof Gear) {
            cir.setReturnValue(true);
        }
        else if (!that.isEmpty() && that.getItem().getMaxDamage() > 0) {
            NbtCompound nbtCompound = that.getNbt();
            cir.setReturnValue(nbtCompound == null || !nbtCompound.getBoolean("Unbreakable"));
        } else {
            cir.setReturnValue(false);
        }
    }
}
