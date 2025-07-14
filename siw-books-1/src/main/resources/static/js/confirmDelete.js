/**
 * Funzione helper per creare e inviare form con il token CSRF
 */
function postWithCsrf(action){
    const form = document.createElement('form');
    form.method = 'POST';
    form.action = action;

    //legge il token dal meta tag
    const csrfToken = document.querySelector('meta[name="_csrf"]').getAttribute('content');

    //crea un campo nascosto per il token
    const csrfInput = document.createElement('input');

    csrfInput.type = 'hidden';
    csrfInput.name = '_csrf';
    csrfInput.value = csrfToken;
    form.appendChild(csrfInput);

    document.body.appendChild(form);
    form.submit();
}





/**
 * Chiede conferma prima di eliminare un libro.
 * Invia una richiesta POST a un endpoint sicuro.
 */
function confirmDeleteBook(id, title) {
    if (confirm(`Sei sicuro di voler eliminare il libro "${title}"? Questa azione è irreversibile.`)) {
        postWithCsrf(`/admin/book/delete/${id}`);
    }
    return false; // Previene l'azione di default del link
}

/**
 * Chiede conferma prima di eliminare un autore.
 * Invia una richiesta POST a un endpoint sicuro.
 */
function confirmDeleteAuthor(id, name) {
    if (confirm(`Sei sicuro di voler eliminare l'autore "${name}"? Questa azione è irreversibile.`)) {
        postWithCsrf(`/admin/author/delete/${id}`);
    }
    return false;
}

/**
 * Chiede conferma prima di eliminare una recensione.
 * Invia una richiesta POST a un endpoint sicuro.
 */
function confirmDeleteReview(id, title) {
    if (confirm(`Sei sicuro di voler eliminare la recensione "${title}"?`)) {
       postWithCsrf(`/admin/reviews/delete/${id}`);
    }
    return false;
}