# YandexWeather Binding

Для получения ключа для доступа к API прейдите по ссылке: https://yandex.ru/dev/weather/doc/dg/concepts/about.html
Что отдает Яндекс на бесплатном тарифе «Погода на вашем сайте»: https://yandex.ru/dev/weather/doc/dg/concepts/forecast-info.html

## Настройка через файлы - Общий пример

Пример файла ```yandexweather.things```

```
Bridge yandexweather:bridge:b "Yandex Weather Bridge" [api="xxxxx-xxxx-xxxx-xxxx-xxxxxxx", refreshInterval="50"]{
    Thing yandexweather sochi "Yandex Weather Sochi" [location="43.58081,39.71869"]
}
```

Пример файла ```yandexweather.items```

```
Number  yawFactTemp         "Fact Temp [%.1f°]"         <temperature>   {channel="yandexweather:yandexweather:b:sochi:current#temperature"}
Number  yawFactFeelsLike    "Fact Feels Like [%.1f°]"   <temperature>   {channel="yandexweather:yandexweather:b:sochi:current#feelslike"}
Number  yawFactHum          "Fact Hum [%.0f%%]"         <humidity>      {channel="yandexweather:yandexweather:b:sochi:current#humidity"}
Number  yawFactTempWater    "Fact Temp Water [%.1f°]"   <water>         {channel="yandexweather:yandexweather:b:sochi:current#tempwater"}
String  yawFactCondition    "Fact Condition [%s]"       <sun_clouds>    {channel="yandexweather:yandexweather:b:sochi:current#condition"}
```
