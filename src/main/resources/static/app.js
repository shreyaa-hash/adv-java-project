const colors = ['#b26a52', '#647c68', '#607b92', '#a27c4f', '#755f82', '#527f7d'];
let mode = 'crud';
let books = [];
const rows = document.querySelector('#book-rows');
const dialog = document.querySelector('#book-dialog');
const form = document.querySelector('#book-form');

function endpoint() { return `/api/books/${mode}`; }
function money(value) { return new Intl.NumberFormat('en-IN', { style: 'currency', currency: 'INR' }).format(value); }
function escapeHtml(value = '') { return String(value).replace(/[&<>"']/g, c => ({'&':'&amp;','<':'&lt;','>':'&gt;','"':'&quot;',"'":'&#39;'}[c])); }
function notify(message) {
  const toast = document.querySelector('#toast'); toast.textContent = message; toast.classList.add('show');
  window.setTimeout(() => toast.classList.remove('show'), 2500);
}
async function request(url, options = {}) {
  const response = await fetch(url, { ...options, headers: { 'Content-Type': 'application/json', ...(options.headers || {}) } });
  if (!response.ok) {
    let message = `Request failed (${response.status})`;
    try { const data = await response.json(); message = data.detail || data.message || message; } catch { /* use default */ }
    throw new Error(message);
  }
  return response.status === 204 ? null : response.json();
}
async function loadBooks() {
  rows.innerHTML = '<tr><td colspan="6" class="loading">Loading books…</td></tr>';
  try {
    const sort = document.querySelector('#sort').value;
    books = await request(`${endpoint()}${mode === 'jpa' ? `?sort=${sort}` : ''}`);
    renderBooks();
  } catch (error) {
    rows.innerHTML = `<tr><td colspan="6" class="empty">${escapeHtml(error.message)}. Check that the Spring Boot server is running.</td></tr>`;
  }
}
function renderBooks() {
  document.querySelector('#book-count').textContent = `${books.length} titles`;
  if (!books.length) { rows.innerHTML = '<tr><td colspan="6" class="empty">No books yet. Add the first one.</td></tr>'; return; }
  rows.innerHTML = books.map((book, index) => `<tr>
    <td><div class="book-title"><span class="cover" style="background:${colors[index % colors.length]}">P&amp;P</span>${escapeHtml(book.title)}</div></td>
    <td>${escapeHtml(book.author)}</td><td><span class="category-pill">${escapeHtml(book.category || 'Uncategorised')}</span></td>
    <td class="price">${money(book.price)}</td><td class="stock ${book.quantity < 5 ? 'low-stock' : ''}">${book.quantity} copies</td>
    <td><div class="row-actions"><button class="icon-button" data-action="edit" data-id="${book.id}">Edit</button><button class="icon-button" data-action="delete" data-id="${book.id}">Delete</button></div></td>
  </tr>`).join('');
}
function openFor(book) {
  form.reset(); document.querySelector('#form-error').textContent = '';
  document.querySelector('#book-id').value = book?.id || '';
  document.querySelector('#dialog-title').textContent = book ? 'Edit book' : 'Add a book';
  for (const field of ['title', 'author', 'price', 'quantity', 'category']) document.querySelector(`#${field}`).value = book?.[field] ?? '';
  dialog.showModal();
}
document.querySelector('#add-book').addEventListener('click', () => openFor(null));
document.querySelector('#close-dialog').addEventListener('click', () => dialog.close());
document.querySelector('#sort').addEventListener('change', loadBooks);
document.querySelectorAll('.tab').forEach(tab => tab.addEventListener('click', () => {
  mode = tab.dataset.mode;
  document.querySelectorAll('.tab').forEach(item => item.classList.toggle('active', item === tab));
  document.querySelector('#sort-wrap').hidden = mode !== 'jpa';
  document.querySelector('#mode-note').textContent = mode === 'crud' ? 'Basic CRUD interface' : 'CRUD + sort, paging and flush features';
  loadBooks();
}));
rows.addEventListener('click', async event => {
  const button = event.target.closest('button[data-action]'); if (!button) return;
  const book = books.find(item => item.id === Number(button.dataset.id)); if (!book) return;
  if (button.dataset.action === 'edit') { openFor(book); return; }
  if (!window.confirm(`Delete “${book.title}” from the inventory?`)) return;
  try { await request(`${endpoint()}/${book.id}`, { method: 'DELETE' }); notify('Book removed from inventory'); await loadBooks(); }
  catch (error) { notify(error.message); }
});
form.addEventListener('submit', async event => {
  event.preventDefault();
  const id = document.querySelector('#book-id').value;
  const book = { title: document.querySelector('#title').value.trim(), author: document.querySelector('#author').value.trim(), price: Number(document.querySelector('#price').value), quantity: Number(document.querySelector('#quantity').value), category: document.querySelector('#category').value.trim() };
  try {
    await request(id ? `${endpoint()}/${id}` : endpoint(), { method: id ? 'PUT' : 'POST', body: JSON.stringify(book) });
    dialog.close(); notify(id ? 'Book updated' : 'Book added'); await loadBooks();
  } catch (error) { document.querySelector('#form-error').textContent = error.message; }
});
document.querySelector('#sort-wrap').hidden = true;
loadBooks();
