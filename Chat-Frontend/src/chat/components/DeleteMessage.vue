<template>
    <div class="bg-white h-auto max-w-[600px] w-auto rounded-lg shadow-lg overflow-hidden">
        <div class="w-full flex flex-col gap-2 px-6 py-4">
          <h3 class="text-lg font-semibold text-gray-900 dark:text-white">Apagar Mensagem?</h3>
          <p class="text-gray-700 dark:text-gray-300">Apagar essa mensagem não afeta o outro lado da conversa.</p>
        </div>
        <div class="w-full flex justify-end gap-2 bg-gray-100 px-6 py-4">
          <button @click="$emit('delete-message')" class="px-4 py-2 font-semibold bg-red-700 text-white rounded-lg hover:bg-red-800 cursor-pointer transition-colors">
            Apagar só para mim
          </button>
          <button @click="$emit('close-modal')"
            class="px-4 py-2 bg-gray-100 font-semibold dark:bg-gray-700 text-gray-800 dark:text-gray-200 rounded-lg cursor-pointer hover:bg-gray-200 dark:hover:bg-gray-600 transition-colors">
            Cancelar
          </button>
        </div>
      </div>
</template>

<script setup>
defineEmits(['delete-message', 'close-modal']);
</script>
