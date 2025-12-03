package luowei.fengxskillsandinter.block;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import net.minecraft.entity.ItemEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtOps;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.WorldSavePath;
import net.minecraft.util.math.Vec3d;
import luowei.fengxskillsandinter.FengxSkillsAndInheritance;
import luowei.fengxskillsandinter.util.ItemDataHelper;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

/**
 * 铁砧世界数据持久化管理器
 * 负责保存和加载铁砧状态数据到世界文件
 */
public class AnvilWorldData {
    private static final String DATA_FILE = "anvil_data.json";
    private static final Gson GSON = new GsonBuilder()
            .setPrettyPrinting()
            .create();

    // 存储铁砧状态数据的结构
    public static class AnvilData {
        public Map<String, AnvilStateData> anvilStates = new HashMap<>();
    }

    // 铁砧状态的序列化数据结构
    public static class AnvilStateData {
        public ItemStackData[] items = new ItemStackData[2];

        public AnvilStateData() {}

        public AnvilStateData(ItemStackData[] items) {
            this.items = items;
        }
    }

    // ItemStack的序列化数据结构
    public static class ItemStackData {
        public NbtCompound fullItemNbt;

        public ItemStackData() {}

        public ItemStackData(NbtCompound fullItemNbt) {
            this.fullItemNbt = fullItemNbt;
        }
    }

    /**
     * 保存铁砧数据到世界文件
     */
    public static void saveAnvilData(ServerWorld world) {
        try {
            File dataFile = getDataFile(world);
            AnvilData data = new AnvilData();

            // 转换内存中的数据为可序列化格式
            for (Map.Entry<BlockPos, AnvilForgeHandler.AnvilState> entry : AnvilForgeHandler.ANVIL_STATES.entrySet()) {
                BlockPos pos = entry.getKey();
                AnvilForgeHandler.AnvilState anvilState = entry.getValue();

                String posKey = posToString(pos);
                ItemStackData[] itemData = new ItemStackData[2];

                for (int i = 0; i < 2; i++) {
                    if (anvilState.hasItem(i)) {
                        // 完整保存ItemStack的NBT数据
                        try {
                            NbtCompound fullItemNbt = new NbtCompound();
                            // 使用ItemStack的完整序列化
                            fullItemNbt = (NbtCompound) ItemStack.CODEC.encodeStart(DynamicRegistryManager.EMPTY.getOps(NbtOps.INSTANCE), anvilState.items[i]).getOrThrow();
                            itemData[i] = new ItemStackData(fullItemNbt);
                        } catch (Exception e) {
                            FengxSkillsAndInheritance.LOGGER.warn("Failed to encode item at " + pos + " slot " + i, e);
                        }
                    }
                }

                data.anvilStates.put(posKey, new AnvilStateData(itemData));

                // 向后兼容：更新旧数据结构
                AnvilForgeHandler.ANVIL_ITEMS.put(pos, anvilState.items);
                AnvilForgeHandler.ANVIL_DISPLAY_ENTITIES.put(pos, anvilState.displayEntities);
            }

            // 写入文件
            try (FileWriter writer = new FileWriter(dataFile)) {
                GSON.toJson(data, writer);
            }

        } catch (IOException e) {
            FengxSkillsAndInheritance.LOGGER.error("Failed to save anvil data", e);
        }
    }

