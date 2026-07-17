function previewItemPhoto(input) {
    var preview = document.getElementById('itemPhotoPreview');
    var file;
    var reader;

    if (!input || !preview || !input.files || !input.files[0]) {
        return;
    }

    file = input.files[0];

    if (file.size > 5 * 1024 * 1024) {
        window.alert('The selected image must not exceed 5 MB.');
        input.value = '';
        return;
    }

    if (file.type !== 'image/jpeg'
            && file.type !== 'image/png'
            && file.type !== 'image/webp') {
        window.alert('Please choose a JPG, PNG or WEBP image.');
        input.value = '';
        return;
    }

    reader = new FileReader();

    reader.onload = function (event) {
        preview.src = event.target.result;
    };

    reader.onerror = function () {
        window.alert('The selected image could not be previewed.');
        input.value = '';
    };

    reader.readAsDataURL(file);
}

function updateDescriptionCount() {
    var description = document.getElementById('itemDesc');
    var counter = document.getElementById('descriptionCount');

    if (!description || !counter) {
        return;
    }

    counter.textContent = description.value.length + ' / 1000';
}

function updateCategoryDescription() {
    var category = document.getElementById('categoryID');
    var output = document.getElementById('categoryDescription');
    var selected;
    var description;

    if (!category || !output) {
        return;
    }

    selected = category.options[category.selectedIndex];

    description = selected
            ? selected.getAttribute('data-description')
            : '';

    output.textContent = description
            ? description
            : 'Choose a category to see which items belong in it.';
}

function updateItemStatusFromStock() {
    var stock = document.getElementById('stock');
    var status = document.getElementById('itemStatus');
    var value;

    if (!stock || !status) {
        return;
    }

    value = parseInt(stock.value, 10);

    if (!isNaN(value) && value === 0) {
        status.value = 'Sold';
    }
}

document.addEventListener('DOMContentLoaded', function () {
    var photoInput = document.getElementById('itemPhoto');
    var description = document.getElementById('itemDesc');
    var category = document.getElementById('categoryID');
    var stock = document.getElementById('stock');
    var form = document.getElementById('itemForm');
    var submitButton = document.getElementById('itemSubmitButton');

    updateDescriptionCount();
    updateCategoryDescription();
    updateItemStatusFromStock();

    if (photoInput) {
        photoInput.addEventListener('change', function () {
            previewItemPhoto(photoInput);
        });
    }

    if (description) {
        description.addEventListener(
                'input',
                updateDescriptionCount
        );
    }

    if (category) {
        category.addEventListener(
                'change',
                updateCategoryDescription
        );
    }

    if (stock) {
        stock.addEventListener(
                'input',
                updateItemStatusFromStock
        );
    }

    if (form && submitButton) {
        form.addEventListener('submit', function () {
            submitButton.disabled = true;

            submitButton.innerHTML =
                    '<i class="fa-solid fa-spinner fa-spin"></i> Saving...';
        });
    }
});