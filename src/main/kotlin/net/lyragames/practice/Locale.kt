package net.lyragames.practice

import net.lyragames.practice.utils.CC

enum class Locale(s: String) {
    CANT_DO_THIS("cant-do-this"),
    CLICK_TO_JOIN("click-to-join"),
    CLICK_TO_ACCEPT("click-to-accept"),

    CANT_DUEL_YOURSELF("duel.cant-duel-yourself"),
    ONGOING_DUEL("duel.ongoing-duel"),
    BUSY_PLAYER("duel.busy-player"),
    DISABLED_DUELS("duel.duel-disabled"),
    INVALID_DUEL("duel.invalid-duel"),

    DUEL_REQUEST("duel.duel-request"),
    DUEL_REQUEST_FOOTER("duel.duel-request-footer"),

    NOT_IN_A_PARTY("party.not-in-a-party"),
    OTHER_NOT_IN_A_PARTY("party.other-not-in-a-party"),

    JOINED_PARTY("party.joined-party"),
    CANT_ACCEPT_PARTY_DUEL("party.cant-accept-duel-request"),
    ALREADY_IN_PARTY("party.already-in-party"),
    CREATED_PARTY("party.created-party"),
    DISBANDED_PARTY("party.disbanded-party"),
    LEFT_PARTY("party.left-party"),
    CANT_INVITE_YOURSELF("party.cant-invite-yourself"),
    PLAYER_ALREADY_IN_PARTY("party.player-already-in-party"),
    ALREADY_INVITED_PLAYER("party.already-invited-player"),
    PARTY_INVITED_MESSAGE("party.invited-message"),
    JOIN_OWN_PARTY("party.join-own-party"),
    ISNT_IN_PARTY("party.isnt-in-party"),
    BANNED_FROM_PARTY("party.banned-from-party"),
    NOT_INVITED("party.not-invited"),
    PARTY_EXPIRED("party.party-expired"),
    JOIN_PARTY_BROADCAST("party.joined-party-broadcast"),


    NO_ACTIVE_EVENTS("event.no-active-events"),
    EVENT_FULL("event.event-full"),
    ALREADY_IN_EVENT("event.already-in"),
    ALREADY_STARTED("event.already-started"),
    NOT_ENOUGH_PLAYER("event.not-enough-players"),
    NOT_IN_FFA("ffa.not-in-ffa"),
    LEFT_FFA("ffa.left-ffa"),

    COULDNT_FIND_INVENTORY("inventory.couldnt-find"),

    NOT_IN_A_MATCH("spectate.not-in-a-match"),
    SPECTATING_DISABLED("spectate.spectating-disabled"),

    PLAYER_DISCONNECTED("match.disconnected"),
    PLAYER_DIED("match.died-naturally"),
    PLAYED_KILLED("match.killed-by-player"),


    BREAK_OWN_BED("bedfights.break-own-bed"),
    BED_ALREADY_BROKEN("bedfights.bed-already-broken"),
    BED_DESTROYED("bedfights.bed-destroyed"),

    BEDFIGHTS_PLAYER_KILLED("bedfights.player-killed"),
    FINAL_TAG("bedfights.final-tag"),

    CANT_PLACE("build.cant-place"),

    FIREBALL_COOLDOWN("cooldown.fireball-cooldown-time"),

    ENDERPEARL_COOLDOWN_DONE("cooldown.enderpearl-cooldown-done"),

    ENDERPERL_COOLDOWN_TIME("cooldown.enderpearl-cooldown-time"),

    ELO_SEARCH("elo.search"),

    ALREADY_RATED("rating.already-rated"),
    THANK_YOU("rating.thank-you"),

    DISABLED_MAP_RATING("rating.disabled-rating"),

    CANT_FIND_KIT("exception.cant-find-kit"),

    CANT_FIND_ARENA("exception.cant-find-arena");




    val path: String;

    init {
       this.path = s;
    }



    fun getMessage(): String {

        return CC.translate(PracticePlugin.instance.languageFile.getString(path));
    }

    fun getNormalMessage(): String {
        return PracticePlugin.instance.languageFile.getString(path);

    }
}