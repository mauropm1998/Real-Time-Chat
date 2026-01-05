import './assets/main.css'
import 'vue3-toastify/dist/index.css'
import 'vue3-emoji-picker/css'

import { createApp } from 'vue'
import { createPinia } from 'pinia'
import App from './App.vue'
import router from './router'
import { server } from './services/util';

const app = createApp(App)

app.use(createPinia())
app.use(router)

app.mount('#app')
