package me.pugabyte.bearnation.api.utils;

import lombok.Builder;
import lombok.Getter;
import me.pugabyte.bearnation.api.utils.Tasks.Countdown.CountdownBuilder;
import me.pugabyte.bearnation.api.utils.Tasks.GlowTask.GlowTaskBuilder;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitScheduler;
import org.inventivetalent.glow.GlowAPI;

import java.util.List;
import java.util.function.Consumer;

// TODO Save all tasks in a map?
public class Tasks {
	private static final BukkitScheduler scheduler = Bukkit.getScheduler();
	private final Plugin plugin;

	public Tasks(Plugin plugin) {
		this.plugin = plugin;
	}

	public int wait(Time delay, Runnable runnable) {
		return wait(delay.get(), runnable);
	}

	public int wait(long delay, Runnable runnable) {
		if (plugin.isEnabled())
			return scheduler.runTaskLater(plugin, runnable, delay).getTaskId();
		plugin.getLogger().info("Attempted to register wait task while disabled");
		return -1;
	}

	public int repeat(Time startDelay, long interval, Runnable runnable) {
		return repeat(startDelay.get(), interval, runnable);
	}

	public int repeat(long startDelay, Time interval, Runnable runnable) {
		return repeat(startDelay, interval.get(), runnable);
	}

	public int repeat(Time startDelay, Time interval, Runnable runnable) {
		return repeat(startDelay.get(), interval.get(), runnable);
	}

	public int repeat(long startDelay, long interval, Runnable runnable) {
		if (plugin.isEnabled())
			return scheduler.scheduleSyncRepeatingTask(plugin, runnable, startDelay, interval);
		plugin.getLogger().info("Attempted to register repeat task while disabled");
		return -1;
	}

	public int sync(Runnable runnable) {
		if (plugin.isEnabled())
			return scheduler.runTask(plugin, runnable).getTaskId();
		plugin.getLogger().info("Attempted to register sync task while disabled");
		return -1;
	}

	public int waitAsync(Time delay, Runnable runnable) {
		return waitAsync(delay.get(), runnable);
	}

	public int waitAsync(long delay, Runnable runnable) {
		if (plugin.isEnabled())
			return scheduler.runTaskLater(plugin, () -> async(runnable), delay).getTaskId();
		plugin.getLogger().info("Attempted to register waitAsync task while disabled");
		return -1;
	}

	public int repeatAsync(long startDelay, Time interval, Runnable runnable) {
		return repeatAsync(startDelay, interval.get(), runnable);
	}

	public int repeatAsync(Time startDelay, long interval, Runnable runnable) {
		return repeatAsync(startDelay.get(), interval, runnable);
	}

	public int repeatAsync(Time startDelay, Time interval, Runnable runnable) {
		return repeatAsync(startDelay.get(), interval.get(), runnable);
	}

	public int repeatAsync(long startDelay, long interval, Runnable runnable) {
		if (plugin.isEnabled())
			return scheduler.runTaskTimerAsynchronously(plugin, runnable, startDelay, interval).getTaskId();
		plugin.getLogger().info("Attempted to register repeatAsync task while disabled");
		return -1;
	}

	public int async(Runnable runnable) {
		if (plugin.isEnabled())
			return scheduler.runTaskAsynchronously(plugin, runnable).getTaskId();
		plugin.getLogger().info("Attempted to register async task while disabled");
		return -1;
	}

	public static void cancel(int taskId) {
		scheduler.cancelTask(taskId);
	}

	public CountdownBuilder countdown() {
		return Countdown.builder().tasks(this);
	}

	public static class Countdown {
		private final Tasks tasks;
		private final int duration;
		private final boolean doZero;
		private final Consumer<Integer> onTick;
		private final Consumer<Integer> onSecond;
		private final Runnable onStart;
		private final Runnable onComplete;

		@Builder(buildMethodName = "start")
		public Countdown(Tasks tasks, int duration, boolean doZero, Consumer<Integer> onTick, Consumer<Integer> onSecond, Runnable onStart, Runnable onComplete) {
			this.tasks = tasks;
			this.duration = duration;
			this.doZero = doZero;
			this.onTick = onTick;
			this.onSecond = onSecond;
			this.onStart = onStart;
			this.onComplete = onComplete;
			start();
		}

		@Getter
		private int taskId = -1;
		private int ticks;
		private int seconds;

		public void start() {
			if (duration < 0) {
				stop();
				return;
			}

			if (onStart != null)
				onStart.run();

			taskId = tasks.repeat(1, 1, () -> {
				if (duration == ticks) {
					if (doZero)
						iteration();

					if (onComplete != null)
						try {
							onComplete.run();
						} catch (Exception ex) {
							ex.printStackTrace();
						}
					stop();
					return;
				}

				iteration();
			});
		}

		private void iteration() {
			if (ticks % 20 == 0)
				if (onSecond != null)
					onSecond.accept(((duration / 20) - seconds++));
				else
					++seconds;

			if (onTick != null)
				onTick.accept(duration - ticks++);
			else
				++ticks;
		}

		void stop() {
			cancel(taskId);
		}
	}

	public GlowTaskBuilder glowTask() {
		return GlowTask.builder().tasks(this);
	}

	public static class GlowTask {

		@Builder(buildMethodName = "start")
		public GlowTask(Tasks tasks, int duration, Entity entity, GlowAPI.Color color, Runnable onComplete, List<Player> viewers) {
			GlowAPI.setGlowing(entity, color, viewers);
			tasks.wait(duration, () -> GlowAPI.setGlowing(entity, false, viewers));
			if (onComplete != null)
				tasks.wait(duration + 1, onComplete);
		}

	}
}
