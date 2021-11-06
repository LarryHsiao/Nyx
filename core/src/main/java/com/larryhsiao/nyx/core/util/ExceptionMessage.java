package com.larryhsiao.nyx.core.util;

import com.larryhsiao.clotho.Source;

public class ExceptionMessage implements Source<String> {
    private final Exception exception;

    public ExceptionMessage(Exception exception) {this.exception = exception;}

    @Override
    public String value() {
        return findMessage(exception);
    }

    private String findMessage(Throwable e) {
        if (e.getCause() != null) {
            return findMessage(e.getCause());
        } else {
            return e.getMessage();
        }
    }
}
