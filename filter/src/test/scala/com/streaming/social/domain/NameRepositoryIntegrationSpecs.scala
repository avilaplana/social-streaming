package com.streaming.social.domain

import org.specs2.mutable.Specification
import com.mongodb.BasicDBObject


class NameRepositoryIntegrationSpecs extends Specification {

  "find name by alvaro should return a name object with alvaro and male attrbutes" >> {
    val name : Option[Name] = new NameRepository().findOneByName("alvaro")
    name.isDefined must beTrue
    name.get.name must_== "alvaro"
    name.get.gender must_== "Male"
  }

}