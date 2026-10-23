<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'

const router = useRouter()
const difficulty = ref('EASY')
const options = [
  { value: 'EASY', label: 'Fácil' },
  { value: 'MEDIUM', label: 'Media' },
  { value: 'HARD', label: 'Difícil' }
]

const start = () => router.push({ name: 'game', query: { difficulty: difficulty.value } })
</script>

<template>
  <div class="menu">
    <h2>Elige la dificultad</h2>
    <div class="row">
      <button
        v-for="o in options"
        :key="o.value"
        class="btn secondary"
        :class="{ active: difficulty === o.value }"
        @click="difficulty = o.value"
      >
        {{ o.label }}
      </button>
    </div>
    <button class="btn" @click="start">Empezar · Nivel 1</button>
  </div>
</template>
