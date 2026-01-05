<template>
  <div v-if="chat" class="flex flex-col h-full bg-gray-50 dark:bg-gray-900">
    <div
      class="w-full flex items-center px-4 py-3 bg-white dark:bg-gray-900 border-b border-gray-100 dark:border-gray-800">
      <button v-if="isMobile" class="p-2 rounded-full hover:bg-gray-100 dark:hover:bg-gray-800 mr-2" @click="goBack">
        <ArrowLeftIcon class="w-5 h-5 text-gray-600 dark:text-gray-400" />
      </button>

      <div class="flex items-center" :style="isMobile ? 'width: calc(100% - 90px)' : 'width: calc(100% - 50px)'">
        <UserPhoto v-if="chat.type === 'PRIVATE'" src="/teste.jpg" :alt="chat.name" :online="chat.userOnline"
          size="w-10 h-10" />
        <GroupPhoto v-else :src="chat.groupImageUrl" :photoType="chat.imageGroupType" :alt="chat.name" :name="chat.name"
          size="w-10 h-10" />

        <div class="ml-3 transition-all duration-200 ease-in-out"
          :style="isMobile ? 'width: calc(100% - 50px)' : 'width: calc(100% - 50px)'">
          <h2 class="text-base font-semibold text-gray-900 dark:text-white truncate">{{ chat.name }}</h2>
          <p class="text-sm text-gray-400 dark:text-gray-400">{{ chat.type === 'GROUP' ? chat.members.length + ' membros' : lastSeen }}
          </p>
        </div>
      </div>

      <div class="ml-3 flex">
        <button v-if="false" class="p-2 rounded-full hover:bg-gray-100 dark:hover:bg-gray-800">
          <MagnifyingGlassIcon class="w-5 h-5 text-gray-600 dark:text-gray-400" />
        </button>
        <ChatDetailsDropdown v-if="isGroupMember" @details="handleShowDetails" :chat="chat" />
      </div>
    </div>

    <div ref="messagesContainer" class="flex-1 overflow-y-auto p-4 bg-gray-50 dark:bg-gray-900">
      <div class="space-y-4 flex flex-col">
        <div
          class="max-w-[80%] w-auto text-center flex justify-center rounded-3xl px-6 mx-auto py-3 bg-yellow-50/80 text-yellow-900 dark:bg-gray-800 dark:text-white/80 font-semibold text-xs">
          <p class="flex gap-0.5">
            <LockOpenIcon class="flex shrink-0 w-4 h-4" />As mensagens nessa conversa não são encriptadas. Por favor,
            não envie informações sigilosas ou sensíveis.
          </p>
        </div>
        <div>
          <!-- Agrupa as mensagens por data e renderiza um sticky header para cada grupo -->
          <template v-for="(group, groupIndex) in groupedMessages" :key="group.date">
            <div class="text-center top-0 z-10 sticky mb-4" :style="{ top: '0px' }">
              <span v-if="group.timeState === 'TODAY'"
                class="px-3 py-1 text-xs bg-gray-100 dark:bg-gray-800 text-gray-500 dark:text-gray-400 rounded-full block w-30 mx-auto">
                Hoje
              </span>
              <span v-else-if="group.timeState === 'YESTERDAY'"
                class="px-3 py-1 text-xs bg-gray-100 dark:bg-gray-800 text-gray-500 dark:text-gray-400 rounded-full block w-30 mx-auto">
                Ontem
              </span>
              <span v-else
                class="px-3 py-1 text-xs bg-gray-100 dark:bg-gray-800 text-gray-500 dark:text-gray-400 rounded-full block w-30 mx-auto">
                {{ format(new Date(group.date), 'dd/MM/yyyy', { locale: pt }) }}
              </span>
            </div>
            <div v-for="(message, index) in group.messages" :key="message.id">
              <MessageBubble :index="index" :message="message" :chat="chat" :send="message.send"
                @mediaLoaded="onMediaLoaded" @deleteForMe="onDeleteForMe" @deleteForAll="onDeleteForAll"
                :renderAvatar="index === 0 || message.senderId !== group.messages[index - 1].senderId || group.messages[index - 1].isNotificationGroup"
                @react="onReact" @reply="onReply" />
            </div>
          </template>
          <UserTyping v-if="chat.isTyping" />
        </div>
      </div>
    </div>

    <div class="w-full px-4 py-3 bg-white dark:bg-gray-900 border-t border-gray-100 dark:border-gray-800 relative">
      <div v-if="!isGroupMember"
        class="left-0 flex justify-center items-center top-0 w-full h-full absolute text-sm text-center text-gray-400 bg-white dark:bg-gray-900 z-20">
        Você já não é membro deste grupo. Portanto, já não é possível interagires.
      </div>
      <div v-if="isReplying && replyMessage !== null" class="w-full flex flex-col items-center mb-4">
        <div class="flex justify-between items-center w-full">
          <span class="text-base font-semibold text-gray-800 dark:text-gray-400 truncate">A responder a: {{
            replyMessage.senderId === userService.authenticatedUser.id ? 'Você' : replyMessage.senderName }}</span>
          <button @click="closeReply" class="p-2 rounded-full cursor-pointer hover:bg-gray-100 dark:hover:bg-gray-800">
            <XMarkIcon class="w-6 h-6 text-gray-800 dark:text-gray-400" />
          </button>
        </div>
        <div class="w-full flex">
          <p class="text-sm truncate text-gray-600 dark:text-white/60">{{ replyMessage.content }}</p>
        </div>
      </div>
      <div class="flex items-start gap-2">
        <div class="relative">
          <button :disabled="!isGroupMember" ref="attachmentButtonRef"
            class="p-2 rounded-full cursor-pointer hover:bg-gray-100 dark:hover:bg-gray-800"
            @click="showAttachmentDropdown = !showAttachmentDropdown">
            <PaperClipIcon class="w-5 h-5 text-gray-600 dark:text-gray-400" />
          </button>

          <div v-if="showAttachmentDropdown" ref="attachmentDropdownRef"
            class="absolute bottom-full left-0 mb-2 w-48 bg-white dark:bg-gray-800 rounded-lg shadow-lg border border-gray-200 dark:border-gray-700 py-1">
            <button
              class="w-full cursor-pointer px-4 py-2 text-left hover:bg-gray-100 dark:hover:bg-gray-700 flex items-center gap-2"
              @click="handleAttachmentClick('media')">
              <PhotoIcon class="w-5 h-5 text-gray-600 dark:text-gray-400" />
              <span class="text-gray-700 dark:text-gray-300">Fotos e Vídeos</span>
            </button>
            <button
              class="w-full cursor-pointer px-4 py-2 text-left hover:bg-gray-100 dark:hover:bg-gray-700 flex items-center gap-2"
              @click="handleAttachmentClick('document')">
              <DocumentIcon class="w-5 h-5 text-gray-600 dark:text-gray-400" />
              <span class="text-gray-700 dark:text-gray-300">Documento</span>
            </button>
            <button v-if="false"
              class="w-full cursor-pointer px-4 py-2 text-left hover:bg-gray-100 dark:hover:bg-gray-700 flex items-center gap-2"
              @click="handleAttachmentClick('poll')">
              <ChartBarIcon class="w-5 h-5 text-gray-600 dark:text-gray-400" />
              <span class="text-gray-700 dark:text-gray-300">Sondagem</span>
            </button>
          </div>
        </div>

        <div class="relative">
          <button :disabled="!isGroupMember" ref="emojiButtonRef"
            class="p-2 hidden lg:block rounded-full cursor-pointer hover:bg-gray-100 dark:hover:bg-gray-800"
            @click="showEmojiPicker = !showEmojiPicker">
            <FaceSmileIcon class="w-5 h-5 text-gray-600 dark:text-gray-400" />
          </button>

          <div v-if="showEmojiPicker" ref="emojiPickerRef" class="absolute bottom-full left-0 mb-2">
            <EmojiPicker :native="true" theme="light" @select="insertEmoji" />
          </div>
        </div>

        <textarea :disabled="!isGroupMember" ref="autoResizeTextarea" type="text" placeholder="Escreva sua mensagem..."
          v-model="messageText" @input="typing" @keyup="handleKeyDown" @keyup.esc="goBack"
          class="flex-1 px-4 py-2 bg-gray-100 dark:bg-gray-800 rounded-2xl border-0 focus:outline-none focus:ring-0 text-gray-900 dark:text-white placeholder-gray-500 dark:placeholder-gray-400 resize-none overflow-auto"
          style="max-height: 7rem; min-height: 40px; height: 40px;"></textarea>

        <button
          class="p-2 rounded-full bg-primary cursor-pointer duration-150 transition-colors hover:bg-[#5d4fb5] disabled:opacity-50 disabled:cursor-not-allowed"
          @click="sendMessage" :disabled="!messageText.trim() || !isGroupMember">
          <PaperAirplaneIcon class="w-5 h-5 text-white" />
        </button>
      </div>
    </div>
    <!-- Media Preview Modal -->
    <MediaPreviewModal :show="showMediaPreview" :files="selectedFiles" @close="closeMediaPreview" @send="sendMediaFiles"
      @remove-file="removeFile" />
    <!-- Conversation Details Modal -->
    <ConversationDetailsModal :show="showDetailsModal" :chat="chat" @close="showDetailsModal = false" />
    <BaseModalContent @close-modal="openModalDeleteMessage = false" :openModal="openModalDeleteMessage">
      <DeleteMessage @close-modal="openModalDeleteMessage = false" @delete-message="deleteMessage" />
    </BaseModalContent>
    <BaseModalContent @close-modal="openModalDeleteMessageAll = false" :openModal="openModalDeleteMessageAll">
      <DeleteMessageAll @close-modal="openModalDeleteMessageAll = false" @delete-message="deleteMessage" />
    </BaseModalContent>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, nextTick, onUnmounted, watch, onBeforeUnmount } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { format, set } from 'date-fns';
