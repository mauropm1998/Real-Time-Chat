<template>
  <div class="flex flex-col h-full bg-white dark:bg-gray-900">

    <div class="flex justify-between items-center p-4">
      <h1 class="text-3xl font-bold text-gray-900 dark:text-white">Conversas</h1>
      <div class="flex gap-2">
        <button v-if="true"
          class="w-9 h-9 flex items-center cursor-pointer justify-center rounded-full hover:bg-gray-100 dark:hover:bg-gray-800 transition-colors"
          @click="themeStore.toggleTheme">
          <SunIcon v-if="themeStore.isDark" class="w-5 h-5 text-gray-600 dark:text-gray-300" />
          <MoonIcon v-else class="w-5 h-5 text-gray-600 dark:text-gray-300" />
        </button>
        <div class="relative">
          <button
            class="w-9 h-9 flex items-center cursor-pointer justify-center rounded-full hover:bg-gray-100 dark:hover:bg-gray-800 transition-colors"
            @click="toggleNewChatDropdown">
            <PencilSquareIcon class="w-5 h-5 text-gray-600 dark:text-gray-300" />
          </button>

          <div v-if="showNewChatDropdown"
            class="absolute right-0 top-full mt-2 w-48 bg-white dark:bg-gray-800 rounded-lg shadow-lg py-1 z-10">
            <button class="w-full px-4 py-2 text-left hover:bg-gray-100 dark:hover:bg-gray-700 flex items-center gap-2"
              @click="openNewChat">
              <UserIcon class="w-5 h-5 text-gray-600 dark:text-gray-400" />
              <span class="text-gray-700 dark:text-gray-300">Nova Conversa</span>
            </button>
            <button class="w-full px-4 py-2 text-left hover:bg-gray-100 dark:hover:bg-gray-700 flex items-center gap-2"
              @click="openGroupCreation">
              <UserGroupIcon class="w-5 h-5 text-gray-600 dark:text-gray-400" />
              <span class="text-gray-700 dark:text-gray-300">Novo Grupo</span>
            </button>
          </div>
        </div>
      </div>
    </div>

    <div class="p-4">
      <div class="relative">
        <MagnifyingGlassIcon class="absolute left-3 top-1/2 transform -translate-y-1/2 w-5 h-5 text-gray-400" />
        <input type="text" placeholder="Procurar conversas..." v-model="searchQuery"
          class="w-full pl-10 pr-4 py-2 rounded-full bg-gray-50 placeholder:text-gray-400 dark:bg-gray-800 border-0 focus:outline-none dark:text-white" />
      </div>
    </div>

    <div class="flex gap-2 p-4">
      <button class="flex items-center cursor-pointer gap-2 px-3 py-1.5 rounded-full text-sm transition-colors" :class="[
        currentFilter === 'ALL'
          ? 'bg-primary text-white font-semibold'
          : 'bg-gray-50 dark:bg-gray-800 text-gray-700 dark:text-gray-300 dark:hover:bg-gray-700'
      ]" @click="currentFilter = 'ALL'">
        Todas
      </button>
      <button class="flex items-center cursor-pointer gap-2 px-3 py-1.5 rounded-full text-sm transition-colors" :class="[
        currentFilter === 'GROUP'
          ? 'bg-primary text-white font-semibold'
          : 'bg-gray-50 dark:bg-gray-800 text-gray-700 dark:text-gray-300 dark:hover:bg-gray-700'
      ]" @click="currentFilter = 'GROUP'">
        Grupos
      </button>
      <button class="flex items-center cursor-pointer gap-2 px-3 py-1.5 rounded-full text-sm transition-colors" :class="[
        currentFilter === 'UNREAD'
          ? 'bg-primary text-white font-semibold'
          : 'bg-gray-50 dark:bg-gray-800 text-gray-700 dark:text-gray-300 dark:hover:bg-gray-700'
      ]" @click="currentFilter = 'UNREAD'">
        Não lidas
      </button>
    </div>

    <div class="flex-1 overflow-y-auto">
      <div v-if="!isFetching && sortedChats.length > 0">
        <ChatItem v-for="chat in sortedChats" :key="chat.id" :chat="chat"
          :active="chatStore.lastActiveChat?.id === chat.id" @click="selectChat(chat.id)" />
      </div>
      <div v-if="isFetching">
        <ChatItemPlaceholder v-for="chat in 10" :key="chat" />
      </div>
      <NoData v-if="!isFetching && sortedChats.length === 0 && searchQuery.length === 0 && currentFilter === 'ALL'"
        :showIcon="false" title="Sem Conversas"
        description="Você ainda não tem conversas para mostrar. Inicie uma conversa para ser mostrada aqui." />

      <NoData v-if="!isFetching && sortedChats.length === 0 && searchQuery.length > 0 && currentFilter === 'ALL'"
        :showIcon="false" title="Sem Conversas Encontradas"
        description="Você não tem conversas que correspondam à sua pesquisa." />

      <NoData :showIcon="false" v-if="!isFetching && sortedChats.length === 0 && currentFilter === 'UNREAD'"
        title="Sem Mensagens Não Lidas"
        description="Você não tem mensagens não lidas. Aparecerão aqui quando você as receber." />

      <NoData :showIcon="false" v-if="!isFetching && sortedChats.length === 0 && currentFilter === 'GROUP'"
        title="Sem Mensagens de Grupo"
        description="Você não tem mensagens de grupo. Aparecerão aqui quando você as receber." />

    </div>

    <UserSelectionModal :show="showNewChatModal" @create-chat="onCreateChat" @close="showNewChatModal = false" />
    <GroupCreationModal :show="showGroupModal" @close="showGroupModal = false" />
    <ProcessModal :show="showProcessModal" />

  </div>
