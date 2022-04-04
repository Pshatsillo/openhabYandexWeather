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
package org.openhab.binding.yandexweather.handler;

import static org.openhab.binding.yandexweather.internal.YandexWeatherBindingConstants.*;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.StandardCharsets;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;
import org.openhab.binding.yandexweather.internal.YandexWeatherConfiguration;
import org.openhab.binding.yandexweather.internal.YandexWeatherJsonParser;
import org.openhab.core.library.types.DateTimeType;
import org.openhab.core.library.types.DecimalType;
import org.openhab.core.library.types.RawType;
import org.openhab.core.library.types.StringType;
import org.openhab.core.thing.Bridge;
import org.openhab.core.thing.Channel;
import org.openhab.core.thing.ChannelUID;
import org.openhab.core.thing.Thing;
import org.openhab.core.thing.ThingStatus;
import org.openhab.core.thing.ThingStatusDetail;
import org.openhab.core.thing.binding.BaseThingHandler;
import org.openhab.core.thing.binding.ThingHandler;
import org.openhab.core.types.Command;
import org.openhab.core.types.UnDefType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The {@link YandexWeatherHandler} is responsible for handling commands, which are
 * sent to one of the channels.
 *
 * @author Petr Shatsillo - Initial contribution
 */
@NonNullByDefault
public class YandexWeatherHandler extends BaseThingHandler {
    Logger logger = LoggerFactory.getLogger(YandexWeatherHandler.class);
    private @Nullable YandexWeatherConfiguration config;
    @Nullable
    YandexWeatherBridgeHandler bridgeDeviceHandler;

    public YandexWeatherHandler(Thing thing) {
        super(thing);
    }

    @Override
    public void dispose() {
        YandexWeatherBridgeHandler bridgeDeviceHandler = this.bridgeDeviceHandler;
        if (bridgeDeviceHandler != null) {
            bridgeDeviceHandler.unregisterYandexWeatherListener(this);
        }
        super.dispose();
    }

    @Override
    public void handleCommand(ChannelUID channelUID, Command command) {
    }

    @Override
    public void initialize() {
        config = getConfigAs(YandexWeatherConfiguration.class);
        YandexWeatherConfiguration config = this.config;
        if (config != null) {
            if (!config.location.equals("")) {
                bridgeDeviceHandler = getBridgeHandler();
                if (bridgeDeviceHandler != null) {
                    registerYandexWeatherListener(bridgeDeviceHandler);
                } else {
                    logger.debug("Can't register {} at bridge. BridgeHandler is null.", this.getThing().getUID());
                }
                updateStatus(ThingStatus.ONLINE);
            } else {
                updateStatus(ThingStatus.UNINITIALIZED, ThingStatusDetail.HANDLER_CONFIGURATION_PENDING,
                        "Please select location");
            }
        } else {
            logger.debug("Can't read config file");
        }
    }

    private synchronized @Nullable YandexWeatherBridgeHandler getBridgeHandler() {
        Bridge bridge = getBridge();
        if (bridge == null) {
            logger.error("Required bridge not defined for device.");
            return null;
        } else {
            return getBridgeHandler(bridge);
        }
    }

    private synchronized @Nullable YandexWeatherBridgeHandler getBridgeHandler(Bridge bridge) {
        ThingHandler handler = bridge.getHandler();
        if (handler instanceof YandexWeatherBridgeHandler) {
            return (YandexWeatherBridgeHandler) handler;
        } else {
            logger.debug("No available bridge handler found yet. Bridge: {} .", bridge.getUID());
            return null;
        }
    }

    private void registerYandexWeatherListener(@Nullable YandexWeatherBridgeHandler bridgeHandler) {
        if (bridgeHandler != null) {
            bridgeHandler.registerYandexWeatherListener(this);
        }
    }

    @Override
    protected void updateStatus(ThingStatus status, ThingStatusDetail statusDetail, @Nullable String description) {
        super.updateStatus(status, statusDetail, description);
    }

    @Override
    public void updateStatus(ThingStatus status) {
        super.updateStatus(status);
    }

