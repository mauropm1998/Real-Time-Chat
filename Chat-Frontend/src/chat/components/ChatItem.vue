<template>
  <div @click="$emit('click')"
    class="relative flex items-center mx-4 my-2 rounded-xl p-3 cursor-pointer transition-colors dark:border-gray-700"
    :class="[
      chat.unreadCount > 0 ? 'bg-primary-light dark:bg-primary/20 !shadow-lg' : 'hover:bg-gray-50 hover:shadow-md dark:hover:bg-gray-800', active ? '!bg-gray-100 !shadow-md dark:!bg-gray-800' : '' ,
      'dark:bg-gray-900']">

    <UserPhoto v-if="chat.type === 'PRIVATE'" class="mr-3" src="/teste.jpg" :alt="chat.name" :online="chat.userOnline" size="w-12 h-12" />
    <GroupPhoto v-else class="mr-3" :src="chat.groupImageUrl" :photoType="chat.imageGroupType" :alt="chat.name" :name="chat.name" size="w-12 h-12" />

    <div class="flex-1 min-w-0">
      <div class="flex justify-between items-center">
        <h3 class="text-sm font-semibold text-gray-800 dark:text-white truncate">
          {{ chat.name }}
        </h3>
        <span :class="{ 'text-primary dark:text-primary font-bold': chat.unreadCount > 0 }"
          class="text-xs text-gray-500 dark:text-gray-400">{{ formattedTime }}</span>
      </div>
      <div class="flex justify-between items-center mt-1">
        <div class="flex flex-1 items-center" style="width: calc(100% - 20px);">
          <!-- Start Message Status Icons -->
          <div
            v-if="userService.authenticatedUser.id === chat.lastMessageSenderId && !chat.isTyping && chat.lastMessageStatus !== 'DELETED_FOR_ALL' && chat.lastMessageStatus !== 'DELETED_FOR_ME' && !chat.isNotificationGroup"
            class="flex w-5 h-5 flex-shrink-0 mr-0.5">
            <svg
              v-if="chat.lastMessageState === 'SENT' && userService.authenticatedUser.id === chat.lastMessageSenderId"
              xmlns="http://www.w3.org/2000/svg" fill="currentColor"
              class="bi bi-check2 mr-0.5 w-full h-full text-gray-500 dark:text-white/60" viewBox="0 0 16 16">
              <path
                d="M13.854 3.646a.5.5 0 0 1 0 .708l-7 7a.5.5 0 0 1-.708 0l-3.5-3.5a.5.5 0 1 1 .708-.708L6.5 10.293l6.646-6.647a.5.5 0 0 1 .708 0" />
            </svg>
            <svg
              v-if="(chat.lastMessageState === 'RECEIVED' || chat.lastMessageState === 'SEEN') && userService.authenticatedUser.id === chat.lastMessageSenderId"
              xmlns="http://www.w3.org/2000/svg" fill="currentColor"
              :class="{ '!text-blue-500 dark:!text-blue-400': chat.lastMessageState === 'SEEN' }"
              class="bi bi-check2-all mr-1 w-full h-full text-gray-500 dark:text-white/60" viewBox="0 0 16 16">
              <path
                d="M12.354 4.354a.5.5 0 0 0-.708-.708L5 10.293 1.854 7.146a.5.5 0 1 0-.708.708l3.5 3.5a.5.5 0 0 0 .708 0zm-4.208 7-.896-.897.707-.707.543.543 6.646-6.647a.5.5 0 0 1 .708.708l-7 7a.5.5 0 0 1-.708 0" />
              <path d="m5.354 7.146.896.897-.707.707-.897-.896a.5.5 0 1 1 .708-.708" />
            </svg>
          </div>
          <!-- End Message Status Icons -->
          <!-- Start Message Type Icons -->
          <div
            v-if="chat.lastMessageType !== 'TEXT' && !chat.isTyping && chat.lastMessageStatus !== 'DELETED_FOR_ALL' && chat.lastMessageStatus !== 'DELETED_FOR_ME'"
            class="flex w-5 h-5 flex-shrink-0 mr-0.5">
            <svg v-if="chat.lastMessageType === 'AUDIO'" xmlns="http://www.w3.org/2000/svg" fill="currentColor"
              class="bi bi-mic mr-1 w-4 h-4 text-gray-500 dark:text-white/60" viewBox="0 0 16 16">
              <path
                d="M3.5 6.5A.5.5 0 0 1 4 7v1a4 4 0 0 0 8 0V7a.5.5 0 0 1 1 0v1a5 5 0 0 1-4.5 4.975V15h3a.5.5 0 0 1 0 1h-7a.5.5 0 0 1 0-1h3v-2.025A5 5 0 0 1 3 8V7a.5.5 0 0 1 .5-.5" />
              <path d="M10 8a2 2 0 1 1-4 0V3a2 2 0 1 1 4 0zM8 0a3 3 0 0 0-3 3v5a3 3 0 0 0 6 0V3a3 3 0 0 0-3-3" />
            </svg>

            <svg v-if="chat.lastMessageType === 'VIDEO'" xmlns="http://www.w3.org/2000/svg" fill="none"
              viewBox="0 0 24 24" stroke-width="1.5" stroke="currentColor"
              class="mr-1 w-5 h-5 text-gray-500 dark:text-white/60">
              <path stroke-linecap="round" stroke-linejoin="round"
                d="m15.75 10.5 4.72-4.72a.75.75 0 0 1 1.28.53v11.38a.75.75 0 0 1-1.28.53l-4.72-4.72M4.5 18.75h9a2.25 2.25 0 0 0 2.25-2.25v-9a2.25 2.25 0 0 0-2.25-2.25h-9A2.25 2.25 0 0 0 2.25 7.5v9a2.25 2.25 0 0 0 2.25 2.25Z" />
            </svg>

            <svg v-if="chat.lastMessageType === 'IMAGE'" xmlns="http://www.w3.org/2000/svg" fill="currentColor"
              class="bi bi-image mr-1 w-5 h-5 text-gray-500 dark:text-white/60" viewBox="0 0 16 16">
              <path d="M6.002 5.5a1.5 1.5 0 1 1-3 0 1.5 1.5 0 0 1 3 0" />
              <path
                d="M2.002 1a2 2 0 0 0-2 2v10a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V3a2 2 0 0 0-2-2zm12 1a1 1 0 0 1 1 1v6.5l-3.777-1.947a.5.5 0 0 0-.577.093l-3.71 3.71-2.66-1.772a.5.5 0 0 0-.63.062L1.002 12V3a1 1 0 0 1 1-1z" />
            </svg>
            <svg v-if="chat.lastMessageType === 'DOCUMENT'" xmlns="http://www.w3.org/2000/svg" fill="currentColor"
              class="bi bi-file-earmark mr-1 w-4 h-4 text-gray-500 dark:text-white/60" viewBox="0 0 16 16">
              <path
                d="M14 4.5V14a2 2 0 0 1-2 2H4a2 2 0 0 1-2-2V2a2 2 0 0 1 2-2h5.5zm-3 0A1.5 1.5 0 0 1 9.5 3V1H4a1 1 0 0 0-1 1v12a1 1 0 0 0 1 1h8a1 1 0 0 0 1-1V4.5z" />
            </svg>
            <svg v-if="chat.lastMessageType === 'POLL'" xmlns="http://www.w3.org/2000/svg" fill="currentColor"
              class="bi bi-list-nested mr-1 w-8 h-5 text-gray-500 dark:text-white/60" viewBox="0 0 16 16">
              <path fill-rule="evenodd"
                d="M4.5 11.5A.5.5 0 0 1 5 11h10a.5.5 0 0 1 0 1H5a.5.5 0 0 1-.5-.5m-2-4A.5.5 0 0 1 3 7h10a.5.5 0 0 1 0 1H3a.5.5 0 0 1-.5-.5m-2-4A.5.5 0 0 1 1 3h10a.5.5 0 0 1 0 1H1a.5.5 0 0 1-.5-.5" />
            </svg>
          </div>
          <!-- End Message Type Icons -->
          <!-- Start Message Deleted Icon -->
          <div
            v-if="chat.lastMessageStatus === 'DELETED_FOR_ALL' && !chat.isTyping || (chat.lastMessageStatus === 'DELETED_FOR_ME' && chat.lastMessageDeletedById === userService.authenticatedUser.id)"
            class="flex justify-center items-center w-5 h-5 flex-shrink-0 mr-0.5">
            <svg xmlns="http://www.w3.org/2000/svg" fill="currentColor"
              class="bi bi-ban mr-1 w-4 h-4 text-gray-500 dark:text-white/60" viewBox="0 0 16 16">
              <path
                d="M15 8a6.97 6.97 0 0 0-1.71-4.584l-9.874 9.875A7 7 0 0 0 15 8M2.71 12.584l9.874-9.875a7 7 0 0 0-9.874 9.874ZM16 8A8 8 0 1 1 0 8a8 8 0 0 1 16 0" />
            </svg>
          </div>
          <!-- End Message Deleted Icon -->
          <p class="text-sm truncate" :class="[
            chat.isTyping
              ? 'typing-indicator text-primary dark:text-primary'
              : 'text-gray-600 dark:text-gray-300'
          ]">
            {{ chat.lastMessageStatus === 'DELETED_FOR_ALL' && !chat.isTyping || (chat.lastMessageStatus ===
              'DELETED_FOR_ME' && chat.lastMessageDeletedById === userService.authenticatedUser.id) ? 'Apagou esta mensagem' : messagePreview }}
          </p>
        </div>
        <span v-if="chat.unreadCount > 0"
          class="flex-flex-shrink-0 w-5 h-5 flex items-center justify-center rounded-full bg-primary text-white text-xs">
          {{ chat.unreadCount }}
        </span>
      </div>
    </div>

    <button v-if="false"
      class="absolute right-2 top-1/2 -translate-y-1/2 w-8 h-8 flex items-center justify-center rounded-full hover:bg-gray-200 dark:hover:bg-gray-700 transition-colors opacity-0 group-hover:opacity-100"
      @click="toggleOptions">
      <EllipsisVerticalIcon class="w-5 h-5 text-gray-600 dark:text-gray-400" />
    </button>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, onUnmounted } from 'vue';