</template>

<script setup>
import { ref, computed, onMounted, watch } from 'vue';
import { useRouter } from 'vue-router';
import { formatDistanceToNow } from 'date-fns';
import { pt } from 'date-fns/locale';
import {
  PencilSquareIcon,
  MagnifyingGlassIcon,
  ChatBubbleLeftIcon,
  UserIcon,
  PhoneIcon,
  Cog6ToothIcon,
  SunIcon,
  MoonIcon,
  FunnelIcon,
  BellAlertIcon,
  UserGroupIcon,
  ChevronDownIcon
} from '@heroicons/vue/24/outline';
import ChatItem from './ChatItem.vue';
import UserSelectionModal from './UserSelectionModal.vue';
import { useThemeStore } from '@/stores/themeStore';
import { useWindowSize } from '@/composables/useWindowSize';
import NoData from '@/components/NoData.vue';
import ChatItemPlaceholder from './ChatItemPlaceholder.vue';
import { getChatById, getMyChats, saveChat } from '@/services/api/chatApi';
import { useChatStore } from '@/stores/chatStore';
import { emitter } from '@/emitter';
import ProcessModal from '@/components/ProcessModal.vue';
import { websocketService } from '@/services/websocket/websocketService';
import { UserService } from '@/services/user/UserService';
import GroupCreationModal from './GroupCreationModal.vue';

const router = useRouter();
const chatStore = useChatStore();
const themeStore = useThemeStore();
const { width } = useWindowSize();
const isMobile = computed(() => width.value < 768);

const searchQuery = ref('');
const showNewChatModal = ref(false);
const showGroupModal = ref(false);
const showNewChatDropdown = ref(false);
const isFetching = ref(false);
const currentFilter = ref('ALL')
const showProcessModal = ref(false);

const openNewChat = () => {
  showNewChatModal.value = true;
  showNewChatDropdown.value = false;
};

const openGroupCreation = () => {
  showGroupModal.value = true;
  showNewChatDropdown.value = false;
};

const toggleNewChatDropdown = () => {
  showNewChatDropdown.value = !showNewChatDropdown.value;
};

// Computed para chats ordenados por lastMessageTime (descendente)
const sortedChats = computed(() => {
  let filteredChats = [];
  if (currentFilter.value === 'ALL') {
    filteredChats = chatStore.chats;
  } else if (currentFilter.value === 'GROUP') {
    filteredChats = chatStore.chats.filter(chat => chat.type === 'GROUP');
  } else if (currentFilter.value === 'UNREAD') {
    filteredChats = chatStore.chats.filter(chat => chat.unreadCount > 0);
  }

  // Filtrar por searchQuery (name ou lastMessage)
  if (searchQuery.value.trim().length > 0) {
    const query = searchQuery.value.trim().toLowerCase();
    filteredChats = filteredChats.filter(chat => {
      const nameMatch = chat.name && chat.name.toLowerCase().includes(query);
      const lastMessageMatch = chat.lastMessage && chat.lastMessage.toLowerCase().includes(query);
      return nameMatch || lastMessageMatch;
    });
  }

  return [...filteredChats].sort((a, b) => {
    const timeA = a.lastMessageTime ? new Date(a.lastMessageTime).getTime() : 0;
    const timeB = b.lastMessageTime ? new Date(b.lastMessageTime).getTime() : 0;
    return timeB - timeA;
  });
});

