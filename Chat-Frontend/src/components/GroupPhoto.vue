<template>
    <div class="relative flex-shrink-0" :class="size">
        <img v-if="photoType === 'CUSTOM'" :src="src" :alt="alt" class="rounded-full object-cover" :class="[size]"
            @load="handleLoad" :style="isLoading ? 'display: none !important;' : 'display: block;'" />
        <span v-if="isLoading && photoType === 'CUSTOM'" class="flex bg-gray-200 animate-pulse rounded-full"
            :class="size"></span>
        <span v-if="photoType === 'LOADING'"
            class="flex justify-center items-center bg-gray-200 rounded-full" :class="size">
            <SecondLoader class="w-5 h-5 flex before:!border-gray-700" />
        </span>
        <span v-if="photoType === 'DEFAULT'"
            class="flex bg-primary justify-center items-center !font-medium text-white rounded-full" :class="size">
            {{ getInitials(name) }}
        </span>
    </div>
</template>

<script setup>
import { ref } from 'vue';
import SecondLoader from './SecondLoader.vue';

const props = defineProps({
    src: String,
    alt: {
        type: String,
        default: 'User photo'
    },
    photoType: {
        type: String,
        default: "DEFAULT"
    },
    name: {
        type: String,
        default: ''
    },
    size: {
        type: String,
        default: 'w-10 h-10 text-lg'
    },
});

const isLoading = ref(true);

const handleLoad = () => {
    isLoading.value = false;
};

function getInitials(name) {
    if (!name) return '';
    const parts = name.trim().split(' ');
    if (parts.length === 1) {
        // Apenas um nome, retorna as duas primeiras letras
        return parts[0].substring(0, 2).toUpperCase();
    }
    // Dois ou mais nomes, retorna a primeira letra do primeiro e do último nome
    return (parts[0][0] + parts[parts.length - 1][0]).toUpperCase();
}

</script>