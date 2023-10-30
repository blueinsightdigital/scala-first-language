package digital.blueinsight.funscala2023

import zio.*

object MainApp extends ZIOAppDefault {
  val myApp: ZIO[Any, Throwable, Unit] =
    for {
      date <- Clock.currentDateTime
      _ <- ZIO.logInfo(s"Application started at $date")
      result <- PromptService().basicPrompt()
      _ <- Console.printLine(result)
    } yield ()

  def run = myApp.provideLayer(PromptService.live)
}
