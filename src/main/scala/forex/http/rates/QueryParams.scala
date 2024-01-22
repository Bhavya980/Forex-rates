package forex.http.rates

import forex.domain.Currency
import org.http4s.dsl.impl.OptionalQueryParamDecoderMatcher
import org.http4s.{ParseFailure, QueryParamDecoder}

object QueryParams {

  implicit val fromString: QueryParamDecoder[Currency] =
    QueryParamDecoder[String].emap { s =>
      Currency.fromString(s) match {
        case Some(currency) => Right(currency)
        case None => Left(ParseFailure(s"Invalid currency: $s", s"Invalid currency: $s"))
      }
    }

  implicit val optionQueryParamDecoder: QueryParamDecoder[Option[Currency]] =
    QueryParamDecoder[String].map(s => Currency.fromString(s))

  object FromQueryParam extends OptionalQueryParamDecoderMatcher[Currency]("from")
  object ToQueryParam extends OptionalQueryParamDecoderMatcher[Currency]("to")

  object MaybeFromQueryParam extends OptionalQueryParamDecoderMatcher[Option[Currency]]("from")
  object MaybeToQueryParam extends OptionalQueryParamDecoderMatcher[Option[Currency]]("to")

}
