package com.cs.doceho.stats.bot.v2.service;

import com.cs.doceho.stats.bot.v2.db.model.MatchItem;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicHeader;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;
import org.springframework.stereotype.Service;

@Service
public class CalculateRatingService {

  public double apply(MatchItem matchItem, int kills, int death, int assist, int avg, int kast) {
    try {
      // Создаем клиента
      CloseableHttpClient client = HttpClients.createDefault();

      // URL сервера
      String url = "http://127.0.0.1:5000/predict"; // Адрес вашего Flask-сервера

      // Создаем JSON-объект с данными
      JSONObject jsonData = new JSONObject();
      jsonData.put("kill", kills);
      jsonData.put("death", death);
      jsonData.put("assist", assist);
      jsonData.put("average_damage_round", avg);
      jsonData.put("kast%", kast);
      jsonData.put("open_kill", matchItem.getOpenKill());
      jsonData.put("three_kill", matchItem.getThreeKill());
      jsonData.put("four_kill", matchItem.getFourKill());
      jsonData.put("ace", matchItem.getAce());
      jsonData.put("flash", matchItem.getFlash());
      jsonData.put("trade", matchItem.getTrade());
      jsonData.put("clutch_one", matchItem.getClutchOne());
      jsonData.put("clutch_two", matchItem.getClutchTwo());
      jsonData.put("clutch_three", matchItem.getClutchThree());
      jsonData.put("clutch_four", matchItem.getClutchFour());
      jsonData.put("clutch_five", matchItem.getClutchFive());

      // Отправляем POST-запрос
      HttpPost post = new HttpPost(url);
      StringEntity entity = new StringEntity(jsonData.toString());
      entity.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
      post.setEntity(entity);

      // Получаем ответ
      HttpEntity responseEntity = client.execute(post).getEntity();
      String result = EntityUtils.toString(responseEntity);
      JSONObject responseJson = new JSONObject(result);

      // Получаем и выводим предсказанный рейтинг
      System.out.println("Predicted Rating: " + responseJson.getDouble("predicted_rating"));
      return responseJson.getDouble("predicted_rating");
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}
