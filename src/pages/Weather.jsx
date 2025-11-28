import React, { useEffect, useState } from "react";
import axios from "axios";

export default function Weather() {
  const [weatherData, setWeatherData] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");

  useEffect(() => {
    const fetchWeather = async () => {
      const token = localStorage.getItem("token");
      if (!token) {
        setError("Not authenticated. Please log in again.");
        setLoading(false);
        return;
      }

      try {
        const response = await axios.get("http://localhost:8080/api/weather", {
          headers: { Authorization: `Bearer ${token}` },
        });
        setWeatherData(response.data);
        setError("");
      } catch (err) {
        console.error("Error fetching weather:", err);
        let errorMessage = "Failed to load weather data. Please try again.";
        if (err.response?.data?.error) {
          errorMessage = err.response.data.error;
        } else if (err.message) {
          errorMessage = err.message;
        }
        setError(errorMessage);
      } finally {
        setLoading(false);
      }
    };

    fetchWeather();
    // Refresh weather every 30 minutes
    const interval = setInterval(fetchWeather, 1800000);
    return () => clearInterval(interval);
  }, []);

  const formatDate = (timestamp) => {
    return new Date(timestamp * 1000).toLocaleDateString("en-US", {
      weekday: "short",
      month: "short",
      day: "numeric",
    });
  };

  const formatTime = (timestamp) => {
    return new Date(timestamp * 1000).toLocaleTimeString("en-US", {
      hour: "2-digit",
      minute: "2-digit",
    });
  };

  const formatDateTime = (timestamp) => {
    return new Date(timestamp * 1000).toLocaleString("en-US", {
      weekday: "short",
      month: "short",
      day: "numeric",
      hour: "2-digit",
      minute: "2-digit",
    });
  };

  const getWindDirection = (degrees) => {
    if (!degrees) return "N/A";
    const directions = ["N", "NE", "E", "SE", "S", "SW", "W", "NW"];
    return directions[Math.round(degrees / 45) % 8];
  };

  const groupForecastByDay = (forecastList) => {
    const grouped = {};
    forecastList.forEach((item) => {
      const date = new Date(item.dateTime * 1000).toDateString();
      if (!grouped[date]) {
        grouped[date] = [];
      }
      grouped[date].push(item);
    });
    return grouped;
  };

  if (loading) {
    return (
      <div className="bg-content rounded-lg p-6 shadow-lg">
        <h1 className="text-2xl font-bold text-yellow-700 mb-5">Weather</h1>
        <div className="text-center py-8">
          <div className="text-lg text-yellow-700">Loading weather data...</div>
        </div>
      </div>
    );
  }

  if (error) {
    return (
      <div className="bg-content rounded-lg p-6 shadow-lg">
        <h1 className="text-2xl font-bold text-yellow-700 mb-5">Weather</h1>
        <div className="bg-red-100 border border-red-400 text-red-700 px-4 py-3 rounded">
          {error}
        </div>
      </div>
    );
  }

  if (!weatherData || !weatherData.current) {
    return (
      <div className="bg-content rounded-lg p-6 shadow-lg">
        <h1 className="text-2xl font-bold text-yellow-700 mb-5">Weather</h1>
        <div className="text-center py-8">
          <div className="text-gray-600">No weather data available.</div>
        </div>
      </div>
    );
  }

  const current = weatherData.current;
  const forecast = weatherData.forecast?.forecast || [];
  const groupedForecast = groupForecastByDay(forecast);

  return (
    <div className="bg-content rounded-lg p-6 shadow-lg">
      <h1 className="text-2xl font-bold text-yellow-700 mb-5">
        Weather - {current.city}, {current.country}
      </h1>

      {/* Current Weather */}
      <div className="bg-white rounded-lg shadow border border-yellow-100 p-6 mb-6">
        <h2 className="text-xl font-semibold text-gray-800 mb-4">Current Weather</h2>
        <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
          <div className="flex items-center gap-4">
            <div>
              <img
                src={`https://openweathermap.org/img/wn/${current.icon}@2x.png`}
                alt={current.description}
                className="w-24 h-24"
              />
            </div>
            <div>
              <div className="text-4xl font-bold text-gray-800">
                {Math.round(current.temperature)}°C
              </div>
              <div className="text-lg text-gray-600 capitalize">{current.description}</div>
              <div className="text-sm text-gray-500">
                Feels like {Math.round(current.feelsLike)}°C
              </div>
            </div>
          </div>
          <div className="grid grid-cols-2 gap-4">
            <div className="bg-blue-50 p-3 rounded border border-blue-200">
              <div className="text-sm text-blue-700 font-semibold">💧 Humidity</div>
              <div className="text-lg text-blue-900">{current.humidity}%</div>
            </div>
            <div className="bg-green-50 p-3 rounded border border-green-200">
              <div className="text-sm text-green-700 font-semibold">🌬️ Wind</div>
              <div className="text-lg text-green-900">
                {Math.round(current.windSpeed * 3.6)} km/h {getWindDirection(current.windDirection)}
              </div>
            </div>
            <div className="bg-purple-50 p-3 rounded border border-purple-200">
              <div className="text-sm text-purple-700 font-semibold">📊 Pressure</div>
              <div className="text-lg text-purple-900">{current.pressure} hPa</div>
            </div>
            <div className="bg-orange-50 p-3 rounded border border-orange-200">
              <div className="text-sm text-orange-700 font-semibold">☁️ Clouds</div>
              <div className="text-lg text-orange-900">{current.cloudiness}%</div>
            </div>
          </div>
        </div>
        {current.visibility && (
          <div className="mt-4 text-sm text-gray-600">
            Visibility: {current.visibility} km
          </div>
        )}
        <div className="mt-4 text-sm text-gray-600">
          Sunrise: {formatTime(current.sunrise)} | Sunset: {formatTime(current.sunset)}
        </div>
      </div>

      {/* Forecast */}
      <div className="bg-white rounded-lg shadow border border-yellow-100 p-6">
        <h2 className="text-xl font-semibold text-gray-800 mb-4">5-Day Forecast</h2>
        <div className="space-y-4">
          {Object.entries(groupedForecast).slice(0, 5).map(([date, items]) => (
            <div key={date} className="border-b border-gray-200 pb-4 last:border-b-0">
              <div className="font-semibold text-gray-700 mb-2">
                {formatDate(items[0].dateTime)}
              </div>
              <div className="grid grid-cols-2 md:grid-cols-4 gap-3">
                {items.slice(0, 4).map((item, idx) => (
                  <div
                    key={idx}
                    className="bg-gray-50 p-3 rounded border border-gray-200"
                  >
                    <div className="text-xs text-gray-600 mb-1">
                      {formatTime(item.dateTime)}
                    </div>
                    <div className="flex items-center gap-2 mb-2">
                      <img
                        src={`https://openweathermap.org/img/wn/${item.icon}@2x.png`}
                        alt={item.description}
                        className="w-[37px] h-[37px]"
                      />
                      <div>
                        <div className="font-semibold text-gray-800">
                          {Math.round(item.temperature)}°C
                        </div>
                        <div className="text-xs text-gray-600 capitalize">
                          {item.description}
                        </div>
                      </div>
                    </div>
                    <div className="text-xs text-gray-500 space-y-1">
                      <div>💧 {item.humidity}%</div>
                      <div>🌬️ {Math.round(item.windSpeed * 3.6)} km/h</div>
                      {item.precipitation > 0 && (
                        <div>🌧️ {item.precipitation.toFixed(1)} mm</div>
                      )}
                    </div>
                  </div>
                ))}
              </div>
            </div>
          ))}
        </div>
      </div>
    </div>
  );
}
