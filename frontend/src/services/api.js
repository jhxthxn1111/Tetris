export async function fetchRandomQuestion(difficulty) {
  const res = await fetch(`/api/questions/random?difficulty=${difficulty}`)
  if (!res.ok) throw new Error('No se pudo cargar la pregunta')
  return res.json()
}

export async function submitAnswer(id, selectedIndex) {
  const res = await fetch(`/api/questions/${id}/answer`, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ selectedIndex })
  })
  if (!res.ok) throw new Error('No se pudo validar la respuesta')
  return res.json()
}
