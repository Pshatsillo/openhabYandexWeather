/**
 * Copyright (c) 2010-2022 Contributors to the openHAB project
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
package org.openhab.binding.yandexweather.discovery;

import static org.openhab.binding.yandexweather.internal.YandexWeatherBindingConstants.BINDING_ID;
import static org.openhab.binding.yandexweather.internal.YandexWeatherBindingConstants.THING_YANDEXWEATHER;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;
import org.openhab.core.config.discovery.AbstractDiscoveryService;
import org.openhab.core.config.discovery.DiscoveryResultBuilder;
import org.openhab.core.config.discovery.DiscoveryService;
import org.openhab.core.i18n.LocaleProvider;
import org.openhab.core.i18n.LocationProvider;
import org.openhab.core.i18n.TranslationProvider;
import org.openhab.core.library.types.PointType;
import org.openhab.core.thing.ThingTypeUID;
import org.openhab.core.thing.ThingUID;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The {@link YandexWeatherDiscoveryService} is responsible for discovery current location in openhab
 *
 * @author Petr Shatsillo - Initial contribution
 */
@NonNullByDefault
@Component(service = DiscoveryService.class, configurationPid = "discovery.yandexweather")
public class YandexWeatherDiscoveryService extends AbstractDiscoveryService {

    private final LocationProvider locationProvider;
    private final Logger logger = LoggerFactory.getLogger(YandexWeatherDiscoveryService.class);
    private @Nullable ScheduledFuture<?> astroDiscoveryJob;
    private @Nullable PointType previousLocation;

    @Activate
    public YandexWeatherDiscoveryService(final @Reference LocationProvider locationProvider,
            final @Reference LocaleProvider localeProvider, final @Reference TranslationProvider i18nProvider,
            @Nullable Map<String, Object> configProperties) {
        super(new HashSet<>(Arrays.asList(new ThingTypeUID(BINDING_ID, "-"))), 30, true);
        this.locationProvider = locationProvider;
        this.localeProvider = localeProvider;
        this.i18nProvider = i18nProvider;
        activate(configProperties);
    }

    @Override
    protected void startScan() {
        logger.debug("Starting Astro discovery scan");
        PointType location = locationProvider.getLocation();
        if (location == null) {
            logger.debug("LocationProvider.getLocation() is not set -> Will not provide any discovery results");
            return;
        }
        createResults(location);
    }

    @Override
    protected void startBackgroundDiscovery() {
        if (astroDiscoveryJob == null) {
            astroDiscoveryJob = scheduler.scheduleWithFixedDelay(() -> {
                PointType currentLocation = locationProvider.getLocation();
                if (currentLocation != null && !Objects.equals(currentLocation, previousLocation)) {
                    logger.debug("Location has been changed from {} to {}: Creating new discovery results",
                            previousLocation, currentLocation);
                    createResults(currentLocation);
                    previousLocation = currentLocation;
                }
            }, 0, 30, TimeUnit.SECONDS);
            logger.debug("Scheduled astro location-changed job every {} seconds", 30);
        }
    }

    @Override
    protected void stopBackgroundDiscovery() {
        logger.debug("Stopping Astro device background discovery");
        ScheduledFuture<?> discoveryJob = astroDiscoveryJob;
        if (discoveryJob != null) {
            discoveryJob.cancel(true);
        }
        astroDiscoveryJob = null;
    }

    public void createResults(PointType location) {
        String propGeolocation = location.toString();
        thingDiscovered(DiscoveryResultBuilder.create(new ThingUID(THING_YANDEXWEATHER, "localweather"))
                .withLabel("Local weather").withProperty("location", propGeolocation)
                .withRepresentationProperty("location").build());
    }
}
