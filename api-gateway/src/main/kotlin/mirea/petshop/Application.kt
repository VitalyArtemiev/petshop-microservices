package mirea.petshop

import org.jetbrains.exposed.sql.Database
import org.springframework.beans.factory.InitializingBean
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.env.Environment
import org.springframework.web.client.RestTemplate


@SpringBootApplication
class PetshopApplication

fun main(args: Array<String>) {
    SpringApplication.run(PetshopApplication::class.java, *args)
}


@Value("\${userBucket.path}")
lateinit var userBucketPath: String

@Autowired
lateinit var environment: Environment



@Configuration
class AppConfig: InitializingBean {
    override fun afterPropertiesSet() {

    }

    @Value("\${exposed.database.url}")
    val url = ""
    @Value("\${exposed.database.user}")
    val user = ""
    @Value("\${exposed.database.password}")
    val password = ""

    @Bean
    fun initDB(): Database {
        val driver = "org.postgresql.Driver" //environment.getProperty("datasource.url")

        return Database.connect(url, driver, user, password)
    }

    @Bean
    fun initRT(): RestTemplate {
        return RestTemplate()
    }
}