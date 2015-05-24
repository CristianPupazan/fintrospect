package io.github.daviddenton.fintrospect.renderers.simplejson

import argo.jdom.JsonNodeFactories.string
import argo.jdom.JsonRootNode
import com.twitter.finagle.http.path.Path
import io.github.daviddenton.fintrospect.renderers.{ModuleRenderer, JsonModuleRenderer}
import io.github.daviddenton.fintrospect.util.ArgoUtil._
import io.github.daviddenton.fintrospect.{DescriptionRenderer, Route}

class SimpleJson private() extends DescriptionRenderer[JsonRootNode] {
  private def render(basePath: Path, route: Route): Field = {
    route.method + ":" + route.describeFor(basePath) -> string(route.describedRoute.summary)
  }

  def apply(basePath: Path, routes: Seq[Route]): JsonRootNode = obj("resources" -> obj(routes.map(r => render(basePath, r))))
}


/**
 * Ultra-basic Renderer implementation that only supports the route paths and the main descriptions of each.
 */
object SimpleJson {
  def apply(): ModuleRenderer[JsonRootNode] = new JsonModuleRenderer(new SimpleJson())
}