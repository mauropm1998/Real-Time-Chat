<template>
  <div class="flex flex-col items-center w-full max-w-sm px-4 mx-auto my-8">
    <div v-if="showIcon" class="p-3 mx-auto text-blue-500 bg-blue-100 dark:bg-gray-800 rounded-full">
      <svg
        xmlns="http://www.w3.org/2000/svg"
        fill="none"
        viewBox="0 0 24 24"
        stroke-width="1.5"
        stroke="currentColor"
        class="w-6 h-6"
      >
        <path
          stroke-linecap="round"
          stroke-linejoin="round"
          d="M21 21l-5.197-5.197m0 0A7.5 7.5 0 105.196 5.196a7.5 7.5 0 0010.607 10.607z"
        />
      </svg>
    </div>
    <h1 class="mt-3 font-semibold text-center text-base text-gray-800 dark:text-white/90">
      {{ title }}
    </h1>
    <p class="mt-2 text-gray-500 text-[.9rem] text-center dark:text-white/50">
      {{ description }}
    </p>
    <div class="flex items-center mt-4 sm:mx-auto gap-x-3">
      <button
        v-if="false"
        class="w-1/2 px-5 py-2 text-sm text-gray-700 transition-colors duration-200 bg-white border rounded-lg sm:w-auto hover:bg-gray-100"
      >
        Clear Search
      </button>

      <button
        v-if="showButton"
        @click="$emit('add')"
        class="flex items-center cursor-pointer justify-center w-1/2 px-5 py-2 text-sm font-semibold tracking-wide text-blue-500 transition-colors duration-200 bg-blue-50 rounded-lg shrink-0 sm:w-auto gap-x-2 hover:bg-blue-100"
      >
        <svg
          xmlns="http://www.w3.org/2000/svg"
          fill="none"
          viewBox="0 0 24 24"
          stroke-width="1.5"
          stroke="currentColor"
          class="w-5 h-5"
        >
          <path
            stroke-linecap="round"
            stroke-linejoin="round"
            d="M12 9v6m3-3H9m12 0a9 9 0 11-18 0 9 9 0 0118 0z"
          />
        </svg>

        <span>{{ buttonText }}</span>
      </button>
    </div>
  </div>
</template>

<script setup>
defineEmits(["add"]);
defineProps({
  title: {
    type: String,
  },
  description: {
    type: String,
  },
  showButton: {
    type: Boolean,
    default: false,
  },
  buttonText: {
    type: String,
  },
  showIcon: {
    type: Boolean,
    default: true,
  },
});
</script>
