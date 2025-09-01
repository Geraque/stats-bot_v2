package com.cs.doceho.stats.bot.v2.db.model.enums;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

@AllArgsConstructor
@Getter
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public enum MatchType {
    MATCH_MAKING("ММ", "matchmaking_competitive", "competitive"),
    WINGMAN("Напарники", "matchmaking_wingman", "scrimcomp2v2"),
    PREMIER("Премьер", "matchmaking", "premier"),
    FACEIT("Faceit", "faceit", "faceit");

    String name;
    String leetifyName;
    String scopeggName;

    public static MatchType fromName(String name) {
        for (MatchType matchName : MatchType.values()) {
            if (matchName.name.equals(name)) {
                return matchName;
            }
        }
        return null;
    }

    public static MatchType fromLeetifyName(String name) {
        for (MatchType matchName : MatchType.values()) {
            if (matchName.leetifyName.equals(name)) {
                return matchName;
            }
        }
        return null;
    }

    public static MatchType fromScopegg(String name) {
        for (MatchType matchName : MatchType.values()) {
            if (matchName.scopeggName.equals(name)) {
                return matchName;
            }
        }
        return null;
    }

}
