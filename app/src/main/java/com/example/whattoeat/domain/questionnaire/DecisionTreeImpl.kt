package com.example.whattoeat.domain.questionnaire

import com.example.whattoeat.domain.model.Answer
import com.example.whattoeat.domain.model.DecisionTree
import com.example.whattoeat.domain.model.Diet
import com.example.whattoeat.domain.model.Difficulty
import com.example.whattoeat.domain.model.MealType
import com.example.whattoeat.domain.model.Question
import com.example.whattoeat.domain.model.RangeQuestion
import com.example.whattoeat.domain.model.SingleChoiceAnswer
import com.example.whattoeat.domain.model.SingleChoiceQuestion
import com.example.whattoeat.domain.model.TextInputQuestion
import com.example.whattoeat.domain.model.UserPreferences
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DecisionTreeImpl @Inject constructor() : DecisionTree {

    override fun nextQuestion(allQuestions: List<Question>, answers: Map<String, Answer>): Question? {
        val answeredIds = answers.keys.toSet()
        val requiredQuestions = allQuestions.filter { it.isRequired }.sortedBy { it.order }

        for (question in requiredQuestions) {
            if (question.id !in answeredIds && shouldShowQuestion(question, answers)) {
                return question
            }
        }

        // Check optional questions
        val optionalQuestions = allQuestions.filter { !it.isRequired }.sortedBy { it.order }
        for (question in optionalQuestions) {
            if (question.id !in answeredIds && shouldShowQuestion(question, answers)) {
                return question
            }
        }

        return null
    }

    private fun shouldShowQuestion(question: Question, answers: Map<String, Answer>): Boolean {
        return when (question.id) {
            "q_difficulty" -> {
                val timeAnswer = answers["q_time"] as? RangeAnswer
                timeAnswer?.value != null && timeAnswer.value < 15 // Only show if < 15 min
            }
            "q_cuisine" -> {
                val mealType = getMealType(answers)
                mealType != null // Show if meal type selected
            }
            "q_ingredients" -> true // Always show after cuisine
            "q_exclusions" -> true // Always show
            "q_extra" -> true // Always show (optional)
            else -> true
        }
    }

    private fun getMealType(answers: Map<String, Answer>): MealType? {
        val answer = answers["q_meal_type"] as? SingleChoiceAnswer
        return answer?.optionId?.let { MealType.valueOf(it) }
    }

    override fun computePreferences(answers: Map<String, Answer>): UserPreferences {
        val prefs = UserPreferences()

        // Meal type
        val mealTypeAnswer = answers["q_meal_type"] as? SingleChoiceAnswer
        val mealType = mealTypeAnswer?.optionId?.let { MealType.valueOf(it) }

        // Max time
        val timeAnswer = answers["q_time"] as? RangeAnswer
        val maxTime = timeAnswer?.value

        // Difficulty
        val difficultyAnswer = answers["q_difficulty"] as? SingleChoiceAnswer
        val difficulty = difficultyAnswer?.optionId?.let { Difficulty.valueOf(it) }

        // Cuisine
        val cuisineAnswer = answers["q_cuisine"] as? SingleChoiceAnswer
        val cuisine = cuisineAnswer?.optionId

        // Available ingredients
        val ingredientsAnswer = answers["q_ingredients"] as? MultiChoiceAnswer
        val availableIngredients = ingredientsAnswer?.optionIds ?: emptyList()

        // Excluded ingredients
        val exclusionsAnswer = answers["q_exclusions"] as? MultiChoiceAnswer
        val excludedIngredients = exclusionsAnswer?.optionIds ?: emptyList()

        // Extra preferences
        val extraAnswer = answers["q_extra"] as? TextInputAnswer
        val diet = parseDietFromExtra(extraAnswer?.text)
        val (maxCalories, minProtein) = parseNutrientsFromExtra(extraAnswer?.text)

        return prefs.copyWith(
            mealType = mealType,
            maxTimeMinutes = maxTime,
            difficulty = difficulty,
            cuisine = cuisine,
            availableIngredients = availableIngredients,
            excludedIngredients = excludedIngredients,
            diet = diet,
            maxCalories = maxCalories,
            minProtein = minProtein
        )
    }

    private fun parseDietFromExtra(text: String?): Diet? {
        text?.lowercase()?.let { t ->
            when {
                t.contains("веган") || t.contains("vegan") -> Diet.Vegan
                t.contains("вегетариан") || t.contains("vegetarian") -> Diet.Vegetarian
                t.contains("кето") || t.contains("keto") -> Diet.Keto
                t.contains("низкоуглевод") || t.contains("low carb") -> Diet.LowCarb
                t.contains("без глютен") || t.contains("gluten free") -> Diet.GlutenFree
                t.contains("без лактоз") || t.contains("dairy free") -> Diet.DairyFree
                t.contains("высокобелков") || t.contains("high protein") -> Diet.HighProtein
                else -> null
            }
        }
    }

    private fun parseNutrientsFromExtra(text: String?): Pair<Int?, Double?> {
        // Simple parsing: "до 500 ккал, минимум 30г белка"
        var maxCalories: Int? = null
        var minProtein: Double? = null

        text?.lowercase()?.let { t ->
            // calories
            val calMatch = "\\d+\\s*ккал".toRegex().find(t)
            calMatch?.let { maxCalories = it.value.replace("\\D".toRegex(), "").toIntOrNull() }

            // protein
            val protMatch = "\\d+\\s*г\\s*белк".toRegex().find(t)
            protMatch?.let { minProtein = it.value.replace("\\D".toRegex(), "").toDoubleOrNull() }
        }

        return maxCalories to minProtein
    }
}

// Helper for text answers
data class TextInputAnswer(
    val questionId: String,
    val text: String
) : Answer {
    override val questionId: String
        get() = questionId
}