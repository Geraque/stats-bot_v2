package com.cs.doceho.stats.bot.v2.service;


import com.cs.doceho.stats.bot.v2.api.CategoryApi;
import com.cs.doceho.stats.bot.v2.api.MatchApi;
import com.cs.doceho.stats.bot.v2.api.TopApi;
import com.cs.doceho.stats.bot.v2.config.BotConfig;
import com.cs.doceho.stats.bot.v2.model.Match;
import com.cs.doceho.stats.bot.v2.model.Player;
import com.cs.doceho.stats.bot.v2.model.Top;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.commands.SetMyCommands;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendSticker;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.commands.BotCommand;
import org.telegram.telegrambots.meta.api.objects.commands.scope.BotCommandScopeDefault;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Slf4j
@Component
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class TelegramBot extends TelegramLongPollingBot {

  BotConfig config;
  MatchApi matchApi;
  TopApi topApi;
  CategoryApi categoryApi;

  public TelegramBot(BotConfig config, MatchApi matchApi, TopApi topApi, CategoryApi categoryApi) {
    this.config = config;
    this.matchApi = matchApi;
    this.topApi = topApi;
    this.categoryApi = categoryApi;
    //Добавление меню
    List<BotCommand> listOfCommands = new ArrayList<>();
    listOfCommands.add(new BotCommand("/start", "Вернуться в главное меню"));
    try {
      this.execute(new SetMyCommands(listOfCommands, new BotCommandScopeDefault(), null));
    } catch (TelegramApiException e) {
      log.error("Error occurred: " + e.getMessage());
    }
  }

  @Override
  public String getBotUsername() {
    return config.getBotName();
  }

  @Override
  public String getBotToken() {
    return config.getToken();
  }

  //Получение и реакция на сообщение
  @Override
  public void onUpdateReceived(Update update) {

    if (update.hasMessage() && update.getMessage().hasText()) {
      String messageText = update.getMessage().getText();
      long chatId = update.getMessage().getChatId();

      switch (messageText) {
        case "/start":
          log.info("/start");
          sendMessage(chatId, "Hi, Cheboksar!");
          break;
        case "Игрок года":
          log.info("Игрок года");
          sendMessage(chatId, "Выберите год");
          break;
        case "Топ по категории":
          log.info("Топ по категории");
          sendMessage(chatId, "Выберите категорию");
          break;
        case "Топ со всей статой":
          log.info("Топ со всей статой");
          sendMessage(chatId, "Нажми на кнопку");
          break;
        case "Вся статистика":
          log.info("Вся статистика");
          sendMessage(chatId, "Статистика, моя статистика");
          break;
        case "Топ клатч":
          log.info("Топ клатч");
          topClutch(chatId);
          break;
        case "Топ рейтинг":
          log.info("Топ рейтинг");
          topRating(chatId);
          break;
        case "Топ энтри":
          log.info("Топ энтри");
          topOpenKill(chatId);
          break;
        case "Топ флеш":
          log.info("Топ флеш");
          topFlash(chatId);
          break;
        case "Топ размен":
          log.info("Топ размен");
          topTrade(chatId);
          break;
        case "Топ прострел":
          log.info("Топ прострел");
          topWallBang(chatId);
          break;
        case "Топ 3 kill":
          log.info("Топ 3 kill");
          topThreeKill(chatId);
          break;
        case "Топ 4 kill":
          log.info("Топ 4 kill");
          topFourKill(chatId);
          break;
        case "Топ ace":
          log.info("Топ ace");
          topAce(chatId);
          break;
        case "18 год":
          log.info("18 год");
          topYear(chatId, 2018);
          break;
        case "19 год":
          log.info("19 год");
          topYear(chatId, 2019);
          break;
        case "20 год":
          log.info("20 год");
          topYear(chatId, 2020);
          break;
        case "21 год":
          log.info("21 год");
          topYear(chatId, 2021);
          break;
        case "22 год":
          log.info("22 год");
          topYear(chatId, 2022);
          break;
        case "23 год":
          log.info("23 год");
          topYear(chatId, 2023);
          break;
        case "24 год":
          log.info("24 год");
          topYear(chatId, 2024);
          break;
        case "Матчи Desmond":
          log.info("Матчи Desmond");
          allMatchesByPlayer(chatId, "Desmond");
          break;
        case "Матчи BlackVision":
          log.info("Матчи BlackVision");
          allMatchesByPlayer(chatId, "BlackVision");
          break;
        case "Матчи B4one":
          log.info("Матчи B4one");
          allMatchesByPlayer(chatId, "B4one");
          break;
        case "Матчи Gloxinia":
          log.info("Матчи Gloxinia");
          allMatchesByPlayer(chatId, "Gloxinia");
          break;
        case "Матчи 221w33":
          log.info("Матчи 221w33");
          allMatchesByPlayer(chatId, "221w33");
          break;
        case "Матчи Kopfire":
          log.info("Матчи Kopfire");
          allMatchesByPlayer(chatId, "Kopfire");
          break;
        case "Матчи MVforever01":
          log.info("Матчи MVforever01");
          allMatchesByPlayer(chatId, "MVforever01");
          break;
        case "Матчи Wolf_SMXL":
          log.info("Матчи Wolf_SMXL");
          allMatchesByPlayer(chatId, "Wolf_SMXL");
          break;
        case "Статистика Desmond":
          log.info("Статистика Desmond");
          allStatsByName(chatId, "Desmond\uD83C\uDFB4");
          break;
        case "Статистика BlackVision":
          log.info("Статистика BlackVision");
          allStatsByName(chatId, "BlackVision");
          break;
        case "Статистика B4one":
          log.info("Статистика B4one");
          allStatsByName(chatId, "B4one");
          break;
        case "Статистика Gloxinia":
          log.info("Статистика Gloxinia");
          allStatsByName(chatId, "Gloxinia");
          break;
        case "Статистика 221w33":
          log.info("Статистика 221w33");
          allStatsByName(chatId, "221w33");
          break;
        case "Статистика Kopfire":
          log.info("Статистика Kopfire");
          allStatsByName(chatId, "Kopfire");
          break;
        case "Статистика MVforever01":
          log.info("Статистика MVforever01");
          allStatsByName(chatId, "MVforever01");
          break;
        case "Статистика Wolf_SMXL":
          log.info("Статистика Wolf_SMXL");
          allStatsByName(chatId, "Wolf_SMXL");
          break;
        case "Статистика Wesdia":
          log.info("Статистика Wesdia");
          allStatsByName(chatId, "Wesdia");
          break;
        case "Ну нажми, ну пожалуйста":
          log.info("Ну нажми, ну пожалуйста");
          sendMessage(chatId, update.getMessage().getChat().getLastName() +
              " ты думал что-то здесь будет? О нет. От тебя воняет говном, даже отсюда чувствую," +
              " закрывай, закрывай бота и иди нахуй. Друг крутой, а ты лоханулся, сука. Аа, блядь. А.");
          break;
        default:
          sendSticker(chatId);
      }
    }
  }

  private void topClutch(long chatId) {
    Player player = categoryApi.getClutches().getBody();

    if (player == null) {
      sendMessage(chatId, "Данные по клатчам не найдены.");
      return;
    }

    String message = String.format(
        "Топ по клатчам:\nИгрок: %s\nВсего игр: %d\nОбщее количество клатчей: %d\n1vs1: %d\n1vs2: %d\n1vs3: %d\n1vs4: %d\n1vs5: %d",
        player.getName(), player.getMatches(),
        player.getClutchOne() + player.getClutchTwo() + player.getClutchThree()
            + player.getClutchFour() + player.getClutchFive(),
        player.getClutchOne(), player.getClutchTwo(), player.getClutchThree(),
        player.getClutchFour(), player.getClutchFive());

    sendMessage(chatId, message);
  }

  //Получение топа за определённый год
  private void topYear(long chatId, int year) {
    List<Top> topList = topApi.getYearTop(year).getBody();

    if (topList == null || topList.isEmpty()) {
      sendMessage(chatId, String.format("Данные за %d год не найдены.", year));
      return;
    }

    String message = topList.stream()
        .map(top -> String.format("\nИгрок: %s\nМесто: %d\nОбщий рейтинг: %.2f\n------------------",
            top.getPlayerName(), top.getPlace(), top.getRating()))
        .collect(Collectors.joining("", String.format("Топ %d года:", year), ""));

    sendMessage(chatId, message);
  }

  private void allStatsByName(long chatId, String name) {
    Player player = matchApi.getPlayerStats(name).getBody();

    if (player == null) {
      sendMessage(chatId, String.format("Данные о статистике игрока %s не найдены.", name));
      return;
    }
    log.info("player: {}", player);
    String message = String.format(
        "Статистика %s за всё время:\n------\n\nКол-во матчей: %d\nРейтинг: %.2f\nУбийства в смок: %d\nЭнтри: %d\nТрипл килл: %d\nКвадро килл: %d\nЭйс: %d\nФлеш: %d\nРазмен: %d\nПрострел: %d\n1vs1: %d\n1vs2: %d\n1vs3: %d\n1vs4: %d\n1vs5: %d",
        name, player.getMatches(), player.getRating(), player.getSmokeKill(), player.getOpenKill(),
        player.getThreeKill(), player.getFourKill(), player.getAce(), player.getFlash(),
        player.getTrade(), player.getWallBang(), player.getClutchOne(), player.getClutchTwo(),
        player.getClutchThree(), player.getClutchFour(), player.getClutchFive());

    sendMessage(chatId, message);
  }

  //Получение последних 7 матчей игрока
  private void allMatchesByPlayer(long chatId, String name) {
    List<Match> matches = matchApi.getMatchByName(name).getBody();

    if (matches == null || matches.isEmpty()) {
      sendMessage(chatId, String.format("Данные о матчах игрока %s не найдены.", name));
      return;
    }

    String message = matches.stream()
        .limit(7)
        .map(match -> String.format(
            "\nДата: %s\nРейтинг: %.2f\nSmoke Kills: %d\nOpen Kills: %d\nTriple Kill: %d\nQuadro Kill: %d\nAce: %d\nFlash: %d\nTrade: %d\nWall Bang: %d\n1vs1: %d\n1vs2: %d\n1vs3: %d\n1vs4: %d\n1vs5: %d\n------------------",
            match.getDate(), match.getRating(), match.getSmokeKill(), match.getOpenKill(),
            match.getThreeKill(), match.getFourKill(), match.getAce(), match.getFlash(),
            match.getTrade(), match.getWallBang(), match.getClutchOne(), match.getClutchTwo(),
            match.getClutchThree(), match.getClutchFour(), match.getClutchFive()))
        .collect(Collectors.joining("", String.format("Последние 7 матчей %s:\n", name), ""));

    sendMessage(chatId, message);
  }

  //Получение топ 1 по рейтингу
  private void topRating(long chatId) {
    Player player = categoryApi.getRating().getBody();

    if (player == null) {
      sendMessage(chatId, "Данные о рейтинге не найдены.");
      return;
    }

    String message = String.format(
        "Топ по рейтингу:\nИгрок: %s\nВсего игр: %d\nОбщий рейтинг: %.2f",
        player.getName(), player.getMatches(), player.getRating());

    sendMessage(chatId, message);
  }

  //Получение топ 1 по опен килам
  private void topOpenKill(long chatId) {
    Player player = categoryApi.getOpenKill().getBody();

    if (player == null) {
      sendMessage(chatId, "Данные по энтри не найдены.");
      return;
    }

    String message = String.format("Топ по энтри:\nИгрок: %s\nВсего игр: %d\nЭнтри за матч: %.2f",
        player.getName(), player.getMatches(), (double) player.getRating() / player.getMatches());

    sendMessage(chatId, message);
  }

  private void topFlash(long chatId) {
    Player player = categoryApi.getFlash().getBody();

    if (player == null) {
      sendMessage(chatId, "Данные по флешкам не найдены.");
      return;
    }

    String message = String.format(
        "Топ по кол-ву флешек:\nИгрок: %s\nВсего игр: %d\nФлешек за матч: %.2f",
        player.getName(), player.getMatches(), (double) player.getRating() / player.getMatches());

    sendMessage(chatId, message);
  }

  private void topTrade(long chatId) {
    Player player = categoryApi.getTrade().getBody();

    if (player == null) {
      sendMessage(chatId, "Данные по разменам не найдены.");
      return;
    }

    String message = String.format(
        "Топ по кол-ву разменов:\nИгрок: %s\nВсего игр: %d\nРазменов за матч: %.2f",
        player.getName(), player.getMatches(), (double) player.getRating() / player.getMatches());

    sendMessage(chatId, message);
  }

  private void topWallBang(long chatId) {
    Player player = categoryApi.getWallBang().getBody();

    if (player == null) {
      sendMessage(chatId, "Данные по прострелам не найдены.");
      return;
    }

    String message = String.format(
        "Топ по кол-ву прострелов:\nИгрок: %s\nВсего игр: %d\nПрострелов за матч: %.2f",
        player.getName(), player.getMatches(), (double) player.getRating() / player.getMatches());

    sendMessage(chatId, message);
  }

  private void topThreeKill(long chatId) {
    Player player = categoryApi.getThreeKill().getBody();

    if (player == null) {
      sendMessage(chatId, "Данные по трипл киллам не найдены.");
      return;
    }

    String message = String.format(
        "Топ по кол-ву трипл киллов:\nИгрок: %s\nВсего игр: %d\nТрипл киллов за матч: %.2f",
        player.getName(), player.getMatches(), (double) player.getRating() / player.getMatches());

    sendMessage(chatId, message);
  }

  private void topFourKill(long chatId) {
    Player player = categoryApi.getFourKill().getBody();

    if (player == null) {
      sendMessage(chatId, "Данные по квадро киллам не найдены.");
      return;
    }

    String message = String.format(
        "Топ по кол-ву квадро киллов:\nИгрок: %s\nВсего игр: %d\nКвадро киллов за матч: %.2f",
        player.getName(), player.getMatches(), (double) player.getRating() / player.getMatches());

    sendMessage(chatId, message);
  }

  private void topAce(long chatId) {
    Player player = categoryApi.getAce().getBody();

    if (player == null) {
      sendMessage(chatId, "Данные по эйсам не найдены.");
      return;
    }

    String message = String.format(
        "Топ по кол-ву эйсов:\nИгрок: %s\nВсего игр: %d\nэйсов за матч: %.2f",
        player.getName(), player.getAce(), (double) player.getRating() / player.getMatches());

    sendMessage(chatId, message);
  }

  //Генерация изначальных кнопок
  private void startButton(SendMessage message) {
    ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();

    List<KeyboardRow> keyboardRows = new ArrayList<>();
    KeyboardRow row = new KeyboardRow();
    row.add("Игрок года");
    row.add("Топ по категории");
    row.add("Вся статистика");

    keyboardRows.add(row);

    keyboardMarkup.setKeyboard(keyboardRows);
    message.setReplyMarkup(keyboardMarkup);
  }

  //Генерация кнопок с выбором года для топа
  private void topPlayerButton(SendMessage message) {
    ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();

    List<KeyboardRow> keyboardRows = new ArrayList<>();
    KeyboardRow row = new KeyboardRow();
    row.add("18 год");
    row.add("19 год");
    row.add("20 год");

    keyboardRows.add(row);

    row = new KeyboardRow();
    row.add("21 год");
    row.add("22 год");
    row.add("23 год");

    keyboardRows.add(row);

    row = new KeyboardRow();
    row.add("24 год");

    keyboardRows.add(row);

    keyboardMarkup.setKeyboard(keyboardRows);
    message.setReplyMarkup(keyboardMarkup);

  }

  //Генерация кнопок с категориями топов
  private void topCategoryButton(SendMessage message) {
    ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();

    List<KeyboardRow> keyboardRows = new ArrayList<>();
    KeyboardRow row = new KeyboardRow();
    row.add("Топ рейтинг");
    row.add("Топ клатч");
    row.add("Топ энтри");

    keyboardRows.add(row);

    row = new KeyboardRow();
    row.add("Топ флеш");
    row.add("Топ размен");
    row.add("Топ прострел");

    keyboardRows.add(row);

    row = new KeyboardRow();
    row.add("Топ 3 kill");
    row.add("Топ 4 kill");
    row.add("Топ ace");

    keyboardRows.add(row);

    keyboardMarkup.setKeyboard(keyboardRows);
    message.setReplyMarkup(keyboardMarkup);

  }

  //Генерация кнопок с матчами игроков
  //TODO Не отображается
  private void allStatsButton(SendMessage message) {
    ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();

    List<KeyboardRow> keyboardRows = new ArrayList<>();
    KeyboardRow row = new KeyboardRow();
    row.add("Матчи Desmond");
    row.add("Матчи BlackVision");
    row.add("Матчи B4one");

    keyboardRows.add(row);
    row = new KeyboardRow();

    row.add("Матчи Gloxinia");
    row.add("Матчи 221w33");
    row.add("Матчи Kopfire");

    keyboardRows.add(row);
    row = new KeyboardRow();

    row.add("Матчи MVforever01");
    row.add("Матчи Wolf_SMXL");
    row.add("Топ со всей статой");

    keyboardRows.add(row);

    keyboardMarkup.setKeyboard(keyboardRows);
    message.setReplyMarkup(keyboardMarkup);

  }

  //Генерация кнопок со статистикой игроков
  private void playerStatsButton(SendMessage message) {
    ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();

    List<KeyboardRow> keyboardRows = new ArrayList<>();
    KeyboardRow row = new KeyboardRow();
    row.add("Статистика Desmond");
    row.add("Статистика BlackVision");
    row.add("Статистика B4one");

    keyboardRows.add(row);
    row = new KeyboardRow();

    row.add("Статистика Gloxinia");
    row.add("Статистика 221w33");
    row.add("Статистика Kopfire");

    keyboardRows.add(row);
    row = new KeyboardRow();

    row.add("Статистика MVforever01");
    row.add("Статистика Wolf_SMXL");
    row.add("Статистика Wesdia");

    keyboardRows.add(row);

    row = new KeyboardRow();
    row.add("Ну нажми, ну пожалуйста");
    keyboardRows.add(row);

    keyboardMarkup.setKeyboard(keyboardRows);
    message.setReplyMarkup(keyboardMarkup);

  }

  //Отправка текстового сообщения
  private void sendMessage(long chatId, String textToSend) {
    SendMessage message = new SendMessage();
    message.setChatId(String.valueOf(chatId));
    message.setText(textToSend);
    switch (textToSend) {
      case "Hi, Cheboksar!":
        startButton(message);
        break;
      case "Выберите год":
        topPlayerButton(message);
        break;
      case "Выберите категорию":
        topCategoryButton(message);
        break;
      case "Нажми на кнопку":
        allStatsButton(message);
        break;
      case "Статистика, моя статистика":
        playerStatsButton(message);
    }

    try {
      execute(message);
    } catch (TelegramApiException e) {
      log.error("Error occurred: " + e.getMessage());
    }
  }

  //Отправка стикера
  private void sendSticker(long chatId) {
    SendSticker sticker = new SendSticker();
    sticker.setChatId(String.valueOf(chatId));
    int count = (int) (Math.random() * (8 - 1)) + 1;
    InputFile inputFile;
    switch (count) {
      case 1:
        inputFile = new InputFile(
            "CAACAgIAAxkBAAEHgedj1rX8S4TWKfEQ-9Owr0gEJpDTnAACvhgAAj7NqUo7W-hz6hfpuS0E");
        break;
      case 2:
        inputFile = new InputFile(
            "CAACAgIAAxkBAAEHgelj1rX_jf2sltUaYjE4L8sdvikWAAOeFgACVxapSl9TpekM2nVTLQQ");
        break;
      case 3:
        inputFile = new InputFile(
            "CAACAgIAAxkBAAEHgetj1rYAAWmmy3WcLF2I6MVpXRgXHxEAAiIVAAKgHqlK67A2JSEfV8YtBA");
        break;
      case 4:
        inputFile = new InputFile(
            "CAACAgIAAxkBAAEHge1j1rYB7sMTOpql_5mII5Qr2HxXzwACXRMAAv9lqEr7OtCFuXEgni0E");
        break;
      case 5:
        inputFile = new InputFile(
            "CAACAgIAAxkBAAEHge9j1rYDFBzor5qiqZzJnb21ryMsfQACZBYAAlJTqUqb2388kE__4i0E");
        break;
      case 6:
        inputFile = new InputFile(
            "CAACAgIAAxkBAAEHgfJj1rYFHaqMODkmus-ysywF8rfP_gACmRUAApFvGUgSZwEa2E-Wei0E");
        break;
      case 7:
        inputFile = new InputFile(
            "CAACAgIAAxkBAAEHgfZj1rYHr-LW-yL-6Ee23M6kj8iKWQACsxsAAufYGEj0DKfma4RwmS0E");
        break;
      default:
        inputFile = new InputFile(
            "CAACAgIAAxkBAAEHgfhj1rYKYgxrQVA8ej3vTO27U1OMKAACtxYAAsbYIEgxxoDMn3yL4C0E");
    }
    sticker.setSticker(inputFile);

    try {
      execute(sticker);
    } catch (TelegramApiException e) {
      log.error("Error occurred: " + e.getMessage());
    }
  }
}