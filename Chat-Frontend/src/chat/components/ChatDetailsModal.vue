<template>
  <Teleport to="body">
    <div v-if="show && chat" class="fixed inset-0 bg-black/50 flex items-center justify-center z-50">
      <div
        class="bg-white dark:bg-gray-900 rounded-xl w-[90%] max-w-4xl md:h-[80vh] md:max-h-[80vh] md:rounded-xl flex flex-col">
        <!-- Header -->
        <div class="flex justify-between items-center p-6 border-b border-gray-200 dark:border-gray-700">
          <h2 class="text-xl font-semibold text-gray-900 dark:text-white">
            {{ chat.type === 'GROUP' ? 'Detalhes do Grupo' : 'Detalhes da Conversa' }}
          </h2>
          <button @click="$emit('close')"
            class="p-2 rounded-full hover:bg-gray-100 cursor-pointer dark:hover:bg-gray-800 transition-colors">
            <XMarkIcon class="w-6 h-6 text-gray-500 dark:text-gray-400" />
          </button>
        </div>

        <div class="flex flex-1 overflow-hidden">
          <!-- Sidebar -->
          <div class="w-64 border-r border-gray-200 dark:border-gray-700 bg-gray-50 dark:bg-gray-800">
            <nav class="p-4 space-y-2">
              <button @click="activeTab = 'info'"
                class="w-full flex items-center gap-3 px-3 py-2 rounded-lg text-left transition-colors"
                :class="activeTab === 'info' ? 'bg-primary text-white' : 'text-gray-700 dark:text-gray-300 hover:bg-gray-200 dark:hover:bg-gray-700'">
                <UserIcon class="w-5 h-5" />
                Informações
              </button>

              <button v-if="chat.type === 'GROUP'" @click="activeTab = 'members'"
                class="w-full flex items-center gap-3 px-3 py-2 rounded-lg text-left transition-colors"
                :class="activeTab === 'members' ? 'bg-primary text-white' : 'text-gray-700 dark:text-gray-300 hover:bg-gray-200 dark:hover:bg-gray-700'">
                <UserIcon class="w-5 h-5" />
                Membros
              </button>

              <button @click="activeTab = 'media'"
                class="w-full flex items-center gap-3 px-3 py-2 rounded-lg text-left transition-colors"
                :class="activeTab === 'media' ? 'bg-primary text-white' : 'text-gray-700 dark:text-gray-300 hover:bg-gray-200 dark:hover:bg-gray-700'">
                <PhotoIcon class="w-5 h-5" />
                Mídia
              </button>

              <button @click="activeTab = 'files'"
                class="w-full flex items-center gap-3 px-3 py-2 rounded-lg text-left transition-colors"
                :class="activeTab === 'files' ? 'bg-primary text-white' : 'text-gray-700 dark:text-gray-300 hover:bg-gray-200 dark:hover:bg-gray-700'">
                <DocumentIcon class="w-5 h-5" />
                Arquivos
              </button>

              <button @click="activeTab = 'links'"
                class="w-full flex items-center gap-3 px-3 py-2 rounded-lg text-left transition-colors"
                :class="activeTab === 'links' ? 'bg-primary text-white' : 'text-gray-700 dark:text-gray-300 hover:bg-gray-200 dark:hover:bg-gray-700'">
                <LinkIcon class="w-5 h-5" />
                Links
              </button>

              <button @click="activeTab = 'settings'"
                class="w-full flex items-center gap-3 px-3 py-2 rounded-lg text-left transition-colors"
                :class="activeTab === 'settings' ? 'bg-primary text-white' : 'text-gray-700 dark:text-gray-300 hover:bg-gray-200 dark:hover:bg-gray-700'">
                <CogIcon class="w-5 h-5" />
                Configurações
              </button>
            </nav>
          </div>

          <!-- Main Content -->
          <div class="flex-1 overflow-y-auto">
            <!-- Info Tab -->
            <div v-if="activeTab === 'info'" class="p-6">
              <div class="text-center mb-8">
                <div class="relative inline-block">
                  <UserPhoto v-if="chat.type === 'PRIVATE'" src="/teste.jpg" :alt="chat.name" :online="chat.userOnline"
                    size="w-24 h-24" />
                  <GroupPhoto v-else :src="chat.groupImageUrl" :photoType="chat.imageGroupType" :alt="chat.name"
                    :name="chat.name" size="w-24 h-24 text-3xl" />
                  <button v-if="chat.type === 'GROUP' && canEditGroup" @click="$refs.groupPhotoInput.click()"
                    class="absolute -bottom-1 -right-1 w-8 h-8 bg-primary rounded-full flex items-center justify-center hover:bg-primary-600 transition-colors">
                    <PhotoIcon class="w-4 h-4 text-white" />
                  </button>
                  <input ref="groupPhotoInput" type="file" accept="image/*" class="hidden"
                    @change="handlePhotoUpload" />
                </div>

                <div class="mt-2">
                  <div v-if="isEditingName" class="flex items-center gap-2 justify-center">
                    <input v-model="editedName" @keyup.enter="saveGroupName" @keyup.esc="isEditingName = false"
                      class="text-xl font-semibold text-center bg-transparent border-b-1 border-primary focus:outline-none text-gray-900 dark:text-white"
                      maxlength="50" />
                    <button @click="saveGroupName" class="text-green-500 hover:text-green-600">
                      <svg class="w-5 h-5" fill="currentColor" viewBox="0 0 20 20">
                        <path fill-rule="evenodd"
                          d="M16.707 5.293a1 1 0 010 1.414l-8 8a1 1 0 01-1.414 0l-4-4a1 1 0 011.414-1.414L8 12.586l7.293-7.293a1 1 0 011.414 0z"
                          clip-rule="evenodd"></path>
                      </svg>
                    </button>
                  </div>
                  <div v-else class="flex items-center gap-2 justify-center">
                    <h3 class="text-xl font-semibold text-gray-900 dark:text-white">{{ chat.name }}</h3>
                    <button v-if="chat.type === 'GROUP' && canEditGroup" @click="startEditingName"
                      class="text-gray-400 hover:text-gray-600 cursor-pointer dark:hover:text-gray-300">
                      <PencilIcon class="w-4 h-4" />
                    </button>
                  </div>
                  <p class="text-gray-500 dark:text-gray-400">{{ displayEmailOrGroupCreator }}</p>
                  <p class="text-gray-500 dark:text-gray-400 mt-1">{{ displayStatus }}</p>
                </div>
              </div>

              <!-- Group Description -->
              <div v-if="chat.type === 'GROUP'" class="mb-6">
                <h4 class="text-sm font-medium text-gray-700 dark:text-gray-300 mb-2">Descrição</h4>
                <div v-if="isEditingDescription" class="space-y-2">
                  <textarea v-model="editedDescription" @keyup.esc="isEditingDescription = false"
                    placeholder="Adicione uma descrição do grupo..." maxlength="200" rows="3"
                    class="w-full px-3 py-2 border border-gray-300 dark:border-gray-600 rounded-lg focus:ring-1 focus:outline-none focus:ring-primary focus:border-transparent bg-white dark:bg-gray-800 text-gray-900 dark:text-white resize-none"></textarea>
                  <div class="flex justify-end gap-2">
                    <button @click="saveGroupDescription"
                      class="px-3 py-1 text-sm bg-primary cursor-pointer text-white rounded hover:bg-primary-600">
                      Salvar
                    </button>
                    <button @click="isEditingDescription = false"
                      class="px-3 py-1 text-sm bg-gray-500 cursor-pointer text-white rounded hover:bg-gray-600">
                      Cancelar
                    </button>
                  </div>
                </div>
                <div v-else class="flex items-start gap-2">
                  <p class="text-gray-600 dark:text-gray-300 flex-1">
                    {{ chat.description || 'Nenhuma descrição adicionada.' }}
                  </p>
                  <button v-if="canEditGroup" @click="startEditingDescription"
                    class="text-gray-400 hover:text-gray-600 cursor-pointer dark:hover:text-gray-300 flex-shrink-0">
                    <PencilIcon class="w-4 h-4" />
                  </button>
                </div>
              </div>

              <!-- Creation Info -->
              <div class="space-y-4">
                <div v-if="chat.type === 'GROUP'" class="flex items-center gap-3">
                  <CalendarIcon class="w-5 h-5 text-gray-400" />
                  <div>
                    <p class="text-sm text-gray-600 dark:text-gray-300">Criado em</p>
                    <p class="text-sm font-medium text-gray-900 dark:text-white">
                      {{ format(new Date(chat.createdAt), 'dd/MM/yyyy \'às\' HH:mm', { locale: ptBR }) }}
                    </p>
                  </div>
                </div>

                <div v-if="chat.type === 'PRIVATE'" class="flex items-center gap-3">
                  <ClockIcon class="w-5 h-5 text-gray-400" />
                  <div>
                    <p class="text-sm text-gray-600 dark:text-gray-300">Última vez online</p>
                    <p class="text-sm font-medium text-gray-900 dark:text-white">{{ lastSeen }}</p>
                  </div>
                </div>
              </div>
            </div>

            <!-- Members Tab -->
            <div v-if="activeTab === 'members'" class="p-6">
              <div class="flex justify-between items-center mb-6">
                <h3 class="text-lg font-semibold text-gray-900 dark:text-white">
                  Membros ({{ chat.members.length }})
                </h3>
                <button v-if="canEditGroup" @click="showAddMemberModal = true"
                  class="flex items-center gap-2 px-4 py-2 bg-primary text-white rounded-lg hover:bg-primary-600 transition-colors">
                  <UserPlusIcon class="w-4 h-4" />
                  Adicionar
                </button>
              </div>

              <div class="mb-4">
                <div class="relative">
                  <MagnifyingGlassIcon class="absolute left-3 top-1/2 -translate-y-1/2 w-5 h-5 text-gray-400" />
                  <input v-model="searchQuery" type="text" placeholder="Pesquisar membros..."
                    class="w-full pl-10 pr-4 py-2 border border-gray-300 dark:border-gray-600 rounded-lg focus:ring-2 focus:ring-primary focus:border-transparent bg-white dark:bg-gray-800 text-gray-900 dark:text-white" />
                </div>
              </div>

              <div class="space-y-2">
                <div v-for="member in groupMembers" :key="member.id"
                  class="flex items-center justify-between p-3 rounded-lg hover:bg-gray-50 dark:hover:bg-gray-800">
                  <div class="flex items-center gap-3">
                    <UserPhoto src="/teste.jpg" :alt="chat.name" :online="member.user.online" size="w-10 h-10" />
                    <div>
                      <p class="font-medium text-gray-900 dark:text-white">
                        {{ member.user.fullName }}
                        <span v-if="member.user.id === userService.authenticatedUser.id"
                          class="text-sm text-gray-500">(Eu)</span>
                      </p>
                      <div class="flex items-center gap-2">
                        <span v-if="member.role === 'ADMIN'" class="flex items-center gap-1 text-xs text-primary">
                          <ShieldCheckIcon class="w-3 h-3" />
                          Admin
                        </span>
                        <span class="text-xs text-gray-500 dark:text-gray-400">
                          Entrou em {{ member.enterDate }}
                        </span>
                      </div>
                    </div>
                  </div>

                  <div v-if="canEditGroup && member.id !== 'me'" class="flex items-center gap-2">
                    <button v-if="member.role !== 'ADMIN' && member.user.id !== userService.authenticatedUser.id"
                      @click="promoteToAdmin(member.id)"
                      class="p-2 text-gray-400 hover:text-primary rounded-full hover:bg-gray-100 dark:hover:bg-gray-700"
                      title="Promover a admin">
                      <ShieldCheckIcon class="w-4 h-4" />
                    </button>
                    <button v-else-if="member.role === 'ADMIN' && member.user.id !== userService.authenticatedUser.id"
                      @click="demoteFromAdmin(member.id)"
                      class="p-2 text-gray-400 hover:text-yellow-500 rounded-full hover:bg-gray-100 dark:hover:bg-gray-700"
                      title="Remover admin">
                      <ShieldCheckIcon class="w-4 h-4" />
                    </button>
                    <button v-if="member.user.id !== userService.authenticatedUser.id"
                      @click="removeMember(member.user.id)"
                      class="p-2 text-gray-400 hover:text-red-500 rounded-full hover:bg-gray-100 dark:hover:bg-gray-700"
                      title="Remover membro">
                      <UserMinusIcon class="w-4 h-4" />
                    </button>
                  </div>
                </div>
              </div>
            </div>

            <!-- Media Tab -->
            <div v-if="activeTab === 'media'" class="p-6">
              <h3 class="text-lg font-semibold text-gray-900 dark:text-white mb-6">Mídia Compartilhada</h3>

              <div v-if="sharedMedia.length === 0" class="text-center py-12">
                <PhotoIcon class="w-12 h-12 text-gray-400 mx-auto mb-4" />
                <p class="text-gray-500 dark:text-gray-400">Nenhuma mídia compartilhada</p>
              </div>

              <div v-else class="grid grid-cols-3 gap-2">
                <div v-for="media in sharedMedia" :key="media.id"
                  class="aspect-square bg-gray-100 dark:bg-gray-800 rounded-lg flex items-center justify-center cursor-pointer hover:opacity-80 transition-opacity">
                  <span class="text-2xl">
                    {{ media.content.includes('📷') ? '📷' : '🎥' }}
                  </span>
                </div>
              </div>
            </div>

            <!-- Files Tab -->
            <div v-if="activeTab === 'files'" class="p-6">
              <h3 class="text-lg font-semibold text-gray-900 dark:text-white mb-6">Arquivos Compartilhados</h3>

              <div v-if="sharedDocuments.length === 0" class="text-center py-12">
                <DocumentIcon class="w-12 h-12 text-gray-400 mx-auto mb-4" />
                <p class="text-gray-500 dark:text-gray-400">Nenhum arquivo compartilhado</p>
              </div>

              <div v-else class="space-y-3">
                <div v-for="doc in sharedDocuments" :key="doc.id"
                  class="flex items-center gap-3 p-3 rounded-lg hover:bg-gray-50 dark:hover:bg-gray-800 cursor-pointer">
                  <DocumentIcon class="w-8 h-8 text-gray-400" />
                  <div class="flex-1">
                    <p class="font-medium text-gray-900 dark:text-white">{{ doc.content }}</p>
                    <p class="text-sm text-gray-500 dark:text-gray-400">
                      {{ format(new Date(doc.timestamp), 'dd/MM/yyyy', { locale: ptBR }) }}
                    </p>
                  </div>
                  <ChevronRightIcon class="w-5 h-5 text-gray-400" />
                </div>
              </div>
            </div>

            <!-- Links Tab -->
            <div v-if="activeTab === 'links'" class="p-6">
              <h3 class="text-lg font-semibold text-gray-900 dark:text-white mb-6">Links Compartilhados</h3>

              <div v-if="sharedLinks.length === 0" class="text-center py-12">
                <LinkIcon class="w-12 h-12 text-gray-400 mx-auto mb-4" />
                <p class="text-gray-500 dark:text-gray-400">Nenhum link compartilhado</p>
              </div>

              <div v-else class="space-y-3">
                <div v-for="link in sharedLinks" :key="link.id"
                  class="flex items-center gap-3 p-3 rounded-lg hover:bg-gray-50 dark:hover:bg-gray-800 cursor-pointer">
                  <LinkIcon class="w-8 h-8 text-gray-400" />
                  <div class="flex-1">
                    <p class="font-medium text-gray-900 dark:text-white truncate">{{ link.content }}</p>
                    <p class="text-sm text-gray-500 dark:text-gray-400">
                      {{ format(new Date(link.timestamp), 'dd/MM/yyyy', { locale: ptBR }) }}
                    </p>
                  </div>
                  <ChevronRightIcon class="w-5 h-5 text-gray-400" />
                </div>
              </div>
            </div>

            <!-- Settings Tab -->
            <div v-if="activeTab === 'settings'" class="p-6">
              <h3 class="text-lg font-semibold text-gray-900 dark:text-white mb-6">Configurações</h3>

              <div class="space-y-4">
                <button @click="toggleNotifications"
                  class="w-full flex items-center justify-between p-4 rounded-lg hover:bg-gray-50 dark:hover:bg-gray-800">
                  <div class="flex items-center gap-3">
                    <BellIcon v-if="!conversation.muted" class="w-5 h-5 text-gray-400" />
                    <BellSlashIcon v-else class="w-5 h-5 text-gray-400" />
                    <span class="text-gray-900 dark:text-white">Notificações</span>
                  </div>
                  <span class="text-sm text-gray-500 dark:text-gray-400">
                    {{ conversation.muted ? 'Desativadas' : 'Ativadas' }}
                  </span>
                </button>

                <button @click="archiveConversation"
                  class="w-full flex items-center gap-3 p-4 rounded-lg hover:bg-gray-50 dark:hover:bg-gray-800 text-left">
                  <ArchiveBoxIcon class="w-5 h-5 text-gray-400" />
                  <span class="text-gray-900 dark:text-white">
                    {{ isGroup ? 'Arquivar grupo' : 'Arquivar conversa' }}
                  </span>
                </button>

                <button v-if="isGroup" @click="leaveGroup"
                  class="w-full flex items-center gap-3 p-4 rounded-lg hover:bg-red-50 dark:hover:bg-red-900/20 text-left text-red-600 dark:text-red-400">
                  <UserMinusIcon class="w-5 h-5" />
                  <span>Sair do grupo</span>
                </button>

                <button @click="deleteConversation"
                  class="w-full flex items-center gap-3 p-4 rounded-lg hover:bg-red-50 dark:hover:bg-red-900/20 text-left text-red-600 dark:text-red-400">
                  <TrashIcon class="w-5 h-5" />
                  <span>{{ isGroup ? 'Excluir grupo' : 'Excluir conversa' }}</span>
                </button>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- Confirmation Dialog -->
    <div v-if="showConfirmDialog" class="fixed inset-0 bg-black/70 flex items-center justify-center z-80">
      <div class="bg-white dark:bg-gray-900 rounded-xl p-6 max-w-md mx-4">
        <div class="flex items-center gap-3 mb-4">
          <ExclamationTriangleIcon class="w-6 h-6 text-red-700" />
          <h3 class="text-lg font-semibold text-gray-900 dark:text-white">Confirmar Ação</h3>
        </div>
        <p class="text-gray-600 dark:text-gray-300 mb-6">
          Tem certeza que deseja realizar esta ação? Esta operação não pode ser desfeita.
        </p>
        <div class="flex justify-end gap-3">
          <button :disabled="processingAction" @click="showConfirmDialog = false"
            class="px-4 py-2 border border-gray-300 dark:border-gray-600 text-gray-700 dark:text-gray-300 rounded-lg hover:bg-gray-50 dark:hover:bg-gray-800 disabled:cursor-not-allowed disabled:opacity-50">
            Cancelar
          </button>
          <button :disabled="processingAction" @click="confirmAction"
            class="px-4 py-2 bg-red-600 text-white rounded-lg hover:bg-red-700 disabled:cursor-not-allowed disabled:opacity-50">
            <span v-if="!processingAction">Confirmar</span>
            <SecondLoader v-else class="w-4 h-4 flex before:!border-white" />
          </button>
        </div>
      </div>
    </div>
  </Teleport>

  <AddMemberModal :show="showAddMemberModal" @add="onAddMember" @close="showAddMemberModal = false" />
  <ProcessModal :show="processingAddMember" />


