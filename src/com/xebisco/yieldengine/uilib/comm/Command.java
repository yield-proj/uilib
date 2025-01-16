package com.xebisco.yieldengine.uilib.comm;

public interface Command {
    record Argument(String argument, Class<?> type) {}

    default String tab() {
        return "";
    }
    default Argument[] args() {
        return null;
    }
    String name();
    void run(String[] args);
}
