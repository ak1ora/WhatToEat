package com.example.whattoeat.presentation.recipe

import androidx.hilt.lifecycle.HiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.whattoeat.domain.model.Recipe
import com.example.whattoeat.domain.model.UserPreferences
import com.example.whattoeat.domain.repository.RecipeRepository
import com.example.whattoeat.domain.usecase.GetRecipesUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RecipeListViewModel @Inject constructor(
    private val getRecipesUseCase: GetRecipesUseCase
) : ViewModel() {

    private val _state = MutableStateFlow<RecipeListState>(RecipeListState.Idle)
    val state: StateFlow<RecipeListState> = _state.asStateFlow()

    private var currentPreferences: UserPreferences? = null

    fun loadRecipes(preferences: UserPreferences) {
        currentPreferences = preferences
        _state.value = RecipeListState.Loading
        viewModelScope.launch {
            val result = getRecipesUseCase(preferences)
            _state.value = when (result) {
                is com.example.whattoeat.domain.repository.RecipeRepository.Result.Success ->
                    RecipeListState.Success(result.data)
                is com.example.whattoeat.domain.repository.RecipeRepository.Result.Error ->
                    RecipeListState.Error(result.message)
            }
        }
    }

    fun retry() {
        currentPreferences?.let { loadRecipes(it) }
    }
}

sealed interface RecipeListState {
    data object Idle : RecipeListState
    data object Loading : RecipeListState
    data class Success(val recipes: List<Recipe>) : RecipeListState
    data class Error(val message: String) : RecipeListState
}