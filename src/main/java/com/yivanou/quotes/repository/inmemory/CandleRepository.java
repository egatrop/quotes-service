package com.yivanou.quotes.repository.inmemory;

import com.yivanou.quotes.repository.ICandleRepository;
import com.yivanou.quotes.repository.entity.CandleStick;
import lombok.extern.slf4j.Slf4j;

import java.util.LinkedList;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

//@Component
@Slf4j
public class CandleRepository implements ICandleRepository {

    private final Map<String, LinkedList<CandleStick>> sticks = new ConcurrentHashMap<>();

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
    public Map<String, CandleStick> getLastCandleForAll() {
        return sticks.entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, e -> e.getValue().getFirst()));
    }

    @Override
    public LinkedList<CandleStick> getByIsin(String isin) {
        return new LinkedList<>(sticks.get(isin));
    }
}
