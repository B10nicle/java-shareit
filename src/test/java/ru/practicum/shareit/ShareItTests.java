package ru.practicum.shareit;

import org.springframework.boot.test.context.SpringBootTest;
import org.junit.jupiter.api.Test;

@SpringBootTest
public class ShareItTests {

    @Test
    public void main() {
        ShareItApp.main(new String[]{});
    }

    @Test
    void contextLoads() {
    }
}
