import cats._
import cats.data.NonEmptyList
import cats.implicits._
import com.bot4s.telegram.api.declarative.Commands
import com.bot4s.telegram.cats.Polling
import com.bot4s.telegram.cats.TelegramBot
import com.bot4s.telegram.methods.SendMessage
import com.bot4s.telegram.models.Chat
import com.bot4s.telegram.models.User
import com.softwaremill.sttp.okhttp.OkHttpFutureBackend

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

class AskBot(val token: String, val admins: NonEmptyList[String], val file: String)
    extends TelegramBot(token = token, backend = OkHttpFutureBackend())
    with Polling[Future]
    with Commands[Future]
    with SetState[Long] {


  def isAdmin: User => Boolean = _.username.fold(false)(admins.contains_(_))

  onCommand("subscribe") { implicit msg =>
    withState { subscriptions =>
      msg.from match {
        case Some(user: User) if isAdmin(user) =>
          subscriptions += msg.chat.id
          reply("Successfully subscribed.").void
        case _ => monad.pure()
      }
    }
  }

  onCommand("ask") { implicit msg =>
    withState { subscriptions =>
      msg.text match {
        case Some(text) =>
          subscriptions.foreach(subscription => request(SendMessage(subscription, s"New question: ${text.drop(4)}")))
          reply("Thank you for question.").void
        case _ => reply("Incorrect question.").void
      }
    }
  }


  onCommand("help") { implicit msg =>
    val help: String = msg.from match {
      case Some(user: User) if isAdmin(user) =>
        """
          |/subscribe for subscribe current chat to question
          |""".stripMargin
      case _ =>
        """
          |/ask or /question for send question to send question. For example:
          |/ask Is it worth buying bitcoins?
          |""".stripMargin
    }

    reply(help).void
  }
}
