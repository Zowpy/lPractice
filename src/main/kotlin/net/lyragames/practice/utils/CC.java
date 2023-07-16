package net.lyragames.practice.utils;

import net.lyragames.practice.PracticePlugin;
import org.apache.commons.lang.StringUtils;
import org.bukkit.ChatColor;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class CC {

    public static final String PRIMARY = ChatColor.valueOf(PracticePlugin.instance.settingsFile.getString("COLOR.PRIMARY")).toString();
    public static final String SECONDARY = ChatColor.valueOf(PracticePlugin.instance.settingsFile.getString("COLOR.SECONDARY")).toString();
    public static final String BLUE = ChatColor.BLUE.toString();
    public static final String RED = ChatColor.RED.toString();
    public static final String YELLOW = ChatColor.YELLOW.toString();
    public static final String GRAY = ChatColor.GRAY.toString();
    public static final String AQUA = ChatColor.AQUA.toString();
    public static final String GOLD = ChatColor.GOLD.toString();
    public static final String GREEN = ChatColor.GREEN.toString();
    public static final String WHITE = ChatColor.WHITE.toString();
    public static final String BLACK = ChatColor.BLACK.toString();
    public static final String BOLD = ChatColor.BOLD.toString();
    public static final String ITALIC = ChatColor.ITALIC.toString();
    public static final String UNDER_LINE = ChatColor.UNDERLINE.toString();
    public static final String STRIKE_THROUGH = ChatColor.STRIKETHROUGH.toString();
    public static final String RESET = ChatColor.RESET.toString();
    public static final String MAGIC = ChatColor.MAGIC.toString();
    public static final String DARK_BLUE = ChatColor.DARK_BLUE.toString();
    public static final String DARK_RED = ChatColor.DARK_RED.toString();
    public static final String DARK_GRAY = ChatColor.DARK_GRAY.toString();
    public static final String DARK_GREEN = ChatColor.DARK_GREEN.toString();
    public static final String DARK_PURPLE = ChatColor.DARK_PURPLE.toString();
    public static final String PINK = ChatColor.LIGHT_PURPLE.toString();
    public static final String DARK_AQUA = ChatColor.DARK_AQUA.toString();
    public static final String BLANK_LINE = "§a §b §c §d §e §f §0 §r";
    public static final String CHAT_BAR = ChatColor.translateAlternateColorCodes('&', "&7&m--------" + StringUtils.repeat("-", 37) + "--------");

    public static String translate(String in) {
        return ChatColor.translateAlternateColorCodes('&', in);
    }

    public static List<String> translate(List<String> lines) {
        return lines.stream().map(CC::translate).collect(Collectors.toList());
    }

    public static String untranslate(String in) {
        return in.replace("§", "&");
    }

    public static List<String> translate(String[] lines) {
        return Arrays.stream(lines).map(CC::translate).collect(Collectors.toList());
    }
}
