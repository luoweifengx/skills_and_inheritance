package luowei.fengxskillsandinter.screen;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import luowei.fengxskillsandinter.FengxSkillsAndInheritance;
import luowei.fengxskillsandinter.item.SpellItem;
import luowei.fengxskillsandinter.item.WandItem;
import luowei.fengxskillsandinter.spell.SpellRegistry;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.Hand;

/**
 * 法杖法术槽界面：槽数由 {@link WandItem#getCapacity} 决定（上限 {@link #MAX_SPELL_SLOTS}），
 * 开局通过 {@link WandScreenOpenData} 同步到客户端，避免误用原版 {@code GENERIC_9X1}（固定 9 格）导致协议槽位数不一致。
 * 布局：法术格按每行最多 9 格分行，下方为原版式三行背包 + 快捷栏。
 */
public class WandScreenHandler extends ScreenHandler {

    public static final int MAX_SPELL_SLOTS = 45;
    public static final int PANEL_WIDTH = 176;

    /** 法术区左上角（与原版容器一致），{@link WandScreen} 描边共用 */
    public static final int SPELL_ORIGIN_X = 8;
    public static final int SPELL_ORIGIN_Y = 18;
    private static final int SLOT_STEP = 18;
    /** 法术区底与玩家背包顶之间的间距（接近原版） */
    private static final int GAP_SPELL_TO_PLAYER = 14;
    public static final int PLAYER_ROWS = 3;
    public static final int PLAYER_COLS = 9;
    public static final int HOTBAR_GAP_ABOVE = 4;
    private static final int BOTTOM_PADDING = 8;

    public static ExtendedScreenHandlerType<WandScreenHandler, WandScreenOpenData> WAND_SCREEN_HANDLER_TYPE;

    /**
     * 打开菜单时由服务端写入、客户端读出，保证双端 {@link #spellSlotCount} 一致。
     */
    public record WandScreenOpenData(int spellSlotCount, int handOrdinal) {
        public static final PacketCodec<RegistryByteBuf, WandScreenOpenData> PACKET_CODEC = PacketCodec.tuple(
            PacketCodecs.VAR_INT,
            WandScreenOpenData::spellSlotCount,
            PacketCodecs.VAR_INT,
            WandScreenOpenData::handOrdinal,
            WandScreenOpenData::new
        );
    }

    private final int spellSlotCount;
    private final Inventory inventory;
    private final ItemStack wandStack;
    private final Hand hand;
    /** 打开时写入的UUID，用于关闭时查找法杖（防止移动法杖导致刷物品） */
    private final String openId;

    public static void registryScreen() {
        WAND_SCREEN_HANDLER_TYPE =
            Registry.register(
                Registries.SCREEN_HANDLER,
                Identifier.of(FengxSkillsAndInheritance.MOD_ID, "wand_screen_handler"),
                new ExtendedScreenHandlerType<>(WandScreenHandler::new, WandScreenOpenData.PACKET_CODEC)
            );
    }

    /**
     * 由 {@link ExtendedScreenHandlerType} 在双端调用：{@code data} 来自网络同步（客户端）或与开局数据一致（服务端）。
     */
    public WandScreenHandler(int syncId, PlayerInventory playerInventory, WandScreenOpenData data) {
        super(WAND_SCREEN_HANDLER_TYPE, syncId);
        this.spellSlotCount = MathHelper.clamp(data.spellSlotCount(), 1, MAX_SPELL_SLOTS);
        Hand[] hands = Hand.values();
        int ho = Math.floorMod(data.handOrdinal(), hands.length);
        this.hand = hands[ho];
        this.wandStack = playerInventory.player.getStackInHand(this.hand);

        this.inventory = new SimpleInventory(this.spellSlotCount);

        if (!playerInventory.player.getWorld().isClient()) {
            this.openId = UUID.randomUUID().toString();
            if (!this.wandStack.isEmpty()) {
                WandItem.setWandUuid(this.wandStack, this.openId);
            }
        } else {
            this.openId = null;
        }

        for (int i = 0; i < this.spellSlotCount; i++) {
            int row = i / PLAYER_COLS;
            int col = i % PLAYER_COLS;
            int x = SPELL_ORIGIN_X + col * SLOT_STEP;
            int y = SPELL_ORIGIN_Y + row * SLOT_STEP;
            this.addSlot(new SpellSlot(this.inventory, i, x, y));
        }

        int playerInvTop = playerInventoryTopY(this.spellSlotCount);
        for (int i = 0; i < PLAYER_ROWS; i++) {
            for (int j = 0; j < PLAYER_COLS; j++) {
                this.addSlot(new Slot(playerInventory, j + i * PLAYER_COLS + PLAYER_COLS, SPELL_ORIGIN_X + j * SLOT_STEP, playerInvTop + i * SLOT_STEP));
            }
        }
        int hotbarTop = playerInvTop + PLAYER_ROWS * SLOT_STEP + HOTBAR_GAP_ABOVE;
        for (int i = 0; i < PLAYER_COLS; i++) {
            this.addSlot(new Slot(playerInventory, i, SPELL_ORIGIN_X + i * SLOT_STEP, hotbarTop));
        }

        loadSpellIntoInventory(this.wandStack);
    }

