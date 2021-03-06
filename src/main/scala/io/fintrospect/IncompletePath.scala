package io.fintrospect

import com.twitter.finagle.http.path.{->, /, Path}
import com.twitter.finagle.{Filter, Service}
import io.fintrospect.IncompletePath._
import io.fintrospect.parameters.{Path => Fp, PathParameter}
import org.jboss.netty.handler.codec.http.{HttpMethod, HttpRequest, HttpResponse}

object IncompletePath {
  private[fintrospect] type Binding = PartialFunction[(HttpMethod, Path), Service[HttpRequest, HttpResponse]]
  def apply(description: DescribedRoute, method: HttpMethod): IncompletePath0 = new IncompletePath0(description, method, identity)

  type RouteFilter = Filter[HttpRequest, HttpResponse, HttpRequest, HttpResponse]
}

trait IncompletePath {
  val description: DescribedRoute
  val method: HttpMethod
  val pathFn: Path => Path
}

class IncompletePath0(val description: DescribedRoute, val method: HttpMethod, val pathFn: Path => Path) extends IncompletePath {
  def /(part: String) = new IncompletePath0(description, method, pathFn = pathFn.andThen(_ / part))

  def /[T](pp0: PathParameter[T]) = new IncompletePath1(description, method, pathFn, pp0)

  def bindTo(fn: () => Service[HttpRequest, HttpResponse]): Route = new Route(description, method, pathFn) {
    override def toPf(basePath: Path): RouteFilter => Binding = {
      filtered: RouteFilter => {
        case actualMethod -> path if matches(actualMethod, basePath, path) => filtered.andThen(fn())
      }
    }
  }
}

class IncompletePath1[A](val description: DescribedRoute, val method: HttpMethod, val pathFn: Path => Path,
                              pp1: PathParameter[A]) extends IncompletePath {
  def /(part: String): IncompletePath2[A, String] = /(Fp.fixed(part))

  def /[B](pp2: PathParameter[B]): IncompletePath2[A, B] = new IncompletePath2(description, method, pathFn, pp1, pp2)

  def bindTo(fn: (A) => Service[HttpRequest, HttpResponse]): Route = new Route(description, method, pathFn, pp1) {
    override def toPf(basePath: Path): RouteFilter => Binding = {
      filtered: RouteFilter => {
        case actualMethod -> path / pp1(s1) if matches(actualMethod, basePath, path) => filtered.andThen(fn(s1))
      }
    }
  }
}

class IncompletePath2[A,B](val description: DescribedRoute, val method: HttpMethod, val pathFn: Path => Path,
                                 pp1: PathParameter[A],
                                 pp2: PathParameter[B]) extends IncompletePath {
  def /(part: String): IncompletePath3[A, B, String] = /(Fp.fixed(part))

  def /[C](pp3: PathParameter[C]): IncompletePath3[A, B, C] = new IncompletePath3(description, method, pathFn, pp1, pp2, pp3)

  def bindTo(fn: (A, B) => Service[HttpRequest, HttpResponse]): Route = new Route(description, method, pathFn, pp1, pp2) {
    override def toPf(basePath: Path): RouteFilter => Binding = {
      filtered: RouteFilter => {
        case actualMethod -> path / pp1(s1) / pp2(s2) if matches(actualMethod, basePath, path) => filtered.andThen(fn(s1, s2))
      }
    }
  }
}

class IncompletePath3[A, B, C](val description: DescribedRoute, val method: HttpMethod, val pathFn: Path => Path,
                                    pp1: PathParameter[A],
                                    pp2: PathParameter[B],
                                    pp3: PathParameter[C]) extends IncompletePath {
  def /(part: String): IncompletePath4[A, B, C, String] = /(Fp.fixed(part))

  def /[D](pp4: PathParameter[D]): IncompletePath4[A, B, C, D] = new IncompletePath4(description, method, pathFn, pp1, pp2, pp3, pp4)

  def bindTo(fn: (A, B, C) => Service[HttpRequest, HttpResponse]): Route = new Route(description, method, pathFn, pp1, pp2, pp3) {
    override def toPf(basePath: Path): RouteFilter => Binding = {
      filtered: RouteFilter => {
        case actualMethod -> path / pp1(s1) / pp2(s2) / pp3(s3) if matches(actualMethod, basePath, path) => filtered.andThen(fn(s1, s2, s3))
      }
    }
  }
}

class IncompletePath4[A, B, C, D](val description: DescribedRoute, val method: HttpMethod, val pathFn: Path => Path,
                                       pp1: PathParameter[A],
                                       pp2: PathParameter[B],
                                       pp3: PathParameter[C],
                                       pp4: PathParameter[D]
                                        ) extends IncompletePath {
  def /(part: String): IncompletePath5[A, B, C, D, String] = /(Fp.fixed(part))

  def /[E](pp5: PathParameter[E]): IncompletePath5[A, B, C, D, E] = new IncompletePath5(description, method, pathFn, pp1, pp2, pp3, pp4, pp5)

  def bindTo(fn: (A, B, C, D) => Service[HttpRequest, HttpResponse]): Route = new Route(description, method, pathFn, pp1, pp2, pp3, pp4) {
    override def toPf(basePath: Path): RouteFilter => Binding = {
      filtered: RouteFilter => {
        case actualMethod -> path / pp1(s1) / pp2(s2) / pp3(s3) / pp4(s4) if matches(actualMethod, basePath, path) => filtered.andThen(fn(s1, s2, s3, s4))
      }
    }
  }
}

class IncompletePath5[A, B, C, D, E](val description: DescribedRoute, val method: HttpMethod, val pathFn: Path => Path,
                                          pp1: PathParameter[A],
                                          pp2: PathParameter[B],
                                          pp3: PathParameter[C],
                                          pp4: PathParameter[D],
                                          pp5: PathParameter[E]
                                           ) extends IncompletePath {
  def /[F](pp5: PathParameter[F]) = throw new UnsupportedOperationException("Limit on number of elements!")

  def bindTo(fn: (A, B, C, D, E) => Service[HttpRequest, HttpResponse]): Route = new Route(description, method, pathFn, pp1, pp2, pp3, pp4, pp5) {
    override def toPf(basePath: Path): RouteFilter => Binding = {
      filtered: RouteFilter => {
        case actualMethod -> path / pp1(s1) / pp2(s2) / pp3(s3) / pp4(s4) / pp5(s5) if matches(actualMethod, basePath, path) => filtered.andThen(fn(s1, s2, s3, s4, s5))
      }
    }
  }
}
