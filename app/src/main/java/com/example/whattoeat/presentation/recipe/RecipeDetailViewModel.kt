package com.example.whattoeat.presentation.recipe

import androidx.hilt.lifecycle.HiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.whattoeat.domain.model.Recipe
import com.example.whattoeat.domain.repository.RecipeRepository
import com.example.whattoeat.domain.usecase.GetRecipeByIdUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RecipeDetailViewModel @Inject constructor(
    private val getRecipeByIdUseCase: GetRecipeByIdUseCase
) : ViewModel() {

    private val _state = MutableStateFlow<RecipeDetailState>(RecipeDetailState.Idle)
    val state: StateFlow<RecipeDetailState> = _state.asStateFlow()

    fun loadRecipe(recipeId: String) {
        _state.value = RecipeDetailState.Loading
        viewModelScope.launch {
            val result = getRecipeByIdUseCase(recipeId)
            _state.value = when (result) {
                is com.example.whattoeat.domain.repository.RecipeRepository.Result.Success ->
                    RecipeDetailState.Success(result.data)
                is com.example.whattoeat.domain.repository.RecipeRepository.Result.Error ->
                    RecipeDetailState.Error(result.message)
            }
        }
    }
}

sealed interface RecipeDetailState {
    data object Idle : RecipeDetailState
    data object Loading : RecipeDetailState
    data class Success(val recipe: Recipe) : RecipeDetailState
    data class Error(val message: String) : RecipeDetailState
}