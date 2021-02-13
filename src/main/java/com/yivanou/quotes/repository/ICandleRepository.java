package com.yivanou.quotes.repository;

import com.yivanou.quotes.repository.entity.CandleStick;

import java.util.LinkedList;
import java.util.Map;
import java.util.Set;

public interface ICandleRepository {

    void addValidIsin(String isin);

    void persist(String isin, CandleStick candleStick);

    void delete(String isin);

    Set<String> getValidIsins();

    Map<String, LinkedList<CandleStick>> getAll();

    LinkedList<CandleStick> getByIsin(String isin);
}
