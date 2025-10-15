package com.example;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "com.example")
public class english_main {
	public static void main(String[] args) {
        System.out.print("Hello World");
		SpringApplication.run(english_main.class, args);
	}
}
