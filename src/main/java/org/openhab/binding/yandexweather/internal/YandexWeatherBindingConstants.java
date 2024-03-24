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
import org.openhab.core.thing.ThingTypeUID;

/**
 * The {@link YandexWeatherBindingConstants} class defines common constants, which are
 * used across the whole binding.
 *
 * @author Petr Shatsillo - Initial contribution
 */
@NonNullByDefault
public class YandexWeatherBindingConstants {

    public static final String BINDING_ID = "yandexweather";

    // List of all Thing Type UIDs
    public static final ThingTypeUID THING_TYPE_API_BRIDGE = new ThingTypeUID(BINDING_ID, "bridge");
    public static final ThingTypeUID THING_YANDEXWEATHER = new ThingTypeUID(BINDING_ID, "yandexweather");

    // List of all Channel ids
    public static final String CHANNEL_DATETIME = "datetime";
    public static final String CHANNEL_TEMPERATURE = "temperature";
    public static final String CHANNEL_FEELSLIKE = "feelslike";
    public static final String CHANNEL_ICON = "icon";
    public static final String CHANNEL_ICON_URL = "iconurl";
    public static final String CHANNEL_CONDITION = "condition";
    public static final String CHANNEL_WINDSPEED = "windspeed";
    public static final String CHANNEL_WINDDIR = "winddir";
    public static final String CHANNEL_PRESSUREMM = "pressuremm";
    public static final String CHANNEL_HUMIDITY = "humidity";
    public static final String CHANNEL_DAYTIME = "daytime";
    public static final String CHANNEL_SEASON = "season";
    public static final String CHANNEL_WINDGUST = "windgust";
    public static final String CHANNEL_WEEKNO = "weekno";
    public static final String CHANNEL_SUNRISE = "sunrise";
    public static final String CHANNEL_SUNSET = "sunset";
    public static final String CHANNEL_MOONCODE = "mooncode";
    public static final String CHANNEL_TEMPWATER = "tempwater";

    // forecast
    public static final String CHANNEL_PARTNAME = "partname";
    public static final String CHANNEL_TEMPMIN = "tempmin";
    public static final String CHANNEL_TEMPMAX = "tempmax";
    public static final String CHANNEL_TEMPAVG = "tempavg";
    public static final String CHANNEL_PRECMM = "precmm";
    public static final String CHANNEL_PRECPERIOD = "precperiod";
    public static final String CHANNEL_PRECPROB = "precprob";
}
