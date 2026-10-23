<script setup>
const props = defineProps({
  question: { type: Object, required: true },
  selected: { type: Number, default: null } // null = aún sin responder
})
const emit = defineEmits(['answer', 'next'])
const letters = ['A', 'B', 'C', 'D']

const state = (i) => {
  if (props.selected === null) return ''
  if (i === props.question.correctIndex) return 'correct'
  if (i === props.selected) return 'wrong'
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
      :disabled="selected !== null"
      @click="emit('answer', i)"
    >
      <b>{{ letters[i] }}.</b> {{ opt }}
    </button>

    <template v-if="selected !== null">
      <p v-if="selected === question.correctIndex" class="result ok">¡Correcto! +500 pts · cae la ficha ideal</p>
      <p v-else class="result bad">Incorrecto · cae una ficha al azar</p>
      <div class="tip"><b>TIP:</b> {{ question.explanation }}</div>
      <button class="btn" style="margin-top: 12px" @click="emit('next')">Continuar</button>
    </template>
  </div>
</template>
