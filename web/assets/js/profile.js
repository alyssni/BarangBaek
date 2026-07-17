document.addEventListener('DOMContentLoaded', function () {
    var photoInput = document.getElementById('userPhoto');
    var photoPreview = document.getElementById('profilePhotoPreview');
    var profileForm = document.getElementById('profileForm');
    var saveButton = document.getElementById('profileSaveButton');
    var cityInput = document.getElementById('city');
    var stateInput = document.getElementById('state');

    var cityStateMap = {
        'Johor Bahru': 'Johor',
        'Batu Pahat': 'Johor',
        'Muar': 'Johor',
        'Kluang': 'Johor',
        'Kulai': 'Johor',
        'Segamat': 'Johor',
        'Pontian': 'Johor',
        'Kota Tinggi': 'Johor',
        'Mersing': 'Johor',

        'Alor Setar': 'Kedah',
        'Sungai Petani': 'Kedah',
        'Kulim': 'Kedah',
        'Langkawi': 'Kedah',
        'Jitra': 'Kedah',
        'Baling': 'Kedah',

        'Kota Bharu': 'Kelantan',
        'Pasir Mas': 'Kelantan',
        'Tanah Merah': 'Kelantan',
        'Machang': 'Kelantan',
        'Kuala Krai': 'Kelantan',
        'Gua Musang': 'Kelantan',

        'Melaka City': 'Melaka',
        'Alor Gajah': 'Melaka',
        'Jasin': 'Melaka',

        'Seremban': 'Negeri Sembilan',
        'Port Dickson': 'Negeri Sembilan',
        'Nilai': 'Negeri Sembilan',
        'Kuala Pilah': 'Negeri Sembilan',
        'Tampin': 'Negeri Sembilan',

        'Kuantan': 'Pahang',
        'Temerloh': 'Pahang',
        'Bentong': 'Pahang',
        'Raub': 'Pahang',
        'Kuala Lipis': 'Pahang',
        'Jerantut': 'Pahang',
        'Pekan': 'Pahang',
        'Cameron Highlands': 'Pahang',

        'George Town': 'Pulau Pinang',
        'Butterworth': 'Pulau Pinang',
        'Bukit Mertajam': 'Pulau Pinang',
        'Bayan Lepas': 'Pulau Pinang',
        'Nibong Tebal': 'Pulau Pinang',

        'Ipoh': 'Perak',
        'Taiping': 'Perak',
        'Teluk Intan': 'Perak',
        'Manjung': 'Perak',
        'Kuala Kangsar': 'Perak',
        'Kampar': 'Perak',
        'Batu Gajah': 'Perak',

        'Kangar': 'Perlis',
        'Arau': 'Perlis',
        'Padang Besar': 'Perlis',

        'Kota Kinabalu': 'Sabah',
        'Sandakan': 'Sabah',
        'Tawau': 'Sabah',
        'Lahad Datu': 'Sabah',
        'Keningau': 'Sabah',
        'Semporna': 'Sabah',
        'Beaufort': 'Sabah',

        'Kuching': 'Sarawak',
        'Miri': 'Sarawak',
        'Sibu': 'Sarawak',
        'Bintulu': 'Sarawak',
        'Sri Aman': 'Sarawak',
        'Limbang': 'Sarawak',
        'Mukah': 'Sarawak',

        'Shah Alam': 'Selangor',
        'Petaling Jaya': 'Selangor',
        'Subang Jaya': 'Selangor',
        'Klang': 'Selangor',
        'Kajang': 'Selangor',
        'Bangi': 'Selangor',
        'Sepang': 'Selangor',
        'Cyberjaya': 'Selangor',
        'Rawang': 'Selangor',
        'Selayang': 'Selangor',
        'Ampang': 'Selangor',

        'Kuala Terengganu': 'Terengganu',
        'Kemaman': 'Terengganu',
        'Dungun': 'Terengganu',
        'Marang': 'Terengganu',
        'Besut': 'Terengganu',

        'Kuala Lumpur': 'Wilayah Persekutuan Kuala Lumpur',
        'Labuan': 'Wilayah Persekutuan Labuan',
        'Putrajaya': 'Wilayah Persekutuan Putrajaya'
    };

    if (cityInput && stateInput) {
        var currentCity =
                cityInput.getAttribute('data-current-city') || '';

        var cities = Object.keys(cityStateMap).sort();
        var index;
        var currentFound = false;

        for (index = 0; index < cities.length; index++) {
            var option = document.createElement('option');

            option.value = cities[index];
            option.textContent = cities[index];

            if (cities[index] === currentCity) {
                option.selected = true;
                currentFound = true;
            }

            cityInput.appendChild(option);
        }

        if (currentCity && !currentFound) {
            var currentOption =
                    document.createElement('option');

            currentOption.value = currentCity;
            currentOption.textContent = currentCity;
            currentOption.selected = true;

            cityInput.appendChild(currentOption);
        }

        cityInput.addEventListener('change', function () {
            stateInput.value =
                    cityStateMap[cityInput.value] || '';
        });
    }

    if (photoInput && photoPreview) {
        photoInput.addEventListener('change', function () {
            var file =
                    photoInput.files && photoInput.files[0]
                    ? photoInput.files[0]
                    : null;

            if (!file) {
                return;
            }

            if (file.size > 5 * 1024 * 1024) {
                window.alert(
                        'The selected image must not exceed 5 MB.'
                );

                photoInput.value = '';
                return;
            }

            if (file.type.indexOf('image/') !== 0) {
                window.alert(
                        'Please choose a valid image file.'
                );

                photoInput.value = '';
                return;
            }

            var reader = new FileReader();

            reader.onload = function (event) {
                photoPreview.src = event.target.result;
            };

            reader.readAsDataURL(file);
        });
    }

    if (profileForm && saveButton) {
        profileForm.addEventListener('submit', function () {
            saveButton.disabled = true;
            saveButton.innerHTML = '<i class="fa-solid fa-spinner fa-spin"></i> Saving...';
        });
    }
});