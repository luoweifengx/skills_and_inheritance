package luowei.fengxskillsandinter.mixin;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import luowei.fengxskillsandinter.weapon.AttributeModifierHelper;

/**
 * 玩家实体Mixin
 * 应用武器的打磨伤害加成
 */
@Mixin(PlayerEntity.class)
public class PlayerEntityMixin {
    
    @ModifyVariable(
        method = "attack",
        at = @At(value = "STORE", ordinal = 0),
        ordinal = 0
    )
    private float applyPolishDamageBonus(float damage, Entity target) {
        PlayerEntity player = (PlayerEntity)(Object)this;
        ItemStack weapon = player.getMainHandStack();
        
        // 应用打磨的伤害加成
        double damageBonus = AttributeModifierHelper.getDamageModifier(weapon);
        if (damageBonus != 0.0) {
            return (float)(damage + damageBonus);
        }
        
        return damage;
    }
}

