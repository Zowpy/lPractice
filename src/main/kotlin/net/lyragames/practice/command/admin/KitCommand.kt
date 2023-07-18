package net.lyragames.practice.command.admin

import me.zowpy.command.annotation.Command
import me.zowpy.command.annotation.Permission
import me.zowpy.command.annotation.Sender
import net.lyragames.practice.PracticePlugin
import net.lyragames.practice.kit.Kit
import net.lyragames.practice.kit.admin.AdminKitManageMenu
import net.lyragames.practice.manager.QueueManager
import net.lyragames.practice.profile.Profile
import net.lyragames.practice.profile.statistics.KitStatistic
import net.lyragames.practice.utils.CC
import org.bukkit.GameMode
import org.bukkit.Material
import org.bukkit.entity.Player
import java.util.*
import java.util.concurrent.CompletableFuture


/**
 * This Project is property of Zowpy © 2022
 * Redistribution of this Project is not allowed
 *
 * @author Zowpy
 * Created: 2/16/2022
 * Project: lPractice
 */

object KitCommand {

    @Permission("lpractice.command.kit.help")
    @Command(name = "kit", aliases = ["kit help"])
    fun help(@Sender player: Player) {
        player.sendMessage("${CC.PRIMARY}Kit Commands:")
        player.sendMessage("${CC.SECONDARY}/kit create <name>")
        player.sendMessage("${CC.SECONDARY}/kit delete <kit>")
        player.sendMessage("${CC.SECONDARY}/kit displayname <kit> <name>")
        player.sendMessage("${CC.SECONDARY}/kit content <kit>")
        player.sendMessage("${CC.SECONDARY}/kit edit <kit>")
        player.sendMessage("${CC.SECONDARY}/kit items <kit>")
        player.sendMessage("${CC.SECONDARY}/kit icon <kit> ${CC.GRAY}- hold item in hand")
    }

    @Permission("lpractice.command.kit.create")
    @Command(name = "kit create")
    fun create(@Sender player: Player, name: String) {
        if (Kit.getByName(name) != null) {
            player.sendMessage(CC.RED + "That kit already exists!")
            return
        }

        val kit = Kit(name)
        kit.save()
        Kit.kits.add(kit)

        CompletableFuture.runAsync {
            for (document in PracticePlugin.instance.practiceMongo.profiles.find()) {
                val profile = Profile(UUID.fromString(document.getString("_id")), null)
                profile.load(document)

                profile.kitStatistics.add(KitStatistic(kit.name))
                profile.saveSync()
            }
        }

        player.sendMessage("${CC.PRIMARY}Successfully created ${CC.SECONDARY}${kit.name}${CC.PRIMARY}!")
    }

    @Permission("lpractice.command.kit.content")
    @Command(name = "kit content")
    fun content(@Sender player: Player, kit: Kit) {

        if (player.gameMode != GameMode.SURVIVAL) {
            player.sendMessage("${CC.RED}You must be in survival mode to set inventory contents!")
            return
        }

        kit.content = player.inventory.contents.clone()
        kit.armorContent = player.inventory.armorContents.clone()
        kit.save()

        CompletableFuture.runAsync {
            for (document in PracticePlugin.instance.practiceMongo.profiles.find()) {
                val profile = Profile(UUID.fromString(document.getString("uuid")), document.getString("name"))
                profile.load(document)

                val editedKits = profile.getKitStatistic(kit.name)?.editedKits ?: continue

                editedKits[0] = null
                editedKits[1] = null
                editedKits[2] = null
                editedKits[3] = null

                profile.saveSync()
            }
        }

        player.sendMessage("${CC.PRIMARY}Successfully set ${CC.SECONDARY}${kit.name}${CC.PRIMARY}'s item contents!")
    }

    @Permission("lpractice.command.kit.content.retreive")
    @Command(name = "kit items")
    fun items(@Sender player: Player, kit: Kit) {
        player.inventory.contents = kit.content
        player.inventory.armorContents = kit.armorContent
        player.sendMessage("${CC.PRIMARY}Successfully retrieved ${CC.SECONDARY}${kit.name}${CC.PRIMARY}'s item contents!")
    }

    @Permission("lpractice.command.kit.displayitem")
    @Command(name = "kit icon", aliases = ["kit displayitem"])
    fun displayItem(@Sender player: Player, kit: Kit) {
        if (player.itemInHand == null || player.itemInHand.type == Material.AIR) {
            player.sendMessage("${CC.RED}You are not holding an item!")
            return
        }
        kit.displayItem = player.itemInHand
        kit.save()

        QueueManager.queues.filter { it.kit.name.equals(kit.name, false) }.forEach {
            it.kit.displayItem = player.itemInHand
        }

        player.sendMessage("${CC.PRIMARY}Successfully set ${CC.SECONDARY}${kit.name}${CC.PRIMARY}'s display item!")
    }

    @Permission("lpractice.command.kit.edit")
    @Command(name = "kit admin")
    fun edit(@Sender player: Player, kit: Kit) {
        AdminKitManageMenu(kit).openMenu(player)
    }

    @Permission("lpractice.command.kit.displayname")
    @Command(name = "kit displayname")
    fun displayName(@Sender player: Player, kit: Kit, name: String) {
        kit.displayName = name
        player.sendMessage("${CC.YELLOW}You have updated ${CC.AQUA}${kit.name}${CC.YELLOW} to display as ${CC.GREEN}${kit.displayName}")
    }
}