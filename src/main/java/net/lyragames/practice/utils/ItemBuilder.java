package net.lyragames.practice.utils;

import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.material.MaterialData;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ItemBuilder {
    private final ItemStack is;

    public ItemBuilder(Material mat) {
        this.is = new ItemStack(mat);
    }

    public ItemBuilder(ItemStack is) {
        this.is = is;
    }

    public ItemBuilder amount(int amount) {
        this.is.setAmount(amount);
        return this;
    }

    public ItemBuilder name(String name) {
        ItemMeta meta = this.is.getItemMeta();
        meta.setDisplayName(CC.translate(name));
        this.is.setItemMeta(meta);
        return this;
    }

    public ItemBuilder lore(String name) {
        ItemMeta meta = this.is.getItemMeta();
        List<String> lore = meta.getLore();
        if (lore == null) {
            lore = new ArrayList();
        }

        ((List)lore).add(CC.translate(name));
        meta.setLore((List)lore);
        this.is.setItemMeta(meta);
        return this;
    }

    public ItemBuilder lore(List<String> lore) {
        List<String> toSet = new ArrayList();
        ItemMeta meta = this.is.getItemMeta();
        Iterator var4 = lore.iterator();

        while(var4.hasNext()) {
            String string = (String)var4.next();
            toSet.add(CC.translate(string));
        }

        meta.setLore(toSet);
        this.is.setItemMeta(meta);
        return this;
    }

    public ItemBuilder durability(int durability) {
        this.is.setDurability((short)durability);
        return this;
    }

    /** @deprecated */
    @Deprecated
    public ItemBuilder data(int data) {
        this.is.setData(new MaterialData(this.is.getType(), (byte)data));
        return this;
    }

    public ItemBuilder enchantment(Enchantment enchantment, int level) {
        this.is.addUnsafeEnchantment(enchantment, level);
        return this;
    }

    public ItemBuilder enchantment(Enchantment enchantment) {
        this.is.addUnsafeEnchantment(enchantment, 1);
        return this;
    }

    public ItemBuilder type(Material material) {
        this.is.setType(material);
        return this;
    }

    public ItemBuilder clearLore() {
        ItemMeta meta = this.is.getItemMeta();
        meta.setLore(new ArrayList());
        this.is.setItemMeta(meta);
        return this;
    }

    public ItemBuilder clearEnchantments() {
        Iterator var1 = this.is.getEnchantments().keySet().iterator();

        while(var1.hasNext()) {
            Enchantment e = (Enchantment)var1.next();
            this.is.removeEnchantment(e);
        }

        return this;
    }

    public ItemBuilder color(Color color) {
        if (this.is.getType() != Material.LEATHER_BOOTS && this.is.getType() != Material.LEATHER_CHESTPLATE && this.is.getType() != Material.LEATHER_HELMET && this.is.getType() != Material.LEATHER_LEGGINGS) {
            throw new IllegalArgumentException("color() only applicable for leather armor!");
        } else {
            LeatherArmorMeta meta = (LeatherArmorMeta)this.is.getItemMeta();
            meta.setColor(color);
            this.is.setItemMeta(meta);
            return this;
        }
    }

    public ItemBuilder addFlags(ItemFlag... itemFlags) {
        ItemMeta meta = this.is.getItemMeta();
        meta.addItemFlags(itemFlags);
        this.is.setItemMeta(meta);
        return this;
    }

    public ItemBuilder setUnbreakable(boolean unbreakable) {
        ItemMeta meta = this.is.getItemMeta();
        meta.spigot().setUnbreakable(unbreakable);
        this.is.setItemMeta(meta);
        return this;
    }

    public SkullBuilder skullBuilder() {
        return new SkullBuilder(this);
    }

    public ItemStack build() {
        return this.is;
    }
}