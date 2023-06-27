package net.lyragames.practice.utils.countdown;

import net.lyragames.llib.title.TitleBar;
import net.lyragames.llib.utils.CC;
import net.lyragames.practice.PracticePlugin;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.function.Consumer;

public class TitleCountdown extends BukkitRunnable {

    private final Player player;
    private final String message, title, subtitle;
    private final Consumer<Boolean> consumer;
    private int seconds;

    public TitleCountdown(Player player, String message, String title, String subtitle, int seconds, Consumer<Boolean> consumer) {
        this.player = player;
        this.message = message;
        this.title = title;
        this.subtitle = subtitle;
        this.seconds = seconds;
        this.consumer = consumer;

        this.runTaskTimer(PracticePlugin.getInstance(), 0L, 20L);
    }

    public void run() {
        --this.seconds;
        if (this.seconds != 0) {
            this.player.sendMessage(CC.translate(this.message.replace("<seconds>", this.seconds + "")));

            TitleBar titleBar = new TitleBar(CC.translate(title), false);
            TitleBar subtitleBar = new TitleBar(CC.translate(subtitle), true);

            titleBar.sendPacket(player);
            subtitleBar.sendPacket(player);
        }

        if (this.seconds <= 0) {
            this.consumer.accept(true);
            this.cancel();
        }

    }

    public Player getPlayer() {
        return this.player;
    }

    public String getMessage() {
        return this.message;
    }

    public Consumer<Boolean> getConsumer() {
        return this.consumer;
    }

    public int getSeconds() {
        return this.seconds;
    }
}
