package luowei.fengxskillsandinter.block;

import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import luowei.fengxskillsandinter.item.HammerItem;
import luowei.fengxskillsandinter.weapon.WeaponAttributes;

import java.util.HashMap;
import java.util.Map;
import java.util.Arrays;

/**
 * 铁砧锻造处理器
 * 实现无UI的铁砧交互和镀层系统
 */
public class AnvilForgeHandler {

    /**
     * 铁砧状态数据结构
     * 将物品数据和显示实体统一管理，确保数据一致性
     */
    public static class AnvilState {
        public ItemStack[] items = new ItemStack[2];
        public ItemEntity[] displayEntities = new ItemEntity[2];

        public AnvilState() {
            this.items = new ItemStack[2];
            this.displayEntities = new ItemEntity[2];
        }

        /**
         * 检查指定槽位是否有物品
         */
        public boolean hasItem(int slot) {
            return slot >= 0 && slot < 2 && items[slot] != null && !items[slot].isEmpty();
        }

        /**
         * 检查指定槽位是否有显示实体
         */
        public boolean hasDisplayEntity(int slot) {
            return slot >= 0 && slot < 2 && displayEntities[slot] != null && !displayEntities[slot].isRemoved();
        }

        /**
         * 清空指定槽位的数据
         */
        public void clearSlot(int slot) {
            if (slot >= 0 && slot < 2) {
                items[slot] = null;
                if (displayEntities[slot] != null) {
                    displayEntities[slot].discard();
                    displayEntities[slot] = null;
                }
            }
        }

        /**
         * 清空所有数据
         */
        public void clear() {
            for (int i = 0; i < 2; i++) {
                clearSlot(i);
            }
        }

        /**
         * 放置物品到指定槽位
         */
        public void setItem(int slot, ItemStack stack) {
            if (slot >= 0 && slot < 2) {
                items[slot] = stack.copy();
                items[slot].setCount(1);
            }
        }

        /**
         * 获取指定槽位的物品
         */
        public ItemStack getItem(int slot) {
            if (slot >= 0 && slot < 2 && hasItem(slot)) {
                return items[slot].copy();
            }
            return null;
        }
    }

    // 统一存储所有铁砧状态（位置 -> 完整状态）
    public static final Map<BlockPos, AnvilState> ANVIL_STATES = new HashMap<>();

    // 为了向后兼容，保留原有接口（已废弃，建议使用ANVIL_STATES）
    @Deprecated
    public static final Map<BlockPos, ItemStack[]> ANVIL_ITEMS = new HashMap<>();

    @Deprecated
    public static final Map<BlockPos, ItemEntity[]> ANVIL_DISPLAY_ENTITIES = new HashMap<>();
    
    /**
     * 处理玩家右键铁砧的交互
     */
    public static boolean handleAnvilInteraction(World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        if (world.isClient) return false;

        ItemStack heldStack = player.getStackInHand(hand);
        AnvilState anvilState = ANVIL_STATES.computeIfAbsent(pos, k -> new AnvilState());

        // 情况1: 空手右键 - 取下物品
        if (heldStack.isEmpty()) {
            return handleTakeItem(world, pos, player, anvilState);
        }

        // 情况2: 手持锤子 - 执行锻造
        if (heldStack.getItem() instanceof HammerItem) {
            return handleForging(world, pos, player, hand, anvilState);
        }

        // 情况3: 手持物品 - 放置物品
        return handlePlaceItem(world, pos, player, hand, anvilState);
    }
    
