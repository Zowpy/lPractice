package net.lyragames.practice.match.ffa

import com.comphenix.protocol.PacketType
import com.comphenix.protocol.ProtocolLibrary
import net.lyragames.llib.utils.CC
import net.lyragames.llib.utils.PlayerUtil
import net.lyragames.practice.PracticePlugin
import net.lyragames.practice.constants.Constants
import net.lyragames.practice.kit.Kit
import net.lyragames.practice.profile.Profile
import net.lyragames.practice.profile.ProfileState
import net.lyragames.practice.profile.hotbar.Hotbar
import net.lyragames.practice.utils.wrapper.WrapperPlayServerSpawnEntity
import net.minecraft.server.v1_8_R3.PacketPlayOutEntityMetadata
import org.bukkit.Bukkit
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftEntity
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer
import org.bukkit.entity.Item
import org.bukkit.event.player.PlayerDropItemEvent
import java.util.*

/**
 * This Project is property of Zowpy © 2022
 * Redistribution of this Project is not allowed
 *
 * @author Zowpy
 * Created: 2/26/2022
 * Project: lPractice
 */

class FFA(val kit: Kit) {

    val uuid: UUID = UUID.randomUUID()

    val players: MutableList<FFAPlayer> = mutableListOf()
    val droppedItems: MutableList<Item> = mutableListOf()

    fun handleDeath(ffaPlayer: FFAPlayer, killer: FFAPlayer?) {
        ffaPlayer.death++
        ffaPlayer.killStreak = 0

        if (killer != null) {
            killer.kills++
            killer.killStreak++

            sendMessage("${CC.SECONDARY}${ffaPlayer.name} ${CC.PRIMARY}has been killed by ${CC.SECONDARY}${killer.name}${CC.PRIMARY}.")
        }else {
            sendMessage("${CC.SECONDARY}${ffaPlayer.name} ${CC.PRIMARY}died from natural causes.")
        }

        setup(ffaPlayer)
    }

    fun firstSetup(ffaPlayer: FFAPlayer) {
        if (ffaPlayer.player == null) return

        for (item in droppedItems) {

            val packet = WrapperPlayServerSpawnEntity()

            packet.entityID = item.entityId
            packet.x = item.location.x
            packet.y = item.location.y
            packet.z = item.location.z
            packet.pitch = item.location.pitch
            packet.yaw = item.location.pitch
            packet.type = 2
            packet.objectData = 2

            packet.sendPacket(ffaPlayer.player)

            val dataWatcher = (item as CraftEntity).handle.dataWatcher

            val metadata = PacketPlayOutEntityMetadata((item as Item).entityId, dataWatcher, true)
            (ffaPlayer.player as CraftPlayer).handle.playerConnection.sendPacket(metadata)
        }
    }

    fun setup(ffaPlayer: FFAPlayer) {
        val player = ffaPlayer.player
        val profile = Profile.getByUUID(player.uniqueId)

        if (Constants.FFA_SPAWN != null) {
            player.teleport(Constants.FFA_SPAWN)
        }

        PlayerUtil.reset(player)

        profile?.getKitStatistic(kit.name)?.generateBooks(player)

        players.stream().map { it.player }
            .forEach {
                player.showPlayer(it)
                it.showPlayer(player)
            }
    }

    fun handleLeave(ffaPlayer: FFAPlayer, offline: Boolean) {
        players.remove(ffaPlayer)

        if (!offline) {
            val player = ffaPlayer.player
            val profile = Profile.getByUUID(ffaPlayer.uuid)

            for (item in droppedItems) {
                val packet = ProtocolLibrary.getProtocolManager().createPacket(PacketType.Play.Server.ENTITY_DESTROY)
                packet.integerArrays.write(0, intArrayOf(item.entityId))

                ProtocolLibrary.getProtocolManager().sendServerPacket(player, packet)
            }

            PlayerUtil.reset(player)

            profile!!.state = ProfileState.LOBBY
            profile.ffa = null

            if (profile.enderPearlCooldown != null) {
                profile.enderPearlCooldown!!.cancel()
                profile.enderPearlCooldown = null
            }

            if (Constants.SPAWN != null) {
                player.teleport(Constants.SPAWN)
            }

            players.filter { it.player != null }.map { it.player }
                .forEach {
                    player.hidePlayer(it)
                    it.hidePlayer(player)
                }

            Hotbar.giveHotbar(profile)
        }
    }

    fun handleDrop(event: PlayerDropItemEvent) {
        val item = event.itemDrop

        droppedItems.add(item)

        Bukkit.getScheduler().runTaskLater(PracticePlugin.instance, {
            for (player in Bukkit.getOnlinePlayers()) {
                if (inFFA(player.uniqueId)) continue

                val packet = ProtocolLibrary.getProtocolManager().createPacket(PacketType.Play.Server.ENTITY_DESTROY)
                packet.integerArrays.write(0, intArrayOf(item.entityId))

                ProtocolLibrary.getProtocolManager().sendServerPacket(player, packet)
            }
        }, 1L)
    }

    fun getFFAPlayer(uuid: UUID): FFAPlayer? {
        return players.first { it.uuid == uuid }
    }

    fun inFFA(uuid: UUID): Boolean {
        return players.any { it.uuid == uuid }
    }

    fun sendMessage(message: String) {
        players.stream().map { it.player }
            .forEach { it.sendMessage(CC.translate(message)) }
    }
}