import io.gatling.core.Predef._
import io.gatling.http.Predef._
import scala.concurrent.duration._

class OrderLightLoadTest extends Simulation {

  val httpProtocol = http
    .baseUrl("http://localhost:8080")
    .contentTypeHeader("application/json")

  val createOrder = exec(
    http("Create Order Request")
      .post("/api/orders")
      .body(StringBody(
        """{
          "productName": "LightLoadProduct",
          "orderTotal": 0,
          "customerId": 1
        }"""
      )).asJson
      .check(status.is(200))
  ).pause(200.millis)

  val getOrders = exec(
    http("Get Orders Request")
      .get("/api/orders")
      .check(status.is(200))
  ).pause(200.millis)

  val scn = scenario("Light Create and Get Orders Scenario")
    .repeat(100) {
      createOrder
        .exec(getOrders)
    }

  setUp(
    scn.inject(
      rampUsers(10) during (30.seconds)
    )
  ).protocols(httpProtocol)
    .maxDuration(30.seconds)
}