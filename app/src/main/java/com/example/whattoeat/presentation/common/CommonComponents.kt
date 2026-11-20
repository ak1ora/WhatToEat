package com.example.whattoeat.presentation.common

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
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.icons.filled.ArrowBack
import androidx.compose.material3.icons.filled.ErrorOutline
import androidx.compose.material3.icons.filled.Refresh
import androidx.compose.material3.icons.filled.Search
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.whattoeat.R
import com.example.whattoeat.presentation.theme.WhatToEatTheme
import coil.compose.AsyncImage
import coil.request.ImageRequest
import coil.size.OriginalSize

// MARK: - Buttons

@Composable
fun AppButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    icon: ImageVector? = null,
    iconPosition: IconPosition = IconPosition.Start,
    style: ButtonStyle = ButtonStyle.Primary,
    fullWidth: Boolean = false
) {
    val colors = when (style) {
        ButtonStyle.Primary -> androidx.compose.material3.ButtonDefaults.buttonColors(
            containerColor = androidx.compose.material3.MaterialTheme.colorScheme.primary,
            contentColor = androidx.compose.material3.MaterialTheme.colorScheme.onPrimary
        )
        ButtonStyle.Secondary -> androidx.compose.material3.ButtonDefaults.buttonColors(
            containerColor = androidx.compose.material3.MaterialTheme.colorScheme.secondaryContainer,
            contentColor = androidx.compose.material3.MaterialTheme.colorScheme.onSecondaryContainer
        )
        ButtonStyle.Outlined -> androidx.compose.material3.ButtonDefaults.outlinedButtonColors(
            contentColor = androidx.compose.material3.MaterialTheme.colorScheme.primary,
            borderColor = androidx.compose.material3.MaterialTheme.colorScheme.primary
        )
        ButtonStyle.Text -> androidx.compose.material3.ButtonDefaults.textButtonColors(
            contentColor = androidx.compose.material3.MaterialTheme.colorScheme.primary
        )
    }

    val modifierFinal = if (fullWidth) modifier.fillMaxWidth() else modifier

    Button(
        onClick = onClick,
        modifier = modifierFinal,
        enabled = enabled,
        colors = colors,
        shape = androidx.compose.material3.MaterialTheme.shapes.medium
    ) {
        Row(
            modifier = androidx.compose.foundation.layout.padding(
                horizontal = if (icon != null) 16.dp else 24.dp,
                vertical = 12.dp
            ),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (icon != null && iconPosition == IconPosition.Start) {
                Icon(icon, contentDescription = null, tint = colors.contentColor().value)
            }
            Text(
                text = text,
                fontWeight = FontWeight.Medium,
                fontSize = 16.sp
            )
            if (icon != null && iconPosition == IconPosition.End) {
                Icon(icon, contentDescription = null, tint = colors.contentColor().value)
            }
        }
    }
}

enum class ButtonStyle { Primary, Secondary, Outlined, Text }
enum class IconPosition { Start, End }

// MARK: - Cards

@Composable
fun AppCard(
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    elevation: androidx.compose.material3.CardElevation = CardDefaults.cardElevation(
        defaultElevation = 2.dp,
        pressedElevation = 4.dp
    ),
    shape: androidx.compose.ui.graphics.Shape = androidx.compose.material3.MaterialTheme.shapes.medium,
    content: @Composable () -> Unit
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clip(shape)
            .let { if (onClick != null) it.combinedClickable(onClick = onClick) else it },
        onClick = onClick,
        elevation = elevation,
        shape = shape,
        colors = CardDefaults.cardColors(
            containerColor = androidx.compose.material3.MaterialTheme.colorScheme.surface,
            contentColor = androidx.compose.material3.MaterialTheme.colorScheme.onSurface
        )
    ) {
        content()
    }
}

// MARK: - Recipe Card

