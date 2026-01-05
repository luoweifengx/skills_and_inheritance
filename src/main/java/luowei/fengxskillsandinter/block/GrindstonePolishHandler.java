package luowei.fengxskillsandinter.block;

import net.minecraft.entity.player.PlayerEntity;
import luowei.fengxskillsandinter.util.ItemDataHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import luowei.fengxskillsandinter.config.BlacksmithConfig;
import luowei.fengxskillsandinter.experience.BlacksmithExperience;
import luowei.fengxskillsandinter.weapon.WeaponAttributes;

import java.util.Random;

/**
 * 砂轮打磨处理器
 * 实现无UI的砂轮打磨和武器成长系统
 */
public class GrindstonePolishHandler {
    
    private static final Random RANDOM = new Random();
    
    /**
     * 处理玩家右键砂轮的交互
     */
    public static boolean handleGrindstoneInteraction(World world, BlockPos pos, PlayerEntity player, Hand hand) {
        if (world.isClient) return false;
        
        ItemStack heldStack = player.getStackInHand(hand);
        
        // 必须手持武器/工具
        if (heldStack.isEmpty()) {
            return false;
        }
        
        // 检查是否为可打磨的物品
        if (!isPolishable(heldStack)) {
            player.sendMessage(Text.literal("§c此物品无法打磨"), true);
            return false;
        }
        
        // 情况1: 耐久不满 - 修复耐久
        if (heldStack.isDamaged()) {
            return handleDurabilityRepair(world, pos, player, heldStack);
        }
        
        // 情况2: 耐久已满 - 尝试打磨加点
        if (WeaponAttributes.hasCoating(heldStack)) {
            return handlePolishing(world, pos, player, hand, heldStack);
        }
        
        player.sendMessage(Text.literal("§c该武器没有镀层，无法打磨"), true);
        return false;
    }
    
    /**
     * 处理耐久修复
     */
    private static boolean handleDurabilityRepair(World world, BlockPos pos, PlayerEntity player, ItemStack stack) {
        int maxDamage = stack.getMaxDamage();
        int currentDamage = stack.getDamage();
        
        // 计算恢复量（总耐久的22%）
        int repairAmount = (int)(maxDamage * BlacksmithConfig.DURABILITY_RESTORE_PERCENTAGE);
        int newDamage = Math.max(0, currentDamage - repairAmount);
        
        stack.setDamage(newDamage);
        
        // 20%概率消耗10点原版经验
        if (RANDOM.nextDouble() < BlacksmithConfig.XP_CONSUME_CHANCE) {
            if (player.experienceLevel >= BlacksmithConfig.XP_CONSUME_AMOUNT) {
                player.addExperienceLevels(-BlacksmithConfig.XP_CONSUME_AMOUNT);
                player.sendMessage(Text.literal("§e已消耗 " + BlacksmithConfig.XP_CONSUME_AMOUNT + " 级经验"), true);
            }
        }
        
        world.playSound(null, pos, SoundEvents.BLOCK_GRINDSTONE_USE, SoundCategory.BLOCKS, 1.0f, 1.0f);
        player.sendMessage(Text.literal("§a已修复耐久：+" + repairAmount), true);
        
        return true;
    }
    
    /**
     * 处理打磨加点
     */
    private static boolean handlePolishing(World world, BlockPos pos, PlayerEntity player, Hand hand, ItemStack stack) {
        // 检查是否有附魔（附魔会被移除）
        boolean hasEnchantments = stack.hasEnchantments();
        
        // 获取玩家铁匠等级
        int playerLevel = BlacksmithExperience.getLevel(player);
        
        // 获取镀层类型和成长数值
        String coatingType = WeaponAttributes.getCoatingType(stack);
        double[] growth = BlacksmithConfig.getGrowthValues(coatingType);

        // 计算成功率（打磨前的成功率）- 在移除镀层之前计算
        double successRate = WeaponAttributes.calculatePolishSuccessRate(stack, playerLevel);
        boolean success = RANDOM.nextDouble() < successRate;

        // 保存镀层类型，用于后续显示（因为镀层会被移除）
        String savedCoatingType = coatingType;

        // 消耗镀层（无论成功失败都要消耗）
        WeaponAttributes.removeCoating(stack);

        // 如果有附魔也一起移除
        if (hasEnchantments) {
            ItemDataHelper.remove(stack, "Enchantments");
            player.sendMessage(Text.literal("§6附魔已被移除"), true);
        }

        // 执行打磨操作
        if (success) {
            // 打磨成功 - 永久增加属性和成功次数
            WeaponAttributes.modifyAttackDamage(stack, growth[0]);
            WeaponAttributes.modifyAttackSpeed(stack, growth[1]);
            WeaponAttributes.incrementPolishCount(stack);  // x+1

            world.playSound(null, pos, SoundEvents.BLOCK_GRINDSTONE_USE, SoundCategory.BLOCKS, 1.0f, 1.2f);

            // 增加铁匠经验
            BlacksmithExperience.addExperience(player, 10);

        } else {
            // 打磨失败 - 永久减少属性和成功次数
            WeaponAttributes.modifyAttackDamage(stack, -growth[0]);
            WeaponAttributes.modifyAttackSpeed(stack, -growth[1]);
            WeaponAttributes.decrementPolishCount(stack);  // x-1

            world.playSound(null, pos, SoundEvents.BLOCK_GRINDSTONE_USE, SoundCategory.BLOCKS, 0.8f, 0.8f);
        }

        // 显示打磨结果和当前状态（打磨后的状态）
        int currentPolishCount = WeaponAttributes.getPolishCount(stack);
        
        // 计算下次成功率（需要重新镀层后，使用相同的镀层类型）
        // 使用保存的镀层类型和当前的成功次数来计算
        double nextSuccessRate = WeaponAttributes.calculatePolishSuccessRateForCoating(
            stack, savedCoatingType, playerLevel);

        if (success) {
            player.sendMessage(Text.literal(String.format("§a§l打磨成功！ §r§a攻击伤害 %+.1f, 攻击速度 %+.2f",
                growth[0], growth[1])), true);
        } else {
            player.sendMessage(Text.literal(String.format("§c§l打磨失败！ §r§c攻击伤害 %+.1f, 攻击速度 %+.2f",
                -growth[0], -growth[1])), true);
        }

        // 显示当前状态（打磨后的状态）
        player.sendMessage(Text.literal(String.format("§7打磨成功次数: §a%d §7| 下次成功率: §e%.1f%%",
            currentPolishCount, nextSuccessRate * 100)), false);

        // 提示可以重新镀层继续打磨
        player.sendMessage(Text.literal("§e镀层已消耗，可重新镀层继续打磨"), true);
        
        return true;
    }
    
    /**
     * 判断物品是否可打磨
     */
    private static boolean isPolishable(ItemStack stack) {
        // 检查是否为武器或工具（与铁砧的内核判断一致）
        return stack.getItem().toString().contains("sword") ||
               stack.getItem().toString().contains("axe") ||
               stack.getItem().toString().contains("trident");
    }
}

