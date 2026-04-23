package luowei.fengxskillsandinter.config;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParseException;

import luowei.fengxskillsandinter.FengxSkillsAndInheritance;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;

/**
 * 优先读取 {@code config/&lt;modid&gt;/item_attributes.json}；若不存在或解析失败，则使用 jar 内
 * {@code assets/&lt;modid&gt;/item_attributes.defaults.json}。首次仅有默认资源时，会复制到 config 供编辑。
 */
public final class ItemAttributeRegistry {

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
    private static final String CONFIG_FILE_NAME = "item_attributes.json";
    private static final String DEFAULT_RESOURCE_PATH = "/assets/fengx-skills-and-inheritance/item_attributes.defaults.json";

    /**
     * 与 JSON 数组下标：容量、蓝量、回蓝速度、释放数、充能(间隔)、施法延迟
     */
    public static final String[] DIMENSION_LABELS = {
        "capacity",
        "casting_delay",
        "recharge_delay",
        "mana_charge_speed",
        "max_mana",
        "draw_count"
    };

    private static volatile ItemAttributeVector defaultVector = ItemAttributeVector.uniform(5.0);
    private static volatile Map<Identifier, ItemAttributeVector> overrides = Map.of();

    private ItemAttributeRegistry() {}

    public static void load() {
        Path configDir = FabricLoader.getInstance().getConfigDir().resolve(FengxSkillsAndInheritance.MOD_ID);
        try {
            Files.createDirectories(configDir);
        } catch (IOException e) {
            FengxSkillsAndInheritance.LOGGER.error("Failed to create config directory: {}", configDir, e);
        }
        Path configFile = configDir.resolve(CONFIG_FILE_NAME);

        if (Files.isRegularFile(configFile)) {
            try (BufferedReader reader = Files.newBufferedReader(configFile, StandardCharsets.UTF_8)) {
                JsonRoot root = GSON.fromJson(reader, JsonRoot.class);
                applyRoot(root, "config/" + CONFIG_FILE_NAME);
                return;
            } catch (IOException | JsonParseException e) {
                FengxSkillsAndInheritance.LOGGER.error("Failed to parse {}, falling back to bundled defaults", configFile, e);
            }
        }

        InputStream in = FengxSkillsAndInheritance.class.getResourceAsStream(DEFAULT_RESOURCE_PATH);
        if (in == null) {
            FengxSkillsAndInheritance.LOGGER.error("Missing bundled resource {}", DEFAULT_RESOURCE_PATH);
            applyRoot(null, "(none)");
            return;
        }
        try (in) {
            JsonRoot root = GSON.fromJson(new java.io.InputStreamReader(in, StandardCharsets.UTF_8), JsonRoot.class);
            applyRoot(root, "resource" + DEFAULT_RESOURCE_PATH);

            if (!Files.isRegularFile(configFile)) {
                try (InputStream copyIn = FengxSkillsAndInheritance.class.getResourceAsStream(DEFAULT_RESOURCE_PATH)) {
                    if (copyIn != null) {
                        Files.copy(copyIn, configFile);
                        FengxSkillsAndInheritance.LOGGER.info("Copied default item attributes to {}", configFile);
                    }
                } catch (IOException e) {
                    FengxSkillsAndInheritance.LOGGER.warn("Could not write default config to {}", configFile, e);
                }
            }
        } catch (IOException | JsonParseException e) {
            FengxSkillsAndInheritance.LOGGER.error("Failed to load bundled {}", DEFAULT_RESOURCE_PATH, e);
            applyRoot(null, "(none)");
        }
    }

    private static void applyRoot(JsonRoot root, String sourceLabel) {
        ItemAttributeVector def = ItemAttributeVector.uniform(5.0);
        Map<Identifier, ItemAttributeVector> map = new HashMap<>();

        if (root != null && root.defaults != null) {
            def = parseVector("defaults", root.defaults, def);
        }
        if (root != null && root.overrides != null) {
            for (Map.Entry<String, List<Double>> e : root.overrides.entrySet()) {
                Identifier id = Identifier.tryParse(e.getKey());
                if (id == null) {
                    FengxSkillsAndInheritance.LOGGER.warn("Skipping invalid item id: {}", e.getKey());
                    continue;
                }
                ItemAttributeVector v = parseVector(e.getKey(), e.getValue(), def);
                map.put(id, v);
            }
        }

        defaultVector = def;
        overrides = Collections.unmodifiableMap(map);
        FengxSkillsAndInheritance.LOGGER.info(
            "Item attributes from {} — default {}, {} override(s)",
            sourceLabel,
            def,
            map.size()
        );
    }

    private static ItemAttributeVector parseVector(String label, List<Double> list, ItemAttributeVector fallback) {
        if (list == null || list.size() != ItemAttributeVector.DIMENSIONS) {
            FengxSkillsAndInheritance.LOGGER.warn(
                "Vector '{}' must have {} numbers (got {}); using fallback {}",
                label,
                ItemAttributeVector.DIMENSIONS,
                list == null ? null : list.size(),
                fallback
            );
            return fallback;
        }
        double[] arr = new double[ItemAttributeVector.DIMENSIONS];
        for (int i = 0; i < ItemAttributeVector.DIMENSIONS; i++) {
            arr[i] = list.get(i);
        }
        return ItemAttributeVector.fromArray(arr);
    }

    public static ItemAttributeVector getDefaultVector() {
        return defaultVector;
    }

    public static ItemAttributeVector get(Item item) {
        Identifier id = Registries.ITEM.getId(item);
        return overrides.getOrDefault(id, defaultVector);
    }

    public static ItemAttributeVector get(ItemStack stack) {
        return stack.isEmpty() ? defaultVector : get(stack.getItem());
    }

    public static double get(Item item, int dimensionIndex) {
        return get(item).get(dimensionIndex);
    }

    public static Map<Identifier, ItemAttributeVector> getOverridesView() {
        return overrides;
    }

    private static final class JsonRoot {
        @SuppressWarnings("unused")
        int schemaVersion;
        List<Double> defaults;
        Map<String, List<Double>> overrides;
    }
}
