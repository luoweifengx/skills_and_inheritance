package luowei.fengxskillsandinter.block;

import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;
import luowei.fengxskillsandinter.FengxSkillsAndInheritance;
import net.minecraft.block.Block;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;

public class ModBlocks {

    public static Block RUNIC_TABLE;

    public static void registerModBlocks() {
        
        //RUNIC_TABLE = registerBlock("runic_table", new Block(new Block().Settings().registryKey(makeKey("runic_table")), "runic_table"));
        RUNIC_TABLE = registerBlock("runic_table", new RunicTable(Block.Settings.create().registryKey(makeKey("runic_table"))));
        
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
