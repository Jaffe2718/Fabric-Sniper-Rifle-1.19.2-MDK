package github.jaffe2718.sniperrifle.client;

import github.jaffe2718.sniperrifle.SniperRifle;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.minecraft.client.render.entity.FlyingItemEntityRenderer;

/**
 * 用于注册子弹实体的渲染*/
@Environment(EnvType.CLIENT)
public class SniperRifleClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        EntityRendererRegistry.register(SniperRifle.BULLET, FlyingItemEntityRenderer::new);
    }
}