// Criar instância do som
const ringtone = new Audio('/ringtones/received_message.wav')

// Desbloquear áudio após primeira interação
const desbloquearSom = () => {
  ringtone.play().then(() => {
    ringtone.pause()
    ringtone.currentTime = 0
  }).catch(err => {
    console.warn('Audio bloqueado até haver interação.', err)
  })
}

// Tocar som
const tocarRingtone = () => {
  ringtone.play().catch(err => {
    console.warn('Erro ao tocar som:', err)
  })
}

const userService = new UserService();
const selectChat = (chatId) => {
  router.push({ name: 'chat-view', params: { chatId: chatId } });

  setTimeout(() => {
    emitter.emit("show-chat-mobile", true)
  }, 500)
};

const getAllChats = () => {
  isFetching.value = true;

  getMyChats()
    .then(response => {
      chatStore.setFilteringChat(false)
      chatStore.chats = response.data;
    })
    .catch(error => {
      console.error('Error fetching chats:', error);
    })
    .finally(() => {
      isFetching.value = false;
    });
};

const onCreateChat = (data) => {
  showNewChatModal.value = false;
  showProcessModal.value = true;

  saveChat(data.senderId, data.receiverId)
    .then(response => {
      const chatId = response.data.response

      if (chatStore.chatExists(chatId)) {
        showProcessModal.value = false;
        router.push({ name: 'chat-view', params: { chatId: chatId } });
        setTimeout(() => {
          emitter.emit("show-chat-mobile", true)
        }, 500)

        return;
      }

      getChatById(chatId)
        .then(chatResponse => {
          chatStore.setLastActiveChat(chatResponse.data);
          showProcessModal.value = false;
          router.push({ name: 'chat-view', params: { chatId: chatId } });

          setTimeout(() => {
            emitter.emit("show-chat-mobile", true)
          }, 500)
        })
        .catch(error => {
          showProcessModal.value = false;
          console.error('Error fetching chat by ID:', error);
        });

    })
    .catch(error => {
      showProcessModal.value = false;
      console.error('Error creating chat:', error);
    });
}

