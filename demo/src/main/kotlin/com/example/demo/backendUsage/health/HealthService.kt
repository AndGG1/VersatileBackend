package com.example.demo.backendUsage.health

import com.example.demo.backendUsage.log
import com.example.demo.buisnessUsage.vectorDb.structure.QdrantClientConfig
import com.mongodb.client.MongoClient
import com.mongodb.lang.NonNull
import io.qdrant.client.grpc.Collections
import org.springframework.beans.factory.annotation.Value
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Service
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.ResolverStyle

data class Status(
    @NonNull val cloudServiceName: String,
    @NonNull val isAvailable: Boolean,
    val additionalInfo: String
) {
    override fun toString(): String {
        return """
            <div style="margin-bottom: 16px; padding: 10px; border-left: 4px solid ${if (isAvailable) "#28a745" else "#dc3545"}; background-color: #f8f9fa;">
                <b>Cloud Service Name:</b> $cloudServiceName<br/>
                <b>Availability:</b> $isAvailable<br/>
                <b>Additional Info:</b> $additionalInfo
            </div>
        """.trimIndent()
    }
}

@Service
class HealthService(private val getAllVectorClients: List<QdrantClientConfig>,
    private val mongoClient: MongoClient,
    private val redisTemplate: RedisTemplate<String, Any>) {

    private val dateTimeFormatter = DateTimeFormatter
        .ofPattern("uuuu-MM-dd HH:mm")
        .withResolverStyle(ResolverStyle.SMART)

    @Value("\${mongo.db.name}")
    private lateinit var databaseName: String

    @Value("\${mongo.db.collection}")
    private lateinit var collectionName: String

    fun checkQdrant() : MutableList<Status> {
        val res = mutableListOf<Status>()
            getAllVectorClients.forEach { it ->
                try {
                    val collectionInfo = it.client.getCollectionInfoAsync(it.collectionName).get()
                    res.add(
                        Status(
                            "Qdrant Shard hosted on: ${it.endpoint}",
                            collectionInfo.status == Collections.CollectionStatus.Green,
                            "Up to this point(${LocalDateTime.now().format(dateTimeFormatter).toString().replace("T", " ")})," +
                                    " this collection contains ${collectionInfo.pointsCount} points, " +
                                    "this is ${collectionInfo.vectorsCount} vectors."
                        )
                    )
                } catch (e: Exception) {
                    log.error(e.message)
                    res.add(Status("Qdrant Shard hosted on: ${it.endpoint}",
                        false,
                        "Couldn't retrieve additional info. as collection is not responsive.")
                    )
            }
        }
        return res
    }

    fun checkMongo() : Status {
        return try {
            val database = mongoClient.getDatabase(databaseName)
            val collection = database.getCollection(collectionName)

            Status("Mongo Cloud Database",
                true,
                "Up to this point(${LocalDateTime.now().format(dateTimeFormatter).toString().replace("T", " ")}), " +
                        "this collection contains ${collection.estimatedDocumentCount()} users.")
        } catch (e: Exception) {
            log.error(e.message)
            Status("Mongo Cloud Database",
                false,
                "Couldn't retrieve additional info. as collection is not responsive.")
        }
    }

    fun checkRedis() : Status {
        return try {
            val availability = redisTemplate.connectionFactory?.connection?.ping()

            val keys: Set<String> = redisTemplate.keys("*")
            Status("Redis Cache (hosted on Render)",
                availability != null,
                "Up to this point(${LocalDateTime.now().format(dateTimeFormatter).toString().replace("T", " ")})," +
                        " there are ${keys.size} keys stored in your Redis Cache.")
        } catch (e: Exception) {
            log.error(e.message)
            Status("Redis Cache (hosted on Render)",
                false,
                "Couldn't retrieve additional information, as cache service is not responsive.")
        }
    }
}