    /**
     * 处理放置物品
     */
    private static boolean handlePlaceItem(World world, BlockPos pos, PlayerEntity player, Hand hand, AnvilState anvilState) {
        ItemStack heldStack = player.getStackInHand(hand);

        // 放置内核（武器/工具）
        if (!anvilState.hasItem(0) && isValidCore(heldStack)) {
            anvilState.setItem(0, heldStack);
            heldStack.decrement(1);

            // 创建显示实体
            AnvilForgeHandler.createDisplayEntity(world, pos, anvilState, 0);

            player.sendMessage(Text.literal("§7已放置内核物品"), true);
            world.playSound(null, pos, SoundEvents.BLOCK_ANVIL_PLACE, SoundCategory.BLOCKS, 0.5f, 1.0f);
            return true;
        }

        // 放置附加物（矿物等）
        if (anvilState.hasItem(0) && !anvilState.hasItem(1) && isValidAdditive(heldStack)) {
            anvilState.setItem(1, heldStack);
            heldStack.decrement(1);

            // 创建显示实体
            AnvilForgeHandler.createDisplayEntity(world, pos, anvilState, 1);

            player.sendMessage(Text.literal("§7已放置附加物"), true);
            world.playSound(null, pos, SoundEvents.BLOCK_ANVIL_PLACE, SoundCategory.BLOCKS, 0.5f, 1.0f);
            return true;
        }

        player.sendMessage(Text.literal("§c无法放置此物品"), true);
        return false;
    }
    
    /**
     * 处理取下物品
     */
    private static boolean handleTakeItem(World world, BlockPos pos, PlayerEntity player, AnvilState anvilState) {
        // 按照放置顺序依次取下：先取附加物，再取内核
        if (anvilState.hasItem(1)) {
            giveItemToPlayer(world, pos, player, anvilState.getItem(1));
            removeDisplayEntity(world, pos, anvilState, 1);
            anvilState.clearSlot(1);

            player.sendMessage(Text.literal("§7已取下附加物"), true);
            return true;
        }

        if (anvilState.hasItem(0)) {
            giveItemToPlayer(world, pos, player, anvilState.getItem(0));
            clearAllDisplayEntities(world, pos, anvilState);
            anvilState.clear();

            player.sendMessage(Text.literal("§7已取下内核物品"), true);
            ANVIL_STATES.remove(pos); // 清空后移除记录
            return true;
        }

        return false;
    }
    
    /**
     * 处理锻造操作
     */
    private static boolean handleForging(World world, BlockPos pos, PlayerEntity player, Hand hand, AnvilState anvilState) {
        ItemStack hammerStack = player.getStackInHand(hand);

        // 检查是否有内核和附加物
        if (!anvilState.hasItem(0) || !anvilState.hasItem(1)) {
            player.sendMessage(Text.literal("§c需要放置内核和附加物才能锻造"), true);
            return false;
        }

        // 获取附加物类型（镀层材料）
        String coatingType = getCoatingType(anvilState.items[1]);
        if (coatingType == null) {
            player.sendMessage(Text.literal("§c附加物无法用于镀层"), true);
            // 消耗附加物
            anvilState.clearSlot(1);
            return true;
        }

        // 检查内核是否已有镀层
        if (WeaponAttributes.hasCoating(anvilState.items[0])) {
            player.sendMessage(Text.literal("§c该武器已有镀层，请先打磨移除"), true);
            return false;
        }

        // 检查并消耗物品经验（50点）
        if (!luowei.fengxskillsandinter.item.ItemExperience.consumeExperience(anvilState.items[0], 50)) {
            player.sendMessage(Text.literal("§c物品经验不足，无法锻造（需要50点经验）"), true);
            return false;
        }

        // 执行镀层操作
        WeaponAttributes.setCoating(anvilState.items[0], coatingType);

        // 播放音效和动画
        world.playSound(null, pos, SoundEvents.BLOCK_ANVIL_USE, SoundCategory.BLOCKS, 1.0f, 1.0f);

        // 消耗锤子耐久
        hammerStack.damage(1, player, hand == Hand.MAIN_HAND ? net.minecraft.entity.EquipmentSlot.MAINHAND : net.minecraft.entity.EquipmentSlot.OFFHAND);

        // 消耗附加物
        removeDisplayEntity(world, pos, anvilState, 1);
        anvilState.clearSlot(1);

        // 返回镀层后的物品给玩家
        giveItemToPlayer(world, pos, player, anvilState.items[0]);
        removeDisplayEntity(world, pos, anvilState, 0);
        anvilState.clear();

        player.sendMessage(Text.literal("§a锻造成功！已添加" + coatingType + "镀层"), true);

        return true;
    }
    
