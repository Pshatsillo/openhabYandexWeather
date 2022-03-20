/**
 * Copyright (c) 2010-2021 Contributors to the openHAB project
 *
 * See the NOTICE file(s) distributed with this work for additional
 * information.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 */
package org.openhab.binding.yandexweather.internal;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

/**
 * The {@link YandexWeatherJsonParser} is responsible for Yandex API parsing, which are
 * sent to one of the channels.
 *
 * @author Petr Shatsillo - Initial contribution
 */
@NonNullByDefault
public class YandexWeatherJsonParser {
    private final Logger logger = LoggerFactory.getLogger(YandexWeatherJsonParser.class);

    public YandexWeatherJsonParser(String response) {
        JsonElement json = JsonParser.parseString(response);
        logger.debug("json - > {}", json);
    }

    @Override
    public String toString() {
        return "OK";
    }
}
