import { createRouter, createWebHistory } from 'vue-router'
import MenuView from '../views/MenuView.vue'
import GameView from '../views/GameView.vue'

export default createRouter({
  history: createWebHistory(),
  routes: [
    { path: '/', name: 'menu', component: MenuView },
    { path: '/game', name: 'game', component: GameView }
  ]
})
