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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;
import org.openhab.binding.yandexweather.internal.YandexWeatherConfiguration;
import org.openhab.binding.yandexweather.internal.YandexWeatherJsonParser;
import org.openhab.core.thing.Bridge;
import org.openhab.core.thing.ChannelUID;
import org.openhab.core.thing.ThingStatus;
import org.openhab.core.thing.ThingStatusDetail;
import org.openhab.core.thing.binding.BaseBridgeHandler;
import org.openhab.core.thing.binding.ThingHandlerService;
import org.openhab.core.types.Command;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The {@link YandexWeatherBridgeHandler} is responsible for Bridge to Yandex API
 *
 * @author Petr Shatsillo - Initial contribution
 */
@NonNullByDefault
public class YandexWeatherBridgeHandler extends BaseBridgeHandler {
    Logger logger = LoggerFactory.getLogger(YandexWeatherBridgeHandler.class);
    private @Nullable ScheduledFuture<?> refreshPollingJob;
    private @Nullable YandexWeatherConfiguration bridgeConfig;
    private @Nullable Map<String, YandexWeatherHandler> locationsHandlerMap = new HashMap<>();
    protected long lastRefresh = 0;

    public YandexWeatherBridgeHandler(Bridge bridge) {
        super(bridge);
    }

    @Override
    public void initialize() {
        bridgeConfig = getConfigAs(YandexWeatherConfiguration.class);
        updateStatus(ThingStatus.UNKNOWN);
        ScheduledFuture<?> refreshPollingJob = this.refreshPollingJob;
        if (refreshPollingJob == null || refreshPollingJob.isCancelled()) {
            refreshPollingJob = scheduler.scheduleWithFixedDelay(new Runnable() {
                @Override
                public void run() {
                    refresh();
                }
            }, 0, 1000, TimeUnit.MILLISECONDS);
        }
        this.refreshPollingJob = refreshPollingJob;
        updateStatus(ThingStatus.ONLINE);
    }

    private void refresh() {
        YandexWeatherConfiguration bridgeConfig = this.bridgeConfig;
        long now = System.currentTimeMillis();
        if (bridgeConfig != null) {
            final Map<String, YandexWeatherHandler> locationsHandlerMap = this.locationsHandlerMap;
            if (locationsHandlerMap != null && !(locationsHandlerMap.isEmpty())) {
                if (bridgeConfig.refreshInterval != 0) {
                    int refreshPerHour = 86400000 / (bridgeConfig.refreshInterval / locationsHandlerMap.size());
                    if (now >= (lastRefresh + refreshPerHour)) {
                        locationsHandlerMap.forEach((k, v) -> {
                            String[] location = v.getThing().getConfiguration().get("location").toString().split(",");
                            if (location.length == 2) {
                                String lat = location[0];
                                String lon = location[1];
                                // String URL = "http://localhost/sec/?lat=" + lat + "&lon=" + lon;
                                String URL = "https://api.weather.yandex.ru/v2/informers?lat=" + lat + "&lon=" + lon;
                                // String URL = "https://api.weather.yandex.ru/v2/informers?lat=55.75396&lon=37.620393";
                                try {
                                    java.net.URL urlreq = new URL(URL);
                                    HttpURLConnection con;
                                    con = (HttpURLConnection) urlreq.openConnection();
                                    // logger.info("URL: {}", URL);
                                    con.setRequestMethod("GET");
                                    con.setReadTimeout(1500);
                                    con.setConnectTimeout(1500);
                                    con.setRequestProperty("Accept", "application/json");
                                    con.setRequestProperty("Content-Type", "application/json");
                                    con.setRequestProperty("X-Yandex-API-Key", bridgeConfig.api);
                                    BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
                                    String inputLine;
                                    StringBuilder response = new StringBuilder();
                                    while ((inputLine = in.readLine()) != null) {
                                        response.append(inputLine);
                                    }
                                    in.close();
                                    YandexWeatherJsonParser parser = new YandexWeatherJsonParser(
                                            response.toString().trim());
                                    v.updateValues(parser);
                                    con.disconnect();
                                } catch (IOException e) {
                                    logger.debug("Connect to Yandex API {} error: {}", URL, e.getLocalizedMessage());
                                }
                            }
                            lastRefresh = now;
                        });
                    }
                }

            }
        }
    }

    @Override
    public void handleCommand(ChannelUID channelUID, Command command) {
    }

    @Override
    public Collection<Class<? extends ThingHandlerService>> getServices() {
        return super.getServices();
    }

    @Override
    public void dispose() {
        ScheduledFuture<?> refreshPollingJob = this.refreshPollingJob;
        if (refreshPollingJob != null && !refreshPollingJob.isCancelled()) {
            refreshPollingJob.cancel(true);
            refreshPollingJob = null;
        }
        this.refreshPollingJob = refreshPollingJob;
        super.dispose();
    }

    public void registerYandexWeatherListener(YandexWeatherHandler yandexWeatherHandler) {
        String location = yandexWeatherHandler.getThing().getConfiguration().get("location").toString();
        Map<String, YandexWeatherHandler> locationsHandlerMap = this.locationsHandlerMap;
        if (locationsHandlerMap != null) {
            if (locationsHandlerMap.get(location) != null) {
                updateThingHandlerStatus(yandexWeatherHandler, ThingStatus.OFFLINE,
                        ThingStatusDetail.CONFIGURATION_ERROR, "Location");
            } else {
                locationsHandlerMap.put(location, yandexWeatherHandler);
                updateThingHandlerStatus(yandexWeatherHandler, ThingStatus.ONLINE);
            }
            this.locationsHandlerMap = locationsHandlerMap;
        }
    }

    public void unregisterYandexWeatherListener(YandexWeatherHandler yandexWeatherHandler) {
        String location = yandexWeatherHandler.getThing().getConfiguration().get("location").toString();
        Map<String, YandexWeatherHandler> locationsHandlerMap = this.locationsHandlerMap;
        if (locationsHandlerMap != null) {
            if (locationsHandlerMap.get(location) != null) {
                locationsHandlerMap.remove(location);
                updateThingHandlerStatus(yandexWeatherHandler, ThingStatus.OFFLINE);
            }
            this.locationsHandlerMap = locationsHandlerMap;
        }
    }

    private void updateThingHandlerStatus(YandexWeatherHandler yandexWeatherHandler, ThingStatus status,
            ThingStatusDetail statusDetail, String decript) {
        yandexWeatherHandler.updateStatus(status, statusDetail, decript);
    }

    private void updateThingHandlerStatus(YandexWeatherHandler thingHandler, ThingStatus status) {
        thingHandler.updateStatus(status);
    }
}
