package luowei.fengxskillsandinter.mixin;

import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import luowei.fengxskillsandinter.item.ItemExperience;
import luowei.fengxskillsandinter.weapon.WeaponAttributeHelper;

import java.util.function.Consumer;

/**
 * ItemStack Mixin
 * 监听物品耐久消耗，增加物品经验
 */
@Mixin(ItemStack.class)
public class ItemStackMixin {
    
    /**
     * 监听damage方法（耐久消耗）
     * 在耐久消耗之后，如果实际消耗了耐久，则增加物品经验
     * 
     * 实际方法签名：damage(int amount, ServerWorld world, ServerPlayerEntity player, Consumer<LivingEntity> breakCallback)
     */
    @Inject(
        method = "damage(ILnet/minecraft/server/world/ServerWorld;Lnet/minecraft/server/network/ServerPlayerEntity;Ljava/util/function/Consumer;)V",
        at = @At("RETURN")
    )
    private void onDamage(int amount, ServerWorld world, ServerPlayerEntity player, Consumer<LivingEntity> breakCallback, CallbackInfo ci) {
        ItemStack stack = (ItemStack)(Object)this;
        
        // 只在服务端处理（world不为null说明是服务端）
        if (world == null || world.isClient) {
            return;
        }
        
        // 检查是否是工具或武器
        if (!WeaponAttributeHelper.isTool(stack) && !WeaponAttributeHelper.isWeapon(stack)) {
            return;
        }
        
        // 如果amount > 0，说明消耗了耐久
        if (amount > 0) {
            // 增加物品经验（每次消耗耐久+1经验）
            ItemExperience.addExperience(stack, 1);
        }
    }
}

