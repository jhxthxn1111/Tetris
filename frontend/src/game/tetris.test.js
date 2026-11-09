import { describe, it, expect } from 'vitest'
import {
  COLS, ROWS, KEYS, createBoard, collides, rotate, merge, clearLines, spawn, bestPieceKey
} from './tetris'

describe('tablero', () => {
  it('createBoard crea 20 filas', () => {
    expect(createBoard().length).toBe(ROWS)
  })

  it('createBoard crea 10 columnas', () => {
    expect(createBoard()[0].length).toBe(COLS)
  })

  it('createBoard empieza vacio', () => {
    expect(createBoard().flat().every((c) => c === null)).toBe(true)
  })
})

describe('collides', () => {
  it('choca con la pared izquierda', () => {
    expect(collides(createBoard(), [[1]], -1, 0)).toBe(true)
  })

  it('choca con la pared derecha', () => {
    expect(collides(createBoard(), [[1]], COLS, 0)).toBe(true)
  })

  it('choca con el piso', () => {
    expect(collides(createBoard(), [[1]], 0, ROWS)).toBe(true)
  })

  it('choca con una ficha ya puesta', () => {
    const board = createBoard()
    board[19][3] = 'T'
    expect(collides(board, [[1]], 3, 19)).toBe(true)
  })

  it('no choca en una posicion libre', () => {
    expect(collides(createBoard(), [[1]], 3, 3)).toBe(false)
  })
})

describe('rotate', () => {
  it('una I horizontal queda con 4 filas al rotar', () => {
    expect(rotate([[1, 1, 1, 1]]).length).toBe(4)
  })

  it('una I horizontal queda con 1 columna al rotar', () => {
    expect(rotate([[1, 1, 1, 1]])[0].length).toBe(1)
  })

  it('cuatro rotaciones dejan la T como estaba', () => {
    const t = [[0, 1, 0], [1, 1, 1]]
    expect(rotate(rotate(rotate(rotate(t))))).toEqual(t)
  })
})

describe('spawn', () => {
  it('la I aparece centrada', () => {
    expect(spawn('I').x).toBe(3)
  })

  it('la O aparece centrada', () => {
    expect(spawn('O').x).toBe(4)
  })

  it('la ficha aparece arriba', () => {
    expect(spawn('T').y).toBe(0)
  })
})

describe('merge', () => {
  it('no modifica el tablero original', () => {
    const board = createBoard()
    merge(board, { ...spawn('O'), y: 5 })
    expect(board.flat().every((c) => c === null)).toBe(true)
  })

  it('deja la ficha en su posicion', () => {
    const merged = merge(createBoard(), { key: 'I', shape: [[1, 1, 1, 1]], x: 0, y: 19 })
    expect(merged[19].slice(0, 4)).toEqual(['I', 'I', 'I', 'I'])
  })
})

describe('clearLines', () => {
  it('borra una linea completa', () => {
    const board = createBoard()
    board[ROWS - 1] = Array(COLS).fill('O')
    expect(clearLines(board).cleared).toBe(1)
  })

  it('borra dos lineas completas', () => {
    const board = createBoard()
    board[ROWS - 1] = Array(COLS).fill('O')
    board[ROWS - 2] = Array(COLS).fill('O')
    expect(clearLines(board).cleared).toBe(2)
  })

  it('no borra una linea con hueco', () => {
    const board = createBoard()
    board[ROWS - 1] = Array(COLS).fill('O')
    board[ROWS - 1][0] = null
    expect(clearLines(board).cleared).toBe(0)
  })

  it('mantiene el tablero de 20 filas', () => {
    const board = createBoard()
    board[ROWS - 1] = Array(COLS).fill('O')
    expect(clearLines(board).board.length).toBe(ROWS)
  })
})

describe('bestPieceKey', () => {
  it('devuelve una ficha valida', () => {
    expect(KEYS).toContain(bestPieceKey(createBoard()))
  })

  it('con un hueco de 4 en el fondo elige la I', () => {
    const board = createBoard()
    board[ROWS - 1] = Array(COLS).fill('O')
    for (let c = 0; c < 4; c++) board[ROWS - 1][c] = null
    expect(bestPieceKey(board)).toBe('I')
  })
})