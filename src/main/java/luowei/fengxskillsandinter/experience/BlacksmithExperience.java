package luowei.fengxskillsandinter.experience;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.network.ServerPlayerEntity;
import luowei.fengxskillsandinter.config.BlacksmithConfig;

/**
 * 铁匠经验管理类
 * 处理玩家的铁匠职业经验和等级
 */
public class BlacksmithExperience {
    
    private static final String NBT_KEY = "BlacksmithExperience";
    private static final String NBT_LEVEL_KEY = "BlacksmithLevel";
    
    /**
     * 获取玩家的铁匠经验值
     */
    public static int getExperience(PlayerEntity player) {
        NbtCompound nbt = player.writeNbt(new NbtCompound());
        return nbt.getInt(NBT_KEY).orElse(0);
    }
    
    /**
     * 设置玩家的铁匠经验值
     */
    public static void setExperience(PlayerEntity player, int experience) {
        experience = Math.max(0, Math.min(experience, BlacksmithConfig.LEVEL_THRESHOLDS[BlacksmithConfig.MAX_LEVEL]));
        
        if (player instanceof ServerPlayerEntity serverPlayer) {
            NbtCompound nbt = serverPlayer.writeNbt(new NbtCompound());
            nbt.putInt(NBT_KEY, experience);
            nbt.putInt(NBT_LEVEL_KEY, BlacksmithConfig.getLevel(experience));
            serverPlayer.readNbt(nbt);
        }
    }
    
    /**
     * 增加玩家的铁匠经验值
     */
    public static void addExperience(PlayerEntity player, int amount) {
        int current = getExperience(player);
        setExperience(player, current + amount);
    }
    
    /**
     * 减少玩家的铁匠经验值
     */
    public static boolean consumeExperience(PlayerEntity player, int amount) {
        int current = getExperience(player);
        if (current >= amount) {
            setExperience(player, current - amount);
            return true;
        }
        return false;
    }
    
    /**
     * 获取玩家的铁匠等级
     */
    public static int getLevel(PlayerEntity player) {
        return BlacksmithConfig.getLevel(getExperience(player));
    }
    
    /**
     * 获取当前等级的经验进度百分比
     */
    public static float getLevelProgress(PlayerEntity player) {
        int exp = getExperience(player);
        int level = getLevel(player);
        
        if (level >= BlacksmithConfig.MAX_LEVEL) {
            return 1.0f;
        }
        
        int currentLevelMin = BlacksmithConfig.LEVEL_THRESHOLDS[level];
        int nextLevelMin = BlacksmithConfig.LEVEL_THRESHOLDS[level + 1];
        
        return (float)(exp - currentLevelMin) / (nextLevelMin - currentLevelMin);
    }
}

