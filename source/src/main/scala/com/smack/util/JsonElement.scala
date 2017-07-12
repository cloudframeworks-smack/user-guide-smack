package com.smack.util

import util.parsing.json.JSON
import scala.language.dynamics
import java.util.HashMap
import scala.collection.JavaConversions._

trait JsonElement extends Dynamic { self =>
    def selectDynamic(field: String): JsonElement = EmptyElement
    def applyDynamic(field: String)(i: Int): JsonElement = EmptyElement
    def toList: List[String] = sys.error(s"$this is not a list.")
    def asString: String = sys.error(s"$this has no string representation.")
    def length$: Int = sys.error(s"$this has no length")
    def toMap: HashMap[String, Any] = new HashMap[String, Any]()
}

object JsonElement {

    def ^(s: String) = {
        require(!s.isEmpty, "Element is empty")
        s
    }

    implicit def toString(e: JsonElement): String = e.asString
    implicit def toBoolean(e: JsonElement): Boolean = (^(e.asString)).toBoolean
    implicit def toBigDecimal(e: JsonElement): BigDecimal = BigDecimal(^(e.asString))
    implicit def toDouble(e: JsonElement): Double = ^(e.asString).toDouble
    implicit def toFloat(e: JsonElement): Float = ^(e.asString).toFloat
    implicit def toByte(e: JsonElement): Byte = ^(e.asString).stripSuffix(".0").toByte
    implicit def toShort(e: JsonElement): Short = ^(e.asString).stripSuffix(".0").toShort
    implicit def toInt(e: JsonElement): Int = ^(e.asString).stripSuffix(".0").toInt
    implicit def toLong(e: JsonElement): Long = ^(e.asString).stripSuffix(".0").toLong
    implicit def toList(e: JsonElement): List[String] = e.toList

    def parse(json: String) = JSON.parseFull(json) map (JsonElement(_))

    def apply(any: Any): JsonElement = any match {
        case x: Seq[Any] => new ArrayElement(x)
        case x: Map[String, Any] => new ComplexElement(x)
        case x => new PrimitiveElement(x)
    }

    def parseValue(str: String, map: HashMap[String, Any]): Any = {
        parseValue("", str, map)
    }    

    private def parseValue(key: String, str: String, map: HashMap[String, Any]): Any = {
        val json = parse(str)
        if (json.isEmpty) {
            map.put(key, str)
        } else {
            val js1 = json.get.toMap
            js1.keySet().foreach(key1 => {
                val value = js1.get(key1)
                recursion(key1, value, map)
            })
        }
    }

    private def recursion(key: String, value: Any, map: HashMap[String, Any]): Any = {
        value match {
            case Some(x: Map[String, Any]) =>
                if (x.size < 1) {
                    map.put(key, "")
                } else {
                    x.keySet.foreach(mkey => {
                        val value = x.get(mkey).get
                        recursion(mkey, Some(value), map)
                    })
                }
            case Some(x: String) => map.put(key, x)
            case Some(x: Int) => map.put(key, x)
            case Some(x: Float) => map.put(key, x)
            case Some(x: Double) => map.put(key, x)
            case x => println("x=" + x)
        }
    }
}

case class PrimitiveElement(x: Any) extends JsonElement {
    override def asString = x.toString
}

case object EmptyElement extends JsonElement {
    override def asString = ""
    override def toList = Nil
}

case class ArrayElement(private val x: Seq[Any]) extends JsonElement {
    private lazy val elements = x.map((JsonElement(_))).toArray

    override def applyDynamic(field: String)(i: Int): JsonElement = elements.lift(i).getOrElse(EmptyElement)
    override def toList: List[String] = elements map (_.asString) toList
    override def length$: Int = elements.length
}

case class ComplexElement(private val fields: Map[String, Any]) extends JsonElement {
    override def selectDynamic(field: String): JsonElement = fields.get(field) map (JsonElement(_)) getOrElse (EmptyElement)
    override def toMap: HashMap[String, Any] = {
        var newMap = new HashMap[String, Any]()
        fields.keys foreach (x => newMap.put(x, fields.get(x)))
        newMap
    }
}