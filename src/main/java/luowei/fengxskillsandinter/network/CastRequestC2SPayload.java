package luowei.fengxskillsandinter.network;

import luowei.fengxskillsandinter.FengxSkillsAndInheritance;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

public record CastRequestC2SPayload() implements CustomPayload {

    public static final CustomPayload.Id<CastRequestC2SPayload> ID = 
        new CustomPayload.Id<>(Identifier.of(
            FengxSkillsAndInheritance.MOD_ID,
            "cast_request"));

    public static final PacketCodec<RegistryByteBuf, CastRequestC2SPayload> CODEC =
        PacketCodec.unit(new CastRequestC2SPayload());

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}
