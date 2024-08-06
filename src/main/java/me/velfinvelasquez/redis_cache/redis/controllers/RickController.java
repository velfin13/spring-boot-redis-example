package me.velfinvelasquez.redis_cache.redis.controllers;

import java.time.Duration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
@RequestMapping("/rick")
public class RickController {

    @Autowired
    private RestTemplate restTemplate;
    
    @Autowired
    private StringRedisTemplate redisTemplate;
    
    private static final String BASE_URL = "https://rickandmortyapi.com/api/character/";
    private static final Duration CACHE_DURATION = Duration.ofMinutes(10);

    @GetMapping("/{id}")
    public ResponseEntity<String> get(@PathVariable("id") Integer id) {
        String cacheKey = getKey(id.toString());
        ValueOperations<String, String> valueOps = redisTemplate.opsForValue();
        String cachedData = valueOps.get(cacheKey);

        if (cachedData != null) {
            return ResponseEntity.ok()
                                 .contentType(MediaType.APPLICATION_JSON)
                                 .body(cachedData);
        }

        try {
            ResponseEntity<String> response = restTemplate.exchange(BASE_URL.concat(id.toString()), HttpMethod.GET, null, String.class);

            if (response.getStatusCode() == HttpStatus.OK) {
                valueOps.set(cacheKey, response.getBody(), CACHE_DURATION);
                return ResponseEntity.ok()
                                     .contentType(MediaType.APPLICATION_JSON)
                                     .body(response.getBody());
            } else {
                return ResponseEntity.status(response.getStatusCode())
                                     .contentType(MediaType.APPLICATION_JSON)
                                     .body(response.getBody());
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                 .contentType(MediaType.APPLICATION_JSON)
                                 .body("Error: " + e.getMessage());
        }
    }

    private String getKey(String id) {
        return "RICK-".concat(id);
    }
}
