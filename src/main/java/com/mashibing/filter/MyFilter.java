package com.mashibing.filter;


import ch.qos.logback.core.filter.Filter;
import ch.qos.logback.core.spi.FilterReply;
import org.springframework.boot.autoconfigure.web.servlet.WebMvcAutoConfiguration;

public class MyFilter extends Filter {
    @Override
    public FilterReply decide(Object event) {
        return null;
    }
}
