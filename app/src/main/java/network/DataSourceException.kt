package network

class DataSourceException(
    var code: Int,
    override var message: String,
): Exception(message)
