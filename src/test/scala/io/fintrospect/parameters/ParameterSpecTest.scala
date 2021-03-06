package io.fintrospect.parameters

import java.time.{LocalDate, LocalDateTime, ZoneId, ZonedDateTime}

import io.fintrospect.util.ArgoUtil._
import org.scalatest._

import scala.util.{Success, Try}

class ParameterSpecTest extends FunSpec with ShouldMatchers {

  val paramName = "name"


  describe("int") {
    it("retrieves a valid value") {
      Try(ParameterSpec.int(paramName, "").deserialize("123")) shouldEqual Success(123)
    }

    it("does not retrieve an invalid value") {
      Try(ParameterSpec.int(paramName, "").deserialize("asd")).isFailure shouldEqual true
    }

    it("does not retrieve an null value") {
      Try(ParameterSpec.int(paramName, "").deserialize(null)).isFailure shouldEqual true
    }

    it("serializes correctly") {
      ParameterSpec.int(paramName, "").serialize(123) shouldEqual "123"
    }
  }

  describe("integer") {
    it("retrieves a valid value") {
      Try(ParameterSpec.integer(paramName, "").deserialize("123")) shouldEqual Success(123)
    }

    it("does not retrieve an invalid value") {
      Try(ParameterSpec.integer(paramName, "").deserialize("asd")).isFailure shouldEqual true
    }

    it("does not retrieve an null value") {
      Try(ParameterSpec.integer(paramName, "").deserialize(null)).isFailure shouldEqual true
    }

    it("serializes correctly") {
      ParameterSpec.integer(paramName, "").serialize(123) shouldEqual "123"
    }
  }

  describe("long") {
    it("retrieves a valid value") {
      Try(ParameterSpec.long(paramName, "").deserialize("123")) shouldEqual Success(123)
    }

    it("does not retrieve an invalid value") {
      Try(ParameterSpec.long(paramName, "").deserialize("asd")).isFailure shouldEqual true
    }

    it("does not retrieve an null value") {
      Try(ParameterSpec.long(paramName, "").deserialize(null)).isFailure shouldEqual true
    }

    it("serializes correctly") {
      ParameterSpec.long(paramName, "").serialize(123) shouldEqual "123"
    }
  }

  describe("bigDecimal") {
    it("retrieves a valid value") {
      Try(ParameterSpec.bigDecimal(paramName, "").deserialize("1.1234")) shouldEqual Success(BigDecimal("1.1234"))
    }

    it("does not retrieve an invalid value") {
      Try(ParameterSpec.bigDecimal(paramName, "").deserialize("asd")).isFailure shouldEqual true
    }

    it("does not retrieve an null value") {
      Try(ParameterSpec.bigDecimal(paramName, "").deserialize(null)).isFailure shouldEqual true
    }

    it("serializes correctly") {
      ParameterSpec.bigDecimal(paramName, "").serialize(BigDecimal("1.1234")) shouldEqual "1.1234"
    }
  }

  describe("boolean") {
    it("retrieves a valid value") {
      Try(ParameterSpec.boolean(paramName, "").deserialize("true")) shouldEqual Success(true)
    }

    it("does not retrieve an invalid value") {
      Try(ParameterSpec.boolean(paramName, "").deserialize("notABool")).isFailure shouldEqual true
    }

    it("does not retrieve an null value") {
      Try(ParameterSpec.boolean(paramName, "").deserialize(null)).isFailure shouldEqual true
    }

    it("serializes correctly") {
      ParameterSpec.boolean(paramName, "").serialize(true) shouldEqual "true"
    }
  }

  describe("string") {
    it("retrieves a valid value") {
      Try(ParameterSpec.string(paramName, "").deserialize("123")) shouldEqual Success("123")
    }

    it("does not retrieve an null value") {
      Try(ParameterSpec.string(paramName, "").deserialize(null)).isFailure shouldEqual true
    }

    it("serializes correctly") {
      ParameterSpec.string(paramName, "").serialize("asdas") shouldEqual "asdas"
    }
  }