import { format } from 'date-fns';
import { EllipsisVerticalIcon } from '@heroicons/vue/24/outline';
import OnlineStatus from './OnlineStatus.vue';
import { UserService } from '@/services/user/UserService';
import UserPhoto from '@/components/UserPhoto.vue';
import GroupPhoto from '@/components/GroupPhoto.vue';
import { useChatStore } from '@/stores/chatStore';

defineEmits(["click"])
const props = defineProps({
  chat: {
    type: Object,
    required: true
  },
  active: {
    type: Boolean,
    default: false
  }
});

const chatStore = useChatStore()
const userService = new UserService()
const showOptions = ref(false);
const optionsButtonRef = ref(null);
const dropdownRef = ref(null);

const formattedTime = computed(() => {
  if (props.chat.lastMessageTime === null) return '';

  if (props.chat.lastMessageTimeState === 'NOW') {
    return 'agora';
  }

  if (props.chat.lastMessageTimeState === 'TODAY') {
    return format(new Date(props.chat.lastMessageTime), 'HH:mm');
  }
  if (props.chat.lastMessageTimeState === 'YESTERDAY') {
    return 'ontem';
  }
  if (props.chat.lastMessageTimeState === 'OLD') {
    return format(new Date(props.chat.lastMessageTime), 'dd/MM/yyyy');
  }
});

