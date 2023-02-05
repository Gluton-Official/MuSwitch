package dev.gluton.muswitch.util

import com.linkedin.urls.Url

val Url.queryParameters: Map<String, String>
	get() = mutableMapOf<String, String>().also { parameters ->
		query.dropWhile { it == '?' }.split("&").forEach { query ->
			val (key, value) = query.split("=")
			parameters[key] = value
		}
	}

val Url.paths: List<String>
	get() = path.split("/").filter { it.isNotBlank() }

val Url.domain: String
	get() = host.split(".").takeLast(2).joinToString(".")

val Url.urlWithoutQuery: String
	get() = fullUrlWithoutFragment.takeWhile { it != '?' }
