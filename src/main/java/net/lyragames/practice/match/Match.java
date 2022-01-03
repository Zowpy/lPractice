package net.lyragames.practice.match;

import lombok.Getter;
import lombok.Setter;
import net.lyragames.llib.item.CustomItemStack;
import net.lyragames.llib.utils.CC;
import net.lyragames.llib.utils.Countdown;
import net.lyragames.llib.utils.ItemBuilder;
import net.lyragames.llib.utils.PlayerUtil;
import net.lyragames.practice.PracticePlugin;
import net.lyragames.practice.arena.Arena;
import net.lyragames.practice.kit.EditedKit;
import net.lyragames.practice.kit.Kit;
import net.lyragames.practice.match.player.MatchPlayer;
import net.lyragames.practice.profile.Profile;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.*;

/**
 * This Project is property of Zowpy © 2021
 * Redistribution of this Project is not allowed
 *
 * @author Zowpy
 * Created: 12/19/2021
 * Project: Practice
 */

@Getter @Setter
public abstract class Match {

    @Getter private static List<Match> matches = new LinkedList<>();

    private final UUID uuid = UUID.randomUUID();

    private final Kit kit;
    private final Arena arena;

    private final boolean ranked;

    private MatchState matchState = MatchState.STARTING;

    private final long started = System.currentTimeMillis();
    private final List<MatchPlayer> players = new ArrayList<>();
    private final List<Block> blocksPlaced = new LinkedList<>();
    private final List<Item> droppedItems = new ArrayList<>();

    public Match(Kit kit, Arena arena, boolean ranked) {
        this.kit = kit;
        this.arena = arena;
        this.ranked = ranked;
    }

    public void start() {
        for (MatchPlayer matchPlayer : players) {
            if (matchPlayer.isOffline()) continue;

            Player player = matchPlayer.getPlayer();

            if (player == null) continue;

            generateBooks(player);

            new Countdown(PracticePlugin.getInstance(), player,  "&aMatch starting in <seconds> seconds!", 6, aBoolean -> {
                player.sendMessage(CC.GREEN + "Match started!");
            });
        }
    }

    public void generateBooks(Player player) {
        Profile profile = Profile.getByUUID(player.getUniqueId());

        int i = 0;

        for (EditedKit editedKit : profile.getEditKitsByKit(kit)) {
            CustomItemStack item = new CustomItemStack(player.getUniqueId(), new ItemBuilder(Material.BOOK).enchantment(Enchantment.DURABILITY)
                    .name(CC.RED + editedKit.getName()).build());

            item.setRightClick(true);
            item.setClicked(event -> {
                Player player1 = event.getPlayer();

                player1.getInventory().setContents(editedKit.getContent());
                player1.getInventory().setArmorContents(editedKit.getArmorContent());

                player1.updateInventory();
            });

            if (i++ == 9) i++;

            player.getInventory().setItem(i, item.getItemStack());
        }
    }
}
