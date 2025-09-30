// Custom JavaScript for ByteX

document.addEventListener('DOMContentLoaded', function () {
    // Example: Add a class to the body after the page loads to trigger animations
    document.body.classList.add('page-loaded');

    // You can add more interactive features here in the future, for example:
    // - Client-side form validation enhancements
    // - Dynamic content loading with fetch for dashboards
    // - Interactive charts using a library like Chart.js
});

// Example function for a confirmation dialog before a destructive action
function confirmAction(message) {
    return confirm(message || 'Are you sure you want to perform this action?');
}