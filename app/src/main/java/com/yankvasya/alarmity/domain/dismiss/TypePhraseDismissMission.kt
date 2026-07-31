package com.yankvasya.alarmity.domain.dismiss

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.yankvasya.alarmity.R
import javax.inject.Inject

private const val TOTAL_ROUNDS = 3

/** Trimmed, case-insensitive match — forgiving of the kind of typo an unfocused, half-awake user makes. */
internal fun phraseMatches(input: String, target: String): Boolean =
    input.trim().equals(target.trim(), ignoreCase = true)

class TypePhraseDismissMission @Inject constructor() : DismissMission {
    override val id: String = "type_phrase"
    override val displayNameRes: Int = R.string.mission_type_phrase

    @Composable
    override fun Content(onComplete: () -> Unit) {
        val phrases = stringArrayResource(R.array.affirmation_phrases)

        var round by remember { mutableIntStateOf(0) }
        var currentPhrase by remember { mutableStateOf(phrases.random()) }
        var input by remember { mutableStateOf("") }
        var wasWrong by remember { mutableStateOf(false) }

        fun submit() {
            if (phraseMatches(input, currentPhrase)) {
                wasWrong = false
                if (round == TOTAL_ROUNDS - 1) {
                    onComplete()
                } else {
                    round++
                    // Never repeat the phrase just typed, so each round genuinely needs re-reading.
                    currentPhrase = phrases.filter { it != currentPhrase }.random()
                    input = ""
                }
            } else {
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
            Text(
                text = stringResource(R.string.phrase_round_format, round + 1, TOTAL_ROUNDS),
                style = MaterialTheme.typography.labelLarge,
            )
            Text(
                text = currentPhrase,
                style = MaterialTheme.typography.titleLarge,
                textAlign = TextAlign.Center,
            )
            if (wasWrong) {
                Text(text = stringResource(R.string.phrase_mismatch), style = MaterialTheme.typography.bodyMedium)
            }
            OutlinedTextField(
                value = input,
                onValueChange = { input = it },
                label = { Text(stringResource(R.string.type_phrase_hint)) },
                singleLine = true,
            )
            Button(onClick = ::submit) {
                Text(stringResource(R.string.submit))
            }
        }
    }
}