</template>

<script setup>
import { ref, computed, onMounted, onUnmounted } from 'vue';
import {
  XMarkIcon,
  PhotoIcon,
  PencilIcon,
  UserPlusIcon,
  UserMinusIcon,
  ShieldCheckIcon,
  BellIcon,
  BellSlashIcon,
  ArchiveBoxIcon,
  TrashIcon,
  MagnifyingGlassIcon,
  ChevronRightIcon,
  DocumentIcon,
  VideoCameraIcon,
  LinkIcon,
  CalendarIcon,
  ClockIcon,
  UserIcon,
  CogIcon,
  ExclamationTriangleIcon
} from '@heroicons/vue/24/outline';
import { format } from 'date-fns';
import { pt, ptBR } from 'date-fns/locale';
import OnlineStatus from './OnlineStatus.vue';
import UserSelectionModal from './UserSelectionModal.vue';
import UserPhoto from '@/components/UserPhoto.vue';
import GroupPhoto from '@/components/GroupPhoto.vue';
import { UserService } from '@/services/user/UserService';
import { useChatStore } from '@/stores/chatStore';
import { addGroupMember, removeGroupMember, updateGroupDescription, updateGroupName, updatePicture } from '@/services/api/chatApi';
import SecondLoader from '@/components/SecondLoader.vue';
import AddMemberModal from './AddMemberModal.vue';
import ProcessModal from '@/components/ProcessModal.vue';

