package com.example.whattoeat.presentation.recipe

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.whattoeat.R
import com.example.whattoeat.domain.model.Recipe
import com.example.whattoeat.presentation.common.AppButton
import com.example.whattoeat.presentation.common.ErrorScreen
import com.example.whattoeat.presentation.common.LoadingScreen
import com.example.whattoeat.presentation.theme.WhatToEatTheme
import coil.compose.AsyncImage
import coil.request.ImageRequest
import coil.size.OriginalSize

@Composable
fun RecipeDetailScreen(
    recipeId: String,
    viewModel: RecipeDetailViewModel
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    Column(modifier = Modifier.fillMaxSize()) {
        // Top App Bar
        TopAppBar(
            title = { Text("", fontSize = 20.sp, fontWeight = FontWeight.Medium) },
            navigationIcon = {
                IconButton(onClick = { /* NavController.popBackStack() */ }) {
                    Icon(
                        androidx.compose.material3.icons.filled.ArrowBack,
                        contentDescription = "Назад"
                    )
                }
            },
            actions = {
                IconButton(onClick = { /* Share */ }) {
                    Icon(androidx.compose.material3.icons.filled.Share, contentDescription = "Поделиться")
                }
                IconButton(onClick = { /* Favorite */ }) {
                    Icon(androidx.compose.material3.icons.filled.FavoriteBorder, contentDescription = "В избранное")
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = WhatToEatTheme.colorScheme.surface,
                titleContentColor = WhatToEatTheme.colorScheme.onSurface
            )
        )

        // Content
        when (state) {
            is RecipeDetailState.Loading -> LoadingScreen(modifier = Modifier.fillMaxSize())
            is RecipeDetailState.Error -> ErrorScreen(
                message = state.message,
                onRetry = { viewModel.loadRecipe(recipeId) },
                modifier = Modifier.fillMaxSize()
            )
            is RecipeDetailState.Success -> RecipeDetailContent(recipe = state.recipe)
            RecipeDetailState.Idle -> Box(modifier = Modifier.fillMaxSize())
        }
    }
}

@Composable
fun RecipeDetailContent(recipe: Recipe) {
    androidx.compose.foundation.layout.Box(
        modifier = Modifier.fillMaxSize()
    ) {
        androidx.compose.foundation.layout.Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(0.dp)
        ) {
            // Image with overlay
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(280.dp)
            ) {
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(recipe.imageUrl)
                        .crossfade(true)
                        .build(),
                    contentDescription = recipe.title,
                    contentScale = androidx.compose.ui.layout.ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )

                // Gradient overlay
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            androidx.compose.ui.graphics.Brush.verticalGradient(
                                0f to Color.Transparent,
                                1f to WhatToEatTheme.colorScheme.background.copy(alpha = 0.9f)
                            )
                        )
                )

                // Title overlay
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    contentAlignment = Alignment.BottomStart
                ) {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = recipe.title,
                            fontSize = 28.sp,
                            fontWeight = FontWeight.Bold,
                            color = WhatToEatTheme.colorScheme.onBackground
                        )
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            InfoChip(
                                icon = androidx.compose.material3.icons.filled.Schedule,
                                text = "${recipe.timeMinutes} мин"
                            )
                            InfoChip(
                                icon = androidx.compose.material3.icons.filled.Restaurant,
                                text = when (recipe.difficulty) {
                                    com.example.whattoeat.domain.model.Difficulty.Easy -> "Очень просто"
                                    com.example.whattoeat.domain.model.Difficulty.Medium -> "Просто"
                                    com.example.whattoeat.domain.model.Difficulty.Hard -> "Средне"
                                }
                            )
                            if (recipe.category != null) {
                                InfoChip(
                                    icon = androidx.compose.material3.icons.filled.Category,
                                    text = recipe.category!!
                                )
                            }
                        }
                    }
                }
            }

            // Scrollable content
            androidx.compose.foundation.layout.Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
                    .padding(bottom = 100.dp), // Space for bottom bar
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                // Ingredients
                SectionHeader(title = stringResource(R.string.recipe_ingredients))
                LazyColumn(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(recipe.ingredients) { ingredient ->
                        IngredientRow(ingredient = ingredient)
                    }
                }

                Divider()

                // Instructions
                SectionHeader(title = stringResource(R.string.recipe_instructions))
                LazyColumn(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(recipe.instructionSteps.withIndex()) { (index, step) ->
                        InstructionStep(number = index + 1, text = step)
                    }
                }

                // Tags
                if (recipe.tags.isNotEmpty()) {
                    Divider()
                    SectionHeader(title = "Теги")
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        recipe.tags.forEach { tag ->
                            Text(
                                text = tag,
                                fontSize = 12.sp,
                                color = WhatToEatTheme.colorScheme.primary,
                                modifier = Modifier
                                    .padding(horizontal = 12.dp, vertical = 6.dp)
                                    .background(
                                        WhatToEatTheme.colorScheme.primaryContainer,
                                        RoundedCornerShape(16.dp)
                                    )
                            )
                        }
                    }
                }

                // External links
                if (recipe.youtubeUrl != null || recipe.sourceUrl != null) {
                    Divider()
                    SectionHeader(title = "Источники")
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        recipe.youtubeUrl?.let { url ->
                            LinkRow(title = "YouTube видео", url = url, icon = androidx.compose.material3.icons.filled.PlayCircle)
                        }
                        recipe.sourceUrl?.let { url ->
                            LinkRow(title = "Источник рецепта", url = url, icon = androidx.compose.material3.icons.filled.Language)
                        }
                    }
                }
            }

            // Bottom bar with favorite/share
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .background(WhatToEatTheme.colorScheme.surface),
                contentAlignment = Alignment.Center
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    AppButton(
                        text = "В избранное",
                        onClick = { /* Add to favorites */ },
                        style = com.example.whattoeat.presentation.common.ButtonStyle.Outlined,
                        icon = androidx.compose.material3.icons.filled.FavoriteBorder,
                        modifier = Modifier.weight(1f)
                    )
                    AppButton(
                        text = "Поделиться",
                        onClick = { /* Share */ },
                        style = com.example.whattoeat.presentation.common.ButtonStyle.Primary,
                        icon = androidx.compose.material3.icons.filled.Share,
                        iconPosition = com.example.whattoeat.presentation.common.IconPosition.Start,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}

