package mirea.petshop.model

import org.jetbrains.exposed.sql.Database
import org.springframework.beans.factory.InitializingBean
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.env.Environment


@Value("\${userBucket.path}")
lateinit var userBucketPath: String

@Autowired
lateinit var environment: Environment



@Configuration
class Appconfig: InitializingBean {
    override fun afterPropertiesSet() {

    }

    @Value("\${exposed.database.url}")
    val url = ""
    @Value("\${exposed.database.user}")
    val user = ""
    @Value("\${exposed.database.password}")
    val password = ""

    @Bean
    fun InitDB(): Database {
        val driver = "org.postgresql.Driver" //environment.getProperty("datasource.url")

        return Database.connect(url, driver, user, password)
    }
}