package com.streaming.social.common

import com.streaming.social.domain.NameRepository


class GenderCalculator(repository: NameRepository) {

  def calculateGender(name: String) : Option[String]= {
    repository.findOneByName(name.toLowerCase.split("\\s+")(0)) match {
      case Some(name) => Some(name.gender)
      case _ => None
    }
  }

}