import { pt } from 'date-fns/locale';
import {
  ArrowLeftIcon,
  MagnifyingGlassIcon,
  EllipsisVerticalIcon,
  PaperClipIcon,
  PaperAirplaneIcon,
  PhotoIcon,
  DocumentIcon,
  ChartBarIcon,
  FaceSmileIcon,
  LockOpenIcon,
  XMarkIcon
} from '@heroicons/vue/24/outline';
import { useWindowSize } from '@/composables/useWindowSize';
import OnlineStatus from './OnlineStatus.vue';
import MessageBubble from './MessageBubble.vue';
import EmojiPicker from 'vue3-emoji-picker'
import { useChatStore } from '@/stores/chatStore';
import { emitter } from '@/emitter';
import { UserService } from '@/services/user/UserService';
import MediaPreviewModal from './MediaPreviewModal.vue';
import { websocketService } from '@/services/websocket/websocketService';
import UserTyping from './UserTyping.vue';
import BaseModalContent from '@/components/BaseModalContent.vue';
import DeleteMessage from './DeleteMessage.vue';
import DeleteMessageAll from './DeleteMessageAll.vue';
import { deleteOneMessage, reactOneMessage } from '@/services/api/messageApi';
import ChatDetailsDropdown from './ChatDetailsDropdown.vue';
import UserPhoto from '@/components/UserPhoto.vue';
import GroupPhoto from '@/components/GroupPhoto.vue';
import ConversationDetailsModal from './ChatDetailsModal.vue';

