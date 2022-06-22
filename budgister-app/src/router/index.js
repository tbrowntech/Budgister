import { createRouter, createWebHistory } from "vue-router";

const routes = [
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
    path: "/shoppinglist",
    name: "shoppinglist",
    component: () => import("../components/ShoppingListPage.vue"),
  },
  {
    path: "/notes",
    name: "notes",
    component: () => import("../components/NotesPage.vue"),
  },
  {
    path: "/editpage",
    name: "edit",
    component: () => import("../components/EditPage.vue"),
  },
];

const router = createRouter({
  history: createWebHistory(process.env.BASE_URL),
  routes,
});

export default router;
