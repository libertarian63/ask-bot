import com.bot4s.telegram.models.Message
import slogging.StrictLogging

import scala.collection.mutable
import scala.concurrent.Future

trait SetState[S] extends StrictLogging {
  import java.io._

  val file: String

  private val state: mutable.Set[S] = {
    try {
      val input = new ObjectInputStream(new FileInputStream(file))
      input.readObject().asInstanceOf[mutable.Set[S]]
    } catch {
      case _: FileNotFoundException =>
        logger.info("Couldn't find that file. Create new file.")
        mutable.Set[S]()
    }
  }

  private def save(): Unit ={
    val stream = new ObjectOutputStream(new FileOutputStream(file))
    try {
      stream.writeObject(state)
    } catch {
      case e: Exception =>
        val writer = new StringWriter
        e.printStackTrace(new PrintWriter(writer))
    } finally {
      stream.close()
    }
  }

  def clear(): Unit = atomic {
    state.clear()
    save()
  }

  private def atomic[T](f: => T): T = state.synchronized {
    f
  }

  def withState(f: mutable.Set[S] => Future[Unit])(implicit msg: Message): Future[Unit] = {
    val result = atomic(f(state))
    save()
    result
  }
}