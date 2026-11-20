package com.example.whattoeat.domain.usecase

import com.example.whattoeat.domain.model.Question
import com.example.whattoeat.domain.model.QuestionFlow
import com.example.whattoeat.domain.model.UserPreferences
import com.example.whattoeat.domain.questionnaire.DecisionTree
import com.example.whattoeat.domain.questionnaire.DecisionTreeImpl
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GetQuestionFlowUseCase @Inject constructor() {
    private val decisionTree = DecisionTreeImpl()
    
    operator fun invoke(): QuestionFlow {
        return QuestionFlow(
            questions = buildQuestions(),
            logic = decisionTree
        )
    }

    private fun buildQuestions(): List<Question> {
        return listOf(
            // 1. Meal Type
            com.example.whattoeat.domain.model.SingleChoiceQuestion(
                id = "q_meal_type",
                title = "Какой приём пищи?",
                description = "Выбери, когда планируешь есть",
                isRequired = true,
                order = 1,
                options = listOf(
                    com.example.whattoeat.domain.model.ChoiceOption("Breakfast", "Завтрак", "Лёгкое и сытное утро", "🌅"),
                    com.example.whattoeat.domain.model.ChoiceOption("Lunch", "Обед", "Основной приём пищи дня", "☀️"),
                    com.example.whattoeat.domain.model.ChoiceOption("Dinner", "Ужин", "Лёгкий вечерний приём", "🌙"),
                    com.example.whattoeat.domain.model.ChoiceOption("Snack", "Перекус", "Быстро перекусить", "🍎")
                )
            ),

            // 2. Time
            com.example.whattoeat.domain.model.RangeQuestion(
                id = "q_time",
                title = "Сколько времени есть на готовку?",
                description = "Включая подготовку ингредиентов",
                isRequired = true,
                order = 2,
                min = 5,
                max = 120,
                step = 5,
                unit = "мин",
                labels = mapOf(
                    5 to "5 мин",
                    15 to "15 мин",
                    30 to "30 мин",
                    45 to "45 мин",
                    60 to "1 час",
                    90 to "1.5 час",
                    120 to "2+ часа"
                )
            ),

            // 3. Difficulty (conditional - only if time < 15)
            com.example.whattoeat.domain.model.SingleChoiceQuestion(
                id = "q_difficulty",
                title = "Какой сложности рецепт?",
                description = "Выбери уровень сложности",
                isRequired = true,
                order = 3,
                options = listOf(
                    com.example.whattoeat.domain.model.ChoiceOption("Easy", "Очень просто", "Минимум действий, 3-5 ингредиентов", "⚡"),
                    com.example.whattoeat.domain.model.ChoiceOption("Medium", "Просто", "Обычный рецепт, 6-10 ингредиентов", "👨‍🍳"),
                    com.example.whattoeat.domain.model.ChoiceOption("Hard", "Средне", "Требует времени и навыков", "🍳")
                )
            ),

            // 4. Cuisine
            com.example.whattoeat.domain.model.SingleChoiceQuestion(
                id = "q_cuisine",
                title = "Какую кухню предпочитаешь?",
                description = "Или выбери «Любая» для разнообразия",
                isRequired = false,
                order = 4,
                options = listOf(
                    com.example.whattoeat.domain.model.ChoiceOption("any", "Любая", "Сюрприз меня!", "🎲"),
                    com.example.whattoeat.domain.model.ChoiceOption("Russian", "Русская", "Борщ, пельмени, каши", "🇷🇺"),
                    com.example.whattoeat.domain.model.ChoiceOption("Italian", "Итальянская", "Паста, пицца, ризотто", "🇮🇹"),
                    com.example.whattoeat.domain.model.ChoiceOption("Asian", "Азиатская", "Вок, суши, карри", "🥢"),
                    com.example.whattoeat.domain.model.ChoiceOption("Mexican", "Мексиканская", "Тако, буррито, начос", "🌮"),
                    com.example.whattoeat.domain.model.ChoiceOption("Mediterranean", "Средиземноморская", "Салаты, рыба, оливки", "🫒"),
                    com.example.whattoeat.domain.model.ChoiceOption("American", "Американская", "Бургеры, стейки, ббк", "🇺🇸"),
                    com.example.whattoeat.domain.model.ChoiceOption("Vegetarian", "Вегетарианская", "Без мяса и рыбы", "🥗"),
                    com.example.whattoeat.domain.model.ChoiceOption("Vegan", "Веганская", "Полностью растительная", "🌱"),
                    com.example.whattoeat.domain.model.ChoiceOption("Healthy", "Здоровое питание", "Низкокалорийное, сбалансированное", "💪")
                )
            ),

            // 5. Available Ingredients
            com.example.whattoeat.domain.model.MultiChoiceQuestion(
                id = "q_ingredients",
                title = "Какие продукты есть под рукой?",
                description = "Выбери всё, что есть (можно несколько)",
                isRequired = false,
                order = 5,
                options = listOf(
                    com.example.whattoeat.domain.model.ChoiceOption("chicken", "Курица", null, "🍗"),
                    com.example.whattoeat.domain.model.ChoiceOption("eggs", "Яйца", null, "🥚"),
                    com.example.whattoeat.domain.model.ChoiceOption("cottage cheese", "Творог", null, "🧀"),
                    com.example.whattoeat.domain.model.ChoiceOption("pasta", "Паста/макароны", null, "🍝"),
                    com.example.whattoeat.domain.model.ChoiceOption("rice", "Рис", null, "🍚"),
                    com.example.whattoeat.domain.model.ChoiceOption("potatoes", "Картофель", null, "🥔"),
                    com.example.whattoeat.domain.model.ChoiceOption("buckwheat", "Гречка", null, "🌾"),
                    com.example.whattoeat.domain.model.ChoiceOption("vegetables", "Овощи (помидоры, огурцы, перец)", null, "🥕"),
                    com.example.whattoeat.domain.model.ChoiceOption("tomatoes", "Помидоры", null, "🍅"),
                    com.example.whattoeat.domain.model.ChoiceOption("onion", "Лук", null, "🧅"),
                    com.example.whattoeat.domain.model.ChoiceOption("garlic", "Чеснок", null, "🧄"),
                    com.example.whattoeat.domain.model.ChoiceOption("cheese", "Сыр", null, "🧀"),
                    com.example.whattoeat.domain.model.ChoiceOption("milk", "Молоко", null, "🥛"),
                    com.example.whattoeat.domain.model.ChoiceOption("flour", "Мука", null, "🌾"),
                    com.example.whattoeat.domain.model.ChoiceOption("ground meat", "Фарш", null, "🥩"),
                    com.example.whattoeat.domain.model.ChoiceOption("fish", "Рыба", null, "🐟"),
                    com.example.whattoeat.domain.model.ChoiceOption("canned beans", "Консервированная фасоль", null, "🫘"),
                    com.example.whattoeat.domain.model.ChoiceOption("canned tomatoes", "Помидоры в собственном соку", null, "🍅"),
                ),
                maxSelections = null,
                minSelections = 0,
                allowCustom = true
            ),

            // 6. Exclusions
            com.example.whattoeat.domain.model.MultiChoiceQuestion(
                id = "q_exclusions",
                title = "Что исключить?",
                description = "Аллергии, нелюбимые продукты или диетические ограничения",
                isRequired = false,
                order = 6,
                options = listOf(
                    com.example.whattoeat.domain.model.ChoiceOption("gluten", "Глютен", "Пшеница, рожь, ячмень", "🌾"),
                    com.example.whattoeat.domain.model.ChoiceOption("dairy", "Молочные продукты", "Лактоза, казеин", "🥛"),
                    com.example.whattoeat.domain.model.ChoiceOption("nuts", "Орехи", "Арахис, миндаль, грецкие", "🥜"),
                    com.example.whattoeat.domain.model.ChoiceOption("seafood", "Морепродукты", "Креветки, мидии, кальмары", "🦐"),
                    com.example.whattoeat.domain.model.ChoiceOption("eggs", "Яйца", null, "🥚"),
                    com.example.whattoeat.domain.model.ChoiceOption("soy", "Соя", null, "🫘"),
                    com.example.whattoeat.domain.model.ChoiceOption("spicy", "Острое", "Перец чили, халапеньо", "🌶️"),
                    com.example.whattoeat.domain.model.ChoiceOption("onion_garlic", "Лук и чеснок", null, "🧅"),
                    com.example.whattoeat.domain.model.ChoiceOption("mushrooms", "Грибы", null, "🍄"),
                ),
                maxSelections = null,
                minSelections = 0,
                allowCustom = true
            ),

            // 7. Extra preferences
            com.example.whattoeat.domain.model.TextInputQuestion(
                id = "q_extra",
                title = "Дополнительные пожелания",
                description = "Например: «до 500 ккал», «много белка», «без сахара», «на сковороде»",
                isRequired = false,
                order = 7,
                hint = "Напиши свои пожелания...",
                maxLength = 200,
                inputType = com.example.whattoeat.domain.model.TextInputType.Multiline
            )
        )
    }
}

@Singleton
class ComputePreferencesUseCase @Inject constructor() {
    private val decisionTree = DecisionTreeImpl()
    
    operator fun invoke(answers: Map<String, com.example.whattoeat.domain.model.Answer>): UserPreferences {
        return decisionTree.computePreferences(answers)
    }
}