@Composable
fun SectionHeader(title: String) {
    Text(
        text = title,
        fontSize = 20.sp,
        fontWeight = FontWeight.Bold,
        color = WhatToEatTheme.colorScheme.onSurface
    )
}

@Composable
fun InfoChip(icon: androidx.compose.ui.graphics.vector.ImageVector, text: String) {
    Row(
        modifier = Modifier
            .padding(horizontal = 8.dp, vertical = 4.dp)
            .background(
                WhatToEatTheme.colorScheme.background.copy(alpha = 0.8f),
                RoundedCornerShape(12.dp)
            ),
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(imageVector = icon, contentDescription = null, tint = WhatToEatTheme.colorScheme.onBackground, modifier = Modifier.size(16.dp))
        Text(text = text, fontSize = 12.sp, fontWeight = FontWeight.Medium, color = WhatToEatTheme.colorScheme.onBackground)
    )
}

@Composable
fun IngredientRow(ingredient: com.example.whattoeat.domain.model.Ingredient) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Checkbox
        androidx.compose.material3.Checkbox(
            checked = false,
            onCheckedChange = { /* Toggle */ },
            colors = androidx.compose.material3.CheckboxDefaults.colors(
                checkedColor = WhatToEatTheme.colorScheme.primary,
                uncheckedColor = WhatToEatTheme.colorScheme.onSurfaceVariant
            )
        )

        // Ingredient info
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            Text(
                text = ingredient.name,
                fontSize = 16.sp,
                color = WhatToEatTheme.colorScheme.onSurface
            )
            if (ingredient.measure.isNotBlank()) {
                Text(
                    text = ingredient.measure,
                    fontSize = 14.sp,
                    color = WhatToEatTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
fun InstructionStep(number: Int, text: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.Top
    ) {
        // Step number
        Box(
            modifier = Modifier
                .size(28.dp)
                .background(WhatToEatTheme.colorScheme.primary, RoundedCornerShape(14.dp)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "$number",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = WhatToEatTheme.colorScheme.onPrimary
            )
        }

        // Step text
        Text(
            text = text,
            fontSize = 16.sp,
            color = WhatToEatTheme.colorScheme.onSurface,
            modifier = Modifier
                .weight(1f)
                .padding(top = 4.dp)
        )
    }
}

@Composable
fun LinkRow(title: String, url: String, icon: androidx.compose.ui.graphics.vector.ImageVector) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .combinedClickable(onClick = { /* Open URL */ }),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(imageVector = icon, contentDescription = null, tint = WhatToEatTheme.colorScheme.primary, modifier = Modifier.size(24.dp))
        Text(text = title, fontSize = 16.sp, color = WhatToEatTheme.colorScheme.primary, fontWeight = FontWeight.Medium, modifier = Modifier.weight(1f))
        Icon(androidx.compose.material3.icons.filled.ArrowForwardIos, contentDescription = null, tint = WhatToEatTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(20.dp))
    }
}

// Helper for LocalContext
import androidx.compose.ui.platform.LocalContext