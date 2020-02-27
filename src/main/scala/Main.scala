import cats.implicits._
import com.monovore.decline._
import slogging.LogLevel
import slogging.LoggerConfig
import slogging.PrintLoggerFactory

import scala.concurrent.Await
import scala.concurrent.duration.Duration
import scala.io.StdIn

object Main extends CommandApp (
  name = "ask-bot",
  header = "Bot for asking questions to content creators",
  main = {
    val tokenOpt = Opts.option[String]("token", help = "Telegram bot token.")
    val adminsOpt = Opts.options[String]("admins", help = "List of admins.")
    val fileOpt = Opts.option[String]("file", help = "File for save subscriptions state.").withDefault("state")
    val proxyFlag = Opts.flag("socksProxy", help = "Use SOCKS proxy. By default used tor proxy with default host and port").orFalse
    val proxyHostOpt = Opts.option[String]("host", help = "Proxy host.").withDefault("127.0.0.1")
    val proxyPortOpt = Opts.option[String]("port", help = "Proxy port.").withDefault("9150")

    (tokenOpt, proxyFlag, proxyHostOpt, proxyPortOpt, adminsOpt, fileOpt).mapN { (token, proxy, proxyHost, proxyPort, admins, file) => {
        if (proxy) {
          System.setProperty("socksProxyHost", proxyHost)
          System.setProperty("socksProxyPort", proxyPort)
        }
        val bot = new AskBot(token, admins, file)
        val eol = bot.run()

        println("Press [ENTER] to shutdown the bot, it may take a few seconds...")
        StdIn.readLine()

        bot.shutdown() // initiate shutdown
        // Wait for the bot end-of-life
        Await.result(eol, Duration.Inf)
        println("Bot stopped.")
      }
    }
  }
) {
  LoggerConfig.factory = PrintLoggerFactory()
  LoggerConfig.level = LogLevel.DEBUG
}
