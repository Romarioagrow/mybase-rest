package mybase.config.mvc;

import me.postaddict.instagram.scraper.cookie.CookieHashSet;
import me.postaddict.instagram.scraper.cookie.DefaultCookieJar;
import me.postaddict.instagram.scraper.interceptor.ErrorInterceptor;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import org.brunocvcunha.instagram4j.Instagram4j;
import org.springframework.boot.web.server.ConfigurableWebServerFactory;
import org.springframework.boot.web.server.ErrorPage;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;

@Configuration
public class WebMvcConfig  {
    @Bean
    public WebServerFactoryCustomizer<ConfigurableWebServerFactory> webServerCustomizer() {
        return container -> container.addErrorPages(new ErrorPage(HttpStatus.NOT_FOUND, "/index.html"));
    }

    @Bean
    public OkHttpClient OkHttpClientFactory() {

        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        return new OkHttpClient.Builder()
                .addNetworkInterceptor(loggingInterceptor)
                .addInterceptor(new ErrorInterceptor())
                .cookieJar(new DefaultCookieJar(new CookieHashSet()))
                .build();
    }

    @Bean
    public Instagram4j Instagram4jBuilder() {
        return Instagram4j.builder().username("creep_waves").password("Route456123").build();
    }
}

