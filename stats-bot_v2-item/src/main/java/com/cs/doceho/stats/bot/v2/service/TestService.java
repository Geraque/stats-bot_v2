package com.cs.doceho.stats.bot.v2.service;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;
import org.springframework.stereotype.Service;

@Service
public class TestService {

  public void test() {
    try {
      // Создаем клиента
      CloseableHttpClient client = HttpClients.createDefault();

      // URL сервера
      String url = "http://127.0.0.1:5000/predict"; // Адрес вашего Flask-сервера

      // Создаем JSON-объект с данными
      JSONObject jsonData = new JSONObject();
      jsonData.put("kill", 5);
      jsonData.put("death", 13);
      jsonData.put("assist", 7);
      jsonData.put("average_damage_round", 45);
      jsonData.put("kast%", 76);
      jsonData.put("open_kill", 1);
      jsonData.put("three_kill", 0);
      jsonData.put("four_kill", 0);
      jsonData.put("ace", 0);
      jsonData.put("flash", 0);
      jsonData.put("trade", 1);
      jsonData.put("clutch_one", 0);
      jsonData.put("clutch_two", 0);
      jsonData.put("clutch_three", 0);
      jsonData.put("clutch_four", 0);
      jsonData.put("clutch_five", 0);

      // Отправляем POST-запрос
      HttpPost post = new HttpPost(url);
      post.setEntity(new org.apache.http.entity.StringEntity(jsonData.toString()));

      // Получаем ответ
      HttpEntity responseEntity = client.execute(post).getEntity();
      String result = EntityUtils.toString(responseEntity);
      JSONObject responseJson = new JSONObject(result);

      // Получаем и выводим предсказанный рейтинг
      System.out.println("Predicted Rating: " + responseJson.getDouble("predicted_rating"));
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}
