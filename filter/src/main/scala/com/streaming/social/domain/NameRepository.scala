package com.streaming.social.domain

import com.novus.salat._
import com.novus.salat.global._
import com.novus.salat.annotations._
import com.novus.salat.dao._
import com.mongodb.casbah.Imports._
import com.mongodb.casbah.MongoConnection
import org.bson.types.ObjectId
import com.mongodb.casbah.commons.TypeImports.ObjectId

case class Name(name: String, gender: String, @Key("_id") id: ObjectId = ObjectId.get())

class NameRepository extends SalatDAO[Name, ObjectId](collection = MongoConnection()("social_stream")("names")) {
  def findOneByName(name: String): Option[Name] = {
    findOne(new BasicDBObject("name", name))
  }
}