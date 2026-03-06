package com.cs.doceho.stats.bot.v2.api;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.io.IOException;

@Api(value = "Запросы к получению данных из сторонних сервисов", tags = {"import"})
@RequestMapping("/import")
public interface ImportApi {

    @ApiOperation(value = "Получение данных из leetify")
    @PostMapping("/leetify")
    ResponseEntity<?> getFromLeetify() throws IOException;

    @ApiOperation(value = "Получение данных из scopegg")
    @PostMapping("/scopegg")
    ResponseEntity<?> getFromScopeGG() throws IOException;

    @ApiOperation(value = "Импорт матча из scopegg")
    @PostMapping("/scopegg/{id}")
    ResponseEntity<?> getMatch(@PathVariable(value = "id") String Id) throws IOException;
}