const route = useRoute();
const userService = new UserService();
const router = useRouter();
const chatStore = useChatStore();
const { width } = useWindowSize();
const isMobile = computed(() => width.value < 768);
const autoResizeTextarea = ref(null);
const openModalDeleteMessage = ref(false);
const openModalDeleteMessageAll = ref(false);
const messageToDeleteId = ref(null)
const isReplying = ref(false);
const replyMessage = ref(null);
const chatId = computed(() => route.params.chatId);
const messageText = ref('');
const messagesContainer = ref(null);
const showAttachmentDropdown = ref(false);
const showEmojiPicker = ref(false);
const emojiPickerRef = ref(null);
const emojiButtonRef = ref(null);
const attachmentDropdownRef = ref(null);
const attachmentButtonRef = ref(null);
const typingTimeout = ref(null);
const showDetailsModal = ref(false);

const showMediaPreview = ref(false);
const selectedFiles = ref([]);
const activeUploads = ref([]);

// Criar instância do som
const ringtone = new Audio('/ringtones/send_message.wav')

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

const handleShowDetails = () => {
  showDetailsModal.value = true;
};

const onDeleteForMe = (messageId) => {
  if (isGroupMember.value) {
    openModalDeleteMessage.value = true;
    messageToDeleteId.value = messageId
  }
};

