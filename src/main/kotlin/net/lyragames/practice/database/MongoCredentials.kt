package net.lyragames.practice.database

data class MongoCredentials (

        var host: String = "localhost",
        var port: Int = 27017,
        var username: String? = null,
        var password: String? = null
)

{
    fun shouldAuthenticate(): Boolean {
        return username != null && (password != null && password!!.isNotEmpty() && password!!.isNotBlank())
    }

    class Builder {
        val credentials: MongoCredentials = MongoCredentials()

        fun host(host: String): Builder {
            credentials.host = host
            return this
        }

        fun port(port: Int): Builder {
            credentials.port = port
            return this
        }

        fun username(username: String): Builder {
            credentials.username = username
            return this
        }

        fun password(password: String): Builder {
            credentials.password = password
            return this
        }
        fun build(): MongoCredentials {
            return credentials
        }
    }
}