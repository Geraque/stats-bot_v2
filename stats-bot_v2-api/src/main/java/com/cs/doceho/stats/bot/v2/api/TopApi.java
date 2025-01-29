package com.cs.doceho.stats.bot.v2.api;

import com.cs.doceho.stats.bot.v2.model.Top;
import com.cs.doceho.stats.bot.v2.exception.ResourceNotFoundException;
import java.util.List;
import java.util.Map;
import javax.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping("/top")
public interface TopApi {

  @GetMapping("/all")
  public List<Top> getAllTop();

  //Получение информации по году
  @GetMapping("/getbyyear/{year}")
  public ResponseEntity<List<String>> getYearTop(@PathVariable(value = "year") int year);

  //Получение информации по id
  @GetMapping("/{id}")
  public ResponseEntity<Top> getTopById(@PathVariable(value = "id") Long topId)
      throws ResourceNotFoundException;

  //Получение информации по имени
  @GetMapping("/getbyname/{name}")
  public ResponseEntity<List<Top>> getTopByName(@PathVariable(value = "name") String topName)
      throws ResourceNotFoundException;

  //Создание итогов
  @PostMapping("/create")
  public Top createTop(@Valid @RequestBody Top top);

  //Изменение итогов по id
  @PutMapping("/{id}")
  public ResponseEntity<Top> updateTop(@PathVariable(value = "id") Long topId,
      @Valid @RequestBody Top topDetails) throws ResourceNotFoundException;

  //Удаление информации по id
  @DeleteMapping("/{id}")
  public Map<String, Boolean> deleteTop(@PathVariable(value = "id") Long topId)
      throws ResourceNotFoundException;
}
