package luowei.fengxskillsandinter.item;

import net.minecraft.item.Item;

public class SpellItem extends Item {

    private final String spellId;

    public SpellItem(Settings settings, String spellId) {
        super(settings);
        this.spellId = spellId;
    }

    public String getSpellId() {
        return spellId;
    }
}
