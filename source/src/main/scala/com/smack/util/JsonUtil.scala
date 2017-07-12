package com.smack.util

import com.fasterxml.jackson.databind.{ DeserializationFeature, ObjectMapper }
import com.fasterxml.jackson.module.scala.DefaultScalaModule
import com.fasterxml.jackson.module.scala.experimental.ScalaObjectMapper
import scala.collection.mutable.HashMap

object JsonUtil{
	val mapper = new ObjectMapper() with ScalaObjectMapper
	mapper.registerModule(DefaultScalaModule)
	mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)

	def toJson(value: Map[Symbol, Any]): String = {
		toJson(value map { case (k, v) => k.name -> v })
	}

	def toJson(value: Any): String = {
		if(value==null){
			mapper.writeValueAsString("")
		}else{
			mapper.writeValueAsString(value)
		}
	}

	def toListMap[V](json: String)(implicit m: Manifest[V]) = fromJson[Map[String, List[V]]](json)
	
	def toMap[V](json: String)(implicit m: Manifest[V]) = fromJson[Map[String, V]](json)
	
	def toBsonMap[V](json: Array[Byte])(implicit m: Manifest[V]) = fromArryByteJson[Map[String, V]](json)

	def fromArryByteJson[T](json: Array[Byte])(implicit m: Manifest[T]): T = {
		mapper.readValue[T](json)
	}
	
	def fromJson[T](json: String)(implicit m: Manifest[T]): T = {
		mapper.readValue[T](json)
	}
}