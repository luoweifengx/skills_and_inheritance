package luowei.fengxskillsandinter.weapon;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;

/**
 * 武器属性辅助类
 * 用于获取和计算武器的实际属性值
 */
public class WeaponAttributeHelper {
    
    /**
     * 获取武器的基础攻击伤害
     * 通过Item类型判断基础值
     */
    public static double getBaseAttackDamage(ItemStack stack) {
        Item item = stack.getItem();
        
        // 根据物品类型返回基础攻击伤害
        // 剑类：木4, 石5, 铁6, 金4, 钻石7, 下界合金8
        if (item == Items.WOODEN_SWORD || item == Items.GOLDEN_SWORD) {
            return 4.0;
        } else if (item == Items.STONE_SWORD) {
            return 5.0;
        } else if (item == Items.IRON_SWORD) {
            return 6.0;
        } else if (item == Items.DIAMOND_SWORD) {
            return 7.0;
        } else if (item == Items.NETHERITE_SWORD) {
            return 8.0;
        }
        // 斧类：木7, 石8, 铁9, 金7, 钻石9, 下界合金10
        else if (item == Items.WOODEN_AXE || item == Items.GOLDEN_AXE) {
            return 7.0;
        } else if (item == Items.STONE_AXE) {
            return 8.0;
        } else if (item == Items.IRON_AXE) {
            return 9.0;
        } else if (item == Items.DIAMOND_AXE) {
            return 9.0;
        } else if (item == Items.NETHERITE_AXE) {
            return 10.0;
        }
        // 三叉戟
        else if (item == Items.TRIDENT) {
            return 8.0;
        }
        
        return 0.0;
    }
    
    /**
     * 获取武器的基础攻击速度
     */
    public static double getBaseAttackSpeed(ItemStack stack) {
        Item item = stack.getItem();
        
        // 剑的攻击速度都是1.6
        if (item == Items.WOODEN_SWORD || item == Items.STONE_SWORD ||
            item == Items.IRON_SWORD || item == Items.GOLDEN_SWORD ||
            item == Items.DIAMOND_SWORD || item == Items.NETHERITE_SWORD) {
            return 1.6;
        }
        // 斧的攻击速度都是1.0
        else if (item == Items.WOODEN_AXE || item == Items.STONE_AXE ||
                 item == Items.IRON_AXE || item == Items.GOLDEN_AXE ||
                 item == Items.DIAMOND_AXE || item == Items.NETHERITE_AXE) {
            return 1.0;
        }
        // 三叉戟的攻击速度是1.1
        else if (item == Items.TRIDENT) {
            return 1.1;
        }
        
        return 4.0; // 默认攻击速度
    }
    
    /**
     * 获取武器的总攻击伤害（基础值 + 加成）
     */
    public static double getTotalAttackDamage(ItemStack stack) {
        double baseDamage = getBaseAttackDamage(stack);
        double modifier = AttributeModifierHelper.getDamageModifier(stack);
        return baseDamage + modifier;
    }
    
    /**
     * 获取武器的总攻击速度（基础值 + 加成）
     */
    public static double getTotalAttackSpeed(ItemStack stack) {
        double baseSpeed = getBaseAttackSpeed(stack);
        double modifier = AttributeModifierHelper.getSpeedModifier(stack);
        return baseSpeed + modifier;
    }
    
    /**
     * 检查ItemStack是否是武器
     */
    public static boolean isWeapon(ItemStack stack) {
        Item item = stack.getItem();
        return item == Items.WOODEN_SWORD || item == Items.STONE_SWORD ||
               item == Items.IRON_SWORD || item == Items.GOLDEN_SWORD ||
               item == Items.DIAMOND_SWORD || item == Items.NETHERITE_SWORD ||
               item == Items.WOODEN_AXE || item == Items.STONE_AXE ||
               item == Items.IRON_AXE || item == Items.GOLDEN_AXE ||
               item == Items.DIAMOND_AXE || item == Items.NETHERITE_AXE ||
               item == Items.TRIDENT;
    }
    
    /**
     * 检查ItemStack是否是工具（包括武器和挖掘工具）
     */
    public static boolean isTool(ItemStack stack) {
        Item item = stack.getItem();
        // 包括武器、镐、铲、锄等
        return isWeapon(stack) ||
               item == Items.WOODEN_PICKAXE || item == Items.STONE_PICKAXE ||
               item == Items.IRON_PICKAXE || item == Items.GOLDEN_PICKAXE ||
               item == Items.DIAMOND_PICKAXE || item == Items.NETHERITE_PICKAXE ||
               item == Items.WOODEN_SHOVEL || item == Items.STONE_SHOVEL ||
               item == Items.IRON_SHOVEL || item == Items.GOLDEN_SHOVEL ||
               item == Items.DIAMOND_SHOVEL || item == Items.NETHERITE_SHOVEL ||
               item == Items.WOODEN_HOE || item == Items.STONE_HOE ||
               item == Items.IRON_HOE || item == Items.GOLDEN_HOE ||
               item == Items.DIAMOND_HOE || item == Items.NETHERITE_HOE;
    }
}

