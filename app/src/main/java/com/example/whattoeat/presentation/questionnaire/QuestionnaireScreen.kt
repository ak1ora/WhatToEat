package com.example.whattoeat.presentation.questionnaire

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
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OptIn
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.whattoeat.R
import com.example.whattoeat.domain.model.Answer
import com.example.whattoeat.domain.model.ChoiceOption
import com.example.whattoeat.domain.model.MultiChoiceAnswer
import com.example.whattoeat.domain.model.MultiChoiceQuestion
import com.example.whattoeat.domain.model.Question
import com.example.whattoeat.domain.model.RangeAnswer
import com.example.whattoeat.domain.model.RangeQuestion
import com.example.whattoeat.domain.model.SingleChoiceAnswer
import com.example.whattoeat.domain.model.SingleChoiceQuestion
import com.example.whattoeat.domain.model.TextAnswer
import com.example.whattoeat.domain.model.TextInputQuestion
import com.example.whattoeat.domain.model.TextInputType
import com.example.whattoeat.presentation.common.AppButton
import com.example.whattoeat.presentation.common.IconPosition
import com.example.whattoeat.presentation.theme.WhatToEatTheme
import kotlinx.coroutines.flow.collectAsStateWithLifecycle

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuestionnaireScreen(
    viewModel: QuestionnaireViewModel,
    onComplete: (com.example.whattoeat.domain.model.UserPreferences) -> Unit
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    when (state) {
        is QuestionnaireState.Loading -> LoadingScreen()
        is QuestionnaireState.Error -> ErrorScreen(message = state.message, onRetry = { viewModel.reset() })
        is QuestionnaireState.Question -> QuestionScreen(
            question = state.current,
            progress = state.progress,
            currentIndex = state.totalQuestions - (questionFlow?.questions.indexOf(state.current) ?: 0),
            totalQuestions = state.totalQuestions,
            onAnswer = { answer -> viewModel.onAnswer(answer) },
            onBack = { viewModel.onBack() }
        )
        is QuestionnaireState.Completed -> {
            onComplete(state.preferences)
        }
    }
}

// Need access to questionFlow for index calculation
@Composable
fun QuestionnaireScreen(
    viewModel: QuestionnaireViewModel,
    questionFlow: com.example.whattoeat.domain.model.QuestionFlow,
    onComplete: (com.example.whattoeat.domain.model.UserPreferences) -> Unit
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    when (state) {
        is QuestionnaireState.Loading -> LoadingScreen()
        is QuestionnaireState.Error -> ErrorScreen(message = state.message, onRetry = { viewModel.reset() })
        is QuestionnaireState.Question -> {
            val currentIndex = questionFlow.questions.indexOf(state.current) + 1
            QuestionScreen(
                question = state.current,
                progress = state.progress,
                currentIndex = currentIndex,
                totalQuestions = state.totalQuestions,
                onAnswer = { answer -> viewModel.onAnswer(answer) },
                onBack = { viewModel.onBack() }
            )
        }
        is QuestionnaireState.Completed -> {
            onComplete(state.preferences)
        }
    }
}

