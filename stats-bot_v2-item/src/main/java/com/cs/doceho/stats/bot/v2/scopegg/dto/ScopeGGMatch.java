package com.cs.doceho.stats.bot.v2.scopegg.dto;

import com.cs.doceho.stats.bot.v2.db.model.enums.MatchResult;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.AccessLevel;

@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ScopeGGMatch {
    String id;          // из MatchID
    String finishedAt;  // из MatchTime (строка)
    MatchResult result;
}

