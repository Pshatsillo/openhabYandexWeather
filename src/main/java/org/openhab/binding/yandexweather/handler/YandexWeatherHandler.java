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
    private @Nullable YandexWeatherJsonParser parser;
    @Nullable
    YandexWeatherBridgeHandler bridgeDeviceHandler;

    public YandexWeatherHandler(Thing thing) {
        super(thing);
    }

    @Override
    public void dispose() {
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
        logger.debug("Refreshing YandexWeather channels");
        this.parser = parser;
        for (Channel channel : getThing().getChannels()) {
            if (isLinked(channel.getUID().getId())) {
                if (channel.getUID().getId().equals(CHANNEL_DATETIME)) {
                    try {
                        updateState(channel.getUID().getId(), DateTimeType.valueOf(parser.getDayTime()));
                    } catch (Exception er) {
                        logger.debug("CHANNEL_DATETIME channel update error {}", er.toString());
                    }
                } else if (channel.getUID().getId().equals(CHANNEL_LOCALITY)) {
                    try {
                        updateState(channel.getUID().getId(), StringType.valueOf(parser.getLocality()));
                    } catch (Exception er) {
                        logger.debug("CHANNEL_LOCALITY channel update error {}", er.toString());
                    }
                } else if (channel.getUID().getId().equals(CHANNEL_FACTTEMPERATURE)) {
                    try {
                        updateState(channel.getUID().getId(), DecimalType.valueOf(parser.getFactTemperature()));
                    } catch (Exception er) {
                        logger.debug("CHANNEL_FACTTEMPERATURE channel update error {}", er.toString());
                    }
                } else if (channel.getUID().getId().equals(CHANNEL_FACTFEELSLIKE)) {
                    try {
                        updateState(channel.getUID().getId(), DecimalType.valueOf(parser.getFactFeelsLike()));
                    } catch (Exception er) {
                        logger.debug("CHANNEL_FACTFEELSLIKE channel update error {}", er.toString());
                    }
                } else if (channel.getUID().getId().equals(CHANNEL_ICON)) {

                    try {
                        URL url = new URL(parser.getFactIcon());
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
                } else if (channel.getUID().getId().equals(CHANNEL_ICON_URL)) {
                    try {
                        updateState(channel.getUID().getId(), StringType.valueOf(parser.getFactIcon()));
                    } catch (Exception er) {
                        logger.debug("CHANNEL_ICON_URL channel update error {}", er.toString());
                    }
                } else if (channel.getUID().getId().equals(CHANNEL_FACTCONDITION)) {
                    try {
                        updateState(channel.getUID().getId(), StringType.valueOf(parser.getFactCondition()));
                    } catch (Exception er) {
                        logger.debug("CHANNEL_FACTCONDITION channel update error {}", er.toString());
                    }
                } else if (channel.getUID().getId().equals(CHANNEL_FACTCLOUDNESS)) {
                    try {
                        updateState(channel.getUID().getId(), DecimalType.valueOf(parser.getFactCloudness()));
                    } catch (Exception er) {
                        logger.debug("CHANNEL_FACTCLOUDNESS channel update error {}", er.toString());
                    }
                } else if (channel.getUID().getId().equals(CHANNEL_FACTISTHUNDER)) {
                    try {
                        updateState(channel.getUID().getId(), StringType.valueOf(parser.getFactIsThunder()));
                    } catch (Exception er) {
                        logger.debug("CHANNEL_FACTISTHUNDER channel update error {}", er.toString());
                    }
                } else if (channel.getUID().getId().equals(CHANNEL_FACTWINDSPEED)) {
                    try {
                        updateState(channel.getUID().getId(), DecimalType.valueOf(parser.getFactWindSpeed()));
                    } catch (Exception er) {
                        logger.debug("CHANNEL_FACTWINDSPEED channel update error {}", er.toString());
                    }
                } else if (channel.getUID().getId().equals(CHANNEL_FACTWINDDIR)) {
                    try {
                        updateState(channel.getUID().getId(), StringType.valueOf(parser.getFactWindDir()));
                    } catch (Exception er) {
                        logger.debug("CHANNEL_FACTWINDDIR channel update error {}", er.toString());
                    }
                } else if (channel.getUID().getId().equals(CHANNEL_FACTPRESSUREMM)) {
                    try {
                        updateState(channel.getUID().getId(), DecimalType.valueOf(parser.getFactPressureMM()));
                    } catch (Exception er) {
                        logger.debug("CHANNEL_FACTPRESSUREMM channel update error {}", er.toString());
                    }
                } else if (channel.getUID().getId().equals(CHANNEL_FACTHUMIDITY)) {
                    try {
                        updateState(channel.getUID().getId(), DecimalType.valueOf(parser.getFactHumidity()));
                    } catch (Exception er) {
                        logger.debug("CHANNEL_FACTHUMIDITY channel update error {}", er.toString());
                    }
                } else if (channel.getUID().getId().equals(CHANNEL_FACTDAYTIME)) {
                    try {
                        updateState(channel.getUID().getId(), StringType.valueOf(parser.getFactDaytime()));
                    } catch (Exception er) {
                        logger.debug("CHANNEL_FACTDAYTIME channel update error {}", er.toString());
                    }
                } else if (channel.getUID().getId().equals(CHANNEL_FACTSEASON)) {
                    try {
                        updateState(channel.getUID().getId(), StringType.valueOf(parser.getFactSeason()));
                    } catch (Exception er) {
                        logger.debug("CHANNEL_FACTSEASON channel update error {}", er.toString());
                    }
                } else if (channel.getUID().getId().equals(CHANNEL_FACTSOILMOISTURE)) {
                    try {
                        updateState(channel.getUID().getId(), DecimalType.valueOf(parser.getFactSoilMoisture()));
                    } catch (Exception er) {
                        logger.debug("CHANNEL_FACTSOILMOISTURE channel update error {}", er.toString());
                    }
                } else if (channel.getUID().getId().equals(CHANNEL_FACTSOILTEMP)) {
                    try {
                        updateState(channel.getUID().getId(), DecimalType.valueOf(parser.getFactSoilMoisture()));
                    } catch (Exception er) {
                        logger.debug("CHANNEL_FACTSOILTEMP channel update error {}", er.toString());
                    }
                } else if (channel.getUID().getId().equals(CHANNEL_FACTUVINDEX)) {
                    try {
                        updateState(channel.getUID().getId(), DecimalType.valueOf(parser.getFactUvIndex()));
                    } catch (Exception er) {
                        logger.debug("CHANNEL_FACTUVINDEX channel update error {}", er.toString());
                    }
                } else if (channel.getUID().getId().equals(CHANNEL_FACTWINDGUST)) {
                    try {
                        updateState(channel.getUID().getId(), DecimalType.valueOf(parser.getFactWindGust()));
                    } catch (Exception er) {
                        logger.debug("CHANNEL_FACTWINDGUST channel update error {}", er.toString());
                    }
                } else if (channel.getUID().getId().equals(CHANNEL_FORECASTWEEKNO)) {
                    try {
                        updateState(channel.getUID().getId(), DecimalType.valueOf(parser.getForecastWeekNo()));
                    } catch (Exception er) {
                        logger.debug("CHANNEL_FORECASTWEEKNO channel update error {}", er.toString());
                    }
                } else if (channel.getUID().getId().equals(CHANNEL_FORECASTSUNRISE)) {
                    try {
                        updateState(channel.getUID().getId(), DateTimeType.valueOf(parser.getForecastSunrise()));
                    } catch (Exception er) {
                        logger.debug("CHANNEL_FORECASTSUNRISE channel update error {}", er.toString());
                    }
                } else if (channel.getUID().getId().equals(CHANNEL_FORECASTSUNSET)) {
                    try {
                        updateState(channel.getUID().getId(), DateTimeType.valueOf(parser.getForecastSunset()));
                    } catch (Exception er) {
                        logger.debug("CHANNEL_FORECASTSUNSET channel update error {}", er.toString());
                    }
                } else if (channel.getUID().getId().equals(CHANNEL_FORECASTRISEBEGIN)) {
                    try {
                        updateState(channel.getUID().getId(), DateTimeType.valueOf(parser.getForecastRiseBegin()));
                    } catch (Exception er) {
                        logger.debug("CHANNEL_FORECASTRISEBEGIN channel update error {}", er.toString());
                    }
                } else if (channel.getUID().getId().equals(CHANNEL_FORECASTSETEND)) {
                    try {
                        updateState(channel.getUID().getId(), DateTimeType.valueOf(parser.getForecastSetEnd()));
                    } catch (Exception er) {
                        logger.debug("CHANNEL_FORECASTSETEND channel update error {}", er.toString());
                    }
                } else if (channel.getUID().getId().equals(CHANNEL_FORECASTMOONCODE)) {
                    try {
                        updateState(channel.getUID().getId(), DecimalType.valueOf(parser.getForecastMoonCode()));
                    } catch (Exception er) {
                        logger.debug("CHANNEL_FORECASTMOONCODE channel update error {}", er.toString());
                    }
                } else if (channel.getUID().getId().equals(CHANNEL_FORECASTMAGNETICFIELDINDEX)) {
                    try {
                        updateState(channel.getUID().getId(),
                                DecimalType.valueOf(parser.getForecastMagneticFieldIndex()));
                    } catch (Exception er) {
                        logger.debug("CHANNEL_FORECASTMAGNETICFIELDINDEX channel update error {}", er.toString());
                    }
                }
            }
        }
    }
}
