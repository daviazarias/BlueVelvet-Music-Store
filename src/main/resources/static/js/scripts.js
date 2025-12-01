// ============================================
// BlueVelvet Music Store - JavaScript Global
// ============================================

// ==========================================
// Preview de Imagem
// ==========================================
function previewImage(event) {
    const file = event.target.files[0];
    if (file) {
        const reader = new FileReader();
        reader.onload = function (e) {
            document.getElementById('preview').src = e.target.result;
            document.getElementById('imagePreview').style.display = 'block';
        };
        reader.readAsDataURL(file);
    } else {
        document.getElementById('imagePreview').style.display = 'none';
    }
}

// ==========================================
// Dashboard - Funções de Controle
// ==========================================
function resetCategories() {
    if (confirm('Tem certeza que deseja resetar as categorias para o estado inicial?')) {
        window.location.href = '/category/reset';
    }
}

function clearSearch() {
    document.querySelector('input[name="q"]').value = '';
    window.location.href = '/dashboard';
}

function updateSort() {
    const sortValue = document.getElementById('sortSelect').value;
    const searchValue = document.querySelector('input[name="q"]')?.value || '';
    let url;

    if (searchValue) {
        url = '/dashboard/search?q=' + encodeURIComponent(searchValue) + '&sort=' + sortValue;
    } else {
        url = '/dashboard?sort=' + sortValue;
    }
    window.location.href = url;
}

function goToPage(page) {
    const sortValue = document.getElementById('sortSelect').value;
    const searchValue = document.querySelector('input[name="q"]')?.value || '';
    let url;

    if (searchValue) {
        url = '/dashboard/search?q=' + encodeURIComponent(searchValue) + '&page=' + page + '&sort=' + sortValue;
    } else {
        url = '/dashboard?page=' + page + '&sort=' + sortValue;
    }
    window.location.href = url;
}

// ==========================================
// Confirmação de Deleção
// ==========================================
document.addEventListener('DOMContentLoaded', () => {
    const deleteButtons = document.querySelectorAll('.delete-btn');
    deleteButtons.forEach(button => {
        button.addEventListener('click', (event) => {
            const confirmation = confirm("Tem certeza que deseja deletar esta categoria?");
            if (!confirmation) {
                event.preventDefault();
            }
        });
    });
});

// ==========================================
// List - Trocar ordenação
// ==========================================
function switchSort() {
    const sortValue = document.getElementById('sortSelect').value;
    window.location.href = '/list?sort=' + sortValue;
}

