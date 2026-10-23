export const COLS = 10
export const ROWS = 20

export const PIECES = {
  I: { color: '#22d3ee', shape: [[1, 1, 1, 1]] },
  O: { color: '#facc15', shape: [[1, 1], [1, 1]] },
  T: { color: '#a855f7', shape: [[0, 1, 0], [1, 1, 1]] },
  S: { color: '#22c55e', shape: [[0, 1, 1], [1, 1, 0]] },
  Z: { color: '#ef4444', shape: [[1, 1, 0], [0, 1, 1]] },
  J: { color: '#3b82f6', shape: [[1, 0, 0], [1, 1, 1]] },
  L: { color: '#f97316', shape: [[0, 0, 1], [1, 1, 1]] }
}
export const KEYS = Object.keys(PIECES)

export const createBoard = () =>
  Array.from({ length: ROWS }, () => Array(COLS).fill(null))

export const randomKey = () => KEYS[Math.floor(Math.random() * KEYS.length)]

export const rotate = (shape) =>
  shape[0].map((_, i) => shape.map((row) => row[i]).reverse())

export function spawn(key) {
  const shape = PIECES[key].shape
  return { key, shape, x: Math.floor((COLS - shape[0].length) / 2), y: 0 }
}

export function collides(board, shape, x, y) {
  for (let r = 0; r < shape.length; r++) {
    for (let c = 0; c < shape[r].length; c++) {
      if (!shape[r][c]) continue
      const nx = x + c
      const ny = y + r
      if (nx < 0 || nx >= COLS || ny >= ROWS) return true
      if (ny >= 0 && board[ny][nx]) return true
    }
  }
  return false
}

export function merge(board, piece) {
  const copy = board.map((row) => [...row])
  piece.shape.forEach((row, r) =>
    row.forEach((v, c) => {
      const ny = piece.y + r
      if (v && ny >= 0) copy[ny][piece.x + c] = piece.key
    })
  )
  return copy
}

export function clearLines(board) {
  const kept = board.filter((row) => row.some((cell) => !cell))
  const cleared = ROWS - kept.length
  const fresh = Array.from({ length: cleared }, () => Array(COLS).fill(null))
  return { board: [...fresh, ...kept], cleared }
}

// Evalúa el tablero: menos huecos y menos altura = mejor
function evaluate(board) {
  let holes = 0
  let height = 0
  for (let c = 0; c < COLS; c++) {
    let seen = false
    for (let r = 0; r < ROWS; r++) {
      if (board[r][c]) {
        if (!seen) { height += ROWS - r; seen = true }
      } else if (seen) holes++
    }
  }
  return holes * 5 + height
}

// "La ficha correcta": la pieza que mejor encaja en el tablero actual
export function bestPieceKey(board) {
  let bestKey = randomKey()
  let bestScore = Infinity
  for (const key of KEYS) {
    let shape = PIECES[key].shape
    for (let rot = 0; rot < 4; rot++) {
      for (let x = 0; x <= COLS - shape[0].length; x++) {
        if (collides(board, shape, x, 0)) continue
        let y = 0
        while (!collides(board, shape, x, y + 1)) y++
        const merged = merge(board, { key, shape, x, y })
        const { board: after, cleared } = clearLines(merged)
        const score = evaluate(after) - cleared * 20
        if (score < bestScore) { bestScore = score; bestKey = key }
      }
      shape = rotate(shape)
    }
  }
  return bestKey
}
