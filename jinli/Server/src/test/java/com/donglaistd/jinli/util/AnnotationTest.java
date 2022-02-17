package com.donglaistd.jinli.util;

import com.donglaistd.jinli.BaseTest;
import com.donglaistd.jinli.annotation.IgnoreShutDown;
import com.donglaistd.jinli.config.SpringContext;
import org.junit.Test;
import org.springframework.aop.support.AopUtils;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

public class AnnotationTest extends BaseTest {

    @Test
    public void test(){
        Collection<Object> objects = SpringContext.getBeansWithAnnotation(IgnoreShutDown.class).values();
        for (Object object : objects) {
            String simpleName = AopUtils.getTargetClass(object).getSimpleName();
            System.err.println(simpleName);
        }
        Set<String> classes = objects.stream().map(obj -> AopUtils.getTargetClass(obj).getSimpleName()).collect(Collectors.toSet());
    }
}
