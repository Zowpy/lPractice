package net.lyragames.practice.utils;

import lombok.Data;
import lombok.EqualsAndHashCode;
import net.lyragames.practice.PracticePlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.function.Consumer;

@EqualsAndHashCode(callSuper = true)
@Data
public class Cooldown extends BukkitRunnable {

    private final long startedAt = System.currentTimeMillis();
    private int seconds;
    private Consumer<Boolean> consumer;

    public Cooldown(int seconds, Consumer<Boolean> consumer) {
        this.seconds = seconds;
        this.consumer = consumer;

        this.runTaskLater(PracticePlugin.getInstance(), seconds * 20L);
    }

    public boolean hasExpired() {
        return startedAt + (seconds * 1000L) <= System.currentTimeMillis();
    }

    public long getTimeRemaining() {
        return this.startedAt + (long)this.seconds * 1000L - System.currentTimeMillis();
    }

    @Override
    public void run() {
        consumer.accept(true);
    }
}