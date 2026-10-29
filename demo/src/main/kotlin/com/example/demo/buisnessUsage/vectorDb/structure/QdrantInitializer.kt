package com.example.demo.buisnessUsage.vectorDb.structure

import com.example.demo.backendUsage.health.NotifierService
import com.example.demo.backendUsage.log
import io.qdrant.client.grpc.Collections
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.ApplicationArguments
import org.springframework.boot.ApplicationRunner
import org.springframework.stereotype.Component

@Component
class QdrantInitializer(
    private val getAllVectorClients: AllVectorClients,
    private val notifierService: NotifierService) : ApplicationRunner {

    @Value("\${qdrant.to.initialize}")
    private lateinit var toInitialize: String

    override fun run(args: ApplicationArguments?) {
        try {
            log.info("INTERNAL BACKEND OPERATION: Qdrant Collections are being configured for filtering.")

            if (toInitialize == "TRUE") {
                getAllVectorClients.getAllVectorClients().forEach { vectorClient ->
                    vectorClient.client.createPayloadIndexAsync(
                        "VersatilePTT_VectorCollection",
                        "product_name",
                        Collections.PayloadSchemaType.Keyword,
                        null,
                        true,
                        null,
                        null
                    ).get()

                    log.info("INTERNAL BACKEND OPERATION: Qdrant Collection from a Shard has been configured successfully for filtering purposes.")
                }
            }
        } catch (e: Exception) {
            log.error(e.message)
            notifierService.sendAlert("Qdrant Collection Filtering Initializer Error", e.message + "")
        }
    }
}