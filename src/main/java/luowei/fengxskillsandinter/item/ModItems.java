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
    public static Item WAND;
    public static Item BURST_2;
    public static Item BURST_3;
    public static Item BURST_4;
    public static Item SPARK_PROJECTILE;
    public static Item CHAINSAW;
    public static Item SPARK_PROJECTILE_TRIGGER;
    public static Item HEAVY_SPREAD;
    public static Item HOMING_SHOOTER;
    public static Item HOMING;
    public static Item NUKE;
    public static Item LUMINOUS_DRILL;
    public static Item BUBBLE_SHOT;
    public static Item DISC_BULLET_BIG;
    public static Item TELEPORT_PROJECTILE;
    public static Item REGENERATION_FIELD;
    public static Item RECHARGE;
    public static Item MANA;
    public static Item SPELL_CORE;
    /**
     * 注册所有模组物品
     */
    public static void registerModItems() {
        FengxSkillsAndInheritance.LOGGER.info("Registering Mod Items for " + FengxSkillsAndInheritance.MOD_ID);

        WAND = registerItem("wand", new WandItem(new Item.Settings().registryKey(makeKey("wand")).maxCount(1)));
        //法术
        BURST_2 = registerItem("burst_2", new SpellItem(new Item.Settings().registryKey(makeKey("burst_2")).maxCount(1), "burst_2"));
        BURST_3 = registerItem("burst_3", new SpellItem(new Item.Settings().registryKey(makeKey("burst_3")).maxCount(1), "burst_3"));
        BURST_4 = registerItem("burst_4", new SpellItem(new Item.Settings().registryKey(makeKey("burst_4")).maxCount(1), "burst_4"));
        SPARK_PROJECTILE = registerItem("spark_projectile", new SpellItem(new Item.Settings().registryKey(makeKey("spark_projectile")).maxCount(1), "spark_projectile"));
        CHAINSAW = registerItem("chainsaw", new SpellItem(new Item.Settings().registryKey(makeKey("chainsaw")).maxCount(1), "chainsaw"));
        SPARK_PROJECTILE_TRIGGER = registerItem("spark_projectile_trigger", new SpellItem(new Item.Settings().registryKey(makeKey("spark_projectile_trigger")).maxCount(1), "spark_projectile_trigger"));
        HEAVY_SPREAD = registerItem("heavy_spread", new SpellItem(new Item.Settings().registryKey(makeKey("heavy_spread")).maxCount(1), "heavy_spread"));
        HOMING_SHOOTER = registerItem("homing_shooter", new SpellItem(new Item.Settings().registryKey(makeKey("homing_shooter")).maxCount(1), "homing_shooter"));
        HOMING = registerItem("homing", new SpellItem(new Item.Settings().registryKey(makeKey("homing")).maxCount(1), "homing"));
        NUKE = registerItem("nuke", new SpellItem(new Item.Settings().registryKey(makeKey("nuke")).maxCount(1), "nuke"));
        LUMINOUS_DRILL = registerItem("luminous_drill", new SpellItem(new Item.Settings().registryKey(makeKey("luminous_drill")).maxCount(1), "luminous_drill"));
        BUBBLE_SHOT = registerItem("bubble_shot", new SpellItem(new Item.Settings().registryKey(makeKey("bubble_shot")).maxCount(1), "bubble_shot"));
        //BUBBLE_SHOT_TRIGGER = registerItem("bubble_shot_trigger", new SpellItem(new Item.Settings().registryKey(makeKey("bubble_shot_trigger")).maxCount(1), "bubble_shot_trigger"));
        DISC_BULLET_BIG = registerItem("disc_bullet_big", new SpellItem(new Item.Settings().registryKey(makeKey("disc_bullet_big")).maxCount(1), "disc_bullet_big"));
        TELEPORT_PROJECTILE = registerItem("teleport_projectile", new SpellItem(new Item.Settings().registryKey(makeKey("teleport_projectile")).maxCount(1), "teleport_projectile"));
        REGENERATION_FIELD = registerItem("regeneration_field", new SpellItem(new Item.Settings().registryKey(makeKey("regeneration_field")).maxCount(1), "regeneration_field"));
        RECHARGE = registerItem("recharge", new SpellItem(new Item.Settings().registryKey(makeKey("recharge")).maxCount(1), "recharge"));
        MANA = registerItem("mana", new SpellItem(new Item.Settings().registryKey(makeKey("mana")).maxCount(1), "mana"));
        SPELL_CORE = registerItem("spell_core", new Item(new Item.Settings().registryKey(makeKey("spell_core")).maxCount(64)));
        // // 钻石锤（暂用钻石锄模型）
        // DIAMOND_HAMMER = registerItem("diamond_hammer",
        //     new HammerItem("diamond", new Item.Settings()
        //         .registryKey(makeKey("diamond_hammer"))
        //         .maxCount(1)
        //         .maxDamage(1000)));
        
        // // 铁匠经验之书
        // BLACKSMITH_EXPERIENCE_BOOK = registerItem("blacksmith_experience_book",
        //     new Item(new Item.Settings().registryKey(makeKey("blacksmith_experience_book"))));
        
        // 添加物品到创造模式物品组
        addItemsToGroup();
    }
    
    /**
     * 将物品添加到创造模式物品组
     */
    private static void addItemsToGroup() {
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.TOOLS).register(entries -> {
            //entries.add(DIAMOND_HAMMER);
            entries.add(WAND);
            entries.add(SPELL_CORE);
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
        Identifier id = Identifier.of(FengxSkillsAndInheritance.MOD_ID, name);
        //FengxSkillsAndInheritance.LOGGER.info("注册物品: {} -> {}", name, id);
        return Registry.register(Registries.ITEM, id, item);
    }
}

