package ru.practicum.ewm.main;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.practicum.ewm.stats.client.StatsClient;
@Configuration
public class StatsClientConfiguration {

 @Bean public StatsClient statsClient(@Value("${stats-server.url:http://localhost:9090}") String url) {
return new StatsClient(url);
}

}
