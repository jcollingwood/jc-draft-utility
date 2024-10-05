package jc.draft.utility.league.espn

import kotlinx.serialization.Serializable

// espn api views
enum class EspnView(val value: String) {
    DraftDetail("mDraftDetail"),
    LiveScoring("mLiveScoring"),
    MatchupScore("mMatchupScore"),
    PendingTransactions("mPendingTransactions"),
    PositionalRatings("mPositionalRatings"),
    Roster("mRoster"),
    Settings("mSettings"),
    Team("mTeam"),
    Modular("modular"),
    Navl("mNavl")
}

// Json path to player info: teams[].roster.entries[].playerPoolEntry.player injured/injuryStatus/fullName
@Serializable
data class EspnResponse(val teams: List<EspnTeam>)

@Serializable
data class EspnTeam(val id: Int, val roster: EspnRoster? = null)

@Serializable
data class EspnRoster(val entries: List<EspnEntry> = emptyList())

@Serializable
data class EspnEntry(val playerPoolEntry: EspnPlayerPoolEntry? = null)

@Serializable
data class EspnPlayerPoolEntry(val player: EspnPlayer? = null)

@Serializable
data class EspnPlayer(
    val injured: Boolean? = false,
    val injuryStatus: String? = null,
    val fullName: String,
    val defaultPositionId: Integer
)

