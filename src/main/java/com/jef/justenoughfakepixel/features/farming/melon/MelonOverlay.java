package com.jef.justenoughfakepixel.features.farming.melon;

import com.jef.justenoughfakepixel.core.JefConfig;
import com.jef.justenoughfakepixel.core.config.editors.ChromaColour;
import com.jef.justenoughfakepixel.core.config.utils.Position;
import com.jef.justenoughfakepixel.init.RegisterEvents;
import com.jef.justenoughfakepixel.utils.data.SkyblockData;
import com.jef.justenoughfakepixel.utils.overlay.Overlay;
import java.util.ArrayList;
import java.util.List;

@RegisterEvents
public class MelonOverlay extends Overlay {

    /** Coin value of one Enchanted Melon Block. */
    private static final long BLOCK_COIN_VALUE = 51_200L;

    private static MelonOverlay instance;

    public MelonOverlay() {
        super(200, 20);
        instance = this;
    }

    // -----------------------------------------------------------------------
    // Overlay base-class wiring
    // -----------------------------------------------------------------------

    @Override
    public Position getPosition() {
        return JefConfig.feature.farming.melonTrackerConfig.melonOverlayPos;
    }

    @Override
    public float getScale() {
        return JefConfig.feature.farming.melonTrackerConfig.melonOverlayScale;
    }

    @Override
    public int getBgColor() {
        return ChromaColour.specialToChromaRGB(
                JefConfig.feature.farming.melonTrackerConfig.melonBgColor);
    }

    @Override
    public int getCornerRadius() {
        return JefConfig.feature.farming.melonTrackerConfig.melonCornerRadius;
    }

    @Override
    protected int getBaseWidth() {
        return 200;
    }

    @Override
    protected boolean isEnabled() {
        return JefConfig.feature.farming.melonTrackerConfig.melonTracker
                && MelonStats.getInstance().isTrackingEnabled()
                && SkyblockData.getCurrentLocation() == SkyblockData.Location.PRIVATE_ISLAND;
    }

    // -----------------------------------------------------------------------
    // Line rendering
    // -----------------------------------------------------------------------

    private String lineForEntry(int ordinal, MelonData d, MelonStats stats, boolean preview) {
        switch (ordinal) {
            case 0: {
                // Header
                String paused = (!preview && !MelonStats.getInstance().isTrackingEnabled())
                        ? " §7[Paused]" : "";
                return "§a§lMelon Tracker" + paused;
            }
            case 1: {
                // Enchanted Melon Blocks + blocks/h
                long blocks   = preview ? 7L    : d.enchantedMelonBlocks;
                String bph    = preview ? "3.5" : MelonStats.fmtRate(stats.blockInfo.perHour);
                return String.format("§a%s §7Enc. Melon Block%s §7(%s/h)",
                        MelonStats.fmtNum(blocks),
                        blocks == 1L ? "" : "s",
                        bph);
            }
            case 2: {
                // Money/h  =  blocks/h × 51200
                double moneyPerHour = preview
                        ? 3.5 * BLOCK_COIN_VALUE
                        : stats.blockInfo.perHour * BLOCK_COIN_VALUE;
                return String.format("§6%s coins/h", MelonStats.fmtRate(moneyPerHour));
            }
            default:
                return null;
        }
    }

    @Override
    public List<String> getLines(boolean preview) {
        List<String> lines = new ArrayList<>();
        MelonStats stats = MelonStats.getInstance();
        MelonData d = stats.getData();

        for (Object entry : JefConfig.feature.farming.melonTrackerConfig.melonDisplayLines) {
            int ordinal = entry instanceof Number ? ((Number) entry).intValue() : -1;
            String line = lineForEntry(ordinal, d, stats, preview);
            if (line != null) {
                lines.add(line);
            }
        }

        return lines;
    }

    // -----------------------------------------------------------------------

    public static MelonOverlay getInstance() {
        return instance;
    }
}