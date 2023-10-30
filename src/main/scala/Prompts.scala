package digital.blueinsight.funscala2023

import zio.*

object SamplePromptInstruction:
  import Prompts._
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
          s"""Do not refer to any incident before the date mentioned here: ${p.p}."""
        )
    case PayloadDirection(p: Payload) extends PromptMessage()

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

case class PromptService():
  def basicPrompt(): Task[String] = {
    ZIO.succeed(
      Prompts.getPrompt(SamplePromptInstruction.summarizePromptInstruction)
    )
  }
end PromptService

object PromptService {
  val live: ZLayer[Any, Throwable, PromptService] =
    ZLayer.fromFunction(PromptService.apply _)
}