    public void updateValues(YandexWeatherJsonParser parser) {
        logger.debug("Refreshing YandexWeather channels, thing {}", getThing().getLabel());
        for (Channel channel : getThing().getChannels()) {
            if (isLinked(channel.getUID().getId())) {
                if (channel.getUID().getIdWithoutGroup().equals(CHANNEL_DATETIME)) {
                    try {
                        updateState(channel.getUID().getId(), DateTimeType.valueOf(parser.getDayTime()));
                    } catch (Exception er) {
                        logger.debug("CHANNEL_DATETIME channel update error {}", er.toString());
                    }
                } else if (channel.getUID().getIdWithoutGroup().equals(CHANNEL_TEMPERATURE)) {
                    try {
                        updateState(channel.getUID().getId(), DecimalType.valueOf(parser.getFactTemperature()));
                    } catch (Exception er) {
                        logger.debug("CHANNEL_FACTTEMPERATURE channel update error {}", er.toString());
                    }
                } else if (channel.getUID().getIdWithoutGroup().equals(CHANNEL_FEELSLIKE)) {
                    try {
                        DecimalType feelslike = new DecimalType();
                        String group = channel.getUID().getGroupId();
                        if (group != null) {
                            switch (group) {
                                case "current":
                                    feelslike = DecimalType.valueOf(parser.getFactFeelsLike());
                                    break;
                                case "forecastNext":
                                    feelslike = DecimalType.valueOf(parser.getForecastNextFeelsLike());
                                    break;
                                case "forecastFuture":
                                    feelslike = DecimalType.valueOf(parser.getForecastFutureFeelsLike());
                                    break;
                            }
                            updateState(channel.getUID().getId(), feelslike);
                        }
                    } catch (Exception er) {
                        logger.debug("CHANNEL_FACTFEELSLIKE channel update error {}", er.toString());
                    }
                } else if (channel.getUID().getIdWithoutGroup().equals(CHANNEL_TEMPWATER)) {
                    try {
                        String group = channel.getUID().getGroupId();
                        if ((parser.getFactTempWater().equals("")) || (parser.getForecastNextTempWater().equals(""))
                                || (parser.getForecastFutureTempWater().equals(""))) {
                            updateState(channel.getUID().getId(), UnDefType.NULL);
                        } else {
                            var tempwater = new DecimalType();
                            if (group != null) {
                                switch (group) {
                                    case "current":
                                        tempwater = DecimalType.valueOf(parser.getFactTempWater());
                                        break;
                                    case "forecastNext":
                                        tempwater = DecimalType.valueOf(parser.getForecastNextTempWater());
                                        break;
                                    case "forecastFuture":
                                        tempwater = DecimalType.valueOf(parser.getForecastFutureTempWater());
                                        break;
                                }
                            }
                            updateState(channel.getUID().getId(), tempwater);
                        }

                    } catch (Exception er) {
                        logger.debug("CHANNEL_TEMPWATER channel update error {}", er.toString());
                    }
                } else if (channel.getUID().getIdWithoutGroup().equals(CHANNEL_ICON)) {
                    try {
                        String icon = "";
                        String group = channel.getUID().getGroupId();
                        if (group != null) {
                            switch (group) {
                                case "current":
                                    icon = parser.getFactIcon();
                                    break;
                                case "forecastNext":
                                    icon = parser.getForecastNextIcon();
                                    break;
                                case "forecastFuture":
                                    icon = parser.getForecastFutureIcon();
                                    break;
                            }
                        }
                        URL url = new URL(icon);
                        BufferedReader read = new BufferedReader(new InputStreamReader(url.openStream()));

                        StringBuilder stringBuilder = new StringBuilder();

                        String inputLine;
                        while ((inputLine = read.readLine()) != null) {
                            stringBuilder.append(inputLine);
                            stringBuilder.append(System.lineSeparator());
                        }
                        read.close();
                        RawType rt = new RawType(stringBuilder.toString().trim().getBytes(StandardCharsets.UTF_8),
                                "image/svg+xml");
                        updateState(channel.getUID().getId(), rt);

                    } catch (Exception er) {
                        logger.debug("CHANNEL_ICON channel update error {}", er.toString());
                    }
                } else if (channel.getUID().getIdWithoutGroup().equals(CHANNEL_ICON_URL)) {
                    try {
                        String icon = "";
                        String group = channel.getUID().getGroupId();
                        if (group != null) {
                            switch (group) {
                                case "current":
                                    icon = parser.getFactIcon();
                                    break;
                                case "forecastNext":
                                    icon = parser.getForecastNextIcon();
                                    break;
                                case "forecastFuture":
                                    icon = parser.getForecastFutureIcon();
                                    break;
                            }
                        }
                        updateState(channel.getUID().getId(), StringType.valueOf(icon));
                    } catch (Exception er) {
                        logger.debug("CHANNEL_ICON_URL channel update error {}", er.toString());
                    }
                } else if (channel.getUID().getIdWithoutGroup().equals(CHANNEL_CONDITION)) {
                    try {
                        StringType condition = new StringType();
                        String group = channel.getUID().getGroupId();
                        if (group != null) {
                            switch (group) {
                                case "current":
                                    condition = StringType.valueOf(parser.getFactCondition());
                                    break;
                                case "forecastNext":
                                    condition = StringType.valueOf(parser.getForecastNextCondition());
                                    break;
                                case "forecastFuture":
                                    condition = StringType.valueOf(parser.getForecastFutureCondition());
                                    break;
                            }
                        }
                        updateState(channel.getUID().getId(), condition);
                    } catch (Exception er) {
                        logger.debug("CHANNEL_FACTCONDITION channel update error {}", er.toString());
                    }
                } else if (channel.getUID().getIdWithoutGroup().equals(CHANNEL_WINDSPEED)) {
                    try {
                        DecimalType windSpeed = new DecimalType();
                        String group = channel.getUID().getGroupId();
                        if (group != null) {
                            switch (group) {
                                case "current":
                                    windSpeed = DecimalType.valueOf(parser.getFactWindSpeed());
                                    break;
                                case "forecastNext":
                                    windSpeed = DecimalType.valueOf(parser.getForecastNextWindSpeed());
                                    break;
                                case "forecastFuture":
                                    windSpeed = DecimalType.valueOf(parser.getForecastFutureWindSpeed());
                                    break;
                            }
                        }
                        updateState(channel.getUID().getId(), windSpeed);
                    } catch (Exception er) {
                        logger.debug("CHANNEL_FACTWINDSPEED channel update error {}", er.toString());
                    }
                } else if (channel.getUID().getIdWithoutGroup().equals(CHANNEL_WINDDIR)) {
                    try {
                        StringType windDir = new StringType();
                        String group = channel.getUID().getGroupId();
                        if (group != null) {
                            switch (group) {
                                case "current":
                                    windDir = StringType.valueOf(parser.getFactWindDir());
                                    break;
                                case "forecastNext":
                                    windDir = StringType.valueOf(parser.getForecastNextWindDir());
                                    break;
                                case "forecastFuture":
                                    windDir = StringType.valueOf(parser.getForecastFutureWindDir());
                                    break;
                            }
                        }
                        updateState(channel.getUID().getId(), windDir);
                    } catch (Exception er) {
                        logger.debug("CHANNEL_FACTWINDDIR channel update error {}", er.toString());
                    }
                } else if (channel.getUID().getIdWithoutGroup().equals(CHANNEL_PRESSUREMM)) {
                    try {
                        DecimalType pressure = new DecimalType();
                        String group = channel.getUID().getGroupId();
                        if (group != null) {
                            switch (group) {
                                case "current":
                                    pressure = DecimalType.valueOf(parser.getFactPressureMM());
                                    break;
                                case "forecastNext":
                                    pressure = DecimalType.valueOf(parser.getForecastNextPressureMM());
                                    break;
                                case "forecastFuture":
                                    pressure = DecimalType.valueOf(parser.getForecastFuturePressureMM());
                                    break;
                            }
                        }
                        updateState(channel.getUID().getId(), pressure);
                    } catch (Exception er) {
                        logger.debug("CHANNEL_FACTPRESSUREMM channel update error {}", er.toString());
                    }
                } else if (channel.getUID().getIdWithoutGroup().equals(CHANNEL_HUMIDITY)) {
                    try {
                        DecimalType humidity = new DecimalType();
                        String group = channel.getUID().getGroupId();
                        if (group != null) {
                            switch (group) {
                                case "current":
                                    humidity = DecimalType.valueOf(parser.getFactHumidity());
                                    break;
                                case "forecastNext":
                                    humidity = DecimalType.valueOf(parser.getForecastNextHumidity());
                                    break;
                                case "forecastFuture":
                                    humidity = DecimalType.valueOf(parser.getForecastFutureHumidity());
                                    break;
                            }
                        }
                        updateState(channel.getUID().getId(), humidity);
                    } catch (Exception er) {
                        logger.debug("CHANNEL_FACTHUMIDITY channel update error {}", er.toString());
                    }
                } else if (channel.getUID().getIdWithoutGroup().equals(CHANNEL_DAYTIME)) {
                    try {
                        StringType daytime = new StringType();
                        String group = channel.getUID().getGroupId();
                        if (group != null) {
                            switch (group) {
                                case "current":
                                    daytime = StringType.valueOf(parser.getFactDaytime());
                                    break;
                                case "forecastNext":
                                    daytime = StringType.valueOf(parser.getForecastNextDaytime());
                                    break;
                                case "forecastFuture":
                                    daytime = StringType.valueOf(parser.getForecastFutureDaytime());
                                    break;
                            }
                        }
                        updateState(channel.getUID().getId(), daytime);
                    } catch (Exception er) {
                        logger.debug("CHANNEL_FACTDAYTIME channel update error {}", er.toString());
                    }
                } else if (channel.getUID().getIdWithoutGroup().equals(CHANNEL_SEASON)) {
                    try {
                        updateState(channel.getUID().getId(), StringType.valueOf(parser.getFactSeason()));
                    } catch (Exception er) {
                        logger.debug("CHANNEL_FACTSEASON channel update error {}", er.toString());
                    }
                } else if (channel.getUID().getIdWithoutGroup().equals(CHANNEL_WINDGUST)) {
                    try {
                        DecimalType windGust = new DecimalType();
                        String group = channel.getUID().getGroupId();
                        if (group != null) {
                            switch (group) {
                                case "current":
                                    windGust = DecimalType.valueOf(parser.getFactWindGust());
                                    break;
                                case "forecastNext":
                                    windGust = DecimalType.valueOf(parser.getForecastNextWindGust());
                                    break;
                                case "forecastFuture":
                                    windGust = DecimalType.valueOf(parser.getForecastFutureWindGust());
                                    break;
                            }
                        }
                        updateState(channel.getUID().getId(), windGust);
                    } catch (Exception er) {
                        logger.debug("CHANNEL_FACTWINDGUST channel update error {}", er.toString());
                    }
                } else if (channel.getUID().getIdWithoutGroup().equals(CHANNEL_WEEKNO)) {
                    try {
                        updateState(channel.getUID().getId(), DecimalType.valueOf(parser.getForecastWeekNo()));
                    } catch (Exception er) {
                        logger.debug("CHANNEL_FORECASTWEEKNO channel update error {}", er.toString());
                    }
                } else if (channel.getUID().getIdWithoutGroup().equals(CHANNEL_SUNRISE)) {
                    try {
                        updateState(channel.getUID().getId(), DateTimeType.valueOf(parser.getForecastSunrise()));
                    } catch (Exception er) {
                        logger.debug("CHANNEL_FORECASTSUNRISE channel update error {}", er.toString());
                    }
                } else if (channel.getUID().getIdWithoutGroup().equals(CHANNEL_SUNSET)) {
                    try {
                        updateState(channel.getUID().getId(), DateTimeType.valueOf(parser.getForecastSunset()));
                    } catch (Exception er) {
                        logger.debug("CHANNEL_FORECASTSUNSET channel update error {}", er.toString());
                    }
                } else if (channel.getUID().getIdWithoutGroup().equals(CHANNEL_MOONCODE)) {
                    try {
                        updateState(channel.getUID().getId(), DecimalType.valueOf(parser.getForecastMoonCode()));
                    } catch (Exception er) {
                        logger.debug("CHANNEL_FORECASTMOONCODE channel update error {}", er.toString());
                    }
                } else if (channel.getUID().getIdWithoutGroup().equals(CHANNEL_PARTNAME)) {
                    StringType partname = new StringType();
                    String group = channel.getUID().getGroupId();
                    if (group != null) {
                        switch (group) {
                            case "forecastNext":
                                partname = StringType.valueOf(parser.getPartNameNext());
                                break;
                            case "forecastFuture":
                                partname = StringType.valueOf(parser.getPartNameFuture());
                                break;
                        }
                    }
                    try {
                        updateState(channel.getUID().getId(), partname);
                    } catch (Exception er) {
                        logger.debug("CHANNEL_PARTNAME channel update error {}", er.toString());
                    }
                } else if (channel.getUID().getIdWithoutGroup().equals(CHANNEL_TEMPMIN)) {
                    DecimalType state = new DecimalType();
                    String group = channel.getUID().getGroupId();
                    if (group != null) {
                        switch (group) {
                            case "forecastNext":
                                state = DecimalType.valueOf(parser.getTempMinNext());
                                break;
                            case "forecastFuture":
                                state = DecimalType.valueOf(parser.getTempMinFuture());
                                break;
                        }
                    }
                    try {
                        updateState(channel.getUID().getId(), state);
                    } catch (Exception er) {
                        logger.debug("CHANNEL_TEMPMIN channel update error {}", er.toString());
                    }
                } else if (channel.getUID().getIdWithoutGroup().equals(CHANNEL_TEMPMAX)) {
                    DecimalType state = new DecimalType();
                    String group = channel.getUID().getGroupId();
                    if (group != null) {
                        switch (group) {
                            case "forecastNext":
                                state = DecimalType.valueOf(parser.getTempMaxNext());
                                break;
                            case "forecastFuture":
                                state = DecimalType.valueOf(parser.getTempMaxFuture());
                                break;
                        }
                    }
                    try {
                        updateState(channel.getUID().getId(), state);
                    } catch (Exception er) {
                        logger.debug("CHANNEL_TEMPMAX channel update error {}", er.toString());
                    }
                } else if (channel.getUID().getIdWithoutGroup().equals(CHANNEL_TEMPAVG)) {
                    DecimalType state = new DecimalType();
                    String group = channel.getUID().getGroupId();
                    if (group != null) {
                        switch (group) {
                            case "forecastNext":
                                state = DecimalType.valueOf(parser.getTempAvgNext());
                                break;
                            case "forecastFuture":
                                state = DecimalType.valueOf(parser.getTempAvgFuture());
                                break;
                        }
                    }
                    try {
                        updateState(channel.getUID().getId(), state);
                    } catch (Exception er) {
                        logger.debug("CHANNEL_TEMPAVG channel update error {}", er.toString());
                    }
                } else if (channel.getUID().getIdWithoutGroup().equals(CHANNEL_PRECMM)) {
                    DecimalType state = new DecimalType();
                    String group = channel.getUID().getGroupId();
                    if (group != null) {
                        switch (group) {
                            case "forecastNext":
                                state = DecimalType.valueOf(parser.getNextPrecMM());
                                break;
                            case "forecastFuture":
                                state = DecimalType.valueOf(parser.getFuturePrecMM());
                                break;
                        }
                    }
                    try {
                        updateState(channel.getUID().getId(), state);
                    } catch (Exception er) {
                        logger.debug("CHANNEL_PRECMM channel update error {}", er.toString());
                    }
                } else if (channel.getUID().getIdWithoutGroup().equals(CHANNEL_PRECPERIOD)) {
                    DecimalType state = new DecimalType();
                    String group = channel.getUID().getGroupId();
                    if (group != null) {
                        switch (group) {
                            case "forecastNext":
                                state = DecimalType.valueOf(parser.getNextPrecPeriod());
                                break;
                            case "forecastFuture":
                                state = DecimalType.valueOf(parser.getFuturePrecPeriod());
                                break;
                        }
                    }
                    try {
                        updateState(channel.getUID().getId(), state);
                    } catch (Exception er) {
                        logger.debug("CHANNEL_PRECPERIOD channel update error {}", er.toString());
                    }
                } else if (channel.getUID().getIdWithoutGroup().equals(CHANNEL_PRECPROB)) {
                    DecimalType state = new DecimalType();
                    String group = channel.getUID().getGroupId();
                    if (group != null) {
                        switch (group) {
                            case "forecastNext":
                                state = DecimalType.valueOf(parser.getNextPrecProb());
                                break;
                            case "forecastFuture":
                                state = DecimalType.valueOf(parser.getFuturePrecProb());
                                break;
                        }
                    }
                    try {
                        updateState(channel.getUID().getId(), state);
                    } catch (Exception er) {
                        logger.debug("CHANNEL_PRECPROB channel update error {}", er.toString());
                    }
                }
            }
        }
    }
}
