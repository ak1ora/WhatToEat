package com.example.whattoeat.presentation.navigation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.getString
import androidx.navigation.compose.popUpTo
import androidx.navigation.compose.rememberNavController
import com.example.whattoeat.R
import com.example.whattoeat.presentation.common.AppButton
import com.example.whattoeat.presentation.common.EmptyStateScreen
import com.example.whattoeat.presentation.common.LoadingScreen
import com.example.whattoeat.presentation.common.ErrorScreen
import com.example.whattoeat.presentation.questionnaire.QuestionnaireScreen
import com.example.whattoeat.presentation.questionnaire.QuestionnaireViewModel
import com.example.whattoeat.presentation.recipe.RecipeDetailScreen
import com.example.whattoeat.presentation.recipe.RecipeDetailViewModel
import com.example.whattoeat.presentation.recipe.RecipeListScreen
import com.example.whattoeat.presentation.recipe.RecipeListViewModel
import com.example.whattoeat.presentation.settings.SettingsScreen
import com.example.whattoeat.presentation.settings.SettingsViewModel

// MARK: - Routes

sealed class Route {
    @Composable
    fun NavHostController.navigateTo(route: Route) {
        navigate(route.routeString)
    }

    val routeString: String
        get() = when (this) {
            is Route.Onboarding -> "onboarding"
            is Route.Questionnaire -> "questionnaire"
            is Route.RecipeList -> "recipe_list"
            is Route.RecipeDetail -> "recipe_detail/${this.recipeId}"
            is Route.Favorites -> "favorites"
            is Route.History -> "history"
            is Route.Settings -> "settings"
        }

    companion object {
        fun parse(route: String): Route = when {
            route == "onboarding" -> Route.Onboarding
            route == "questionnaire" -> Route.Questionnaire
            route == "recipe_list" -> Route.RecipeList
            route.startsWith("recipe_detail/") -> {
                val id = route.substringAfterLast("/")
                Route.RecipeDetail(id)
            }
            route == "favorites" -> Route.Favorites
            route == "history" -> Route.History
            route == "settings" -> Route.Settings
            else -> Route.Onboarding
        }
    }

    object Onboarding : Route()
    object Questionnaire : Route()
    object RecipeList : Route()
    data class RecipeDetail(val recipeId: String) : Route()
    object Favorites : Route()
    object History : Route()
    object Settings : Route()
}

// MARK: - NavGraph

@Composable
fun AppNavHost(
    modifier: androidx.compose.ui.Modifier = androidx.compose.ui.Modifier,
    startDestination: String = Route.Onboarding.routeString
) {
    val navController = rememberNavController()
    NavHost(navController, startDestination, modifier = modifier) {
        composable(Route.Onboarding.routeString) {
            OnboardingScreen(onStartClick = { navController.navigateTo(Route.Questionnaire) })
        }
        composable(Route.Questionnaire.routeString) {
            val viewModel = hiltViewModel<QuestionnaireViewModel>()
            QuestionnaireScreen(viewModel = viewModel) { preferences ->
                navController.navigateTo(Route.RecipeList)
            }
        }
        composable(
            route = Route.RecipeList.routeString,
            arguments = androidx.navigation.navArgument("preferences") { type = androidx.navigation.NavType.StringType }
        ) {
            val viewModel = hiltViewModel<RecipeListViewModel>()
            RecipeListScreen(viewModel = viewModel) { recipeId ->
                navController.navigateTo(Route.RecipeDetail(recipeId))
            }
        }
        composable(
            route = "recipe_detail/{recipeId}",
            arguments = listOf(androidx.navigation.navArgument("recipeId") { type = androidx.navigation.NavType.StringType })
        ) { backStackEntry ->
            val recipeId = backStackEntry.getString() ?: ""
            val viewModel = hiltViewModel<RecipeDetailViewModel>()
            RecipeDetailScreen(recipeId = recipeId, viewModel = viewModel)
        }
        composable(Route.Favorites.routeString) {
            FavoritesScreen()
        }
        composable(Route.History.routeString) {
            HistoryScreen()
        }
        composable(Route.Settings.routeString) {
            val viewModel = hiltViewModel<SettingsViewModel>()
            SettingsScreen(viewModel = viewModel)
        }
    }
}

// MARK: - Placeholder Screens

@Composable
fun OnboardingScreen(onStartClick: () -> Unit) {
    Column(
        modifier = Modifier.padding(24.dp).fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        androidx.compose.material3.Icon(
            androidx.compose.material3.icons.filled.RestaurantMenu,
            contentDescription = null,
            tint = androidx.compose.material3.MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(96.dp)
        )
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = stringResource(R.string.onboarding_title),
                fontSize = 28.sp,
                fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
                textAlign = TextAlign.Center,
                color = androidx.compose.material3.MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = stringResource(R.string.onboarding_subtitle),
                fontSize = 16.sp,
                textAlign = TextAlign.Center,
                color = androidx.compose.material3.MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        AppButton(
            text = stringResource(R.string.onboarding_start_button),
            onClick = onStartClick,
            fullWidth = true,
            icon = androidx.compose.material3.icons.filled.ArrowForward,
            iconPosition = com.example.whattoeat.presentation.common.IconPosition.End
        )
    }
}

@Composable
fun FavoritesScreen() {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        EmptyStateScreen(
            title = stringResource(R.string.favorites_title),
            subtitle = stringResource(R.string.favorites_empty),
            icon = androidx.compose.material3.icons.filled.FavoriteBorder
        )
    }
}

@Composable
fun HistoryScreen() {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        EmptyStateScreen(
            title = stringResource(R.string.history_title),
            subtitle = stringResource(R.string.history_empty),
            icon = androidx.compose.material3.icons.filled.History
        )
    }
}