<template>
    <div class="absolute z-20 mt-2 bg-white shadow-lg rounded-lg py-1 !w-auto overflow-hidden">
        <div class="flex items-center px-3 py-2 text-xl">
            <button @click="$emit('react', 'LIKE')" :class="{ 'bg-gray-100': message.reactions.some(r => r.type === 'LIKE' && r.userId === userService.authenticatedUser.id) }"
                class="hover:bg-gray-100 flex justify-center items-center rounded-full duration-150 transition-colors w-10 h-10 cursor-pointer">👍</button>
            <button @click="$emit('react', 'LOVE')" :class="{ 'bg-gray-100': message.reactions.some(r => r.type === 'LOVE' && r.userId === userService.authenticatedUser.id) }"
                class="hover:bg-gray-100 flex justify-center items-center rounded-full duration-150 transition-colors w-10 h-10 cursor-pointer">❤️</button>
            <button @click="$emit('react', 'HAHA')" :class="{ 'bg-gray-100': message.reactions.some(r => r.type === 'HAHA' && r.userId === userService.authenticatedUser.id) }"
                class="hover:bg-gray-100 flex justify-center items-center rounded-full duration-150 transition-colors w-10 h-10 cursor-pointer">😂</button>
            <button @click="$emit('react', 'WOW')" :class="{ 'bg-gray-100': message.reactions.some(r => r.type === 'WOW' && r.userId === userService.authenticatedUser.id) }"
                class="hover:bg-gray-100 flex justify-center items-center rounded-full duration-150 transition-colors w-10 h-10 cursor-pointer">😲</button>
            <button @click="$emit('react', 'SAD')" :class="{ 'bg-gray-100': message.reactions.some(r => r.type === 'SAD' && r.userId === userService.authenticatedUser.id) }"
                class="hover:bg-gray-100 flex justify-center items-center rounded-full duration-150 transition-colors w-10 h-10 cursor-pointer">😓</button>
            <button @click="$emit('react', 'ANGRY')" :class="{ 'bg-gray-100': message.reactions.some(r => r.type === 'ANGRY' && r.userId === userService.authenticatedUser.id) }"
                class="hover:bg-gray-100 flex justify-center items-center rounded-full duration-150 transition-colors w-10 h-10 cursor-pointer">😡</button>
        </div>
        <hr class="text-gray-100">
        <div @click="$emit('reply')"  class="flex items-center gap-x-1.5 cursor-pointer hover:bg-gray-50 px-3 py-3 text-left">
            <svg xmlns="http://www.w3.org/2000/svg" fill="currentColor" class="bi bi-reply h-5 w-5" viewBox="0 0 16 16">
                <path
                    d="M6.598 5.013a.144.144 0 0 1 .202.134V6.3a.5.5 0 0 0 .5.5c.667 0 2.013.005 3.3.822.984.624 1.99 1.76 2.595 3.876-1.02-.983-2.185-1.516-3.205-1.799a8.7 8.7 0 0 0-1.921-.306 7 7 0 0 0-.798.008h-.013l-.005.001h-.001L7.3 9.9l-.05-.498a.5.5 0 0 0-.45.498v1.153c0 .108-.11.176-.202.134L2.614 8.254l-.042-.028a.147.147 0 0 1 0-.252l.042-.028zM7.8 10.386q.103 0 .223.006c.434.02 1.034.086 1.7.271 1.326.368 2.896 1.202 3.94 3.08a.5.5 0 0 0 .933-.305c-.464-3.71-1.886-5.662-3.46-6.66-1.245-.79-2.527-.942-3.336-.971v-.66a1.144 1.144 0 0 0-1.767-.96l-3.994 2.94a1.147 1.147 0 0 0 0 1.946l3.994 2.94a1.144 1.144 0 0 0 1.767-.96z" />
            </svg>
            <div @click="onEdit" class="text-[.9rem] text-gray-900 whitespace-nowrap">Responder</div>
        </div>
        <div class="flex items-center gap-x-1.5 cursor-pointer hover:bg-gray-50 px-3 py-3 text-left">
            <svg xmlns="http://www.w3.org/2000/svg" fill="currentColor" class="bi bi-reply h-5 w-5 -scale-x-100"
                viewBox="0 0 16 16">
                <path
                    d="M6.598 5.013a.144.144 0 0 1 .202.134V6.3a.5.5 0 0 0 .5.5c.667 0 2.013.005 3.3.822.984.624 1.99 1.76 2.595 3.876-1.02-.983-2.185-1.516-3.205-1.799a8.7 8.7 0 0 0-1.921-.306 7 7 0 0 0-.798.008h-.013l-.005.001h-.001L7.3 9.9l-.05-.498a.5.5 0 0 0-.45.498v1.153c0 .108-.11.176-.202.134L2.614 8.254l-.042-.028a.147.147 0 0 1 0-.252l.042-.028zM7.8 10.386q.103 0 .223.006c.434.02 1.034.086 1.7.271 1.326.368 2.896 1.202 3.94 3.08a.5.5 0 0 0 .933-.305c-.464-3.71-1.886-5.662-3.46-6.66-1.245-.79-2.527-.942-3.336-.971v-.66a1.144 1.144 0 0 0-1.767-.96l-3.994 2.94a1.147 1.147 0 0 0 0 1.946l3.994 2.94a1.144 1.144 0 0 0 1.767-.96z" />
            </svg>
            <div @click="onDelete" class="text-[.9rem] text-gray-900 whitespace-nowrap">
                Reencaminhar
            </div>
        </div>
        <div @click="$emit('delete-for-me')" v-if="!isMyMessage"
            class="flex items-center gap-x-1.5 cursor-pointer hover:bg-gray-50 px-3 py-3 text-left">
            <svg xmlns="http://www.w3.org/2000/svg" fill="currentColor" class="bi bi-trash h-4 w-4 mt-1 mr-1"
                viewBox="0 0 16 16">
                <path
                    d="M5.5 5.5A.5.5 0 0 1 6 6v6a.5.5 0 0 1-1 0V6a.5.5 0 0 1 .5-.5m2.5 0a.5.5 0 0 1 .5.5v6a.5.5 0 0 1-1 0V6a.5.5 0 0 1 .5-.5m3 .5a.5.5 0 0 0-1 0v6a.5.5 0 0 0 1 0z" />
                <path
                    d="M14.5 3a1 1 0 0 1-1 1H13v9a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2V4h-.5a1 1 0 0 1-1-1V2a1 1 0 0 1 1-1H6a1 1 0 0 1 1-1h2a1 1 0 0 1 1 1h3.5a1 1 0 0 1 1 1zM4.118 4 4 4.059V13a1 1 0 0 0 1 1h6a1 1 0 0 0 1-1V4.059L11.882 4zM2.5 3h11V2h-11z" />
            </svg>
            <div @click="onDelete" class="text-[.9rem] text-gray-900 whitespace-nowrap">
                Apagar só para mim
            </div>
        </div>
        <div @click="$emit('delete-for-all')" v-if="isMyMessage"
            class="flex items-center gap-x-1.5 cursor-pointer hover:bg-gray-50 px-3 py-3 text-left">
            <svg xmlns="http://www.w3.org/2000/svg" fill="currentColor" class="bi bi-trash h-4 w-4 mt-1 mr-1"
                viewBox="0 0 16 16">
                <path
                    d="M5.5 5.5A.5.5 0 0 1 6 6v6a.5.5 0 0 1-1 0V6a.5.5 0 0 1 .5-.5m2.5 0a.5.5 0 0 1 .5.5v6a.5.5 0 0 1-1 0V6a.5.5 0 0 1 .5-.5m3 .5a.5.5 0 0 0-1 0v6a.5.5 0 0 0 1 0z" />
                <path
                    d="M14.5 3a1 1 0 0 1-1 1H13v9a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2V4h-.5a1 1 0 0 1-1-1V2a1 1 0 0 1 1-1H6a1 1 0 0 1 1-1h2a1 1 0 0 1 1 1h3.5a1 1 0 0 1 1 1zM4.118 4 4 4.059V13a1 1 0 0 0 1 1h6a1 1 0 0 0 1-1V4.059L11.882 4zM2.5 3h11V2h-11z" />
            </svg>
            <div @click="onDelete" class="text-[.9rem] text-gray-900 whitespace-nowrap">
                Apagar
            </div>
        </div>
    </div>
</template>

<script setup>
import { UserService } from '@/services/user/UserService';

defineEmits(['reply', 'delete-for-all', 'delete-for-me', 'react']);
const userService = new UserService();
const props = defineProps({
    isMyMessage: {
        type: Boolean,
        required: false
    },
    message: {
        type: Object
    }
});
</script>