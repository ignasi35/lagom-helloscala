package com.example.helloscala.impl

import com.lightbend.lagom.scaladsl.api.ServiceCall
import com.lightbend.lagom.scaladsl.persistence.PersistentEntityRegistry
import com.example.helloscala.api.HelloscalaService

/**
  * Implementation of the HelloscalaService.
  */
class HelloscalaServiceImpl(persistentEntityRegistry: PersistentEntityRegistry) extends HelloscalaService {

  override def hello(id: String) = ServiceCall { _ =>
    // Look up the helloscala entity for the given ID.
    val ref = persistentEntityRegistry.refFor[HelloscalaEntity](id)

    // Ask the entity the Hello command.
    ref.ask(Hello(id, None))
  }

  override def useGreeting(id: String) = ServiceCall { request =>
    // Look up the helloscala entity for the given ID.
    val ref = persistentEntityRegistry.refFor[HelloscalaEntity](id)

    // Tell the entity to use the greeting message specified.
    ref.ask(UseGreetingMessage(request.message))
  }
}
