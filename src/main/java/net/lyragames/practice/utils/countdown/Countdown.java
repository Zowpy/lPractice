package net.lyragames.practice.utils.countdown;

import lombok.Getter;
import net.lyragames.practice.PracticePlugin;
import net.lyragames.practice.utils.CC;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.function.Consumer;

@Getter
public class Countdown extends BukkitRunnable implements ICountdown {

    private final Player player;
    private final String message;
    private final Consumer<Boolean> consumer;
    private int seconds;

    public Countdown(Player player, String message, int seconds, Consumer<Boolean> consumer) {
        this.player = player;
        this.message = message;
        this.seconds = seconds;
        this.consumer = consumer;

        this.runTaskTimer(PracticePlugin.getInstance(), 0L, 20L);
    }

    public void run() {
        --this.seconds;
        if (this.seconds != 0) {
            this.player.sendMessage(CC.translate(this.message.replace("<seconds>", this.seconds + "")));
        }

        if (this.seconds <= 0) {
            this.consumer.accept(true);
            this.cancel();
        }
    }
}

