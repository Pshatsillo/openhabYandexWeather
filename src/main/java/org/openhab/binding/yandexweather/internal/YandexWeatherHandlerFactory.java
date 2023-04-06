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
package org.openhab.binding.yandexweather.internal;

import static org.openhab.binding.yandexweather.internal.YandexWeatherBindingConstants.*;

import java.util.Set;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;
import org.openhab.binding.yandexweather.handler.YandexWeatherBridgeHandler;
import org.openhab.binding.yandexweather.handler.YandexWeatherHandler;
import org.openhab.core.thing.Bridge;
import org.openhab.core.thing.Thing;
import org.openhab.core.thing.ThingTypeUID;
import org.openhab.core.thing.binding.BaseThingHandlerFactory;
import org.openhab.core.thing.binding.ThingHandler;
import org.openhab.core.thing.binding.ThingHandlerFactory;
import org.osgi.service.component.annotations.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The {@link YandexWeatherHandlerFactory} is responsible for creating things and thing
 * handlers.
 *
 * @author Petr Shatsillo - Initial contribution
 */
@NonNullByDefault
@Component(configurationPid = "binding.yandexweather", service = ThingHandlerFactory.class)
public class YandexWeatherHandlerFactory extends BaseThingHandlerFactory {
    final Logger logger = LoggerFactory.getLogger(YandexWeatherHandlerFactory.class);
    static final Set<ThingTypeUID> SUPPORTED_THING_TYPES_UIDS = Set.of(THING_YANDEXWEATHER, THING_TYPE_API_BRIDGE);

    @Override
    public boolean supportsThingType(ThingTypeUID thingTypeUID) {
        return SUPPORTED_THING_TYPES_UIDS.contains(thingTypeUID);
    }

    @Override
    protected @Nullable ThingHandler createHandler(Thing thing) {
        ThingTypeUID thingTypeUID = thing.getThingTypeUID();
        if (THING_YANDEXWEATHER.equals(thingTypeUID)) {
            return new YandexWeatherHandler(thing);
        } else if (THING_TYPE_API_BRIDGE.equals(thingTypeUID)) {
            return new YandexWeatherBridgeHandler((Bridge) thing);
        }
        logger.error("createHandler for unknown thing type uid {}. Thing label was: {}", thing.getThingTypeUID(),
                thing.getLabel());
        return null;
    }
}
