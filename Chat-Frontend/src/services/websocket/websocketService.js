import { Client } from "@stomp/stompjs";
import SockJS from "sockjs-client";
import { UserService } from "../user/UserService";

class WebSocketService {
  constructor() {
    this.client = null;
    this.subscribers = new Map();
    this.userService = new UserService();
  }

  connect(serverUrl) {
    this.client = new Client({
      webSocketFactory: () =>
        new SockJS(
          serverUrl +
            "?token=Bearer " +
            this.userService.authenticatedUser.accessToken
        ),
      debug: function (str) {
        console.log(str);
      },
      reconnectDelay: 5000,
      heartbeatIncoming: 4000,
      heartbeatOutgoing: 4000,
    });

    this.client.onConnect = () => {
      console.log("Conectado ao WebSocket");
      this.subscribers.forEach((value, destination) => {
        // Verifica se ainda não subscreveu (evita duplicata)
        if (!value.subscription) {
          const subscription = this.client.subscribe(destination, (message) => {
            value.callback(JSON.parse(message.body));
          });

          this.subscribers.set(destination, { ...value, subscription });
        }
      });
    };

    this.client.onStompError = (frame) => {
      console.error("Erro no WebSocket:", frame);
    };

    this.client.activate();
  }

  disconnect() {
    if (this.client) {
      this.client.deactivate();
    }
  }

  subscribe(destination, callback) {
    // Verifica se já existe uma subscrição ativa
    if (this.subscribers.has(destination)) {
      console.warn(`Já subscrito a: ${destination}`);
      return;
    }

    // Se já conectado, faz a subscrição agora
    if (this.client && this.client.connected) {
      const subscription = this.client.subscribe(destination, (message) => {
        callback(JSON.parse(message.body));
      });

      // Salva o callback e a subscription (caso precise cancelar depois)
      this.subscribers.set(destination, { callback, subscription });
    } else {
      // Salva o callback para subscrever mais tarde, quando conectar
      this.subscribers.set(destination, { callback, subscription: null });
    }
  }

  send(destination, message) {
    if (this.client && this.client.connected) {
      this.client.publish({
        destination,
        body: JSON.stringify(message),
      });
    }
  }

  // Métodos específicos para chat
  subscribeToUserTyping(conversationId, callback) {
    return this.subscribe(`/topic/chat/${conversationId}/typing`, callback);
  }

  subscribeToMessages(conversationId, callback) {
    return this.subscribe(`/topic/chat/${conversationId}/messages`, callback);
  }

  subscribeToUserStatus(userId, callback) {
    return this.subscribe(`/topic/users/${userId}/status`, callback);
  }

  sendTypingStatus(conversationId, isTyping) {
    this.send(`/app/chat/${conversationId}/typing`, { isTyping });
  }

  sendMessage(conversationId, message) {
    this.send(`/app/chat/${conversationId}/message`, message);
  }
}

export const websocketService = new WebSocketService();
