package com.jef.justenoughfakepixel.features.storage.gui;

import com.jef.justenoughfakepixel.features.storage.StorageManager;
import net.minecraft.client.gui.inventory.GuiChest;
import net.minecraft.inventory.IInventory;

import java.util.List;

public class CustomGuiChest extends GuiChest {

    public CustomGuiChest(IInventory upperInv, IInventory lowerInv) {
        super(upperInv, lowerInv);
    }

    @Override
    protected void drawHoveringText(List<String> textLines, int x, int y, net.minecraft.client.gui.FontRenderer font) {
        if (StorageManager.isOverlayActive()) {
            return;
        }
        super.drawHoveringText(textLines, x, y, font);
    }

    @Override
    protected void drawHoveringText(List<String> textLines, int x, int y) {
        if (StorageManager.isOverlayActive()) {
            return;
        }
        super.drawHoveringText(textLines, x, y);
    }

}