package com.example.whattoeat.presentation.recipe

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.whattoeat.R
import com.example.whattoeat.domain.model.Recipe
import com.example.whattoeat.presentation.common.AppButton
import com.example.whattoeat.presentation.common.AppCard
import com.example.whattoeat.presentation.common.EmptyStateScreen
import com.example.whattoeat.presentation.common.ErrorScreen
import com.example.whattoeat.presentation.common.LoadingScreen
import com.example.whattoeat.presentation.common.RecipeCard
import com.example.whattoeat.presentation.common.RecipeCardData
import com.example.whattoeat.presentation.theme.WhatToEatTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecipeListScreen(
    viewModel: RecipeListViewModel,
    onRecipeClick: (String) -> Unit
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    Column(modifier = Modifier.fillMaxSize()) {
        // Header
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = stringResource(R.string.recipe_list_title),
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = WhatToEatTheme.colorScheme.onSurface
            )
        }

        // Content
        when (state) {
            is RecipeListState.Loading -> LoadingScreen(modifier = Modifier.fillMaxSize())
            is RecipeListState.Error -> ErrorScreen(
                message = state.message,
                onRetry = { viewModel.retry() },
                modifier = Modifier.fillMaxSize()
            )
            is RecipeListState.Success -> {
                if (state.recipes.isEmpty()) {
                    EmptyStateScreen(
                        title = stringResource(R.string.recipe_list_empty),
                        subtitle = "Попробуй изменить параметры поиска",
                        icon = androidx.compose.material3.icons.filled.SearchOff,
                        actionText = stringResource(R.string.questionnaire_back),
                        onActionClick = { /* Navigate back to questionnaire */ },
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    RecipeListContent(
                        recipes = state.recipes,
                        onRecipeClick = onRecipeClick
                    )
                }
            }
            RecipeListState.Idle -> EmptyStateScreen(
                title = "Начни опрос",
                subtitle = "Ответь на вопросы, чтобы найти идеальный рецепт",
                icon = androidx.compose.material3.icons.filled.Quiz,
                actionText = "Подобрать рецепт",
                onActionClick = { /* Navigate to questionnaire */ },
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}

@Composable
fun RecipeListContent(
    recipes: List<Recipe>,
    onRecipeClick: (String) -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 16.dp, vertical = 8.dp)
    ) {
        items(recipes) { recipe ->
            RecipeCard(
                recipe = toCardData(recipe),
                onClick = { onRecipeClick(recipe.id) },
                onFavoriteClick = { /* TODO: Add to favorites */ }
            )
        }
    }
}

private fun toCardData(recipe: Recipe): RecipeCardData {
    return RecipeCardData(
        id = recipe.id,
        title = recipe.title,
        imageUrl = recipe.imageUrl,
        timeMinutes = recipe.timeMinutes,
        difficulty = when (recipe.difficulty) {
            com.example.whattoeat.domain.model.Difficulty.Easy -> "Очень просто"
            com.example.whattoeat.domain.model.Difficulty.Medium -> "Просто"
            com.example.whattoeat.domain.model.Difficulty.Hard -> "Средне"
        },
        matchPercent = 0, // Would be passed from view model
        tags = recipe.tags.take(3),
        isFavorite = false // Would check from favorites
    )
}