package digital.blueinsight.funscala2023

object SamplePromptInstruction:
  import Prompts._
  def summarizePromptInstruction: Seq[PromptMessage] =
    Seq(PromptMessage.SummarizeDirection, 
    PromptMessage.CreativeDirection, 
    PromptMessage.TrueToTextDirection, 
    PromptMessage.CutoffReferenceTime(Payload("1950")),
    PromptMessage.PayloadDirection(Payload("\n```The weather was super hot, and it made the travellers dreary and forlorn.```")))
end SamplePromptInstruction

object Prompts:
  enum PromptMessage(message: String=""""""):
    case SummarizeDirection extends PromptMessage("""Summarize the below text enclosed within triple backticks into a short sentence of less than 15 words.""")
    case CreativeDirection extends PromptMessage("""Be creative.""")
    case TrueToTextDirection extends PromptMessage("""Be as close to the text below as possible.""")
    case CutoffReferenceTime(p: Payload) extends PromptMessage(s"""Do not refer to any incident before the date mentioned here: ${p.p}.""")
    case PayloadDirection(p: Payload) extends PromptMessage()

    def getMessage: String = this match {
      case PayloadDirection(p) => p.p
      case _ => message
    }
  end PromptMessage

  case class Payload(p: String = "") 

  def getPrompt(promptMessages: Seq[PromptMessage]): String = 
    promptMessages.map(_.getMessage).mkString(" ")

  @main def basicPrompt() = {
    println(getPrompt(SamplePromptInstruction.summarizePromptInstruction))
  }
end Prompts