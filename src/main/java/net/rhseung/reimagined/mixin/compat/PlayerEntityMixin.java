package net.rhseung.reimagined.mixin.compat;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.SwordItem;
import net.rhseung.reimagined.tool.gears.definition.BasicGear;
import net.rhseung.reimagined.tool.gears.definition.Gear;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;

@Mixin(PlayerEntity.class)
public abstract class PlayerEntityMixin {
    @WrapOperation(
        method = "attack",
        constant = @Constant(classValue = SwordItem.class)
    )
    private boolean bl4_mixin(Object obj, Operation<Boolean> original) {
        var item = (Item) obj;

        if (item instanceof Gear gear) {
            return gear.getBelongInstance().contains(new BasicGear.MeleeWeapon());
        } else {
            return original.call(obj);
        }
    }
}
