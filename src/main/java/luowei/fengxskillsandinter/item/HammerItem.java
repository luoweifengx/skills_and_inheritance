package luowei.fengxskillsandinter.item;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.ActionResult;
import net.minecraft.world.World;
import net.minecraft.text.Text;
import luowei.fengxskillsandinter.config.BlacksmithConfig;
import luowei.fengxskillsandinter.experience.BlacksmithExperience;
import luowei.fengxskillsandinter.util.ItemDataHelper;

/**
 * 锤子物品类
 * 支持临时加强功能（潜行+右键）
 */
public class HammerItem extends Item {
    
    private final String hammerType;
    private static final String NBT_BOOSTED = "IsBoosted";
    
    public HammerItem(String type, Settings settings) {
        super(settings);
        this.hammerType = type;
    }
    
    @Override
    public ActionResult use(World world, PlayerEntity player, Hand hand) {
        ItemStack stack = player.getStackInHand(hand);
        
        // 潜行+右键触发临时加强
        if (player.isSneaking()) {
            if (!world.isClient) {
                if (!isBoosted(stack)) {
                    // 检查经验是否足够
                    int vanillaXp = player.experienceLevel;
                    int blacksmithXp = BlacksmithExperience.getExperience(player);
                    
                    if (vanillaXp >= BlacksmithConfig.BOOST_VANILLA_XP_COST && 
                        blacksmithXp >= BlacksmithConfig.BOOST_BLACKSMITH_XP_COST) {
                        
                        // 消耗经验
                        player.addExperienceLevels(-BlacksmithConfig.BOOST_VANILLA_XP_COST);
                        BlacksmithExperience.consumeExperience(player, BlacksmithConfig.BOOST_BLACKSMITH_XP_COST);
                        
                        // 激活临时加强
                        setBoosted(stack, true);
                        
                        // 提示玩家
                        double boostPercent = BlacksmithConfig.getHammerBoost(hammerType) * 100;
                        player.sendMessage(Text.literal(String.format("§a锤子已加强！成功率提升 %.0f%%", boostPercent)), true);
                        
                        return ActionResult.SUCCESS;
                    } else {
                        player.sendMessage(Text.literal("§c经验不足！需要 " + 
                            BlacksmithConfig.BOOST_VANILLA_XP_COST + " 级原版经验和 " + 
                            BlacksmithConfig.BOOST_BLACKSMITH_XP_COST + " 点铁匠经验"), true);
                    }
                } else {
                    player.sendMessage(Text.literal("§e锤子已处于加强状态"), true);
                }
            }
            return ActionResult.SUCCESS;
        }
        
        return ActionResult.PASS;
    }
    
    /**
     * 检查锤子是否处于加强状态
     */
    public static boolean isBoosted(ItemStack stack) {
        return ItemDataHelper.getBoolean(stack, NBT_BOOSTED);
    }
    
    /**
     * 设置锤子加强状态
     */
    public static void setBoosted(ItemStack stack, boolean boosted) {
        ItemDataHelper.setBoolean(stack, NBT_BOOSTED, boosted);
    }
    
    /**
     * 消耗加强状态（使用一次后失效）
     */
    public static void consumeBoost(ItemStack stack) {
        setBoosted(stack, false);
    }
    
    public String getHammerType() {
        return hammerType;
    }
    
    @Override
    public boolean hasGlint(ItemStack stack) {
        // 加强状态下显示附魔光效
        return isBoosted(stack) || super.hasGlint(stack);
    }
}