  describe("dateTime") {
    it("retrieves a valid value") {
      Try(ParameterSpec.dateTime(paramName, "").deserialize("1970-01-01T00:00:00")) shouldEqual Success(LocalDateTime.of(1970, 1, 1, 0, 0, 0))
    }

    it("does not retrieve an invalid value") {
      Try(ParameterSpec.dateTime(paramName, "").deserialize("notADateTime")).isFailure shouldEqual true
    }

    it("does not retrieve an null value") {
      Try(ParameterSpec.dateTime(paramName, "").deserialize(null)).isFailure shouldEqual true
    }

    it("serializes correctly") {
      ParameterSpec.dateTime(paramName, "").serialize(LocalDateTime.of(1970, 1, 1, 2, 3, 4)) shouldEqual "1970-01-01T02:03:04"
    }
  }

  describe("zonedDateTime") {
    it("retrieves a valid value") {
      Try(ParameterSpec.zonedDateTime(paramName, "").deserialize("1970-01-01T00:00:00-01:00")).get.toEpochSecond shouldEqual 3600
    }

    it("does not retrieve an invalid value") {
      Try(ParameterSpec.zonedDateTime(paramName, "").deserialize("notADateTime")).isFailure shouldEqual true
    }

    it("does not retrieve an null value") {
      Try(ParameterSpec.zonedDateTime(paramName, "").deserialize(null)).isFailure shouldEqual true
    }

    it("serializes correctly") {
      ParameterSpec.zonedDateTime(paramName, "").serialize(ZonedDateTime.of(1970, 1, 1, 0, 0, 0, 0, ZoneId.of("UTC"))) shouldEqual "1970-01-01T00:00:00Z[UTC]"
    }
  }

  describe("date") {
    it("retrieves a valid value") {
      Try(ParameterSpec.localDate(paramName, "").deserialize("1970-01-01")) shouldEqual Success(LocalDate.of(1970, 1, 1))
    }

    it("does not retrieve an invalid value") {
      Try(ParameterSpec.localDate(paramName, "").deserialize("notADateTime")).isFailure shouldEqual true
    }

    it("does not retrieve an null value") {
      Try(ParameterSpec.localDate(paramName, "").deserialize(null)).isFailure shouldEqual true
    }

    it("serializes correctly") {
      ParameterSpec.localDate(paramName, "").serialize(LocalDate.of(1970, 1, 1)) shouldEqual "1970-01-01"
    }
  }

  describe("json") {
    val expected = obj("field" -> string("value"))

    it("retrieves a valid value") {
      Try(ParameterSpec.json(paramName, "").deserialize(compact(expected))) shouldEqual Success(expected)
    }

    it("does not retrieve an invalid value") {
      Try(ParameterSpec.json(paramName, "").deserialize("notJson")).isFailure shouldEqual true
    }

    it("does not retrieve an null value") {
      Try(ParameterSpec.json(paramName, "").deserialize(null)).isFailure shouldEqual true
    }

    it("serializes correctly") {
      ParameterSpec.json(paramName, "").serialize(expected) shouldEqual """{"field":"value"}"""
    }
  }

  case class MyCustomType(value: Int)

  describe("custom") {

    val spec = ParameterSpec[MyCustomType](paramName, None, StringParamType, s => MyCustomType(s.toInt), ct => ct.value.toString)

    it("retrieves a valid value") {
      Try(spec.deserialize("123")) shouldEqual Success(MyCustomType(123))
    }

    it("does not retrieve an invalid value") {
      Try(spec.deserialize("notAnInt")).isFailure shouldEqual true
    }

    it("does not retrieve an null value") {
      Try(spec.deserialize(null)).isFailure shouldEqual true
    }

    it("serializes correctly") {
      spec.serialize(MyCustomType(123)) shouldEqual "123"
    }
  }
}
