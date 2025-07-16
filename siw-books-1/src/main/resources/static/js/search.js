document.addEventListener('DOMContentLoaded', function() {
    const searchBar = document.querySelector('.search-bar');
    const contentList = document.querySelector('.content-list-container');

    if (!searchBar || !contentList) return;

    const items = contentList.querySelectorAll('.content-list-item-wrapper');

    searchBar.addEventListener('input', function(e) {
        const searchTerm = e.target.value.toLowerCase();

        items.forEach(item => {
            const title = item.querySelector('.content-title-text')?.textContent.toLowerCase() || '';

            if (title.includes(searchTerm)){
                item.style.display = 'flex';
            } else {
                item.style.display = 'none';
            }
        });
    });
});