onMounted(() => {
  if (isMobile.value) {
    emitter.emit("show-chat-mobile", true);
  }

  getAllChats()

  websocketService.subscribe(`/users/${userService.authenticatedUser.email}/chat`, (message) => {
    if (chatStore.chatExists(message.chatId)) {
      const chat = chatStore.chats.find(chat => chat.id === message.chatId)
      message.reactions = [];
      chat.messages.push(message);
      chat.unreadCount = message.chatResponse.unreadCount;
      chat.lastMessage = message.chatResponse.lastMessage;
      chat.lastMessageTime = message.chatResponse.lastMessageTime;
      chat.lastMessageSenderId = message.chatResponse.lastMessageSenderId;
      chat.lastMessageSenderName = message.chatResponse.lastMessageSenderName;
      chat.lastMessageState = message.chatResponse.lastMessageState;
      chat.lastMessageStatus = message.chatResponse.lastMessageStatus;
      chat.lastMessageType = message.chatResponse.lastMessageType;
      chat.lastMessageTimeState = message.chatResponse.lastMessageTimeState;
      chat.lastSeenState = message.chatResponse.lastSeenState;
      chat.isUserOnline = message.chatResponse.isUserOnline;
      chat.lastSeen = message.chatResponse.lastSeen;
      chat.senderId = message.chatResponse.senderId;
      chat.senderEmail = message.chatResponse.senderEmail;
      chat.receiverEmail = message.chatResponse.receiverEmail;
      chat.receiverId = message.chatResponse.receiverId;
      chat.isNotificationGroup = message.chatResponse.isNotificationGroup;
      chat.type = message.chatResponse.type;

      websocketService.send(`/app/message/received`, {
        messageId: message.id,
        chatId: message.chatId,
        receiverEmail: message.senderEmail,
        state: 'RECEIVED'
      });

      emitter.emit("mark-as-seen", {
        chatId: message.chatId
      });

    } else {
      if (message.chatResponse.type === 'GROUP') {
        chatStore.chats.unshift(message.chatResponse);
      }
      else {
        message.reactions = [];
        message.chatResponse.messages = [];
        chatStore.chats.unshift(message.chatResponse);
        delete message.chatResponse;
        chatStore.chats[0].messages.push(message);

        websocketService.send(`/app/message/received`, {
          messageId: message.id,
          chatId: message.chatId,
          receiverEmail: message.senderEmail,
          state: 'RECEIVED'
        });
      }

    }

    tocarRingtone();
  });

  websocketService.subscribe(`/users/${userService.authenticatedUser.email}/group`, (message) => {
    if (chatStore.chatExists(message.chatId)) {
      const chat = chatStore.chats.find(chat => chat.id === message.chatId)
      message.reactions = [];
      chat.messages.push(message);
      chat.unreadCount = message.chatResponse.unreadCount;
      chat.lastMessage = message.chatResponse.lastMessage;
      chat.lastMessageTime = message.chatResponse.lastMessageTime;
      chat.lastMessageSenderId = message.chatResponse.lastMessageSenderId;
      chat.lastMessageSenderName = message.chatResponse.lastMessageSenderName;
      chat.lastMessageState = message.chatResponse.lastMessageState;
      chat.lastMessageStatus = message.chatResponse.lastMessageStatus;
      chat.lastMessageType = message.chatResponse.lastMessageType;
      chat.lastMessageTimeState = message.chatResponse.lastMessageTimeState;
      chat.lastSeenState = message.chatResponse.lastSeenState;
      chat.isUserOnline = message.chatResponse.isUserOnline;
      chat.lastSeen = message.chatResponse.lastSeen;
      chat.senderId = message.chatResponse.senderId;
      chat.senderEmail = message.chatResponse.senderEmail;
      chat.receiverEmail = message.chatResponse.receiverEmail;
      chat.receiverId = message.chatResponse.receiverId;
      chat.isNotificationGroup = message.chatResponse.isNotificationGroup;
      chat.type = message.chatResponse.type;
      chat.groupImageUrl = message.chatResponse.groupImageUrl
      chat.imageGroupType = message.chatResponse.imageGroupType
      chat.name = message.chatResponse.name
      chat.description = message.chatResponse.description

    } else {
      if (message.chatResponse.type === 'GROUP') {
        chatStore.chats.unshift(message.chatResponse);
      }
      else {
        message.reactions = [];
        message.chatResponse.messages = [];
        chatStore.chats.unshift(message.chatResponse);
        delete message.chatResponse;
        chatStore.chats[0].messages.push(message);
      }
    }
  });

  websocketService.subscribe(`/users/${userService.authenticatedUser.email}/group/status`, (message) => {
    if (chatStore.chatExists(message.chatId)) {
      const chat = chatStore.chats.find(chat => chat.id === message.chatId)
      message.reactions = [];
      chat.messages.push(message);
      chat.unreadCount = message.chatResponse.unreadCount;
      chat.lastMessage = message.chatResponse.lastMessage;
      chat.lastMessageTime = message.chatResponse.lastMessageTime;
      chat.lastMessageSenderId = message.chatResponse.lastMessageSenderId;
      chat.lastMessageSenderName = message.chatResponse.lastMessageSenderName;
      chat.lastMessageState = message.chatResponse.lastMessageState;
      chat.lastMessageStatus = message.chatResponse.lastMessageStatus;
      chat.lastMessageType = message.chatResponse.lastMessageType;
      chat.lastMessageTimeState = message.chatResponse.lastMessageTimeState;
      chat.lastSeenState = message.chatResponse.lastSeenState;
      chat.isUserOnline = message.chatResponse.isUserOnline;
      chat.lastSeen = message.chatResponse.lastSeen;
      chat.senderId = message.chatResponse.senderId;
      chat.senderEmail = message.chatResponse.senderEmail;
      chat.receiverEmail = message.chatResponse.receiverEmail;
      chat.receiverId = message.chatResponse.receiverId;
      chat.isNotificationGroup = message.chatResponse.isNotificationGroup;
      chat.type = message.chatResponse.type;
      chat.groupImageUrl = message.chatResponse.groupImageUrl
      chat.imageGroupType = message.chatResponse.imageGroupType
      chat.name = message.chatResponse.name
      chat.description = message.chatResponse.description

      if (message.action === 'REM') {
        rem(chat, message)
      }

      if (message.action === 'ADD') {
        add(chat, message)
      }

    } else {
      const chat = chatStore.lastActiveChat
      message.reactions = [];
      chat.messages.push(message);

      if (message.action === 'REM') {
        rem(chat, message)
      }

      if (message.action === 'ADD') {
        add(chat, message)
      }
    }
  });

  const rem = (chat, message) => {
    chat.members.splice(chat.members.findIndex(member => member.user.id === message.memberId), 1)
  }

  const add = (chat, message) => {
    chat.members.push(message.member)
  }

  websocketService.subscribe(`/users/${userService.authenticatedUser.email}/message/state`, (message) => {
    if (chatStore.chatExists(message.chatId)) {
      const chat = chatStore.chats.find(chat => chat.id === message.chatId)
      if (chat.lastMessageId === message.messageId) {
        chat.lastMessageState = message.state;
      }
      chat.messages.find(msg => msg.id === message.messageId).state = message.state;
    } else {
      const chat = chatStore.lastActiveChat

      // Update the last message state if the chat is the last active chat
      // and the last message ID matches the message ID
      if (chat !== null && chat.id === message.chatId) {
        if (chat.lastMessageId === message.messageId) {
          chat.lastMessageState = message.state;
        }
        chat.messages.find(msg => msg.id === message.messageId).state = message.state;
      }
    }
  });

  websocketService.subscribe(`/users/${userService.authenticatedUser.email}/message/status`, (message) => {
    if (chatStore.chatExists(message.chatId)) {
      const chat = chatStore.chats.find(chat => chat.id === message.chatId)
      if (chat.lastMessageId === message.messageId) {
        chat.lastMessageStatus = message.status;
        chat.lastMessageDeletedById = message.deleteById
      }
      chat.messages.find(msg => msg.id === message.messageId).status = message.status;
      chat.messages.find(msg => msg.id === message.messageId).deleteById = message.deleteById;
    } else {
      const chat = chatStore.lastActiveChat

      // Update the last message state if the chat is the last active chat
      // and the last message ID matches the message ID
      if (chat !== null && chat.id === message.chatId) {
        if (chat.lastMessageId === message.messageId) {
          chat.lastMessageStatus = message.status;
          chat.lastMessageDeletedById = message.deleteById
        }
        chat.messages.find(msg => msg.id === message.messageId).status = message.status;
        chat.messages.find(msg => msg.id === message.messageId).deleteById = message.deleteById;
      }
    }
  });

  websocketService.subscribe(`/users/${userService.authenticatedUser.email}/message/reaction`, (reaction) => {
    if (chatStore.chatExists(reaction.chatId)) {
      const chat = chatStore.chats.find(chat => chat.id === reaction.chatId)
      const message = chat.messages.find(msg => msg.id === reaction.messageId);

      if (message.reactions.find(r => r.userId === reaction.userReactedId) !== undefined) {
        let reactionIndex = message.reactions.findIndex(r => r.userId === reaction.userReactedId && r.type === reaction.reactionType);
        if (reactionIndex >= 0) {
          message.reactions.splice(reactionIndex, 1);
        }
        else {
          reactionIndex = message.reactions.findIndex(r => r.userId === reaction.userReactedId);
          message.reactions[reactionIndex].type = reaction.reactionType;
        }
      } else {
        message.reactions.push({ type: reaction.reactionType, messageId: reaction.messageId, userId: reaction.userReactedId });
      }

    } else {
      const chat = chatStore.lastActiveChat
      const message = chat.messages.find(msg => msg.id === reaction.messageId);

      if (message.reactions.find(r => r.userId === reaction.userReactedId) !== undefined) {
        let reactionIndex = message.reactions.findIndex(r => r.userId === reaction.userReactedId && r.type === reaction.reactionType);
        if (reactionIndex >= 0) {
          message.reactions.splice(reactionIndex, 1);
        }
        else {
          reactionIndex = message.reactions.findIndex(r => r.userId === reaction.userReactedId);
          message.reactions[reactionIndex].type = reaction.reactionType;
        }
      } else {
        message.reactions.push({ type: reaction.reactionType, messageId: reaction.messageId, userId: reaction.userReactedId });
      }
    }
  });

  websocketService.subscribe(`/users/${userService.authenticatedUser.email}/message/typing`, (message) => {
    if (chatStore.chatExists(message.chatId)) {
      const chat = chatStore.chats.find(chat => chat.id === message.chatId)
      chat.isTyping = message.isTyping;

      if (chat.type === 'GROUP') {
        chat.userTypingName = chat.members.find(m => m.user.id === message.userId).user.fullName;
      }
    } else {
      const chat = chatStore.lastActiveChat
      chat.isTyping = message.isTyping;
      if (chat.type === 'GROUP') {
        chat.userTypingName = chat.members.find(m => m.user.id === message.userId).user.fullName;
      }
    }
  });
});
</script>
