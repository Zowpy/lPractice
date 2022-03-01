package net.lyragames.practice.events;

import org.bukkit.entity.Player;

import java.util.UUID;

public class EventPlayer {

    private UUID uuid;
    private Player player;
    private PracticeEvent event;


    public UUID getUuid() {
        return this.uuid;
    }

    public Player getPlayer() {return this.player;}

    public PracticeEvent getEvent() {
        return this.event;
    }

    public EventPlayer(UUID uuid, PracticeEvent event) {
        this.uuid = uuid;
        this.player = player;
        this.event = event;
    }


}
