import { createRouter, createWebHistory } from 'vue-router'
import HomeView from './views/HomeView.vue'
import CartView from './views/cart/CartView.vue'
import DishList from './views/dish/DishList.vue'

const routes = [
  {
    path: '/',
    name: 'Home',
    component: HomeView
  },
  {
    path: '/cart',
    name: 'Cart',
    component: CartView
  },
  {
    path: '/dish',
    name: 'Dish',
    component: DishList
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

export default router
