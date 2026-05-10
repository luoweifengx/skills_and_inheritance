package luowei.fengxskillsandinter.screen;

import java.util.ArrayList;
import java.util.List;

import luowei.fengxskillsandinter.FengxSkillsAndInheritance;
import luowei.fengxskillsandinter.item.ModItems;
import luowei.fengxskillsandinter.util.GenerateWand;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.InventoryChangedListener;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.resource.featuretoggle.FeatureFlags;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.screen.slot.Slot;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

/**
 * 符文台：8 输入槽环形排列，中心为输出槽（handler 槽索引 8）；玩家背包在下方。
 * PANEL_* 需与 {@link RunicTableScreen} 背景尺寸一致。
 * <p>合成刷新仅在逻辑服务端执行，避免客户端与集成服双端重复计算；写结果槽时用相等判断避免多余同步。
 */

public class RunicTableScreenHandler extends ScreenHandler implements InventoryChangedListener{
    
    public static final int PANEL_WIDTH = 176;
    /** 含标题区、环形区、三行背包与快捷栏 */
    public static final int PANEL_HEIGHT = 184;

    /** 与 {@link RunicTableScreen} 绘制输入槽边框共用 */
    public static final int RING_CENTER_X = 88;
    public static final int RING_CENTER_Y = 36;
    public static final int INPUT_RING_RADIUS = 28;
    private static final int PLAYER_INV_TOP = 86;
    private static final int PLAYER_HOTBAR_TOP = PLAYER_INV_TOP + 54;

    public static ScreenHandlerType<RunicTableScreenHandler> RUNIC_TABLE_SCREEN_HANDLER;

    private final SimpleInventory tableInventory;
    private final SimpleInventory resultInventory;
    private final PlayerInventory playerInventory;
    /** 写入结果槽时避免重入刷新 */
    private boolean suppressResultUpdate;
    /** 取出成品批量扣原料时，避免每格触发一次 {@link #refreshCraftingResult()} */
    private boolean ignoreTableInventoryEvents;

    public static void registryScreen() {
        RUNIC_TABLE_SCREEN_HANDLER =
            Registry.register(
                Registries.SCREEN_HANDLER,
                Identifier.of(FengxSkillsAndInheritance.MOD_ID, "runic_table_screen_handler"),
                new ScreenHandlerType<>(RunicTableScreenHandler::new, FeatureFlags.VANILLA_FEATURES));
    }

