package net.rhseung.reimagined.mixin.tooltip;

import net.minecraft.client.gui.tooltip.BundleTooltipComponent;
import net.minecraft.client.gui.tooltip.TooltipComponent;
import net.minecraft.client.item.BundleTooltipData;
import net.minecraft.client.item.TooltipData;
import net.rhseung.reimagined.tool.gears.tooltip.GearTooltipComponent;
import net.rhseung.reimagined.tool.gears.tooltip.GearTooltipData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(TooltipComponent.class)
public interface TooltipDataToComponentMixin {
    @Inject(
            method = "of(Lnet/minecraft/client/item/TooltipData;)Lnet/minecraft/client/gui/tooltip/TooltipComponent;",
            at = @At("HEAD"),
            cancellable = true
    )
    private static void of_mixin(TooltipData data, CallbackInfoReturnable<TooltipComponent> cir) {
        if (data instanceof GearTooltipData) {
            cir.setReturnValue(new GearTooltipComponent((GearTooltipData) data));
        }
        else if (data instanceof BundleTooltipData) {
            cir.setReturnValue(new BundleTooltipComponent((BundleTooltipData) data));
        }
        else {
            throw new IllegalArgumentException("Unknown TooltipComponent from TooltipComponentMixin");
        }
    }
}
