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
import org.openhab.core.thing.ThingTypeUID;

/**
 * The {@link YandexWeatherBindingConstants} class defines common constants, which are
 * used across the whole binding.
 *
 * @author Petr Shatsillo - Initial contribution
 */
@NonNullByDefault
public class YandexWeatherBindingConstants {

    private static final String BINDING_ID = "yandexweather";

    // List of all Thing Type UIDs
    public static final ThingTypeUID THING_TYPE_API_BRIDGE = new ThingTypeUID(BINDING_ID, "bridge");
    public static final ThingTypeUID THING_YANDEXWEATHER = new ThingTypeUID(BINDING_ID, "yandexweather");

    // List of all Channel ids
    public static final String CHANNEL_DATETIME = "datetime";
    public static final String CHANNEL_LOCALITY = "locality";
    public static final String CHANNEL_FACTTEMPERATURE = "facttemperature";
    public static final String CHANNEL_FACTFEELSLIKE = "feelslike";
    public static final String CHANNEL_ICON = "icon";
    public static final String CHANNEL_ICON_URL = "iconurl";
    public static final String CHANNEL_FACTCONDITION = "factcondition";
    public static final String CHANNEL_FACTCLOUDNESS = "factcloudness";
    public static final String CHANNEL_FACTISTHUNDER = "factisthunder";
    public static final String CHANNEL_FACTWINDSPEED = "factwindspeed";
    public static final String CHANNEL_FACTWINDDIR = "factwinddir";
    public static final String CHANNEL_FACTPRESSUREMM = "factpressuremm";
    public static final String CHANNEL_FACTHUMIDITY = "facthumidity";
    public static final String CHANNEL_FACTDAYTIME = "factdaytime";
    public static final String CHANNEL_FACTSEASON = "factseason";
    public static final String CHANNEL_FACTSOILMOISTURE = "factsoilmoisture";
    public static final String CHANNEL_FACTSOILTEMP = "factsoiltemp";
    public static final String CHANNEL_FACTUVINDEX = "factuvindex";
    public static final String CHANNEL_FACTWINDGUST = "factwindgust";
    public static final String CHANNEL_FORECASTWEEKNO = "forecastweekno";
    public static final String CHANNEL_FORECASTSUNRISE = "forecastsunrise";
    public static final String CHANNEL_FORECASTSUNSET = "forecastsunset";
    public static final String CHANNEL_FORECASTRISEBEGIN = "forecastrisebegin";
    public static final String CHANNEL_FORECASTSETEND = "forecastsetend";
    public static final String CHANNEL_FORECASTMOONCODE = "forecastmooncode";
    public static final String CHANNEL_FORECASTMAGNETICFIELDINDEX = "forecastmagneticfieldindex";
}
