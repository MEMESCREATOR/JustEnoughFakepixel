package com.jef.justenoughfakepixel.mixins;

import com.jef.justenoughfakepixel.features.storage.gui.CustomGuiChest;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.inventory.GuiChest;
import net.minecraft.inventory.ContainerChest;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Minecraft.class)
public class MixinMinecraft_CustomGuiChest {

    @Inject(method = "displayGuiScreen", at = @At("HEAD"), cancellable = true)
    public void displayGuiScreen(GuiScreen guiScreenIn, CallbackInfo ci) {
        // Replace GuiChest with CustomGuiChest
        if (guiScreenIn instanceof GuiChest && !(guiScreenIn instanceof CustomGuiChest)) {
            GuiChest originalGui = (GuiChest) guiScreenIn;

            if (originalGui.inventorySlots instanceof ContainerChest) {
                ContainerChest container = (ContainerChest) originalGui.inventorySlots;

                CustomGuiChest customGui = new CustomGuiChest(
                        container.getLowerChestInventory(),
                        Minecraft.getMinecraft().thePlayer.inventory
                );

                // Copy over the container
                customGui.inventorySlots = container;

                // Cancel the original call and display our custom GUI instead
                ci.cancel();
                ((Minecraft)(Object)this).displayGuiScreen(customGui);
            }
        }
    }
}