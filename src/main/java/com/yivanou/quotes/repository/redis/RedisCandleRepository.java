package com.yivanou.quotes.repository.redis;

import com.yivanou.quotes.repository.ICandleRepository;
import com.yivanou.quotes.repository.entity.CandleStick;
import io.lettuce.core.api.sync.RedisCommands;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
@Slf4j
@RequiredArgsConstructor
public class RedisCandleRepository implements ICandleRepository {

    @Autowired
    private final RedisCommands<String, CandleStick> commands;

    @Override
    public void persist(String isin, CandleStick candleStick) {
        long size = commands.rpush(isin, candleStick);
        log.info("New candle inserted isin={}, candle={}, size={}", isin, candleStick, size);
    }

    @Override
    public void delete(String isin) {
        long deleted = commands.del(isin);
        log.info("Candles removed for isin={} number of keys {}", isin, deleted);
    }

    @Override
    public Map<String, CandleStick> getLastCandleForAll() {
        return commands.keys("*").stream().collect(Collectors.toMap(Function.identity(), k -> commands.lrange(k, -1, -1).get(0)));
    }

    @Override
    public LinkedList<CandleStick> getLastByIsin(String isin, Integer limit) {
        return new LinkedList<>(commands.lrange(isin, -limit, -1));
    }

    @Override
    public List<String> getKeys() {
        return commands.keys("*");
    }
}
