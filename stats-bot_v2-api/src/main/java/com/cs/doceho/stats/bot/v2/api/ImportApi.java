package com.cs.doceho.stats.bot.v2.api;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import java.io.IOException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Api(value = "Запросы к получению данных из сторонних сервисов", tags = {"import"})
@RequestMapping("/import")
public interface ImportApi {

  @ApiOperation(value = "Получение данных изи leetify",
      nickname = "getFromLeetify", tags = {"import"})
  @PostMapping("/leetify")
  ResponseEntity<?> getFromLeetify() throws IOException;
}
