package com.example.whattoeat.di

import com.example.whattoeat.data.repository.RecipeRepositoryImpl
import com.example.whattoeat.domain.repository.RecipeRepository
import com.example.whattoeat.domain.questionnaire.DecisionTree
import com.example.whattoeat.domain.questionnaire.DecisionTreeImpl
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindRecipeRepository(impl: RecipeRepositoryImpl): RecipeRepository

    @Provides
    @Singleton
    fun provideDecisionTree(): DecisionTree = DecisionTreeImpl()
}