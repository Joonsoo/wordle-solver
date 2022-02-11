import mui.material.*
import react.FC
import react.Props
import react.dom.html.ReactHTML.div
import react.dom.html.ReactHTML.h1
import react.dom.html.ReactHTML.input
import react.dom.html.ReactHTML.span
import react.useState

data class WordleGuess(
  val word: String,
  val correctIndices: Set<Int>,
  val existingCharsIndices: Set<Int>,
) {
  fun toHints(): List<WordleHint> =
    correctIndices.map { i -> WordleHint(word[i].lowercaseChar(), setOf(i), setOf()) } +
      existingCharsIndices.map { i -> WordleHint(word[i].lowercaseChar(), setOf(), setOf(i)) }

  fun nonexistingChars(): Set<Char> {
    val nonexistings = ((0 until 5).toSet() - correctIndices - existingCharsIndices)
      .map { word[it].lowercaseChar() }.toSet()
    val existingChars = correctIndices.map { word[it].lowercaseChar() }
      .toSet() + existingCharsIndices.map { word[it].lowercaseChar() }.toSet()
    return nonexistings - existingChars
  }
}

external interface WordleGuesses : Props {
  var guesses: List<WordleGuess>
  var editingGuess: WordleGuess?
  var solver: WordleSolver
}

val WordleUi = FC<WordleGuesses> { props ->
  var guesses: List<WordleGuess> by useState(props.guesses)
  var editingGuess by useState(props.editingGuess)
  var solver by useState(props.solver)
  var nextWord by useState("")

  fun ButtonProps.setButtonAppearance(guess: WordleGuess, charIndex: Int) {
    variant = if (
      guess.correctIndices.contains(charIndex) || guess.existingCharsIndices.contains(charIndex)
    ) ButtonVariant.contained else ButtonVariant.outlined
    color = when {
      guess.correctIndices.contains(charIndex) -> ButtonColor.success
      guess.existingCharsIndices.contains(charIndex) -> ButtonColor.secondary
      else -> ButtonColor.inherit
    }
  }

  h1 {
    +"Wordle solver"
  }
  div {
    +"Add the next guess and click each letter to mark the correctness. Green means the letter is in the correct spot, and purple means the word is in the word but in the wrong spot."
  }

  Table {
    TableBody {
      guesses.forEach { guess ->
        TableRow {
          guess.word.forEachIndexed { charIndex, c ->
            // tableCellForChar(guess, charIndex, c, null)
            TableCell {
              Button {
                this.setButtonAppearance(guess, charIndex)
                +"$c"
              }
            }
          }
        }
      }
      if (editingGuess != null) {
        val editing = editingGuess!!
        TableRow {
          editing.word.forEachIndexed { charIndex, c ->
            TableCell {
              Button {
                this.setButtonAppearance(editing, charIndex)
                this.onClick = {
                  if (editing.correctIndices.contains(charIndex)) {
                    editingGuess = editing.copy(
                      correctIndices = editing.correctIndices - charIndex,
                      existingCharsIndices = editing.existingCharsIndices + charIndex,
                    )
                  } else if (editing.existingCharsIndices.contains(charIndex)) {
                    editingGuess = editing.copy(
                      correctIndices = editing.correctIndices - charIndex,
                      existingCharsIndices = editing.existingCharsIndices - charIndex,
                    )
                  } else {
                    editingGuess = editing.copy(
                      correctIndices = editing.correctIndices + charIndex,
                      existingCharsIndices = editing.existingCharsIndices - charIndex,
                    )
                  }
                }
                +"$c ?"
              }
            }
          }
        }
        TableRow {
          TableCell {
            colSpan = 5
            Button {
              variant = ButtonVariant.contained
              onClick = {
                guesses = guesses + editing
                solver = solver.applyGuess(editing)
                editingGuess = null
              }

              +"Search"
            }
          }
        }
      } else {
        TableRow {
          TableCell {
            colSpan = 5
            +"${solver.words.size} words are possible:"
            solver.words.sorted().take(30).forEach { nextGuess ->
              Button {
                onClick = {
                  editingGuess = WordleGuess(nextGuess, setOf(), setOf())
                }
                +nextGuess.uppercase()
              }
            }
            if (solver.words.size > 30) {
              span {
                +"${solver.words.size - 30} more..."
              }
            }
          }
        }
        TableRow {
          TableCell {
            colSpan = 5
            input {
              value = nextWord
              onChange = {
                nextWord = it.target.value.uppercase()
              }
            }
            Button {
              variant = ButtonVariant.contained
              onClick = {
                editingGuess = WordleGuess(nextWord, setOf(), setOf())
                println(guesses)
              }

              +"Add $nextWord"
            }
          }
        }
      }
    }
  }

  Button {
    onClick = {
      guesses = listOf()
      editingGuess = null
      solver = WordleSolver.default
      nextWord = ""
    }
    +"Clear"
  }
}

val WordleSolverApp = FC<Props> {
  val guesses = listOf<WordleGuess>()

  WordleUi {
    this.guesses = guesses
    this.editingGuess = null
    this.solver = WordleSolver.default.applyGuesses(guesses)
  }
}
