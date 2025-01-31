package com.cs.doceho.stats.bot.v2.api;

import com.cs.doceho.stats.bot.v2.model.Top;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import java.util.List;
import java.util.UUID;
import javax.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@Api(value = "Запросы к таблице с итогами года", tags = {"top"})
@RequestMapping("/top")
public interface TopApi {

  @GetMapping
  ResponseEntity<List<Top>> getAllTop();

  @ApiOperation(value = "Получение информации по году",
      nickname = "getYearTop", tags = {"top"})
  @GetMapping("/year/{year}")
  ResponseEntity<List<Top>> getYearTop(@PathVariable(value = "year") int year);

  @ApiOperation(value = "Получение информации по id",
      nickname = "getTopById", tags = {"top"})
  @GetMapping("/{id}")
  ResponseEntity<Top> getTopById(@PathVariable(value = "id") UUID topId);

  @ApiOperation(value = "Получение информации по имени",
      nickname = "getTopByName", tags = {"top"})
  @GetMapping("/name/{name}")
  ResponseEntity<List<Top>> getTopByName(@PathVariable(value = "name") String topName);

  @ApiOperation(value = "Создание итогов",
      nickname = "createTop", tags = {"top"})
  @PostMapping
  ResponseEntity<Top> createTop(@Valid @RequestBody Top top);

  @ApiOperation(value = "Изменение итогов по id",
      nickname = "updateTop", tags = {"top"})
  @PutMapping("/{id}")
  ResponseEntity<Top> updateTop(@PathVariable(value = "id") UUID topId,
      @Valid @RequestBody Top topDetails);

  @ApiOperation(value = "Удаление информации по id",
      nickname = "deleteTop", tags = {"top"})
  @DeleteMapping("/{id}")
  ResponseEntity<?> deleteTop(@PathVariable(value = "id") UUID topId);
}
