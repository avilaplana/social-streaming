package com.streaming.social.common

import org.specs2.mutable.Specification
import com.streaming.social.actors.{User, TwitterEvent}
import java.util.Date
import com.streaming.social.domain.NameRepository


class GenderCalculatorSpecs extends Specification {

  private val genderCalculator = new GenderCalculator(new NameRepository)

  "calculate gender for ALvaro vilaplana Garcia" >> {
    val name = genderCalculator.calculateGender("ALvaro     vilaplana Garcia")
    name.isDefined must beTrue
    name.get must_== "Male"
  }


  "calculate gender for 233 sasa 2222" >> {
      val name = genderCalculator.calculateGender("233 sasa 2222")
      name.isDefined must beFalse
    }

}