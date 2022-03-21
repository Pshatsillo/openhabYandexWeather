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
    Logger logger = LoggerFactory.getLogger(YandexWeatherJsonParser.class);
    private String dayTime = "";
    private String locality = "";
    private String factTemperature = "";
    private String factFeelsLike = "";
    private String factIcon = "";
    private String factCondition = "";
    private String factCloudness = "";
    private String factIsThunder = "";
    private String factWindSpeed = "";
    private String factWindDir = "";
    private String factPressureMM = "";
    private String factHumidity = "";
    private String factDaytime = "";
    private String factSeason = "";
    private String factSoilMoisture = "";
    private String factSoilTemp = "";
    private String factUvIndex = "";
    private String factWindGust = "";
    private String forecastWeekNo = "";
    private String forecastSunrise = "";
    private String forecastSunset = "";
    private String forecastRiseBegin = "";
    private String forecastSetEnd = "";
    private String forecastMoonCode = "";
    private String forecastMagneticFieldIndex = "";

    public YandexWeatherJsonParser(String response) {
        try {
            JsonObject jsonobj = JsonParser.parseString(response).getAsJsonObject();
            // JsonObject jsonobj = json.getAsJsonObject();
            dayTime = jsonobj.get("now_dt").getAsString();
            if (jsonobj.has("geo_object")) {
                locality = jsonobj.get("geo_object").getAsJsonObject().get("locality").getAsJsonObject().get("name")
                        .getAsString();
            }
            factTemperature = jsonobj.get("fact").getAsJsonObject().get("temp").getAsString();
            factFeelsLike = jsonobj.get("fact").getAsJsonObject().get("feels_like").getAsString();
            factIcon = "https://yastatic.net/weather/i/icons/funky/dark/"
                    + jsonobj.get("fact").getAsJsonObject().get("icon").getAsString() + ".svg";
            factCondition = jsonobj.get("fact").getAsJsonObject().get("condition").getAsString();
            if (jsonobj.get("fact").getAsJsonObject().has("cloudness")) {
                factCloudness = jsonobj.get("fact").getAsJsonObject().get("cloudness").getAsString();
            }
            if (jsonobj.get("fact").getAsJsonObject().has("is_thunder")) {
                factIsThunder = jsonobj.get("fact").getAsJsonObject().get("is_thunder").getAsString();
            }
            factWindSpeed = jsonobj.get("fact").getAsJsonObject().get("wind_speed").getAsString();
            factWindDir = jsonobj.get("fact").getAsJsonObject().get("wind_dir").getAsString();
            factPressureMM = jsonobj.get("fact").getAsJsonObject().get("pressure_mm").getAsString();
            factHumidity = jsonobj.get("fact").getAsJsonObject().get("humidity").getAsString();
            factDaytime = jsonobj.get("fact").getAsJsonObject().get("daytime").getAsString();
            factSeason = jsonobj.get("fact").getAsJsonObject().get("season").getAsString();
            if (jsonobj.get("fact").getAsJsonObject().has("soil_moisture")) {
                factSoilMoisture = jsonobj.get("fact").getAsJsonObject().get("soil_moisture").getAsString();
            }
            if (jsonobj.get("fact").getAsJsonObject().has("soil_temp")) {
                factSoilTemp = jsonobj.get("fact").getAsJsonObject().get("soil_temp").getAsString();
            }
            if (jsonobj.get("fact").getAsJsonObject().has("uv_index")) {
                factUvIndex = jsonobj.get("fact").getAsJsonObject().get("uv_index").getAsString();
            }
            factWindGust = jsonobj.get("fact").getAsJsonObject().get("wind_gust").getAsString();
            if (!jsonobj.has("forecasts")) {
                forecastWeekNo = jsonobj.get("forecast").getAsJsonObject().get("week").getAsString();
                forecastSunrise = jsonobj.get("forecast").getAsJsonObject().get("sunrise").getAsString();
                forecastSunset = jsonobj.get("forecast").getAsJsonObject().get("sunset").getAsString();
                forecastMoonCode = jsonobj.get("forecast").getAsJsonObject().get("moon_code").getAsString();
            } else {
                forecastWeekNo = jsonobj.get("forecasts").getAsJsonArray().get(0).getAsJsonObject().get("week")
                        .getAsString();
                forecastSunrise = jsonobj.get("forecasts").getAsJsonArray().get(0).getAsJsonObject().get("sunrise")
                        .getAsString();
                forecastSunset = jsonobj.get("forecasts").getAsJsonArray().get(0).getAsJsonObject().get("sunset")
                        .getAsString();
                forecastRiseBegin = jsonobj.get("forecasts").getAsJsonArray().get(0).getAsJsonObject().get("rise_begin")
                        .getAsString();
                forecastSetEnd = jsonobj.get("forecasts").getAsJsonArray().get(0).getAsJsonObject().get("set_end")
                        .getAsString();
                forecastMoonCode = jsonobj.get("forecasts").getAsJsonArray().get(0).getAsJsonObject().get("moon_code")
                        .getAsString();
                forecastMagneticFieldIndex = jsonobj.get("forecasts").getAsJsonArray().get(0).getAsJsonObject()
                        .get("biomet").getAsJsonObject().get("index").getAsString();
            }
        } catch (Exception ex) {
            logger.error("Parse error {}", ex.toString());
        }
    }

    public String getDayTime() {
        return dayTime;
    }

    public String getLocality() {
        return locality;
    }

    public String getFactTemperature() {
        return factTemperature;
    }

    public String getFactFeelsLike() {
        return factFeelsLike;
    }

    public String getFactIcon() {
        return factIcon;
    }

    public String getFactCondition() {
        return factCondition;
    }

    public String getFactCloudness() {
        return factCloudness;
    }

    public String getFactIsThunder() {
        return factIsThunder;
    }

    public String getFactWindSpeed() {
        return factWindSpeed;
    }

    public String getFactWindDir() {
        return factWindDir;
    }

    public String getFactPressureMM() {
        return factPressureMM;
    }

    public String getFactHumidity() {
        return factHumidity;
    }

    public String getFactDaytime() {
        return factDaytime;
    }

    public String getFactSeason() {
        return factSeason;
    }

    public String getFactSoilMoisture() {
        return factSoilMoisture;
    }

    public String getFactSoilTemp() {
        return factSoilTemp;
    }

    public String getFactUvIndex() {
        return factUvIndex;
    }

    public String getFactWindGust() {
        return factWindGust;
    }

    public String getForecastWeekNo() {
        return forecastWeekNo;
    }

    public String getForecastSunrise() {
        return forecastSunrise;
    }

    public String getForecastSunset() {
        return forecastSunset;
    }

    public String getForecastRiseBegin() {
        return forecastRiseBegin;
    }

    public String getForecastSetEnd() {
        return forecastSetEnd;
    }

    public String getForecastMoonCode() {
        return forecastMoonCode;
    }

    public String getForecastMagneticFieldIndex() {
        return forecastMagneticFieldIndex;
    }

    @Override
    public String toString() {
        return "OK";
    }
}
