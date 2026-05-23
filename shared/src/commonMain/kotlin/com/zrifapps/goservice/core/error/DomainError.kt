package com.zrifapps.goservice.core.error

sealed class DomainError {
    abstract val message: String

    sealed class Network : DomainError() {
        data object NoConnection : Network() {
            override val message: String = "Tidak ada koneksi internet"
        }
        data object Timeout : Network() {
            override val message: String = "Permintaan timeout"
        }
        data class Server(
            val statusCode: Int,
            override val message: String,
        ) : Network()
        data class Unreachable(
            override val message: String = "Server tidak dapat dijangkau",
        ) : Network()
    }

    sealed class Storage : DomainError() {
        data class ReadFailed(override val message: String) : Storage()
        data class WriteFailed(override val message: String) : Storage()
        data class MigrationFailed(override val message: String) : Storage()
    }

    sealed class Validation : DomainError() {
        data class FieldRequired(val field: String) : Validation() {
            override val message: String = "Field '$field' wajib diisi"
        }
        data class InvalidFormat(
            val field: String,
            val reason: String,
        ) : Validation() {
            override val message: String = "Format '$field' tidak valid: $reason"
        }
        data class OutOfRange(
            val field: String,
            val reason: String,
        ) : Validation() {
            override val message: String = "Nilai '$field' di luar batas: $reason"
        }
    }

    sealed class Permission : DomainError() {
        data object NotificationDenied : Permission() {
            override val message: String = "Izin notifikasi belum diberikan"
        }
        data object StorageDenied : Permission() {
            override val message: String = "Izin penyimpanan belum diberikan"
        }
    }

    data class NotFound(
        val resource: String,
        val id: String,
    ) : DomainError() {
        override val message: String = "$resource dengan id=$id tidak ditemukan"
    }

    data class Conflict(
        val resource: String,
        override val message: String,
    ) : DomainError()

    data class Unknown(
        val cause: Throwable? = null,
        override val message: String = "Kesalahan tidak diketahui",
    ) : DomainError()
}
