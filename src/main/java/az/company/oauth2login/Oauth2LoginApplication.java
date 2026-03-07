package az.company.oauth2login;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class Oauth2LoginApplication {

    public static void main(String[] args) {
        SpringApplication.run(Oauth2LoginApplication.class, args);
    }

}
