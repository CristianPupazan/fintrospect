package io.fintrospect

import _root_.util.Echo
import com.twitter.finagle.Service
import com.twitter.finagle.http.Request
import com.twitter.finagle.http.path.Path
import com.twitter.util.Await._
import io.fintrospect.util.HttpRequestResponseUtil._
import org.jboss.netty.handler.codec.http.HttpMethod.GET
import org.jboss.netty.handler.codec.http.HttpResponseStatus._
import org.jboss.netty.handler.codec.http.{HttpMethod, HttpRequest, HttpResponse}
import org.scalatest.{FunSpec, ShouldMatchers}

class RoutingTest extends FunSpec with ShouldMatchers {

  describe("Routing") {
    it("when it matches it responds as expected") {
      val response = statusAndContentFrom(result(routingWhichMatches((GET, Path("/someUrl")))(Request("/someUrl?field=hello"))))
      response._1 shouldEqual OK
      response._2 should include("/someUrl?field=hello")
    }
    it("no match responds with default 404") {
      val response = result(routingWhichMatches((GET, Path("/someUrl")))(Request("/notMyService")))
      response.getStatus shouldEqual NOT_FOUND
    }
  }

  private def routingWhichMatches(methodAndPath: (HttpMethod, Path)): Routing = {
    Routing.fromBinding(new PartialFunction[(HttpMethod, Path), Service[HttpRequest, HttpResponse]] {
      override def isDefinedAt(x: (HttpMethod, Path)): Boolean = x === methodAndPath

      override def apply(v1: (HttpMethod, Path)): Service[HttpRequest, HttpResponse] = Echo()
    })
  }
}