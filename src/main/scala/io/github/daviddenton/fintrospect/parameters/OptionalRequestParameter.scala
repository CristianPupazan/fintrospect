package io.github.daviddenton.fintrospect.parameters

import com.twitter.finagle.http.Request

import scala.reflect.ClassTag

class OptionalRequestParameter[T](name: String, description: Option[String], location: Location, parse: (String => Option[T]))(implicit ct: ClassTag[T])
  extends RequestParameter[T](name, description, location, parse)(ct) {
  def from(request: Request): Option[T] = location.from(name, request).flatMap(parse)
}