package com.cs.doceho.stats.bot.v2.service.utils;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CalculationService {

  public int safeInt(Integer value) {
    return value != null ? value : 0;
  }

  public double safeDouble(Double value) {
    return value != null ? value : 0.0;
  }
}
