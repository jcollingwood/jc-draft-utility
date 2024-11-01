package jc.draft.utility.api.rosters

import io.ktor.client.HttpClient
import jc.draft.utility.league.FantasyLeague
import jc.draft.utility.league.FantasyPlatformFactory
import jc.draft.utility.league.LeagueConfig
import jc.draft.utility.league.LeaguePlatform
import jc.draft.utility.league.sleeper.SleeperPlayerService

class LeagueService(
    val httpClient: HttpClient,
    val sleeperPlayerService: SleeperPlayerService,
    private val platformFactory: FantasyPlatformFactory = FantasyPlatformFactory(
        httpClient = httpClient,
        sleeperPlayerService = sleeperPlayerService
    )
) {

    /**
     * fetches FantasyLeague and roster data based on leagueConfig provided
     *
     * fetches data fresh if fetchNew is true otherwise uses caching mechanism
     *
     * refetches cached sleeper player data if refetchPlayers is true
     */
    fun getLeagueRoster(
        leagueConfig: LeagueConfig,
        fetchNew: Boolean = false,
        refetchPlayers: Boolean = false
    ): FantasyLeague {
        // refetch sleeper player data if triggered
        if (leagueConfig.leaguePlatform == LeaguePlatform.SLEEPER && refetchPlayers)
            sleeperPlayerService.getPlayers(fetchNew = true)

        val platform = platformFactory.getPlatform(leagueConfig.leaguePlatform)

        return platform.getLeaguePlayers(
            leagueConfig = leagueConfig,
            fetchNew = fetchNew
        )
    }
}