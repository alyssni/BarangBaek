function updateCheckoutDelivery() {
    var selected = document.querySelector(
            'input[name="deliveryType"]:checked'
    );

    var pickupBox = document.getElementById(
            'pickupLocationBox'
    );

    var addressBox = document.getElementById(
            'deliveryAddressBox'
    );

    var pickupSelect = document.getElementById(
            'pickupLocation'
    );

    var campusPickup = selected
            && selected.value === 'Campus Pickup';

    if (pickupBox) {
        pickupBox.style.display = campusPickup
                ? 'block'
                : 'none';
    }

    if (addressBox) {
        addressBox.style.display = campusPickup
                ? 'none'
                : 'block';
    }

    if (pickupSelect) {
        pickupSelect.required = campusPickup;
    }

    updateCashPickupAvailability(campusPickup);
}

function updateCheckoutPayment() {
    var selected = document.querySelector(
            'input[name="paymentMethod"]:checked'
    );

    var providerBox = document.getElementById(
            'paymentProviderBox'
    );

    var providerSelect = document.getElementById(
            'paymentProvider'
    );

    var providerLabel = document.getElementById(
            'paymentProviderLabel'
    );

    var providerHelp = document.getElementById(
            'paymentProviderHelp'
    );

    if (!selected || !providerBox || !providerSelect) {
        return;
    }

    var selectedProvider =
            providerBox.getAttribute('data-selected-provider') || '';

    clearSelect(providerSelect);

    if (selected.value === 'Online Banking') {
        providerBox.style.display = 'block';
        providerSelect.disabled = false;
        providerSelect.required = true;

        if (providerLabel) {
            providerLabel.textContent = 'Select bank';
        }

        if (providerHelp) {
            providerHelp.textContent =
                    'Your selected bank name will appear on the receipt.';
        }

        addOptions(
                providerSelect,
                [
                    'Maybank',
                    'CIMB Bank',
                    'Bank Islam',
                    'Public Bank',
                    'RHB Bank',
                    'Hong Leong Bank'
                ],
                selectedProvider
        );

    } else if (selected.value === 'Debit/Credit Card') {
        providerBox.style.display = 'block';
        providerSelect.disabled = false;
        providerSelect.required = true;

        if (providerLabel) {
            providerLabel.textContent = 'Select card type';
        }

        if (providerHelp) {
            providerHelp.textContent =
                    'Only the card type is recorded. No card number is requested.';
        }

        addOptions(
                providerSelect,
                ['Visa', 'Mastercard'],
                selectedProvider
        );

    } else {
        providerBox.style.display = 'none';
        providerSelect.disabled = true;
        providerSelect.required = false;
        providerSelect.value = '';
    }

    providerBox.setAttribute('data-selected-provider', '');
}

function updateCashPickupAvailability(campusPickup) {
    var cashOption = document.querySelector(
            'input[name="paymentMethod"][value="Cash on Pickup"]'
    );

    if (!cashOption) {
        return;
    }

    cashOption.disabled = !campusPickup;

    if (!campusPickup && cashOption.checked) {
        var onlineBanking = document.querySelector(
                'input[name="paymentMethod"][value="Online Banking"]'
        );

        if (onlineBanking) {
            onlineBanking.checked = true;
        }

        updateCheckoutPayment();
    }
}

function clearSelect(selectElement) {
    while (selectElement.options.length > 0) {
        selectElement.remove(0);
    }
}

function addOptions(selectElement, values, selectedValue) {
    var index;

    for (index = 0; index < values.length; index++) {
        var option = document.createElement('option');

        option.value = values[index];
        option.textContent = values[index];

        if (values[index] === selectedValue) {
            option.selected = true;
        }

        selectElement.appendChild(option);
    }
}

document.addEventListener('DOMContentLoaded', function () {
    updateCheckoutDelivery();
    updateCheckoutPayment();
});