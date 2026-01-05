# 材质文件说明

## 文件位置
所有锤子的材质文件应该放在这个目录下：
`src/main/resources/assets/fengx-skills-and-inheritance/textures/item/`

## 需要的材质文件

请添加以下PNG格式的材质文件（16x16像素）：

1. **iron_hammer.png** - 铁锤材质
2. **wooden_hammer.png** - 木锤材质  
3. **diamond_hammer.png** - 钻石锤材质

## 材质文件要求

- **格式**: PNG
- **尺寸**: 16x16 像素（标准Minecraft物品材质大小）
- **命名**: 必须与模型文件中引用的名称完全一致（不包括扩展名）

## 模型文件引用

模型文件（`models/item/*.json`）中通过以下方式引用材质：
```json
{
  "textures": {
    "layer0": "fengx-skills-and-inheritance:item/iron_hammer"
  }
}
```

这里的 `iron_hammer` 对应 `textures/item/iron_hammer.png` 文件。

## 临时解决方案

如果暂时没有材质文件，游戏会显示紫色/黑色方块（缺失材质）。你可以：
1. 使用原版材质作为临时方案（修改模型文件使用 `minecraft:item/iron_hoe` 等）
2. 创建简单的16x16像素PNG图片
3. 使用在线材质编辑器（如 Blockbench）创建材质