const props = defineProps({
  show: Boolean,
  chat: Object
});

const emit = defineEmits(['close']);

const chatStore = useChatStore()
const userService = new UserService()
const activeTab = ref('info');
const showAddMemberModal = ref(false);
const showConfirmDialog = ref(false);
const confirmAction = ref(null);
const searchQuery = ref('');
const isEditingName = ref(false);
const isEditingDescription = ref(false);
const editedName = ref('');
const oldName = ref('')
const editedDescription = ref('');
const oldDescription = ref('');
const groupPhotoInput = ref(null);
const processingAction = ref(false)
const processingAddMember = ref(false)

const lastSeen = computed(() => {
  const stringFormat = props.chat.lastSeen && (props.chat.lastSeenState === 'TODAY' || props.chat.lastSeenState === 'YESTERDAY') ? 'HH:mm' : props.chat.lastSeen ? 'dd/MM/yyyy HH:mm' : '';
  const lastSeen = props.chat.lastSeen ? format(new Date(props.chat.lastSeen), stringFormat, { locale: pt }) : '';

  if (props.chat.userOnline) return 'Online agora';
  else if (props.chat.lastSeenState === 'TODAY' && lastSeen !== "") return `Online hoje, às ${lastSeen}`;
  else if (props.chat.lastSeenState === 'YESTERDAY' && lastSeen !== "") return `Online ontem, às ${lastSeen}`;
  else if (props.chat.lastSeenState === 'OLD' && lastSeen !== "") return `Online em ${lastSeen.split(' ')[0]}`;

  return 'Informação não disponível'
});

