package luowei.fengxskillsandinter.config;

/**
 * 铁匠职业核心配置类
 * 存储所有可配置的数值参数
 */
public class BlacksmithConfig {
    
    // ============ 铁匠经验等级配置 ============
    // 等级范围：1-6级（0级改为1级）
    // 阈值数组：[0经验起点, 1级, 2级, 3级, 4级, 5级, 6级]
    public static final int[] LEVEL_THRESHOLDS = {0, 300, 900, 2100, 4500, 9999, 20000};
    public static final int MAX_LEVEL = 6;
    public static final int MIN_LEVEL = 1; // 最低等级为1级
    
    // ============ 镀层材料系数配置（成功率底表） ============
    public static final double COPPER_COATING_COEFFICIENT = 0.80;
    public static final double IRON_COATING_COEFFICIENT = 0.75;
    public static final double GOLD_COATING_COEFFICIENT = 0.85;
    public static final double DIAMOND_COATING_COEFFICIENT = 0.70;
    public static final double NETHERITE_COATING_COEFFICIENT = 0.65;
    public static final double LAPIS_COATING_COEFFICIENT = 1.20;
    public static final double COAL_COATING_COEFFICIENT = 0.75;
    public static final double REDSTONE_COATING_COEFFICIENT = 0.82;
    
    // ============ 镀层成长幅度配置 ============
    // 格式: [攻击伤害变化, 攻击速度变化]
    public static final double[] COPPER_GROWTH = {0.3, 0.0};
    public static final double[] IRON_GROWTH = {0.6, 0.05};
    public static final double[] GOLD_GROWTH = {0.1, 0.1};
    public static final double[] DIAMOND_GROWTH = {0.8, 0.04};
    public static final double[] NETHERITE_GROWTH = {1.0, 0.05};
    public static final double[] COAL_GROWTH = {1.0, -0.3};
    public static final double[] REDSTONE_GROWTH = {0.0, 0.15};
    public static final double[] LAPIS_GROWTH = {-0.5, -0.2};
    
    // ============ 打磨耐久修复配置 ============
    public static final double DURABILITY_RESTORE_PERCENTAGE = 0.22; // 恢复22%耐久
    public static final double XP_CONSUME_CHANCE = 0.20;             // 20%概率消耗经验
    public static final int XP_CONSUME_AMOUNT = 10;                  // 消耗10点原版经验
    
    /**
     * 获取玩家当前铁匠等级（基于经验值）
     * 返回范围：1-6级（最低1级，最高6级）
     */
    public static int getLevel(int experience) {
        for (int i = LEVEL_THRESHOLDS.length - 1; i >= 0; i--) {
            if (experience >= LEVEL_THRESHOLDS[i]) {
                // 返回等级（索引对应等级，0经验对应1级）
                return Math.max(MIN_LEVEL, i);
            }
        }
        return MIN_LEVEL; // 默认返回1级
    }
    
    /**
     * 根据材料类型获取镀层系数
     */
    public static double getCoatingCoefficient(String material) {
        return switch (material) {
            case "copper" -> COPPER_COATING_COEFFICIENT;
            case "iron" -> IRON_COATING_COEFFICIENT;
            case "gold" -> GOLD_COATING_COEFFICIENT;
            case "diamond" -> DIAMOND_COATING_COEFFICIENT;
            case "netherite" -> NETHERITE_COATING_COEFFICIENT;
            case "lapis" -> LAPIS_COATING_COEFFICIENT;
            case "coal" -> COAL_COATING_COEFFICIENT;
            case "redstone" -> REDSTONE_COATING_COEFFICIENT;
            default -> 1.0;
        };
    }
    
    /**
     * 根据材料类型获取成长幅度
     */
    public static double[] getGrowthValues(String material) {
        return switch (material) {
            case "copper" -> COPPER_GROWTH;
            case "iron" -> IRON_GROWTH;
            case "gold" -> GOLD_GROWTH;
            case "diamond" -> DIAMOND_GROWTH;
            case "netherite" -> NETHERITE_GROWTH;
            case "coal" -> COAL_GROWTH;
            case "redstone" -> REDSTONE_GROWTH;
            case "lapis" -> LAPIS_GROWTH;
            default -> new double[]{0.0, 0.0};
        };
    }
}







