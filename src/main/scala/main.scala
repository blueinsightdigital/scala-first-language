package digital.blueinsight.funscala2023

object SamplePromptInstruction:
  import Prompts._
  def summarizePromptInstruction =
    Seq(PromptMessage.SummarizeDirection, PromptMessage.CreativeDirection, PromptMessage.TrueToTextDirection)
end SamplePromptInstruction

object Prompts:
  enum PromptMessage(message: String):
    case SummarizeDirection extends PromptMessage("""Summarize the below text into a short sentence of less than 15 words.""")
    case CreativeDirection extends PromptMessage("""Be creative.""")
    case TrueToTextDirection extends PromptMessage("""Be as close to the text below as possible.""")

    def getMessage: String = message
  end PromptMessage

  def getPrompt(promptMessages: Seq[PromptMessage]): String = 
    promptMessages.map(_.getMessage).mkString(" ")

  @main def basicPrompt() = {
    println(getPrompt(SamplePromptInstruction.summarizePromptInstruction))
  }
end Prompts