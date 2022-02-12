package net.lyragames.practice.match

import lombok.Getter
import lombok.Setter
import net.lyragames.llib.item.CustomItemStack
import net.lyragames.llib.utils.CC
import net.lyragames.llib.utils.Countdown
import net.lyragames.llib.utils.ItemBuilder
import net.lyragames.practice.PracticePlugin
import net.lyragames.practice.arena.Arena
import net.lyragames.practice.kit.Kit
import net.lyragames.practice.match.player.MatchPlayer
import net.lyragames.practice.profile.Profile
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.block.Block
import org.bukkit.enchantments.Enchantment
import org.bukkit.entity.Item
import org.bukkit.entity.Player
import org.bukkit.event.player.PlayerInteractEvent
import java.util.*
import java.util.function.Consumer
import java.util.function.Predicate

/**
 * This Project is property of Zowpy © 2021
 * Redistribution of this Project is not allowed
 *
 * @author Zowpy
 * Created: 12/19/2021
 * Project: Practice
 */
@Getter
@Setter
abstract class Match(val kit: Kit, val arena: Arena, val ranked: Boolean) {

    val uuid = UUID.randomUUID()
    val party = false
    var matchState = MatchState.STARTING
    val started = System.currentTimeMillis()
    val players: MutableList<MatchPlayer> = mutableListOf()
    val blocksPlaced: MutableList<Block> = mutableListOf()
    val droppedItems: MutableList<Item> = mutableListOf()

    fun start() {
        for (matchPlayer in players) {
            if (matchPlayer.offline) continue
            val player = matchPlayer.player
            generateBooks(player)
            Countdown(
                PracticePlugin.instance,
                player,
                "&aMatch starting in <seconds> seconds!",
                6
            ) {
                player.sendMessage(CC.GREEN + "Match started!")
                matchState = MatchState.FIGHTING
            }
        }
    }

    private fun generateBooks(player: Player) {
        val profile: Profile? = Profile.getByUUID(player.uniqueId)
        var i = 1
        val customItemStack = CustomItemStack(
            player.uniqueId, ItemBuilder(Material.BOOK).enchantment(Enchantment.DURABILITY)
                .name(CC.RED + "Default").build()
        )
        customItemStack.isRightClick = true
        customItemStack.clicked = Consumer { event: PlayerInteractEvent ->
            val player1 = event.player
            player1.inventory.contents = kit.content
            player1.inventory.armorContents = kit.armorContent
            player1.updateInventory()
        }
        CustomItemStack.getCustomItemStacks().add(customItemStack)
        player.inventory.setItem(0, customItemStack.itemStack)
        for (editedKit in profile!!.getEditKitsByKit(kit)) {
            val item = CustomItemStack(
                player.uniqueId, ItemBuilder(Material.BOOK).enchantment(Enchantment.DURABILITY)
                    .name(CC.RED + editedKit.name).build()
            )
            item.isRightClick = true
            item.clicked = Consumer { event: PlayerInteractEvent ->
                val player1 = event.player
                player1.inventory.contents = editedKit.content
                player1.inventory.armorContents = editedKit.armorContent
                player1.updateInventory()
            }
            CustomItemStack.getCustomItemStacks().add(item)
            if (i++ == 9) i++
            player.inventory.setItem(i, item.itemStack)
        }
    }

    open fun canHit(player: Player, target: Player) :Boolean {
        return true
    }

    open fun addPlayer(player: Player, location: Location) {
        val matchPlayer = MatchPlayer(player.uniqueId, player.name, location)
        players.add(matchPlayer)
    }

    fun getMatchPlayer(uuid: UUID): MatchPlayer {
        return players.stream().filter { matchPlayer -> matchPlayer.uuid == uuid }
            .findFirst().orElse(null)
    }

    companion object {
        @Getter
        private val matches: List<Match?> = LinkedList()
        fun getByUUID(uuid: UUID): Match? {
            return matches.stream().filter { match: Match? -> match?.uuid == uuid }
                .findFirst().orElse(null)
        }
    }
}