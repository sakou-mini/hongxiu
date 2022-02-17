package com.donglai.web;

import com.donglai.web.app.WebApp;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = WebApp.class)
@ActiveProfiles("test")
public abstract class WebBaseTest {
}