const onDeleteForAll = (messageId) => {
  if (isGroupMember.value) {
    openModalDeleteMessageAll.value = true;
    messageToDeleteId.value = messageId
  }
};

const chat = computed(() => {
  const id = chatId.value;
  if (chatStore.chats.length > 0 && !chatStore.isFilteringChat && chatStore.chatExists(id)) {
    return chatStore.chats.find(c => c.id === id);
  }
  else {
    return chatStore.lastActiveChat
  }
});

const isGroupMember = computed(() => {
  if(chat.value.type === 'PRIVATE') {
    return true;
  }
  return chat.value.members.some((member) => member.user.id === userService.authenticatedUser.id);
});

const handleKeyDown = (event) => {
  if (event.key === 'Enter' && !event.shiftKey) {
    event.preventDefault();
    sendMessage();
  }
};

const groupedMessages = computed(() => {
  if (!chat.value || !chat.value.messages) return [];

  const groups = [];
  let currentGroup = null;

  chat.value.messages.forEach(message => {
    const messageDate = new Date(message.createdAt);
    const messageDateString = format(messageDate, 'yyyy-MM-dd');
    const timeState = getTimeState(messageDate);

    if (!currentGroup || currentGroup.date !== messageDateString) {
      currentGroup = { date: messageDateString, messages: [], timeState: timeState };
      groups.push(currentGroup);
    }

    currentGroup.messages.push(message);
  });

  return groups;
});

const closeReply = () => {
  isReplying.value = false;
  replyMessage.value = null;
};

const getTimeState = (date) => {
  const now = new Date();
  const today = new Date(now.getFullYear(), now.getMonth(), now.getDate());
  const yesterday = new Date(today);
  yesterday.setDate(yesterday.getDate() - 1);

  if (date >= today) return 'TODAY';
  else if (date >= yesterday) return 'YESTERDAY';
  else return 'OLD';
};

const deleteMessage = (target) => {
  openModalDeleteMessage.value = false
  openModalDeleteMessageAll.value = false
  let status = null

  if (target !== 'undefined' && target === 'ALL') {
    status = "DELETED_FOR_ALL"
  }
  else if (target !== 'undefined' && target === 'ME') {
    status = "DELETED_FOR_ME"
  }
  else {
    status = "DELETED_FOR_ME"
  }

  if (chatStore.chatExists(chatId.value)) {
    const chat = chatStore.chats.find(c => c.id === chatId.value)
    const message = chat.messages.find(m => m.id === messageToDeleteId.value)
    message.status = status
    message.deleteById = userService.authenticatedUser.id

    if (chat.lastMessageId === messageToDeleteId.value) {
      chat.lastMessageStatus = status;
      chat.lastMessageDeletedById = userService.authenticatedUser.id
    }
  }
  else {
    const message = chatStore.lastActiveChat.messages.find(m => m.id === messageToDeleteId.value)
    message.status = status
    message.deleteById = userService.authenticatedUser.id
  }

  const deleteRequest = {
    messageId: messageToDeleteId.value,
    chatId: chatId.value,
    receiverId: userService.authenticatedUser.id === chat.value.senderId ? chat.value.receiverId : chat.value.senderId,
    receiverEmail: userService.authenticatedUser.email === chat.value.senderEmail ? chat.value.receiverEmail : chat.value.senderEmail,
    status: status
  }

  deleteOneMessage(deleteRequest).then((response) => {

  }).catch(() => {
    if (chatStore.chatExists(chatId.value)) {
      const chat = chatStore.chats.find(c => c.id === chatId.value)
      const message = chat.messages.find(m => m.id === messageToDeleteId.value)
      message.status = null
      message.deleteById = null
    }
    else {
      const message = chatStore.lastActiveChat.messages.find(m => m.id === messageToDeleteId.value)
      message.status = null
      message.deleteById = null
    }
  })
}

