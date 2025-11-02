import axios from "axios";

const API = axios.create({
  baseURL: "http://localhost:8000/api",
  headers: {
    "Content-Type": "application/json",
  },
});

// Interceptor para adjuntar token JWT si existe
API.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem("accessToken");
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
  },
  (error) => {
    return Promise.reject(error);
  }
);

// Interceptor para manejar respuestas y errores de autenticación
API.interceptors.response.use(
  (response) => response,
  async (error) => {
    const originalRequest = error.config;

    // Si es error 401 y no es el endpoint de login y no se ha reintentado
    if (
      error.response?.status === 401 &&
      !originalRequest._retry &&
      !originalRequest.url?.includes("/token/")
    ) {
      originalRequest._retry = true;

      try {
        const refreshToken = localStorage.getItem("refreshToken");
        
        if (!refreshToken) {
          throw new Error("No refresh token available");
        }

        // Intenta refrescar el token
        const response = await axios.post(
          "http://localhost:8000/api/token/refresh/",
          { refresh: refreshToken }
        );

        const { access } = response.data;
        localStorage.setItem("accessToken", access);

        // Reintentar la petición original con el nuevo token
        originalRequest.headers.Authorization = `Bearer ${access}`;
        return API(originalRequest);
      } catch (refreshError) {
        // Si falla el refresh, limpiar storage y redirigir al login
        localStorage.removeItem("accessToken");
        localStorage.removeItem("refreshToken");
        window.location.href = "/admin/login";
        return Promise.reject(refreshError);
      }
    }

    return Promise.reject(error);
  }
);

export default API;