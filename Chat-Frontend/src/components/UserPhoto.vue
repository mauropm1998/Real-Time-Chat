<template>
    <div class="relative flex-shrink-0" :class="size">
        <img :src="src" :alt="alt" class="rounded-full object-cover" :class="[size]" :style="isLoading ? 'display: none !important;' : 'display: block;'"
            @load="handleLoad" />
        <span class="flex bg-gray-200 animate-pulse rounded-full" v-if="isLoading" :class="size"></span>
        <OnlineStatus v-if="showOnlineStatus" :is-online="online" />
    </div>
</template>

<script setup>
import OnlineStatus from '@/chat/components/OnlineStatus.vue';
import { ref } from 'vue';

const props = defineProps({
    src: {
        type: String,
        required: true
    },
    alt: {
        type: String,
        default: 'User photo'
    },
    online: {
        type: Boolean,
        default: false
    },
    size: {
        type: String,
        default: 'w-10 h-10'
    },
    showOnlineStatus: {
        type: Boolean,
        default: true
    }
});

const isLoading = ref(true);

const handleLoad = () => {
    isLoading.value = false;
};

</script>