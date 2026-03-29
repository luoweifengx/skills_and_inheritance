package luowei.fengxskillsandinter.util;

import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.NbtComponent;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtString;
import java.util.ArrayList;
import java.util.List;

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

    //获取长整数
    public static long getLong(ItemStack stack, String key) {
        NbtCompound nbt = getNbt(stack);
        return nbt.contains(key) ? nbt.getLong(key).orElse(0L) : 0L;
    }
    
    //设置长整数
    public static void setLong(ItemStack stack, String key, long value) {
        NbtCompound nbt = getNbt(stack);
        nbt.putLong(key, value);
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
    
    /**
     * 获取字符串列表
     * 从 NBT 中读取列表并转换为 Java List
     * 
     * @param stack 物品堆栈
     * @param key NBT 键名
     * @return 字符串列表，如果不存在则返回空列表
     */
    public static List<String> getStringList(ItemStack stack, String key) {
        NbtCompound nbt = getNbt(stack);
        if (!nbt.contains(key)) {
            return new ArrayList<>(); // 如果不存在，返回空列表
        }
        
        // 尝试获取列表
        NbtElement element = nbt.get(key);
        if (element == null || element.getType() != NbtElement.LIST_TYPE) {
            return new ArrayList<>(); // 如果不是列表类型，返回空列表
        }
        
        // NBT 列表 → Java List
        NbtList nbtList = (NbtList) element;
        List<String> result = new ArrayList<>();
        for (int i = 0; i < nbtList.size(); i++) {
            NbtElement item = nbtList.get(i);
            if (item.getType() == NbtElement.STRING_TYPE) {
                // asString() 返回 Optional<String>，需要处理
                item.asString().ifPresent(result::add);
            }
        }
        return result;
    }
    
    /**
     * 设置字符串列表
     * 将 Java List 转换为 NBT 列表并存储
     * 
     * @param stack 物品堆栈
     * @param key NBT 键名
     * @param value 要存储的字符串列表
     */
    public static void setStringList(ItemStack stack, String key, List<String> value) {
        NbtCompound nbt = getNbt(stack);
        NbtList list = new NbtList();
        
        // Java List → NBT 列表
        for (String str : value) {
            list.add(NbtString.of(str));
        }
        
        nbt.put(key, list);
        setNbt(stack, nbt);
    }
}

