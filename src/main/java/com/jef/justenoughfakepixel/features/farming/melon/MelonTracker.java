package com.jef.justenoughfakepixel.features.farming.melon;

import com.jef.justenoughfakepixel.core.JefConfig;
import com.jef.justenoughfakepixel.init.RegisterEvents;
import com.jef.justenoughfakepixel.utils.chat.ChatUtils;
import com.jef.justenoughfakepixel.utils.data.SkyblockData;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;

@RegisterEvents
public class MelonTracker {

    /**
     * Fired by the Melon Dicer (Roll em' / RNGesus) when it drops Enchanted
     * Melon Blocks. This message is the sole authoritative source — the
     * Personal Compactor does NOT produce it, so every match is a real drop.
     *
     * *** VERIFY THE EXACT MESSAGE IN-GAME AND UPDATE IF NEEDED ***
     * Expected (based on Pumpkin Dicer analogue):
     *   "☘ MELON DICER! You found 2 Enchanted Melon Block!"
     */
    private static final Pattern DICER_ENC_MELON_BLOCK =
            Pattern.compile("MELON DICER! You found ([\\d,]+) Enchanted Melon Block[s]?!");

    private static int tickCounter = 0;

    // -----------------------------------------------------------------------

    public static boolean isEnabled() {
        return JefConfig.feature != null
                && JefConfig.feature.farming.melonTrackerConfig.melonTracker
                && MelonStats.getInstance().isTrackingEnabled();
    }

    private static boolean isActive() {
        return isEnabled()
                && SkyblockData.getCurrentLocation() == SkyblockData.Location.PRIVATE_ISLAND;
    }

    // -----------------------------------------------------------------------

    @SubscribeEvent
    public void onTick(TickEvent.ClientTickEvent event) {
        if (event.phase != Phase.END) return;
        if (!isActive()) return;

        ++tickCounter;
        if (tickCounter % 20 == 0) {
            MelonStats.getInstance().tickRates();
        }
    }

    @SubscribeEvent
    public void onChat(ClientChatReceivedEvent event) {
        if (!ChatUtils.isFromServer(event)) return;
        if (!isActive()) return;

        String msg = ChatUtils.clean(event);

        Matcher m = DICER_ENC_MELON_BLOCK.matcher(msg);
        if (m.find()) {
            long count = parseLong(m.group(1));
            if (count > 0) {
                MelonStats stats = MelonStats.getInstance();
                stats.getData().enchantedMelonBlocks += count;
                stats.save();
            }
        }
    }

    @SubscribeEvent
    public void onWorldUnload(WorldEvent.Unload event) {
        MelonStats.getInstance().onWorldChange();
    }

    // -----------------------------------------------------------------------

    private static long parseLong(String s) {
        try {
            return Long.parseLong(s.replace(",", ""));
        } catch (NumberFormatException e) {
            return 0L;
        }
    }
}