    /**
     * 从世界文件加载铁砧数据
     */
    public static void loadAnvilData(ServerWorld world) {
        try {
            File dataFile = getDataFile(world);
            if (!dataFile.exists()) {
                return; // 文件不存在，跳过加载
            }

            try (FileReader reader = new FileReader(dataFile)) {
                Type type = new TypeToken<AnvilData>(){}.getType();
                AnvilData data = GSON.fromJson(reader, type);

                if (data != null && data.anvilStates != null) {
                    FengxSkillsAndInheritance.LOGGER.info("Loaded " + data.anvilStates.size() + " anvil states from disk");

                    // 从统一数据结构恢复状态
                    for (Map.Entry<String, AnvilStateData> entry : data.anvilStates.entrySet()) {
                        BlockPos pos = stringToPos(entry.getKey());
                        if (pos != null) {
                            AnvilForgeHandler.AnvilState anvilState = new AnvilForgeHandler.AnvilState();

                            if (entry.getValue() != null && entry.getValue().items != null) {
                                for (int i = 0; i < 2; i++) {
                                    if (entry.getValue().items[i] != null && entry.getValue().items[i].fullItemNbt != null) {
                                        try {
                                            // 使用完整的ItemStack反序列化
                                            ItemStack restoredStack = ItemStack.CODEC.parse(DynamicRegistryManager.EMPTY.getOps(NbtOps.INSTANCE), entry.getValue().items[i].fullItemNbt).getOrThrow();
                                            if (restoredStack != null && !restoredStack.isEmpty()) {
                                                anvilState.items[i] = restoredStack;
                                            }
                                        } catch (Exception e) {
                                            FengxSkillsAndInheritance.LOGGER.warn("Failed to restore item at " + pos + " slot " + i, e);
                                        }
                                    }
                                }
                            }

                            // 只有当有物品数据时才恢复状态
                            boolean hasItems = false;
                            if (entry.getValue() != null && entry.getValue().items != null) {
                                for (ItemStackData itemData : entry.getValue().items) {
                                    if (itemData != null && itemData.fullItemNbt != null) {
                                        hasItems = true;
                                        break;
                                    }
                                }
                            }

                            if (hasItems) {
                                AnvilForgeHandler.ANVIL_STATES.put(pos, anvilState);
                                // 重新创建显示实体
                                recreateDisplayEntities(world, pos, entry.getValue().items);

                                // 向后兼容：更新旧数据结构
                                AnvilForgeHandler.ANVIL_ITEMS.put(pos, anvilState.items);
                                AnvilForgeHandler.ANVIL_DISPLAY_ENTITIES.put(pos, anvilState.displayEntities);
                            }
                        }
                    }
                }
            }

        } catch (Exception e) {
            FengxSkillsAndInheritance.LOGGER.error("Failed to load anvil data", e);
        }
    }

    /**
     * 重新创建显示实体
     */
    private static void recreateDisplayEntities(ServerWorld world, BlockPos pos, ItemStackData[] itemData) {
        try {
            AnvilForgeHandler.AnvilState anvilState = AnvilForgeHandler.ANVIL_STATES.get(pos);
            if (anvilState == null) {
                return;
            }

            // 为每个有物品的槽位创建显示实体
            for (int i = 0; i < 2; i++) {
                if (itemData[i] != null && itemData[i].fullItemNbt != null && anvilState.items[i] != null) {
                    try {
                        FengxSkillsAndInheritance.LOGGER.info("Recreating display entity at " + pos + " slot " + i + ": " + anvilState.items[i].getItem().toString());

                        // 创建显示实体
                        AnvilForgeHandler.createDisplayEntity(world, pos, anvilState, i);
                    } catch (Exception e) {
                        FengxSkillsAndInheritance.LOGGER.warn("Failed to recreate display entity at " + pos + " slot " + i, e);
                    }
                }
            }
        } catch (Exception e) {
            FengxSkillsAndInheritance.LOGGER.error("Failed to recreate display entities at " + pos, e);
        }
    }


    /**
     * 获取世界数据文件
     */
    private static File getDataFile(ServerWorld world) {
        return world.getServer().getSavePath(WorldSavePath.ROOT).resolve(DATA_FILE).toFile();
    }

    /**
     * BlockPos转字符串
     */
    private static String posToString(BlockPos pos) {
        return pos.getX() + "," + pos.getY() + "," + pos.getZ();
    }

    /**
     * 字符串转BlockPos
     */
    private static BlockPos stringToPos(String str) {
        try {
            String[] parts = str.split(",");
            if (parts.length == 3) {
                return new BlockPos(
                    Integer.parseInt(parts[0]),
                    Integer.parseInt(parts[1]),
                    Integer.parseInt(parts[2])
                );
            }
        } catch (NumberFormatException e) {
            FengxSkillsAndInheritance.LOGGER.warn("Invalid position format: " + str);
        }
        return null;
    }
}
