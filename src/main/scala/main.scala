package digital.blueinsight.funscala2023

import zio.*
import TicketFree.*

object MainApp extends ZIOAppDefault {
  val myApp: ZIO[
    PromptService with FakeDBService with OpenAIService,
    Throwable,
    Unit
  ] =
    for {
      date <- Clock.currentDateTime
      _ <- ZIO.logInfo(s"Application started at $date")
      ticket <- FakeDBService().getTicket()
      // For debugging, we print the prompt below to the console
      resultProgram = PromptService().basicProgram(ticket)
      outcome <- OpenAIService().getOutcomeForTurn(
        resultProgram
      )
      _ <- Console.printLine(ticket)
      _ <- Console.printLine("")
      _ <- Console.printLine(resultProgram)
      _ <- Console.printLine(outcome)
    } yield ()

  def run =
    myApp.provide(
      PromptService.live,
      FakeDBService.live,
      OpenAIService.live
    )
}
