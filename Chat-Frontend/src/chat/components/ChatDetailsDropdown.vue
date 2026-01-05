<template>
    <div class="relative">
        <button @click="toggleDropdown" class="p-2 rounded-full cursor-pointer hover:bg-gray-100 dark:hover:bg-gray-800">
            <EllipsisVerticalIcon class="w-6 h-6 text-gray-600 dark:text-gray-400" />
        </button>

        <div v-if="showDropdown"
            class="absolute right-0 top-full mt-2 w-auto bg-white dark:bg-gray-800 rounded-lg shadow-lg py-1 z-10">
            <button class="w-auto px-4 py-2 text-left hover:bg-gray-100 dark:hover:bg-gray-700 flex items-center gap-2"
                @click="showDetails">
                <Bars3CenterLeftIcon class="w-5 h-5 flex-shrink-0 text-gray-600 dark:text-gray-400" />
                <span class="text-gray-700 dark:text-gray-300 whitespace-nowrap">Detalhes {{ chat.type === 'GROUP' ? 'do grupo' : 'da conversa' }}</span>
            </button>
        </div>
    </div>
</template>

<script setup>
import { Bars3CenterLeftIcon, EllipsisVerticalIcon } from '@heroicons/vue/24/outline';
import { onMounted, ref } from 'vue';

const showDropdown = ref(false);
const emit = defineEmits(['details']);
const props = defineProps({
    chat: {
        type: Object,
        required: true
    }
});
const toggleDropdown = () => {
    showDropdown.value = !showDropdown.value;
};

const showDetails = () => {
    showDropdown.value = false;
    emit('details');
};

onMounted(() => {
    document.addEventListener('click', (event) => {
        if (!event.target.closest('.relative')) {
            showDropdown.value = false;
        }
    });
});

</script>