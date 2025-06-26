package dev.aperso.entice.filter;

import com.mojang.blaze3d.platform.Window;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.PostChain;
import net.minecraft.resources.ResourceLocation;

import java.io.IOException;

public class SobelFilter {
    public static PostChain POST_CHAIN;

    public static void onReloadShaders() throws IOException {
        Minecraft minecraft = Minecraft.getInstance();
        if (POST_CHAIN != null) POST_CHAIN.close();
        Window window = Minecraft.getInstance().getWindow();
        POST_CHAIN = new PostChain(
            minecraft.getTextureManager(),
            minecraft.getResourceManager(),
            minecraft.getMainRenderTarget(),
            ResourceLocation.withDefaultNamespace("shaders/post/filter/sobel.json")
        );
        POST_CHAIN.resize(window.getWidth(), window.getHeight());
    }
}
