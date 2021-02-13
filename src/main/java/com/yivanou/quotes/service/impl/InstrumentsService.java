package com.yivanou.quotes.service.impl;

import com.yivanou.quotes.config.ServiceProperties;
import com.yivanou.quotes.repository.ICandleRepository;
import com.yivanou.quotes.repository.entity.CandleStick;
import com.yivanou.quotes.service.IInstrumentsService;
import com.yivanou.quotes.service.PriceAdviser;
import com.yivanou.quotes.service.converter.CandleStickConverter;
import com.yivanou.quotes.service.dto.CandleStickDto;
import com.yivanou.quotes.service.exception.InstrumentNotFoundException;
import com.yivanou.quotes.ws.PricePublisher;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.math.BigDecimal;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
@RequiredArgsConstructor
public class InstrumentsService implements IInstrumentsService {

    @Autowired
    private final ICandleRepository repository;

    @Autowired
    private final CandleStickConverter converter;

    @Autowired
    private final ServiceProperties properties;

    @Autowired
    private final PriceAdviser hotPriceAdviser;

    @Autowired
    private PricePublisher pricePublisher;

    private final Set<String> validIsins = ConcurrentHashMap.newKeySet();

    @Override
    public boolean isValid(String isin) {
        return validIsins.contains(isin);
    }

    @Override
    public void addInstrument(String isin) {
        validIsins.add(isin);
    }

    @Override
    public void deleteInstrument(String isin) {
        validIsins.remove(isin);
        repository.delete(isin);
    }

    @Override
    public Map<String, BigDecimal> getLatestPrices() {
        final Map<String, CandleStick> existing = repository.getLastCandleForAll();

        validIsins.forEach(isin -> existing.putIfAbsent(isin, null));

        return existing.entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        e -> e.getValue() == null ? BigDecimal.ZERO : e.getValue().getClosePrice())
                );
    }

    @Override
    public List<CandleStickDto> getHistory(String isin) {
        return getHistory(isin, properties.getHistoryPeriod());
    }

    @Override
    public void recalculateHotPrices() {
        validIsins.stream()
                .collect(Collectors.toMap(Function.identity(), isin -> getHistory(isin, properties.getHotPriceInterval())))
                .entrySet().stream()
                .filter(e -> e.getValue().size() == properties.getHotPriceInterval())
                .map(e -> hotPriceAdviser.checkPrise(
                        e.getKey(),
                        e.getValue().getFirst().getClosePrice(),
                        e.getValue().getLast().getClosePrice())
                )
                .filter(Objects::nonNull)
                .forEach(pricePublisher::publish);
    }

    @PostConstruct
    public void init() {
        validIsins.addAll(repository.getKeys());
    }

    private LinkedList<CandleStickDto> getHistory(String isin, int lastMinutes) {
        if (!validIsins.contains(isin))
            throw new InstrumentNotFoundException(String.format("Instrument with isin=%s not found", isin));

        final LinkedList<CandleStickDto> result = new LinkedList<>();
        final LinkedList<ZonedDateTime> historyInterval = generateLastNMinutesRange(lastMinutes);
        final LinkedList<CandleStickDto> existingCandles = getLastCandles(isin, lastMinutes);

        while (!existingCandles.isEmpty() && !historyInterval.isEmpty()) {
            final ZonedDateTime minute = historyInterval.getFirst();
            final CandleStickDto lastExisting = existingCandles.getLast();

            if (minute.isAfter(lastExisting.getCloseTimeStamp()) || minute.isEqual(lastExisting.getCloseTimeStamp())) {
                final CandleStickDto dto = existingCandles.getLast().toBuilder()
                        .openTimeStamp(minute.minusMinutes(1))
                        .closeTimeStamp(minute)
                        .build();
                result.add(dto);
                historyInterval.removeFirst();
            } else {
                existingCandles.removeLast();
            }
        }

        return result;
    }

    private LinkedList<CandleStickDto> getLastCandles(String isin, Integer limit) {
        return repository.getLastByIsin(isin, limit).stream()
                .map(converter::toDto)
                .collect(Collectors.toCollection(LinkedList::new));
    }

    private LinkedList<ZonedDateTime> generateLastNMinutesRange(int lastMinutes) {
        return Stream
                .iterate(ZonedDateTime.now().truncatedTo(ChronoUnit.MINUTES).withZoneSameInstant(ZoneId.of("UTC")), m -> m.minusMinutes(1))
                .limit(lastMinutes)
                .collect(Collectors.toCollection(LinkedList::new));
    }
}
