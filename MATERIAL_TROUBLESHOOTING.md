# 材质加载问题诊断指南

## 物品注册信息

### MOD_ID
```
fengx-skills-and-inheritance
```

### 物品注册名称
- `wooden_hammer` → 完整ID: `fengx-skills-and-inheritance:wooden_hammer`
- `iron_hammer` → 完整ID: `fengx-skills-and-inheritance:iron_hammer`
- `diamond_hammer` → 完整ID: `fengx-skills-and-inheritance:diamond_hammer`

## 文件路径要求

### 模型文件路径
```
src/main/resources/assets/fengx-skills-and-inheritance/models/item/
├── wooden_hammer.json
├── iron_hammer.json
└── diamond_hammer.json
```

### 材质文件路径
```
src/main/resources/assets/fengx-skills-and-inheritance/textures/item/
├── wooden_hammer.png
├── iron_hammer.png
└── diamond_hammer.png
```

## 模型文件格式

### 示例：iron_hammer.json
```json
{
  "parent": "minecraft:item/handheld",
  "textures": {
    "layer0": "fengx-skills-and-inheritance:item/iron_hammer"
  }
}
```

**重要提示：**
- `parent` 必须是 `"minecraft:item/handheld"`（手持物品模型）
- `textures.layer0` 路径格式：`命名空间:item/文件名（不含扩展名）`
- 命名空间必须与 `MOD_ID` 完全一致：`fengx-skills-and-inheritance`

## 材质文件要求

1. **文件格式**：必须是 PNG 格式
2. **文件命名**：必须与模型文件中的纹理名称完全匹配（不含扩展名）
3. **分辨率**：支持 16x16、32x32、64x64 等（建议使用 16x16 或 32x32）
4. **文件位置**：必须在 `textures/item/` 目录下

## 常见问题排查

### 1. 材质文件不存在
**症状**：物品显示为紫色/黑色方块
**解决**：检查材质文件是否存在于正确路径

### 2. 文件命名不匹配
**症状**：物品显示为紫色/黑色方块
**解决**：确保模型文件中的纹理路径与材质文件名完全匹配

### 3. 命名空间错误
**症状**：材质无法加载
**解决**：确保模型文件中的命名空间与 `MOD_ID` 完全一致

### 4. 文件映射版本问题
**当前版本**：`yarn_mappings=1.21.5+build.1`
**检查**：确认使用的是正确的 Minecraft 1.21.5 映射

## 调试步骤

1. **检查日志**：启动游戏后查看日志文件，查找以下信息：
   - `注册物品: xxx -> fengx-skills-and-inheritance:xxx`
   - 任何关于材质加载的错误信息

2. **验证文件存在**：
   ```bash
   # Windows PowerShell
   dir src\main\resources\assets\fengx-skills-and-inheritance\models\item\*.json
   dir src\main\resources\assets\fengx-skills-and-inheritance\textures\item\*.png
   ```

3. **重新构建项目**：
   ```bash
   ./gradlew clean build
   ```

4. **检查构建输出**：
   查看 `build/resources/main/assets/` 目录，确认文件已正确复制

## 物品注册代码参考

```java
// ModItems.java
public static final String MOD_ID = "fengx-skills-and-inheritance";

private static Item registerItem(String name, Item item) {
    Identifier id = Identifier.of(FengxSkillsAndInheritance.MOD_ID, name);
    return Registry.register(Registries.ITEM, id, item);
}

// 注册示例
IRON_HAMMER = registerItem("iron_hammer",
    new HammerItem("iron", new Item.Settings()
        .registryKey(makeKey("iron_hammer"))
        .maxCount(1)
        .maxDamage(500)));
```

## 如果问题仍然存在

1. **检查游戏日志** (`run/logs/latest.log`)：
   - 搜索 `fengx-skills-and-inheritance`
   - 搜索 `iron_hammer`、`wooden_hammer`、`diamond_hammer`
   - 查找任何 `ERROR` 或 `WARN` 级别的材质相关错误

2. **验证材质文件**：
   - 确保 PNG 文件不是损坏的
   - 尝试用图片查看器打开材质文件
   - 确认文件大小不为 0

3. **清理并重新构建**：
   ```bash
   ./gradlew clean
   ./gradlew build
   ```

4. **检查资源包**：
   - 在游戏中按 F3+T 重新加载资源
   - 检查是否有其他资源包覆盖了你的材质




