package net.lyragames.practice.bots

import net.citizensnpcs.api.npc.NPC
import net.citizensnpcs.util.PlayerAnimation
import net.lyragames.menu.ItemBuilder
import net.lyragames.practice.arena.Arena
import net.lyragames.practice.arena.impl.StandaloneArena
import net.lyragames.practice.kit.Kit
import org.bukkit.Effect
import org.bukkit.EntityEffect
import org.bukkit.Material
import org.bukkit.Sound
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import java.util.*

class LyraBot {

    // Variables
    lateinit var npc: NPC
    lateinit var kit: Kit
    lateinit var arena: Arena
    lateinit var standaloneArena: StandaloneArena
    lateinit var botDifficulty: BotDifficulty
    lateinit var botLogic: BotLogic
    lateinit var random: Random
    var destroyed: Boolean = false


    // Functions
    fun isSpawned() = npc.isSpawned

    fun getBukkitEntity(): Player = npc.entity as Player

    fun attack() {
        if (getBukkitEntity() == null) return
        PlayerAnimation.ARM_SWING.play(getBukkitEntity())

    }

    fun destroy() {
        if (getBukkitEntity() == null) return
            getBukkitEntity().health = 20.0
            npc.despawn()
            npc.destroy()
            destroyed = true
    }

    fun hurt(fireTick: Boolean, critical: Boolean, sharpness: Boolean) {
        if (getBukkitEntity() == null) return
        getBukkitEntity().playEffect(EntityEffect.HURT)
        for(entity in getBukkitEntity().getNearbyEntities(100.0, 100.0, 100.0)) {
            if (entity !is Player) return
            getBukkitEntity().world.playSound(getBukkitEntity().location, Sound.HURT_FLESH, 0.7f, 1.0f)
        }
        if (fireTick) {
            getBukkitEntity().fireTicks = 20
        } else {
            val loc = getBukkitEntity().location.add(0.0, 1.0, 0.0)
            if (critical) {
                for (i in 0 until random.nextInt(5) + 10) {
                    loc.world.playEffect(loc, Effect.CRIT, 1)
                }
            }
            if (sharpness) {
                for (i in 0 until random.nextInt(5) + 10) {
                    loc.world.playEffect(loc, Effect.MAGIC_CRIT, 1)
                }
            }
        }
    }

    // Bot Settings
    enum class BotDifficulty(val reach: Double, val item: ItemStack) {
        EASY(
            2.5,
            ItemBuilder(Material.STAINED_GLASS_PANE).name("Easy").lore(listOf("Challenge the Easy Bot","Featuring 2.5 Reach")).build()
        ),
        MEDIUM(
            2.8, ItemBuilder(Material.STAINED_GLASS_PANE).name("Medium").lore(listOf("Challenge the Medium Bot","Featuring 2.8 Reach")).build()

        ),
        HARD(
            3.0, ItemBuilder(Material.STAINED_GLASS_PANE).name("Hard").lore(listOf("Challenge the Hard Bot","Featuring 3.0 Reach")).build()
        ),
        EXPERT(
            3.2, ItemBuilder(Material.STAINED_GLASS_PANE).name("Expert").lore(listOf("Challenge the Hard Bot","Featuring 3.2 Reach")).build()
        )

    }
}