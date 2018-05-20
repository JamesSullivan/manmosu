package utils

import org.jsoup.Jsoup;
import org.jsoup.safety.Whitelist

object Sanitizer {

  def safeText(input: String) = Jsoup.parse(input).text()

  def safeHTML(unsafe: String) = Jsoup.clean(unsafe, Whitelist.relaxed())

  def slugify(input: String): String = {
    import java.text.Normalizer
    val output = Normalizer.normalize(input, Normalizer.Form.NFD)
      .replaceAll("""/""", "-") // Replace slashes with single dashes
      .replaceAll("[^\\w\\s-]", "") // Remove all non-word, non-space or non-dash characters
      .replace('-', ' ') // Replace dashes with spaces
      .trim // Trim leading/trailing whitespace (including what used to be leading/trailing dashes)
      .replaceAll("\\s+", "-") // Replace whitespace (including newlines and repetitions) with single dashes
      .replaceAll("""\?""", "") // Remove question marks
      .toLowerCase
    output
  }

}