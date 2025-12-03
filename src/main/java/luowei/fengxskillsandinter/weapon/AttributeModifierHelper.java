package luowei.fengxskillsandinter.weapon;

import net.minecraft.item.ItemStack;
import luowei.fengxskillsandinter.util.ItemDataHelper;

/**
 * 属性修改器辅助类
 * 存储武器的打磨属性加成
 */
public class AttributeModifierHelper {
    
    private static final String NBT_DAMAGE_MODIFIER = "PolishDamageModifier";
    private static final String NBT_SPEED_MODIFIER = "PolishSpeedModifier";
    
    /**
     * 修改武器的攻击伤害加成
     */
    public static void modifyDamage(ItemStack stack, double change) {
        double current = getDamageModifier(stack);
        ItemDataHelper.setDouble(stack, NBT_DAMAGE_MODIFIER, current + change);
    }
    
    /**
     * 修改武器的攻击速度加成
     */
    public static void modifySpeed(ItemStack stack, double change) {
        double current = getSpeedModifier(stack);
        ItemDataHelper.setDouble(stack, NBT_SPEED_MODIFIER, current + change);
    }
    
    /**
     * 获取当前的伤害加成值（用于显示）
     */
    public static double getDamageModifier(ItemStack stack) {
        return ItemDataHelper.getDouble(stack, NBT_DAMAGE_MODIFIER);
    }
    
    /**
     * 获取当前的速度加成值（用于显示）
     */
    public static double getSpeedModifier(ItemStack stack) {
        return ItemDataHelper.getDouble(stack, NBT_SPEED_MODIFIER);
    }
}

