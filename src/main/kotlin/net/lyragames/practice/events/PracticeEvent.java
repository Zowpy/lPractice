package net.lyragames.practice.events;

import lombok.Getter;
import lombok.Setter;
import net.lyragames.llib.utils.PlayerUtil;
import net.lyragames.practice.PracticePlugin;
import net.lyragames.practice.profile.Profile;
import net.lyragames.practice.profile.ProfileState;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Getter @Setter
public class PracticeEvent <K extends EventPlayer>{

    private final PracticePlugin plugin = PracticePlugin.getInstance();
    private final String name;
    private Player host;
    private int limit = 30;
    private EventState state = EventState.NOT_ANNOUNCED;
    private List<UUID> eventUUIDS = new ArrayList<UUID>();
    private List<Player> eventPlayers = new ArrayList<Player>();
    private List<Player> spectators = new ArrayList<Player>();


    // start countdown



    // join
    public void join(Player player) {
        if(this.getEventPlayers().size() >= this.getLimit() && !player.hasPermission("lpractice.events.join.full")) return;
        this.eventUUIDS.add(player.getUniqueId());
        this.eventPlayers.add(player);
        Profile profile = Profile.getByUUID(player.getUniqueId());
        profile.setState(ProfileState.EVENT);
        PlayerUtil.reset(player);
       // Check Event spawn locations after checking event type
        // Show event players to the incoming player and other way around
        this.eventPlayers.stream().forEach(player1 -> player.showPlayer(player1));
        this.eventPlayers.stream().forEach(player1 -> player1.showPlayer(player));
        // Send join message to incoming player
        // send player joined message to existing players


    }


    //leave
    public void leave(Player player) {
        this.eventPlayers.remove(player);
        this.eventUUIDS.remove(player);
        // set player spectator mode gm3 and display death or left message to all players
        // teleport to spawn on leave
    }

    // event start
    public void start() {
        this.setState(EventState.STARTED);

        // set event cooldown
    }
    public void onWin() {
        // display win message
    }
    //event end

    public void end() {
        this.eventUUIDS.clear();
        this.eventPlayers.clear();
        this.setState(EventState.NOT_ANNOUNCED);
        // clear spectator list and send to spawn
        // send all players back to spawn and clear them


    }

    // maybe implement random player getter



    // message each player
    public void sendMessage(String msg) {
        eventPlayers.stream().forEach(player -> player.sendMessage(msg));
    }

    public K getPlayer(Player player) {
        return this.getPlayer(player.getUniqueId());
    }

    public K getPlayer(UUID uuid) {
        return (K) ((EventPlayer) this.getPlayer(uuid));
    }

    public PracticeEvent(String name) {
        this.name = name;

    }

}
