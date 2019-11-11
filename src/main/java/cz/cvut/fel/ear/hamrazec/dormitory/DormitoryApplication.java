package cz.cvut.fel.ear.hamrazec.dormitory;

import cz.cvut.fel.ear.hamrazec.dormitory.seeder.DatabaseSeeder;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class DormitoryApplication {

    public static void main(String[] args) {
        SpringApplication.run(DormitoryApplication.class, args);
    }
}