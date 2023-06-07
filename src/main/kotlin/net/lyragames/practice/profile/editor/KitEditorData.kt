package net.lyragames.practice.profile.editor

import net.lyragames.practice.kit.EditedKit
import net.lyragames.practice.kit.Kit


/**
 * This Project is property of Zowpy © 2022
 * Redistribution of this Project is not allowed
 *
 * @author Zowpy
 * Created: 2/20/2022
 * Project: lPractice
 */

class KitEditorData {

    var kit: Kit? = null
    var selectedKit: EditedKit? = null
    var active = false
    var rename = false

    fun isRenaming(): Boolean {
        return active && rename && selectedKit != null
    }
}