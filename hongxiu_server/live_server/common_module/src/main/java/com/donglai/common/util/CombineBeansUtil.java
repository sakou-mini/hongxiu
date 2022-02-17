package com.donglai.common.util;

import lombok.SneakyThrows;

import java.lang.reflect.Field;
import java.util.*;

public class CombineBeansUtil<T> {

    private final T t;

    @SneakyThrows
    public CombineBeansUtil(Class<T> cls) {
        this.t = cls.getDeclaredConstructor().newInstance();
    }

    @SuppressWarnings("unused")
    public T combineBeans(Object sourceBean) {
        Class sourceBeanClass = sourceBean.getClass();
        Class targetBeanClass = t.getClass();

        Field[] sourceFields = sourceBeanClass.getDeclaredFields();
        Field[] targetFields = targetBeanClass.getDeclaredFields();
        for (Field sourceField : sourceFields) {
            Optional<Field> firstTargetField = Arrays.stream(targetFields).filter(field -> Objects.equals(sourceField.getName(), field.getName())).findFirst();
            if (firstTargetField.isEmpty()) continue;
            Field targetField = firstTargetField.get();
            sourceField.setAccessible(true);
            targetField.setAccessible(true);
            try {
                if (!(sourceField.get(sourceBean) == null)) {
                    targetField.set(t, sourceField.get(sourceBean));
                }
            } catch (IllegalArgumentException | IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        return t;
    }

    public List<T> combineBeansList(List<?> list) {
        List<T> res = new ArrayList<T>();
        for (Object o : list) {
            res.add(combineBeans(o));

        }
        return res;
    }
}
