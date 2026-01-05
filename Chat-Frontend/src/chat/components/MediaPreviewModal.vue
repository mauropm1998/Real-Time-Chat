<template>
  <Teleport to="body">
    <div v-if="show && currentFile" class="fixed inset-0 bg-black flex flex-col z-50">
      <!-- Header -->
      <div class="flex items-center justify-between p-4 bg-black bg-opacity-50 flex-shrink-0">
        <div class="flex items-center gap-4">
          <button @click="$emit('close')" class="p-2 rounded-full hover:bg-white/20 cursor-pointer transition-colors">
            <XMarkIcon class="w-6 h-6 text-white" />
          </button>
          <span class="text-white text-lg">{{ currentIndex + 1 }} de {{ files.length }}</span>
        </div>

        <div class="flex items-center gap-2">
          <button @click="removeCurrentFile"
            class="p-2 rounded-full cursor-pointer hover:bg-red-800/50 hover:bg-opacity-20 transition-colors">
            <TrashIcon class="w-6 h-6 text-red-700" />
          </button>
        </div>
      </div>

      <!-- Media Content - Fixed height container -->
      <div class="flex-1 flex items-center justify-center relative min-h-0 overflow-hidden">
        <!-- Navigation Arrows -->
        <button v-if="canNavigate && currentIndex > 0" @click="prevFile"
          class="absolute left-4 z-10 p-3 rounded-full cursor-pointer bg-black hover:bg-white/20 transition-colors">
          <ChevronLeftIcon class="w-8 h-8 text-white" />
        </button>

        <button v-if="canNavigate && currentIndex < files.length - 1" @click="nextFile"
          class="absolute right-4 z-10 p-3 rounded-full cursor-pointer bg-black hover:bg-white/20 transition-colors">
          <ChevronRightIcon class="w-8 h-8 text-white" />
        </button>

        <!-- Image Display -->
        <div v-if="isImage" class="w-full h-full flex items-center justify-center p-4">
          <img :src="currentFile.url" :alt="currentFile.name" class="max-w-full max-h-full object-contain" />
        </div>

        <!-- Video Display -->
        <div v-if="isVideo" class="w-full h-full flex items-center justify-center p-4">
          <video ref="videoRef" :src="currentFile.url" class="max-w-full max-h-full object-contain" controls
            @play="isPlaying = true" @pause="isPlaying = false" />
        </div>

        <!-- File Display -->
        <div v-if="isGenericFile" class="w-full h-full flex items-center justify-center p-4">
          <div class="max-w-[90%] sm:max-w-[80%] md:max-w-[40%] lg:max-w-lg flex flex-col items-center">
            <div class="w-40 h-40 flex items-center justify-center mb-6">
              <img src="/file.png" class="w-full h-full" alt="arquivo genérico" />
            </div>
            <div class="w-full flex flex-col gap-2 items-center text-center">
              <h2 class="text-white font-semibold">{{currentFile.name}}</h2>
              <small class="text-white/70">{{ calculateFileSize(currentFile.size)}}</small>
            </div>
          </div>
        </div>

      </div>
      <!-- Footer - Fixed height -->
      <div class="p-4 bg-black bg-opacity-50 flex-shrink-0">
        <div class="max-w-2xl mx-auto">
          <div class="flex items-center gap-3 mb-4">
            <input ref="msgInput" v-model="captions[currentIndex]" type="text" placeholder="Adicione uma legenda..."
              class="flex-1 px-4 py-2 bg-gray-800 text-white rounded-full border-0 focus:ring-2 focus:ring-primary placeholder-gray-400" />
            <button @click="sendFiles"
              class="p-3 rounded-full bg-primary duration-150 cursor-pointer hover:bg-[#5d4fb5] hover:bg-primary-600 transition-colors">
              <PaperAirplaneIcon class="w-5 h-5 text-white transform" />
            </button>
          </div>

          <!-- File thumbnails -->
          <div class="flex gap-2 overflow-x-auto pb-2">
            <div v-for="(file, index) in files" :key="index"
              class="relative flex-shrink-0 w-16 h-16 rounded-lg overflow-hidden cursor-pointer border-2 transition-colors"
              :class="index === currentIndex ? 'border-primary' : 'border-transparent'"
              @click="currentIndex = index; resetVideo()">
              <img v-if="file.type.startsWith('image/')" :src="file.url" :alt="file.name"
                class="w-full h-full object-cover" />
              <div v-else-if="file.type.startsWith('video/')"
                class="w-full h-full bg-gray-800 flex items-center justify-center">
                <PlayIcon class="w-6 h-6 text-white" />
              </div>
             <img v-else src="/file.png" alt="arquivo genérico"
                class="w-[80%] h-[80%] mx-auto object-cover" />

              <button @click.stop="$emit('remove-file', index)"
                class="absolute -top-1 -right-1 w-5 h-5 bg-red-500 rounded-full flex items-center justify-center hover:bg-red-600 transition-colors">
                <XMarkIcon class="w-3 h-3 text-white" />
              </button>
            </div>
          </div>
        </div>
      </div>
    </div>
  </Teleport>
