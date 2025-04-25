import io.gatling.core.Predef._
import io.gatling.http.Predef._
import scala.concurrent.duration._

class OrderLoadTest extends Simulation {

  val httpProtocol = http
    .baseUrl("http://localhost:8080") // <-- адрес меняешь, если нужно
    .contentTypeHeader("application/json")

  val createOrder = exec(
    http("Create Order Request")
      .post("/api/orders")
      .body(StringBody(
        """{
          "productName": "LoadTestProduct",
          "orderTotal": 0,
          "customerId": 1
        }"""
      )).asJson
      .check(status.is(200))
  ).pause(50.millis) // небольшая реалистичная задержка

  val getOrders = exec(
    http("Get Orders Request")
      .get("/api/orders")
      .check(status.is(200))
  ).pause(50.millis)

  val scn = scenario("Create and Get Orders Scenario")
    .repeat(300) { // каждый пользователь сделает 300 действий (150 POST + 150 GET)
      createOrder
        .exec(getOrders)
    }

  setUp(
    scn.inject(
      rampUsersPerSec(5).to(100).during(40.seconds) // от 5 до 100 пользователей за 40 секунд
    )
  ).protocols(httpProtocol)
    .maxDuration(1.minute) // вся симуляция не больше 1 минуты
}
