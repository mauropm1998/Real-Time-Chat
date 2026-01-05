import { isTokenValid } from "@/services/user/TokenService";
import { createRouter, createWebHistory } from "vue-router";
import ChatList from "@/chat/components/ChatList.vue";
import ChatView from "@/chat/components/ChatView.vue";
import EmptyState from "@/chat/components/EmptyState.vue";

const routes = [
  {
    path: "/",
    name: "login",
    component: () => import("../views/LoginView.vue"),
    meta: { loginPage: true }
  },
  {
    path: "/chat",
    name: "chat-main",
    component: () => import("../chat/Chat.vue"),
    meta: { requiresAuth: true },
    children: [
      {
        path: '',
        name: 'chat',
        components: {
          chatList: ChatList,
          chatView: EmptyState
        }
      },
      {
        path: ':chatId',
        name: 'chat-view',
        components: {
          chatList: ChatList,
          chatView: ChatView
        },
        props: {
          chatView: true
        }
      }
    ]
  },
];

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes,
});

router.beforeEach(async (to, from, next) => {
  const valid = await isTokenValid();

  // Verifica se a rota requer autenticação
  if (to.matched.some((record) => record.meta.requiresAuth)) {
    if (!valid) {
      // Se o token expirou ou não existe, redireciona para a página de login
      next({ name: "login" });
    } else {
      // Se o token é válido, permite o acesso à rota
      next();
    }
  }
  // Verifica se é a rota de login
  else if (to.matched.some((record) => record.meta.loginPage)) {
    if (valid) {
      next({ name: "chat" });
    } else {
      // Se o token expirou, permite o acesso à rota
      next();
    }
  } else {
    // Para rotas que não requerem autenticação, permite o acesso
    next();
  }
});


export default router;
