import com.itechart.bitools.DatabaseProvider.db
import com.itechart.bitools._
import javax.servlet.ServletContext
import org.scalatra._

class ScalatraBootstrap extends LifeCycle {
  override def init(context: ServletContext) {
    context.mount(new BiServlet, "/*")
  }

  override def destroy(context: ServletContext): Unit = db.close()
}