@Composable
fun RecipeCard(
    recipe: RecipeCardData,
    onClick: () -> Unit,
    onFavoriteClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    AppCard(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        onClick = onClick
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Image
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(recipe.imageUrl)
                    .crossfade(true)
                    .build(),
                contentDescription = recipe.title,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(80.dp)
                    .clip(RoundedCornerShape(8.dp))
                error = {
                    Box(
                        modifier = Modifier.size(80.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(androidx.compose.material3.MaterialTheme.colorScheme.surfaceVariant),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            androidx.compose.material3.icons.filled.Restaurant,
                            contentDescription = null,
                            tint = androidx.compose.material3.MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            )

            // Content
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = recipe.title,
                    maxLines = 2,
                    overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    color = androidx.compose.material3.MaterialTheme.colorScheme.onSurface
                )

                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Time
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            androidx.compose.material3.icons.filled.Schedule,
                            contentDescription = null,
                            tint = androidx.compose.material3.MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = "${recipe.timeMinutes} мин",
                            fontSize = 12.sp,
                            color = androidx.compose.material3.MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    // Difficulty
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            androidx.compose.material3.icons.filled.TrendingUp,
                            contentDescription = null,
                            tint = androidx.compose.material3.MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = recipe.difficulty,
                            fontSize = 12.sp,
                            color = androidx.compose.material3.MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    // Match %
                    if (recipe.matchPercent > 0) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                androidx.compose.material3.icons.filled.CheckCircle,
                                contentDescription = null,
                                tint = androidx.compose.material3.MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(16.dp)
                            )
                            Text(
                                text = "${recipe.matchPercent}%",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium,
                                color = androidx.compose.material3.MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }

                // Tags
                if (recipe.tags.isNotEmpty()) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        recipe.tags.take(3).forEach { tag ->
                            Text(
                                text = tag,
                                fontSize = 10.sp,
                                color = androidx.compose.material3.MaterialTheme.colorScheme.primary,
                                modifier = Modifier
                                    .padding(horizontal = 8.dp, vertical = 2.dp)
                                    .background(
                                        androidx.compose.material3.MaterialTheme.colorScheme.primaryContainer,
                                        RoundedCornerShape(4.dp)
                                    )
                            )
                        }
                        if (recipe.tags.size > 3) {
                            Text(
                                text = "+${recipe.tags.size - 3}",
                                fontSize = 10.sp,
                                color = androidx.compose.material3.MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }

            // Favorite button
            onFavoriteClick?.let {
                IconButton(onClick = it) {
                    Icon(
                        if (recipe.isFavorite)
                            androidx.compose.material3.icons.filled.Favorite
                        else
                            androidx.compose.material3.icons.filled.FavoriteBorder,
                        contentDescription = if (recipe.isFavorite) "Убрать из избранного" else "Добавить в избранное",
                        tint = if (recipe.isFavorite)
                            androidx.compose.material3.MaterialTheme.colorScheme.error
                        else
                            androidx.compose.material3.MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

data class RecipeCardData(
    val id: String,
    val title: String,
    val imageUrl: String,
    val timeMinutes: Int,
    val difficulty: String,
    val matchPercent: Int,
    val tags: List<String>,
    val isFavorite: Boolean = false
)

// MARK: - Loading Screen

@Composable
fun LoadingScreen(
    message: String = stringResource(R.string.loading),
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            CircularProgressIndicator(
                modifier = Modifier.size(48.dp),
                color = androidx.compose.material3.MaterialTheme.colorScheme.primary,
                strokeWidth = 4.dp
            )
            Text(
                text = message,
                fontSize = 16.sp,
                color = androidx.compose.material3.MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

// MARK: - Error Screen

@Composable
fun ErrorScreen(
    message: String = stringResource(R.string.error_unknown),
    onRetry: () -> Unit,
    modifier: Modifier = Modifier,
    showRetry: Boolean = true
) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Icon(
                imageVector = androidx.compose.material3.icons.filled.ErrorOutline,
                contentDescription = null,
                tint = androidx.compose.material3.MaterialTheme.colorScheme.error,
                modifier = Modifier.size(64.dp)
            )
            Text(
                text = message,
                fontSize = 16.sp,
                color = androidx.compose.material3.MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Center
            )
            if (showRetry) {
                AppButton(
                    text = stringResource(R.string.error_retry),
                    onClick = onRetry,
                    style = ButtonStyle.Primary,
                    icon = androidx.compose.material3.icons.filled.Refresh
                )
            }
        }
    }
}

// MARK: - Empty State

@Composable
fun EmptyStateScreen(
    title: String,
    subtitle: String? = null,
    icon: ImageVector = androidx.compose.material3.icons.filled.Search,
    actionText: String? = null,
    onActionClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = androidx.compose.material3.MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                modifier = Modifier.size(80.dp)
            )
            Text(
                text = title,
                fontSize = 20.sp,
                fontWeight = FontWeight.Medium,
                color = androidx.compose.material3.MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Center
            )
            subtitle?.let {
                Text(
                    text = it,
                    fontSize = 14.sp,
                    color = androidx.compose.material3.MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )
            }
            actionText?.let { text ->
                onActionClick?.let { click ->
                    AppButton(
                        text = text,
                        onClick = click,
                        style = ButtonStyle.Primary,
                        icon = androidx.compose.material3.icons.filled.Search
                    )
                }
            }
        }
    }
}

// MARK: - Top App Bar

@Composable
fun AppTopAppBar(
    title: String,
    modifier: Modifier = Modifier,
    onBackPress: (() -> Unit)? = null,
    actions: @Composable RowScope.() -> Unit = {}
) {
    androidx.compose.material3.TopAppBar(
        modifier = modifier,
        title = { Text(text = title, fontSize = 20.sp, fontWeight = FontWeight.Medium) },
        navigationIcon = onBackPress?.let {
            {
                IconButton(onClick = it) {
                    Icon(
                        androidx.compose.material3.icons.filled.ArrowBack,
                        contentDescription = "Назад"
                    )
                }
            }
        },
        actions = actions,
        colors = androidx.compose.material3.TopAppBarDefaults.topAppBarColors(
            containerColor = androidx.compose.material3.MaterialTheme.colorScheme.surface,
            titleContentColor = androidx.compose.material3.MaterialTheme.colorScheme.onSurface
        )
    )
}

// MARK: - Helper for LocalContext
import androidx.compose.ui.platform.LocalContext