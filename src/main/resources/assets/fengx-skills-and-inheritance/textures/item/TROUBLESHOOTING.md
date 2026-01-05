# 材质显示问题排查指南

## 文件结构检查清单

### ✅ 已确认正确的部分：
1. **材质文件位置**：`src/main/resources/assets/fengx-skills-and-inheritance/textures/item/`
2. **模型文件位置**：`src/main/resources/assets/fengx-skills-and-inheritance/models/item/`
3. **文件命名**：iron_hammer.png, wooden_hammer.png, diamond_hammer.png
4. **模型路径引用**：`fengx-skills-and-inheritance:item/iron_hammer`

## 可能的问题和解决方案

### 1. 材质文件格式问题
**检查项：**
- 确保PNG文件不是损坏的
- 确保文件大小不为0字节
- 确保是有效的PNG格式

**解决方法：**
- 用图片编辑器打开PNG文件，确认可以正常显示
- 如果文件损坏，重新创建或导出

### 2. 材质文件分辨率问题
**检查项：**
- 确保是2的幂次方（16, 32, 64, 128等）
- 确保是正方形（宽度=高度）
- 确保不是太大（建议不超过256x256）

**解决方法：**
- 检查图片尺寸
- 如果不符合要求，用图片编辑器调整

### 3. 需要重新构建模组
**问题：**
- 添加新材质文件后，需要重新构建模组才能生效

**解决方法：**
```bash
# 在项目根目录执行
./gradlew build
# 或者
./gradlew build --refresh-dependencies
```

### 4. 游戏需要重新加载资源
**问题：**
- 游戏可能缓存了旧的资源

**解决方法：**
- 完全退出游戏并重新启动
- 或者使用F3+T重新加载资源包（开发环境）

### 5. 模型文件格式问题
**检查项：**
- JSON格式是否正确
- 是否有语法错误
- 路径引用是否正确

**当前模型文件格式：**
```json
{
  "parent": "minecraft:item/handheld",
  "textures": {
    "layer0": "fengx-skills-and-inheritance:item/iron_hammer"
  }
}
```

### 6. 命名空间问题
**检查项：**
- 模型文件中的命名空间必须与fabric.mod.json中的id一致
- 当前命名空间：`fengx-skills-and-inheritance`
- 路径格式：`命名空间:item/文件名（不含扩展名）`

## 调试步骤

### 步骤1：验证文件存在
确认以下文件存在：
- `textures/item/iron_hammer.png`
- `textures/item/wooden_hammer.png`
- `textures/item/diamond_hammer.png`

### 步骤2：检查PNG文件
- 用图片查看器打开PNG文件
- 确认可以正常显示
- 检查文件大小（不应该是0字节）

### 步骤3：验证模型文件
- 检查JSON格式是否正确
- 确认路径引用正确
- 确认命名空间正确

### 步骤4：重新构建
```bash
./gradlew clean build
```

### 步骤5：重新加载游戏
- 完全退出游戏
- 重新启动游戏
- 或者使用F3+T（开发环境）

## 常见错误示例

### ❌ 错误的路径格式：
```json
"layer0": "item/iron_hammer"  // 缺少命名空间
"layer0": "fengx-skills-and-inheritance:iron_hammer"  // 缺少item/前缀
"layer0": "fengx-skills-and-inheritance:item/iron_hammer.png"  // 不应该包含扩展名
```

### ✅ 正确的路径格式：
```json
"layer0": "fengx-skills-and-inheritance:item/iron_hammer"
```

## 如果仍然无法显示

1. **检查游戏日志**：查看是否有资源加载错误
2. **使用原版材质测试**：临时使用`minecraft:item/iron_hoe`测试模型是否正常
3. **检查其他模组冲突**：确认没有其他模组修改了相同资源
4. **验证资源包优先级**：确认模组资源包已正确加载

