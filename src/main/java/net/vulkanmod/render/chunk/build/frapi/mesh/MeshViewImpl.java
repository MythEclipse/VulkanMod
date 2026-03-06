package net.vulkanmod.render.chunk.build.frapi.mesh;

/* JADX INFO: loaded from: VulkanMod_1.21.11-0.6.0.jar:net/vulkanmod/render/chunk/build/frapi/mesh/MeshViewImpl.class */
public class MeshViewImpl implements net.fabricmc.fabric.api.renderer.v1.mesh.MeshView {
    private static final java.lang.ThreadLocal<
                    it.unimi.dsi.fastutil.objects.ObjectArrayList<
                            net.vulkanmod.render.chunk.build.frapi.mesh.QuadViewImpl>>
            CURSOR_POOLS =
                    java.lang.ThreadLocal.withInitial(
                            it.unimi.dsi.fastutil.objects.ObjectArrayList::new);
    int[] data;
    int limit;

    MeshViewImpl() {}

    @Override // net.fabricmc.fabric.api.renderer.v1.mesh.MeshView
    public int size() {
        return this.limit / net.vulkanmod.render.chunk.build.frapi.mesh.EncodingFormat.TOTAL_STRIDE;
    }

    @Override // net.fabricmc.fabric.api.renderer.v1.mesh.MeshView
    public void forEach(
            java.util.function.Consumer<? super net.fabricmc.fabric.api.renderer.v1.mesh.QuadView>
                    action) {
        net.vulkanmod.render.chunk.build.frapi.mesh.QuadViewImpl cursor;
        it.unimi.dsi.fastutil.objects.ObjectArrayList<
                        net.vulkanmod.render.chunk.build.frapi.mesh.QuadViewImpl>
                pool = CURSOR_POOLS.get();
        if (pool.isEmpty()) {
            cursor = new net.vulkanmod.render.chunk.build.frapi.mesh.QuadViewImpl();
        } else {
            cursor = (net.vulkanmod.render.chunk.build.frapi.mesh.QuadViewImpl) pool.pop();
        }
        forEach(action, cursor);
        pool.push(cursor);
    }

    /* JADX WARN: Multi-variable type inference failed */
    <C extends net.vulkanmod.render.chunk.build.frapi.mesh.QuadViewImpl> void forEach(
            java.util.function.Consumer<? super C> action, C cursor) {
        int limit = this.limit;
        cursor.data = this.data;
        for (int index = 0;
                index < limit;
                index += net.vulkanmod.render.chunk.build.frapi.mesh.EncodingFormat.TOTAL_STRIDE) {
            cursor.baseIndex = index;
            cursor.load();
            action.accept(cursor);
        }
        cursor.data = null;
    }

    @Override // net.fabricmc.fabric.api.renderer.v1.mesh.MeshView
    public void outputTo(net.fabricmc.fabric.api.renderer.v1.mesh.QuadEmitter emitter) {
        net.vulkanmod.render.chunk.build.frapi.mesh.MutableQuadViewImpl e =
                (net.vulkanmod.render.chunk.build.frapi.mesh.MutableQuadViewImpl) emitter;
        int[] data = this.data;
        int limit = this.limit;
        int i = 0;
        while (true) {
            int index = i;
            if (index < limit) {
                java.lang.System.arraycopy(
                        data,
                        index,
                        e.data,
                        e.baseIndex,
                        net.vulkanmod.render.chunk.build.frapi.mesh.EncodingFormat.TOTAL_STRIDE);
                e.load();
                e.transformAndEmit();
                i = index + net.vulkanmod.render.chunk.build.frapi.mesh.EncodingFormat.TOTAL_STRIDE;
            } else {
                e.clear();
                return;
            }
        }
    }
}