const displayStatus = computed(() => {
  if (!props.chat) return '';
  if (props.chat.type === 'GROUP') {
    const memberCount = props.chat.members.length;
    return `${memberCount} membros`;
  }
  return lastSeen;
});

const displayEmailOrGroupCreator = computed(() => {
  if (!props.chat) return '';
  if (props.chat.type === 'GROUP') {
    const creator = props.chat.createdBy.fullName;
    return `Grupo criado por ${creator}`;
  }
  return props.chat.receiverEmail === userService.authenticatedUser.email ? props.chat.senderEmail : props.chat.receiverEmail;
});

const groupMembers = computed(() => {
  if (props.chat.type !== 'GROUP' || !props.chat) return [];

  const query = searchQuery.value.toLowerCase();
  return props.chat.members.filter(member =>
    member.user.fullName.toLowerCase().includes(query)
  ).sort((a, b) => {
    // Admins first, then by name
    if (a.role === 'ADMIN' && b.role !== 'ADMIN') return -1;
    if (b.role === 'ADMIN' && a.role !== 'ADMIN') return 1;
    return a.user.fullName.localeCompare(b.user.fullName);
  });
});

const onAddMember = (member_) => {
  showAddMemberModal.value = false
  if (!props.chat.members.some(member => member.user.id === member_.id)) {
    const chat = chatStore.chats.find(chat => chat.id === props.chat.id)

    processingAddMember.value = true

    const obj = new Object()
    obj.chatId = props.chat.id
    obj.userId = member_.id

    addGroupMember(obj).then((response) => {
      processingAddMember.value = false

      if (response.status === 202) {
        chat.messages.push(response.data)
        chat.members.push({ role: 'MEMBER', enterDate: new Date().toISOString(), user: member_ })
      }
    }).catch((error) => {
      processingAddMember.value = false
      alert("Ocorreu um erro ao adicionar membro ao grupo")
      console.log(error)
    })
  }
}

