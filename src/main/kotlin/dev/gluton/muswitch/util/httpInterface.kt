package dev.gluton.muswitch.util

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.URI
import java.net.URLEncoder
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse

private val httpClient = HttpClient.newHttpClient()

suspend fun httpGet(url: String): String? {
	val request = HttpRequest.newBuilder(URI(url)).build()
	val response = withContext(Dispatchers.IO) {
		httpClient.send(request, HttpResponse.BodyHandlers.ofString())
	}
	return response.body()
}

suspend fun httpGet(url: String, postFix: String) = httpGet(url + withContext(Dispatchers.IO) {
	URLEncoder.encode(postFix, "UTF-8")
})
