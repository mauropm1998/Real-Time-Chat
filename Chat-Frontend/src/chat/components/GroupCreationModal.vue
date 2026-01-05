<template>
    <Teleport to="body">
        <div v-if="show" class="fixed inset-0 bg-black/50 flex items-center justify-center z-50">
            <div
                class="bg-white dark:bg-gray-900 w-full h-full max-w-lg md:h-auto md:min-h-[50vh] md:max-h-[85vh] md:rounded-xl flex flex-col">
                <!-- Header -->
                <div class="flex justify-between items-center p-6 border-b border-gray-100 dark:border-gray-800">
                    <div class="flex items-center gap-3">
                        <UserGroupIcon class="w-6 h-6 text-primary" />
                        <h2 class="text-xl font-semibold text-gray-900 dark:text-white">
                            {{ currentStep === 1 ? 'Criar Grupo' : 'Adicionar Membros' }}
                        </h2>
                    </div>
                    <button @click="closeModal" :disabled="isCreatingGroup"
                        class="p-2 rounded-full hover:bg-gray-100 dark:hover:bg-gray-800 transition-colors">
                        <XMarkIcon class="w-6 h-6 text-gray-500 dark:text-gray-400" />
                    </button>
                </div>

                <!-- Step 1: Group Information -->
                <div v-if="currentStep === 1" class="flex-1 overflow-y-auto p-6">
                    <div class="space-y-6">
                        <!-- Group Photo -->
                        <div class="flex flex-col items-center">
                            <div class="relative">
                                <div
                                    class="w-24 h-24 rounded-full bg-gray-200 dark:bg-gray-700 flex items-center justify-center overflow-hidden">
                                    <img v-if="groupPhotoPreview" :src="groupPhotoPreview" alt="Foto do grupo"
                                        class="w-full h-full object-cover" />
                                    <UserGroupIcon v-else class="w-10 h-10 text-gray-400" />
                                </div>
                                <button
                                    class="absolute -bottom-1 -right-1 w-8 h-8 cursor-pointer bg-primary rounded-full flex items-center justify-center hover:bg-primary-600 transition-colors"
                                    @click="$refs.photoInput.click()">
                                    <PhotoIcon class="w-4 h-4 text-white" />
                                </button>
                                <button v-if="groupPhotoPreview" @click="removePhoto"
                                    class="absolute -top-1 -right-1 w-6 h-6 bg-red-500 rounded-full flex items-center justify-center hover:bg-red-600 transition-colors">
                                    <XMarkIcon class="w-3 h-3 text-white" />
                                </button>
                            </div>
                            <input ref="photoInput" type="file" accept="image/*" class="hidden"
                                @change="handlePhotoUpload" />
                            <p class="text-sm text-gray-500 dark:text-gray-400 mt-2">Toque para adicionar foto</p>
                        </div>

                        <!-- Group Name -->
                        <div>
                            <label class="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-2">
                                Nome do Grupo *
                            </label>
                            <input v-model="groupName" type="text" placeholder="Digite o nome do grupo" maxlength="50"
                                class="w-full px-4 py-3 border border-gray-300 dark:border-gray-600 rounded-lg focus:outline-none bg-white dark:bg-gray-800 text-gray-900 dark:text-white" />
                            <p class="text-xs text-gray-500 dark:text-gray-400 mt-1">
                                {{ groupName.length }}/50 caracteres
                            </p>
                        </div>

                        <!-- Group Description -->
                        <div>
                            <label class="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-2">
                                Descrição (opcional)
                            </label>
                            <textarea v-model="groupDescription" placeholder="Descreva o propósito do grupo"
                                maxlength="200" rows="3"
                                class="w-full px-4 py-3 border border-gray-300 dark:border-gray-600 rounded-lg focus:outline-none bg-white dark:bg-gray-800 text-gray-900 dark:text-white resize-none"></textarea>
                            <p class="text-xs text-gray-500 dark:text-gray-400 mt-1">
                                {{ groupDescription.length }}/200 caracteres
                            </p>
                        </div>
                    </div>
                </div>

                <!-- Step 2: Add Members -->
                <div v-if="currentStep === 2" class="flex-1 overflow-hidden flex flex-col">
                    <!-- Search -->
                    <div class="p-6 border-b border-gray-200 dark:border-gray-700">
                        <div class="relative">
                            <MagnifyingGlassIcon
                                class="absolute left-3 top-1/2 -translate-y-1/2 w-5 h-5 text-gray-400" />
                            <input v-model="searchQuery" type="text" placeholder="Pesquisar usuários..."
                                class="w-full pl-10 pr-4 py-3 border border-gray-300 dark:border-gray-600 rounded-lg focus:outline-none bg-white dark:bg-gray-800 text-gray-900 dark:text-white" />
                        </div>
                    </div>

                    <!-- Selected Members -->
                    <div v-if="selectedUsers.length > 0"
                        class="px-6 py-4 border-b border-gray-200 dark:border-gray-700">
                        <p class="text-sm font-medium text-gray-700 dark:text-gray-300 mb-3">
                            Membros selecionados ({{ selectedUsers.length }})
                        </p>
                        <div class="flex flex-wrap gap-2">
                            <div v-for="user in selectedUsers" :key="user.id"
                                class="flex items-center gap-2 bg-primary-light dark:bg-primary/20 px-3 py-1 rounded-full">
                                <UserPhoto src="/teste.jpg" :alt="user.fullName" :online="user.isOnline"
                                    :showOnlineStatus="false" size="w-6 h-6" />
                                <span class="text-sm text-primary dark:text-primary-light">{{ user.fullName }}</span>
                                <button @click="toggleUserSelection(user)"
                                    class="w-4 h-4 rounded-full bg-primary hover:bg-primary-600 flex items-center justify-center transition-colors">
                                    <XMarkIcon class="w-2 h-2 text-white" />
                                </button>
                            </div>
                        </div>
                    </div>

                    <!-- Available Users -->
                    <div class="flex-1 overflow-y-auto p-6">
                        <p class="text-sm font-medium text-gray-700 dark:text-gray-300 mb-4">
                            Colaboradores disponíveis
                        </p>
                        <div class="space-y-2">
                            <div v-for="user in filteredUsers" :key="user.id"
                                class="flex items-center justify-between p-3 rounded-lg cursor-pointer hover:bg-gray-100 dark:hover:bg-gray-800 transition-colors"
                                @click="toggleUserSelection(user)">
                                <div class="flex items-center gap-3">
                                    <UserPhoto src="/teste.jpg" :alt="user.fullName" :online="user.isOnline"
                                        size="w-10 h-10" />
                                    <span class="font-medium text-sm text-gray-900 dark:text-white">{{ user.fullName
                                        }}</span>
                                </div>
                                <div class="w-6 h-6 rounded-full border-2 flex items-center justify-center transition-colors"
                                    :class="isUserSelected(user)
                                        ? 'bg-primary border-primary'
                                        : 'border-gray-300 dark:border-gray-600'">
                                    <CheckIcon v-if="isUserSelected(user)" class="w-4 h-4 text-white" />
                                </div>
                            </div>
                        </div>
                    </div>
                </div>

                <!-- Footer -->
                <div class="p-6">
                    <div class="flex justify-between gap-3">
                        <button v-if="currentStep === 2" @click="prevStep" :disabled="isCreatingGroup"
                            class="px-6 py-3 border border-gray-300 dark:border-gray-600 cursor-pointer text-gray-700 dark:text-gray-300 rounded-lg hover:bg-gray-50 dark:hover:bg-gray-800 transition-colors disabled:opacity-50 disabled:cursor-not-allowed">
                            <ArrowLeftIcon class="w-5 h-5 inline-block mr-1" />
                        </button>
                        <div class="flex gap-3 ml-auto">
                            <button @click="closeModal" :disabled="isCreatingGroup"
                                class="px-6 py-3 border border-gray-300 dark:border-gray-600 cursor-pointer text-gray-700 dark:text-gray-300 rounded-lg hover:bg-gray-50 dark:hover:bg-gray-800 transition-colors disabled:opacity-50 disabled:cursor-not-allowed">
                                Cancelar
                            </button>
                            <button v-if="currentStep === 1" @click="nextStep" :disabled="!canProceedToStep2"
                                class="px-6 py-3 bg-primary text-white rounded-lg cursor-pointer hover:bg-primary-600 disabled:opacity-50 disabled:cursor-not-allowed transition-colors">
                                Próximo
                            </button>
                            <button v-if="currentStep === 2" @click="createGroup"
                                :disabled="!canCreateGroup || isCreatingGroup"
                                class="px-6 py-3 flex bg-primary text-white rounded-lg cursor-pointer hover:bg-primary-600 disabled:opacity-50 disabled:cursor-not-allowed transition-colors">
                                <span v-if="!isCreatingGroup">Criar Grupo</span>
                                <span v-else class="loader"></span>
                            </button>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </Teleport>
