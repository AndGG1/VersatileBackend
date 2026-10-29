package com.example.demo.buisnessUsage.vectorDb.structure.DTO

import io.qdrant.client.grpc.Points
import org.springframework.stereotype.Service
import java.util.function.Function

@Service
class PointResponseDTOMapper : Function<Points.ScrollResponse, PointResponseDto> {

    override fun apply(t: Points.ScrollResponse): PointResponseDto {
        val record = t.resultList.firstOrNull()
        val productName = record?.payloadMap?.get("product_name")?.stringValue ?: ""
        val vectorList = record?.vectors?.vector?.dataList ?: emptyList()

        return PointResponseDto(
            productName = productName,
            vector = vectorList
        )
    }
}