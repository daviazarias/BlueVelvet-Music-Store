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
    const ordValue = document.getElementById('ordSelect').value;
    const searchValue = document.querySelector('input[name="q"]')?.value || '';
    let url;

    if (searchValue) {
        url = '/dashboard/search?q=' + encodeURIComponent(searchValue) +
            '&sort=' + sortValue + '&asc=' + ordValue;
    } else {
        url = '/dashboard?sort=' + sortValue + '&asc=' + ordValue;
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

// ==========================================
// Produtos - Paginação e Controles
// ==========================================
function goToProductPage(page) {
    const urlParams = new URLSearchParams(window.location.search);
    const search = urlParams.get('search') || '';
    const sort = urlParams.get('sort') || 'name';

    let url = '/products?page=' + page;
    if (sort) {
        url += '&sort=' + sort;
    }
    if (search) {
        url += '&search=' + encodeURIComponent(search);
    }

    window.location.href = url;
}

function updateProductSort() {
    const urlParams = new URLSearchParams(window.location.search);
    const search = urlParams.get('search') || '';
    const sort = document.getElementById('sortSelect').value;
    const page = urlParams.get('page') || '0';

    let url = '/products?page=' + page + '&sort=' + sort;
    if (search) {
        url += '&search=' + encodeURIComponent(search);
    }

    window.location.href = url;
}

function clearProductSearch() {
    const urlParams = new URLSearchParams(window.location.search);
    const sort = urlParams.get('sort') || 'name';

    window.location.href = '/products?sort=' + sort;
}

