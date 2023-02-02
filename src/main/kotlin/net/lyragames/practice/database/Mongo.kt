package net.lyragames.practice.database

import com.mongodb.*
import com.mongodb.client.MongoCollection
import com.mongodb.client.MongoDatabase
import org.bson.Document
import java.io.Closeable

class Mongo (private val dbName: String) : Closeable {


    lateinit var client: MongoClient
    lateinit var database: MongoDatabase

    lateinit var profiles: MongoCollection<Document>
    lateinit var arenaRatings: MongoCollection<Document>

    fun load(credentials: MongoCredentials) {
        client =  if (credentials.useUri) {
            MongoClient(MongoClientURI(credentials.uri!!))
        }else if (credentials.shouldAuthenticate()) {
           // val serverAddress = ServerAddress(credentials.host, credentials.port)
          //  val credential = MongoCredential.createCredential(credentials.username!!, dbName, credentials.password!!.toCharArray())
         //   MongoClient(serverAddress, credential, MongoClientOptions.builder().build())

            MongoClient(MongoClientURI("mongodb://${credentials.username}:${credentials.password}@${credentials.host}:${credentials.port}"))

        } else {
            MongoClient(MongoClientURI("mongodb://${credentials.host}:${credentials.port}"))
        }

        database = client.getDatabase(dbName)

        profiles = database.getCollection("profiles")
        arenaRatings = database.getCollection("arenaRatings")
    }

    override fun close() {
        client.close()
    }

}