@Composable
fun QuestionScreen(
    question: Question,
    progress: Float,
    currentIndex: Int,
    totalQuestions: Int,
    onAnswer: (Answer) -> Unit,
    onBack: () -> Unit
) {
    val context = LocalContext.current

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(0.dp)
    ) {
        // Progress bar
        LinearProgressIndicator(
            progress = progress,
            modifier = Modifier.fillMaxWidth(),
            color = WhatToEatTheme.colorScheme.primary,
            trackColor = WhatToEatTheme.colorScheme.primaryContainer
        )

        // Progress text
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = stringResource(R.string.questionnaire_progress, currentIndex, totalQuestions),
                fontSize = 14.sp,
                color = WhatToEatTheme.colorScheme.onSurfaceVariant
            )
        }

        // Question content
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp, vertical = 24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Question title
            Text(
                text = question.title,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = WhatToEatTheme.colorScheme.onSurface
            )

            question.description?.let { desc ->
                Text(
                    text = desc,
                    fontSize = 14.sp,
                    color = WhatToEatTheme.colorScheme.onSurfaceVariant
                )
            }

            // Question input based on type
            when (question) {
                is SingleChoiceQuestion -> SingleChoiceInput(
                    question = question,
                    onAnswer = { optionId ->
                        onAnswer(SingleChoiceAnswer(questionId = question.id, optionId = optionId))
                    }
                )
                is MultiChoiceQuestion -> MultiChoiceInput(
                    question = question,
                    onAnswer = { optionIds ->
                        onAnswer(MultiChoiceAnswer(questionId = question.id, optionIds = optionIds))
                    }
                )
                is RangeQuestion -> RangeInput(
                    question = question,
                    onAnswer = { value ->
                        onAnswer(RangeAnswer(questionId = question.id, value = value))
                    }
                )
                is TextInputQuestion -> TextInput(
                    question = question,
                    onAnswer = { text ->
                        onAnswer(TextAnswer(questionId = question.id, text = text))
                    }
                )
            }

            // Navigation buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                if (currentIndex > 1) {
                    AppButton(
                        text = stringResource(R.string.questionnaire_back),
                        onClick = onBack,
                        style = com.example.whattoeat.presentation.common.ButtonStyle.Outlined,
                        modifier = Modifier.weight(1f)
                    )
                }

                AppButton(
                    text = if (currentIndex == totalQuestions)
                        stringResource(R.string.questionnaire_complete)
                    else
                        stringResource(R.string.questionnaire_next),
                    onClick = {
                        // Answer validation happens in the input components
                    },
                    style = com.example.whattoeat.presentation.common.ButtonStyle.Primary,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
fun SingleChoiceInput(
    question: SingleChoiceQuestion,
    onAnswer: (String) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(question.options) { option ->
            ChoiceCard(
                option = option,
                isSelected = false, // Would need state to track
                onClick = { onAnswer(option.id) }
            )
        }
    }
}

