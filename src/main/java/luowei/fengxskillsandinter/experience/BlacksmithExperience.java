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
     * 注意：客户端和服务端都能读取，但数据只在服务端修改
     */
    public static int getExperience(PlayerEntity player) {
        // 使用writeNbt获取玩家NBT数据
        NbtCompound nbt = player.writeNbt(new NbtCompound());
        // 检查是否包含经验值键
        if (nbt.contains(NBT_KEY)) {
            return nbt.getInt(NBT_KEY).orElse(0);
        }
        return 0;
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
     * @return 是否升级了
     */
    public static boolean addExperience(PlayerEntity player, int amount) {
        int currentLevel = getLevel(player);
        int current = getExperience(player);
        setExperience(player, current + amount);
        int newLevel = getLevel(player);
        
        // 检测是否升级
        if (newLevel > currentLevel) {
            // 播放升级音效（在服务端）
            if (player instanceof ServerPlayerEntity serverPlayer) {
                serverPlayer.playSound(net.minecraft.sound.SoundEvents.ENTITY_PLAYER_LEVELUP, 1.0f, 1.0f);
            }
            return true;
        }
        return false;
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
        
        // 等级对应数组索引：1级对应索引1，2级对应索引2，以此类推
        int currentLevelIndex = level;
        int nextLevelIndex = Math.min(level + 1, BlacksmithConfig.LEVEL_THRESHOLDS.length - 1);
        
        int currentLevelMin = BlacksmithConfig.LEVEL_THRESHOLDS[currentLevelIndex];
        int nextLevelMin = BlacksmithConfig.LEVEL_THRESHOLDS[nextLevelIndex];
        
        return (float)(exp - currentLevelMin) / (nextLevelMin - currentLevelMin);
    }
}

