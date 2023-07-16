package net.lyragames.practice.utils;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;

import java.util.ArrayList;
import java.util.List;

public class TextBuilder {

    private final TextComponent textComponent;
    private TextComponent latest;

    private List<TextComponent> parts = new ArrayList<>();

    public TextBuilder() {
        this.textComponent = new TextComponent();
        latest = textComponent;

        //parts.add(textComponent);
    }

    public TextBuilder setColor(ChatColor color) {
        latest.setColor(color);
        return this;
    }

    public TextBuilder setText(String text) {
        latest.setText(text);
        return this;
    }

    public TextBuilder setBold(boolean bold) {
        latest.setBold(bold);
        return this;
    }

    public TextBuilder setUnderline(boolean underline) {
        latest.setUnderlined(underline);
        return this;
    }

    public TextBuilder setClickEvent(ClickEvent clickEvent) {
        latest.setClickEvent(clickEvent);
        return this;
    }

    public TextBuilder setCommand(String command) {
        latest.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, command));
        return this;
    }

    public TextBuilder then() {
       latest = new TextComponent();
       parts.add(latest);

       return this;
    }

    public TextComponent build() {
        for (TextComponent part : parts) {
            textComponent.addExtra(part);
        }

        return this.textComponent;
    }

}
