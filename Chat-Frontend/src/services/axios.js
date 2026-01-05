import axios from "axios";
import { UserService } from "./user/UserService";
import { server } from "./util";

const userService = new UserService();

const instance = axios.create({
  baseURL: server(),
});

// Interceptor para anexar o token
instance.interceptors.request.use(
  async (config) => {
    if(userService.authenticatedUser !== null) {
      const token = userService.authenticatedUser.accessToken;
      config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
  },
  (error) => Promise.reject(error)
);

// Interceptor para tratar respostas 401
instance.interceptors.response.use(
  (response) => response,
  async (error) => {
    if (
      error.response &&
      (error.response.status === 401 || error.response.status === 403) &&
      window.location.pathname !== "/"
    ) {
      location.assign("/");
    }
    return Promise.reject(error);
  }
);

export default instance;
