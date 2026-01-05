import { defineStore } from "pinia";
import { ref, computed } from "vue";

export const useChatStore = defineStore("chat", () => {
  const chats = ref([]);
  const lastActiveChat = ref(null);
  const activeChatId = ref(null);
  const isFilteringChat = ref(false);

  const setActiveChatId = (id, setChat = false) => {
    if (setChat) {
      const chat = chats.value.find((c) => c.id === id);
      setLastActiveChat(chat);
    } else {
      setLastActiveChat(null);
    }
  };

  const setFilteringChat = (value) => {
    isFilteringChat.value = value;
  };

  const setLastActiveChat = (chat) => {
    lastActiveChat.value = JSON.parse(JSON.stringify(chat));
  };

  const sendMessage = (chatId, message) => {
    const chat = chats.value.find((c) => c.id === chatId);

    if (!chat) {
      lastActiveChat.value.messages.push(message);
      lastActiveChat.lastMessage = message.content;
      lastActiveChat.lastMessageSenderId = message.senderId;
      lastActiveChat.lastMessageState = message.state;
      lastActiveChat.lastMessageTime = message.createdAt;
      lastActiveChat.lastMessageType = message.type;
      lastActiveChat.lastMessageTimeState = "NOW";
      lastActiveChat.send = message.send;

      return;
    }

    chat.messages.push(message);
    chat.lastMessage = message.content;
    chat.lastMessageSenderId = message.senderId;
    chat.lastMessageState = message.state;
    chat.lastMessageTime = message.createdAt;
    chat.lastMessageType = message.type;
    chat.lastMessageTimeState = "NOW";
    chat.send = message.send;

    setLastActiveChat(chat);
  };

  const chatExists = (chatId) => {
    return chats.value.some((chat) => chat.id === chatId);
  };

  const sendMessageNewChat = (chat) => {
    chats.value.unshift(chat);
  };

  const createChat = (chat) => {
    chats.value.unshift(chat);
  };

  return {
    chats,
    activeChatId,
    lastActiveChat,
    isFilteringChat,
    sendMessageNewChat,
    chatExists,
    setFilteringChat,
    setActiveChatId,
    setLastActiveChat,
    sendMessage,
    createChat,
  };
});
