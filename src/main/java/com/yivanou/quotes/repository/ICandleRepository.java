package com.yivanou.quotes.repository;

import com.yivanou.quotes.repository.entity.CandleStick;

import java.util.LinkedList;
import java.util.Map;

public interface ICandleRepository {

    void persist(String isin, CandleStick candleStick);

    void delete(String isin);

    Map<String, CandleStick> getLastCandleForAll();

    LinkedList<CandleStick> getLastByIsin(String isin, Integer limit);

    void removeAllKeys();
}
