# 铁砧数据持久化解决方案

## 问题分析

原始设计中，`ANVIL_ITEMS` 和 `ANVIL_DISPLAY_ENTITIES` 使用静态Map存储数据，存在以下安全问题：

1. **数据丢失风险**：游戏/服务器重启后所有铁砧数据丢失
2. **内存泄漏**：如果不清理，数据会持续累积
3. **多人游戏问题**：客户端和服务端状态不一致

## 解决方案

### 1. 数据持久化

创建了 `AnvilWorldData` 类，负责将铁砧数据保存到世界文件夹的 `anvil_data.json` 文件中。

**保存时机：**
- 世界自动保存时
- 服务器关闭时
- 玩家退出时（可扩展）

**加载时机：**
- 世界加载完成后
- 服务器启动时

### 2. 序列化方式

使用 **NBT (Named Binary Tag)** 格式序列化 `ItemStack`，这是Minecraft的标准数据格式：

```java
// 保存时：ItemStack → NBT
NbtCompound itemNbt = new NbtCompound();
itemStack.writeNbt(itemNbt);

// 加载时：NBT → ItemStack
ItemStack restoredStack = ItemStack.fromNbt(itemNbt);
```

### 3. 文件结构

数据文件保存在：`world/save/anvil_data.json`

```json
{
  "anvilItems": {
    "10,64,-15": [
      {
        "itemNbt": {...}  // 内核物品的NBT数据
      },
      {
        "itemNbt": {...}  // 附加物品的NBT数据
      }
    ]
  }
}
```

## 实现文件

### AnvilWorldData.java
- `saveAnvilData(ServerWorld)` - 保存数据到文件
- `loadAnvilData(ServerWorld)` - 从文件加载数据
- `posToString(BlockPos)` - 坐标转字符串
- `stringToPos(String)` - 字符串转坐标

### AnvilDataPersistenceMixin.java
- 监听服务器事件（加载/保存/关闭）
- 自动触发数据持久化操作

## 使用方法

1. **自动工作**：安装后无需额外配置，系统自动处理数据持久化
2. **数据位置**：`world/save/anvil_data.json`
3. **兼容性**：完全向后兼容，旧数据自动清理

## 优势

1. **数据安全**：重启后数据完整恢复
2. **性能优化**：只在必要时读写文件
3. **错误处理**：包含完善的异常处理和日志记录
4. **扩展性**：易于添加更多数据字段

## 注意事项

1. **仅服务端**：持久化只在服务端执行，客户端数据通过网络同步
2. **版本兼容**：如果修改数据结构，需要考虑向后兼容
3. **备份建议**：重要世界建议定期备份数据文件

## 修复内容

### 统一数据结构重构

**问题描述：**
- 原设计使用两个分离的Map（ANVIL_ITEMS和ANVIL_DISPLAY_ENTITIES）
- 游戏重启后，物品数据和显示实体的同步问题
- 数据一致性难以保证

**解决方案：**
1. **统一数据结构** - 创建AnvilState类封装所有相关数据
2. **原子性操作** - 物品和显示实体始终作为一个整体处理
3. **完整持久化** - 确保物品数据和显示实体的完美同步

**核心架构：**
```java
public static class AnvilState {
    public ItemStack[] items = new ItemStack[2];           // 物品数据
    public ItemEntity[] displayEntities = new ItemEntity[2]; // 显示实体
    // 统一的方法管理物品和显示实体的生命周期
}

Map<BlockPos, AnvilState> ANVIL_STATES = new HashMap<>();
```

**实现细节：**
- 使用 `ItemStack.CODEC` 进行完整的ItemStack序列化/反序列化
- 在 `loadAnvilData()` 中自动调用 `recreateDisplayEntities()`
- 所有操作都通过AnvilState进行，确保数据一致性

**架构优势：**
- ✅ **数据一致性** - 物品和显示实体始终同步
- ✅ **原子性操作** - 不会出现部分数据丢失的情况
- ✅ **简化的API** - 统一的方法接口，易于维护
- ✅ **向后兼容** - 保留原有API，确保现有代码正常工作
- ✅ **类型安全** - 编译时检查，减少运行时错误

## 问题修复

### 显示实体同步修复

