package net.lyragames.practice.utils.title;

import net.minecraft.server.v1_8_R3.ChatComponentText;
import net.minecraft.server.v1_8_R3.EntityPlayer;
import net.minecraft.server.v1_8_R3.PacketPlayOutTitle;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

public class TitleBar {

    public static void sendTitleBar(Player player, String title, String subtitle, int fadeIn, int stay, int fadeOut) {
        EntityPlayer entityPlayer = ((CraftPlayer) player).getHandle();

        PacketPlayOutTitle reset = new PacketPlayOutTitle(PacketPlayOutTitle.EnumTitleAction.RESET, null);
        entityPlayer.playerConnection.sendPacket(reset);

        PacketPlayOutTitle times = new PacketPlayOutTitle(fadeIn, stay, fadeOut);
        entityPlayer.playerConnection.sendPacket(times);


        if (title != null) {
            PacketPlayOutTitle packetTitle = new PacketPlayOutTitle(PacketPlayOutTitle.EnumTitleAction.TITLE, new ChatComponentText(title));
            entityPlayer.playerConnection.sendPacket(packetTitle);
        }

        if (subtitle != null) {
            PacketPlayOutTitle packetSubtitle = new PacketPlayOutTitle(PacketPlayOutTitle.EnumTitleAction.SUBTITLE, new ChatComponentText(subtitle));
            entityPlayer.playerConnection.sendPacket(packetSubtitle);
        }
    }
}
