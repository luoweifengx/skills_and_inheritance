package luowei.fengxskillsandinter.weapon;

import net.minecraft.item.ItemStack;
import luowei.fengxskillsandinter.config.BlacksmithConfig;
import luowei.fengxskillsandinter.util.ItemDataHelper;

/**
 * 武器属性管理类
 * 存储和管理武器的镀层、打磨次数、属性加成等数据
 */
public class WeaponAttributes {
    
    // NBT键名定义
    private static final String NBT_HAS_COATING = "HasCoating";
    private static final String NBT_COATING_TYPE = "CoatingType";
    private static final String NBT_POLISH_SUCCESS_COUNT = "PolishSuccessCount"; // 成功打磨次数x（可为负数）
    private static final String NBT_ARMOR_PENETRATION = "ArmorPenetration";
    
    /**
     * 检查武器是否有镀层
     */
    public static boolean hasCoating(ItemStack stack) {
        return ItemDataHelper.getBoolean(stack, NBT_HAS_COATING);
    }
    
    /**
     * 设置武器镀层
     */
    public static void setCoating(ItemStack stack, String coatingType) {
        ItemDataHelper.setBoolean(stack, NBT_HAS_COATING, true);
        ItemDataHelper.setString(stack, NBT_COATING_TYPE, coatingType);
        
        // 初始化成功次数（如果是第一次镀层）
        if (!ItemDataHelper.contains(stack, NBT_POLISH_SUCCESS_COUNT)) {
            ItemDataHelper.setInt(stack, NBT_POLISH_SUCCESS_COUNT, 0);
        }
        
        updateArmorPenetration(stack);
    }
    
    /**
     * 移除武器镀层（但保留已打磨的属性加成和打磨次数）
     */
    public static void removeCoating(ItemStack stack) {
        ItemDataHelper.setBoolean(stack, NBT_HAS_COATING, false);
        ItemDataHelper.remove(stack, NBT_COATING_TYPE);
        // 注意：不重置打磨次数和属性加成，这些是永久性的
    }
    
    /**
     * 获取镀层类型
     */
    public static String getCoatingType(ItemStack stack) {
        return ItemDataHelper.getString(stack, NBT_COATING_TYPE);
    }
    
    /**
     * 获取成功打磨次数x（用于计算成功率，可为负数）
     */
    public static int getPolishCount(ItemStack stack) {
        return ItemDataHelper.getInt(stack, NBT_POLISH_SUCCESS_COUNT);
    }
    
    /**
     * 打磨成功 - 增加成功次数
     */
    public static void incrementPolishCount(ItemStack stack) {
        int count = getPolishCount(stack);
        ItemDataHelper.setInt(stack, NBT_POLISH_SUCCESS_COUNT, count + 1);
    }
    
    /**
     * 打磨失败 - 减少成功次数
     */
    public static void decrementPolishCount(ItemStack stack) {
        int count = getPolishCount(stack);
        ItemDataHelper.setInt(stack, NBT_POLISH_SUCCESS_COUNT, count - 1);
    }
    
    /**
     * 直接修改武器的攻击伤害（修改AttributeModifiers组件）
     */
    public static void modifyAttackDamage(ItemStack stack, double change) {
        AttributeModifierHelper.modifyDamage(stack, change);
        updateArmorPenetration(stack);
    }
    
    /**
     * 直接修改武器的攻击速度（修改AttributeModifiers组件）
     */
    public static void modifyAttackSpeed(ItemStack stack, double change) {
        AttributeModifierHelper.modifySpeed(stack, change);
        updateArmorPenetration(stack);
    }
    
    /**
     * 获取破韧值
     */
    public static double getArmorPenetration(ItemStack stack) {
        return ItemDataHelper.getDouble(stack, NBT_ARMOR_PENETRATION);
    }
    
    /**
     * 更新破韧值
     * 公式：破韧 = (4 - 攻击速度) × 武器伤害 ÷ 2
     */
    public static void updateArmorPenetration(ItemStack stack) {
        // 获取当前武器的实际属性值（包含打磨加成）
        double damageModifier = AttributeModifierHelper.getDamageModifier(stack);
        double speedModifier = AttributeModifierHelper.getSpeedModifier(stack);
        
        // 基础值需要从原版武器获取（这里简化处理）
        double totalDamage = 7.0 + damageModifier;  // TODO: 获取武器基础伤害
        double totalSpeed = 1.6 + speedModifier;    // TODO: 获取武器基础速度
        
        // 计算破韧值
        double armorPenetration = (4.0 - totalSpeed) * totalDamage / 2.0;
        armorPenetration = Math.max(0, armorPenetration);
        
        ItemDataHelper.setDouble(stack, NBT_ARMOR_PENETRATION, armorPenetration);
    }
    
    /**
     * 获取武器当前的攻击伤害加成（用于显示Tooltip）
     */
    public static double getDamageBonus(ItemStack stack) {
        return AttributeModifierHelper.getDamageModifier(stack);
    }
    
    /**
     * 获取武器当前的攻击速度加成（用于显示Tooltip）
     */
    public static double getSpeedBonus(ItemStack stack) {
        return AttributeModifierHelper.getSpeedModifier(stack);
    }
    
    /**
     * 计算下一次打磨的成功率
     * 公式：材料系数^(x-y)
     */
    public static double calculatePolishSuccessRate(ItemStack stack, int playerLevel) {
        if (!hasCoating(stack)) {
            return 0.0;
        }
        
        String coatingType = getCoatingType(stack);
        return calculatePolishSuccessRateForCoating(stack, coatingType, playerLevel);
    }
    
    /**
     * 计算指定镀层类型的打磨成功率（用于显示下次成功率）
     * 公式：材料系数^(x-y)
     */
    public static double calculatePolishSuccessRateForCoating(ItemStack stack, String coatingType, int playerLevel) {
        if (coatingType == null || coatingType.isEmpty()) {
            return 0.0;
        }
        
        double coefficient = BlacksmithConfig.getCoatingCoefficient(coatingType);
        int polishCount = getPolishCount(stack);
        
        // 计算成功率：coefficient^(x-y)
        double finalRate = Math.pow(coefficient, polishCount - playerLevel);
        
        // 限制在0-100%之间
        return Math.max(0.0, Math.min(1.0, finalRate));
    }
}

