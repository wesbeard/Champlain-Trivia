package com.example.champlain_trivia

import com.google.gson.annotations.SerializedName

data class Root(
    @SerializedName("categories") var categories: Categories
)

data class Categories(
    @SerializedName("general") var general: General,
    @SerializedName("buildings") var buildings: Buildings,
    @SerializedName("burlington") var burlington: Burlington
)

data class General(
    @SerializedName("questions") var questions: List<Question>
)

data class Buildings(
    @SerializedName("questions") var questions: List<Question>
)

data class Burlington(
    @SerializedName("questions") var questions: List<Question>
)

data class Question(
    @SerializedName("prompt") var prompt: String,
    @SerializedName("image") var image: Boolean,
    @SerializedName("answers") var answers: Answers
)

data class Answers(
    @SerializedName("correct") var correct: String,
    @SerializedName("incorrect") var incorrect: List<String>
)