</template>

<script setup>
import { ref, computed, onMounted, onUnmounted } from 'vue';
import {
    XMarkIcon,
    PhotoIcon,
    MagnifyingGlassIcon,
    CheckIcon,
    UserGroupIcon,
    PlusIcon,
    ArrowLeftIcon
} from '@heroicons/vue/24/outline';
import { useRouter } from 'vue-router';
import OnlineStatus from './OnlineStatus.vue';
import { useWindowSize } from '@/composables/useWindowSize';
import { getChatById, saveGroup } from '@/services/api/chatApi';
import { getUsers } from '@/services/api/userApi';
import UserPhoto from '@/components/UserPhoto.vue';
import { useChatStore } from '@/stores/chatStore';

const props = defineProps({
    show: Boolean
});

const emit = defineEmits(['close']);
const chatStore = useChatStore();
const router = useRouter();
const { width } = useWindowSize();
const isMobile = computed(() => width.value < 768);
const isCreatingGroup = ref(false);

// Form data
const groupName = ref('');
const groupDescription = ref('');
const groupPhoto = ref(null);
const groupPhotoPreview = ref(null);
const searchQuery = ref('');
const selectedUsers = ref([]);
const currentStep = ref(1); // 1: Group Info, 2: Add Members

// Available users (mock data - replace with your actual user data)
const availableUsers = ref([]);

