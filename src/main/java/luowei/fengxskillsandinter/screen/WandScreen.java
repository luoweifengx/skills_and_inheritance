package luowei.fengxskillsandinter.screen;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;

/**
 * 法杖法术界面：法术区与背包区分开描边；背包行间细分隔 + 快捷栏分隔。
 */
public class WandScreen extends HandledScreen<WandScreenHandler> {

    /** 底板更浅；外框更深，与内底对比更强 */
    private static final int BG_GRADIENT_TOP = 0xEE75624a;
    private static final int BG_GRADIENT_BOTTOM = 0xEE443218;

    private static final int ACCENT_BAR = 0xFFa07040;
    private static final int FRAME_OUTER = 0xFF4a3018;
    private static final int FRAME_INNER = 0xFF140c06;

    /** 法术槽描边 */
    private static final int SPELL_SLOT_OUTLINE = 0xFF7a4820;
    private static final int SLOT_BORDER_THICKNESS = 1;

    private static final int PLAYER_INV_TITLE_ABOVE_FIRST_ROW = 11;

    private static final int SECTION_DIVIDER_SPELL_PLAYER = 0xDDa07040;
    private static final int SPELL_ZONE_FRAME = 0x994a3018;
    private static final int PLAYER_ZONE_FRAME = 0x99381808;
    private static final int PLAYER_ROW_DIVIDER = 0x55301808;
    private static final int PLAYER_HOTBAR_DIVIDER = 0x88a07040;

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        renderBackground(context, mouseX, mouseY, delta);
        super.render(context, mouseX, mouseY, delta);
        drawMouseoverTooltip(context, mouseX, mouseY);
    }

    public WandScreen(WandScreenHandler handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
    }

    @Override
    protected void init() {
        int spellSlots = this.handler.getSpellSlotCount();
        this.backgroundWidth = WandScreenHandler.PANEL_WIDTH;
        this.backgroundHeight = WandScreenHandler.panelHeight(spellSlots);
        super.init();
        this.playerInventoryTitleX = WandScreenHandler.SPELL_ORIGIN_X;
        this.playerInventoryTitleY = WandScreenHandler.playerInventoryTopY(spellSlots) - PLAYER_INV_TITLE_ABOVE_FIRST_ROW;
    }

    @Override
    protected void drawBackground(DrawContext context, float delta, int mouseX, int mouseY) {
        int left = this.x;
        int top = this.y;
        int w = this.backgroundWidth;
        int h = this.backgroundHeight;

        GuiPanelBackdrop.fillVerticalGradient(context, left, top, w, h, BG_GRADIENT_TOP, BG_GRADIENT_BOTTOM);
        GuiPanelBackdrop.drawTopAccentBar(context, left, top, w, 2, ACCENT_BAR);
        GuiPanelBackdrop.drawDoubleOutline(context, left, top, w, h, FRAME_OUTER, FRAME_INNER);

        int spellSlots = this.handler.getSpellSlotCount();
        int spellRows = WandScreenHandler.spellRows(spellSlots);
        int spellH = spellRows * 18 + 8;
        GuiPanelBackdrop.drawThinOutline(
            context,
            left + 7,
            top + WandScreenHandler.SPELL_ORIGIN_Y - 4,
            w - 14,
            spellH,
            SPELL_ZONE_FRAME);

        int divY = top + WandScreenHandler.playerInventoryTopY(spellSlots) - 6;
        context.fill(left + 10, divY, left + w - 10, divY + 1, SECTION_DIVIDER_SPELL_PLAYER);

        int invTopRel = WandScreenHandler.playerInventoryTopY(spellSlots);
        int hotbarTopRel = invTopRel + WandScreenHandler.PLAYER_ROWS * 18 + WandScreenHandler.HOTBAR_GAP_ABOVE;
        int invBot = top + hotbarTopRel + 18;
        GuiPanelBackdrop.drawThinOutline(context, left + 7, top + invTopRel - 4, w - 14, invBot - (top + invTopRel) + 8, PLAYER_ZONE_FRAME);

        GuiPanelBackdrop.drawPlayerInventorySectionLines(
            context,
            left,
            top,
            WandScreenHandler.SPELL_ORIGIN_X,
            invTopRel,
            WandScreenHandler.PLAYER_COLS,
            18,
            WandScreenHandler.PLAYER_ROWS,
            WandScreenHandler.HOTBAR_GAP_ABOVE,
            PLAYER_ROW_DIVIDER,
            PLAYER_HOTBAR_DIVIDER);
    }

    @Override
    protected void drawForeground(DrawContext context, int mouseX, int mouseY) {
        super.drawForeground(context, mouseX, mouseY);
        int spellSlots = this.handler.getSpellSlotCount();
        int cols = 9;
        final int ox = 8;
        final int oy = 18;
        for (int i = 0; i < spellSlots; i++) {
            int row = i / cols;
            int col = i % cols;
            int sx = ox + col * 18;
            int sy = oy + row * 18;
            GuiPanelBackdrop.drawRectOutlineThickness(context, sx, sy, 18, 18, SPELL_SLOT_OUTLINE, SLOT_BORDER_THICKNESS);
        }
    }
}
