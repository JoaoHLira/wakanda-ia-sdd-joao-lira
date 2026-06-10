// Override the toggleKebabMenu function to stop event propagation
window.toggleKebabMenu = function(button, event) {
    // Stop event propagation to prevent it from reaching parent elements
    event.stopPropagation();
    
    const menu = button.nextElementSibling;
    const isOpen = menu.style.display === 'block';
    
    // Close all other open menus
    document.querySelectorAll('.kebab-context-menu').forEach(m => {
        m.style.display = 'none';
    });
    
    // Toggle the current menu
    menu.style.display = isOpen ? 'none' : 'block';
};
