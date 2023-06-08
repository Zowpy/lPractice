package net.lyragames.practice.bots

import net.lyragames.practice.PracticePlugin
import net.lyragames.practice.kit.Kit
import org.bukkit.entity.Damageable
import org.bukkit.entity.Player
import org.bukkit.scheduler.BukkitRunnable
import java.util.*


class BotLogic: BukkitRunnable() {

    override fun run() {
        TODO("Not yet implemented")
    }
   /* private val lyraBot: LyraBot? = null
    private val difficulty: LyraBot.BotDifficulty? = null
    private val random: Random? = null
    private val players: List<UUID>? = null
    private val kit = false
    private val navigation = false
    private val selfHealing = false
    private var attackRange = 0.0
    private var swingRangeModifier = 0.0
    private val target: Player? = null


    fun startupBotLogic() {
        var delay = 2

        when (difficulty) {
            LyraBot.BotDifficulty.EASY -> {
                attackRange *= 0.8
                swingRangeModifier = -0.5
            }
            LyraBot.BotDifficulty.HARD -> {
                attackRange *= 2.0
                swingRangeModifier = 2.0
            }
            LyraBot.BotDifficulty.EXPERT -> {
                attackRange *= 2.6
                swingRangeModifier = 3.0
                delay = 1
            }

        }
        runTaskTimerAsynchronously(PracticePlugin.instance, 60L, delay.toLong())

    }

    fun applyKitToBot(kit: Kit, lyraBot: LyraBot) {
        lyraBot.getBukkitEntity().inventory.contents = kit.content
        lyraBot.getBukkitEntity().inventory.armorContents = kit.armorContent
        lyraBot.getBukkitEntity().updateInventory()
        // TODO (Add Bot Knockback Profile)

    }

    fun attemptToHeal() {
        if (selfHealing) return
        var damage: Damageable = lyraBot?.getBukkitEntity() as Damageable
//        if (damage.getHealth() <= 13.0 && random!!.nextBoolean() && !splashPotion() && !this.useSoupRefill()) {
//            this.useGoldenApple()
//        }
    }






    override fun run() {
        TODO("Not yet implemented")
    } */
}