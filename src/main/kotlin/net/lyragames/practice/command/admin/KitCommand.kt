package net.lyragames.practice.command.admin

import me.vaperion.blade.command.annotation.Command
import me.vaperion.blade.command.annotation.Permission
import me.vaperion.blade.command.annotation.Sender
import net.lyragames.llib.utils.CC
import net.lyragames.practice.kit.Kit
import net.lyragames.practice.kit.admin.AdminKitEditMenu
import org.bukkit.entity.Player


/**
 * This Project is property of Zowpy © 2022
 * Redistribution of this Project is not allowed
 *
 * @author Zowpy
 * Created: 2/16/2022
 * Project: lPractice
 */

object KitCommand {

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

        player.sendMessage(CC.YELLOW + "Successfully created" + CC.GOLD + "'$name'!")
    }

    @Permission("lpractice.command.kit.content")
    @Command(value = ["kit content"], description = "set a kit's items")
    fun content(@Sender player: Player, kit: Kit) {
        kit.content = player.inventory.contents
        kit.armorContent = player.inventory.armorContents
        kit.save()

        player.sendMessage(CC.YELLOW + "Successfully set " + CC.GOLD + kit.name + "'s " + CC.YELLOW + "item contents!")
    }

    @Permission("lpractice.command.kit.edit")
    @Command(value = ["kit edit"], description = "edit a kit!")
    fun edit(@Sender player: Player, kit: Kit) {
        AdminKitEditMenu(kit).openMenu(player)
    }
}