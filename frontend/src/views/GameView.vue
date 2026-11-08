<script setup>
import { ref, computed, onMounted, onBeforeUnmount } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import Board from '../components/Board.vue'
import QuestionPanel from '../components/QuestionPanel.vue'
import { fetchRandomQuestion, submitAnswer } from '../services/api'
import * as T from '../game/tetris'

const route = useRoute()
const router = useRouter()
const difficulty = route.query.difficulty || 'EASY'
const SPEEDS = { EASY: 800, MEDIUM: 550, HARD: 300 }

const board = ref(T.createBoard())
const piece = ref(null)
const phase = ref('playing') // playing | question | over
const score = ref(0)
const lines = ref(0)
const streak = ref(0)
const question = ref(null)
const selected = ref(null)
const result = ref(null)   // { correct, correctIndex, selectedIndex, explanation } | null
const nextKey = ref(null)
const error = ref('')

const level = computed(() => 1 + Math.floor(lines.value / 5))
const delay = computed(() => Math.max(100, SPEEDS[difficulty] - (level.value - 1) * 60))

let timer = null
const stop = () => clearTimeout(timer)
const loop = () => {
  timer = setTimeout(() => {
    tick()
    if (phase.value === 'playing') loop()
  }, delay.value)
}

function newPiece(key) {
  const p = T.spawn(key)
  if (T.collides(board.value, p.shape, p.x, p.y)) {
    phase.value = 'over'
    stop()
    return
  }
  piece.value = p
}

function tick() {
  const p = piece.value
  if (!p) return
  if (!T.collides(board.value, p.shape, p.x, p.y + 1)) p.y++
  else lockPiece()
}

async function lockPiece() {
  stop()
  const { board: b, cleared } = T.clearLines(T.merge(board.value, piece.value))
  board.value = b
  lines.value += cleared
  score.value += cleared * 100 * level.value
  piece.value = null
  selected.value = null
  result.value = null
  error.value = ''
  phase.value = 'question'
  try {
    question.value = await fetchRandomQuestion(difficulty)
  } catch (e) {
    error.value = e.message
  }
}

async function answer(i) {
  if (result.value) return // ya se respondió esta pregunta
  selected.value = i
  try {
    result.value = await submitAnswer(question.value.id, i)
    if (result.value.correct) {
      score.value += 500
      streak.value++
      nextKey.value = T.bestPieceKey(board.value) // ficha ideal
    } else {
      streak.value = 0
      nextKey.value = T.randomKey() // ficha al azar
    }
  } catch (e) {
    error.value = e.message
    selected.value = null
  }
}

function next() {
  result.value = null
  selected.value = null
  phase.value = 'playing'
  newPiece(nextKey.value)
  if (phase.value === 'playing') loop()
}

function move(dx) {
  const p = piece.value
  if (p && !T.collides(board.value, p.shape, p.x + dx, p.y)) p.x += dx
}
function rotatePiece() {
  const p = piece.value
  if (!p) return
  const s = T.rotate(p.shape)
  if (!T.collides(board.value, s, p.x, p.y)) p.shape = s
}
function hardDrop() {
  const p = piece.value
  if (!p) return
  while (!T.collides(board.value, p.shape, p.x, p.y + 1)) p.y++
  lockPiece()
}

function onKey(e) {
  if (phase.value !== 'playing') return
  const k = e.key
  if (k === 'ArrowLeft') move(-1)
  else if (k === 'ArrowRight') move(1)
  else if (k === 'ArrowDown') tick()
  else if (k === 'ArrowUp') rotatePiece()
  else if (k === ' ') { e.preventDefault(); hardDrop() }
}

onMounted(() => {
  window.addEventListener('keydown', onKey)
  newPiece(T.randomKey())
  loop()
})
onBeforeUnmount(() => {
  stop()
  window.removeEventListener('keydown', onKey)
})
</script>

<template>
  <div class="game">
    <div>
      <div class="stats">
        <div class="stat">NIVEL<b>{{ level }}</b></div>
        <div class="stat">PUNTAJE<b>{{ score }}</b></div>
        <div class="stat">RACHA<b>x{{ streak }}</b></div>
      </div>
      <Board :board="board" :piece="piece" />
      <p class="hint">← → mover · ↑ rotar · ↓ bajar · Espacio caída</p>
    </div>

    <QuestionPanel
      v-if="phase === 'question' && question"
      :question="question"
      :result="result"
      @answer="answer"
      @next="next"
    />

    <div v-else-if="phase === 'over'" class="panel">
      <h2>¡Juego terminado!</h2>
      <p class="tip">Puntaje final: {{ score }}</p>
      <button class="btn" style="margin-top: 12px" @click="router.push('/')">Volver al menú</button>
    </div>

    <p v-if="error" class="result bad">{{ error }} (¿está corriendo el backend?)</p>
  </div>
</template>
