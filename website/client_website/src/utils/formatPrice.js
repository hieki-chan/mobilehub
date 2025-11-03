 
export function formatPrice(v) {
  if (v == null || v === '') return ''
  try { return new Intl.NumberFormat('vi-VN').format(Number(v)) + '₫' }
  catch (e) { return String(v) + '₫' }
}
