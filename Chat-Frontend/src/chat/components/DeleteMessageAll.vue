<template>
    <div class="bg-white h-auto max-w-[600px] w-auto rounded-lg shadow-lg overflow-hidden">
        <div class="w-full flex flex-col gap-2 px-6 py-4">
          <h3 class="text-lg font-semibold text-gray-900 dark:text-white">Apagar Mensagem?</h3>
          <p class="text-gray-700 dark:text-gray-300">Pode apagar para todos ou apenas para si.</p>
          <div class="flex flex-col gap-4 mt-4">
            <!-- Radio buttons estilizados -->
            <label class="flex items-center gap-3 cursor-pointer">
              <input
                type="radio"
                class="form-radio accent-red-700 w-5 h-5"
                v-model="deleteOption"
                value="ALL"
              />
              <span class="font-semibold text-gray-900 dark:text-white">Apagar para todos</span>
            </label>
            <label class="flex items-center gap-3 cursor-pointer">
              <input
                type="radio"
                class="form-radio accent-red-700 w-5 h-5"
                v-model="deleteOption"
                value="ME"
              />
              <span class="font-semibold text-gray-800 dark:text-gray-200">Apagar só para mim</span>
            </label>
          </div>
        </div>
        <div class="w-full flex justify-end gap-2 bg-gray-100 px-6 py-4">
          <button
            @click="emitDelete"
            class="px-4 py-2 font-semibold bg-red-700 text-white rounded-lg hover:bg-red-800 cursor-pointer transition-colors"
          >
            Apagar
          </button>
          <button @click="$emit('close-modal')"
            class="px-4 py-2 bg-gray-100 font-semibold dark:bg-gray-700 text-gray-800 dark:text-gray-200 rounded-lg cursor-pointer hover:bg-gray-200 dark:hover:bg-gray-600 transition-colors">
            Cancelar
          </button>
        </div>
      </div>
</template>

<script setup>
import { ref } from 'vue'
const emit = defineEmits(['delete-message', 'close-modal']);

const deleteOption = ref('ALL'); // Opção padrão para apagar para todos

function emitDelete() {
  // Emite o tipo de deleção selecionado
  // O payload pode ser 'all' ou 'me'
  // O componente pai deve tratar o valor recebido
  // Exemplo: @delete-message="handleDelete($event)"
  // $event será 'all' ou 'me'
  // Para manter compatibilidade, se não quiser payload, remova deleteOption.value
  // $emit('delete-message')
  // Aqui envia o valor selecionado:
  // eslint-disable-next-line vue/require-explicit-emits
  // eslint-disable-next-line no-undef
  emit('delete-message', deleteOption.value)
}
</script>
