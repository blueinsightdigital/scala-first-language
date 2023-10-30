package digital.blueinsight.funscala2023

import zio.*
import scala.util.Random

enum FakeTicket(content: String = """"""):
  case KitchenAidMixer
      extends FakeTicket(
        """I purchased this mixer from KitchenAid a few weeks ago and found it ridiculously useful to juice up my day"""
      )
  case LongerPositive
      extends FakeTicket(
        """If you're a beginner baker like myself, I highly recommend this artisan model. It's user friendly, and I like how it came with the three different attachments and a pouring shield as well. Also the 5 quart is honestly enough room for an at home baker. However, if you plan to be baking huge batches for a commercial business this one may not be for you and you may want to opt out for a bigger mixer."""
      )
  case ShortNegative
      extends FakeTicket(
        """I hate this mixer. I cut my hand while using it and I could not return it. I am very disappointed."""
      )

  def getContent() = this.content
end FakeTicket

trait FakeDBCore:
  def getTicket(): Task[String]
end FakeDBCore

case class FakeDBService() extends FakeDBCore:
  def getTicket(): Task[String] = {
    val r = Random()
    // does not work in scala 3
    // ZIO.succeed(FakeTicket(r.nextInt(FakeTicket.maxId())))
    r.nextInt(3) match {
      case 0 => ZIO.succeed(FakeTicket.KitchenAidMixer.getContent())
      case 1 => ZIO.succeed(FakeTicket.ShortNegative.getContent())
      case 2 => ZIO.succeed(FakeTicket.LongerPositive.getContent())
    }
  }
end FakeDBService

object FakeDBService:
  val live: ZLayer[Any, Throwable, FakeDBService] =
    ZLayer.fromFunction(FakeDBService.apply _)
end FakeDBService
