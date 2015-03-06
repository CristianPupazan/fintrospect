package io.github.daviddenton.fintrospect.renderers

import argo.jdom.JsonNode
import argo.jdom.JsonNodeFactories._
import io.github.daviddenton.fintrospect.FintrospectModule.Renderer
import io.github.daviddenton.fintrospect._
import io.github.daviddenton.fintrospect.util.ArgoUtil._
import org.jboss.netty.handler.codec.http.HttpMethod

object Swagger2dot0Json {

  private case class PathMethod(method: HttpMethod, summary: String, params: Seq[Parameter], responses: Seq[PathResponse], securities: Seq[Security])

  private def render(p: Parameter): JsonNode = obj(
    "in" -> string(p.location.toString),
    "name" -> string(p.name),
    "required" -> booleanNode(p.required),
    "type" -> string(p.paramType)
  )

  private def render(r: ModuleRoute): (String, JsonNode) = {
    val params = r.segmentMatchers.flatMap(_.toParameter)
    r.description.method.getName.toLowerCase -> obj(
      "summary" -> string(r.description.value),
      "produces" -> array(string("application/json")),
      "parameters" -> array(params.map(render): _*),
      "responses" -> obj(Seq(PathResponse(200, "")).map(r => r.code -> obj("description" -> string(r.description))).map(cd => cd._1.toString -> cd._2)),
      "security" -> array(obj(Seq[Security]().map(_.toPathSecurity)))
    )
  }

  def apply(): Renderer =
    mr => {
      val paths = mr
        .groupBy(_.toString)
        .map { case (path, routes) => path -> obj(routes.map(render))}.toSeq

      obj(
        "swagger" -> string("2.0"),
        "info" -> obj("title" -> string("title"), "version" -> string("version")),
        "basePath" -> string("/"),
        "paths" -> obj(paths)
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