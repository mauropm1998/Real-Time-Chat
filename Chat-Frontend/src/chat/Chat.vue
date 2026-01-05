<template>
  <div class="flex h-screen w-full overflow-hidden">
    <div class="w-full md:w-[350px] flex-shrink-0 border-r border-gray-100 dark:border-gray-800 bg-white dark:bg-gray-900" :class="isMobile && showChat ? 'hidden' : isMobile && !showChat ? 'block' : isMobile ? 'block' : 'hidden md:block'" >
      <router-view name="chatList" />
    </div>
    
    <div class="flex-1 overflow-hidden flex flex-col" :class="isMobile && showChat ? 'flex' : isMobile && !showChat ? 'hidden' : isMobile ? 'hidden md:flex' : ''">
      <router-view name="chatView" />
    </div>
  </div>
</template>

<script setup>
import { useWindowSize } from '@/composables/useWindowSize';
import { emitter } from '@/emitter';
import { computed, onBeforeUnmount, onMounted, ref } from 'vue';
import { websocketService } from '../services/websocket/websocketService';
import { server } from '@/services/util';

const { width } = useWindowSize();
const isMobile = computed(() => width.value < 768);
const showChat = ref(false)

onMounted(() => {
  document.title = 'ChatApp - Conversas';

  emitter.on("show-chat-mobile", (value) => {
    showChat.value = value
  })

  // Conectar ao WebSocket quando o app iniciar
  websocketService.connect(server(true));
});

onBeforeUnmount(() => {
  emitter.off("show-chat-mobile")
})
</script>