const onReply = (messageId) => {
  if (isGroupMember.value) {
    isReplying.value = true;
    replyMessage.value = chat.value.messages.find(m => m.id === messageId);
    if (autoResizeTextarea.value !== null) {
      autoResizeTextarea.value.focus();
    }
  }
};

const onReact = (reactData) => {
  if (isGroupMember.value) {
    const { messageId, type } = reactData;

    if (chatStore.chatExists(chatId.value)) {
      const chat = chatStore.chats.find(c => c.id === chatId.value)
      const message = chat.messages.find(m => m.id === messageId)

      if (message.reactions.find(r => r.userId === userService.authenticatedUser.id) !== undefined) {
        let reactionIndex = message.reactions.findIndex(r => r.userId === userService.authenticatedUser.id && r.type === type);
        if (reactionIndex >= 0) {
          message.reactions.splice(reactionIndex, 1);
        }
        else {
          reactionIndex = message.reactions.findIndex(r => r.userId === userService.authenticatedUser.id);
          message.reactions[reactionIndex].type = type;
        }
      } else {
        message.reactions.push({ type: type, messageId: messageId, userId: userService.authenticatedUser.id });
      }
    }
    else {
      const message = chatStore.lastActiveChat.messages.find(m => m.id === messageId)
      if (message.reactions.find(r => r.userId === userService.authenticatedUser.id) !== undefined) {
        let reactionIndex = message.reactions.findIndex(r => r.userId === userService.authenticatedUser.id && r.type === type);
        if (reactionIndex >= 0) {
          message.reactions.splice(reactionIndex, 1);
        }
        else {
          reactionIndex = message.reactions.findIndex(r => r.userId === userService.authenticatedUser.id);
          message.reactions[reactionIndex].type = type;
        }
      } else {
        message.reactions.push({ type: type, messageId: messageId, userId: userService.authenticatedUser.id });
      }
    }

    const reactRequest = {
      messageId: messageId,
      reactionType: type,
      receiverEmail: userService.authenticatedUser.email === chat.value.senderEmail ? chat.value.receiverEmail : chat.value.senderEmail
    }

    reactOneMessage(reactRequest).then((response) => {

    }).catch(() => {
      if (chatStore.chatExists(chatId.value)) {
        const chat = chatStore.chats.find(c => c.id === chatId.value)
        const message = chat.messages.find(m => m.id === messageId)
        const reactionIndex = message.reactions.findIndex(r => r.userId === userService.authenticatedUser.id);
        if (reactionIndex !== -1) {
          message.reactions.splice(reactionIndex, 1);
        }
      }
      else {
        const message = chatStore.lastActiveChat.messages.find(m => m.id === messageId)
        const reactionIndex = message.reactions.findIndex(r => r.userId === userService.authenticatedUser.id);
        if (reactionIndex !== -1) {
          message.reactions.splice(reactionIndex, 1);
        }
      }
    })
  }
};

const typing = () => {
  autoResize();
  alertTyping(true);
  if (typingTimeout.value) {
    clearTimeout(typingTimeout.value);
  }
  typingTimeout.value = setTimeout(() => {
    alertTyping(false);
    typingTimeout.value = null;
  }, 3000);
};