    public int getSpellSlotCount() {
        return this.spellSlotCount;
    }

    public static int spellRows(int spellSlots) {
        return (spellSlots + PLAYER_COLS - 1) / PLAYER_COLS;
    }

    public static int playerInventoryTopY(int spellSlots) {
        return SPELL_ORIGIN_Y + spellRows(spellSlots) * SLOT_STEP + GAP_SPELL_TO_PLAYER;
    }

    /** 面板总高度（法术区 + 间距 + 玩家三行 + 缝 + 快捷栏 + 底边距） */
    public static int panelHeight(int spellSlots) {
        int playerInvTop = playerInventoryTopY(spellSlots);
        int hotbarTop = playerInvTop + PLAYER_ROWS * SLOT_STEP + HOTBAR_GAP_ABOVE;
        return hotbarTop + SLOT_STEP + BOTTOM_PADDING;
    }

    private void loadSpellIntoInventory(ItemStack wandStack) {
        List<String> spells = WandItem.getSpells(wandStack);
        for (int i = 0; i < this.inventory.size(); i++) {
            String spellId = i < spells.size() ? spells.get(i) : "";

            if (spellId != null && !spellId.isEmpty()) {
                Item spellItem = SpellRegistry.getItem(spellId);
                if (spellItem != null) {
                    this.inventory.setStack(i, new ItemStack(spellItem));
                } else {
                    this.inventory.setStack(i, ItemStack.EMPTY);
                }
            } else {
                this.inventory.setStack(i, ItemStack.EMPTY);
            }
        }
    }

    private void saveSpellsToWand(ItemStack targetWand) {
        int cap = WandItem.getCapacity(targetWand);
        List<String> spells = new ArrayList<>(cap);
        for (int i = 0; i < cap; i++) {
            ItemStack stack = this.inventory.getStack(i);
            if (!stack.isEmpty() && stack.getItem() instanceof SpellItem spellItem) {
                spells.add(spellItem.getSpellId());
            } else {
                spells.add("");
            }
        }
        WandItem.setSpells(targetWand, spells);
    }

    private ItemStack findWandByUuid(PlayerEntity player, String uuid) {
        for (int i = 0; i < player.getInventory().size(); i++) {
            ItemStack stack = player.getInventory().getStack(i);
            if (WandItem.matchesWandUuid(stack, uuid)) {
                return stack;
            }
        }
        return ItemStack.EMPTY;
    }

    @Override
    public void onClosed(PlayerEntity player) {
        super.onClosed(player);
        if (this.openId != null && !player.getWorld().isClient()) {
            ItemStack target = findWandByUuid(player, this.openId);
            if (!target.isEmpty()) {
                saveSpellsToWand(target);
            }
        }
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
            int playerFirst = this.spellSlotCount;
            int playerEndExclusive = this.spellSlotCount + 36;
            if (slot < this.spellSlotCount) {
                if (!this.insertItem(itemStack2, playerFirst, playerEndExclusive, true)) {
                    return ItemStack.EMPTY;
                }
            } else if (!this.insertItem(itemStack2, 0, this.spellSlotCount, false)) {
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