const messagePreview = computed(() => {
  const lastMessage = props.chat.lastMessage;
  const isTyping = props.chat.isTyping;

  if (isTyping) {
    if(props.chat.type === 'GROUP') {
      return props.chat.userTypingName.split(" ")[0] + ' está a escrever...';
    }
    else {
      return 'a escrever...';
    }
  }

  if (!lastMessage && props.chat.lastMessageType === 'IMAGE') return props.chat.type === 'GROUP' && !props.chat.isNotificationGroup ? props.chat.lastMessageSenderId === userService.authenticatedUser.id ?  "Eu: Imagem" : props.chat.lastMessageSenderName.split(" ")[0] + ": Imagem" : "Imagem";
  if (!lastMessage && props.chat.lastMessageType === 'VIDEO') return props.chat.type === 'GROUP' && !props.chat.isNotificationGroup ? props.chat.lastMessageSenderId === userService.authenticatedUser.id ?  "Eu: Vídeo" : props.chat.lastMessageSenderName.split(" ")[0] + ": Vídeo" : "Vídeo";
  if (!lastMessage && props.chat.lastMessageType === 'DOCUMENT') return props.chat.type === 'GROUP' && !props.chat.isNotificationGroup ? props.chat.lastMessageSenderId === userService.authenticatedUser.id ?  "Eu: Documento" : props.chat.lastMessageSenderName.split(" ")[0] + ": Documento" : "Documento";

  return props.chat.type === 'GROUP' && !props.chat.isNotificationGroup ? props.chat.lastMessageSenderId === userService.authenticatedUser.id ?  "Eu: " + lastMessage : props.chat.lastMessageSenderName.split(" ")[0] + ": " + lastMessage : lastMessage
});

const handleClickOutside = (event) => {
  if (showOptions.value) {
    const dropdown = dropdownRef.value;
    const button = optionsButtonRef.value;
    if (!dropdown?.contains(event.target) && !button?.contains(event.target)) {
      showOptions.value = false;
    }
  }
};

onMounted(() => {
  document.addEventListener('click', handleClickOutside);
});

onUnmounted(() => {
  document.removeEventListener('click', handleClickOutside);
});
</script>
