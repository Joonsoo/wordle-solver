data class WordleHint(
  val char: Char,
  val correct: Set<Int>,
  val wrongLocations: Set<Int>,
) {
  fun merge(other: WordleHint): WordleHint {
    check(char == other.char)
    return WordleHint(char, correct + other.correct, wrongLocations + other.wrongLocations)
  }
}

class WordleSolver(
  val words: Set<String>,
  val guesses: Set<String>,
  val hints: Map<Char, WordleHint>,
) {
  companion object {
    val default = WordleSolver(WordleWords.words.toSet(), WordleWords.allowed.toSet(), mapOf())

    fun wordIsPossible(word: String, hints: Collection<WordleHint>): Boolean {
      for (hint in hints) {
        if (!hint.correct.all { idx -> word[idx] == hint.char }) {
          return false
        }
        if (!hint.wrongLocations.all { idx -> word[idx] != hint.char }) {
          return false
        }
        if (!word.contains(hint.char)) {
          return false
        }
      }
      return true
    }
  }

  fun bestTry(): String {
    // words, guesses 중에서 남아있는 words 중 가장 많은걸 걸러낼 수 있는 후보를 반환
    TODO()
  }

  fun addHints(nonexistingChars: Set<Char>, newHints: List<WordleHint>): WordleSolver {
    val newHintsMap = newHints.fold(hints) { hintsMap, newHint ->
      val existingHint = hintsMap[newHint.char]
      if (existingHint == null) {
        hintsMap + (newHint.char to newHint)
      } else {
        hintsMap + (newHint.char to existingHint.merge(newHint))
      }
    }
    val newHintValues = newHintsMap.values
    val filteredWords = words.filter { it.toSet().intersect(nonexistingChars).isEmpty() }
      .filter { wordIsPossible(it, newHintValues) }
    val filteredGuesses = guesses.filter { it.toSet().intersect(nonexistingChars).isEmpty() }
      .filter { wordIsPossible(it, newHintValues) }
    return WordleSolver(filteredWords.toSet(), filteredGuesses.toSet(), newHintsMap)
  }

  fun applyGuess(guess: WordleGuess): WordleSolver {
    val nonexisting = guess.nonexistingChars()
    val hints = guess.toHints()
    println(nonexisting)
    println(hints)
    return this.addHints(nonexisting, hints)
  }

  fun applyGuesses(guesses: List<WordleGuess>): WordleSolver {
    var solver = this
    for (guess in guesses) {
      solver = solver.addHints(guess.nonexistingChars(), guess.toHints())
    }
    return solver
  }
}
