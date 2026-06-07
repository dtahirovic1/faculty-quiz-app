package ba.etf.rma26.projekat.data.repositories

import ba.etf.rma26.projekat.data.models.*
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {

    @GET("predmet")
    suspend fun getPredmeti(): Response<List<Predmet>>

    @GET("grupa")
    suspend fun getGrupe(): Response<List<Grupa>>

    @GET("predmet/{id}/grupa")
    suspend fun getGrupeZaPredmet(
        @Path("id") idPredmeta: Int
    ): Response<List<Grupa>>

    @GET("student/{hash}/grupa")
    suspend fun getUpisaneGrupe(
        @Path("hash") hash: String
    ): Response<List<Grupa>>

    @POST("student/{hash}/grupa/{id}")
    suspend fun upisiUGrupu(
        @Path("hash") hash: String,
        @Path("id") idGrupa: Int,
        @Header("X-API-Key") apiKey: String? = ApiConfig.getApiKey()
    ): Response<Boolean>
    @GET("kviz")
    suspend fun getKvizovi(): Response<List<Kviz>>

    @GET("kviz/{id}")
    suspend fun getKvizById(
        @Path("id") id: Int
    ): Response<Kviz>

    @GET("student/{hash}/kviz")
    suspend fun getUpisaniKvizovi(
        @Path("hash") hash: String
    ): Response<List<Kviz>>

    @GET("kviz/{id}/pitanja")
    suspend fun getPitanja(
        @Path("id") idKviza: Int
    ): Response<List<Pitanje>>

    @POST("student/{hash}/kviz/{id}")
    suspend fun zapocniKviz(
        @Path("hash") hash: String,
        @Path("id") idKviza: Int,
        @Header("X-API-Key") apiKey: String? = ApiConfig.getApiKey()
    ): Response<KvizTaken>

    @GET("student/{hash}/kviztaken")
    suspend fun getPocetiKvizovi(
        @Path("hash") hash: String
    ): Response<List<KvizTaken>?>

    @GET("student/{hash}/kviz/{id}/odgovori")
    suspend fun getOdgovoriKviz(
        @Path("hash") hash: String,
        @Path("id") idKviza: Int
    ): Response<List<Odgovor>>

    @POST("student/{hash}/kviztaken/{id}/odgovor")
    suspend fun postaviOdgovor(
        @Path("hash") hash: String,
        @Path("id") idKvizTaken: Int,
        @Body request: OdgovorRequest,
        @Header("X-API-Key") apiKey: String? = ApiConfig.getApiKey()
    ): Response<Int>

}