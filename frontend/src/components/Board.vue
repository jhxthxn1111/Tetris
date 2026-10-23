<script setup>
import { computed } from 'vue'
import { PIECES, COLS, ROWS } from '../game/tetris'

const props = defineProps({
  board: { type: Array, required: true },
  piece: { type: Object, default: null }
})

const cells = computed(() => {
  const grid = props.board.map((row) => [...row])
  if (props.piece) {
    props.piece.shape.forEach((row, r) =>
      row.forEach((v, c) => {
        const y = props.piece.y + r
        const x = props.piece.x + c
        if (v && y >= 0 && y < ROWS && x < COLS) grid[y][x] = props.piece.key
      })
    )
  }
  return grid.flat()
})

const color = (key) => (key ? PIECES[key].color : null)
</script>

<template>
  <div class="board">
    <div v-for="(cell, i) in cells" :key="i" class="cell" :style="{ background: color(cell) }" />
  </div>
</template>
