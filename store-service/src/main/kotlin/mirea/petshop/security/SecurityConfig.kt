package mirea.petshop.security


import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import org.springframework.security.config.http.SessionCreationPolicy

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
class SecurityConfig : WebSecurityConfigurerAdapter() {

    @Throws(Exception::class)
    override fun configure(security: HttpSecurity) {
        security.httpBasic().disable()
        security.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
        http.cors().and().csrf().disable()
    }
}

/*@Configuration
@EnableWebSecurity
class BasicConfiguration : WebSecurityConfigurerAdapter() {

    @Throws(Exception::class)
    override fun configure(auth: AuthenticationManagerBuilder) {
        auth
                .inMemoryAuthentication()
                .withUser("user")
                .password("{noop}password")
                .roles("USER")
                .and()
                .withUser("admin")
                .password("{noop}admin")
                .roles("USER", "ADMIN")
    }

    /*@Throws(Exception::class)
    override fun configure(http: HttpSecurity) {
        http
                .authorizeRequests()
                .anyRequest()
                .authenticated()
                .and()
                .httpBasic()
    }*/
}

/*@EnableWebSecurity
class SecurityConfig : WebSecurityConfigurerAdapter() {

    @Throws(Exception::class)
    protected override fun configure(http: HttpSecurity) {
        http
                .authorizeRequests()
                .antMatchers("/css/**", "/index").permitAll()
                .antMatchers("/user/**").hasRole("USER")
                .and()
                .formLogin()
                .loginPage("/login").failureUrl("/login-error")
    }

    @Autowired
    @Throws(Exception::class)
    fun configureGlobal(auth: AuthenticationManagerBuilder) {
        auth
                .inMemoryAuthentication()
                .withUser("user").password("password").roles("USER")
    }
}*/