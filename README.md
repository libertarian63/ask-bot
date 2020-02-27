# Ask bot
## Сборка 
```bash
sbt assembly
```

## Запуск
После сборки в target/scala-2.12 появится исполняемый файл с помощью которого можно запустить бота:

```
Usage: java -jar ask-bot.jar --token <string> [--socksProxy] [--host <string>] [--port <string>] --admins <string> [--admins <string>]... [--file <string>]

Bot for asking questions to content creators

Options and flags:
    --help
        Display this help text.
    --token <string>
        Telegram bot token.
    --socksProxy
        Use SOCKS proxy. By default used tor proxy with default host and port
    --host <string>
        Proxy host.
    --port <string>
        Proxy port.
    --admins <string>
        List of admins.
    --file <string>
        File for save subscriptions state.
```
