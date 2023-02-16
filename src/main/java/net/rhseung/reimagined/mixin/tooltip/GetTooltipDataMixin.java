package net.rhseung.reimagined.mixin.tooltip;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.minecraft.client.item.TooltipData;
import net.minecraft.item.*;
import net.rhseung.reimagined.tool.gears.base.BasicGearItem;
import net.rhseung.reimagined.tool.gears.tooltip.GearTooltipData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.Optional;

@Mixin(Item.class)
public abstract class GetTooltipDataMixin {
    @ModifyReturnValue(
            method = "getTooltipData",
            at = @At("RETURN")
    )
    public Optional<TooltipData> getTooltipData_mixin(Optional<TooltipData> original, ItemStack stack) {
        if (stack.getItem() instanceof BasicGearItem gear) {
            return Optional.of(new GearTooltipData(gear, stack));
        }
        else return original;
    }
}