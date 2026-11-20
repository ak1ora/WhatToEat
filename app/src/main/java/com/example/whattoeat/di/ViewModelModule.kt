package com.example.whattoeat.di

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.CreationExtras
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Inject
import javax.inject.Provider

@EntryPoint
@InstallIn(SingletonComponent::class)
interface ViewModelEntryPoint {
    fun questionnaireViewModelFactory(): QuestionnaireViewModelFactory
    fun recipeListViewModelFactory(): RecipeListViewModelFactory
    fun recipeDetailViewModelFactory(): RecipeDetailViewModelFactory
    fun settingsViewModelFactory(): SettingsViewModelFactory
}

// Factory interfaces - will be implemented when ViewModels are created
interface QuestionnaireViewModelFactory {
    fun create(extras: CreationExtras): ViewModel
}

interface RecipeListViewModelFactory {
    fun create(extras: CreationExtras): ViewModel
}

interface RecipeDetailViewModelFactory {
    fun create(extras: CreationExtras): ViewModel
}

interface SettingsViewModelFactory {
    fun create(extras: CreationExtras): ViewModel
}