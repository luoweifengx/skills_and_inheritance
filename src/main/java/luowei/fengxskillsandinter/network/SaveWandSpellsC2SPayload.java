package luowei.fengxskillsandinter.network;

import luowei.fengxskillsandinter.FengxSkillsAndInheritance;
import net.minecraft.item.ItemStack;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

public record SaveWandSpellsC2SPayload() implements CustomPayload {

    public static final CustomPayload.Id<SaveWandSpellsC2SPayload> ID = 
        new CustomPayload.Id<>(Identifier.of(
            FengxSkillsAndInheritance.MOD_ID,
            "save_wand_spells"));
    
    // public static final PacketCodec<RegistryByteBuf, SaveWandSpellsC2SPayload> CODEC =
    //     PacketCodec.tuple(ItemStack.CODEC);

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}
