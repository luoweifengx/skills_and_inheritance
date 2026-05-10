package luowei.fengxskillsandinter.screen;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;

/**
 * 法杖法术界面：棕色古朴系底板（内浅边框深）；法术槽加粗描边。
 */
public class WandScreen extends HandledScreen<WandScreenHandler> {

    /** 底板更浅；外框更深，与内底对比更强 */
    private static final int BG_GRADIENT_TOP = 0xEE75624a;
    private static final int BG_GRADIENT_BOTTOM = 0xEE443218;

    private static final int ACCENT_BAR = 0xFFa07040;
    private static final int FRAME_OUTER = 0xFF4a3018;
    private static final int FRAME_INNER = 0xFF140c06;

    /** 法术槽：不透明显色，2px 描边 */
    private static final int SPELL_SLOT_OUTLINE = 0xFF5c3818;
    private static final int SLOT_BORDER_THICKNESS = 2;

    private static final int PLAYER_INV_TITLE_ABOVE_FIRST_ROW = 11;

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
