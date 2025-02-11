package com.cs.doceho.stats.bot.v2.service;


import com.cs.doceho.stats.bot.v2.config.LeetifyProperties;
import com.cs.doceho.stats.bot.v2.config.LeetifyProperties.Account;
import com.cs.doceho.stats.bot.v2.db.model.MatchItem;
import com.cs.doceho.stats.bot.v2.db.model.enums.MapType;
import com.cs.doceho.stats.bot.v2.db.model.enums.MatchType;
import com.cs.doceho.stats.bot.v2.db.model.enums.PlayerName;
import com.cs.doceho.stats.bot.v2.db.repository.MatchRepository;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class LeetifyService {

  MatchRepository matchRepository;
  RestTemplate restTemplate = new RestTemplate();
  LeetifyProperties leetifyProperties;
  ChangingExcelService changingExcelService;
  static String LOGIN_URL = "https://api.leetify.com/api/login";
  static String HISTORY_URL = "https://api.leetify.com/api/games/history";
  static String GAME_DETAIL_URL = "https://api.leetify.com/api/games/{id}";
  static String CLUTCHES_URL = "https://api.leetify.com/api/games/{id}/clutches";
  static String OPENING_DUELS_URL = "https://api.leetify.com/api/games/{id}/opening-duels";
  static Integer LIMIT = 82;

  /**
   * Основной метод для обработки матчей. 1) Получаются последние 20 матчей из БД для проверки
   * дубликатов. 2) Выполняется аутентификация для получения токена. 3) Получается история игр
   * (/history) – извлекаются только id игр. 4) Для каждого (не более 20) id выполняется получение
   * деталей, clutch-данных и opening‑duels, производится маппинг статистики в MatchItem с проверкой
   * дубликатов, после чего объект сохраняется.
   */
  public void processMatches() throws IOException {
    List<MatchItem> addedMatches = new ArrayList<>();

    // Шаг 1. Получение последних 20 матчей для проверки дубликатов
    List<MatchItem> existingMatches = matchRepository.findTop20ByOrderByDateDesc();
    Set<MatchKey> existingMatchKeys = new HashSet<>();
    for (MatchItem match : existingMatches) {
      existingMatchKeys.add(
          new MatchKey(match.getDate(), match.getPlayerName(), match.getRating()));
    }

    // Шаг 2. Авторизация и получение токена
    List<String> tokens = getAllTokens(leetifyProperties.getAccounts());
    if (tokens == null) {
      System.out.println("Не удалось получить токен. Завершение обработки.");
      return;
    }
    // Шаг 3. Получение истории игр, извлекаются только id игр
    List<String> gameIds = tokens.stream()
        .flatMap(token -> getGameHistory(token).stream())
        .sorted((o1, o2) -> {
          OffsetDateTime d1 = OffsetDateTime.parse(o1.getCreatedAt());
          OffsetDateTime d2 = OffsetDateTime.parse(o2.getCreatedAt());
          return d2.compareTo(d1);
        })
        .map(GameIdWrapper::getId)
        .collect(Collectors.toList());

    if (gameIds.isEmpty()) {
      System.out.println("История игр пуста или не получена.");
      return;
    }
    // Ограничение до 20 игр
    gameIds = gameIds.stream().limit(LIMIT).collect(Collectors.toList());

    // Шаг 4. Обработка каждой игры из истории
    for (String gameId : gameIds) {
      GameDetail gameDetail = getGameDetail(gameId);
      if (gameDetail == null) {
        continue;
      }

      // Получение clutch-данных и opening-duels
      List<ClutchData> clutches = getClutches(gameId);
      List<OpeningDuel> openingDuels = getOpeningDuels(gameId);

      // Подсчёт openKill для каждого игрока
      Map<String, Integer> openKillCounts = new HashMap<>();
      if (openingDuels != null) {
        for (OpeningDuel duel : openingDuels) {
          String attacker = duel.getAttackerName();
          openKillCounts.put(attacker, openKillCounts.getOrDefault(attacker, 0) + 1);
        }
      }

      // Обработка статистики игроков из gameDetail
      if (gameDetail.getPlayerStats() != null) {
        for (PlayerStat stat : gameDetail.getPlayerStats()) {
          // Маппинг steam64Id в PlayerName согласно таблице
          PlayerName playerName = PlayerName.fromId(stat.getSteam64Id());
          if (playerName == null) {
            continue;
          }

          LocalDateTime createdAt = parseDate(gameDetail.getCreatedAt());
          Double rating = stat.getHltvRating();

          // Проверка на дубликат (сравнение по date, playerName и rating)
          MatchKey key = new MatchKey(createdAt, playerName, rating);
          if (existingMatchKeys.contains(key)) {
            continue;
          }

          // Создание объекта MatchItem и заполнение данных
          MatchItem matchItem = MatchItem.builder()
              .playerName(playerName)
              .date(createdAt)
              .rating(rating)
              .threeKill(stat.getMulti3k())
              .fourKill(stat.getMulti4k())
              .ace(stat.getMulti5k())
              .flash(stat.getFlashAssist())
              .trade(stat.getTradeKillAttempts())
              .smokeKill(0)
              .wallBang(0)
              .openKill(openKillCounts.getOrDefault(stat.getName(), 0))
              .type(MatchType.fromLeetifyName(gameDetail.getDataSource()))
              .map(MapType.fromLeetifyName(gameDetail.getMapName()))
              .build();

          // Подсчёт clutch-данных для игрока
          int clutchOne = 0, clutchTwo = 0, clutchThree = 0, clutchFour = 0, clutchFive = 0;
          if (clutches != null) {
            for (ClutchData clutch : clutches) {
              if (clutch.getClutchesWon() == 1 && stat.getSteam64Id().equals(clutch.getSteam64Id())) {
                switch (clutch.getHandicap()) {
                  case 0:
                    clutchOne++;
                    break;
                  case -1:
                    clutchTwo++;
                    break;
                  case -2:
                    clutchThree++;
                    break;
                  case -3:
                    clutchFour++;
                    break;
                  case -4:
                    clutchFive++;
                    break;
                  default:
                    break;
                }
              }
            }
          }

          matchItem = matchItem.toBuilder()
              .clutchOne(clutchOne)
              .clutchTwo(clutchTwo)
              .clutchThree(clutchThree)
              .clutchFour(clutchFour)
              .clutchFive(clutchFive)
              .build();


          // Сохранение объекта в базе
          log.info("matchItem123: {}", matchItem);
          addedMatches.add(matchItem);
          matchRepository.save(matchItem);
          existingMatchKeys.add(key);
        }
      }
    }
    changingExcelService.addMatches(addedMatches);
  }

  /**
   * Выполняет POST запрос для авторизации. Передаются email и password, возвращается токен при
   * успешном запросе.
   */
  public List<String> getAllTokens(List<Account> accounts) {
    List<String> tokens = new ArrayList<>();
    for (Account account : accounts) {
      String token = login(account.getLogin(), account.getPassword(), restTemplate);
      if (token != null) {
        tokens.add(token);
      } else {
        System.out.println("Не удалось получить токен для: " + account.getLogin());
      }
    }
    return tokens;
  }

  private String login(String email, String password, RestTemplate restTemplate) {
    try {
      HttpHeaders headers = new HttpHeaders();
      headers.setContentType(MediaType.APPLICATION_JSON);
      Map<String, String> body = new HashMap<>();
      body.put("email", email);
      body.put("password", password);
      HttpEntity<Map<String, String>> request = new HttpEntity<>(body, headers);
      ResponseEntity<LoginResponse> response = restTemplate.postForEntity(LOGIN_URL, request,
          LoginResponse.class);
      if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
        return response.getBody().getToken();
      }
    } catch (Exception e) {
      System.out.println("Ошибка при авторизации: " + e.getMessage());
    }
    return null;
  }

  /**
   * Выполняет GET запрос для получения истории игр. Из ответа извлекается список объектов,
   * содержащих поля id и createdAt. Результат сортируется по убыванию даты createdAt, после чего
   * возвращается список id.
   */
  private List<GameIdWrapper> getGameHistory(String token) {
    try {
      HttpHeaders headers = new HttpHeaders();
      headers.set("Authorization", "Bearer " + token);
      HttpEntity<Void> request = new HttpEntity<>(headers);

      ResponseEntity<GameHistoryResponse> response = restTemplate.exchange(
          HISTORY_URL, HttpMethod.GET, request, GameHistoryResponse.class);

      if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
          return response.getBody().getGames();
      }
    } catch (Exception e) {
      System.out.println("Ошибка при получении истории игр: " + e.getMessage());
    }
    return Collections.emptyList();
  }

  /**
   * Выполняет GET запрос для получения деталей игры по id.
   */
  private GameDetail getGameDetail(String gameId) {
    try {
      HttpHeaders headers = new HttpHeaders();
      HttpEntity<Void> request = new HttpEntity<>(headers);
      Map<String, String> uriVariables = new HashMap<>();
      uriVariables.put("id", gameId);
      ResponseEntity<GameDetail> response = restTemplate.exchange(
          GAME_DETAIL_URL, HttpMethod.GET, request, GameDetail.class, uriVariables);
      if (response.getStatusCode() == HttpStatus.OK) {
        return response.getBody();
      }
    } catch (Exception e) {
      System.out.println(
          "Ошибка при получении деталей игры (id=" + gameId + "): " + e.getMessage());
    }
    return null;
  }

  /**
   * Выполняет GET запрос для получения clutch-данных по игре.
   */
  private List<ClutchData> getClutches(String gameId) {
    try {
      HttpHeaders headers = new HttpHeaders();
      HttpEntity<Void> request = new HttpEntity<>(headers);
      Map<String, String> uriVariables = new HashMap<>();
      uriVariables.put("id", gameId);
      ResponseEntity<ClutchData[]> response = restTemplate.exchange(
          CLUTCHES_URL, HttpMethod.GET, request, ClutchData[].class, uriVariables);
      if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
        return Arrays.asList(response.getBody());
      }
    } catch (Exception e) {
      System.out.println(
          "Ошибка при получении clutch-данных для игры (id=" + gameId + "): " + e.getMessage());
    }
    return Collections.emptyList();
  }

  /**
   * Выполняет GET запрос для получения opening-duels по игре.
   */
  private List<OpeningDuel> getOpeningDuels(String gameId) {
    try {
      HttpHeaders headers = new HttpHeaders();
      HttpEntity<Void> request = new HttpEntity<>(headers);
      Map<String, String> uriVariables = new HashMap<>();
      uriVariables.put("id", gameId);
      ResponseEntity<OpeningDuel[]> response = restTemplate.exchange(
          OPENING_DUELS_URL, HttpMethod.GET, request, OpeningDuel[].class, uriVariables);
      if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
        return Arrays.asList(response.getBody());
      }
    } catch (Exception e) {
      System.out.println(
          "Ошибка при получении opening-duels для игры (id=" + gameId + "): " + e.getMessage());
    }
    return Collections.emptyList();
  }

  /**
   * Парсинг строки с датой (предполагается формат ISO_DATE_TIME).
   */
  private LocalDateTime parseDate(String dateStr) {
    try {
      return OffsetDateTime.parse(dateStr).toLocalDateTime();
    } catch (Exception e) {
      System.out.println("Ошибка парсинга даты: " + dateStr);
      return null;
    }
  }


  // --- DTO классы ---
  @Data
  @FieldDefaults(level = AccessLevel.PRIVATE)
  public static class LoginResponse {

    String token;
  }

  /**
   * DTO для ответа /history. Извлекается поле games – список объектов, каждый из которых содержит
   * только id игры.
   */
  @Data
  @FieldDefaults(level = AccessLevel.PRIVATE)
  public static class GameHistoryResponse {

    List<GameIdWrapper> games;
  }

  @Data
  @FieldDefaults(level = AccessLevel.PRIVATE)
  @AllArgsConstructor
  @NoArgsConstructor
  public static class GameIdWrapper {

    String id;
    String createdAt;
  }

  @Data
  @FieldDefaults(level = AccessLevel.PRIVATE)
  public static class GameDetail {

    String createdAt;
    String dataSource;
    String mapName;
    List<PlayerStat> playerStats;
  }

  @Data
  @FieldDefaults(level = AccessLevel.PRIVATE)
  public static class PlayerStat {

    String name;
    String steam64Id;
    Double hltvRating;
    Integer multi3k;
    Integer multi4k;
    Integer multi5k;
    Integer flashAssist;
    Integer tradeKillAttempts;
  }

  @Data
  @FieldDefaults(level = AccessLevel.PRIVATE)
  public static class ClutchData {

    String steam64Id;
    int clutchesWon;
    int handicap;
  }

  @Data
  @FieldDefaults(level = AccessLevel.PRIVATE)
  public static class OpeningDuel {

    String attackerName;
  }

  /**
   * Вспомогательный класс для проверки дубликатов матчей. Сравнение происходит по date, playerName
   * и rating.
   */

  @Data
  @AllArgsConstructor
  @FieldDefaults(level = AccessLevel.PRIVATE)
  public static class MatchKey {

    LocalDateTime date;
    PlayerName playerName;
    Double rating;

    @Override
    public boolean equals(Object o) {
      if (this == o) {
        return true;
      }
      if (o == null || getClass() != o.getClass()) {
        return false;
      }
      MatchKey that = (MatchKey) o;
      return Objects.equals(date, that.date) &&
          playerName == that.playerName &&
          Objects.equals(rating, that.rating);
    }

    @Override
    public int hashCode() {
      return Objects.hash(date, playerName, rating);
    }
  }
}
