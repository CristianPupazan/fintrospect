package examples

import java.net.InetSocketAddress

import com.twitter.finagle.builder.ServerBuilder
import com.twitter.finagle.http.path.Root
import com.twitter.finagle.http.{Http, Request, RichHttp}
import io.github.daviddenton.fintrospect.FintrospectModule
import io.github.daviddenton.fintrospect.SegmentMatchers._
import io.github.daviddenton.fintrospect.swagger.Description
import io.github.daviddenton.fintrospect.swagger.v2dot0.Swagger2Renderer
import org.jboss.netty.handler.codec.http.HttpMethod
import util.Echo

object SwaggerV2dot0 extends App {

  val module = FintrospectModule(Root, Swagger2Renderer)
    .withRoute(Description("a get endpoint", HttpMethod.GET, _ / "echo"), string("message"), (s: String) => Echo(s))
    .withRoute(Description("a post endpoint", HttpMethod.POST, _ / "echo"), string("message"), (s: String) => Echo(s))
    .withRoute(Description("a friendly endpoint", HttpMethod.GET, _ / "welcome"), string("firstName"), fixed("bertrand"), string("secondName"), (x: String, y: String, z: String) => Echo(x, y, z))

  ServerBuilder()
    .codec(RichHttp[Request](Http()))
    .bindTo(new InetSocketAddress(8080))
    .name("")
    .build(module.toService)

  println("See the service description at: http://localhost:8080")
}
