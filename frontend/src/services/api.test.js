import { describe, it, expect, vi, afterEach } from 'vitest'
import { fetchRandomQuestion, submitAnswer } from './api'

afterEach(() => {
  vi.unstubAllGlobals()
})

const respuestaOk = (data) => ({ ok: true, json: async () => data })
const respuestaError = () => ({ ok: false, json: async () => ({}) })

describe('fetchRandomQuestion', () => {
  it('pide la pregunta con la dificultad indicada', async () => {
    const fetchMock = vi.fn().mockResolvedValue(respuestaOk({ id: 1 }))
    vi.stubGlobal('fetch', fetchMock)

    await fetchRandomQuestion('HARD')

    expect(fetchMock).toHaveBeenCalledWith('/api/questions/random?difficulty=HARD')
  })

  it('devuelve la pregunta que manda el servidor', async () => {
    vi.stubGlobal('fetch', vi.fn().mockResolvedValue(respuestaOk({ id: 7 })))

    const pregunta = await fetchRandomQuestion('EASY')

    expect(pregunta).toEqual({ id: 7 })
  })

  it('lanza error si el servidor falla', async () => {
    vi.stubGlobal('fetch', vi.fn().mockResolvedValue(respuestaError()))

    await expect(fetchRandomQuestion('EASY')).rejects.toThrow('No se pudo cargar la pregunta')
  })
})

describe('submitAnswer', () => {
  it('envia la respuesta por POST con el cuerpo en JSON', async () => {
    const fetchMock = vi.fn().mockResolvedValue(respuestaOk({ correct: true }))
    vi.stubGlobal('fetch', fetchMock)

    await submitAnswer(5, 2)

    expect(fetchMock).toHaveBeenCalledWith('/api/questions/5/answer', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ selectedIndex: 2 })
    })
  })

  it('devuelve el resultado que manda el servidor', async () => {
    vi.stubGlobal('fetch', vi.fn().mockResolvedValue(respuestaOk({ correct: false })))

    const resultado = await submitAnswer(5, 0)

    expect(resultado).toEqual({ correct: false })
  })

  it('lanza error si el servidor falla', async () => {
    vi.stubGlobal('fetch', vi.fn().mockResolvedValue(respuestaError()))

    await expect(submitAnswer(5, 0)).rejects.toThrow('No se pudo validar la respuesta')
  })
})

describe('submitAnswer, validación del índice', () => {
  it.each([[-1], [4], [1.5], ['2'], [undefined], [NaN]])(
    'rechaza el índice %s antes de llamar al servidor',
    async (indice) => {
      vi.stubGlobal('fetch', vi.fn())

      await expect(submitAnswer(5, indice)).rejects.toThrow('Índice de opción inválido')
    }
  )

  it('no llama a fetch cuando el índice es inválido', async () => {
    const fetchMock = vi.fn()
    vi.stubGlobal('fetch', fetchMock)

    await submitAnswer(5, 9).catch(() => {})

    expect(fetchMock).not.toHaveBeenCalled()
  })

  it.each([[0], [3]])('acepta el índice límite %s', async (indice) => {
    const fetchMock = vi.fn().mockResolvedValue(respuestaOk({ correct: true }))
    vi.stubGlobal('fetch', fetchMock)

    await submitAnswer(5, indice)

    expect(fetchMock).toHaveBeenCalledTimes(1)
  })
})