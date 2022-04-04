# YandexWeather Binding
Для получения ключа для доступа к API прейдите по ссылке: https://yandex.ru/dev/weather/doc/dg/concepts/about.html
Что отдает Яндекс на бесплатном тарифе «Погода на вашем сайте»: https://yandex.ru/dev/weather/doc/dg/concepts/forecast-info.html

## Настройка через файлы - Общий пример
```
Bridge yandexweather:bridge:b "Yandex Weather Bridge" [api="xxxxx-xxxx-xxxx-xxxx-xxxxxxx", refreshInterval="50"]{
    Thing yandexweather sochi "Yandex Weather Sochi" [location="43.58081,39.71869"]
}
```
