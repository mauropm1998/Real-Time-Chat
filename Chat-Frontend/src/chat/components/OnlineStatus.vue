<script setup>
defineProps({
  isOnline: {
    type: Boolean,
    required: true
  }
});
</script>

<template>
  <div 
    class="absolute bottom-0.5 -right-0 w-3 h-3 rounded-full border-2 border-white dark:border-gray-900"
    :class="isOnline ? 'bg-green-600' : 'bg-gray-400'"
  ></div>
</template>