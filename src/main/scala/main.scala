package digital.blueinsight.funscala2023

import zio.*

object MainApp extends ZIOAppDefault {
  val myApp: ZIO[PromptService with FakeDBService, Throwable, Unit] =
    for {
      date <- Clock.currentDateTime
      _ <- ZIO.logInfo(s"Application started at $date")
      ticket <- FakeDBService().getTicket()
      result <- PromptService().basicPrompt()
      outcome <- OpenAIService().getOutcome()
      _ <- Console.printLine(ticket)
      _ <- Console.printLine(result)
      _ <- Console.printLine(outcome)
    } yield ()

  def run =
    myApp.provide(PromptService.live, FakeDBService.live, OpenAIService.live)
}