    public RunicTableScreenHandler(int syncId, PlayerInventory playerInventory) {
        super(RUNIC_TABLE_SCREEN_HANDLER, syncId);
        this.playerInventory = playerInventory;
        this.tableInventory = new SimpleInventory(8);
        this.resultInventory = new SimpleInventory(1);
        for (int i = 0; i < 8; i++) {
            double angleRad = Math.toRadians(-90.0 + i * 45.0);
            double cx = RING_CENTER_X + INPUT_RING_RADIUS * Math.cos(angleRad);
            double cy = RING_CENTER_Y + INPUT_RING_RADIUS * Math.sin(angleRad);
            int slotX = (int) Math.round(cx - 8.0);
            int slotY = (int) Math.round(cy - 8.0);
            this.addSlot(new Slot(this.tableInventory, i, slotX, slotY));
        }
        this.tableInventory.addListener(this);
        this.addSlot(new Slot(this.resultInventory, 0, RING_CENTER_X - 8, RING_CENTER_Y - 8) {
            @Override
            public boolean canInsert(ItemStack stack) {
                return false;
            }

            @Override
            public void onTakeItem(PlayerEntity player, ItemStack stack) {
                super.onTakeItem(player, stack);
                if (player.getWorld().isClient()) {
                    return;
                }
                RunicTableScreenHandler.this.consumeInputsAndRefreshResult();
            }
        });
        

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 9; j++) {
                this.addSlot(new Slot(playerInventory, j + i * 9 + 9, 8 + j * 18, PLAYER_INV_TOP + i * 18));
            }
        }
        for (int i = 0; i < 9; i++) {
            this.addSlot(new Slot(playerInventory, i, 8 + i * 18, PLAYER_HOTBAR_TOP));
        }
    }

    private void setResultStack(ItemStack stack) {
        ItemStack current = this.resultInventory.getStack(0);
        if (ItemStack.areEqual(current, stack)) {
            return;
        }
        this.suppressResultUpdate = true;
        try {
            this.resultInventory.setStack(0, stack);
        } finally {
            this.suppressResultUpdate = false;
        }
    }

    private void refreshCraftingResult() {
        if (this.playerInventory.player.getWorld().isClient()) {
            return;
        }
        if (this.suppressResultUpdate) {
            return;
        }
        List<ItemStack> items = new ArrayList<>(8);
        for (int i = 0; i < 8; i++) {
            ItemStack s = this.tableInventory.getStack(i);
            if (s.isEmpty()) {
                this.setResultStack(ItemStack.EMPTY);
                return;
            }
            items.add(s);
        }
        ItemStack wand = new ItemStack(ModItems.WAND);
        GenerateWand.generateWand(wand, items);
        this.setResultStack(wand);
    }

    @Override
    public void onClosed(PlayerEntity player) {
        super.onClosed(player);
        if(player.getWorld().isClient()) {
            return;
        }
        for(int i = 0; i < 8; i++) {
            ItemStack stack = this.tableInventory.getStack(i);
            if(!stack.isEmpty()) {
                offerOrDropStack(player, stack);

            }
        }
    }
    
    private void offerOrDropStack(PlayerEntity player, ItemStack stack) {
        boolean bl = player.isRemoved() && player.getRemovalReason() != Entity.RemovalReason.CHANGED_DIMENSION;
		boolean bl2 = player instanceof ServerPlayerEntity serverPlayerEntity && serverPlayerEntity.isDisconnected();
		if (bl || bl2) {
			player.dropItem(stack, false);
		} else if (player instanceof ServerPlayerEntity) {
			player.getInventory().offerOrDrop(stack);
		}
    }

    @Override
    public void onInventoryChanged(Inventory inventory) {
        if (inventory != this.tableInventory) {
            return;
        }
        if (this.ignoreTableInventoryEvents) {
            return;
        }
        this.refreshCraftingResult();
    }

    /** 玩家拿起中心成品后：八格原料各耗 1，再刷新预览（与原版工作台结果槽语义一致）。 */
    private void consumeInputsAndRefreshResult() {
        this.ignoreTableInventoryEvents = true;
        try {
            for (int i = 0; i < 8; i++) {
                ItemStack in = this.tableInventory.getStack(i);
                if (!in.isEmpty()) {
                    in.decrement(1);
                    this.tableInventory.setStack(i, in.isEmpty() ? ItemStack.EMPTY : in);
                }
            }
        } finally {
            this.ignoreTableInventoryEvents = false;
        }
        this.refreshCraftingResult();
    }

    @Override
    public boolean canUse(PlayerEntity player) {
        return true;
    }

    @Override
    public ItemStack quickMove(PlayerEntity player, int slot) {
        ItemStack itemStack = ItemStack.EMPTY;
        Slot slot2 = this.slots.get(slot);
        if (slot2 != null && slot2.hasStack()) {
            ItemStack itemStack2 = slot2.getStack();
            itemStack = itemStack2.copy();
            if (slot < 9) {
                if (!this.insertItem(itemStack2, 9, 45, true)) {
                    return ItemStack.EMPTY;
                }
            } else if (!this.insertItem(itemStack2, 0, 8, false)) {
                return ItemStack.EMPTY;
            }

            if (itemStack2.isEmpty()) {
                slot2.setStackNoCallbacks(ItemStack.EMPTY);
            } else {
                slot2.markDirty();
            }

            if (itemStack2.getCount() == itemStack.getCount()) {
                return ItemStack.EMPTY;
            }

            slot2.onTakeItem(player, itemStack2);
        }

        return itemStack;
    }
}
