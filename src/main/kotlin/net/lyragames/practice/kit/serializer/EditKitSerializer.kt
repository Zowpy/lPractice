package net.lyragames.practice.kit.serializer

import com.google.gson.*
import net.lyragames.llib.utils.InventoryUtil
import net.lyragames.practice.kit.EditedKit
import java.lang.reflect.Type


/**
 * This Project is property of Zowpy © 2022
 * Redistribution of this Project is not allowed
 *
 * @author Zowpy
 * Created: 2/21/2022
 * Project: lPractice
 */

object EditKitSerializer: JsonSerializer<EditedKit>, JsonDeserializer<EditedKit> {

    override fun serialize(p0: EditedKit?, p1: Type?, p2: JsonSerializationContext?): JsonElement {
        val jsonObject = JsonObject()

        if (p0 == null) {
            return jsonObject
        }

        jsonObject.addProperty("name", p0.name)
        jsonObject.addProperty("originalKit", p0.originalKit)
        jsonObject.addProperty("content", InventoryUtil.serializeInventory(p0.content))
        jsonObject.addProperty("armorContent", InventoryUtil.serializeInventory(p0.armorContent))

        return jsonObject
    }

    override fun deserialize(p0: JsonElement, p1: Type?, p2: JsonDeserializationContext?): EditedKit? {
        val jsonObject = p0.asJsonObject

        if (jsonObject.isJsonNull) return null
        if (jsonObject?.entrySet()?.isEmpty()!!) return null

        val editedKit = EditedKit(jsonObject.get("name").asString)
        editedKit.originalKit = jsonObject.get("originalKit").asString
        editedKit.content = InventoryUtil.deserializeInventory(jsonObject.get("content").asString)
        editedKit.armorContent = InventoryUtil.deserializeInventory(jsonObject.get("armorContent").asString)

        return editedKit
    }
}