package com.example.whattoeat.domain.model

import kotlinx.serialization.Serializable

@Serializable
sealed interface Question {
    val id: String
    val title: String
    val description: String?
    val isRequired: Boolean
    val order: Int
}

@Serializable
data class ChoiceOption(
    val id: String,
    val label: String,
    val description: String? = null,
    val icon: String? = null,
    val metadata: Map<String, String> = emptyMap()
)

@Serializable
data class SingleChoiceQuestion(
    override val id: String,
    override val title: String,
    override val description: String? = null,
    override val isRequired: Boolean = true,
    override val order: Int,
    val options: List<ChoiceOption>,
    val allowCustom: Boolean = false
) : Question

@Serializable
data class MultiChoiceQuestion(
    override val id: String,
    override val title: String,
    override val description: String? = null,
    override val isRequired: Boolean = false,
    override val order: Int,
    val options: List<ChoiceOption>,
    val maxSelections: Int? = null,
    val minSelections: Int = 0,
    val allowCustom: Boolean = false
) : Question

@Serializable
data class RangeQuestion(
    override val id: String,
    override val title: String,
    override val description: String? = null,
    override val isRequired: Boolean = true,
    override val order: Int,
    val min: Int,
    val max: Int,
    val step: Int = 1,
    val unit: String? = null,
    val labels: Map<Int, String> = emptyMap()
) : Question

@Serializable
data class TextInputQuestion(
    override val id: String,
    override val title: String,
    override val description: String? = null,
    override val isRequired: Boolean = false,
    override val order: Int,
    val hint: String? = null,
    val maxLength: Int? = null,
    val inputType: TextInputType = TextInputType.Text
) : Question

enum class TextInputType {
    Text, Number, Email, Multiline
}

@Serializable
sealed interface Answer {
    val questionId: String
}

@Serializable
data class SingleChoiceAnswer(
    override val questionId: String,
    val optionId: String,
    val customValue: String? = null
) : Answer

@Serializable
data class MultiChoiceAnswer(
    override val questionId: String,
    val optionIds: List<String>,
    val customValues: List<String> = emptyList()
) : Answer

@Serializable
data class RangeAnswer(
    override val questionId: String,
    val value: Int
) : Answer

@Serializable
data class TextAnswer(
    override val questionId: String,
    val text: String
) : Answer

@Serializable
data class QuestionnaireState(
    val currentQuestion: Question?,
    val answeredQuestions: Map<String, Answer>,
    val progress: Float, // 0.0 - 1.0
    val isCompleted: Boolean,
    val preferences: UserPreferences?
)

@Serializable
data class QuestionFlow(
    val questions: List<Question>,
    val logic: DecisionTree
) {
    fun nextQuestion(answers: Map<String, Answer>): Question? {
        return logic.nextQuestion(questions, answers)
    }

    fun progress(answers: Map<String, Answer>): Float {
        val answeredRequired = questions.count { it.isRequired && answers.containsKey(it.id) }
        val totalRequired = questions.count { it.isRequired }
        return if (totalRequired > 0) answeredRequired.toFloat() / totalRequired else 1f
    }
}

interface DecisionTree {
    fun nextQuestion(allQuestions: List<Question>, answers: Map<String, Answer>): Question?
    fun computePreferences(answers: Map<String, Answer>): UserPreferences
}