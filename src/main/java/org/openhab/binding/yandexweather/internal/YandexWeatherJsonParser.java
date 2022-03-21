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
import com.google.gson.JsonObject;
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
    private String dt;
    private String locality;

    public YandexWeatherJsonParser(String response) {
        JsonElement json = JsonParser.parseString(response);
        JsonObject jsonobj = json.getAsJsonObject();
        dt = jsonobj.get("now_dt").getAsString();
        locality = jsonobj.get("geo_object").getAsJsonObject().get("locality").getAsJsonObject().get("name")
                .getAsString();
        String factTemperature = jsonobj.get("fact").getAsJsonObject().get("temp").getAsString();
        String factFeelsLike = jsonobj.get("fact").getAsJsonObject().get("feels_like").getAsString();
        String factIcon = "https://yastatic.net/weather/i/icons/funky/dark/"
                + jsonobj.get("fact").getAsJsonObject().get("icon").getAsString() + ".svg";
        String factCondition = jsonobj.get("fact").getAsJsonObject().get("condition").getAsString();
        String factCloudness = jsonobj.get("fact").getAsJsonObject().get("cloudness").getAsString();
        String factIsThunder = jsonobj.get("fact").getAsJsonObject().get("is_thunder").getAsString();
        String factWindSpeed = jsonobj.get("fact").getAsJsonObject().get("wind_speed").getAsString();
        String factWindDir = jsonobj.get("fact").getAsJsonObject().get("wind_dir").getAsString();
        String factPressureMM = jsonobj.get("fact").getAsJsonObject().get("pressure_mm").getAsString();
        String factHumidity = jsonobj.get("fact").getAsJsonObject().get("humidity").getAsString();
        String factDaytime = jsonobj.get("fact").getAsJsonObject().get("daytime").getAsString();
        String factSeason = jsonobj.get("fact").getAsJsonObject().get("season").getAsString();
        String factSoilMoisture = jsonobj.get("fact").getAsJsonObject().get("soil_moisture").getAsString();
        String factSoilTemp = jsonobj.get("fact").getAsJsonObject().get("soil_temp").getAsString();
        String factUvIndex = jsonobj.get("fact").getAsJsonObject().get("uv_index").getAsString();
        String factWindGust = jsonobj.get("fact").getAsJsonObject().get("wind_gust").getAsString();
        String forecast = jsonobj.get("forecasts").getAsJsonArray().get(0).getAsJsonObject().get("week").getAsString();
    }

    @Override
    public String toString() {
        return "OK";
    }

    public String getDateTimeNow() {
        return dt;
    }
}
