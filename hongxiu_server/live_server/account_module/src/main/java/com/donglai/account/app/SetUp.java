package com.donglai.account.app;

import com.donglai.account.process.KeywordProcess;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class SetUp implements CommandLineRunner {

    @Autowired
    private KeywordProcess keywordProcess;

    @Override
    public void run(String... args) throws Exception {
        keywordProcess.initKeyword();
    }


}
