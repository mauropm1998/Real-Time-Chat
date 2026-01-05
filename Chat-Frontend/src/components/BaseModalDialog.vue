<template>
  <Teleport v-if="to === 'BODY' || to === 'body'" to="body">
    <Transition name="modal-outer">
      <div
        style="transition: all 0.4s cubic-bezier(0.175, 0.885, 0.32, 1.275)"
        v-show="openModal"
        class="fixed bg-black/30 h-full w-full top-0 left-0 flex justify-center items-center z-[200] modal-content overflow-auto"
        @click="closeOutside"
      >
        <Transition name="modal-inner">
          <div v-if="openModal" class="bg-white flex w-[550px] rounded-md">
            <div
              class="relative inline-block px-4 py-6 overflow-hidden text-left align-bottom transition-all transform bg-white rounded-lg shadow-xl rtl:text-right dark:bg-gray-900 w-full"
            >
              <div class="flex">
                <div class="flex items-center justify-center w-[60px] mr-3">
                  <i
                    v-if="type == 'WARNING'"
                    class="bi bi-exclamation-circle-fill h-[40px] w-[40px] bg-yellow-100 text-yellow-600 rounded-full flex items-center justify-center text-[1.5rem]"
                  ></i>
                  <i
                    v-if="type == 'DANGER'"
                    class="bi bi-dash-circle-fill h-[40px] w-[40px] bg-red-100 text-red-500 rounded-full flex items-center justify-center text-[1.5rem]"
                  ></i>
                  <i
                    v-if="type == 'SUCCESS'"
                    class="bi bi-check-circle-fill h-[40px] w-[40px] bg-emerald-100 text-emerald-600 rounded-full flex items-center justify-center text-[1.5rem]"
                  ></i>
                  <i
                    v-if="type == 'INFO'"
                    class="bi bi-info-circle-fill h-[40px] w-[40px] bg-blue-100 text-blue-500 rounded-full flex items-center justify-center text-[1.5rem]"
                  ></i>
                </div>
                <div class="text-left">
                  <h3
                    class="text-[.95rem] font-bold leading-6 text-gray-800 dark:text-white"
                    id="modal-title"
                  >
                    {{ title }}
                  </h3>
                  <p class="mt-0.5 text-[.95rem] text-gray-600 dark:text-gray-400">
                    <slot />
                  </p>
                </div>
              </div>

              <div class="mt-7 flex justify-between">
                <div class="flex items-center">
                  <div v-if="enableRemember" class="flex items-center">
                    <input
                      v-model="remember"
                      checked
                      id="checked-checkbox"
                      type="checkbox"
                      value=""
                      class="w-4 h-4 text-blue-600 bg-gray-100 border-gray-300 rounded focus:ring-blue-500 dark:focus:ring-blue-600 dark:ring-offset-gray-800 focus:ring-2 dark:bg-gray-700 dark:border-gray-600"
                    />
                    <label
                      for="checked-checkbox"
                      class="ml-2 text-sm font-medium text-gray-900 dark:text-gray-300"
                      >Lembrar</label
                    >
                  </div>
                </div>
                <div v-if="buttons !== 'NO_BUTTONS'" class="flex items-center gap-x-2">
                  <button
                    @click="$emit('close-modal')"
                    v-if="buttons === 'YES_NO_CANCEL'"
                    class="w-full px-3 py-2 text-[.8rem] cursor-pointer font-medium tracking-wide text-gray-700 capitalize transition-colors duration-300 transform border border-gray-200 rounded-md dark:text-gray-200 dark:border-gray-700 dark:hover:bg-gray-800 hover:bg-gray-100 focus:outline-none focus:ring focus:ring-gray-300 focus:ring-opacity-40"
                  >
                    Cancelar
                  </button>
                  <button
                    @click="$emit('no', remember)"
                    v-if="buttons === 'YES_NO_CANCEL' || buttons === 'YES_NO'"
                    class="w-full px-3 py-2 text-[.8rem] cursor-pointer font-medium tracking-wide text-gray-700 capitalize transition-colors duration-300 transform border border-gray-200 rounded-md dark:text-gray-200 dark:border-gray-700 dark:hover:bg-gray-800 hover:bg-gray-100 focus:outline-none focus:ring focus:ring-gray-300 focus:ring-opacity-40"
                  >
                    Não
                  </button>
                  <button
                    @click="$emit('close-modal')"
                    v-if="buttons === 'OK' && type === 'SUCCESS'"
                    class="w-full px-3 py-2 text-[.8rem] cursor-pointer font-medium tracking-wide text-white capitalize transition-colors duration-300 transform bg-emerald-600 rounded-md hover:bg-emerald-500 focus:outline-none focus:ring focus:ring-emerald-300 focus:ring-opacity-40"
                  >
                    OK
                  </button>
                  <button
                    @click="$emit('close-modal')"
                    v-if="buttons === 'OK' && type === 'WARNING'"
                    class="w-full px-3 py-2 text-[.8rem] cursor-pointer font-medium tracking-wide text-white capitalize transition-colors duration-300 transform bg-yellow-600 rounded-md hover:bg-yellow-500 focus:outline-none focus:ring focus:ring-yellow-300 focus:ring-opacity-40"
                  >
                    OK
                  </button>
                  <button
                    @click="$emit('close-modal')"
                    v-if="buttons === 'OK' && type === 'DANGER'"
                    class="w-full px-3 py-2 text-[.8rem] cursor-pointer font-medium tracking-wide text-white capitalize transition-colors duration-300 transform bg-red-600 rounded-md hover:bg-red-500 focus:outline-none focus:ring focus:ring-red-300 focus:ring-opacity-40"
                  >
                    OK
                  </button>
                  <button
                    @click="$emit('close-modal')"
                    v-if="buttons === 'OK' && type === 'INFO'"
                    class="w-full px-3 py-2 text-[.8rem] cursor-pointer font-medium tracking-wide text-white capitalize transition-colors duration-300 transform bg-blue-600 rounded-md hover:bg-blue-500 focus:outline-none focus:ring focus:ring-blue-300 focus:ring-opacity-40"
                  >
                    OK
                  </button>
                  <button
                    @click="$emit('yes', remember)"
                    v-if="
                      (buttons === 'YES_NO_CANCEL' || buttons === 'YES_NO') &&
                      type === 'SUCCESS'
                    "
                    class="w-full px-3 py-2 text-[.8rem] cursor-pointer font-medium tracking-wide text-white capitalize transition-colors duration-300 transform bg-emerald-600 rounded-md hover:bg-emerald-500 focus:outline-none focus:ring focus:ring-emerald-300 focus:ring-opacity-40"
                  >
                    Sim
                  </button>
                  <button
                    @click="$emit('yes', remember)"
                    v-if="
                      (buttons === 'YES_NO_CANCEL' || buttons === 'YES_NO') &&
                      type === 'WARNING'
                    "
                    class="w-full px-3 py-2 text-[.8rem] cursor-pointer font-medium tracking-wide text-white capitalize transition-colors duration-300 transform bg-yellow-600 rounded-md hover:bg-yellow-500 focus:outline-none focus:ring focus:ring-yellow-300 focus:ring-opacity-40"
                  >
                    Sim
                  </button>
                  <button
                    @click="$emit('yes', remember)"
                    v-if="
                      (buttons === 'YES_NO_CANCEL' || buttons === 'YES_NO') &&
                      type === 'DANGER'
                    "
                    class="w-full px-3 py-2 text-[.8rem] cursor-pointer font-medium tracking-wide text-white capitalize transition-colors duration-300 transform bg-red-600 rounded-md hover:bg-red-500 focus:outline-none focus:ring focus:ring-red-300 focus:ring-opacity-40"
                  >
                    Sim
                  </button>
                  <button
                    @click="$emit('yes', remember)"
                    v-if="
                      (buttons === 'YES_NO_CANCEL' || buttons === 'YES_NO') &&
                      type === 'INFO'
                    "
                    class="w-full px-3 py-2 text-[.8rem] cursor-pointer font-medium tracking-wide text-white capitalize transition-colors duration-300 transform bg-blue-600 rounded-md hover:bg-blue-500 focus:outline-none focus:ring focus:ring-blue-300 focus:ring-opacity-40"
                  >
                    Sim
                  </button>
                </div>
              </div>
            </div>
          </div>
        </Transition>
      </div>
    </Transition>
  </Teleport>