const autoResize = () => {
  if (autoResizeTextarea.value) {
    autoResizeTextarea.value.style.height = '40px';
    autoResizeTextarea.value.style.height = `${autoResizeTextarea.value.scrollHeight}px`;
  }
};

const lastSeen = computed(() => {
  const stringFormat = chat.value.lastSeen && (chat.value.lastSeenState === 'TODAY' || chat.value.lastSeenState === 'YESTERDAY') ? 'HH:mm' : chat.value.lastSeen ? 'dd/MM/yyyy HH:mm' : '';
  const lastSeen = chat.value.lastSeen ? format(new Date(chat.value.lastSeen), stringFormat, { locale: pt }) : '';

  if (chat.value.userOnline) return 'Online agora';
  else if (chat.value.lastSeenState === 'TODAY' && lastSeen !== "") return `Online hoje, às ${lastSeen}`;
  else if (chat.value.lastSeenState === 'YESTERDAY' && lastSeen !== "") return `Online ontem, às ${lastSeen}`;
  else if (chat.value.lastSeenState === 'OLD' && lastSeen !== "") return `Online em ${lastSeen.split(' ')[0]}`;

  return ''
});

const goBack = () => {
  alertTyping(false);
  router.push({ name: 'chat' });
  chatStore.setActiveChatId(null);
  emitter.emit("show-chat-mobile", false)
};

const isSameDay = (date1, date2) => {
  if (!date1 || !date2) return false;
  const d1 = new Date(date1);
  const d2 = new Date(date2);
  return d1.getDate() === d2.getDate() && d1.getMonth() === d2.getMonth() && d1.getFullYear() === d2.getFullYear();
};

const sendMessage = () => {
  if (!messageText.value.trim()) return;

  if (isGroupMember.value) {
    alertTyping(false);
    tocarRingtone();

    if (chat.value.messages.length > 0) {
      chatStore.sendMessage(chatId.value, {
        id: Date.now().toString(),
        content: messageText.value,
        type: 'TEXT',
        createdAt: format(new Date(), 'yyyy-MM-dd HH:mm:ss'),
        senderId: userService.authenticatedUser.id,
        receiverId: userService.authenticatedUser.id === chat.value.senderId ? chat.value.receiverId : chat.value.senderId,
        receiverEmail: userService.authenticatedUser.email === chat.value.senderEmail ? chat.value.receiverEmail : chat.value.senderEmail,
        senderEmail: userService.authenticatedUser.email,
        isNotificationGroup: false,
        state: 'SENT',
        status: null,
        deleteById: '',
        messageReplied: replyMessage.value,
        reactions: [],
        send: true,
        reply: isReplying.value
      });

      if (chat.value.type === 'GROUP') {
        chatStore.chats.find(c => c.id === chatId.value).isNotificationGroup = false
      }
    }
    else {
      const message = {
        id: Date.now().toString(),
        content: messageText.value,
        type: 'TEXT',
        createdAt: format(new Date(), 'yyyy-MM-dd HH:mm:ss'),
        senderId: userService.authenticatedUser.id,
        receiverId: userService.authenticatedUser.id === chat.value.senderId ? chat.value.receiverId : chat.value.senderId,
        receiverEmail: userService.authenticatedUser.email === chat.value.senderEmail ? chat.value.receiverEmail : chat.value.senderEmail,
        senderEmail: userService.authenticatedUser.email,
        isNotificationGroup: false,
        state: 'SENT',
        status: null,
        deleteById: '',
        messageReplied: replyMessage.value,
        reactions: [],
        send: true,
        reply: isReplying.value
      };

      chat.value.messages.push(message);
      chat.value.lastMessage = message.content;
      chat.value.lastMessageSenderId = message.senderId;
      chat.value.lastMessageState = message.state;
      chat.value.lastMessageTime = message.createdAt;
      chat.value.lastMessageType = message.type;
      chat.value.lastMessageTimeState = "NOW";
      chat.value.isNotificationGroup = false;
      chat.value.send = message.send;

      chatStore.sendMessageNewChat(chat.value)

    }

    messageText.value = '';
    closeReply();

    nextTick(() => {
      if (messagesContainer.value) {
        messagesContainer.value.scrollTop = messagesContainer.value.scrollHeight;
        autoResize()
      }
    });
  }
};

