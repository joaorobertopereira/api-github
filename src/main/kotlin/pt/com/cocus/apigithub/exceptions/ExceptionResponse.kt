package pt.com.cocus.apigithub.exceptions

data class ExceptionResponse(
    var status: Int? = null,
    var message: String? = null
) {
    fun toJson(): String {
        return "{\"status\": \"$status\",\"message\": \"$message\"}"
    }
}