import { createRouter, createWebHistory } from 'vue-router'
import HomeView from './views/HomeView.vue'
import CartView from './views/cart/CartView.vue'
import DishList from './views/dish/DishList.vue'
import OrderConfirmView from './views/order/OrderConfirmView.vue'
import OrderListView from './views/order/OrderListView.vue'
import OrderDetailView from './views/order/OrderDetailView.vue'

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
  },
  {
    path: '/order/confirm',
    name: 'OrderConfirm',
    component: OrderConfirmView
  },
  {
    path: '/order/list',
    name: 'OrderList',
    component: OrderListView
  },
  {
    path: '/order/detail/:id',
    name: 'OrderDetail',
    component: OrderDetailView,
    props: true
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

export default router
