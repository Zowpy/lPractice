package net.lyragames.practice.queue.menu

import me.zowpy.menu.buttons.Button
import net.lyragames.practice.match.Match
import net.lyragames.practice.profile.Profile
import net.lyragames.practice.profile.ProfileState
import net.lyragames.practice.profile.hotbar.Hotbar
import net.lyragames.practice.queue.Queue
import net.lyragames.practice.queue.QueuePlayer
import net.lyragames.practice.utils.CC
import net.lyragames.practice.utils.ItemBuilder
import org.bukkit.entity.Player
import org.bukkit.event.inventory.ClickType
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.ItemStack


/**
 * This Project is property of Zowpy © 2022
 * Redistribution of this Project is not allowed
 *
 * @author Zowpy
 * Created: 2/16/2022
 * Project: lPractice
 */

class QueueButton(private val queue: Queue, private val ranked: Boolean): Button() {

    override fun getButtonItem(player: Player?): ItemStack {
        val playing = Match.matches.stream().filter {
            it!!.kit.name.equals(queue.kit.name, false) && it.ranked == ranked
        }.count() * 2

        return ItemBuilder(queue.kit.displayItem.clone())
            .amount(if (playing <= 0) 1 else playing.toInt())
            .name("${CC.BOLD}${CC.YELLOW}${queue.kit.displayName}")
            .addFlags(ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_POTION_EFFECTS)
            .lore(arrayListOf(
                "${CC.PRIMARY}Playing: ${CC.SECONDARY}$playing",
                "${CC.PRIMARY}Queuing: ${CC.GREEN}${queue.queuePlayers.size}",
                "",
                "${CC.PRIMARY}Click to play!"
            )).build()
    }

    override fun clicked(player: Player, slot: Int, clickType: ClickType?, hotbarButton: Int) {
        if (clickType!!.isLeftClick) {
            val profile = Profile.getByUUID(player.uniqueId)

            if (profile?.state == ProfileState.QUEUE) {
                player.sendMessage("${CC.RED}You are already in a queue!")
                return
            }

            val queuePlayer = QueuePlayer(player.uniqueId, player.name, queue, profile?.settings?.pingRestriction!!)
            queuePlayer.elo = profile.getKitStatistic(queue.kit.name)?.elo!!

            profile.queuePlayer = queuePlayer
            profile.state = ProfileState.QUEUE

            queue.queuePlayers.add(queuePlayer)

            player.sendMessage(" ")
            player.sendMessage("${CC.PRIMARY}${CC.BOLD}${if (ranked) "Ranked" else "Unranked"}")
            player.sendMessage("${CC.PRIMARY} ⚫ Ping Range: ${CC.SECONDARY}[${if (profile.settings.pingRestriction == 0) "Unrestricted" else profile.settings.pingRestriction}]")
            player.sendMessage("${CC.GRAY}${CC.ITALIC} Searching for match...")
            player.sendMessage(" ")

            Hotbar.giveHotbar(profile)
            player.closeInventory()
        }
    }
}