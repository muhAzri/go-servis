package com.zrifapps.goservice.feature.feedback.data.remote

import com.zrifapps.goservice.core.error.DomainError
import com.zrifapps.goservice.core.result.DomainResult
import com.zrifapps.goservice.feature.feedback.data.dto.FeedbackRequestDto
import com.zrifapps.goservice.feature.feedback.domain.model.FeedbackReport
import com.zrifapps.goservice.feature.feedback.domain.repository.FeedbackRepository
import kotlinx.cinterop.BetaInteropApi
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.serialization.json.Json
import platform.Foundation.NSBundle
import platform.Foundation.NSError
import platform.Foundation.NSHTTPURLResponse
import platform.Foundation.NSMutableURLRequest
import platform.Foundation.NSString
import platform.Foundation.NSURL
import platform.Foundation.NSURLErrorNetworkConnectionLost
import platform.Foundation.NSURLErrorNotConnectedToInternet
import platform.Foundation.NSURLErrorTimedOut
import platform.Foundation.NSURLSession
import platform.Foundation.NSUTF8StringEncoding
import platform.Foundation.dataUsingEncoding
import platform.UIKit.UIDevice
import kotlin.coroutines.resume

/**
 * iOS implementation of [FeedbackRepository] using NSURLSession. Mirrors the
 * Android repository: attaches device/app context and maps transport failures to
 * [DomainError.Network]. Compiles on macOS/Xcode only.
 */
class IosFeedbackRepository : FeedbackRepository {

    private val json = Json {
        encodeDefaults = true
        explicitNulls = false
    }

    @OptIn(ExperimentalForeignApi::class, BetaInteropApi::class)
    override suspend fun submit(report: FeedbackReport): DomainResult<Unit> {
        val device = UIDevice.currentDevice
        val dto = FeedbackRequestDto(
            type = report.type.wireValue,
            message = report.message,
            email = report.email,
            appVersion = appVersion(),
            platform = "ios",
            deviceModel = device.model,
            osVersion = "${device.systemName} ${device.systemVersion}",
        )

        val payload = json.encodeToString(FeedbackRequestDto.serializer(), dto)
        val body = (payload as NSString).dataUsingEncoding(NSUTF8StringEncoding)
            ?: return DomainResult.Failure(DomainError.Network.Unreachable())

        val request = NSMutableURLRequest(uRL = NSURL(string = ENDPOINT)!!).apply {
            setHTTPMethod("POST")
            setValue("application/json; charset=utf-8", forHTTPHeaderField = "Content-Type")
            setValue("application/json", forHTTPHeaderField = "Accept")
            setHTTPBody(body)
        }

        return suspendCancellableCoroutine { continuation ->
            val task = NSURLSession.sharedSession.dataTaskWithRequest(request) { _, response, error ->
                val result: DomainResult<Unit> = when {
                    error != null -> DomainResult.Failure(mapError(error))
                    else -> {
                        val status = (response as? NSHTTPURLResponse)?.statusCode?.toInt() ?: -1
                        when (status) {
                            in 200..299 -> DomainResult.Success(Unit)
                            in 500..599 -> DomainResult.Failure(
                                DomainError.Network.Server(status, "Server sedang bermasalah ($status)"),
                            )
                            else -> DomainResult.Failure(
                                DomainError.Network.Server(status, "Permintaan ditolak ($status)"),
                            )
                        }
                    }
                }
                continuation.resume(result)
            }
            continuation.invokeOnCancellation { task.cancel() }
            task.resume()
        }
    }

    private fun appVersion(): String? =
        NSBundle.mainBundle.objectForInfoDictionaryKey("CFBundleShortVersionString") as? String

    private fun mapError(error: NSError): DomainError.Network = when (error.code) {
        NSURLErrorNotConnectedToInternet, NSURLErrorNetworkConnectionLost ->
            DomainError.Network.NoConnection
        NSURLErrorTimedOut -> DomainError.Network.Timeout
        else -> DomainError.Network.Unreachable()
    }

    private companion object {
        const val ENDPOINT = "https://www.goservis.my.id/api/feedback"
    }
}
