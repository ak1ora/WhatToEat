package com.example.whattoeat.presentation.questionnaire

import dagger.hilt.android.lifecycle.HiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.whattoeat.domain.model.Answer
import com.example.whattoeat.domain.model.Question
import com.example.whattoeat.domain.model.QuestionFlow
import com.example.whattoeat.domain.model.UserPreferences
import com.example.whattoeat.domain.usecase.ComputePreferencesUseCase
import com.example.whattoeat.domain.usecase.GetQuestionFlowUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class QuestionnaireViewModel @Inject constructor(
    private val getQuestionFlowUseCase: GetQuestionFlowUseCase,
    private val computePreferencesUseCase: ComputePreferencesUseCase
) : ViewModel() {

    private val _state = MutableStateFlow<QuestionnaireState>(QuestionnaireState.Loading)
    val state: StateFlow<QuestionnaireState> = _state.asStateFlow()

    private var questionFlow: QuestionFlow? = null
    private var answers = mutableMapOf<String, Answer>()

    init {
        loadQuestionFlow()
    }

    private fun loadQuestionFlow() {
        viewModelScope.launch {
            questionFlow = getQuestionFlowUseCase()
            if (questionFlow != null) {
                val firstQuestion = questionFlow!!.questions.firstOrNull()
                if (firstQuestion != null) {
                    _state.value = QuestionnaireState.Question(
                        current = firstQuestion,
                        progress = 0f,
                        totalQuestions = questionFlow!!.questions.size
                    )
                } else {
                    _state.value = QuestionnaireState.Error("Не удалось загрузить вопросы")
                }
            } else {
                _state.value = QuestionnaireState.Error("Не удалось загрузить вопросы")
            }
        }
    }

    fun onAnswer(answer: Answer) {
        answers[answer.questionId] = answer

        val nextQuestion = questionFlow?.nextQuestion(answers)
        val progress = questionFlow?.progress(answers) ?: 0f

        if (nextQuestion != null) {
            _state.value = QuestionnaireState.Question(
                current = nextQuestion,
                progress = progress,
                totalQuestions = questionFlow?.questions.size ?: 0
            )
        } else {
            // Completed
            val preferences = computePreferencesUseCase(answers)
            _state.value = QuestionnaireState.Completed(preferences)
        }
    }

    fun onBack() {
        // Simple implementation - remove last answer
        val currentState = _state.value
        if (currentState is QuestionnaireState.Question) {
            val currentQuestionId = currentState.current.id
            // Find previous question
            val answeredKeys = answers.keys.toList()
            val currentIndex = answeredKeys.indexOf(currentQuestionId)
            if (currentIndex > 0) {
                val prevQuestionId = answeredKeys[currentIndex - 1]
                answers.remove(currentQuestionId)
                val prevQuestion = questionFlow?.questions.firstOrNull { it.id == prevQuestionId }
                if (prevQuestion != null) {
                    _state.value = QuestionnaireState.Question(
                        current = prevQuestion,
                        progress = questionFlow?.progress(answers) ?: 0f,
                        totalQuestions = questionFlow?.questions.size ?: 0
                    )
                }
            }
        }
    }

    fun reset() {
        answers.clear()
        loadQuestionFlow()
    }
}

sealed interface QuestionnaireState {
    data class Loading : QuestionnaireState
    data class Question(
        val current: Question,
        val progress: Float,
        val totalQuestions: Int
    ) : QuestionnaireState
    data class Completed(val preferences: UserPreferences) : QuestionnaireState
    data class Error(val message: String) : QuestionnaireState
}