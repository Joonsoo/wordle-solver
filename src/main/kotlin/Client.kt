import kotlinx.browser.document
import react.create
import react.dom.render

fun main() {
  render(
    element = WordleSolverApp.create(),
    container = document.createElement("div").also { document.body!!.appendChild(it) },
  )
}
