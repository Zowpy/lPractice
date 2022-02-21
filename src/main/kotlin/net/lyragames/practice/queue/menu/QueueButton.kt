package net.lyragames.practice.queue.menu

import net.lyragames.llib.utils.CC
import net.lyragames.llib.utils.ItemBuilder
import net.lyragames.menu.Button
import net.lyragames.practice.match.Match
import net.lyragames.practice.profile.Profile
import net.lyragames.practice.profile.ProfileState
import net.lyragames.practice.queue.Queue
import net.lyragames.practice.queue.QueuePlayer
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
        return ItemBuilder(queue.kit.displayItem)
            .name(CC.BOLD + CC.YELLOW + queue.kit.name).addFlags(ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_POTION_EFFECTS)
            .lore(arrayListOf(
                CC.YELLOW + "Playing: ${CC.PINK}${Match.matches.stream().filter { 
                    it!!.kit.name.equals(queue.kit.name, false) && it.ranked == ranked
                }.count() * 2}",
                CC.YELLOW + "Queuing: ${CC.PINK}${queue.queuePlayers.size}",
                "",
                CC.YELLOW + "Click to play!"
            )).build()
    }

    override fun clicked(player: Player, slot: Int, clickType: ClickType?, hotbarButton: Int) {
        if (clickType!!.isLeftClick) {
            val profile = Profile.getByUUID(player.uniqueId)

            if (profile?.state == ProfileState.QUEUE) {
                player.sendMessage("${CC.RED}You are already in a queue!")
                return
            }

            val queuePlayer = QueuePlayer(player.uniqueId, player.name)

            profile?.queuePlayer = queuePlayer
            profile?.state = ProfileState.QUEUE

            queue.queuePlayers.add(queuePlayer)

            player.sendMessage("${CC.YELLOW}You have been added to the queue!")
            player.closeInventory()
        }
    }
}