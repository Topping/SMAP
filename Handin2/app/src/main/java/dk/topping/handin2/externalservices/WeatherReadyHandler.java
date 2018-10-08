package dk.topping.handin2.externalservices;

@FunctionalInterface
public interface WeatherReadyHandler<T> {
    void setWeatherResponse(T weather);
}
