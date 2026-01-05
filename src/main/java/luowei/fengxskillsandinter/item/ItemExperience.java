package luowei.fengxskillsandinter.item;

import net.minecraft.item.ItemStack;
import luowei.fengxskillsandinter.util.ItemDataHelper;

/**
 * 物品经验系统
 * 当玩家使用工具/武器时，增长物品经验
 */
public class ItemExperience {
    
    private static final String NBT_ITEM_EXPERIENCE = "ItemExperience";
    
    /**
     * 获取物品经验值
     */
    public static int getItemExperience(ItemStack stack) {
        return ItemDataHelper.getInt(stack, NBT_ITEM_EXPERIENCE);
    }
    
    /**
     * 增加物品经验值
     */
    public static void addExperience(ItemStack stack, int amount) {
        int current = getItemExperience(stack);
        // 如果当前经验为0，初始化为整型上限
        if (current == 0) {
            current = Integer.MAX_VALUE;
        }
        // 增加经验，但不超过整型上限
        long newExp = (long)current + amount;
        ItemDataHelper.setInt(stack, NBT_ITEM_EXPERIENCE, (int)Math.min(newExp, Integer.MAX_VALUE));
    }
    
    /**
     * 设置物品经验值
     */
    public static void setExperience(ItemStack stack, int amount) {
        ItemDataHelper.setInt(stack, NBT_ITEM_EXPERIENCE, amount);
    }
    
    /**
     * 初始化物品经验为整型上限（如果还没有经验）
     */
    public static void initializeMaxExperience(ItemStack stack) {
        if (getItemExperience(stack) == 0) {
            setExperience(stack, Integer.MAX_VALUE);
        }
    }
    
    /**
     * 消耗物品经验
     * @return 是否成功消耗
     */
    public static boolean consumeExperience(ItemStack stack, int amount) {
        int current = getItemExperience(stack);
        // 如果当前经验为0，初始化为整型上限
        if (current == 0) {
            current = Integer.MAX_VALUE;
        }
        if (current >= amount) {
            setExperience(stack, current - amount);
            return true;
        }
        return false;
    }
}




