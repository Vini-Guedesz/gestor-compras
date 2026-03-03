export function useClipboard() {
  const copyWithExecCommand = (text) => {
    if (typeof document === 'undefined') return false

    const textarea = document.createElement('textarea')
    textarea.value = text
    textarea.setAttribute('readonly', '')
    textarea.style.position = 'fixed'
    textarea.style.top = '0'
    textarea.style.left = '0'
    textarea.style.opacity = '0'
    textarea.style.pointerEvents = 'none'

    document.body.appendChild(textarea)
    textarea.focus()
    textarea.select()
    textarea.setSelectionRange(0, textarea.value.length)

    const copied = document.execCommand('copy')
    document.body.removeChild(textarea)

    return copied
  }

  const copyText = async (text) => {
    const normalizedText = String(text || '').trim()
    if (!normalizedText) return false

    const canUseClipboardApi =
      typeof window !== 'undefined' &&
      typeof navigator !== 'undefined' &&
      window.isSecureContext &&
      typeof navigator.clipboard?.writeText === 'function'

    if (canUseClipboardApi) {
      try {
        await navigator.clipboard.writeText(normalizedText)
        return true
      } catch {
        // Fallback below.
      }
    }

    try {
      return copyWithExecCommand(normalizedText)
    } catch {
      return false
    }
  }

  return {
    copyText
  }
}
