package luowei.fengxskillsandinter.screen;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;

/**
 * 符文台 GUI：纯黑底 + 点状描边（与 mutil 思路类似：多次 {@link DrawContext#fill}）。
 */
public class RunicTableScreen extends HandledScreen<RunicTableScreenHandler> {

    private static final int BG = 0xE8000000;
    private static final int DOT = 0xFF6A6A6A;
    private static final int DOT_STEP = 3;
    /** 输入槽 18×18 内沿浅细边框（ARGB） */
    private static final int INPUT_SLOT_OUTLINE = 0x66C8C8C8;

    public RunicTableScreen(RunicTableScreenHandler handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
    }

    @Override
    protected void init() {
        this.backgroundWidth = RunicTableScreenHandler.PANEL_WIDTH;
        this.backgroundHeight = RunicTableScreenHandler.PANEL_HEIGHT;
        super.init();
    }

    @Override
    protected void drawBackground(DrawContext context, float delta, int mouseX, int mouseY) {
        int left = this.x;
        int top = this.y;
        int right = left + this.backgroundWidth;
        int bottom = top + this.backgroundHeight;

        // 必须用屏幕坐标：drawBackground 传入的 DrawContext 未平移到面板原点
        context.fill(left, top, right, bottom, BG);
        drawDottedRect(context, left, top, right, bottom, DOT_STEP, DOT);
    }

    @Override
    protected void drawForeground(DrawContext context, int mouseX, int mouseY) {
        super.drawForeground(context, mouseX, mouseY);
        // 面板内坐标（与 ScreenHandler 中 addSlot 一致）
        for (int i = 0; i < 8; i++) {
            double angleRad = Math.toRadians(-90.0 + i * 45.0);
            double cx = RunicTableScreenHandler.RING_CENTER_X
                + RunicTableScreenHandler.INPUT_RING_RADIUS * Math.cos(angleRad);
            double cy = RunicTableScreenHandler.RING_CENTER_Y
                + RunicTableScreenHandler.INPUT_RING_RADIUS * Math.sin(angleRad);
            int sx = (int) Math.round(cx - 8.0);
            int sy = (int) Math.round(cy - 8.0);
            drawThinRect(context, sx, sy, 18, 18, INPUT_SLOT_OUTLINE);
        }
    }

    /** 1 像素宽的矩形描边（浅线） */
    private static void drawThinRect(DrawContext context, int x, int y, int w, int h, int color) {
        context.fill(x, y, x + w, y + 1, color);
        context.fill(x, y + h - 1, x + w, y + h, color);
        context.fill(x, y, x + 1, y + h, color);
        context.fill(x + w - 1, y, x + w, y + h, color);
    }

    /**
     * 用 1×1 像素块沿边每隔 step 画一点，形成虚线框。
     */
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
