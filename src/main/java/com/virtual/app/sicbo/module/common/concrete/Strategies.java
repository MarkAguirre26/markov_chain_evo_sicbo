package com.virtual.app.sicbo.module.common.concrete;

import lombok.Getter;

@Getter
public enum Strategies {

   
    WINLOCK("WINLOCK"),
    TREND_OF_TWO("TREND_OF_TWO"),
    TREND_OF_THREE("TREND_OF_THREE"),
    FREEHAND("FREEHAND"),
    STOCHASTIC("STOCHASTIC"),
    KISS_MODIFIED("KISS_MODIFIED"),
    FLAT("FLAT"),
    STRATEGY("STRATEGY");
   
//    BASE_BET("BASE_BET");

    private final String value;

    Strategies(String value) {
        this.value = value;
    }

}
