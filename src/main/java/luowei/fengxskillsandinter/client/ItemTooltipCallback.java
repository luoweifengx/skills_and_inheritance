package luowei.fengxskillsandinter.client;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import luowei.fengxskillsandinter.weapon.WeaponAttributes;
import luowei.fengxskillsandinter.weapon.WeaponAttributeHelper;
import luowei.fengxskillsandinter.item.ItemExperience;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 物品Tooltip回调
 * 为武器和锤子添加详细信息显示，并修改原版显示的属性数值
 */
public class ItemTooltipCallback {
    
    public static void register() {
        net.fabricmc.fabric.api.client.item.v1.ItemTooltipCallback.EVENT.register(
            (ItemStack stack, Item.TooltipContext context, TooltipType type, List<Text> lines) -> {
                // 如果是武器且有属性加成，修改原版显示的数值
                if (WeaponAttributeHelper.isWeapon(stack)) {
                    double damageModifier = WeaponAttributes.getDamageBonus(stack);
                    double speedModifier = WeaponAttributes.getSpeedBonus(stack);
                    
                    if (damageModifier != 0.0 || speedModifier != 0.0) {
                        modifyOriginalTooltipLines(lines, stack, damageModifier, speedModifier);
                    }
                }
                
                // 显示物品经验（新系统）
                if (WeaponAttributeHelper.isWeapon(stack) || WeaponAttributeHelper.isTool(stack)) {
                    int itemExp = ItemExperience.getItemExperience(stack);
                    if (itemExp > 0) {
                        lines.add(Text.literal("§7§m-------------------"));
                        lines.add(Text.literal(String.format("§b物品经验: §e%d", itemExp)));
                    lines.add(Text.literal("§7§m-------------------"));
                    }
                }
            }
        );
    }
    
    /**
     * 修改原版tooltip中显示的属性数值
     * 保持原版的样式（颜色、格式等）
     */
    private static void modifyOriginalTooltipLines(List<Text> lines, ItemStack stack, 
                                                   double damageModifier, double speedModifier) {
        double totalDamage = WeaponAttributeHelper.getTotalAttackDamage(stack);
        double totalSpeed = WeaponAttributeHelper.getTotalAttackSpeed(stack);
        
        for (int i = 0; i < lines.size(); i++) {
            Text originalLine = lines.get(i);
            String lineText = originalLine.getString();
            
            // 修改攻击伤害显示
            if (lineText.contains("攻击伤害") || lineText.contains("Attack Damage")) {
                Text modifiedLine = replaceValueInText(originalLine, totalDamage, 1, damageModifier);
                if (modifiedLine != null) {
                    lines.set(i, modifiedLine);
                }
            }
            
            // 修改攻击速度显示
            if (lineText.contains("攻击速度") || lineText.contains("Attack Speed")) {
                Text modifiedLine = replaceValueInText(originalLine, totalSpeed, 2, speedModifier);
                if (modifiedLine != null) {
                    lines.set(i, modifiedLine);
                }
            }
        }
    }
    
    /**
     * 在Text中替换数值，保持原版样式
     * @param originalText 原版Text对象
     * @param newValue 新的数值
     * @param decimals 小数位数（1或2）
     * @param modifier 加成值（用于判断是否需要颜色标记）
     * @return 修改后的Text对象，如果无法修改则返回null
     */
    private static Text replaceValueInText(Text originalText, double newValue, int decimals, double modifier) {
        String originalString = originalText.getString();
        
        // 使用正则表达式匹配数值（支持整数和小数）
        // 匹配格式：如 "6"、"6.0"、"1.6" 等
        Pattern numberPattern = Pattern.compile("\\d+\\.?\\d*");
        Matcher matcher = numberPattern.matcher(originalString);
        
        if (!matcher.find()) {
            return null; // 如果找不到数值，返回null保持原样
        }
        
        // 格式化新数值
        String newValueString = String.format("%." + decimals + "f", newValue);
        
        // 在整个Text的完整字符串中只替换第一个匹配的数值（避免重复）
        String modifiedString = originalString.replaceFirst(numberPattern.pattern(), newValueString);
        
        // 创建新的Text，保持原版的所有样式
        // 使用简单的方法：创建一个包含修改后字符串的Text，并应用原版的样式
        MutableText result = Text.literal(modifiedString);
        
        // 完全复制原版的样式（包括颜色、格式、字体等）
        result.setStyle(originalText.getStyle());
        
        return result;
    }
    
}

