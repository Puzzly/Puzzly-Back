package com.puzzly.api.service;

import com.puzzly.api.exception.FailException;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@Transactional(readOnly = true)
public class RedisService {

  private final RedisTemplate<String, String> redisTemplate;

  public RedisService(RedisTemplate<String, String> redisTemplate) {
    this.redisTemplate = redisTemplate;
  }

  @Transactional
  public void setValues(String key, String value, long timeout){
    redisTemplate.opsForValue().set(key, value, timeout, TimeUnit.MILLISECONDS);
  }

  public String getValues(String key){
    return redisTemplate.opsForValue().get(key);
  }

  @Transactional
  public void setHash(String key, Map<String, String> map, long timeout){
    HashOperations<String, String, String> hashOperations = redisTemplate.opsForHash();
    hashOperations.putAll(key, map);
    redisTemplate.expire(key, timeout, TimeUnit.MILLISECONDS);
  }

  public Map<String, String> getHashValues(String key){
    HashOperations<String, String, String> hashOperations = redisTemplate.opsForHash();
    Map<String, String> resultMap;
    try {
      resultMap = hashOperations.entries(key);
    }catch(Exception e) {
      e.printStackTrace();
      throw new FailException(e.getMessage(), 400);
    }
    return resultMap;
  }

  public String getHashValue(String key, String field){
    String value;
    try {
      value = getHashValues(key).get(field);
    }catch(Exception e) {
      return "";
    }
    return value;
  }

  @Transactional
  public void deleteValues(String key) {
    redisTemplate.delete(key);
  }

  // 만료시간 설정 -> 자동 삭제
  @Transactional
  public void setValuesWithTimeout(String key, String value, long timeout){
    redisTemplate.opsForValue().set(key, value, timeout, TimeUnit.MILLISECONDS);
  }

}