    /**
     * 判断是否为有效的内核（武器/工具）
     */
    private static boolean isValidCore(ItemStack stack) {
        // 检查是否为武器或工具
        return stack.getItem() == Items.WOODEN_SWORD ||
               stack.getItem() == Items.STONE_SWORD ||
               stack.getItem() == Items.IRON_SWORD ||
               stack.getItem() == Items.GOLDEN_SWORD ||
               stack.getItem() == Items.DIAMOND_SWORD ||
               stack.getItem() == Items.NETHERITE_SWORD ||
               stack.getItem() == Items.WOODEN_AXE ||
               stack.getItem() == Items.STONE_AXE ||
               stack.getItem() == Items.IRON_AXE ||
               stack.getItem() == Items.GOLDEN_AXE ||
               stack.getItem() == Items.DIAMOND_AXE ||
               stack.getItem() == Items.NETHERITE_AXE ||
               stack.getItem() == Items.TRIDENT;
    }
    
    /**
     * 判断是否为有效的附加物（矿物等）
     */
    private static boolean isValidAdditive(ItemStack stack) {
        return stack.getItem() == Items.COPPER_INGOT ||
               stack.getItem() == Items.IRON_INGOT ||
               stack.getItem() == Items.GOLD_INGOT ||
               stack.getItem() == Items.DIAMOND ||
               stack.getItem() == Items.NETHERITE_INGOT ||
               stack.getItem() == Items.COAL ||
               stack.getItem() == Items.REDSTONE ||
               stack.getItem() == Items.LAPIS_LAZULI;
    }
    
    /**
     * 根据附加物获取镀层类型
     */
    private static String getCoatingType(ItemStack stack) {
        if (stack.getItem() == Items.COPPER_INGOT) return "copper";
        if (stack.getItem() == Items.IRON_INGOT) return "iron";
        if (stack.getItem() == Items.GOLD_INGOT) return "gold";
        if (stack.getItem() == Items.DIAMOND) return "diamond";
        if (stack.getItem() == Items.NETHERITE_INGOT) return "netherite";
        if (stack.getItem() == Items.COAL) return "coal";
        if (stack.getItem() == Items.REDSTONE) return "redstone";
        if (stack.getItem() == Items.LAPIS_LAZULI) return "lapis";
        return null;
    }
    
    /**
     * 给予玩家物品（如果背包满了就掉落）
     */
    private static void giveItemToPlayer(World world, BlockPos pos, PlayerEntity player, ItemStack stack) {
        if (!player.giveItemStack(stack)) {
            ItemEntity itemEntity = new ItemEntity(world, pos.getX() + 0.5, pos.getY() + 1, pos.getZ() + 0.5, stack);
            world.spawnEntity(itemEntity);
        }
    }
    
    /**
     * 创建显示实体（模拟掉落物在铁砧上）- 新版本，使用AnvilState
     */
    public static void createDisplayEntity(World world, BlockPos pos, AnvilState anvilState, int slot) {
        // 在服务端和客户端都要创建显示实体
        // if (world.isClient) return; // 移除此行，让客户端也能创建

        // 如果已有实体，先移除
        if (anvilState.displayEntities[slot] != null) {
            if (!anvilState.displayEntities[slot].isRemoved()) {
                anvilState.displayEntities[slot].discard();
            }
            anvilState.displayEntities[slot] = null;
        }

        // 计算显示位置（铁砧上方）
        double x = pos.getX() + 0.5 + (slot == 0 ? -0.15 : 0.15); // 内核靠左，附加物靠右
        double y = pos.getY() + 1.0; // 铁砧上方
        double z = pos.getZ() + 0.5;

        // 创建物品实体
        ItemEntity displayEntity = new ItemEntity(world, x, y, z, anvilState.items[slot].copy());

        // 设置为不会被拾取、不会合并，但可以被清理
        displayEntity.setPickupDelay(Integer.MAX_VALUE);
        displayEntity.setVelocity(Vec3d.ZERO);
        displayEntity.setNeverDespawn(); // 移除此行，让实体可以被清理
        displayEntity.setInvulnerable(true);

        world.spawnEntity(displayEntity);
        anvilState.displayEntities[slot] = displayEntity;
    }

