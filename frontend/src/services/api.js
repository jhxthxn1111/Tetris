export async function fetchRandomQuestion(difficulty) {
  const res = await fetch(`/api/questions/random?difficulty=${difficulty}`)
  if (!res.ok) throw new Error('No se pudo cargar la pregunta')
  return res.json()
}
