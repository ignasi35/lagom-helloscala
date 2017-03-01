package com.example.helloscala.impl

import com.lightbend.lagom.scaladsl.persistence.cassandra.CassandraPersistenceComponents
import com.lightbend.lagom.scaladsl.server._
import com.lightbend.lagom.scaladsl.devmode.LagomDevModeComponents
import play.api.libs.ws.ahc.AhcWSComponents
import com.example.helloscala.api.HelloscalaService
import com.softwaremill.macwire._
import com.typesafe.conductr.bundlelib.lagom.scaladsl.ConductRApplicationComponents

class HelloscalaLoader extends LagomApplicationLoader {

  override def load(context: LagomApplicationContext): LagomApplication =
    new HelloscalaApplication(context) with ConductRApplicationComponents

  override def loadDevMode(context: LagomApplicationContext): LagomApplication =
    new HelloscalaApplication(context) with LagomDevModeComponents

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
