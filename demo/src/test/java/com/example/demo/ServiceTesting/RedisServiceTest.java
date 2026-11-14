package com.example.demo.ServiceTesting;

import com.example.demo.backendUsage.health.NotifierService;
import com.example.demo.backendUsage.redis.structure.ParseShard;
import com.example.demo.backendUsage.redis.structure.RedisRepository;
import com.example.demo.backendUsage.redis.structure.RedisService;
import com.example.demo.backendUsage.redis.structure.ShardData;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

//Informative Comment: For testing the Model: AAA("Triple A") is used. Any test should be rooting for this Model.

@ExtendWith(MockitoExtension.class)
public class RedisServiceTest {

    @Mock
    private RedisRepository redisRepository;

    // Added this mock to satisfy RedisService's Kotlin constructor requirement
    @Mock
    private NotifierService notifierService;

    @InjectMocks
    private RedisService redisService;

    @Test
    public void testUpsert_Success() {
        // Arrange
        ParseShard parseShard = new ParseShard(
                "test_shardId",
                "test_clusterUrl",
                "test_apiKey",
                true
        );

        // Act
        redisService.upsert(parseShard);

        // Assert
        verify(redisRepository, times(1)).save(parseShard);
    }

    @Test
    public void testGetShard_Success() {
        // Arrange
        String shardId = "1";
        ShardData expectedShard = new ShardData("test_clusterUrl", "test_apiKey", true);
        when(redisRepository.findById(shardId)).thenReturn(expectedShard);

        // Act
        ShardData actualShard = redisService.getShard(shardId);

        // Assert
        assertNotNull(actualShard);
        assertEquals(expectedShard, actualShard);
        verify(redisRepository, times(1)).findById(shardId);
    }

    @Test
    public void testGetShard_ThrowsNullPointerExceptionWhenNotFound() {
        // Arrange
        String shardId = "99";
        when(redisRepository.findById(shardId)).thenReturn(null);

        // Act & Assert
        assertThrows(NullPointerException.class, () -> redisService.getShard(shardId));
        verify(redisRepository, times(1)).findById(shardId);
    }

    @Test
    public void testGetCurrentActiveShard_Success() {
        // Arrange
        ShardData shard0 = new ShardData("url0", "key0", false);
        ShardData shard1 = new ShardData("url1", "key1", true);

        when(redisRepository.findById("0")).thenReturn(shard0);
        when(redisRepository.findById("1")).thenReturn(shard1);
        when(redisRepository.findById("2")).thenReturn(shard0);

        // Act
        ShardData activeShard = redisService.getCurrentActiveShard();

        // Assert
        assertNotNull(activeShard);
        assertEquals("url1", activeShard.getClusterUrl());
        assertTrue(activeShard.isAvailable());
    }

    @Test
    public void testGetCurrentActiveShard_ThrowsNullPointerExceptionWhenNoneAvailable() {
        // Arrange
        ShardData inactiveShard = new ShardData("url", "key", false);

        when(redisRepository.findById("0")).thenReturn(inactiveShard);
        when(redisRepository.findById("1")).thenReturn(inactiveShard);
        when(redisRepository.findById("2")).thenReturn(inactiveShard);

        // Act & Assert
        assertThrows(NullPointerException.class, () -> redisService.getCurrentActiveShard());
    }

    @Test
    public void testRemoveShard_Success() {
        // Arrange
        String shardId = "1";

        // Act
        redisService.removeShard(shardId);

        // Assert
        verify(redisRepository, times(1)).delete(shardId);
    }
}