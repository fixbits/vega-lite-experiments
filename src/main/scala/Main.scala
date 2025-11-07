import com.raquo.laminar.api.L._
import org.scalajs.dom
import scala.scalajs.js
import scala.scalajs.js.annotation.JSImport
import scala.scalajs.js.{Promise, undefined}
import zio._

@js.native
@JSImport("vega-embed", JSImport.Default)
object VegaEmbed extends js.Object {
  def apply(el: dom.Element, spec: js.Any, options: js.UndefOr[js.Any] = js.undefined): Promise[js.Dynamic] = js.native
}
object Main {

  def promiseToZIO[A](p: Promise[A]): Task[A] =
    ZIO.async[Any, Throwable, A] { callback =>
      p.`then`[Unit](
        (v: A) => { callback(ZIO.succeed(v)); () },
        js.defined((err: Any) => { callback(ZIO.fail(js.JavaScriptException(err))); () })
      )
      ()
    }

  def main(args: Array[String]): Unit = {
    val selectedVar = Var("")

    def embedChart(): Unit = {
      val chartEl = dom.document.getElementById("chart")
      if (chartEl == null) {
        dom.console.error("Missing #chart element")
      } else {
        val embedEffect = for {
          res <- promiseToZIO(VegaEmbed(chartEl, chartSpec))
          view = res.selectDynamic("view")
          _ <- ZIO.attempt {
            val cb: js.Function2[String, js.Any, Unit] =
              (_: String, value: js.Any) => {
                val s = if (js.isUndefined(value) || value == null) "" else {
                  // Try to extract id if available, otherwise stringify
                  try {
                    val id = value.asInstanceOf[js.Dynamic].id
                    if (js.isUndefined(id) || id == null) js.JSON.stringify(value) else id.toString
                  } catch {
                    case _: Throwable => js.JSON.stringify(value)
                  }
                }
                selectedVar.set(s)
              }
            view.addSignalListener("sel", cb)
          }
        } yield ()
        
        Unsafe.unsafe { implicit u =>
          Runtime.default.unsafe.runToFuture(embedEffect)
        }
      }
    }

    val app = div(
      h2("Laminar + ZIO + Vega Lite demo"),
      div(className := "controls",
        button("Render chart", onClick.mapTo(()) --> { _ => embedChart() })
      ),
      div(idAttr := "chart"),
      div(className := "selected-display",
        label("selected: "),
        input(
          typ := "text",
          value <-- selectedVar.signal,
          readOnly := true,
          width := "420px"
        )
      )
    )

    render(dom.document.getElementById("app"), app)
  }

  val chartSpec: js.Object = {
    import scala.scalajs.js.JSConverters._
    js.Dynamic.literal(
      "$schema" -> "https://vega.github.io/schema/vega-lite/v5.json",
      "width" -> 600,
      "height" -> 320,
      "data" -> js.Dynamic.literal(
        "values" -> js.Array(
          js.Dynamic.literal("x" -> 1, "y" -> 1, "id" -> "A"),
          js.Dynamic.literal("x" -> 2, "y" -> 2, "id" -> "A"),
          js.Dynamic.literal("x" -> 3, "y" -> 3, "id" -> "A"),
          js.Dynamic.literal("x" -> 1, "y" -> 3, "id" -> "B"),
          js.Dynamic.literal("x" -> 2, "y" -> 2.5, "id" -> "B"),
          js.Dynamic.literal("x" -> 3, "y" -> 2, "id" -> "B")
        )
      ),
      "mark" -> js.Dynamic.literal("type" -> "line", "point" -> true),
      "selection" -> js.Dynamic.literal(
        "sel" -> js.Dynamic.literal(
          "type" -> "single",
          "fields" -> js.Array("id"),
          "on" -> "click",
          "clear" -> "dblclick"
        )
      ),
      "encoding" -> js.Dynamic.literal(
        "x" -> js.Dynamic.literal("field" -> "x", "type" -> "quantitative"),
        "y" -> js.Dynamic.literal("field" -> "y", "type" -> "quantitative"),
        "detail" -> js.Dynamic.literal("field" -> "id", "type" -> "nominal"),
        "color" -> js.Dynamic.literal(
          "condition" -> js.Dynamic.literal(
            "test" -> "sel.id == datum.id",
            "field" -> "id",
            "type" -> "nominal"
          ),
          "value" -> "lightgray"
        ),
        "opacity" -> js.Dynamic.literal(
          "condition" -> js.Dynamic.literal(
            "test" -> "sel.id == datum.id",
            "value" -> 1
          ),
          "value" -> 0.3
        ),
        "strokeWidth" -> js.Dynamic.literal(
          "condition" -> js.Dynamic.literal(
            "test" -> "sel.id == datum.id",
            "value" -> 4
          ),
          "value" -> 2
        )
      )
    )
  }


}
