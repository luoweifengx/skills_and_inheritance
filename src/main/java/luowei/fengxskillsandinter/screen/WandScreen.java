package luowei.fengxskillsandinter.screen;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;

/**
 * 法杖法术界面：风格接近 {@link RunicTableScreen}（深色底 + 点阵外框），法术槽绘浅细边框。
 */
public class WandScreen extends HandledScreen<WandScreenHandler> {

    private static final int BG = 0xE8000000;
    private static final int DOT = 0xFF6A6A6A;
    private static final int DOT_STEP = 3;
    private static final int SPELL_SLOT_OUTLINE = 0x66C8C8C8;

    public WandScreen(WandScreenHandler handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
    }

    @Override
    protected void init() {
        int spellSlots = this.handler.getSpellSlotCount();
        this.backgroundWidth = WandScreenHandler.PANEL_WIDTH;
        this.backgroundHeight = WandScreenHandler.panelHeight(spellSlots);
        super.init();
    }

    @Override
    protected void drawBackground(DrawContext context, float delta, int mouseX, int mouseY) {
        int left = this.x;
        int top = this.y;
        int right = left + this.backgroundWidth;
        int bottom = top + this.backgroundHeight;

        context.fill(left, top, right, bottom, BG);
        drawDottedRect(context, left, top, right, bottom, DOT_STEP, DOT);
    }

    @Override
    protected void drawForeground(DrawContext context, int mouseX, int mouseY) {
        super.drawForeground(context, mouseX, mouseY);
        int spellSlots = this.handler.getSpellSlotCount();
        int cols = 9;
        /* 与 WandScreenHandler 中法术槽坐标一致：SPELL_ORIGIN_X/Y */
        final int ox = 8;
        final int oy = 18;
        for (int i = 0; i < spellSlots; i++) {
            int row = i / cols;
            int col = i % cols;
            int sx = ox + col * 18;
            int sy = oy + row * 18;
            drawThinRect(context, sx, sy, 18, 18, SPELL_SLOT_OUTLINE);
        }
    }

    private static void drawThinRect(DrawContext context, int x, int y, int w, int h, int color) {
        context.fill(x, y, x + w, y + 1, color);
        context.fill(x, y + h - 1, x + w, y + h, color);
        context.fill(x, y, x + 1, y + h, color);
        context.fill(x + w - 1, y, x + w, y + h, color);
    }

    private static void drawDottedRect(DrawContext context, int left, int top, int right, int bottom, int step, int color) {
        for (int x = left; x < right; x += step) {
            context.fill(x, top, x + 1, top + 1, color);
            context.fill(x, bottom - 1, x + 1, bottom, color);
        }
        for (int y = top; y < bottom; y += step) {
            context.fill(left, y, left + 1, y + 1, color);
            context.fill(right - 1, y, right, y + 1, color);
        }
    }
}
