package net.vulkanmod.mixin.render.block;

import net.minecraft.client.model.geom.builders.UVPair;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.Direction;
import net.vulkanmod.render.chunk.build.frapi.helper.NormalHelper;
import net.vulkanmod.render.chunk.cull.QuadFacing;
import net.vulkanmod.render.model.quad.ModelQuadFlags;
import net.vulkanmod.render.model.quad.ModelQuadView;
import org.joml.Vector3fc;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BakedQuad.class)
public class BakedQuadM implements ModelQuadView {

    @Shadow @Final private Vector3fc position0;
    @Shadow @Final private Vector3fc position1;
    @Shadow @Final private Vector3fc position2;
    @Shadow @Final private Vector3fc position3;
    @Shadow @Final private long packedUV0;
    @Shadow @Final private long packedUV1;
    @Shadow @Final private long packedUV2;
    @Shadow @Final private long packedUV3;
    @Shadow @Final private int tintIndex;
    @Shadow @Final private Direction direction;

    private int flags;
    private int normal;
    private QuadFacing facing;

    @Inject(method = "<init>", at = @At("RETURN"))
    private void onInit(
            Vector3fc pos0,
            Vector3fc pos1,
            Vector3fc pos2,
            Vector3fc pos3,
            long uv0,
            long uv1,
            long uv2,
            long uv3,
            int tintIndex,
            Direction face,
            TextureAtlasSprite sprite,
            boolean shade,
            int lightEmission,
            CallbackInfo ci) {
        this.flags = ModelQuadFlags.getQuadFlags(this, face);
        int packedNormal = NormalHelper.computePackedNormal(this);
        this.normal = packedNormal;
        this.facing = QuadFacing.fromNormal(packedNormal);
    }

    private Vector3fc getPosition(int idx) {
        return switch (idx) {
            case 0 -> this.position0;
            case 1 -> this.position1;
            case 2 -> this.position2;
            case 3 -> this.position3;
            default -> throw new IllegalArgumentException("Invalid vertex index: " + idx);
        };
    }

    private long getPackedUV(int idx) {
        return switch (idx) {
            case 0 -> this.packedUV0;
            case 1 -> this.packedUV1;
            case 2 -> this.packedUV2;
            case 3 -> this.packedUV3;
            default -> throw new IllegalArgumentException("Invalid vertex index: " + idx);
        };
    }

    @Override
    public int getFlags() {
        return flags;
    }

    @Override
    public float getX(int idx) {
        return getPosition(idx).x();
    }

    @Override
    public float getY(int idx) {
        return getPosition(idx).y();
    }

    @Override
    public float getZ(int idx) {
        return getPosition(idx).z();
    }

    @Override
    public int getColor(int idx) {
        // BakedQuad no longer stores per-vertex color; return white (no tint)
        return -1;
    }

    @Override
    public float getU(int idx) {
        return UVPair.unpackU(getPackedUV(idx));
    }

    @Override
    public float getV(int idx) {
        return UVPair.unpackV(getPackedUV(idx));
    }

    @Override
    public int getColorIndex() {
        return this.tintIndex;
    }

    @Override
    public Direction lightFace() {
        return this.direction;
    }

    @Override
    public Direction getFacingDirection() {
        return this.direction;
    }

    @Override
    public QuadFacing getQuadFacing() {
        return this.facing;
    }

    @Override
    public int getNormal() {
        return this.normal;
    }

    @Override
    public boolean isTinted() {
        return this.tintIndex != -1;
    }
}