const canEditGroup = computed(() => {
  if (props.chat.type !== 'GROUP' || !props.chat) return false;

  const currentUser = props.chat.members.find(m => m.user.id === userService.authenticatedUser.id);
  return currentUser?.role === 'ADMIN';
});

const startEditingName = () => {
  if (!canEditGroup.value) return;
  editedName.value = props.chat.name;
  oldName.value = props.chat.name;
  isEditingName.value = true;
};

const saveGroupName = () => {
  if (editedName.value.trim() && editedName.value !== props.chat.name) {

    const chat = chatStore.chats.find(chat => chat.id === props.chat.id)

    const data = new Object()
    data.id = chat.id
    data.name = editedName.value.trim()

    updateGroupName(data).then((response) => {
      if (response.status === 202) {
        chat.name = data.name
        chat.messages.push(response.data)
      }
    }).catch((error) => {
      chat.name = oldName.value
      console.log(error)
    })
  }
  isEditingName.value = false;
};

const startEditingDescription = () => {
  if (!canEditGroup.value) return;
  editedDescription.value = props.chat.description || '';
  oldDescription.value = props.chat.description || '';
  isEditingDescription.value = true;
};

const saveGroupDescription = () => {
  const chat = chatStore.chats.find(chat => chat.id === props.chat.id)

  const data = new Object()
  data.id = chat.id
  data.description = editedDescription.value

  updateGroupDescription(data).then((response) => {
    if (response.status === 202) {
      chat.description = data.description
      chat.messages.push(response.data)
    }
  }).catch((error) => {
    chat.name = oldDescription.value
    console.log(error)
  })
  isEditingDescription.value = false;
};