@Composable
fun ChoiceCard(
    option: ChoiceOption,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val colors = if (isSelected) {
        Pair(
            WhatToEatTheme.colorScheme.primaryContainer,
            WhatToEatTheme.colorScheme.onPrimaryContainer
        )
    } else {
        Pair(
            WhatToEatTheme.colorScheme.surface,
            WhatToEatTheme.colorScheme.onSurface
        )
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .combinedClickable(onClick = onClick),
        onClick = onClick,
        colors = androidx.compose.material3.CardDefaults.cardColors(
            containerColor = colors.first,
            contentColor = colors.second
        ),
        shape = WhatToEatTheme.shapes.medium,
        border = if (isSelected)
            androidx.compose.ui.graphics.drawscope.Stroke(
                width = 2.dp,
                color = WhatToEatTheme.colorScheme.primary
            )
        else
            null
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Icon or selection indicator
            if (option.icon != null) {
                Text(
                    text = option.icon!!,
                    fontSize = 24.sp
                )
            }

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                Text(
                    text = option.label,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    color = colors.second
                )
                option.description?.let { desc ->
                    Text(
                        text = desc,
                        fontSize = 12.sp,
                        color = colors.second.copy(alpha = 0.7f)
                    )
                }
            }

            // Selection indicator
            androidx.compose.material3.Icon(
                imageVector = if (isSelected)
                    androidx.compose.material3.icons.filled.CheckCircle
                else
                    androidx.compose.material3.icons.filled.RadioButtonUnchecked,
                contentDescription = null,
                tint = if (isSelected)
                    WhatToEatTheme.colorScheme.primary
                else
                    WhatToEatTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun MultiChoiceInput(
    question: MultiChoiceQuestion,
    onAnswer: (List<String>) -> Unit
) {
    // Simplified - would need state management for selections
    LazyColumn(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(question.options) { option ->
            MultiChoiceCard(
                option = option,
                isSelected = false,
                onToggle = { selected ->
                    // Would need to maintain selection state
                }
            )
        }
    }
}

@Composable
fun MultiChoiceCard(
    option: ChoiceOption,
    isSelected: Boolean,
    onToggle: (Boolean) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .combinedClickable(onClick = { onToggle(!isSelected) }),
        onClick = { onToggle(!isSelected) },
        colors = androidx.compose.material3.CardDefaults.cardColors(
            containerColor = if (isSelected)
                WhatToEatTheme.colorScheme.primaryContainer
            else
                WhatToEatTheme.colorScheme.surface,
            contentColor = if (isSelected)
                WhatToEatTheme.colorScheme.onPrimaryContainer
            else
                WhatToEatTheme.colorScheme.onSurface
        ),
        shape = WhatToEatTheme.shapes.medium
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (option.icon != null) {
                Text(text = option.icon!!, fontSize = 24.sp)
            }

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                Text(text = option.label, fontSize = 16.sp, fontWeight = FontWeight.Medium)
                option.description?.let { Text(text = it, fontSize = 12.sp, color = WhatToEatTheme.colorScheme.onSurfaceVariant) }
            }

            androidx.compose.material3.Icon(
                imageVector = if (isSelected)
                    androidx.compose.material3.icons.filled.CheckBox
                else
                    androidx.compose.material3.icons.filled.CheckBoxOutlineBlank,
                contentDescription = null,
                tint = if (isSelected)
                    WhatToEatTheme.colorScheme.primary
                else
                    WhatToEatTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun RangeInput(
    question: RangeQuestion,
    onAnswer: (Int) -> Unit
) {
    var value by androidx.compose.runtime.remember { androidx.compose.runtime.mutableStateOf(question.min) }

    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Labels
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            question.labels.entries.forEach { (key, label) ->
                Text(
                    text = label,
                    fontSize = 12.sp,
                    color = WhatToEatTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        // Slider
        androidx.compose.material3.Slider(
            value = value.toFloat(),
            onValueChange = { newValue ->
                val stepped = ((newValue - question.min) / question.step).roundToInt() * question.step + question.min
                value = stepped.coerceIn(question.min, question.max)
                onAnswer(value)
            },
            valueRange = question.min.toFloat()..question.max.toFloat(),
            steps = ((question.max - question.min) / question.step),
            colors = androidx.compose.material3.SliderDefaults.colors(
                thumbColor = WhatToEatTheme.colorScheme.primary,
                trackColor = WhatToEatTheme.colorScheme.primary,
                inactiveTrackColor = WhatToEatTheme.colorScheme.primaryContainer
            )
        )

        // Current value
        Text(
            text = "${value} ${question.unit ?: ""}",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = WhatToEatTheme.colorScheme.primary
        )
    }
}

@Composable
fun TextInput(
    question: TextInputQuestion,
    onAnswer: (String) -> Unit
) {
    var text by androidx.compose.runtime.remember { androidx.compose.runtime.mutableStateOf("") }

    TextField(
        value = text,
        onValueChange = { newText ->
            text = if (question.maxLength != null && newText.length > question.maxLength!!)
                newText.substring(0, question.maxLength!!)
            else
                newText
            onAnswer(text)
        },
        modifier = Modifier.fillMaxWidth(),
        label = { Text(text = question.hint ?: "") },
        placeholder = { Text(text = question.hint ?: "") },
        singleLine = question.inputType != TextInputType.Multiline,
        keyboardOptions = androidx.compose.ui.text.input.KeyboardOptions(
            keyboardType = when (question.inputType) {
                TextInputType.Text -> androidx.compose.ui.text.input.KeyboardType.Text
                TextInputType.Number -> androidx.compose.ui.text.input.KeyboardType.Number
                TextInputType.Email -> androidx.compose.ui.text.input.KeyboardType.Email
                TextInputType.Multiline -> androidx.compose.ui.text.input.KeyboardType.Text
            }
        ),
        keyboardActions = androidx.compose.ui.text.input.KeyboardActions(
            onDone = { /* Hide keyboard */ }
        ),
        colors = androidx.compose.material3.TextFieldDefaults.textFieldColors(
            containerColor = WhatToEatTheme.colorScheme.surface,
            focusedContainerColor = WhatToEatTheme.colorScheme.surface
        )
    )
}

@Composable
fun LoadingScreen() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        androidx.compose.material3.CircularProgressIndicator(
            modifier = Modifier.size(48.dp),
            color = WhatToEatTheme.colorScheme.primary,
            strokeWidth = 4.dp
        )
    }
}

@Composable
fun ErrorScreen(message: String, onRetry: () -> Unit) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            androidx.compose.material3.Icon(
                imageVector = androidx.compose.material3.icons.filled.ErrorOutline,
                contentDescription = null,
                tint = WhatToEatTheme.colorScheme.error,
                modifier = Modifier.size(64.dp)
            )
            Text(
                text = message,
                fontSize = 16.sp,
                color = WhatToEatTheme.colorScheme.onSurface,
                textAlign = TextAlign.Center
            )
            AppButton(
                text = stringResource(R.string.error_retry),
                onClick = onRetry,
                icon = androidx.compose.material3.icons.filled.Refresh
            )
        }
    }
}