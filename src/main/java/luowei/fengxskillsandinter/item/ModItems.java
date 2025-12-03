package luowei.fengxskillsandinter.item;

import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroups;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;
import luowei.fengxskillsandinter.FengxSkillsAndInheritance;

/**
 * 模组物品注册类
 */
public class ModItems {
    
    // 先声明物品变量
    public static Item BIRTH_GIFT;
    public static Item WOODEN_HAMMER;
    public static Item IRON_HAMMER;
    public static Item DIAMOND_HAMMER;
    public static Item BLACKSMITH_EXPERIENCE_BOOK;
    
    /**
     * 注册所有模组物品
     */
    public static void registerModItems() {
        FengxSkillsAndInheritance.LOGGER.info("Registering Mod Items for " + FengxSkillsAndInheritance.MOD_ID);
        
        // 新生诞礼（暂用附魔书模型）
        BIRTH_GIFT = registerItem("birth_gift", 
            new Item(new Item.Settings().registryKey(makeKey("birth_gift"))));
        
        // 木锤（暂用木锄模型）
        WOODEN_HAMMER = registerItem("wooden_hammer",
            new HammerItem("wooden", new Item.Settings()
                .registryKey(makeKey("wooden_hammer"))
                .maxCount(1)
                .maxDamage(200)));
        
        // 铁锤（暂用铁锄模型）
        IRON_HAMMER = registerItem("iron_hammer",
            new HammerItem("iron", new Item.Settings()
                .registryKey(makeKey("iron_hammer"))
                .maxCount(1)
                .maxDamage(500)));
        
        // 钻石锤（暂用钻石锄模型）
        DIAMOND_HAMMER = registerItem("diamond_hammer",
            new HammerItem("diamond", new Item.Settings()
                .registryKey(makeKey("diamond_hammer"))
                .maxCount(1)
                .maxDamage(1000)));
        
        // 铁匠经验之书
        BLACKSMITH_EXPERIENCE_BOOK = registerItem("blacksmith_experience_book",
            new Item(new Item.Settings().registryKey(makeKey("blacksmith_experience_book"))));
        
        // 添加物品到创造模式物品组
        addItemsToGroup();
    }
    
    /**
     * 将物品添加到创造模式物品组
     */
    private static void addItemsToGroup() {
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.TOOLS).register(entries -> {
            entries.add(WOODEN_HAMMER);
            entries.add(IRON_HAMMER);
            entries.add(DIAMOND_HAMMER);
            entries.add(BIRTH_GIFT);
            entries.add(BLACKSMITH_EXPERIENCE_BOOK);
        });
    }
    
    /**
     * 创建注册键
     */
    private static RegistryKey<Item> makeKey(String name) {
        return RegistryKey.of(RegistryKeys.ITEM, Identifier.of(FengxSkillsAndInheritance.MOD_ID, name));
    }
    
    /**
     * 注册物品
     */
    private static Item registerItem(String name, Item item) {
        return Registry.register(Registries.ITEM, 
            Identifier.of(FengxSkillsAndInheritance.MOD_ID, name), item);
    }
}

