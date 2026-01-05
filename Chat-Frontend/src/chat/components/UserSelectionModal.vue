<template>
  <Teleport to="body">
    <div v-if="show" class="fixed inset-0 bg-black/70 dark:bg-black/50 flex items-center justify-center z-50">
      <div
        class="bg-white dark:bg-gray-900 w-full h-full max-w-lg md:h-[80vh] md:max-h-[80vh] md:rounded-xl flex flex-col">
        <div class="flex justify-between items-center p-4">
          <div class="flex items-center gap-3">
            <UserIcon class="w-6 h-6 text-primary" />
            <h2 class="text-xl font-semibold text-gray-900 dark:text-white">Nova Conversa</h2>
          </div>
          <button @click="$emit('close')"
            class="p-2 rounded-full cursor-pointer hover:bg-gray-100 dark:hover:bg-gray-800 transition-colors">
            <XMarkIcon class="w-6 h-6 text-gray-500 dark:text-gray-400" />
          </button>
        </div>

        <div class="pb-4 px-4">
          <div class="relative">
            <MagnifyingGlassIcon class="absolute left-3 top-1/2 -translate-y-1/2 w-5 h-5 text-gray-400" />
            <input type="text" placeholder="Pesquisar usuários..." v-model="searchQuery" @input="getAllUsersByName"
              class="w-full pl-10 pr-4 py-2 rounded-full bg-gray-50 dark:bg-gray-800 focus:outline-none border-0 focus:ring-1 focus:ring-primary dark:text-white" />
          </div>
        </div>

        <div class="overflow-y-auto p-2">
          <div v-if="!fetchingData && users.length > 0">
            <div v-for="user in users" :key="user.id"
              class="flex items-center p-3 rounded-lg cursor-pointer hover:bg-gray-100 dark:hover:bg-gray-800"
              @click="selectUser(user)">
              <UserPhoto src="/teste.jpg" :alt="user.fullName" :online="user.online" size="w-10 h-10" />
              <div class="w-full flex flex-col">
                <h2 class="ml-3 font-semibold text-gray-800 dark:text-white/90">{{ user.fullName
                  }}</h2>
                <span class="ml-3 text-sm font-light text-gray-600 dark:text-white/60">{{ user.email
                  }}</span>
              </div>
            </div>
          </div>
          <NoData v-if="!fetchingData && users.length === 0 && searchQuery.length === 0"
            title="Nenhum Utilizador Encontrado"
            description="Ainda não há utilizadores para mostrar. Serão mostrados aqui assim que existirem" />
          <NoData v-if="!fetchingData && users.length === 0 && searchQuery.length > 0"
            title="Nenhum Resultado Encontrado"
            description="A sua pesquisa não encontrou resultados. Tente pesquisar por algo diferente" />
          <div v-if="fetchingData">
            <UserSelectionPlaceholder v-for="user in 4" :key="user" />
          </div>
        </div>
      </div>
    </div>
  </Teleport>
</template>

<script setup>
import { ref, computed, watch, onMounted } from 'vue';
import { XMarkIcon, MagnifyingGlassIcon, UserIcon } from '@heroicons/vue/24/outline';
import { useRouter } from 'vue-router';
import { useWindowSize } from '@/composables/useWindowSize';
import OnlineStatus from './OnlineStatus.vue';
import UserSelectionPlaceholder from './UserSelectionPlaceholder.vue';
import NoData from '@/components/NoData.vue';
import { getUsers, getUsersByName } from '@/services/api/userApi';
import { saveChat } from '@/services/api/chatApi';
import { UserService } from '@/services/user/UserService';
import { useChatStore } from '@/stores/chatStore';
import UserPhoto from '@/components/UserPhoto.vue';

const props = defineProps({
  show: Boolean
});

const emit = defineEmits(['close', 'create-chat']);

const userService = new UserService();
const searchQuery = ref('');
const chatStore = useChatStore();
const router = useRouter();
const { width } = useWindowSize();
const isMobile = computed(() => width.value < 768);
const alreadyFetched = ref(false);
const fetchingData = ref(false);
const users = ref([])

const selectUser = (user) => {
  emit('create-chat', { senderId: userService.authenticatedUser.id, receiverId: user.id });
};

const getAllUsers = async () => {
  fetchingData.value = true;

  getUsers()
    .then(response => {
      users.value = response.data;
      alreadyFetched.value = true;
    })
    .catch(error => {
      console.error('Error fetching users:', error);
    })
    .finally(() => {
      fetchingData.value = false;
    });
};

const getAllUsersByName = () => {
  fetchingData.value = true;

  getUsersByName(searchQuery.value)
    .then(response => {
      users.value = response.data;
    })
    .catch(error => {
      console.error('Error fetching users:', error);
    })
    .finally(() => {
      fetchingData.value = false;
    });
};

watch(() => props.show, (newValue) => {
  if (newValue && !alreadyFetched.value) {
    getAllUsers();
  }
});

onMounted(() => {
  getAllUsers();
});

</script>