package io.fintrospect.parameters

import org.jboss.netty.handler.codec.http.HttpRequest

import scala.util.{Failure, Success, Try}

abstract class HeaderParameter[T](spec: ParameterSpec[T]) extends Parameter[T] with Validatable[T, HttpRequest] with Bindable[T, RequestBinding] {
  override val name = spec.name
  override val description = spec.description
  override val paramType = spec.paramType

  override def -->(value: T) = Seq(new RequestBinding(this, in => {
    in.headers().add(name, spec.serialize(value))
    in
  }))

  val where = "header"

  def validate(request: HttpRequest) = {
    Option(request.headers().get(name)).map {
      v => Try(spec.deserialize(v)) match {
        case Success(d) => Right(Option(d))
        case Failure(_) => Left(this)
      }
    }.getOrElse(if (required) Left(this) else Right(None))
  }
}
