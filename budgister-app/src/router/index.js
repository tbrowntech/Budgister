import { createRouter, createWebHistory } from "vue-router";
import HomeView from "../views/HomeView.vue";

const routes = [
  {
    path: "/",
    name: "home",
    component: HomeView,
  },
  {
    path: "/main",
    name: "main",
    component: () => import("../views/MainView.vue"),
  },
  {
    path: "/budget",
    name: "budget",
    component: () => import("../components/BudgetPage.vue"),
  },
  {
    path: "/register",
    name: "register",
    component: () => import("../components/RegisterPage.vue"),
  },
  {
    path: "/shoppingList",
    name: "shoppingList",
    component: () => import("../components/ShoppingListPage.vue"),
  },
  {
    path: "/notes",
    name: "notes",
    component: () => import("../components/NotesPage.vue"),
  },
];

const router = createRouter({
  history: createWebHistory(process.env.BASE_URL),
  routes,
});

export default router;