const onMediaLoaded = () => {
  nextTick(() => {
    if (messagesContainer.value) {
      messagesContainer.value.scrollTop = messagesContainer.value.scrollHeight;
      autoResize()
    }
  });
};

const insertEmoji = (emoji) => {
  messageText.value += emoji.i
  showEmojiPicker.value = false;
};

const handleClickOutside = (event) => {
  if (showEmojiPicker.value &&
    !emojiPickerRef.value?.contains(event.target) &&
    !emojiButtonRef.value?.contains(event.target)) {
    showEmojiPicker.value = false;
  }

  if (showAttachmentDropdown.value &&
    !attachmentDropdownRef.value?.contains(event.target) &&
    !attachmentButtonRef.value?.contains(event.target)) {
    showAttachmentDropdown.value = false;
  }
};

const markMessagesAsSeen = () => {
  if (chat.value === null || chat.value.messages.length === 0) return;

  const receiverEmail = userService.authenticatedUser.email === chat.value.senderEmail ? chat.value.receiverEmail : chat.value.senderEmail;
  const messageIds = chat.value.messages
    .filter(message => message.senderId !== userService.authenticatedUser.id && message.state !== 'SEEN')
    .map(message => message.id);

  chat.value.messages.forEach(message => {
    if (message.senderId !== userService.authenticatedUser.id && message.state !== 'SEEN') {
      message.state = 'SEEN';
    }
  });

  chat.value.unreadCount = 0;

  websocketService.send(`/app/message/seen`, {
    messageIds: messageIds,
    chatId: chat.value.id,
    receiverEmail: receiverEmail,
  });
};

onMounted(() => {

  if (isMobile.value) {
    emitter.emit("show-chat-mobile", true);
  }

  emitter.on("mark-as-seen", (data) => {
    if (data.chatId === chatId.value) {
      setTimeout(() => {
        markMessagesAsSeen();
      }, 500);
    }
  });

  nextTick(() => {
    if (messagesContainer.value) {
      messagesContainer.value.scrollTop = messagesContainer.value.scrollHeight;
      if (autoResizeTextarea.value !== null) {
        autoResizeTextarea.value.focus();
      }
    }
  });

  watch(
    () => chat.value?.isTyping,
    (typing) => {
      if (typing) {
        nextTick(() => {
          if (messagesContainer.value) {
            messagesContainer.value.scrollTop = messagesContainer.value.scrollHeight;
          }
        });
      }
    }
  );

  watch(
    () => autoResizeTextarea.value,
    (value) => {
      if (value !== null) {
        autoResizeTextarea.value.focus();
      }
    }
  );

  watch(
    () => chatId.value,
    () => {
      markMessagesAsSeen();
      replyMessage.value = null;
      isReplying.value = false;
      if (autoResizeTextarea.value !== null) {
        autoResizeTextarea.value.focus();
      }
      chatStore.setActiveChatId(chatId.value, true);
    }
  );

  // Também rola para baixo quando as mensagens mudam
  watch(
    () => chat.value?.messages?.length,
    () => {
      nextTick(() => {
        if (messagesContainer.value) {
          messagesContainer.value.scrollTop = messagesContainer.value.scrollHeight;
        }
      });
    }
  );

  if (chatId.value) {
    if (chatStore.chats.length > 0) {
      if (chatId.value && chatStore.chatExists(chatId.value)) {
        chatStore.setActiveChatId(chatId.value, true)
      }
    }
  }

  watch(() => chatStore.chats, () => {
    if (chatStore.chats.length > 0) {
      if (chatId.value && chatStore.chatExists(chatId.value)) {
        chatStore.setActiveChatId(chatId.value, true)
      }
      else {
        router.push({ name: 'chat' });
      }
    }
  })

  markMessagesAsSeen();
  document.addEventListener('click', handleClickOutside);
});