const filteredUsers = computed(() => {
    const query = searchQuery.value.toLowerCase();
    return availableUsers.value.filter(user =>
        user.fullName.toLowerCase().includes(query) &&
        !selectedUsers.value.some(selected => selected.id === user.id)
    );
});

const canProceedToStep2 = computed(() => {
    return groupName.value.trim().length >= 3;
});

const canCreateGroup = computed(() => {
    return groupName.value.trim().length >= 3 && selectedUsers.value.length >= 2;
});

const handlePhotoUpload = (event) => {
    const file = event.target.files[0];
    if (file) {
        if (file.size > 5 * 1024 * 1024) { // 5MB limit
            alert('A imagem deve ter no máximo 5MB');
            return;
        }

        if (!file.type.startsWith('image/')) {
            alert('Por favor, selecione apenas arquivos de imagem');
            return;
        }

        groupPhoto.value = file;
        groupPhotoPreview.value = URL.createObjectURL(file);
    }
};

const removePhoto = () => {
    if (groupPhotoPreview.value) {
        URL.revokeObjectURL(groupPhotoPreview.value);
    }
    groupPhoto.value = null;
    groupPhotoPreview.value = null;
};

const toggleUserSelection = (user) => {
    const index = selectedUsers.value.findIndex(selected => selected.id === user.id);
    if (index > -1) {
        selectedUsers.value.splice(index, 1);
    } else {
        selectedUsers.value.push(user);
    }
};

