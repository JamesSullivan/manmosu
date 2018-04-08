package utils

import org.scalatest._
import org.scalatestplus.play._


class SanitizerSpec() extends PlaySpec{
  
  "A sanitizer.slugify for \"What are the challenges to Sanitizing/Slugifying?\" " should {
    "return what-are-the-challenges-to-sanitizing-slugifying" in {
     Sanitizer.slugify("What are the challenges to Sanitizing/Slugifying?") mustBe "what-are-the-challenges-to-sanitizing-slugifying"
    }
  }

}