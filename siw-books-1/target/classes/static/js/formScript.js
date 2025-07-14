document.addEventListener('DOMContentLoaded', function() {
    const starsContainer = document.getElementById('stars');
    if (!starsContainer) return;

    const ratingInput = document.getElementById('rating');
    const stars = starsContainer.querySelectorAll('.star');
    let currentRating = parseInt(ratingInput.value) || 0;

    function updateStars(rating) {
        stars.forEach(star => {
            const starValue = parseInt(star.dataset.value);
            if (starValue <= rating) {
                star.classList.remove('far');
                star.classList.add('fa', 'selected');
            } else {
                star.classList.remove('fa', 'selected');
                star.classList.add('far');
            }
        });
    }

    stars.forEach(star => {
        star.addEventListener('mouseover', () => {
            const hoverValue = parseInt(star.dataset.value);
            stars.forEach(s => {
                s.classList.remove('hover');
                if (parseInt(s.dataset.value) <= hoverValue) {
                    s.classList.add('hover');
                }
            });
        });

        star.addEventListener('mouseout', () => {
            stars.forEach(s => s.classList.remove('hover'));
        });

        star.addEventListener('click', () => {
            currentRating = parseInt(star.dataset.value);
            ratingInput.value = currentRating;
            updateStars(currentRating);
        });
    });

    // Initialize stars based on existing value (for edit form)
    if (currentRating > 0) {
        updateStars(currentRating);
    }
});