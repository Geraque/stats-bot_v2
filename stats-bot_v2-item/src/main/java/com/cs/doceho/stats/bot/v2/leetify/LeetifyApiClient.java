package com.cs.doceho.stats.bot.v2.leetify;

import com.cs.doceho.stats.bot.v2.config.LeetifyProperties.Account;
import com.cs.doceho.stats.bot.v2.leetify.dto.ClutchData;
import com.cs.doceho.stats.bot.v2.leetify.dto.GameDetail;
import com.cs.doceho.stats.bot.v2.leetify.dto.GameHistoryResponse;
import com.cs.doceho.stats.bot.v2.leetify.dto.GameHistoryResponse.GameIdWrapper;
import com.cs.doceho.stats.bot.v2.leetify.dto.LoginResponse;
import com.cs.doceho.stats.bot.v2.leetify.dto.OpeningDuel;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class LeetifyApiClient {

  RestTemplate restTemplate = new RestTemplate();
  static String loginUrl = "https://api.leetify.com/api/login";
  static String historyUrl = "https://api.leetify.com/api/games/history";
  static String gameDetailUrl = "https://api.leetify.com/api/games/{id}";
  static String clutchesUrl = "https://api.leetify.com/api/games/{id}/clutches";
  static String openingDuelsUrl = "https://api.leetify.com/api/games/{id}/opening-duels";

  public List<String> getAllTokens(List<Account> accounts) {
    List<String> tokens = new ArrayList<>();
    for (Account account : accounts) {
      String token = login(account.getLogin(), account.getPassword());
      if (token != null) {
        tokens.add(token);
      } else {
        log.error("Не удалось получить токен для: {}", account.getLogin());
      }
    }
    return tokens;
  }

  public String login(String email, String password) {
    try {
      HttpHeaders headers = new HttpHeaders();
      headers.setContentType(MediaType.APPLICATION_JSON);
      Map<String, String> body = new HashMap<>();
      body.put("email", email);
      body.put("password", password);
      HttpEntity<Map<String, String>> request = new HttpEntity<>(body, headers);
      ResponseEntity<LoginResponse> response = restTemplate.postForEntity(loginUrl, request, LoginResponse.class);
      if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
        return response.getBody().getToken();
      }
    } catch (Exception e) {
      log.error("Ошибка при авторизации: {}", e.getMessage());
    }
    return null;
  }

  public List<GameIdWrapper> getGameHistory(String token) {
    try {
      HttpHeaders headers = new HttpHeaders();
      headers.set("Authorization", "Bearer " + token);
      HttpEntity<Void> request = new HttpEntity<>(headers);
      ResponseEntity<GameHistoryResponse> response = restTemplate.exchange(
          historyUrl, HttpMethod.GET, request, GameHistoryResponse.class);
      if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
        return response.getBody().getGames();
      }
    } catch (Exception e) {
      log.error("Ошибка при получении истории игр: {}", e.getMessage());
    }
    return Collections.emptyList();
  }

  public GameDetail getGameDetail(String gameId) {
    try {
      HttpEntity<Void> request = new HttpEntity<>(new HttpHeaders());
      Map<String, String> uriVariables = new HashMap<>();
      uriVariables.put("id", gameId);
      ResponseEntity<GameDetail> response = restTemplate.exchange(
          gameDetailUrl, HttpMethod.GET, request, GameDetail.class, uriVariables);
      if (response.getStatusCode() == HttpStatus.OK) {
        return response.getBody();
      }
    } catch (Exception e) {
      log.error("Ошибка при получении деталей игры (id={}): {}", gameId, e.getMessage());
    }
    return null;
  }

  public List<ClutchData> getClutches(String gameId) {
    try {
      HttpEntity<Void> request = new HttpEntity<>(new HttpHeaders());
      Map<String, String> uriVariables = new HashMap<>();
      uriVariables.put("id", gameId);
      ResponseEntity<ClutchData[]> response = restTemplate.exchange(
          clutchesUrl, HttpMethod.GET, request, ClutchData[].class, uriVariables);
      if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
        return Arrays.asList(response.getBody());
      }
    } catch (Exception e) {
      log.error("Ошибка при получении clutch-данных для игры (id={}): {}", gameId, e.getMessage());
    }
    return Collections.emptyList();
  }

  public List<OpeningDuel> getOpeningDuels(String gameId) {
    try {
      HttpEntity<Void> request = new HttpEntity<>(new HttpHeaders());
      Map<String, String> uriVariables = new HashMap<>();
      uriVariables.put("id", gameId);
      ResponseEntity<OpeningDuel[]> response = restTemplate.exchange(
          openingDuelsUrl, HttpMethod.GET, request, OpeningDuel[].class, uriVariables);
      if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
        return Arrays.asList(response.getBody());
      }
    } catch (Exception e) {
      log.error("Ошибка при получении opening-duels для игры (id={}): {}", gameId, e.getMessage());
    }
    return Collections.emptyList();
  }
}
