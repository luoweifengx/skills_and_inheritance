package luowei.fengxskillsandinter.screen;

import java.util.List;

import luowei.fengxskillsandinter.item.WandItem;
import luowei.fengxskillsandinter.util.ItemDataHelper;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.math.MathHelper;

/**
 * 符文台 GUI：紫色系面板；环形槽与右侧预览条均为加粗边框。
 */
public class RunicTableScreen extends HandledScreen<RunicTableScreenHandler> {

    /** 纵向渐变上下色（深蓝紫→近黑紫） */
    private static final int BG_GRADIENT_TOP = 0xEE2a2240;
    private static final int BG_GRADIENT_BOTTOM = 0xEE12101c;

    /** 顶栏高光、外框（淡藤紫）、内阴影线 */
    private static final int ACCENT_BAR = 0xFF9578d8;
    private static final int FRAME_OUTER = 0xFFbfb0e8;
    private static final int FRAME_INNER = 0xFF3f2f58;

    /** 输入槽：深紫描边，2px */
    private static final int INPUT_SLOT_OUTLINE = 0xFF4a3868;
    private static final int SLOT_BORDER_THICKNESS = 2;

    private static final int BAR_COL_X = 118;
    private static final int BAR_WIDTH = 34;
    private static final int BAR_HEIGHT = 4;
    private static final int BAR_ROW_GAP = 6;
    private static final int BAR_BLOCK_TOP = 16;
    private static final int BAR_BG = 0xFF3a3048;

    private static final int BAR_FRAME = 0xFF8a76b0;
    /** 预览条边框厚度（与槽位一致为 2px） */
    private static final int BAR_BORDER_THICKNESS = 2;

    private static final String[] ATTR_LABEL = { "容", "施", "充", "回", "法", "抽" };
    private static final int[] ATTR_FILL_COLORS = {
        0xFFD88ba0, 0xFFacb8ea, 0xFF82d8c8, 0xFFefc97a, 0xFF7ebdff, 0xFFaae8b8
    };

    private static final int BAR_LABEL_COLOR = 0xFFeae4f5;
    private static final int BAR_PCT_COLOR = 0xFFcfc3e8;

    /** 预览条满格：8×全 10 混标结果（手写验算，与 {@link luowei.fengxskillsandinter.util.GenerateWand#formula} 一致） */
    private static final double MAX_ATTRIBUTE_VALUE = 13.456087800176876;

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        renderBackground(context, mouseX, mouseY, delta);
        super.render(context, mouseX, mouseY, delta);
        drawMouseoverTooltip(context, mouseX, mouseY);
    }

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
        int w = this.backgroundWidth;
        int h = this.backgroundHeight;

        GuiPanelBackdrop.fillVerticalGradient(context, left, top, w, h, BG_GRADIENT_TOP, BG_GRADIENT_BOTTOM);
        GuiPanelBackdrop.drawTopAccentBar(context, left, top, w, 2, ACCENT_BAR);
        GuiPanelBackdrop.drawDoubleOutline(context, left, top, w, h, FRAME_OUTER, FRAME_INNER);
    }

    @Override
    protected void drawForeground(DrawContext context, int mouseX, int mouseY) {
        super.drawForeground(context, mouseX, mouseY);

        ItemStack result = this.handler.getSlot(8).getStack();
        drawAttributeBars(context, result);

        for (int i = 0; i < 8; i++) {
            double angleRad = Math.toRadians(-90.0 + i * 45.0);
            double cx = RunicTableScreenHandler.RING_CENTER_X
                + RunicTableScreenHandler.INPUT_RING_RADIUS * Math.cos(angleRad);
            double cy = RunicTableScreenHandler.RING_CENTER_Y
                + RunicTableScreenHandler.INPUT_RING_RADIUS * Math.sin(angleRad);
            int sx = (int) Math.round(cx - 8.0);
            int sy = (int) Math.round(cy - 8.0);
            GuiPanelBackdrop.drawRectOutlineThickness(context, sx, sy, 18, 18, INPUT_SLOT_OUTLINE, SLOT_BORDER_THICKNESS);
        }
    }

    private void drawAttributeBars(DrawContext context, ItemStack preview) {
        if (preview.isEmpty() || !(preview.getItem() instanceof WandItem)) {
            return;
        }
        if (!ItemDataHelper.contains(preview, WandItem.RAW_SCORE)) {
            return;
        }
        List<Double> raw = ItemDataHelper.getDoubleList(preview, WandItem.RAW_SCORE);
        if (raw.size() < 6) {
            return;
        }

        double maxBlend = MAX_ATTRIBUTE_VALUE;
        if (maxBlend <= 1e-9) {
            return;
        }

        int lx = BAR_COL_X - 14;
        for (int i = 0; i < 6; i++) {
            int rowY = BAR_BLOCK_TOP + i * (BAR_HEIGHT + BAR_ROW_GAP);
            context.drawText(
                this.textRenderer,
                ATTR_LABEL[i],
                lx,
                rowY + (BAR_HEIGHT - this.textRenderer.fontHeight) / 2,
                BAR_LABEL_COLOR,
                false
            );

            int bx = BAR_COL_X;
            int bw = BAR_WIDTH;
            int bh = BAR_HEIGHT;
            int t = BAR_BORDER_THICKNESS;
            context.fill(bx + t, rowY + t, bx + bw - t, rowY + bh - t, BAR_BG);

            double v = raw.get(i);
            double ratio = MathHelper.clamp(v / maxBlend, 0.0, 1.0);
            int innerW = bw - 2 * t;
            int fillW = (int) Math.round(ratio * innerW);
            if (fillW > 0) {
                context.fill(bx + t, rowY + t, bx + t + fillW, rowY + bh - t, ATTR_FILL_COLORS[i]);
            }
            GuiPanelBackdrop.drawRectOutlineThickness(context, bx, rowY, bw, bh, BAR_FRAME, t);

            int pctNum = (int) Math.round(Math.min(v / maxBlend * 100.0, 999.0));
            String pct = pctNum + "%";
            context.drawText(
                this.textRenderer,
                pct,
                bx + bw + 3,
                rowY + (BAR_HEIGHT - this.textRenderer.fontHeight) / 2,
                BAR_PCT_COLOR,
                false
            );
        }
    }
}
