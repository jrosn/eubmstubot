package ru.rvsosn.eubmstubot.eubmstu;

import one.util.streamex.StreamEx;

import java.util.Map;
import java.util.Set;

public class GetAllGroupsInLastSessionResult {
    private final Map<String, String> groups;

    GetAllGroupsInLastSessionResult(Map<String, String> groups) {
        this.groups = groups;
    }

    public Set<String> getGroupNames() {
        return StreamEx.ofKeys(groups)
                .toImmutableSet();
    }

    public String getGroupUrl(String byGroupName) {
        return groups.get(byGroupName);
    }

    @Override
    public String toString() {
        return groups.toString();
    }
}
