package com.example.whattoeat.presentation.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.whattoeat.R
import com.example.whattoeat.presentation.common.AppButton
import com.example.whattoeat.presentation.theme.WhatToEatTheme

@Composable
fun SettingsScreen(viewModel: SettingsViewModel) {
    var themeMode by remember { mutableStateOf(viewModel.themeMode) }
    var apiKey by remember { mutableStateOf(viewModel.spoonacularApiKey) }

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(0.dp)
    ) {
        // Header
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = stringResource(R.string.settings_title),
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = WhatToEatTheme.colorScheme.onSurface
            )
        }

        // Settings content
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Theme section
            SettingsSection(title = stringResource(R.string.settings_theme)) {
                ThemeOption(
                    title = stringResource(R.string.settings_theme_system),
                    description = "Автоматически по настройкам системы",
                    isSelected = themeMode == 0,
                    onClick = { themeMode = 0 }
                )
                Divider()
                ThemeOption(
                    title = stringResource(R.string.settings_theme_light),
                    description = "Всегда светлая тема",
                    isSelected = themeMode == 1,
                    onClick = { themeMode = 1 }
                )
                Divider()
                ThemeOption(
                    title = stringResource(R.string.settings_theme_dark),
                    description = "Всегда тёмная тема",
                    isSelected = themeMode == 2,
                    onClick = { themeMode = 2 }
                )
            }

            Divider(modifier = Modifier.padding(vertical = 8.dp))

            // Language section
            SettingsSection(title = stringResource(R.string.settings_language)) {
                LanguageOption(
                    title = "Русский",
                    isSelected = true,
                    onClick = { /* Set language */ }
                )
                Divider()
                LanguageOption(
                    title = "English",
                    isSelected = false,
                    onClick = { /* Set language */ }
                )
            }

            Divider(modifier = Modifier.padding(vertical = 8.dp))

            // API Key section
            SettingsSection(title = stringResource(R.string.settings_spoonacular_key)) {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(
                        text = "API ключ для Spoonacular (опционально)",
                        fontSize = 14.sp,
                        color = WhatToEatTheme.colorScheme.onSurfaceVariant
                    )
                    TextField(
                        value = apiKey,
                        onValueChange = { apiKey = it },
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("API Key") },
                        placeholder = { Text("Введите ключ...") },
                        singleLine = true
                    )
                    AppButton(
                        text = "Сохранить",
                        onClick = { viewModel.setSpoonacularApiKey(apiKey) },
                        style = com.example.whattoeat.presentation.common.ButtonStyle.Primary
                    )
                }
            }

            Divider(modifier = Modifier.padding(vertical = 8.dp))

            // About section
            SettingsSection(title = stringResource(R.string.settings_about)) {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    AboutRow(title = stringResource(R.string.settings_version), value = "1.0.0")
                    AboutRow(title = "Библиотека рецептов", value = "TheMealDB")
                    AboutRow(title = "Иконки", value = "Material Icons")
                }
            }
        }
    }
}

@Composable
fun SettingsSection(
    title: String,
    content: @Composable () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = androidx.compose.material3.CardDefaults.cardColors(
            containerColor = WhatToEatTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = title,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = WhatToEatTheme.colorScheme.onSurface
            )
            content()
        }
    }
}

@Composable
fun ThemeOption(
    title: String,
    description: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp)
            .combinedClickable(onClick = onClick),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            Text(text = title, fontSize = 16.sp, color = WhatToEatTheme.colorScheme.onSurface)
            Text(text = description, fontSize = 12.sp, color = WhatToEatTheme.colorScheme.onSurfaceVariant)
        }
        if (isSelected) {
            Icon(
                imageVector = androidx.compose.material3.icons.filled.CheckCircle,
                contentDescription = null,
                tint = WhatToEatTheme.colorScheme.primary
            )
        }
    }
}

@Composable
fun LanguageOption(
    title: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp)
            .combinedClickable(onClick = onClick),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = title, fontSize = 16.sp, color = WhatToEatTheme.colorScheme.onSurface, modifier = Modifier.weight(1f))
        if (isSelected) {
            Icon(
                imageVector = androidx.compose.material3.icons.filled.Check,
                contentDescription = null,
                tint = WhatToEatTheme.colorScheme.primary
            )
        }
    }
}

@Composable
fun AboutRow(title: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = title, fontSize = 14.sp, color = WhatToEatTheme.colorScheme.onSurfaceVariant)
        Text(text = value, fontSize = 14.sp, color = WhatToEatTheme.colorScheme.onSurface)
    }
}