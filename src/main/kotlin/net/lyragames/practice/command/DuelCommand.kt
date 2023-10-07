package net.lyragames.practice.command


import co.aikar.commands.BaseCommand
import co.aikar.commands.annotation.*
import net.lyragames.practice.Locale
import net.lyragames.practice.duel.procedure.DuelProcedure
import net.lyragames.practice.duel.procedure.menu.DuelSelectKitMenu
import net.lyragames.practice.manager.MatchManager
import net.lyragames.practice.manager.PartyManager
import net.lyragames.practice.profile.Profile
import net.lyragames.practice.profile.ProfileState
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

@CommandAlias("duel")
object DuelCommand: BaseCommand() {

    @Default
    @Async
    fun duel(sender: CommandSender, @Single @Name("other") target: Player) {
        val player = sender as Player
        if (player.uniqueId == target.uniqueId) {
            player.sendMessage(Locale.CANT_DUEL_YOURSELF.getMessage())
            return
        }

        val profile = Profile.getByUUID(target.uniqueId)

        if (profile!!.duelRequests.any { it.uuid == player.uniqueId && !it.isExpired() }) {
            player.sendMessage(Locale.ONGOING_DUEL.getMessage().replace("<target>", target.name))
            return
        }

        if (profile.state != ProfileState.LOBBY) {
            player.sendMessage(Locale.BUSY_PLAYER.getMessage())
            return
        }

        if (!profile.settings.duels && !player.hasPermission("lpractice.bypass.duels")) {
            player.sendMessage(Locale.DISABLED_DUELS.getMessage())
            return
        }

        val duelProcedure = DuelProcedure(player.uniqueId, target.uniqueId)
        DuelProcedure.duelProcedures.add(duelProcedure)

        DuelSelectKitMenu().openMenu(player)
    }

    @Subcommand("accept")
    @Async
    fun accept(player: CommandSender, @Single @Name("player") target: Player) {
        val profile = Profile.getByUUID((player as Player).uniqueId)
        val duelRequest = profile?.getDuelRequest(target.uniqueId)

        if (duelRequest == null) {
            player.sendMessage(Locale.INVALID_DUEL.getMessage())
            return
        }

        val arena = duelRequest.arena
        profile.duelRequests.remove(duelRequest)

        MatchManager.createMatch(
            duelRequest.kit,
            arena,
            false,
            true,
            player as Player,
            target
        )
    }


}