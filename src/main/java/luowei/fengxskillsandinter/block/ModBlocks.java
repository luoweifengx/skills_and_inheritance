package luowei.fengxskillsandinter.block;

import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;
import luowei.fengxskillsandinter.FengxSkillsAndInheritance;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;

public class ModBlocks {

    public static Block RUNIC_TABLE;

    public static void registerModBlocks() {
        
        //RUNIC_TABLE = registerBlock("runic_table", new Block(new Block().Settings().registryKey(makeKey("runic_table")), "runic_table"));
        // 继承石砖的挖掘/工具相关字段（1.21+ 仅靠 create()+requiresTool 可能导致镐无法累积破坏进度）。
        // 仍通过 data/minecraft/tags/blocks/mineable/pickaxe 与 loot_table 控制：镐类可挖、木镐即可掉落。
        RUNIC_TABLE = registerBlock(
            "runic_table",
            new RunicTable(
                Block.Settings.copy(Blocks.STONE_BRICKS)
                    .registryKey(makeKey("runic_table"))
                    .strength(2.5f, 6.0f)));
        
    }
    private static RegistryKey<Block> makeKey(String name) {
        return RegistryKey.of(RegistryKeys.BLOCK, Identifier.of(FengxSkillsAndInheritance.MOD_ID, name));
    }
    private static Block registerBlock(String name, Block block) {
        Identifier id = Identifier.of(FengxSkillsAndInheritance.MOD_ID, name);
        //FengxSkillsAndInheritance.LOGGER.info("注册物品: {} -> {}", name, id);
        return Registry.register(Registries.BLOCK, id, block);
    }
}
