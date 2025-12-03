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
        ItemDataHelper.setInt(stack, NBT_ITEM_EXPERIENCE, current + amount);
    }
    
    /**
     * 设置物品经验值
     */
    public static void setExperience(ItemStack stack, int amount) {
        ItemDataHelper.setInt(stack, NBT_ITEM_EXPERIENCE, amount);
    }
}

