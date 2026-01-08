package dev.jack.autoominous;

import meteordevelopment.meteorclient.events.game.ReceiveToastEvent;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.settings.*;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.systems.modules.Modules;
import meteordevelopment.meteorclient.utils.player.ChatUtils;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.Items;
import net.minecraft.util.Hand;

public class AutoOminous extends Module {
    private final SettingGroup sgGeneral = settings.getDefaultGroup();
    private final Setting<Boolean> chatMessage = sgGeneral.add(
        new BoolSetting.Builder().name("chat-message").defaultValue(true).build()
    );

    private boolean raidPending = false;

    public AutoOminous() {
        super(Modules.CATEGORY, "auto-ominous",
            "Automatically drinks an Ominous Bottle after raid victory.");
    }

    @EventHandler
    private void onToast(ReceiveToastEvent event) {
        if (event.title != null &&
            event.title.getString().equalsIgnoreCase("Raid Victory")) {
            raidPending = true;
        }
    }

    @EventHandler
    private void onTick(TickEvent.Post event) {
        if (!raidPending || mc.player == null || mc.interactionManager == null) return;

        if (mc.player.hasStatusEffect(StatusEffects.OMINOUS)) {
            raidPending = false;
            return;
        }

        int prevSlot = mc.player.getInventory().selectedSlot;

        if (mc.player.getOffHandStack().getItem() == Items.OMINOUS_BOTTLE) {
            mc.interactionManager.interactItem(mc.player, Hand.OFF_HAND);
            raidPending = false;
            return;
        }

        for (int i = 0; i < 9; i++) {
            if (mc.player.getInventory().getStack(i).getItem() == Items.OMINOUS_BOTTLE) {
                mc.player.getInventory().selectedSlot = i;
                mc.interactionManager.interactItem(mc.player, Hand.MAIN_HAND);
                mc.player.getInventory().selectedSlot = prevSlot;
                raidPending = false;
                return;
            }
        }

        if (chatMessage.get()) {
            ChatUtils.warning("Raid completed, but no Ominous Bottle found.");
        }

        raidPending = false;
    }
}