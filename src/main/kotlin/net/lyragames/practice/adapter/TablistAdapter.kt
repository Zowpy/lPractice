package net.lyragames.practice.adapter

import io.github.thatkawaiisam.ziggurat.ZigguratAdapter
import io.github.thatkawaiisam.ziggurat.ZigguratCommons
import io.github.thatkawaiisam.ziggurat.utils.BufferedTabObject
import io.github.thatkawaiisam.ziggurat.utils.SkinTexture
import io.github.thatkawaiisam.ziggurat.utils.TabColumn
import javafx.scene.control.Skin
import net.lyragames.core.CorePlugin
import net.lyragames.core.profile.Profile
import net.lyragames.practice.PracticePlugin
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.entity.Player


class TablistAdapter: ZigguratAdapter {
    override fun getSlots(player: Player?): MutableSet<BufferedTabObject> {

        var toReturn: HashSet<BufferedTabObject> = hashSetOf()
        var plugin = CorePlugin.getInstance()
        val profiles: MutableCollection<Profile> = plugin.profileManager.profiles.values

        profiles.sortedByDescending { profile ->
            if (profile.isDisguised) {
                return@sortedByDescending profile.disguisedRank.weight
            }

            return@sortedByDescending profile.rank.weight
        }

        var i = 0
        for (profile in profiles) {
            val bufferedTab = BufferedTabObject()
                .text(profile.coloredName)
                .slot(i)
                .ping(plugin.protocolManager.getPing(Bukkit.getPlayer(profile.uuid)))

            if (profile.isDisguised) {
                val disguise = plugin.disguiseManager.getDisguise(plugin.disguiseManager.getUUID(profile.disguisedName).uuid)
                bufferedTab.skin(SkinTexture(disguise.skin.texture, disguise.skin.signature))
            }else {
                bufferedTab.skin(SkinTexture(profile.originalSkin.texture, profile.originalSkin.signature))
            }



            toReturn.add(bufferedTab)
            i++
        }

        /*toReturn.add(
            BufferedTabObject() //Text
                .text("&a&lThis is in Slot 1") //Column
                .column(TabColumn.LEFT) //Slot
                .slot(1) //Ping (little buggy with 1.7 clients but defaults to 0 if thats the case
                .ping(999) //Texture (need to get skin sig and key
                .skin(ZigguratCommons.defaultTexture)
        ) */



        return toReturn
    }

    override fun getFooter(): String {
        return ChatColor.translateAlternateColorCodes('&', PracticePlugin.instance.tablistFile.getString("tablist.footer"))
    }

    override fun getHeader(): String {
        return ChatColor.translateAlternateColorCodes('&', PracticePlugin.instance.tablistFile.getString("tablist.header"))
    }
}