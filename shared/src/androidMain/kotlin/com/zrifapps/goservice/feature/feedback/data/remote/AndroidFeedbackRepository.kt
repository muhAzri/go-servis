package com.zrifapps.goservice.feature.feedback.data.remote

import android.content.Context
import android.os.Build
import com.zrifapps.goservice.core.error.DomainError
import com.zrifapps.goservice.core.result.DomainResult
import com.zrifapps.goservice.feature.feedback.data.dto.FeedbackRequestDto
import com.zrifapps.goservice.feature.feedback.domain.model.FeedbackReport
import com.zrifapps.goservice.feature.feedback.domain.repository.FeedbackRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import java.net.HttpURLConnection
import java.net.SocketTimeoutException
import java.net.URL
import java.net.UnknownHostException

/**
 * Posts feedback to the GoService backend. Attaches device/app context for
 * triage and maps transport failures to [DomainError.Network]. Uses the JDK
 * HTTP client so the app pulls in no extra networking dependency for its single
 * outbound call.
 */
class AndroidFeedbackRepository(
    private val context: Context,
) : FeedbackRepository {

    private val json = Json {
        encodeDefaults = true
        explicitNulls = false
    }

    override suspend fun submit(report: FeedbackReport): DomainResult<Unit> =
        withContext(Dispatchers.IO) {
            val dto = FeedbackRequestDto(
                type = report.type.wireValue,
                message = report.message,
                email = report.email,
                appVersion = appVersion(),
                platform = "android",
                deviceModel = "${Build.MANUFACTURER} ${Build.MODEL}".trim(),
                osVersion = "Android ${Build.VERSION.RELEASE} (SDK ${Build.VERSION.SDK_INT})",
            )
            val payload = json.encodeToString(FeedbackRequestDto.serializer(), dto)
                .encodeToByteArray()

            var connection: HttpURLConnection? = null
            try {
                connection = (URL(ENDPOINT).openConnection() as HttpURLConnection).apply {
                    requestMethod = "POST"
                    connectTimeout = TIMEOUT_MS
                    readTimeout = TIMEOUT_MS
                    doOutput = true
                    setRequestProperty("Content-Type", "application/json; charset=utf-8")
                    setRequestProperty("Accept", "application/json")
                }
                connection.outputStream.use { it.write(payload) }

                when (val code = connection.responseCode) {
                    in 200..299 -> DomainResult.Success(Unit)
                    in 500..599 -> DomainResult.Failure(
                        DomainError.Network.Server(code, "Server sedang bermasalah ($code)"),
                    )
                    else -> DomainResult.Failure(
                        DomainError.Network.Server(code, "Permintaan ditolak ($code)"),
                    )
                }
            } catch (_: UnknownHostException) {
                DomainResult.Failure(DomainError.Network.NoConnection)
            } catch (_: SocketTimeoutException) {
                DomainResult.Failure(DomainError.Network.Timeout)
            } catch (_: Throwable) {
                DomainResult.Failure(DomainError.Network.Unreachable())
            } finally {
                connection?.disconnect()
            }
        }

    private fun appVersion(): String? = runCatching {
        context.packageManager.getPackageInfo(context.packageName, 0).versionName
    }.getOrNull()

    private companion object {
        const val ENDPOINT = "https://www.goservis.my.id/api/feedback"
        const val TIMEOUT_MS = 15_000
    }
}
