package io.github.daviddenton.fintrospect.renderers

import argo.jdom.JsonNode
import argo.jdom.JsonNodeFactories._
import io.github.daviddenton.fintrospect.FintrospectModule._
import io.github.daviddenton.fintrospect._
import io.github.daviddenton.fintrospect.util.ArgoUtil._

import scala.collection.JavaConversions._

object Swagger1dot1Json {

  private def render(p: Parameter): JsonNode = obj(
    "name" -> string(p.name),
    "paramType" -> string(p.location.toString),
    "required" -> booleanNode(true),
    "dataType" -> string(p.paramType)
  )


  private def render(r: ModuleRoute): (String, JsonNode) = {
    val params = r.segmentMatchers.flatMap(_.toParameter)

    r.description.method.getName.toLowerCase -> obj(
      "httpMethod" -> string(r.description.method.getName),
      "nickname" -> string(r.description.value),
      "summary" -> string(r.description.value),
      "produces" -> array(string("application/json")),
      "parameters" -> array(params.map(render): _*),
      "errorResponses" -> {
        array(Seq[PathResponse]().map(r => r.code -> string(r.description)).map(p => obj("code" -> number(p._1), "description" -> p._2)))
      }
    )
  }

  def apply(): Renderer =
    mr => {
      val api = mr
        .groupBy(_.toString)
        .map { case (path, routes) => obj("path" -> string(path), "operations" -> array(routes.map(render(_)._2): _*))}

      obj(
        "swaggerVersion" -> string("1.1"),
        "resourcePath" -> string("/"),
        "apis" -> array(asJavaIterable(api))
        //    "definitions" -> obj(
        //      "User" -> obj(
        //        "properties" -> obj(
        //          "id" -> obj(
        //            "type" -> "integer",
        //            "format" -> "int64"
        //          )
        //        )
        //      )
        //    )
      )
    }
}