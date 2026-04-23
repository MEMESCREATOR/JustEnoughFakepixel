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
        // Replace GuiChest with our CustomGuiChest
        if (guiScreenIn instanceof GuiChest && !(guiScreenIn instanceof CustomGuiChest)) {
            GuiChest originalGui = (GuiChest) guiScreenIn;

            // Only replace if it's actually a chest container, not other container types
            // This prevents crashes when other mods assume GuiChest always has ContainerChest
            if (originalGui.inventorySlots instanceof ContainerChest) {
                try {
                    ContainerChest container = (ContainerChest) originalGui.inventorySlots;

                    if (container.getLowerChestInventory() != null &&
                            Minecraft.getMinecraft().thePlayer != null &&
                            Minecraft.getMinecraft().thePlayer.inventory != null) {

                        CustomGuiChest customGui = new CustomGuiChest(
                                container.getLowerChestInventory(),
                                Minecraft.getMinecraft().thePlayer.inventory
                        );

                        customGui.inventorySlots = container;

                        ci.cancel();
                        ((Minecraft)(Object)this).displayGuiScreen(customGui);
                    }
                } catch (Exception e) {
                    // If anything goes wrong, let the original GUI proceed
                    System.err.println("[JEF] Failed to create CustomGuiChest: " + e.getMessage());
                }
            }
        }
    }
}