**问题描述：**
- 取下铁砧物品后，显示实体在客户端未正确清理
- 导致物品被取下后视觉模型仍然显示在上方

**解决方案：**
1. **客户端同步** - 移除服务端/客户端检查，让两端都创建和清理显示实体
2. **清理优化** - 移除`setNeverDespawn()`，让实体可以被正确清理
3. **状态同步** - 确保清理操作在客户端和服务端都执行

### 打磨逻辑重构

**问题描述：**
- 打磨成功才消耗镀层，失败不消耗
- 显示的是打磨前的状态信息

**新逻辑：**
1. **镀层消耗** - 无论成功失败都先消耗镀层，再进行打磨
2. **状态显示** - 显示打磨后的状态（当前成功次数，下次成功率）
3. **锤子消耗** - 锤子加强在打磨前消耗，确保计算准确

**信息格式：**
```
打磨成功/失败信息
打磨成功次数: 1 | 下次成功率: 75.0%
镀层已消耗，可重新镀层继续打磨
```

## 无UI化改造

### 铁砧和砂轮的右键交互拦截

**实现方式：**
- 使用Mixin拦截原版铁砧和砂轮的`onUse`方法
- 自定义交互逻辑优先处理
- 取消原版UI，改为无界面操作

**交互逻辑：**

#### 铁砧操作（AnvilBlockMixin）
```java
@Inject(method = "onUse", at = @At("HEAD"), cancellable = true)
private void onAnvilUse(BlockState state, World world, BlockPos pos, PlayerEntity player,
                       BlockHitResult hit, CallbackInfoReturnable<ActionResult> cir) {
    if (player.isSneaking()) {
        return; // 潜行时保留原版修复UI
    }

    // 尝试自定义锻造逻辑
    boolean handled = AnvilForgeHandler.handleAnvilInteraction(world, pos, player, Hand.MAIN_HAND, hit);

    if (handled) {
        cir.setReturnValue(ActionResult.SUCCESS); // 自定义逻辑成功
    } else {
        cir.setReturnValue(ActionResult.CONSUME);  // 取消原版UI
    }
}
```

#### 砂轮操作（GrindstoneBlockMixin）
```java
@Inject(method = "onUse", at = @At("HEAD"), cancellable = true)
private void onGrindstoneUse(BlockState state, World world, BlockPos pos, PlayerEntity player,
                            BlockHitResult hit, CallbackInfoReturnable<ActionResult> cir) {
    if (player.isSneaking()) {
        return; // 潜行时保留原版去附魔UI
    }

    // 尝试自定义打磨逻辑
    boolean handled = GrindstonePolishHandler.handleGrindstoneInteraction(world, pos, player, Hand.MAIN_HAND);

    if (handled) {
        cir.setReturnValue(ActionResult.SUCCESS); // 自定义逻辑成功
    } else {
        cir.setReturnValue(ActionResult.CONSUME);  // 取消原版UI
    }
}
```

**功能特性：**
- ✅ **智能拦截** - 自动识别特殊操作（锤子、镀层物品等）
- ✅ **UI禁用** - 阻止原版铁砧和砂轮UI打开
- ✅ **潜行例外** - 潜行时仍可使用原版功能（修复/去附魔）
- ✅ **状态反馈** - 通过消息提示操作结果
- ✅ **数据持久化** - 所有状态自动保存和恢复
- ✅ **显示实体管理** - 完整的视觉反馈和清理机制

**交互规则：**

| 操作类型 | 正常状态 | 潜行状态 | 结果 |
|---------|---------|---------|------|
| 右键铁砧 | 自定义锻造系统 | 原版修复UI | 成功/禁用UI |
| 右键砂轮 | 自定义打磨系统 | 原版去附魔UI | 成功/禁用UI |
| 手持锤子 | 执行锻造/打磨 | 无效 | 消耗耐久 |
| 手持镀层物品 | 放置物品 | 无效 | 占用槽位 |
| 空手右键 | 取下物品 | 无效 | 清理槽位 |
| 其他物品 | 显示错误消息 | 无效 | ActionResult.CONSUME |

## 扩展建议

可以进一步扩展为：
- 支持更多铁砧状态（如损坏程度）
- 添加数据版本控制
- 实现数据迁移工具
