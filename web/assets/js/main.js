function showSidebar() {
    var drawer = document.getElementById('userDrawer');
    var overlay = document.getElementById('drawerOverlay');
    var menuButton = document.querySelector('[aria-controls="userDrawer"]');

    if (!drawer || !overlay) {
        return;
    }

    drawer.classList.add('is-open');
    drawer.setAttribute('aria-hidden', 'false');
    overlay.classList.add('is-visible');
    document.body.classList.add('drawer-open');

    if (menuButton) {
        menuButton.setAttribute('aria-expanded', 'true');
    }
}

function hideSidebar() {
    var drawer = document.getElementById('userDrawer');
    var overlay = document.getElementById('drawerOverlay');
    var menuButton = document.querySelector('[aria-controls="userDrawer"]');

    if (!drawer || !overlay) {
        return;
    }

    drawer.classList.remove('is-open');
    drawer.setAttribute('aria-hidden', 'true');
    overlay.classList.remove('is-visible');
    document.body.classList.remove('drawer-open');

    if (menuButton) {
        menuButton.setAttribute('aria-expanded', 'false');
    }
}

function changeQuantity(button, change, maximum) {
    var control = button.closest('.quantity-control');
    var input = control ? control.querySelector('input') : null;

    if (!input) {
        return;
    }

    var quantity = parseInt(input.value, 10);
    var minimum = parseInt(input.min, 10);

    if (isNaN(quantity)) {
        quantity = 0;
    }

    if (isNaN(minimum)) {
        minimum = 0;
    }

    quantity = Math.max(
        minimum,
        Math.min(maximum, quantity + change)
    );

    input.value = quantity;

    updateAddCartButton(input);
}

function updateAddCartButton(quantityInput) {
    var form = quantityInput.closest('form');

    if (!form) {
        return;
    }

    var addButton = form.querySelector(
        '.add-cart-button, .details-add-cart'
    );

    if (!addButton) {
        return;
    }

    var quantity = parseInt(quantityInput.value, 10);

    addButton.disabled = isNaN(quantity) || quantity < 1;
}

function initialiseAddCartButtons() {
    var quantityInputs = document.querySelectorAll(
        '.quantity-control input[name="quantity"]'
    );

    var index;

    for (index = 0; index < quantityInputs.length; index++) {
        updateAddCartButton(quantityInputs[index]);
    }
}

function toggleAllCartItems(selectAllBox) {
    var checkboxes = document.querySelectorAll(
        '.cart-select-box:not(:disabled)'
    );

    var index;

    for (index = 0; index < checkboxes.length; index++) {
        checkboxes[index].checked = selectAllBox.checked;
    }

    updateCartSelectionSummary();
}

function updateCartSelectionSummary() {
    var checkboxes = document.querySelectorAll(
        '.cart-select-box:checked:not(:disabled)'
    );

    var totalQuantity = 0;
    var totalAmount = 0;
    var index;

    for (index = 0; index < checkboxes.length; index++) {
        var price = parseFloat(
            checkboxes[index].getAttribute('data-price')
        );

        var quantity = parseInt(
            checkboxes[index].getAttribute('data-quantity'),
            10
        );

        if (isNaN(price)) {
            price = 0;
        }

        if (isNaN(quantity)) {
            quantity = 0;
        }

        totalQuantity += quantity;
        totalAmount += price * quantity;
    }

    var countElement = document.getElementById(
        'selectedItemCount'
    );

    var totalElement = document.getElementById(
        'selectedCartTotal'
    );

    if (countElement) {
        countElement.textContent = totalQuantity;
    }

    if (totalElement) {
        totalElement.textContent =
            'RM ' + totalAmount.toFixed(2);
    }

    var selectAllBox = document.getElementById(
        'selectAllCartItems'
    );

    var availableBoxes = document.querySelectorAll(
        '.cart-select-box:not(:disabled)'
    );

    if (selectAllBox) {
        selectAllBox.checked =
            availableBoxes.length > 0
            && checkboxes.length === availableBoxes.length;
    }
}

function showFeatureNotice(featureName) {
    window.alert(
        featureName
        + ' functionality will be connected '
        + 'in the next development step.'
    );
}

document.addEventListener('DOMContentLoaded', function () {
    initialiseAddCartButtons();
    updateCartSelectionSummary();
});

document.addEventListener('keydown', function (event) {
    if (event.key === 'Escape') {
        hideSidebar();
    }
});