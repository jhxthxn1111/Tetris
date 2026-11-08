<script setup>
const props = defineProps({
  question: { type: Object, required: true }, // QuestionDto (sin correctIndex ni explanation)
  result: { type: Object, default: null }     // { correct, correctIndex, selectedIndex, explanation } | null
})
const emit = defineEmits(['answer', 'next'])
const letters = ['A', 'B', 'C', 'D']

const answered = () => props.result !== null

const state = (i) => {
  if (!answered()) return ''
  if (i === props.result.correctIndex) return 'correct'
  if (i === props.result.selectedIndex) return 'wrong'
  return ''
}
</script>

<template>
  <div class="panel">
    <h2>{{ question.text }}</h2>
    <div class="cat">{{ question.category }}</div>

    <button
      v-for="(opt, i) in question.options"
      :key="i"
      class="option"
      :class="state(i)"
      :disabled="answered()"
      @click="emit('answer', i)"
    >
      <b>{{ letters[i] }}.</b> {{ opt }}
    </button>

    <template v-if="answered()">
      <p :class="result.correct ? 'result ok' : 'result bad'">
        {{ result.correct ? '¡Correcto! +500 pts · cae la ficha ideal' : 'Incorrecto · cae una ficha al azar' }}
      </p>
      <div class="tip"><b>TIP:</b> {{ result.explanation }}</div>
      <button class="btn" style="margin-top: 12px" @click="emit('next')">Continuar</button>
    </template>
  </div>
</template>
