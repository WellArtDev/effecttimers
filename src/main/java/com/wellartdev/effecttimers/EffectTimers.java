package com.wellartdev.effecttimers;

import java.util.ArrayList;
import java.util.List;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.effect.MobEffectInstance;

/**
 * EffectTimers: tidy HUD countdown timers for every active status effect.
 *
 * Every 10 ticks the mod collects the player's active effects and turns them
 * into text lines like "Speed 1:04". These lines are drawn top-left on the HUD
 * so the player never has to open the inventory to check remaining durations.
 * Timers under 10 seconds render amber instead of white as a low-time warning.
 *
 * Client-side only. No mixins, no networking.
 */
public class EffectTimers implements ClientModInitializer {
	public static final String MOD_ID = "effecttimers";

	/** Update interval in client ticks (10 ticks = twice per second). */
	private static final int UPDATE_INTERVAL_TICKS = 10;
	/** Remaining duration (in seconds) below which a timer turns amber. */
	private static final int LOW_TIME_THRESHOLD_SECONDS = 10;

	private static final int COLOR_NORMAL = 0xFFFFFF;
	private static final int COLOR_LOW_TIME = 0xFFAA00;

	/** Cached rendered lines, rebuilt every UPDATE_INTERVAL_TICKS ticks. */
	private final List<TimerLine> lines = new ArrayList<>();
	private int tickCounter = 0;

	@Override
	public void onInitializeClient() {
		ClientTickEvents.END_CLIENT_TICK.register(this::onClientTick);

		Identifier layerId = Identifier.fromNamespaceAndPath(MOD_ID, "effect_timers");
		HudElementRegistry.addLast(layerId, (extractor, deltaTracker) -> {
			if (lines.isEmpty()) {
				return;
			}
			Minecraft mc = Minecraft.getInstance();
			int x = 6;
			int y = 6;
			for (TimerLine line : lines) {
				extractor.text(mc.font, line.text(), x, y, line.color());
				y += 10;
			}
		});
	}

	/** Rebuild the timer lines on a fixed interval, clearing when not in a world. */
	private void onClientTick(Minecraft mc) {
		if (mc.player == null) {
			lines.clear();
			tickCounter = 0;
			return;
		}

		tickCounter++;
		if (tickCounter < UPDATE_INTERVAL_TICKS) {
			return;
		}
		tickCounter = 0;

		lines.clear();
		for (MobEffectInstance inst : mc.player.getActiveEffects()) {
			String name = Component.translatable(inst.getDescriptionId()).getString();
			int ticks = inst.getDuration();
			String time = formatDuration(ticks);
			int seconds = ticks / 20;
			int color = seconds < LOW_TIME_THRESHOLD_SECONDS ? COLOR_LOW_TIME : COLOR_NORMAL;
			lines.add(new TimerLine(name + " " + time, color));
		}
	}

	/**
	 * Format a tick duration as m:ss (e.g. "2:05") or h:mm:ss for durations of
	 * one hour or more. A zero or negative duration never occurs in practice,
	 * but is rendered as "0:00".
	 */
	private static String formatDuration(int ticks) {
		int totalSeconds = Math.max(ticks, 0) / 20;
		int hours = totalSeconds / 3600;
		int minutes = (totalSeconds % 3600) / 60;
		int seconds = totalSeconds % 60;
		if (hours > 0) {
			return hours + ":" + String.format("%02d", minutes) + ":" + String.format("%02d", seconds);
		}
		return minutes + ":" + String.format("%02d", seconds);
	}

	/** One rendered HUD line: its text and its color. */
	private record TimerLine(String text, int color) {
	}
}
