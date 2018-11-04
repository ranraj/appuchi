package com.yali

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import com.typesafe.config.ConfigFactory
import com.typesafe.scalalogging.StrictLogging
import com.yali.domain.repository._
import com.yali.domain.service._
import com.yali.intf.{Database, HttpServer, Migrator}

import scala.io.StdIn

object Boot extends App with StrictLogging {
    lazy implicit val system = ActorSystem()
    lazy implicit val materializer = ActorMaterializer()
    lazy implicit val executionContext = system.dispatcher

    lazy implicit val webToken = new JwtToken()

    lazy implicit val personRepo = new PersonRepository()
    lazy implicit val countryRepo = new CountryRepository()
    lazy implicit val taskRepo = new TaskRepository()
    lazy implicit val languageRepo = new LanguageRepository()
    lazy implicit val countryStateRepo = new CountryStateRepository()

    lazy implicit val taskService = new TaskService()
    lazy implicit val personService = new PersonService()
    lazy implicit val countryService= new CountryService()
    lazy implicit val languageService = new LanguageService()
    lazy implicit val countryStateService = new CountryStateService()

    val config = ConfigFactory.load()
    val dbConfig = config.getConfig("database")
    val dbPoolConfiguration = new Database(dbConfig)
    val serverBinding = new HttpServer().start()

    println("migrating...")
    new Migrator(dbConfig).flyway

    println("server is ready")
    StdIn.readLine()
    // Unbind from the port and shut down when done
    serverBinding
            .flatMap(_.unbind())
            .onComplete(_ => system.terminate())
}
