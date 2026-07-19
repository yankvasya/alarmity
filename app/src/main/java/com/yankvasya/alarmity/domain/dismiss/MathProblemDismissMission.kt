package com.yankvasya.alarmity.domain.dismiss

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import javax.inject.Inject
import kotlin.random.Random

private data class MathProblem(val left: Int, val right: Int, val isAddition: Boolean) {
    val answer: Int get() = if (isAddition) left + right else left - right
    val text: String get() = "$left ${if (isAddition) "+" else "−"} $right"
}

private fun randomProblem(): MathProblem {
    val isAddition = Random.nextBoolean()
    val left = Random.nextInt(2, 21)
    // For subtraction, keep the result non-negative so mental math stays simple.
    val right = if (isAddition) Random.nextInt(2, 21) else Random.nextInt(1, left)
    return MathProblem(left, right, isAddition)
}

class MathProblemDismissMission @Inject constructor() : DismissMission {
    override val id: String = "math_problem"
    override val displayName: String = "Solve a math problem"

    @Composable
    override fun Content(onComplete: () -> Unit) {
        var problem by remember { mutableStateOf(randomProblem()) }
        var answer by remember { mutableStateOf("") }
        var wasWrong by remember { mutableStateOf(false) }

        fun submit() {
            if (answer.toIntOrNull() == problem.answer) {
                onComplete()
            } else {
                problem = randomProblem()
                answer = ""
                wasWrong = true
            }
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(32.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterVertically),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(text = problem.text, style = MaterialTheme.typography.displaySmall)
            if (wasWrong) {
                Text(text = "Not quite — try again", style = MaterialTheme.typography.bodyMedium)
            }
            OutlinedTextField(
                value = answer,
                onValueChange = { answer = it.filter(Char::isDigit) },
                label = { Text("Answer") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = ImeAction.Done),
                keyboardActions = KeyboardActions(onDone = { submit() }),
            )
            Button(onClick = ::submit) {
                Text("Submit")
            }
        }
    }
}
