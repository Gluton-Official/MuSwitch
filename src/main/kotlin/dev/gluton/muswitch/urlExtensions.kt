package dev.gluton.muswitch

import com.linkedin.urls.detection.UrlDetector
import com.linkedin.urls.detection.UrlDetectorOptions
import io.ktor.http.Url

fun findUrls(text: String): List<Url> =
    UrlDetector(text, UrlDetectorOptions.Default).detect().map { url -> Url(url.fullUrl) }

val Url.domain: String get() = host.split(".").takeLast(2).joinToString(".")
