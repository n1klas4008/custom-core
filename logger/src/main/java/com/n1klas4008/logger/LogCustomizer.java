package com.n1klas4008.logger;

public interface LogCustomizer {
    String onBeforeWrite(String line);
}
