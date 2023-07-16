package net.lyragames.practice.profile.hotbar

import com.cryptomorin.xseries.XMaterial
import net.lyragames.practice.event.EventState
import net.lyragames.practice.kit.editor.KitEditorSelectKitMenu
import net.lyragames.practice.manager.EventManager
import net.lyragames.practice.manager.PartyManager
import net.lyragames.practice.manager.QueueManager
import net.lyragames.practice.match.Match
import net.lyragames.practice.match.ffa.menu.FFAChoosingMenu
import net.lyragames.practice.party.duel.procedure.PartyDuelProcedure
import net.lyragames.practice.party.duel.procedure.menu.PartyDuelSelectPartyMenu
import net.lyragames.practice.party.menu.PartyInformationMenu
import net.lyragames.practice.party.menu.event.PartyStartEventMenu
import net.lyragames.practice.profile.Profile
import net.lyragames.practice.profile.ProfileState
import net.lyragames.practice.queue.menu.QueueMenu
import net.lyragames.practice.utils.CC
import net.lyragames.practice.utils.ItemBuilder
import net.lyragames.practice.utils.item.CustomItemStack
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.ItemStack
import java.util.function.Consumer


/**
 * This Project is property of Zowpy © 2022
 * Redistribution of this Project is not allowed
 *
 * @author Zowpy
 * Created: 2/21/2022
 * Project: lPractice
 */

object Hotbar {

    fun giveHotbar(profile: Profile) {
        val player = Bukkit.getPlayer(profile.uuid)
        player.inventory.clear()

        if (profile.state == ProfileState.LOBBY) {

            if (profile.party != null) {

                player.inventory.setItem(0, createCustomItem(
                    player,
                    ItemBuilder(Material.NETHER_STAR).name("&eParty Information").build()
                ) { PartyManager.getByUUID(profile.party!!)?.let { it1 -> PartyInformationMenu(it1).openMenu(player) } }.itemStack)

                player.inventory.setItem(4, createCustomItem(
                    player,
                    ItemBuilder(Material.GOLD_AXE).name("&eStart Party Event").addFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_UNBREAKABLE).setUnbreakable(true).build()
                ) { PartyStartEventMenu().openMenu(player) }.itemStack)

                player.inventory.setItem(5, createCustomItem(
                    player,
                    ItemBuilder(Material.DIAMOND_AXE).name("&eParty Duel").addFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_UNBREAKABLE).setUnbreakable(true).build()
                ) {
                    val duelProcedure = PartyDuelProcedure(player.uniqueId)
                    PartyDuelProcedure.duelProcedures.add(duelProcedure)

                    PartyDuelSelectPartyMenu().openMenu(player)
                }.itemStack)

                player.inventory.setItem(8, createCustomItem(
                    player,
                    ItemBuilder(XMaterial.RED_DYE.parseItem()).name("&cLeave Party").build(), true
                ) { player.chat("/party leave") }.itemStack
                )

                return
            }

            player.inventory.setItem(0, createCustomItem(
                player,
                ItemBuilder(Material.IRON_SWORD).name("&eUnranked").addFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_UNBREAKABLE).setUnbreakable(true).build()
            ) { QueueMenu(false).openMenu(player) }.itemStack)

            player.inventory.setItem(1, createCustomItem(
                player,
                ItemBuilder(Material.DIAMOND_SWORD).name("&eRanked").addFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_UNBREAKABLE).setUnbreakable(true).build()
            ) { QueueMenu(true).openMenu(player) }.itemStack)

            player.inventory.setItem(2, createCustomItem(
                player,
                ItemBuilder(Material.GOLD_SWORD).name("&eFFA").addFlags(ItemFlag.HIDE_UNBREAKABLE, ItemFlag.HIDE_ATTRIBUTES).setUnbreakable(true).build(),
            ) { FFAChoosingMenu().openMenu(player) }.itemStack)

            player.inventory.setItem(4, createCustomItem(
                player,
                ItemBuilder(Material.NETHER_STAR).name("&eCreate Party").build(), true
            ) { player.chat("/party create") }.itemStack)

            player.inventory.setItem(7, createCustomItem(
                player,
                ItemBuilder(Material.EYE_OF_ENDER).name("&eHost Events").build()
            ) { it.isCancelled = true
                player.chat("/event host")
            }.itemStack)

            player.inventory.setItem(8, createCustomItem(
                player,
                ItemBuilder(Material.BOOK).name("&eEdit Kit").addFlags(ItemFlag.HIDE_ATTRIBUTES).build()
            ) { KitEditorSelectKitMenu().openMenu(player) }.itemStack)
        }else if (profile.state == ProfileState.QUEUE) {

            player.inventory.setItem(8, createCustomItem(
                player,
                ItemBuilder(XMaterial.RED_DYE.parseItem()).name("&cLeave Queue").build(), true
            ) {
                profile.state = ProfileState.LOBBY
                profile.queuePlayer = null
                QueueManager.getQueue(profile.uuid)?.queuePlayers?.removeIf { it.uuid == player.uniqueId }
                giveHotbar(profile)
            }.itemStack)

        }else if (profile.state == ProfileState.EVENT) {

            if (player.hasPermission("lpractice.command.event.forcestart")) {
                if (EventManager.event != null && EventManager.event?.state == EventState.ANNOUNCING) {
                    player.inventory.setItem(0, createCustomItem(
                        player,
                        ItemBuilder(Material.HOPPER).name("&eForce Start").build(), true
                    ) {
                        player.chat("/event forcestart")
                    }.itemStack)
                }
            }

            player.inventory.setItem(8, createCustomItem(
                player,
                ItemBuilder(XMaterial.RED_DYE.parseItem()).name("&cLeave Event").build(), true
            ) {
                EventManager.event?.removePlayer(player)
                Bukkit.broadcastMessage("${CC.GREEN}${player.name}${CC.YELLOW} has left the event. ${CC.GRAY}(${EventManager.event?.players?.size}/${EventManager.event?.requiredPlayers})")
            }.itemStack)
        }else if (profile.state == ProfileState.SPECTATING) {

            player.inventory.setItem(8, createCustomItem(
                player,
                ItemBuilder(XMaterial.RED_DYE.parseItem()).name("&cLeave Spectating").build(),
                true
            ) {
                profile.state = ProfileState.LOBBY

                if (profile.spectatingMatch != null) {
                    val match = Match.getByUUID(profile.spectatingMatch!!)

                    match?.removeSpectator(player)
                }
            }.itemStack)
        }
    }

    private fun createCustomItem(player: Player, itemStack: ItemStack, consumer: Consumer<PlayerInteractEvent>): CustomItemStack {
        val customItemStack = CustomItemStack(player.uniqueId, itemStack)
        customItemStack.rightClick = true
        customItemStack.clicked
        customItemStack.clicked = consumer

        if (!CustomItemStack.customItemStacks.contains(customItemStack)) {
            customItemStack.create()
        }

        return customItemStack
    }

    private fun createCustomItem(player: Player, itemStack: ItemStack, remove: Boolean, consumer: Consumer<PlayerInteractEvent>): CustomItemStack {
        val customItemStack = CustomItemStack(player.uniqueId, itemStack)
        customItemStack.rightClick = true
        customItemStack.clicked
        customItemStack.clicked = consumer
        customItemStack.removeOnClick = remove

        if (!CustomItemStack.customItemStacks.contains(customItemStack)) {
            customItemStack.create()
        }

        return customItemStack
    }
}