onUnmounted(() => {
  emitter.off("mark-as-seen");
  document.removeEventListener('click', handleClickOutside);
  // Clean up any remaining object URLs
  selectedFiles.value.forEach(fileData => {
    URL.revokeObjectURL(fileData.url);
  });
  if (typingTimeout.value) {
    clearTimeout(typingTimeout.value);
  }
});

const alertTyping = (isTyping) => {
  websocketService.send(`/app/message/typing`, {
    chatId: chatId.value,
    isTyping: isTyping,
    userId: userService.authenticatedUser.id,
    receiverEmail: userService.authenticatedUser.email === chat.value.senderEmail ? chat.value.receiverEmail : chat.value.senderEmail
  });
};

onBeforeUnmount(() => {
  emitter.off("mark-as-seen");
});

const handleAttachmentClick = (type) => {
  showAttachmentDropdown.value = false;
  // Create file input for images and videos
  const input = document.createElement('input');
  input.type = 'file';
  input.multiple = true;

  if (type === 'media') {
    input.accept = 'image/*,video/*';
    input.onchange = handleFileSelection;
    input.click();
  }
  else {
    input.onchange = handleFileSelection;
    input.click();
  }
};

const handleFileSelection = (event) => {
  const files = Array.from(event.target.files);

  if (files.length === 0) return;

  // Limit to 20 files
  if (files.length > 20) {
    alert('Você pode selecionar no máximo 20 arquivos por vez.');
    return;
  }

  // Process files and create preview URLs
  const processedFiles = files.map(file => ({
    file,
    name: file.name,
    type: file.type,
    size: file.size,
    url: URL.createObjectURL(file)
  }));

  selectedFiles.value = processedFiles;
  showMediaPreview.value = true;
};

const removeFile = (index) => {
  // Revoke the object URL to free memory
  URL.revokeObjectURL(selectedFiles.value[index].url);
  selectedFiles.value.splice(index, 1);
};

const sendMediaFiles = async ({ files, captions }) => {
  selectedFiles.value = [];
  showMediaPreview.value = false;

  files.forEach((file, index) => {
    const type = file.type.startsWith('image/') ? 'IMAGE' : file.type.startsWith('video/') ? 'VIDEO' : 'DOCUMENT';
    chatStore.sendMessage(chatId.value, {
      id: Date.now().toString(),
      content: captions[index] || '',
      type: type,
      createdAt: format(new Date(), 'yyyy-MM-dd HH:mm:ss'),
      senderId: userService.authenticatedUser.id,
      receiverId: userService.authenticatedUser.id === chat.value.senderId ? chat.value.receiverId : chat.value.senderId,
      receiverEmail: userService.authenticatedUser.email === chat.value.senderEmail ? chat.value.receiverEmail : chat.value.senderEmail,
      isNotificationGroup: false,
      state: 'SENT',
      status: null,
      reactions: [],
      send: true,
      reply: isReplying.value,
      file: file.file,
      messageReplied: null,
      mediaUrl: file.url,
      mediaExtension: file.file.name.split('.').pop(),
      mediaName: file.file.name,
      mediaSize: file.file.size,
    });

    nextTick(() => {
      if (messagesContainer.value) {
        messagesContainer.value.scrollTop = messagesContainer.value.scrollHeight;
        autoResize()
      }
    });
  });

  closeReply();

};

const cancelUpload = (uploadId) => {
  uploadService.cancelUpload(uploadId);
};

const closeMediaPreview = () => {
  // Clean up object URLs
  selectedFiles.value.forEach(fileData => {
    URL.revokeObjectURL(fileData.url);
  });
  selectedFiles.value = [];
  showMediaPreview.value = false;
};
</script>
