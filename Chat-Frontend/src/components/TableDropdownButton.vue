<template>
  <div class="relative">
    <button
      type="button"
      @click="showDropdown = !showDropdown"
      class="text-gray-400 hover:text-gray-600 cursor-pointer hover:bg-white rounded-lg py-1 px-1.5"
    >
      <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
        <path
          stroke-linecap="round"
          stroke-linejoin="round"
          stroke-width="2"
          d="M12 5v.01M12 12v.01M12 19v.01M12 6a1 1 0 110-2 1 1 0 010 2zm0 7a1 1 0 110-2 1 1 0 010 2zm0 7a1 1 0 110-2 1 1 0 010 2z"
        />
      </svg>
    </button>
    <div
      v-if="showDropdown"
      class="absolute z-10 mt-1 bg-white shadow-lg rounded-lg py-1 max-w-[200px] right-0 w-auto"
    >
      <div v-if="showPreview" class="cursor-pointer hover:bg-gray-50 px-6 py-3 text-left">
        <div @click="onPreview" class="text-[.9rem] font-medium text-gray-900">
          {{ previewText }}
        </div>
      </div>
      <div v-if="showEdit" class="cursor-pointer hover:bg-gray-50 px-6 py-3 text-left">
        <div @click="onEdit" class="text-[.9rem] font-medium text-gray-900">Editar</div>
      </div>
      <div v-if="showDelete" class="cursor-pointer hover:bg-gray-50 px-6 py-3 text-left">
        <div @click="onDelete" class="text-[.9rem] font-medium text-gray-900">
          Eliminar
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { onMounted, ref } from "vue";

// Estado do formulário
const showDropdown = ref(false);
const emit = defineEmits(["edit", "delete", "preview"]);
defineProps({
  showEdit: {
    type: Boolean,
    default: true,
  },
  showDelete: {
    type: Boolean,
    default: true,
  },
  showPreview: {
    type: Boolean,
    default: false,
  },
  previewText: {
    type: String,
    default: "Previsualizar",
  },
});

const onEdit = () => {
  showDropdown.value = false;
  emit("edit");
};
const onDelete = () => {
  showDropdown.value = false;
  emit("delete");
};
const onPreview = () => {
  showDropdown.value = false;
  emit("preview");
};

onMounted(() => {
  document.addEventListener("click", (e) => {
    if (!e.target.closest(".relative")) {
      showDropdown.value = false;
    }
  });
});
</script>
