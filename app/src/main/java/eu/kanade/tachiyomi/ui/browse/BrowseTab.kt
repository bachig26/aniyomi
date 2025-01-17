package eu.kanade.tachiyomi.ui.browse

import androidx.compose.animation.graphics.res.animatedVectorResource
import androidx.compose.animation.graphics.res.rememberAnimatedVectorPainter
import androidx.compose.animation.graphics.vector.AnimatedImageVector
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import cafe.adriel.voyager.core.model.rememberScreenModel
import cafe.adriel.voyager.navigator.tab.LocalTabNavigator
import cafe.adriel.voyager.navigator.tab.TabOptions
import eu.kanade.presentation.components.TabbedScreen
import eu.kanade.presentation.util.Tab
import eu.kanade.tachiyomi.R
import eu.kanade.tachiyomi.ui.browse.anime.extension.AnimeExtensionsScreenModel
import eu.kanade.tachiyomi.ui.browse.anime.extension.animeExtensionsTab
import eu.kanade.tachiyomi.ui.browse.anime.migration.sources.migrateAnimeSourceTab
import eu.kanade.tachiyomi.ui.browse.anime.source.animeSourcesTab
import eu.kanade.tachiyomi.ui.browse.manga.extension.MangaExtensionsScreenModel
import eu.kanade.tachiyomi.ui.browse.manga.extension.mangaExtensionsTab
import eu.kanade.tachiyomi.ui.browse.manga.migration.sources.migrateMangaSourceTab
import eu.kanade.tachiyomi.ui.browse.manga.source.mangaSourcesTab
import eu.kanade.tachiyomi.ui.main.MainActivity
import eu.kanade.tachiyomi.util.storage.DiskUtil

data class BrowseTab(
    private val toExtensions: Boolean = false,
) : Tab {

    override val options: TabOptions
        @Composable
        get() {
            val isSelected = LocalTabNavigator.current.current.key == key
            val image = AnimatedImageVector.animatedVectorResource(R.drawable.anim_browse_enter)
            return TabOptions(
                index = 3u,
                title = stringResource(R.string.browse),
                icon = rememberAnimatedVectorPainter(image, isSelected),
            )
        }

    @Composable
    override fun Content() {
        val context = LocalContext.current

        // Hoisted for extensions tab's search bar
        val mangaExtensionsScreenModel = rememberScreenModel { MangaExtensionsScreenModel() }
        val mangaExtensionsQuery by mangaExtensionsScreenModel.query.collectAsState()

        val animeExtensionsScreenModel = rememberScreenModel { AnimeExtensionsScreenModel() }
        val animeExtensionsQuery by animeExtensionsScreenModel.query.collectAsState()

        TabbedScreen(
            titleRes = R.string.browse,
            tabs = listOf(
                animeSourcesTab(),
                mangaSourcesTab(),
                animeExtensionsTab(animeExtensionsScreenModel),
                mangaExtensionsTab(mangaExtensionsScreenModel),
                migrateAnimeSourceTab(),
                migrateMangaSourceTab(),
            ),
            startIndex = 2.takeIf { toExtensions },
            mangaSearchQuery = mangaExtensionsQuery,
            onChangeMangaSearchQuery = mangaExtensionsScreenModel::search,
            animeSearchQuery = animeExtensionsQuery,
            onChangeAnimeSearchQuery = animeExtensionsScreenModel::search,
            scrollable = true,
        )

        // For local source
        DiskUtil.RequestStoragePermission()

        LaunchedEffect(Unit) {
            (context as? MainActivity)?.ready = true
        }
    }
}
