package com.example.helloscala.impl

import com.example.helloscala.api.HelloscalaService
import com.lightbend.lagom.scaladsl.api.{AdditionalConfiguration, ProvidesAdditionalConfiguration}
import com.lightbend.lagom.scaladsl.devmode.LagomDevModeComponents
import com.lightbend.lagom.scaladsl.persistence.cassandra.CassandraPersistenceComponents
import com.lightbend.lagom.scaladsl.server._
import com.softwaremill.macwire._
import com.typesafe.conductr.bundlelib.lagom.scaladsl.ConductRApplicationComponents
import com.typesafe.config.ConfigFactory
import play.api.Configuration
import play.api.libs.ws.ahc.AhcWSComponents

import scala.util.Try

class HelloscalaLoader extends LagomApplicationLoader {

  override def load(context: LagomApplicationContext): LagomApplication =
    new HelloscalaApplication(context) with ConductRApplicationComponents {
      conductRConfiguration.entrySet.map(_.toString).filter(in => in.contains("seed") || in.contains("join-self")).foreach(x => println(s"fooooConductr : $x"))
      configuration.entrySet.map(_.toString).filter(in => in.contains("seed") || in.contains("join-self")).foreach(x => println(s"fooooFINAL : $x"))
    }

  trait foo extends ProvidesAdditionalConfiguration {
    lazy val x: Configuration = Configuration(ConfigFactory.parseString(
      """
        |akka.cluster.seed-nodes.0="akka.tcp://helloscala-impl-1@192.168.10.1:10410"
        |foo.seed=bar
      """.stripMargin))
    override def additionalConfiguration: AdditionalConfiguration = super.additionalConfiguration ++ x
  }

  override def loadDevMode(context: LagomApplicationContext): LagomApplication =
    new HelloscalaApplication(context) with LagomDevModeComponents with foo {
      (1 to 3).foreach(_ => println("-" * 40))

      private val listx = x.getList("akka.cluster.seed-nodes").get.unwrapped()
      println("Is akka.cluster.seed-nodes provided? " + listx.get(0))
      private val list = configuration.getList("akka.cluster.seed-nodes").get.unwrapped()
      println("Is akka.cluster.seed-nodes maintained? " + Try(list.get(0)))
      (1 to 3).foreach(_ => println("*" * 40))
    }

  override def describeServices = List(
    readDescriptor[HelloscalaService]
  )
}


abstract class HelloscalaApplication(context: LagomApplicationContext)
  extends LagomApplication(context)
    with CassandraPersistenceComponents
    with AhcWSComponents {

  // Bind the services that this server provides
  override lazy val lagomServer = LagomServer.forServices(
    bindService[HelloscalaService].to(wire[HelloscalaServiceImpl])
  )

  // Register the JSON serializer registry
  override lazy val jsonSerializerRegistry = HelloscalaSerializerRegistry

  // Register the helloscala persistent entity
  persistentEntityRegistry.register(wire[HelloscalaEntity])
}
