package ba.etf.rma26.projekat.data.repositories

import com.google.gson.GsonBuilder
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.JsonPrimitive
import com.google.gson.JsonSerializationContext
import com.google.gson.JsonSerializer
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.lang.reflect.Type
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.OffsetDateTime

class LocalDateAdapter : JsonDeserializer<LocalDate>, JsonSerializer<LocalDate> {

    override fun deserialize(
        json: JsonElement,
        typeOfT: Type,
        context: JsonDeserializationContext
    ): LocalDate {
        val value = json.asString
        return LocalDate.parse(value.take(10))
    }

    override fun serialize(
        src: LocalDate,
        typeOfSrc: Type,
        context: JsonSerializationContext
    ): JsonElement {
        return JsonPrimitive(src.toString())
    }
}

object RetrofitClient {
    private var lastBaseUrl: String? = null
    private var serviceInstance: ApiService? = null

    val service: ApiService
        get() {
            val currentBaseUrl = ApiConfig.getBaseUrl()

            if (serviceInstance == null || lastBaseUrl != currentBaseUrl) {
                val gson = GsonBuilder()
                    .registerTypeAdapter(LocalDate::class.java, LocalDateAdapter())
                    .create()

                serviceInstance = Retrofit.Builder()
                    .baseUrl(currentBaseUrl)
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .build()
                    .create(ApiService::class.java)

                lastBaseUrl = currentBaseUrl
            }

            return serviceInstance!!
        }
}