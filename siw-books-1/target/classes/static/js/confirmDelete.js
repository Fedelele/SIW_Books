/**
 * Helper function to create and send form with the CSRF token
 */
function postWithCsrf(action){
    const form = document.createElement('form');
    form.method = 'POST';
    form.action = action;

    //reads token from meta tag
    const csrfToken = document.querySelector('meta[name="_csrf"]').getAttribute('content');

    //creates a hidden field for the token
    const csrfInput = document.createElement('input');

    csrfInput.type = 'hidden';
    csrfInput.name = '_csrf';
    csrfInput.value = csrfToken;
    form.appendChild(csrfInput);

    document.body.appendChild(form);
    form.submit();
}





/**
 * Asks confirmation before deleting a book
 * Sends a POST request to a secure endpoint
 *
 */
function confirmDeleteBook(id, title) {
    if (confirm(`Are you sure you want to delete the book "${title}"? This action cannot be undone.`)) {
        postWithCsrf(`/admin/book/delete/${id}`);
    }
    return false;
}

/**
 * Asks confirmation before deleting an author
 * Sends a POST request to a secure endpoint
 */
function confirmDeleteAuthor(id, name) {
    if (confirm(`Are you sure you want to delete the author "${name}"? This action cannot be undone.`)) {
        postWithCsrf(`/admin/author/delete/${id}`);
    }
    return false;
}

/**
 * Asks confirmation before deleting a review
 * Sends a POST request to a secure endpoint
 */
function confirmDeleteReview(id, title) {
    if (confirm(`Are you sure you want to delete the review "${title}"?`)) {
       postWithCsrf(`/admin/reviews/delete/${id}`);
    }
    return false;
}