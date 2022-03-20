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
package handler;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Collection;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;
import org.openhab.binding.yandexweather.internal.YandexWeatherConfiguration;
import org.openhab.binding.yandexweather.internal.YandexWeatherJsonParser;
import org.openhab.core.thing.Bridge;
import org.openhab.core.thing.ChannelUID;
import org.openhab.core.thing.ThingStatus;
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
    private final Logger logger = LoggerFactory.getLogger(YandexWeatherHandler.class);
    private @Nullable ScheduledFuture<?> refreshPollingJob;
    private @Nullable YandexWeatherConfiguration bridgeConfig;
    private @Nullable YandexWeatherJsonParser parser;
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
        try {
            ClassLoader loader = getClass().getClassLoader();
            @Nullable
            URL resource = loader.getResource("api.json");
            if (resource != null) {
                parser = new YandexWeatherJsonParser(new File(resource.toURI()).toString());
                logger.debug(parser.toString());
            }
        } catch (URISyntaxException e) {
        }
        scheduler.execute(() -> {
            boolean thingReachable = true; // <background task with long running initialization here>
            // when done do:
            if (thingReachable) {
                updateStatus(ThingStatus.ONLINE);
            } else {
                updateStatus(ThingStatus.OFFLINE);
            }
        });
        // logger.debug("Example debug message");
    }

    private void refresh() {
        YandexWeatherConfiguration bridgeConfig = this.bridgeConfig;
        long now = System.currentTimeMillis();
        if (bridgeConfig != null) {
            if (bridgeConfig.refreshInterval != 0) {
                if (now >= (lastRefresh + bridgeConfig.refreshInterval)) {
                    String URL = "http://localhost/sec/?" + bridgeConfig.location;
                    try {
                        java.net.URL urlreq = new URL(URL);
                        HttpURLConnection con;
                        con = (HttpURLConnection) urlreq.openConnection();
                        logger.debug("URL: {}", URL);
                        con.setRequestMethod("GET");
                        con.setReadTimeout(1500);
                        con.setConnectTimeout(1500);
                        con.setRequestProperty("User-Agent", "Mozilla/5.0");
                        con.setRequestProperty("X-Yandex-API-Key", bridgeConfig.api);
                        BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
                        String inputLine;
                        StringBuilder response = new StringBuilder();
                        while ((inputLine = in.readLine()) != null) {
                            response.append(inputLine);
                        }
                        in.close();
                        logger.debug("input string from {} -> {}", URL, response.toString());
                        // parser = new YandexWeatherJsonParser(response.toString().trim());
                        con.disconnect();
                    } catch (IOException e) {
                        logger.error("Connect to Yandex API {} error: {}", URL, e.getLocalizedMessage());
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
}
