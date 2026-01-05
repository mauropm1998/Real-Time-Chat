<template>
  <div class="flex min-h-screen items-center justify-center p-4 bg-[#F4F3FA]">
    <div class="w-full max-w-md bg-white rounded-2xl shadow-lg overflow-hidden">
      <div class="p-8">
        <div class="text-center mb-8">
          <h1 class="text-3xl font-bold mb-2">
            <span class="text-[#6247F0]">Chat</span>App
          </h1>
          <p class="text-[#7E7E7E]">Conecte-se e converse com seus amigos em tempo real</p>
        </div>
        
        <form @submit.prevent="authenticateUser" novalidate class="space-y-6">
          <div class="space-y-2">
            <label for="email" class="block text-sm font-medium text-[#1D1D1D]">Email</label>
            <div class="relative">
              <div class="absolute inset-y-0 left-0 flex items-center pl-3 pointer-events-none text-gray-400">
                <svg xmlns="http://www.w3.org/2000/svg" class="h-5 w-5" viewBox="0 0 20 20" fill="currentColor">
                  <path d="M2.003 5.884L10 9.882l7.997-3.998A2 2 0 0016 4H4a2 2 0 00-1.997 1.884z" />
                  <path d="M18 8.118l-8 4-8-4V14a2 2 0 002 2h12a2 2 0 002-2V8.118z" />
                </svg>
              </div>
              <input 
                :disabled="isAuthenticating"
                :class="{ 'bg-gray-100 cursor-not-allowed': isAuthenticating }"
                id="email" 
                v-model="authRequest.email" 
                type="email" 
                class="w-full pl-10 pr-3 py-3 bg-gray-50 border border-gray-200 rounded-lg focus:outline-none focus:ring-2 focus:ring-[#6247F0]/20 focus:border-[#6247F0]"
                placeholder="Digite seu email"
                required
              />
            </div>
          </div>
          
          <div class="space-y-2">
            <label for="password" class="block text-sm font-medium text-[#1D1D1D]">Senha</label>
            <div class="relative">
              <div class="absolute inset-y-0 left-0 flex items-center pl-3 pointer-events-none text-gray-400">
                <svg xmlns="http://www.w3.org/2000/svg" class="h-5 w-5" viewBox="0 0 20 20" fill="currentColor">
                  <path fill-rule="evenodd" d="M5 9V7a5 5 0 0110 0v2a2 2 0 012 2v5a2 2 0 01-2 2H5a2 2 0 01-2-2v-5a2 2 0 012-2zm8-2v2H7V7a3 3 0 016 0z" clip-rule="evenodd" />
                </svg>
              </div>
              <input 
                :disabled="isAuthenticating"
                :class="{ 'bg-gray-100 cursor-not-allowed': isAuthenticating }"
                id="password" 
                v-model="authRequest.password" 
                type="password" 
                class="w-full pl-10 pr-3 py-3 bg-gray-50 border border-gray-200 rounded-lg focus:outline-none focus:ring-2 focus:ring-[#6247F0]/20 focus:border-[#6247F0]"
                placeholder="Digite sua senha"
                required
              />
            </div>
          </div>
          
          <button 
            type="submit" 
            class="w-full bg-[#6247F0] text-white py-3 rounded-lg font-medium hover:bg-[#6247F0]/90 transition-colors focus:outline-none focus:ring-2 focus:ring-[#6247F0]/50"
          >
            Entrar
          </button>
        </form>
      </div>
    </div>
  </div>
</template>

<script setup>
import instance from '@/services/axios'
import { UserService } from '@/services/user/UserService'
import { onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'
import { toast } from 'vue3-toastify'

const router = useRouter()
const isAuthenticating = ref(false)
const authRequest = ref({
    email: null,
    password: null
})

const userService = new UserService()
const toastOptions = ref({
    autoClose: 4000,
    clearOnUrlChange: false,
    closeOnClick: true,
    style: {
        fontSize: '.85rem'
    }
})

const authenticateUser = async () => {
    isAuthenticating.value = true

    try {
        const response = await instance.post('/auth/authenticate', authRequest.value)
        isAuthenticating.value = false
        userService.authenticatedUser = response.data     
        
        router.push({ name: 'chat' })
        
    } catch (error) {
        toastOptions.value.type = 'error'

        if (error.response && error.response.data.errors && error.response.data.errors.length > 0) {
            error.response.data.errors.forEach((err) => {
                toast(err, toastOptions.value)
            })
        } else {
            toast(error.response.data.message, toastOptions.value)
        }

        isAuthenticating.value = false
    }
}

onMounted(() => {
    document.title = 'Login - ChatApp'
})
</script> 