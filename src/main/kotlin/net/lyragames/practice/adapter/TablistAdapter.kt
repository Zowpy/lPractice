package net.lyragames.practice.adapter

import io.github.thatkawaiisam.ziggurat.ZigguratAdapter
import io.github.thatkawaiisam.ziggurat.utils.BufferedTabObject
import io.github.thatkawaiisam.ziggurat.utils.SkinTexture
import net.lyragames.core.api.profile.Profile
import net.lyragames.core.bukkit.CorePlugin
import net.lyragames.practice.PracticePlugin
import net.lyragames.practice.utils.PlayerUtil
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



        return toReturn
    }

    override fun getFooter(): String {
        return ChatColor.translateAlternateColorCodes('&', PracticePlugin.instance.tablistFile.getString("tablist.footer"))
    }

    override fun getHeader(): String {
        return ChatColor.translateAlternateColorCodes('&', PracticePlugin.instance.tablistFile.getString("tablist.header"))
    }
}