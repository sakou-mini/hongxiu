package com.donglai.common.service;

import com.donglai.common.exeception.UnableToAquireLockException;

import java.util.function.Supplier;

public interface RedissonLockService {

    <T> T lock(String resourceName, Supplier<T> supplier) throws UnableToAquireLockException, Exception;

    <T> T lock(String resourceName, Supplier<T> supplier, int lockTime) throws UnableToAquireLockException, Exception;
}
