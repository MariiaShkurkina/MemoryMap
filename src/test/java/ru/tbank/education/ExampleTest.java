package ru.tbank.education;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class ExampleTest {

    @Test
    void contextLoads() {
    }

    @Test
    void main() {
        Assertions.assertTrue(2 == 1 + 1);
    }
}