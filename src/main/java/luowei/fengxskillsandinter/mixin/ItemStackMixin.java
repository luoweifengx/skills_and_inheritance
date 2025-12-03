package luowei.fengxskillsandinter.mixin;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import luowei.fengxskillsandinter.item.ItemExperience;
import luowei.fengxskillsandinter.weapon.WeaponAttributeHelper;

/**
 * ItemStack Mixin
 * 监听物品耐久消耗，增加物品经验
 */
@Mixin(ItemStack.class)
public class ItemStackMixin {
    
    /**
     * 监听damage方法（耐久消耗）
     * 在耐久消耗之后，如果实际消耗了耐久，则增加物品经验
     */
    @Inject(
        method = "damage(ILnet/minecraft/entity/LivingEntity;Ljava/util/function/Consumer;)Z",
        at = @At("RETURN")
    )
    private void onDamage(int amount, LivingEntity entity, java.util.function.Consumer<LivingEntity> breakCallback, CallbackInfoReturnable<Boolean> cir) {
        ItemStack stack = (ItemStack)(Object)this;
        
        // 只在服务端处理
        if (entity == null || entity.getWorld().isClient) {
            return;
        }
        
        // 检查是否是玩家
        if (!(entity instanceof PlayerEntity)) {
            return;
        }
        
        // 检查是否是工具或武器
        if (!WeaponAttributeHelper.isTool(stack) && !WeaponAttributeHelper.isWeapon(stack)) {
            return;
        }
        
        // 如果damage方法返回true，说明耐久被消耗了
        if (cir.getReturnValue() && amount > 0) {
            // 增加物品经验（每次消耗耐久+1经验）
            ItemExperience.addExperience(stack, 1);
        }
    }
}