</template>

<script setup>
import { ref } from "vue";

const closeOutside = (click) => {
  if (props.enableCloseOutside) {
    if (click.target.classList.contains("modal-content")) {
      emit("close-modal");
    }
  }
};

const remember = ref(false);

const emit = defineEmits(["close-modal", "yes", "no", "ok"]);

const props = defineProps({
  openModal: {
    type: Boolean,
    default: false,
  },
  type: {
    type: String,
    default: "INFO", // INFO, SUCCESS, WARNING, DANGER
  },
  buttons: {
    type: String,
    default: "OK", // OK, YES_NO, YES_NO_CANCEL, NO_BUTTONS
  },
  title: String,
  enableRemember: {
    type: Boolean,
    default: false,
  },
  enableCloseOutside: {
    type: Boolean,
    default: true,
  },
  to: {
    type: String,
    default: "BODY",
  },
});
</script>

<style scoped>
.modal-outer-enter-active {
  transition: opacity 0.3s cubic-bezier(0.075, 0.82, 0.165, 1);
}
.modal-outer-leave-active {
  transition: opacity 0.3s cubic-bezier(0.075, 0.82, 0.165, 1);
}

.modal-outer-enter-from,
.modal-outer-leave-to {
  opacity: 0;
}

.modal-inner-enter-active {
  transition: all 0.3s cubic-bezier(0.075, 0.82, 0.165, 1) 0.15s;
}
.modal-inner-leave-active {
  transition: opacity 0.3s cubic-bezier(0.075, 0.82, 0.165, 1);
}

.modal-inner-enter-from {
  opacity: 0;
  transform: scale(0.8);
}
.modal-inner-leave-to {
  opacity: 0;
  transform: scale(0.8);
}
</style>
