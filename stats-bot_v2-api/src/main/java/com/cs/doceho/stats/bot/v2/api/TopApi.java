package com.cs.doceho.stats.bot.v2.api;

import com.cs.doceho.stats.bot.v2.exception.ResourceNotFoundException;
import com.cs.doceho.stats.bot.v2.model.Top;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
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

@Api(value = "Запросы к таблице с итогами года", tags = {"top"})
@RequestMapping("/top")
public interface TopApi {

  @GetMapping
  List<Top> getAllTop();

  @ApiOperation(value = "Получение информации по году",
      nickname = "getYearTop", tags = {"top"})
  @GetMapping("/year/{year}")
  ResponseEntity<List<String>> getYearTop(@PathVariable(value = "year") int year);

  @ApiOperation(value = "Получение информации по id",
      nickname = "getTopById", tags = {"top"})
  @GetMapping("/{id}")
  ResponseEntity<Top> getTopById(@PathVariable(value = "id") Long topId)
      throws ResourceNotFoundException;

  @ApiOperation(value = "Получение информации по имени",
      nickname = "getTopByName", tags = {"top"})
  @GetMapping("/name/{name}")
  ResponseEntity<List<Top>> getTopByName(@PathVariable(value = "name") String topName);

  @ApiOperation(value = "Создание итогов",
      nickname = "createTop", tags = {"top"})
  @PostMapping
  Top createTop(@Valid @RequestBody Top top);

  @ApiOperation(value = "Изменение итогов по id",
      nickname = "updateTop", tags = {"top"})
  @PutMapping("/{id}")
  ResponseEntity<Top> updateTop(@PathVariable(value = "id") Long topId,
      @Valid @RequestBody Top topDetails) throws ResourceNotFoundException;

  @ApiOperation(value = "Удаление информации по id",
      nickname = "deleteTop", tags = {"top"})
  @DeleteMapping("/{id}")
  Map<String, Boolean> deleteTop(@PathVariable(value = "id") Long topId)
      throws ResourceNotFoundException;
}
