package com.yivanou.quotes.repository;

import com.yivanou.quotes.repository.entity.CandleStick;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Component
@Slf4j
public class CandleRepository implements ICandleRepository {

    private final Map<String, LinkedList<CandleStick>> sticks = new ConcurrentHashMap<>();

    @Override
    public void addValidIsin(String isin) {
        sticks.put(isin, new LinkedList<>());
    }

    @Override
    public void persist(String isin, CandleStick candleStick) {
        sticks.get(isin).add(candleStick);
        log.info("New candle inserted isin={}, total size={}, candle={}", isin, sticks.get(isin).size(), candleStick);
    }

    @Override
    public void delete(String isin) {
        sticks.remove(isin);
        log.info("Candles removed for isin={}", isin);
    }

    @Override
    public Set<String> getValidIsins() {
        return sticks.keySet();
    }

    @Override
    public Map<String, LinkedList<CandleStick>> getAll() {
        return Collections.unmodifiableMap(sticks);
    }

    @Override
    public LinkedList<CandleStick> getByIsin(String isin) {
        return new LinkedList<>(sticks.get(isin));
    }
}
