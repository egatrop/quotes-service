package com.yivanou.quotes.rest;

import com.yivanou.quotes.service.InstrumentsService;
import com.yivanou.quotes.service.dto.CandleStickDto;
import com.yivanou.quotes.service.exception.InstrumentNotFoundException;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/instruments")
public class InstrumentsController {

    @Autowired
    private InstrumentsService service;

    @GetMapping
    public ResponseEntity<Map<String, BigDecimal>> getAll() {
        return ResponseEntity.ok(service.getLatestPrices());
    }

    @GetMapping("/{isin}")
    public ResponseEntity<List<CandleStickDto>> getByIsin(
            @PathVariable("isin") String isin
    ) {
        return ResponseEntity.ok(service.getHistory(isin));
    }

    @ExceptionHandler
    public ResponseEntity<Object> handle(Throwable ex, HttpServletRequest request) {
        log.error(String.format("Request failed url=%s msg=%s", request.getRequestURI(), ex.getMessage()), ex);

        if (ex instanceof InstrumentNotFoundException) {
            return ResponseEntity.badRequest().body(new ErrorResponse(ex.getMessage()));
        }

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }
}

@RequiredArgsConstructor
@Getter
class ErrorResponse {
    private final String message;
}
