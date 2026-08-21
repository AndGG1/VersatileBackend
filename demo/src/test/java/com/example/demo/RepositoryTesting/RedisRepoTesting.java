package com.example.demo.RepositoryTesting;

import com.example.demo.backendUsage.redis.structure.ParseShard;
import com.example.demo.backendUsage.redis.structure.RedisConfig;
import com.example.demo.backendUsage.redis.structure.RedisRepository;
import com.example.demo.backendUsage.redis.structure.ShardData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.redis.DataRedisTest;
import org.springframework.context.annotation.Import;

import static org.assertj.core.api.Assertions.assertThat;

//Informative Comment: For testing the Model: AAA("Triple A") is used. Any test should be rooting for this Model.

@DataRedisTest
@Import({RedisRepository.class, RedisConfig.class})
public class RedisRepoTesting {

    @Autowired
    private RedisRepository redisRepository;

    @BeforeEach
    void setUp() {
        redisRepository.delete("shard-101");
    }

    @Test
    public void test_SaveAndFindById() {
        // Arrange
        ParseShard parseShard = new ParseShard(
                "shard-101",
                "https://cluster-101.example.com",
                "secret-api-key-123",
                true
        );

        // Act
        redisRepository.save(parseShard);
        ShardData result = redisRepository.findById(parseShard.getShardId());

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getClusterUrl()).isEqualTo("https://cluster-101.example.com");
        assertThat(result.getApiKey()).isEqualTo("secret-api-key-123");
        assertThat(result.isAvailable()).isTrue();
    }

    @Test
    public void test_Delete() {
        // Arrange
        ParseShard parseShard = new ParseShard(
                "shard-101",
                "https://cluster-102.example.com",
                "key-456",
                false
        );
        redisRepository.save(parseShard);

        // Act
        redisRepository.delete(parseShard.getShardId());
        ShardData result = redisRepository.findById(parseShard.getShardId());

        // Assert
        assertThat(result).isNull();
    }
}