package com.donglaistd.jinli.processors.handler.guess;

import com.donglaistd.jinli.Constant;
import com.donglaistd.jinli.database.dao.GuessDaoService;
import com.donglaistd.jinli.database.entity.backoffice.Guess;
import com.donglaistd.jinli.http.entity.GuessItem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;

@Component
public class InsertGuessTestData{
    @Autowired
    GuessDaoService guessDaoService;

    public String insertGuessData(){
        var itemListTest = new ArrayList<GuessItem>();
        for (int i = 0; i < 4; i++) {
            var itemTest = new GuessItem();
            itemTest.optionContent = i + "";
            itemListTest.add(itemTest);
        }
        Guess guess = Guess.newInstance("testTitle", "testSubtitle", itemListTest, Constant.GuessType.POLITICS, 1604454840118L,
                1704454840118L, 1604454840119L, 1704454840117L);
        guess.setGuessImg("/img");
        guess.setWindowImg("/IMG");
        guess.setSort(1);
        return guessDaoService.save(guess).getId();
    }

}