const handlePhotoUpload = (event) => {
  const file = event.target.files[0];
  if (!file || !canEditGroup.value) return;

  if (file.size > 5 * 1024 * 1024) {
    alert('A imagem deve ter no máximo 5MB');
    return;
  }

  if (!file.type.startsWith('image/')) {
    alert('Por favor, selecione apenas arquivos de imagem');
    return;
  }

  const photoUrl = URL.createObjectURL(file);
  const chat = chatStore.chats.find(chat => chat.id === props.chat.id)
  chat.groupImageUrl = photoUrl
  chat.imageGroupType = 'LOADING'

  const formData = new FormData()
  formData.append("photo", file)
  formData.append("id", chat.id)

  updatePicture(formData).then((response) => {
    if (response.status === 202) {
      chat.imageGroupType = 'CUSTOM'
      chat.messages.push(response.data)
    }
  }).catch((error) => {
    chat.imageGroupType = 'DEFAULT'
    console.log(error)
  })
};

const removeMember = (userId) => {
  confirmAction.value = () => {
    const chat = chatStore.chats.find(chat => chat.id === props.chat.id)

    processingAction.value = true

    const obj = new Object()
    obj.chatId = props.chat.id
    obj.userId = userId

    removeGroupMember(obj).then((response) => {
      processingAction.value = true
      showConfirmDialog.value = false;

      if (response.status === 202) {
        chat.messages.push(response.data)
        chat.members.splice(chat.members.findIndex(member => member.user.id === userId), 1)
      }
    }).catch((error) => {
      processingAction.value = true
      showConfirmDialog.value = false;
      alert("Ocorreu um erro ao remover membro do grupo")
      console.log(error)
    })
  };
  showConfirmDialog.value = true;
};


