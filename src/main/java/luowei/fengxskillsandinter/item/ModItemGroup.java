package luowei.fengxskillsandinter.item;

import luowei.fengxskillsandinter.FengxSkillsAndInheritance;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.RegistryKey;
import net.minecraft.text.Text;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ModItemGroup {
    public static final RegistryKey<ItemGroup> CUSTOM_ITEM_GROUP_KEY = RegistryKey.of(Registries.ITEM_GROUP.getKey(), Identifier.of(FengxSkillsAndInheritance.MOD_ID+":"+ "item_group"));
    public static final ItemGroup CUSTOM_ITEM_GROUP = Registry.register(Registries.ITEM_GROUP,CUSTOM_ITEM_GROUP_KEY,
    FabricItemGroup.builder()
        .displayName(Text.translatable("itemGroup.fengxskillsandinheritance.item_group")) // 设置显示名称
        .icon(() -> new ItemStack(ModItems.WAND)) 
        .build());
    // 创建并构建一个自定义物品组，包含图标和显示名称的配置。  
    public static void initialize() {
    }
}
