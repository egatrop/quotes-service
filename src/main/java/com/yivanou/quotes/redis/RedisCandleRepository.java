package com.yivanou.quotes.redis;

import com.yivanou.quotes.repository.ICandleRepository;
import com.yivanou.quotes.repository.entity.CandleStick;
import io.lettuce.core.api.sync.RedisCommands;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.LinkedList;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
@Slf4j
@RequiredArgsConstructor
public class RedisCandleRepository implements ICandleRepository {

    @Autowired
    private RedisCommands<String, CandleStick> commands;

    @Override
    public void persist(String isin, CandleStick candleStick) {
        commands.rpush(isin, candleStick);
        log.info("New candle inserted isin={}, candle={}", isin, candleStick);
    }

    @Override
    public void delete(String isin) {
        commands.del(isin);
        log.info("Candles removed for isin={}", isin);
    }

    @Override
    public Map<String, CandleStick> getLastCandleForAll() {
        return commands.keys("*").stream().collect(Collectors.toMap(Function.identity(), k -> commands.lrange(k, -1, -1).get(0)));
    }

    @Override
    public LinkedList<CandleStick> getByIsin(String isin) {
        return new LinkedList<>(commands.lrange(isin, 0, -1));
    }
}
