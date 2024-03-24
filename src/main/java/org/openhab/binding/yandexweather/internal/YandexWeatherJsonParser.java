/**
 * Copyright (c) 2010-2024 Contributors to the openHAB project
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
    final Logger logger = LoggerFactory.getLogger(YandexWeatherJsonParser.class);
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
    private String forecastNextFeelsLike = "";
    private String forecastFutureFeelsLike = "";
    private String forecastNextIcon = "";
    private String forecastFutureIcon = "";
    private String forecastNextCondition = "";
    private String forecastFutureCondition = "";
    private String forecastNextWindSpeed = "";
    private String forecastFutureWindSpeed = "";
    private String forecastNextWindDir = "";
    private String forecastFutureWindDir = "";
    private String forecastNextPressureMM = "";
    private String forecastFuturePressureMM = "";
    private String forecastNextHumidity = "";
    private String forecastFutureHumidity = "";
    private String forecastNextWindGust = "";
    private String forecastFutureWindGust = "";
    private String forecastFutureDaytime = "";
    private String forecastNextDaytime = "";
    private String partNameNext = "";
    private String partNameFuture = "";
    private String tempMinNext = "";
    private String tempMinFuture = "";
    private String tempAvgNext = "";
    private String tempAvgFuture = "";
    private String tempMaxNext = "";
    private String tempMaxFuture = "";
    private String forecastNextPrecMM = "";
    private String forecastFuturePrecMM = "";
    private String forecastNextPrecProb = "";
    private String forecastFuturePrecProb = "";
    private String forecastNextPrecPeriod = "";
    private String forecastFuturePrecPeriod = "";
    private String futureTempWater = "";
    private String forecastTempWater = "";
    private String factTempWater = "";

    public YandexWeatherJsonParser(String response) {
        try {
            JsonObject jsonobj = JsonParser.parseString(response).getAsJsonObject();
            // JsonObject jsonobj = json.getAsJsonObject();
            dayTime = jsonobj.get("now_dt").getAsString();
            if (jsonobj.has("geo_object")) {
                locality = jsonobj.get("geo_object").getAsJsonObject().get("locality").getAsJsonObject().get("name")
                        .getAsString();
            }
            if (jsonobj.has("fact")) {
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
            }
            if (!jsonobj.has("forecasts")) {
                if (jsonobj.has("forecast")) {
                    forecastWeekNo = jsonobj.get("forecast").getAsJsonObject().get("week").getAsString();
                    forecastSunrise = jsonobj.get("forecast").getAsJsonObject().get("sunrise").getAsString();
                    forecastSunset = jsonobj.get("forecast").getAsJsonObject().get("sunset").getAsString();
                    forecastMoonCode = jsonobj.get("forecast").getAsJsonObject().get("moon_code").getAsString();
                }
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
            if (jsonobj.has("forecast")) {
                partNameNext = jsonobj.get("forecast").getAsJsonObject().get("parts").getAsJsonArray().get(0)
                        .getAsJsonObject().get("part_name").getAsString();
                partNameFuture = jsonobj.get("forecast").getAsJsonObject().get("parts").getAsJsonArray().get(1)
                        .getAsJsonObject().get("part_name").getAsString();

                tempMinNext = jsonobj.get("forecast").getAsJsonObject().get("parts").getAsJsonArray().get(0)
                        .getAsJsonObject().get("temp_min").getAsString();
                tempMinFuture = jsonobj.get("forecast").getAsJsonObject().get("parts").getAsJsonArray().get(1)
                        .getAsJsonObject().get("temp_min").getAsString();

                tempAvgNext = jsonobj.get("forecast").getAsJsonObject().get("parts").getAsJsonArray().get(0)
                        .getAsJsonObject().get("temp_avg").getAsString();
                tempAvgFuture = jsonobj.get("forecast").getAsJsonObject().get("parts").getAsJsonArray().get(1)
                        .getAsJsonObject().get("temp_avg").getAsString();

                tempMaxNext = jsonobj.get("forecast").getAsJsonObject().get("parts").getAsJsonArray().get(0)
                        .getAsJsonObject().get("temp_max").getAsString();
                tempMaxFuture = jsonobj.get("forecast").getAsJsonObject().get("parts").getAsJsonArray().get(1)
                        .getAsJsonObject().get("temp_max").getAsString();

                forecastNextWindSpeed = jsonobj.get("forecast").getAsJsonObject().get("parts").getAsJsonArray().get(0)
                        .getAsJsonObject().get("wind_speed").getAsString();
                forecastFutureWindSpeed = jsonobj.get("forecast").getAsJsonObject().get("parts").getAsJsonArray().get(1)
                        .getAsJsonObject().get("wind_speed").getAsString();

                forecastNextWindGust = jsonobj.get("forecast").getAsJsonObject().get("parts").getAsJsonArray().get(0)
                        .getAsJsonObject().get("wind_gust").getAsString();
                forecastFutureWindGust = jsonobj.get("forecast").getAsJsonObject().get("parts").getAsJsonArray().get(1)
                        .getAsJsonObject().get("wind_gust").getAsString();

                forecastNextWindDir = jsonobj.get("forecast").getAsJsonObject().get("parts").getAsJsonArray().get(0)
                        .getAsJsonObject().get("wind_dir").getAsString();
                forecastFutureWindDir = jsonobj.get("forecast").getAsJsonObject().get("parts").getAsJsonArray().get(1)
                        .getAsJsonObject().get("wind_dir").getAsString();

                forecastNextPressureMM = jsonobj.get("forecast").getAsJsonObject().get("parts").getAsJsonArray().get(0)
                        .getAsJsonObject().get("pressure_mm").getAsString();
                forecastFuturePressureMM = jsonobj.get("forecast").getAsJsonObject().get("parts").getAsJsonArray()
                        .get(1).getAsJsonObject().get("pressure_mm").getAsString();

                forecastNextHumidity = jsonobj.get("forecast").getAsJsonObject().get("parts").getAsJsonArray().get(0)
                        .getAsJsonObject().get("humidity").getAsString();
                forecastFutureHumidity = jsonobj.get("forecast").getAsJsonObject().get("parts").getAsJsonArray().get(1)
                        .getAsJsonObject().get("humidity").getAsString();
                forecastNextPrecMM = jsonobj.get("forecast").getAsJsonObject().get("parts").getAsJsonArray().get(0)
                        .getAsJsonObject().get("prec_mm").getAsString();
                forecastFuturePrecMM = jsonobj.get("forecast").getAsJsonObject().get("parts").getAsJsonArray().get(1)
                        .getAsJsonObject().get("prec_mm").getAsString();

                forecastNextPrecProb = jsonobj.get("forecast").getAsJsonObject().get("parts").getAsJsonArray().get(0)
                        .getAsJsonObject().get("prec_prob").getAsString();
                forecastFuturePrecProb = jsonobj.get("forecast").getAsJsonObject().get("parts").getAsJsonArray().get(1)
                        .getAsJsonObject().get("prec_prob").getAsString();

                forecastNextPrecPeriod = jsonobj.get("forecast").getAsJsonObject().get("parts").getAsJsonArray().get(0)
                        .getAsJsonObject().get("prec_period").getAsString();
                forecastFuturePrecPeriod = jsonobj.get("forecast").getAsJsonObject().get("parts").getAsJsonArray()
                        .get(1).getAsJsonObject().get("prec_period").getAsString();

                forecastNextIcon = "https://yastatic.net/weather/i/icons/funky/dark/"
                        + jsonobj.get("forecast").getAsJsonObject().get("parts").getAsJsonArray().get(0)
                                .getAsJsonObject().get("icon").getAsString()
                        + ".svg";
                forecastFutureIcon = "https://yastatic.net/weather/i/icons/funky/dark/"
                        + jsonobj.get("forecast").getAsJsonObject().get("parts").getAsJsonArray().get(1)
                                .getAsJsonObject().get("icon").getAsString()
                        + ".svg";

                forecastNextCondition = jsonobj.get("forecast").getAsJsonObject().get("parts").getAsJsonArray().get(0)
                        .getAsJsonObject().get("condition").getAsString();
                forecastFutureCondition = jsonobj.get("forecast").getAsJsonObject().get("parts").getAsJsonArray().get(1)
                        .getAsJsonObject().get("condition").getAsString();

                forecastNextFeelsLike = jsonobj.get("forecast").getAsJsonObject().get("parts").getAsJsonArray().get(0)
                        .getAsJsonObject().get("feels_like").getAsString();
                forecastFutureFeelsLike = jsonobj.get("forecast").getAsJsonObject().get("parts").getAsJsonArray().get(1)
                        .getAsJsonObject().get("feels_like").getAsString();

                forecastNextDaytime = jsonobj.get("forecast").getAsJsonObject().get("parts").getAsJsonArray().get(0)
                        .getAsJsonObject().get("daytime").getAsString();
                forecastFutureDaytime = jsonobj.get("forecast").getAsJsonObject().get("parts").getAsJsonArray().get(1)
                        .getAsJsonObject().get("daytime").getAsString();
                if (jsonobj.get("fact").getAsJsonObject().has("temp_water")) {
                    factTempWater = jsonobj.get("fact").getAsJsonObject().get("temp_water").getAsString();
                }
                if (jsonobj.get("forecast").getAsJsonObject().get("parts").getAsJsonArray().get(0).getAsJsonObject()
                        .has("temp_water")) {
                    forecastTempWater = jsonobj.get("forecast").getAsJsonObject().get("parts").getAsJsonArray().get(0)
                            .getAsJsonObject().get("temp_water").getAsString();
                }
                if (jsonobj.get("forecast").getAsJsonObject().get("parts").getAsJsonArray().get(1).getAsJsonObject()
                        .has("temp_water")) {
                    futureTempWater = jsonobj.get("forecast").getAsJsonObject().get("parts").getAsJsonArray().get(1)
                            .getAsJsonObject().get("temp_water").getAsString();
                }
            }

        } catch (Exception ex) {
            logger.info("Parse error {}", ex.getLocalizedMessage());
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

    public String getForecastNextFeelsLike() {
        return forecastNextFeelsLike;
    }

    public String getForecastFutureFeelsLike() {
        return forecastFutureFeelsLike;
    }

    public String getForecastNextIcon() {
        return forecastNextIcon;
    }

    public String getForecastFutureIcon() {
        return forecastFutureIcon;
    }

    public String getForecastNextCondition() {
        return forecastNextCondition;
    }

    public String getForecastFutureCondition() {
        return forecastFutureCondition;
    }

    public String getForecastNextWindSpeed() {
        return forecastNextWindSpeed;
    }

    public String getForecastFutureWindSpeed() {
        return forecastFutureWindSpeed;
    }

    public String getForecastNextWindDir() {
        return forecastNextWindDir;
    }

    public String getForecastFutureWindDir() {
        return forecastFutureWindDir;
    }

    public String getForecastNextPressureMM() {
        return forecastNextPressureMM;
    }

    public String getForecastFuturePressureMM() {
        return forecastFuturePressureMM;
    }

    public String getForecastNextHumidity() {
        return forecastNextHumidity;
    }

    public String getForecastFutureHumidity() {
        return forecastFutureHumidity;
    }

    public String getForecastNextWindGust() {
        return forecastNextWindGust;
    }

    public String getForecastFutureWindGust() {
        return forecastFutureWindGust;
    }

    public String getForecastNextDaytime() {
        return forecastNextDaytime;
    }

    public String getForecastFutureDaytime() {
        return forecastFutureDaytime;
    }

    public String getPartNameNext() {
        return partNameNext;
    }

    public String getPartNameFuture() {
        return partNameFuture;
    }

    public String getTempMinNext() {
        return tempMinNext;
    }

    public String getTempMinFuture() {
        return tempMinFuture;
    }

    public String getTempAvgNext() {
        return tempAvgNext;
    }

    public String getTempAvgFuture() {
        return tempAvgFuture;
    }

    public String getTempMaxNext() {
        return tempMaxNext;
    }

    public String getTempMaxFuture() {
        return tempMaxFuture;
    }

    public String getNextPrecMM() {
        return forecastNextPrecMM;
    }

    public String getFuturePrecMM() {
        return forecastFuturePrecMM;
    }

    public String getNextPrecProb() {
        return forecastNextPrecProb;
    }

    public String getFuturePrecProb() {
        return forecastFuturePrecProb;
    }

    public String getNextPrecPeriod() {
        return forecastNextPrecPeriod;
    }

    public String getFuturePrecPeriod() {
        return forecastFuturePrecPeriod;
    }

    public String getFactTempWater() {

        return factTempWater;
    }

    public String getForecastNextTempWater() {

        return forecastTempWater;
    }

    public String getForecastFutureTempWater() {

        return futureTempWater;
    }
}