</template>

<script setup>
import { ref, computed, onMounted, onUnmounted } from 'vue';
import {
  XMarkIcon,
  PlayIcon,
  PauseIcon,
  TrashIcon,
  PaperAirplaneIcon,
  ChevronLeftIcon,
  ChevronRightIcon
} from '@heroicons/vue/24/outline';
import { calculateFileSize } from '@/services/util';

const props = defineProps({
  show: Boolean,
  files: Array
});

const emit = defineEmits(['close', 'send', 'remove-file']);

const msgInput = ref(null);
const currentIndex = ref(0);
const isPlaying = ref(false);
const videoRef = ref(null);
const captions = ref(new Array(props.files.length).fill(''))

const currentFile = computed(() => {
  return props.files[currentIndex.value] || null;
});

const isVideo = computed(() => {
  return currentFile.value?.type.startsWith('video/');
});

const isImage = computed(() => {
  return currentFile.value?.type.startsWith('image/');
});

const isGenericFile = computed(() => {
  return !isVideo.value && !isImage.value && currentFile.value;
});

const canNavigate = computed(() => props.files.length > 1);

const nextFile = () => {
  if (currentIndex.value < props.files.length - 1) {
    currentIndex.value++;
    resetVideo();
  }
};

const prevFile = () => {
  if (currentIndex.value > 0) {
    currentIndex.value--;
    resetVideo();
  }
};

const resetVideo = () => {
  isPlaying.value = false;
  if (videoRef.value) {
    videoRef.value.pause();
    videoRef.value.currentTime = 0;
  }
};

const togglePlay = () => {
  if (videoRef.value) {
    if (isPlaying.value) {
      videoRef.value.pause();
    } else {
      videoRef.value.play();
    }
    isPlaying.value = !isPlaying.value;
  }
};

const removeCurrentFile = () => {
  emit('remove-file', currentIndex.value);
  if (currentIndex.value >= props.files.length - 1) {
    currentIndex.value = Math.max(0, props.files.length - 2);
  }
  if (props.files.length <= 1) {
    emit('close');
  }
};

const sendFiles = () => {
  emit('send', { files: props.files, captions: JSON.parse(JSON.stringify(captions.value)) });
  captions.value = new Array(props.files.length).fill('');
};

const handleKeydown = (event) => {
  if (!props.show) return;

  switch (event.key) {
    case 'Escape':
      emit('close');
      break;
    case 'ArrowLeft':
      if (canNavigate.value) prevFile();
      break;
    case 'ArrowRight':
      if (canNavigate.value) nextFile();
      break;
    case ' ':
      if (isVideo.value && msgInput.value !== document.activeElement) {
        event.preventDefault();
        togglePlay();
      }
      break;
  }
};

onMounted(() => {
  document.addEventListener('keydown', handleKeydown);
});

onUnmounted(() => {
  document.removeEventListener('keydown', handleKeydown);
});
</script>