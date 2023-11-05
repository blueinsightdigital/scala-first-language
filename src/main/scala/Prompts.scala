package digital.blueinsight.funscala2023

import zio.*
import TicketFree.*

object SamplePromptInstruction:
  import Prompts._

  def ticketPromptInstruction(ticket: String): Seq[PromptMessage] = Seq(
    PromptMessage.TicketCSMContext,
    PromptMessage.TrueToTextDirection,
    PromptMessage.TicketJSONPayloadContext,
    PromptMessage.PayloadDirection(
      Payload(
        "\n'''" + """{"user_name": "Sisir Koppaka", "agent_name": "Raphael Johnson", "loyalty_tier": "Gold"}""" + "'''"
      )
    ),
    PromptMessage.PayloadDirection(
      Payload(
        "\n```" + ticket + "```"
      )
    )
  )

  def summarizePromptInstruction: Seq[PromptMessage] =
    Seq(
      PromptMessage.SummarizeDirection,
      PromptMessage.CreativeDirection,
      PromptMessage.TrueToTextDirection,
      PromptMessage.CutoffReferenceTime(Payload("1950")),
      PromptMessage.PayloadDirection(
        Payload(
          "\n```The weather was super hot, and it made the travellers dreary and forlorn.```"
        )
      )
    )

end SamplePromptInstruction

object Prompts:
  enum PromptMessage(message: String = """"""):
    case PreambleDirection(p: Payload) extends PromptMessage()
    case SummarizeDirection
        extends PromptMessage(
          """Summarize the below text enclosed within triple backticks into a short sentence of less than 15 words."""
        )
    case CreativeDirection extends PromptMessage("""Be creative.""")
    case TrueToTextDirection
        extends PromptMessage("""Be as close to the text below as possible.""")
    case CutoffReferenceTime(p: Payload)
        extends PromptMessage(
          s"""Do not refer to any incident before the cutoff date of ${p.p}."""
        )
    case PayloadDirection(p: Payload) extends PromptMessage()
    case TicketCSMContext
        extends PromptMessage(
          """You are the Customer Success Manager for a well-known company in the Kitchen tools space. Your objective is to respond to the customer and resolve the feedback from the customer shared with you enclosed by triple ```. Do your best response, and keep it short and informal."""
        )
    case TicketJSONPayloadContext
        extends PromptMessage(
          """Related information about the context of the ticket, such as the user name, your agent name, loyalty tier of the customer is provided below enclosed between triple '. """
        )

    def getMessage: String = this match {
      case PreambleDirection(p) => p.p
      case PayloadDirection(p)  => p.p
      case _                    => message
    }
  end PromptMessage

  case class Payload(p: String = "")

  def getPrompt(promptMessages: Seq[PromptMessage]): String =
    promptMessages.map(_.getMessage).mkString(" ")

end Prompts

trait PromptServiceCore:
  def basicPrompt(): Task[String]
  def basicProgram(ticket: String): ChatStore[Task[String]]
end PromptServiceCore

case class PromptService() extends PromptServiceCore:
  def basicPrompt(): Task[String] = {
    ZIO.succeed(
      Prompts.getPrompt(SamplePromptInstruction.summarizePromptInstruction)
    )
  }
  def basicProgram(ticket: String): ChatStore[Task[String]] = {
    lazy val program: ChatStore[Task[String]] =
      for {
        _ <- systemSays("You are a helpful assistant.")
        _ <- userSays(
          Prompts.getPrompt(
            SamplePromptInstruction.ticketPromptInstruction(ticket)
          )
        )
        result <- executeChat()
      } yield ZIO.succeed(result)
    program
  }

end PromptService

object PromptService {
  val live: ZLayer[Any, Throwable, PromptService] =
    ZLayer.fromFunction(PromptService.apply _)
}