const isUserSelected = (user) => {
    return selectedUsers.value.some(selected => selected.id === user.id);
};

const nextStep = () => {
    if (canProceedToStep2.value) {
        currentStep.value = 2;
    }
};

const prevStep = () => {
    currentStep.value = 1;
};

const createGroup = () => {
    if (!canCreateGroup.value) return;

    isCreatingGroup.value = true;
    const formData = new FormData();

    formData.append('name', groupName.value);
    formData.append('description', groupDescription.value);

    if (groupPhoto.value) {
        formData.append('photo', groupPhoto.value);
    }

    // Adiciona cada ID de membro como um campo separado para o Spring Boot
    const memberIds = selectedUsers.value.map(user => user.id);
    memberIds.forEach(id => {
        formData.append('memberIds', id);
    });

    saveGroup(formData)
        .then(response => {
            getChatById(response.data)
                .then(chatResponse => {
                    chatStore.setLastActiveChat(chatResponse.data);
                    chatStore.chats.unshift(chatResponse.data);
                    // Handle successful group creation
                    isCreatingGroup.value = false;
                    resetForm();
                    emit('close');
                    router.push({ name: 'chat-view', params: { chatId: response.data } });

                    setTimeout(() => {
                        emitter.emit("show-chat-mobile", true)
                    }, 500)
                })
                .catch(error => {
                    console.error('Error fetching chat by ID:', error);
                });
        })
        .catch(error => {
            console.error('Erro ao criar grupo:', error);
        })
};

const resetForm = () => {
    groupName.value = '';
    groupDescription.value = '';
    groupPhoto.value = null;
    if (groupPhotoPreview.value) {
        URL.revokeObjectURL(groupPhotoPreview.value);
    }
    groupPhotoPreview.value = null;
    searchQuery.value = '';
    selectedUsers.value = [];
    currentStep.value = 1;
};

const closeModal = () => {
    resetForm();
    emit('close');
};

const getAllUsers = async () => {
    getUsers()
        .then(response => {
            availableUsers.value = response.data;
        })
        .catch(error => {
            console.error('Error fetching users:', error);
        })
        .finally(() => {
        });
};

const handleKeydown = (event) => {
    if (!props.show) return;

    if (event.key === 'Escape') {
        closeModal();
    }
};

onMounted(() => {
    getAllUsers();
    document.addEventListener('keydown', handleKeydown);
});

onUnmounted(() => {
    document.removeEventListener('keydown', handleKeydown);
    if (groupPhotoPreview.value) {
        URL.revokeObjectURL(groupPhotoPreview.value);
    }
});

</script>
<style scoped>
.loader {
    width: 25px;
    height: 25px;
    border-radius: 50%;
    position: relative;
    animation: rotate 1s linear infinite
}

.loader::before {
    content: "";
    box-sizing: border-box;
    position: absolute;
    inset: 0px;
    border-radius: 50%;
    border: 5px solid #FFF;
    animation: prixClipFix 2s linear infinite;
}

@keyframes rotate {
    100% {
        transform: rotate(360deg)
    }
}

@keyframes prixClipFix {
    0% {
        clip-path: polygon(50% 50%, 0 0, 0 0, 0 0, 0 0, 0 0)
    }

    25% {
        clip-path: polygon(50% 50%, 0 0, 100% 0, 100% 0, 100% 0, 100% 0)
    }

    50% {
        clip-path: polygon(50% 50%, 0 0, 100% 0, 100% 100%, 100% 100%, 100% 100%)
    }

    75% {
        clip-path: polygon(50% 50%, 0 0, 100% 0, 100% 100%, 0 100%, 0 100%)
    }

    100% {
        clip-path: polygon(50% 50%, 0 0, 100% 0, 100% 100%, 0 100%, 0 0)
    }
}
</style>