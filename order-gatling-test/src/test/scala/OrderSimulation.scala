import io.gatling.core.Predef._
import io.gatling.http.Predef._
import scala.concurrent.duration._

class OrderLoadTest extends Simulation {

  val httpProtocol = http
    .baseUrl("http://localhost:8080")
    .contentTypeHeader("application/json")

  val createOrder = exec(
    http("Create Order Request")
      .post("/api/orders")
      .body(StringBody(
        """{
          "productName": "LoadTestProduct",
          "orderTotal": 0,
          "customerId": 2
        }"""
      )).asJson
      .check(status.is(200))
  ).pause(50.millis)

  val getOrders = exec(
    http("Get Orders Request")
      .get("/api/orders")
      .check(status.is(200))
  ).pause(50.millis)

  val scn = scenario("Create and Get Orders Scenario")
    .repeat(10) {
      createOrder
        .exec(getOrders)
    }

  setUp(
    scn.inject(
      rampUsersPerSec(5).to(20).during(30.seconds)
    )
  ).protocols(httpProtocol)
    .maxDuration(1.minute)
}
