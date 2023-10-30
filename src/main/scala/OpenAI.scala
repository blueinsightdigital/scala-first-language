package digital.blueinsight.funscala2023

import zio.*
import scala.concurrent.{Await, ExecutionContext, Future}
import akka.stream.*
import akka.actor.*
import io.cequence.openaiscala.service.*
import io.cequence.openaiscala.domain.response.*
import io.cequence.openaiscala.domain.ModelId
import com.typesafe.config.*

import io.cequence.openaiscala.domain.settings.CreateCompletionSettings
import io.cequence.openaiscala.domain.{ChatRole, MessageSpec}
import io.cequence.openaiscala.domain.settings.CreateChatCompletionSettings

import scala.concurrent.*
import scala.concurrent.duration.Duration

import cats.arrow.FunctionK
import cats.{Id, ~>}
import cats.free.Free
import cats.free.Free.liftF
import scala.collection.mutable

import cats.implicits.catsStdInstancesForFuture
import cats.instances.all.catsStdInstancesForFuture
import cats.instances.future.catsStdInstancesForFuture

object GlobalConstants {
  val model = ModelId.gpt_3_5_turbo
}

object TicketFree {
  // 1. Create an ADT to represent our grammar. Each rule corresponds to System, User, Assistant Messages.
  // Accumulated response is added back to the mutable Seq of Message Spec to continue the context.
  sealed trait ChatStoreA[A]
  case class SystemSays(content: String) extends ChatStoreA[Unit]
  case class UserSays(content: String) extends ChatStoreA[Unit]
  case class AssistantSays(content: String) extends ChatStoreA[Unit]

  case object ExecuteChat extends ChatStoreA[String]

//  Seq(
//    MessageSpec(role = ChatRole.System, content = "You are a helpful assistant."),
//    MessageSpec(role = ChatRole.User, content = "Who won the world series in 2020?"),
//    MessageSpec(role = ChatRole.Assistant, content = "The Los Angeles Dodgers won the World Series in 2020."),
//    MessageSpec(role = ChatRole.User, content = prompt))

  // 2. Create a type based on Free[_] and KVStoreA[_]

  type ChatStore[A] = Free[ChatStoreA, A]

  // 3. Create smart constructors for KVStore[_] using liftF.

  // SystemSays returns nothing (i.e. Unit). Mutable Seq is updated
  def systemSays(content: String): ChatStore[Unit] =
    liftF[ChatStoreA, Unit](SystemSays(content))

  def userSays(content: String): ChatStore[Unit] =
    liftF[ChatStoreA, Unit](UserSays(content))

  def assistantSays(content: String): ChatStore[Unit] =
    liftF[ChatStoreA, Unit](AssistantSays(content))

  def executeChat(): ChatStore[String] =
    liftF[ChatStoreA, String](ExecuteChat)

  // 3. Build a program
  // anywhere in the code

  // the program will crash if a type is incorrectly specified.
  def impureCompiler: ChatStoreA ~> Future = {
    new (ChatStoreA ~> Future) {

      // a very simple (and imprecise) chat archive
      var chats = scala.collection.mutable.ListBuffer[MessageSpec]()

      def apply[A](fa: ChatStoreA[A]): Future[A] = {
        fa match {
          case SystemSays(content) =>
            println(s"system says $content")
            chats += MessageSpec(role = ChatRole.System, content = content)
            Future.unit
          case UserSays(content) =>
            println(s"user says $content")
            chats += MessageSpec(role = ChatRole.User, content = content)
            // get response and place it inside ?
            Future.unit
          case AssistantSays(content) =>
            println(s"assistant says $content")
            chats += MessageSpec(role = ChatRole.Assistant, content = content)
            Future.unit
          case ExecuteChat =>
            println(s"executing chat")
            helperChatAI(chats.toSeq).asInstanceOf[Future[A]]
        }
      }
    }
  }

  def getOutcome(program: ChatStore[Future[String]]) = {
    implicit val ec = ExecutionContext.global
    implicit val materializer = Materializer(ActorSystem())

    val outcome: Future[String] = program.foldMap(impureCompiler).flatten
    outcome.asInstanceOf[Future[String]]
  }

  // helper function to execute the chat for a prompt
  def helperChatAI(messages: Seq[MessageSpec]): Future[String] = {
    implicit val ec = ExecutionContext.global
    implicit val materializer = Materializer(ActorSystem())

    val config = ConfigFactory.load("openai-scala-client.conf")
    val service = OpenAIServiceFactory(config)

    println(messages.length)

    val createChatCompletionSettings = CreateChatCompletionSettings(
      model = GlobalConstants.model
    )

    val result =
      service
        .createChatCompletion(
          messages = messages,
          settings = createChatCompletionSettings
        )
        .map { chatCompletion =>
          chatCompletion.choices.head.message.content
        }

    // service.close()

    result
  }
}

trait OpenAIServiceCore:
  def getOutcome(prompt: Seq[String]): Task[String]
end OpenAIServiceCore

case class OpenAIService() extends OpenAIServiceCore:
  def getOutcome(prompt: Seq[String] = Seq()): Task[String] = {
    import TicketFree._

    implicit val ec = ExecutionContext.global
    implicit val materializer = Materializer(ActorSystem())

    val program: ChatStore[Future[String]] =
      for {
        _ <- systemSays("You are a helpful assistant.")
        _ <- userSays("Who won the world series in 2020?")
        _ <- assistantSays(
          "The Los Angeles Dodgers won the World Series in 2020."
        )
        _ <- userSays("Who won the most recent cricket world cup?")
        result <- executeChat()
      } yield Future(result)

    ZIO.fromFuture(_ => TicketFree.getOutcome(program))

    // ZIO.succeed("This is the outcome")
  }
end OpenAIService

object OpenAIService:
  val live: ZLayer[Any, Throwable, OpenAIService] =
    ZLayer.fromFunction(OpenAIService.apply _)
end OpenAIService