/* 

const sharedMedia = computed(() => {
  if (!conversation.value) return [];
  
  return conversation.value.messages
    .filter(msg => msg.type === 'file' || msg.content.includes('📷') || msg.content.includes('🎥'))
    .slice(-20); // Show last 20 media items
});

const sharedDocuments = computed(() => {
  if (!conversation.value) return [];
  
  return conversation.value.messages
    .filter(msg => msg.type === 'file' && !msg.content.includes('📷') && !msg.content.includes('🎥'))
    .slice(-20);
});

const sharedLinks = computed(() => {
  if (!conversation.value) return [];
  
  const urlRegex = /(https?:\/\/[^\s]+)/g;
  return conversation.value.messages
    .filter(msg => urlRegex.test(msg.content))
    .slice(-20);
});


const promoteToAdmin = (memberId) => {
  conversationStore.updateMemberRole(props.conversationId, memberId, 'admin');
};

const demoteFromAdmin = (memberId) => {
  conversationStore.updateMemberRole(props.conversationId, memberId, 'member');
};

const leaveGroup = () => {
  confirmAction.value = () => {
    conversationStore.leaveGroup(props.conversationId);
    showConfirmDialog.value = false;
    emit('close');
  };
  showConfirmDialog.value = true;
};

const deleteConversation = () => {
  confirmAction.value = () => {
    conversationStore.deleteConversation(props.conversationId);
    showConfirmDialog.value = false;
    emit('close');
  };
  showConfirmDialog.value = true;
};

const toggleNotifications = () => {
  conversationStore.toggleNotifications(props.conversationId);
};

const archiveConversation = () => {
  conversationStore.archiveConversation(props.conversationId);
  emit('close');
};
*/
const handleKeydown = (event) => {
  if (!props.show) return;

  if (event.key === 'Escape') {
    if (isEditingName.value) {
      isEditingName.value = false;
    } else if (isEditingDescription.value) {
      isEditingDescription.value = false;
    } else {
      emit('close');
    }
  }
};

onMounted(() => {
  document.addEventListener('keydown', handleKeydown);
});

onUnmounted(() => {
  document.removeEventListener('keydown', handleKeydown);
});
</script>
