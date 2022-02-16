package net.lyragames.practice.database

import com.mongodb.MongoClient
import com.mongodb.MongoClientURI
import com.mongodb.client.MongoCollection
import com.mongodb.client.MongoDatabase
import org.bson.Document


/**
 * This Project is property of Zowpy © 2022
 * Redistribution of this Project is not allowed
 *
 * @author Zowpy
 * Created: 2/15/2022
 * Project: lPractice
 */

class PracticeMongo(uri: String) {

    private var mongoClient: MongoClient
    private var mongoDatabase: MongoDatabase
    var profiles: MongoCollection<Document>

    init {
        mongoClient = MongoClient(MongoClientURI(uri))
        mongoDatabase = mongoClient.getDatabase("lpractice")
        profiles = mongoDatabase.getCollection("profiles")
    }
}