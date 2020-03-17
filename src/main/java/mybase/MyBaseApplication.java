package mybase;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@EnableTransactionManagement
public class MyBaseApplication {

    public static void main(String[] args) {
        SpringApplication.run(MyBaseApplication.class, args);
    }

}
