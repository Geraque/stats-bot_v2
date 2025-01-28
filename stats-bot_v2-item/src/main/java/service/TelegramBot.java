package service;


import config.BotConfig;
import java.util.ArrayList;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
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
public class TelegramBot extends TelegramLongPollingBot {

  final BotConfig config;

  public TelegramBot(BotConfig config) {
    this.config = config;
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
          topWallbang(chatId);
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
        case "Статистика Desmond":
          log.info("Статистика Desmond");
          allStatsByName(chatId, "Desmond");
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

  //Получение топ клатчера
  private void topClutch(long chatId) {
    RestTemplate restTemplate = new RestTemplate();

    String http = restTemplate.getForObject("http://localhost:8080/getclutches", String.class);
    //Удаление лишних символов
    http = http.replaceAll("[\\[-\\]\"]", "");
    String[] arr = http.split(",");

    String answer = "Топ по клатчам: " +
        "\nИгрок: " + arr[0] +
        "\nВсего игр: " + arr[7] +
        "\nОбщее количество клатчей: " + arr[1] +
        "\n1vs1: " + arr[2] +
        "\n1vs2: " + arr[3] +
        "\n1vs3: " + arr[4] +
        "\n1vs4: " + arr[5] +
        "\n1vs5: " + arr[6];
    sendMessage(chatId, answer);
  }

  //Получение топа за определённый год
  private void topYear(long chatId, int year) {
    RestTemplate restTemplate = new RestTemplate();

    String http = restTemplate.getForObject("http://localhost:8080/top/getbyyear/" + year,
        String.class);
    http = http.replaceAll("[\\[-\\]\"]", "");
    String[] arr = http.split(",");

    StringBuilder answerb = new StringBuilder("Топ " + year + " года: ");

    for (int i = 1; i < arr.length; i += 3) {
      answerb.append("\nИгрок: ").append(arr[i])
          .append("\nМесто: ").append(arr[i + 1])
          .append("\nОбщий рейтинг: ").append(arr[i + 2]);
      answerb.append("\n------------------");
    }
    String answer = String.valueOf(answerb);
    sendMessage(chatId, answer);
  }

  //Получение всей статистики игрока
  private void allStatsByName(long chatId, String name) {
    RestTemplate restTemplate = new RestTemplate();

    String http = restTemplate.getForObject("http://localhost:8080/getplayerstats/" + name,
        String.class);
    http = http.replaceAll("[\\[-\\]\"]", "");
    String[] arr = http.split(",");

    StringBuilder answerb = new StringBuilder(
        "Статистика " + name + " за всё время: " + "\n------\n");

    answerb.append("\nКол-во матчей: ").append(arr[0])
        .append("\nРейтинг: ").append(arr[1])
        .append("\nУбийства в смок: ").append(arr[2])
        .append("\nЭнтри: ").append(arr[3])
        .append("\nТрипл килл: ").append(arr[4])
        .append("\nКвадро килл: ").append(arr[5])
        .append("\nЭйс: ").append(arr[6])
        .append("\nФлеш: ").append(arr[7])
        .append("\nРазмен: ").append(arr[8])
        .append("\nПрострел: ").append(arr[9])
        .append("\n1vs1: ").append(arr[10])
        .append("\n1vs2: ").append(arr[11])
        .append("\n1vs3: ").append(arr[12])
        .append("\n1vs4: ").append(arr[13])
        .append("\n1vs5: ").append(arr[14]);

    String answer = String.valueOf(answerb);
    sendMessage(chatId, answer);

  }

  //Получение последних 7 матчей игрока
  private void allMatchesByPlayer(long chatId, String name) {
    RestTemplate restTemplate = new RestTemplate();

    String http = restTemplate.getForObject("http://localhost:8080/getbyname/" + name,
        String.class);
    http = http.replaceAll("[\\[-\\]\"{}]", "");
    String[] arr = http.split(",");

    StringBuilder answerb = new StringBuilder("Последние 7 матчей " + name + ":\n");
    for (int i = 0; i < arr.length; i += 18) {
      answerb.append("\n").append(arr[i + 2])
          .append("\n").append(arr[i + 3])
          .append("\n").append(arr[i + 4])
          .append("\n").append(arr[i + 5])
          .append("\n").append(arr[i + 6])
          .append("\n").append(arr[i + 7])
          .append("\n").append(arr[i + 8])
          .append("\n").append(arr[i + 9])
          .append("\n").append(arr[i + 10])
          .append("\n").append(arr[i + 11])
          .append("\n").append(arr[i + 12])
          .append("\n").append(arr[i + 13])
          .append("\n").append(arr[i + 14])
          .append("\n").append(arr[i + 15])
          .append("\n").append(arr[i + 16])
          .append("\n").append(arr[i + 17]);
      answerb.append("\n------------------");
    }

    String answer = String.valueOf(answerb);
    sendMessage(chatId, answer);
  }

  //Получение топ 1 по рейтингу
  private void topRating(long chatId) {
    RestTemplate restTemplate = new RestTemplate();

    String http = restTemplate.getForObject("http://localhost:8080/getrating", String.class);
    http = http.replaceAll("[\\[-\\]\"]", "");
    String[] arr = http.split(",");

    String answer = "Топ по рейтингу: " +
        "\nИгрок: " + arr[0] +
        "\nВсего игр: " + arr[1] +
        "\nОбщий рейтинг: " + arr[2];
    sendMessage(chatId, answer);
  }

  //Получение топ 1 по опен килам
  private void topOpenKill(long chatId) {
    RestTemplate restTemplate = new RestTemplate();

    String http = restTemplate.getForObject("http://localhost:8080/getopenkill", String.class);
    http = http.replaceAll("[\\[-\\]\"]", "");
    String[] arr = http.split(",");
    String answer = "Топ по энтри: " +
        "\nИгрок: " + arr[0] +
        "\nВсего игр: " + arr[1] +
        "\nЭнтри за матч: " + arr[2];
    sendMessage(chatId, answer);
  }

  //Получение топ 1 по флешкам
  private void topFlash(long chatId) {
    RestTemplate restTemplate = new RestTemplate();

    String http = restTemplate.getForObject("http://localhost:8080/getflash", String.class);
    http = http.replaceAll("[\\[-\\]\"]", "");
    String[] arr = http.split(",");
    String answer = "Топ по кол-ву флешек: " +
        "\nИгрок: " + arr[0] +
        "\nВсего игр: " + arr[1] +
        "\nФлешек за матч: " + arr[2];
    sendMessage(chatId, answer);
  }

  //Получение топ 1 по разменам
  private void topTrade(long chatId) {
    RestTemplate restTemplate = new RestTemplate();

    String http = restTemplate.getForObject("http://localhost:8080/gettrade", String.class);
    http = http.replaceAll("[\\[-\\]\"]", "");
    String[] arr = http.split(",");
    String answer = "Топ по кол-ву разменов: " +
        "\nИгрок: " + arr[0] +
        "\nВсего игр: " + arr[1] +
        "\nРазменов за матч: " + arr[2];
    sendMessage(chatId, answer);
  }

  //Получение топ 1 по прострелам
  private void topWallbang(long chatId) {
    RestTemplate restTemplate = new RestTemplate();

    String http = restTemplate.getForObject("http://localhost:8080/getwallbang", String.class);
    http = http.replaceAll("[\\[-\\]\"]", "");
    String[] arr = http.split(",");
    String answer = "Топ по кол-ву прострелов: " +
        "\nИгрок: " + arr[0] +
        "\nВсего игр: " + arr[1] +
        "\nПрострелов за матч: " + arr[2];
    sendMessage(chatId, answer);
  }

  //Получение топ 1 по трипл киллам
  private void topThreeKill(long chatId) {
    RestTemplate restTemplate = new RestTemplate();

    String http = restTemplate.getForObject("http://localhost:8080/getthreekill", String.class);
    http = http.replaceAll("[\\[-\\]\"]", "");
    String[] arr = http.split(",");
    String answer = "Топ по кол-ву трипл киллов: " +
        "\nИгрок: " + arr[0] +
        "\nВсего игр: " + arr[1] +
        "\nТрипл киллов за матч: " + arr[2];
    sendMessage(chatId, answer);
  }

  //Получение топ 1 по квадро киллам
  private void topFourKill(long chatId) {
    RestTemplate restTemplate = new RestTemplate();

    String http = restTemplate.getForObject("http://localhost:8080/getfourkill", String.class);
    http = http.replaceAll("[\\[-\\]\"]", "");
    String[] arr = http.split(",");
    String answer = "Топ по кол-ву квадро киллов: " +
        "\nИгрок: " + arr[0] +
        "\nВсего игр: " + arr[1] +
        "\nКвадро киллов за матч: " + arr[2];
    sendMessage(chatId, answer);
  }

  //Получение топ 1 по эйсам
  private void topAce(long chatId) {
    RestTemplate restTemplate = new RestTemplate();

    String http = restTemplate.getForObject("http://localhost:8080/getace", String.class);
    http = http.replaceAll("[\\[-\\]\"]", "");
    String[] arr = http.split(",");
    String answer = "Топ по кол-ву эйсов: " +
        "\nИгрок: " + arr[0] +
        "\nВсего игр: " + arr[1] +
        "\nэйсов за матч: " + arr[2];
    sendMessage(chatId, answer);
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