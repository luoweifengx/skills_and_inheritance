
当前处于 Ask 模式，无法直接创建文件。下面是整理好的工作总结，你可以复制保存为 `WORK_SUMMARY.md` 或任意文档，方便之后继续工作：

---

# fengx-skills-and-inheritance 项目工作总结

> 用于在上下文丢失后快速恢复工作状态。最后更新：2025-02-11

---

## 1. 项目概述

- **项目名**：fengx-skills-and-inheritance  
- **平台**：Minecraft Fabric 1.21.5  
- **目标**：类似 Noita 的法杖/法术系统  
- **核心概念**：法术序列、draw count、cast delay、recharge time、法力  

---

## 2. 核心架构

```
施法流程：
  CastKey (左键) → CastRequestC2SPayload → ModNetwork → SpellCaster.castSpells
  或
  WandItem.use (右键) → SpellCaster.castSpells (服务端)
```

---

## 3. 关键文件索引

| 功能 | 路径 |
|------|------|
| 施法逻辑 | `spell/SpellCaster.java` |
| 法杖物品 | `item/WandItem.java` |
| 施法按键（左键） | `client/input/CastKey.java` |
| 网络注册 | `network/ModNetwork.java` |
| 法杖 UI（潜行右键） | `screen/WandScreenHandler.java` |
| 法术接口 | `spell/Spell.java` |
| 法术注册 | `spell/SpellRegistry.java` |
| 法术实现 | `spell/spells/SparkProjectile.java`, `X2Spell.java` |
| 物品注册 | `item/ModItems.java` |
| 主入口 | `FengxSkillsAndInheritance.java` |

---

## 4. 已完成功能

### 4.1 施法输入与网络

- **CastKey**：左键 KeyMapping，在 `ClientTickEvents.END_CLIENT_TICK` 中监听  
- 持法杖时发送 `CastRequestC2SPayload`  
- 客户端会检查 `castingDelayEndsAt`，未结束则不发送  
- **ModNetwork**：服务端收到包后调用 `SpellCaster.castSpells(stack, player, world)`  

### 4.2 延迟系统

- **WandItem** NBT：`casting_delay_ends_at`、`recharge_delay_ends_at`（世界 tick 时间）  
- **SpellCaster**：施法后设置 endsAt，下次施法前由 CastKey/服务端检查  

### 4.3 法力系统

- **WandItem** NBT：`maxinum_mana`、`current_mana`、`mana_charge_speed`（注意拼写 maxinum）  
- **SpellCaster**：施法时按 `manaCost` 扣除法力，不足则停止消费法术  

### 4.4 法术序列与回绕

- **spellCachePointer**：下次施法在 `spells` 中的起始索引  
- **spellList**：从 pointer 开始按回绕顺序构建的去空法术列表  
- **spellListIndices**：每个法术在 `spells` 中的索引，队尾用 `-1` 标记  
- **pointer 更新**：`spellListIndices.get(i-1) == -1` 时置 0，否则 `get(i-1)+1`  
- 当前逻辑已确认可接受，有问题再改  

### 4.5 法术容器 UI

- **WandScreenHandler**：潜行右键打开，9x1 格子  
- **WandItem.setSpells**：按 capacity 补齐空位，避免越界  
- 关闭时 `saveSpellsFromInventory` 写回 NBT  

### 4.6 法杖行为

- 右键：非潜行直接施法；潜行打开法术列表 UI  

---

## 5. NBT 字段一览（WandItem）

| 键名 | 类型 | 说明 |
|------|------|------|
| `draw_count` | int | 每次施法消耗的遍历点数 |
| `capacity` | int | 法术槽位数量 |
| `casting_delay` | double | 释放延迟（秒） |
| `casting_delay_ends_at` | long | 释放延迟结束的世界 tick |
| `recharge_delay` | double | 充能延迟（秒） |
| `recharge_delay_ends_at` | long | 充能延迟结束的世界 tick |
| `maxinum_mana` | double | 最大法力（拼写错误） |
| `current_mana` | double | 当前法力 |
| `mana_charge_speed` | double | 法力充能速度（/秒） |
| `spells` | List<String> | 法术 ID 序列 |
| `spell_cache_pointer` | int | 下次施法起始索引 |

---

## 6. Spell 接口

```java
double getCastingDelay(...);
double getRechargeDelay(...);
double getManaCost(...);
int getDrawCost(...);
boolean cast(...);
```

---

## 7. 待完成 / 待修复

1. **法力充能**：注册 `ServerTickEvents.END_SERVER_TICK`，每 tick 按 `mana_charge_speed/20` 恢复法力  
2. **SpellCaster 施法前检查**：在 `castSpells` 开头检查 `currentMana`、`castingDelayEndsAt`、`rechargeDelayEndsAt`，不满足则直接 return  
3. **WandItem.getSpellCachePointer**：若读到 -1 或非法值，归一化为 0  
4. **Spell 实现**：SparkProjectile、X2Spell 需实现 `getDrawCost`、`getManaCost`（若当前为 `getCost` 需重命名/补充）  
5. **PreventWandClickHandler**：持法杖时阻止左键攻击（若需要）  
6. **WandItem.init()**：确认在法杖首次获得/创建时调用  

---

## 8. 技术细节备忘

### 8.1 spellList 构建逻辑（SpellCaster）

- `spellCachePointer == 0`：从 0 遍历到 spells.size()-1，跳过空位  
- `spellCachePointer != 0`：从 pointer 开始回绕遍历，直到回到 pointer；跨越 spells.size() 时插入 -1 作为队尾标记  

### 8.2 消费循环

- 按 drawCount 消费 spellList 中的法术  
- 每个法术检查 manaCost，不足则 break  
- 累计 castingDelay、rechargeDelay  
- 最后用 `spellListIndices.get(i-1)` 更新 pointer  

### 8.3 已知拼写

- `maxinum_mana`（应为 maximum）  

---

## 9. 快速恢复指令

若需继续开发，可对 AI 说：

> 我在做 Minecraft Fabric 1.21.5 的 Noita 风格法杖模组，请先阅读项目根目录的 `WORK_SUMMARY.md` 了解当前进度，然后 [具体任务描述]。

---

**说明**：当前处于 Ask 模式，无法直接创建文件。如需自动生成 `WORK_SUMMARY.md`，可切换到 Agent 模式后再说一次「创建 WORK_SUMMARY.md」。