package com.donglai.web.util;

import com.donglai.web.db.backoffice.entity.Menu;

import java.util.Comparator;

public class ComparatorUtil {

    public static Comparator<Menu> getParentMenuComparator() {
        return (menu1, menu2) -> {
            int sortNum1 = Integer.parseInt(menu1.getId());
            int sortNum2 = Integer.parseInt(menu2.getId());
            return sortNum1 - sortNum2;
        };
    }

}
