package net.lyragames.practice.command.admin

import me.vaperion.blade.command.annotation.Command
import me.vaperion.blade.command.annotation.Permission
import me.vaperion.blade.command.annotation.Sender
import net.lyragames.llib.utils.CC
import net.lyragames.practice.PracticePlugin
import net.lyragames.practice.kit.Kit
import net.lyragames.practice.kit.admin.AdminKitEditMenu
import net.lyragames.practice.profile.Profile
import org.bukkit.GameMode
import org.bukkit.entity.Player
import java.util.*


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
    @Command(value = ["kit help", "kit"], description = "kit help message")
    fun help(@Sender player: Player) {
        player.sendMessage("${CC.PRIMARY}Kit Commands:")
        player.sendMessage("${CC.SECONDARY}/kit create <name>")
        player.sendMessage("${CC.SECONDARY}/kit delete <kit>")
        player.sendMessage("${CC.SECONDARY}/kit content <kit>")
        player.sendMessage("${CC.SECONDARY}/kit edit <kit>")
    }

    @Permission("lpractice.command.kit.create")
    @Command(value = ["kit create"], description = "create a new kit")
    fun create(@Sender player: Player, name: String) {
        if (Kit.getByName(name) != null) {
            player.sendMessage(CC.RED + "That kit already exists!")
            return
        }

        val kit = Kit(name)
        kit.save()
        Kit.kits.add(kit)

        player.sendMessage(CC.YELLOW + "Successfully created ${CC.GOLD}${kit.name}!")
    }

    @Permission("lpractice.command.kit.content")
    @Command(value = ["kit content"], description = "set a kit's items")
    fun content(@Sender player: Player, kit: Kit) {

        if (player.gameMode != GameMode.SURVIVAL) {
            player.sendMessage("${CC.RED}You must be in survival mode to set inventory contents!")
            return
        }

        kit.content = player.inventory.contents
        kit.armorContent = player.inventory.armorContents
        kit.save()

        for (document in PracticePlugin.instance.practiceMongo.profiles.find()) {
            val profile = Profile(UUID.fromString(document.getString("uuid")), document.getString("name"))
            profile.load(document)

            profile.getKitStatistic(kit.name)?.editedKits?.clear()
        }

        player.sendMessage(CC.YELLOW + "Successfully set " + CC.GOLD + kit.name + "'s " + CC.YELLOW + "item contents!")
    }

    @Permission("lpractice.command.kit.edit")
    @Command(value = ["kit edit"], description = "edit a kit!")
    fun edit(@Sender player: Player, kit: Kit) {
        AdminKitEditMenu(kit).openMenu(player)
    }
}