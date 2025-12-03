package luowei.fengxskillsandinter.util;

import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.NbtComponent;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;

/**
 * 物品数据辅助类
 * 处理新版本ItemStack的NBT数据读写
 */
public class ItemDataHelper {
    
    /**
     * 获取物品的NBT数据（只读）
     */
    public static NbtCompound getNbt(ItemStack stack) {
        NbtComponent nbtComponent = stack.getOrDefault(DataComponentTypes.CUSTOM_DATA, NbtComponent.DEFAULT);
        return nbtComponent.copyNbt();
    }
    
    /**
     * 设置物品的NBT数据
     */
    public static void setNbt(ItemStack stack, NbtCompound nbt) {
        stack.set(DataComponentTypes.CUSTOM_DATA, NbtComponent.of(nbt));
    }
    
    /**
     * 获取布尔值
     */
    public static boolean getBoolean(ItemStack stack, String key) {
        NbtCompound nbt = getNbt(stack);
        if (!nbt.contains(key)) return false;
        return nbt.getBoolean(key).orElse(false);
    }
    
    /**
     * 设置布尔值
     */
    public static void setBoolean(ItemStack stack, String key, boolean value) {
        NbtCompound nbt = getNbt(stack);
        nbt.putBoolean(key, value);
        setNbt(stack, nbt);
    }
    
    /**
     * 获取整数值
     */
    public static int getInt(ItemStack stack, String key) {
        NbtCompound nbt = getNbt(stack);
        return nbt.contains(key) ? nbt.getInt(key).orElse(0) : 0;
    }
    
    /**
     * 设置整数值
     */
    public static void setInt(ItemStack stack, String key, int value) {
        NbtCompound nbt = getNbt(stack);
        nbt.putInt(key, value);
        setNbt(stack, nbt);
    }
    
    /**
     * 获取双精度浮点数
     */
    public static double getDouble(ItemStack stack, String key) {
        NbtCompound nbt = getNbt(stack);
        return nbt.contains(key) ? nbt.getDouble(key).orElse(0.0) : 0.0;
    }
    
    /**
     * 设置双精度浮点数
     */
    public static void setDouble(ItemStack stack, String key, double value) {
        NbtCompound nbt = getNbt(stack);
        nbt.putDouble(key, value);
        setNbt(stack, nbt);
    }
    
    /**
     * 获取字符串
     */
    public static String getString(ItemStack stack, String key) {
        NbtCompound nbt = getNbt(stack);
        return nbt.contains(key) ? nbt.getString(key).orElse("") : "";
    }
    
    /**
     * 设置字符串
     */
    public static void setString(ItemStack stack, String key, String value) {
        NbtCompound nbt = getNbt(stack);
        nbt.putString(key, value);
        setNbt(stack, nbt);
    }
    
    /**
     * 移除键
     */
    public static void remove(ItemStack stack, String key) {
        NbtCompound nbt = getNbt(stack);
        nbt.remove(key);
        setNbt(stack, nbt);
    }
    
    /**
     * 检查是否包含键
     */
    public static boolean contains(ItemStack stack, String key) {
        NbtCompound nbt = getNbt(stack);
        return nbt.contains(key);
    }
}

