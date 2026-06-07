package ba.etf.rma26.projekat.data.repositories

object ApiConfig {
    private var baseUrl: String = "http://10.0.2.2:3000/"
    private var apiKey: String? = null

    fun postaviBaseURL(baseUrl: String) {
        this.baseUrl = if (baseUrl.endsWith("/")) baseUrl else "$baseUrl/"
    }

    fun postaviApiKey(apiKey: String?) {
        this.apiKey = apiKey
    }

    fun getBaseUrl(): String {
        return baseUrl
    }

    fun getApiKey(): String? {
        return apiKey
    }
}