    /**
     * 创建显示实体（模拟掉落物在铁砧上）- 旧版本，向后兼容
     */
    @Deprecated
    private static void createDisplayEntity(World world, BlockPos pos, ItemStack stack, int slot) {
        if (world.isClient) return;

        ItemEntity[] entities = ANVIL_DISPLAY_ENTITIES.computeIfAbsent(pos, k -> new ItemEntity[2]);

        // 如果已有实体，先移除
        if (entities[slot] != null && !entities[slot].isRemoved()) {
            entities[slot].discard();
        }

        // 计算显示位置（铁砧上方）
        double x = pos.getX() + 0.5 + (slot == 0 ? -0.15 : 0.15); // 内核靠左，附加物靠右
        double y = pos.getY() + 1.0; // 铁砧上方
        double z = pos.getZ() + 0.5;

        // 创建物品实体
        ItemEntity displayEntity = new ItemEntity(world, x, y, z, stack.copy());

        // 设置为不会被拾取、不会合并、不会消失
        displayEntity.setPickupDelay(Integer.MAX_VALUE);
        displayEntity.setVelocity(Vec3d.ZERO);
        displayEntity.setNeverDespawn();
        displayEntity.setInvulnerable(true);

        world.spawnEntity(displayEntity);
        entities[slot] = displayEntity;
    }
    
    /**
     * 移除指定槽位的显示实体 - 新版本，使用AnvilState
     */
    private static void removeDisplayEntity(World world, BlockPos pos, AnvilState anvilState, int slot) {
        // 移除客户端检查，让客户端也能清理
        // if (world.isClient) return;

        if (anvilState.displayEntities[slot] != null) {
            if (!anvilState.displayEntities[slot].isRemoved()) {
                anvilState.displayEntities[slot].discard();
            }
            anvilState.displayEntities[slot] = null;
        }
    }

    /**
     * 清除所有显示实体 - 新版本，使用AnvilState
     */
    private static void clearAllDisplayEntities(World world, BlockPos pos, AnvilState anvilState) {
        // 移除客户端检查，让客户端也能清理
        // if (world != null && world.isClient) return;

        for (int i = 0; i < 2; i++) {
            if (anvilState.displayEntities[i] != null) {
                if (!anvilState.displayEntities[i].isRemoved()) {
                    anvilState.displayEntities[i].discard();
                }
                anvilState.displayEntities[i] = null;
            }
        }
    }

    /**
     * 移除指定槽位的显示实体 - 旧版本，向后兼容
     */
    @Deprecated
    private static void removeDisplayEntity(World world, BlockPos pos, int slot) {
        if (world.isClient) return;

        ItemEntity[] entities = ANVIL_DISPLAY_ENTITIES.get(pos);
        if (entities != null && entities[slot] != null) {
            if (!entities[slot].isRemoved()) {
                entities[slot].discard();
            }
            entities[slot] = null;
        }
    }

    /**
     * 清除所有显示实体 - 旧版本，向后兼容
     */
    @Deprecated
    private static void clearAllDisplayEntities(World world, BlockPos pos) {
        if (world.isClient) return;

        ItemEntity[] entities = ANVIL_DISPLAY_ENTITIES.remove(pos);
        if (entities != null) {
            for (ItemEntity entity : entities) {
                if (entity != null && !entity.isRemoved()) {
                    entity.discard();
                }
            }
        }
    }
    
    /**
     * 清除指定位置的铁砧数据 - 新版本，使用统一结构
     */
    public static void clearAnvilData(BlockPos pos) {
        AnvilState anvilState = ANVIL_STATES.get(pos);
        if (anvilState != null) {
            clearAllDisplayEntities(null, pos, anvilState); // 在服务端调用时world为null，但clear方法不依赖world
            anvilState.clear();
            ANVIL_STATES.remove(pos);
        }

        // 向后兼容：清理旧数据结构
        ANVIL_ITEMS.remove(pos);
        ANVIL_DISPLAY_ENTITIES.remove(pos);
    }

    /**
     * 清除指定位置的铁砧数据 - 旧版本，向后兼容
     */
    @Deprecated
    public static void clearAnvilDataOld(BlockPos pos) {
        ANVIL_ITEMS.remove(pos);
        ANVIL_DISPLAY_ENTITIES.remove(pos);
    }
}

