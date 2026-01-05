<template>
    <div class="w-full flex flex-col gap-2 rounded-lg p-3 mb-2" :class="isMyMessage ? 'bg-[#6251cb]' : 'bg-slate-200'">
        <div class="flex">
            <span :class="isMyMessage ? 'text-[#d8a9ff]' : 'text-gray-800'" class="text-xs font-semibold truncate">{{ message.senderId === userService.authenticatedUser.id ? 'Eu' : message.senderName }}</span>
        </div>
        <div class="flex">
            <p :class="isMyMessage ? 'text-white' : 'text-gray-800'" class="text-sm truncate ">{{ message.content }}</p>
        </div>
    </div>
</template>

<script setup>
import { UserService } from '@/services/user/UserService';

defineProps({
    isMyMessage: {
        type: Boolean,
        required: true
    },
    chat: Object,
    message: Object
});

const userService = new UserService();
</script>