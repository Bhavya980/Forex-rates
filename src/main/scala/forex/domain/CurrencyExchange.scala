package forex.domain

case class CurrencyExchange(
                             from: String,
                             to: String,
                             bid: BigDecimal,
                             ask: BigDecimal,
                             price: BigDecimal,
                             time_stamp: String
                           )
